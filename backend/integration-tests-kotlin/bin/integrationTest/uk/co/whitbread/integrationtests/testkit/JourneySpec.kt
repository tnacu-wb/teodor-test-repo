package uk.co.whitbread.integrationtests.testkit

import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.core.spec.style.scopes.FeatureSpecContainerScope
import io.kotest.core.test.TestScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withContext
import uk.co.whitbread.integrationtests.framework.config.IntegrationTestConfig
import uk.co.whitbread.integrationtests.framework.http.ApiResult
import uk.co.whitbread.integrationtests.framework.http.HttpEvidenceContext
import uk.co.whitbread.integrationtests.framework.http.httpEvidence
import uk.co.whitbread.integrationtests.framework.reporting.ScenarioEvidenceOutcome
import uk.co.whitbread.integrationtests.framework.reporting.ScenarioEvidenceSink
import uk.co.whitbread.integrationtests.framework.wiremock.CleanupException
import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.testkit.mocks.MockInstaller
import uk.co.whitbread.integrationtests.testkit.mocks.OperaEndpoint
import uk.co.whitbread.integrationtests.testkit.mocks.Upstream
import uk.co.whitbread.integrationtests.testkit.model.Booking

/**
 * Base Kotest feature specification that owns scenario identity, evidence, mocks, and cleanup.
 *
 * Each declared scenario receives a unique test ID, one isolated evidence artifact, and one strict
 * cleanup attempt. Journey authors use [JourneySpecScope.scenario] and [ScenarioScope] without
 * managing those lifecycle details directly.
 *
 * @param description feature name used when generating scenario test IDs.
 * @param body feature declaration containing one or more journey scenarios.
 */
abstract class JourneySpec(
    private val description: String,
    body: suspend JourneySpecScope.() -> Unit,
) : FeatureSpec() {
    init {
        feature(description) {
            JourneySpecScope(description, this).body()
        }
    }

    /**
     * Receiver used to declare journey scenarios under one feature.
     *
     * @param featureName feature description used for test-ID generation.
     * @param featureScope Kotest container receiving declared scenario leaves.
     */
    class JourneySpecScope(
        private val featureName: String,
        private val featureScope: FeatureSpecContainerScope,
    ) {
        /**
         * Declares one isolated journey scenario with strict cleanup and an evidence artifact.
         *
         * @param name scenario name used for reporting and test-ID generation.
         * @param test journey body executed with its scenario-scoped helpers.
         */
        suspend fun scenario(
            name: String,
            test: suspend ScenarioScope.() -> Unit,
        ) = featureScope.scenario(name) {
            val testId = testIdFor(featureName, name)
            val evidenceSink = ScenarioEvidenceSink(testId)
            val mockInstaller = MockInstaller(IntegrationTestConfig.wiremock, evidenceSink)
            val scenarioScope =
                ScenarioScope(
                    testId = testId,
                    mockInstaller = mockInstaller,
                    kotestScope = this,
                    evidenceSink = evidenceSink,
                )
            var primaryFailure: Throwable? = null
            var outcome = ScenarioEvidenceOutcome.PASSED

            try {
                withContext(HttpEvidenceContext(evidenceSink)) {
                    runWithCleanup(
                        test = { scenarioScope.test() },
                        cleanup = {
                            try {
                                evidenceSink.recordCleanup(mockInstaller.removeFor(testId))
                            } catch (cleanupFailure: CleanupException) {
                                evidenceSink.recordCleanup(cleanupFailure.result)
                                throw cleanupFailure
                            }
                        },
                    )
                }
            } catch (failure: Throwable) {
                primaryFailure = failure
                outcome = ScenarioEvidenceOutcome.FAILED
                evidenceSink.recordScenarioFailure(failure)
                throw failure
            } finally {
                finalizeScenarioEvidence(evidenceSink, outcome, primaryFailure)
            }
        }
    }

    /**
     * Test-author receiver exposing the scenario ID, mock operations, assertions, and evidence.
     *
     * @property testId unique ID propagated through baggage and WireMock matchers.
     * @param mockInstaller scenario-owned mock implementation.
     * @param kotestScope underlying Kotest leaf scope delegated by this receiver.
     * @param evidenceSink scenario-owned destination for attached HTTP evidence.
     */
    class ScenarioScope internal constructor(
        val testId: String,
        private val mockInstaller: MockInstaller,
        private val kotestScope: TestScope,
        private val evidenceSink: ScenarioEvidenceSink,
    ) : TestScope by kotestScope {
        /** Installs every default selected by [booking]. */
        suspend fun installFor(
            booking: Booking,
            excluded: Set<String> = emptySet(),
        ) {
            mockInstaller.installFor(
                booking = booking,
                testId = testId,
                excluded = excluded,
            )
        }

        /** Installs one self-contained stub for this scenario. */
        suspend fun installStub(stub: PlannedStub) {
            mockInstaller.installStub(stub, testId)
        }

        /** Counts requests that this scenario sent to [upstream]. */
        suspend fun callCount(upstream: Upstream): Int = mockInstaller.countCallsFor(upstream.target, testId)

        /** Counts requests that this scenario sent to one Opera [endpoint]. */
        suspend fun callCount(endpoint: OperaEndpoint): Int = mockInstaller.countCallsFor(endpoint, testId)

        /**
         * Executes one named assertion block.
         *
         * The block is suspending so that assertions which query WireMock, such as
         * [callCount], can sit with the assertions they belong to rather than
         * being hoisted above the block.
         *
         * @param name human-readable expectation label prepended to any failure in the block,
         * including non-assertion exceptions such as a failed [ApiResult.body] read. A
         * cancellation of the scenario coroutine is not a failure and propagates unlabelled.
         * @param block assertions to execute.
         */
        suspend fun expect(
            name: String,
            block: suspend () -> Unit,
        ) {
            try {
                withClue(name) { block() }
            } catch (labelled: AssertionError) {
                // withClue wraps every non-assertion throwable, which is the labelling this
                // method exists for — except a cancellation of this scenario's coroutine,
                // which is control flow and must keep propagating as itself. Kotest converts
                // a TimeoutCancellationException on purpose (a timed-out assertion block is a
                // finding), so only genuine cancellation is unwrapped.
                val cause = labelled.cause
                if (cause is CancellationException && cause !is TimeoutCancellationException) {
                    throw cause
                }
                throw labelled
            }
        }

        /**
         * Adds this result's bounded raw request and response to the scenario evidence artifact.
         *
         * @param prefix optional label distinguishing multiple calls within the same scenario.
         */
        fun <T> ApiResult<T>.attachEvidence(prefix: String? = null) {
            evidenceSink.recordHttp(prefix, response.httpEvidence())
        }
    }
}

/**
 * Creates a WireMock-safe scenario ID from feature and scenario names plus a random suffix.
 *
 * @param featureName containing feature description.
 * @param scenarioName leaf scenario description.
 * @return lowercase identifier bounded to the framework's matcher-friendly length.
 */
private fun testIdFor(
    featureName: String,
    scenarioName: String,
): String {
    val base =
        "$featureName $scenarioName"
            .lowercase()
            .replace(Regex("[^a-z0-9]+"), "-")
            .trim('-')
            .take(90)
            .trim('-')
    val suffix = (1..6).map { (('a'..'z') + ('0'..'9')).random() }.joinToString("")

    return "$base-$suffix"
}
