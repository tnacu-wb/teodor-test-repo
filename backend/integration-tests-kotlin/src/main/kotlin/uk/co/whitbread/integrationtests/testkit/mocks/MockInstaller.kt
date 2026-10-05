package uk.co.whitbread.integrationtests.testkit.mocks

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import uk.co.whitbread.integrationtests.framework.http.BAGGAGE_HEADER
import uk.co.whitbread.integrationtests.framework.http.matchesTestId
import uk.co.whitbread.integrationtests.framework.http.testIdHeaderMatcher
import uk.co.whitbread.integrationtests.framework.reporting.StubInstallEvidenceSink
import uk.co.whitbread.integrationtests.framework.wiremock.CleanupFailure
import uk.co.whitbread.integrationtests.framework.wiremock.CleanupOperation
import uk.co.whitbread.integrationtests.framework.wiremock.CleanupResult
import uk.co.whitbread.integrationtests.framework.wiremock.WireMockAdmin
import uk.co.whitbread.integrationtests.framework.wiremock.WireMockInstances
import uk.co.whitbread.integrationtests.framework.wiremock.WireMockTarget
import uk.co.whitbread.integrationtests.framework.wiremock.model.RequestJournalCriteria
import uk.co.whitbread.integrationtests.framework.wiremock.model.RequestPattern
import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.selectedStubs
import uk.co.whitbread.integrationtests.testkit.model.Booking

/**
 * Stamps scenario ownership onto this stub, so a low-level builder never has to.
 *
 * Ownership is a property of the mock, not of the request it matches, and cleanup finds mappings
 * *by* this matcher. Applying it in one place means a builder cannot silently omit it and leave a
 * mapping that serves requests normally but survives its scenario.
 *
 * Every mapping the framework installs is scoped. Authentication plumbing, whose callers do not
 * propagate the baggage header and so could never be scoped or reclaimed, is served by the stack
 * instead — see `backend/integration-env/wiremock/README.md`.
 */
internal fun PlannedStub.scopedTo(testId: String): PlannedStub =
    copy(
        mappings =
            mappings.map { mapping ->
                mapping.copy(
                    request =
                        mapping.request.copy(
                            headers =
                                mapping.request.headers.orEmpty() +
                                    (BAGGAGE_HEADER to testIdHeaderMatcher(testId)),
                        ),
                )
            },
    )

/** Installs planned mappings and removes scenario-owned WireMock state. */
internal class MockInstaller(
    private val wiremock: WireMockInstances,
    private val evidenceSink: StubInstallEvidenceSink? = null,
) {
    /** Collects and installs every mapping selected by [booking]. */
    suspend fun installFor(
        booking: Booking,
        testId: String,
        excluded: Set<String> = emptySet(),
    ) {
        val selected = selectedStubs(booking, excluded)

        if (selected.isEmpty()) {
            // Validation already proved every excluded ID was in the plan, so a non-empty
            // exclusion set here means the exclusions emptied a plan that did select stubs.
            if (excluded.isEmpty()) {
                evidenceSink?.recordEmptyPlan("Booking", testId)
            } else {
                evidenceSink?.recordFullyExcludedPlan("Booking", excluded, testId)
            }
            return
        }

        for (stub in selected) {
            installStub(stub, testId)
        }
    }

    /**
     * Counts request-journal events this scenario sent to [target].
     *
     * Deliberately broad: it matches any request carrying the scenario's baggage rather than one
     * stub's shape, because a regressed service can send a request no installed stub matches. A
     * matcher-derived query would report zero for exactly the call an absence assertion exists
     * to catch.
     */
    suspend fun countCallsFor(
        target: WireMockTarget,
        testId: String,
    ): Int = wiremock[target].countRequests(anyScopedRequest(testId))

    /** Counts request-journal events for one Opera operation owned by [testId]. */
    suspend fun countCallsFor(
        endpoint: OperaEndpoint,
        testId: String,
    ): Int = wiremock[WireMockTarget.OPERA].countRequests(scopedRequest(endpoint, testId))

    /**
     * Removes every dynamic mapping and request-journal event owned by [testId] across all
     * configured WireMocks.
     *
     * Ownership is determined solely by the exact test-ID baggage matcher. Mapping and journal
     * cleanup are independent and continue after individual failures so the final exception is
     * complete and all reachable state is still removed.
     */
    suspend fun removeFor(testId: String): CleanupResult =
        withContext(NonCancellable) {
            removeScopedStateFor(testId)
        }

    private suspend fun removeScopedStateFor(testId: String): CleanupResult {
        val failures = mutableListOf<CleanupFailure>()
        var removedMappings = 0
        var removedRequestEvents = 0

        fun recordFailure(
            target: WireMockTarget,
            admin: WireMockAdmin,
            operation: CleanupOperation,
            cause: Throwable,
            mappingId: String? = null,
        ) {
            failures +=
                CleanupFailure(
                    wireMockName = target.displayName,
                    wireMockUrl = admin.baseUrl,
                    operation = operation,
                    mappingId = mappingId,
                    cause = cause,
                )
        }

        val requestCriteria =
            RequestJournalCriteria(
                urlPattern = ".*",
                headers = mapOf(BAGGAGE_HEADER to testIdHeaderMatcher(testId)),
            )

        for (target in WireMockTarget.entries) {
            val admin = wiremock[target]
            val stubs =
                try {
                    admin.listStubs()
                } catch (failure: CancellationException) {
                    throw failure
                } catch (failure: Exception) {
                    recordFailure(
                        target = target,
                        admin = admin,
                        operation = CleanupOperation.LIST_MAPPINGS,
                        cause = failure,
                    )
                    null
                }

            if (stubs != null) {
                val ownedStubs =
                    stubs.filter { stub ->
                        stub.request.headers
                            ?.get(BAGGAGE_HEADER)
                            ?.matchesTestId(testId) == true
                    }

                for (stub in ownedStubs) {
                    try {
                        admin.removeStub(checkNotNull(stub.id) { "Matching mapping has no ID" })
                        removedMappings++
                    } catch (failure: CancellationException) {
                        throw failure
                    } catch (failure: Exception) {
                        recordFailure(
                            target = target,
                            admin = admin,
                            operation = CleanupOperation.DELETE_MAPPING,
                            mappingId = stub.id,
                            cause = failure,
                        )
                    }
                }
            }

            try {
                removedRequestEvents += admin.removeRequests(requestCriteria)
            } catch (failure: CancellationException) {
                throw failure
            } catch (failure: Exception) {
                recordFailure(
                    target = target,
                    admin = admin,
                    operation = CleanupOperation.REMOVE_REQUEST_EVENTS,
                    cause = failure,
                )
            }
        }

        return CleanupResult(
            testId = testId,
            removedMappings = removedMappings,
            removedRequestEvents = removedRequestEvents,
            failures = failures.toList(),
        ).orThrow()
    }

    /** Matches every request carrying this scenario's baggage, whatever its shape. */
    private fun anyScopedRequest(testId: String): RequestPattern =
        RequestPattern(
            method = "ANY",
            urlPattern = ".*",
            headers = mapOf(BAGGAGE_HEADER to testIdHeaderMatcher(testId)),
        )

    /** Matches one Opera operation carrying this scenario's baggage. */
    private fun scopedRequest(
        endpoint: OperaEndpoint,
        testId: String,
    ): RequestPattern =
        RequestPattern(
            method = endpoint.method,
            urlPathPattern = endpoint.urlPathPattern,
            headers = mapOf(BAGGAGE_HEADER to testIdHeaderMatcher(testId)),
        )

    /**
     * Registers every mapping in [stub] in order and records [PlannedStub.id] as evidence.
     *
     * This method scopes stubs from both installation paths. `scopedTo` overwrites an existing
     * test-ID header matcher.
     */
    internal suspend fun installStub(
        stub: PlannedStub,
        testId: String,
    ) {
        val scoped = stub.scopedTo(testId)
        for (mapping in scoped.mappings) {
            wiremock[scoped.target].stub(mapping)
            evidenceSink?.recordInstalledStub(scoped.id, mapping, testId)
        }
    }
}
