package uk.co.whitbread.integrationtests.testkit

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.types.shouldBeSameInstanceAs
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.withTimeout
import uk.co.whitbread.integrationtests.framework.reporting.ScenarioEvidenceOutcome
import uk.co.whitbread.integrationtests.framework.reporting.ScenarioEvidenceSink
import uk.co.whitbread.integrationtests.framework.wiremock.CleanupException
import uk.co.whitbread.integrationtests.framework.wiremock.CleanupFailure
import uk.co.whitbread.integrationtests.framework.wiremock.CleanupOperation
import uk.co.whitbread.integrationtests.framework.wiremock.CleanupResult
import java.nio.file.Files
import kotlin.time.Duration.Companion.milliseconds

/** Tests scenario cleanup and evidence finalization exception precedence. */
class JourneyCleanupTest :
    FunSpec({

        test("cleanup failure fails an otherwise successful scenario") {
            val cleanupFailure = cleanupException()

            val thrown =
                captureFailure {
                    runWithCleanup(
                        test = {},
                        cleanup = { throw cleanupFailure },
                    )
                }

            thrown shouldBeSameInstanceAs cleanupFailure
        }

        test("scenario failure stays primary when cleanup also fails") {
            val scenarioFailure = IllegalStateException("scenario failed")
            val cleanupFailure = cleanupException()

            val thrown =
                captureFailure {
                    runWithCleanup(
                        test = { throw scenarioFailure },
                        cleanup = { throw cleanupFailure },
                    )
                }

            thrown shouldBeSameInstanceAs scenarioFailure
            thrown.suppressed.toList().shouldContainExactly(cleanupFailure)
        }

        test("timeout stays primary when cleanup also fails") {
            val cleanupFailure = cleanupException()
            var lifecycleFailure: Throwable? = null

            val thrown =
                captureFailure {
                    withTimeout(50.milliseconds) {
                        try {
                            runWithCleanup(
                                test = { awaitCancellation() },
                                cleanup = {
                                    throw cleanupFailure
                                },
                            )
                        } catch (failure: Throwable) {
                            lifecycleFailure = failure
                            throw failure
                        }
                    }
                }

            thrown.shouldBeInstanceOf<TimeoutCancellationException>()
            val primaryFailure = lifecycleFailure ?: error("Expected lifecycle failure")
            primaryFailure.shouldBeInstanceOf<TimeoutCancellationException>()
            primaryFailure.suppressed.toList().shouldContainExactly(cleanupFailure)
        }

        test("successful evidence finalization writes one artifact and reports its path once") {
            val root = Files.createTempDirectory("journey-evidence-success")
            try {
                val sink = ScenarioEvidenceSink("successful-scenario", root)
                val reported = mutableListOf<String>()

                finalizeScenarioEvidence(
                    sink = sink,
                    outcome = ScenarioEvidenceOutcome.PASSED,
                    primaryFailure = null,
                    reportPath = { path -> reported += path.toString() },
                )

                reported shouldBe listOf(sink.artifactPath.toString())
                Files.readString(sink.artifactPath) shouldContain "Outcome: PASSED"
            } finally {
                root.toFile().deleteRecursively()
            }
        }

        test("evidence write failure fails an otherwise successful scenario") {
            val invalidRoot = Files.createTempFile("journey-evidence-invalid", ".tmp")
            try {
                val failure =
                    captureFailure {
                        finalizeScenarioEvidence(
                            sink = ScenarioEvidenceSink("write-failure", invalidRoot),
                            outcome = ScenarioEvidenceOutcome.PASSED,
                            primaryFailure = null,
                            reportPath = {},
                        )
                    }

                failure::class.simpleName shouldContain "File"
            } finally {
                Files.deleteIfExists(invalidRoot)
            }
        }

        test("evidence write failure is suppressed when a scenario failure is already primary") {
            val invalidRoot = Files.createTempFile("journey-evidence-suppressed", ".tmp")
            val primary = IllegalStateException("journey failed")
            try {
                finalizeScenarioEvidence(
                    sink = ScenarioEvidenceSink("suppressed-write-failure", invalidRoot),
                    outcome = ScenarioEvidenceOutcome.FAILED,
                    primaryFailure = primary,
                    reportPath = {},
                )

                primary.suppressed.size shouldBe 1
                primary.suppressed.single()::class.simpleName shouldContain "File"
            } finally {
                Files.deleteIfExists(invalidRoot)
            }
        }
    })

private suspend fun captureFailure(block: suspend () -> Unit): Throwable =
    try {
        block()
        error("Expected failure")
    } catch (failure: Throwable) {
        failure
    }

private fun cleanupException(): CleanupException {
    val cause = IllegalStateException("cleanup failed")
    return CleanupException(
        CleanupResult(
            testId = "test-id",
            removedMappings = 0,
            removedRequestEvents = 0,
            failures =
                listOf(
                    CleanupFailure(
                        wireMockName = "Opera",
                        wireMockUrl = "http://opera.test",
                        operation = CleanupOperation.LIST_MAPPINGS,
                        cause = cause,
                    ),
                ),
        ),
    )
}
