package uk.co.whitbread.integrationtests.testkit.mocks

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.framework.http.BAGGAGE_HEADER
import uk.co.whitbread.integrationtests.framework.http.matchesTestId
import uk.co.whitbread.integrationtests.framework.wiremock.WireMockTarget
import uk.co.whitbread.integrationtests.support.wiremock.journalCountWireMock

private const val COUNT_TEST_ID = "count-scenario"

/**
 * Verifies how a scenario's request-journal query is built.
 *
 * The query exists to prove a scenario did not touch an upstream. Such an assertion is only
 * worth anything if it can fail, so these tests pin both the breadth of the matcher and the
 * fact that a non-zero journal count reaches the caller.
 */
class MockInstallerCountTest :
    FunSpec({

        test("counting matches any scenario-owned request shape") {
            val wiremock = journalCountWireMock(journalCount = 4)

            try {
                val installer = MockInstaller(wiremock.instances)

                // A non-zero answer must survive the round trip: a count that always returned 0
                // would satisfy every absence assertion in the suite.
                installer.countCallsFor(WireMockTarget.AEM, COUNT_TEST_ID) shouldBeExactly 4

                // The query stays scoped by baggage while matching any method and path, so a
                // request that no installed stub would match is still observed.
                val query = wiremock.countQueries.single()
                query.host shouldBe "aem.test"
                query.pattern.urlPattern shouldBe ".*"
                query.pattern.method shouldBe "ANY"
                query.pattern.url shouldBe null
                query.pattern.headers
                    ?.get(BAGGAGE_HEADER)
                    ?.matchesTestId(COUNT_TEST_ID) shouldBe true
            } finally {
                wiremock.close()
            }
        }

        test("counting queries only the requested WireMock and needs no prior installation") {
            val wiremock = journalCountWireMock(journalCount = 0)

            try {
                MockInstaller(wiremock.instances)
                    .countCallsFor(WireMockTarget.WORLDLINE, COUNT_TEST_ID) shouldBeExactly 0

                wiremock.countQueries.single().host shouldBe "worldline.test"
            } finally {
                wiremock.close()
            }
        }

        test("counting an Opera endpoint matches its operation and scenario") {
            val wiremock = journalCountWireMock(journalCount = 2)

            try {
                MockInstaller(wiremock.instances)
                    .countCallsFor(OperaEndpoint.GET_HOUSEKEEPING, COUNT_TEST_ID) shouldBeExactly 2

                val query = wiremock.countQueries.single()
                query.host shouldBe "opera.test"
                query.pattern.method shouldBe "GET"
                query.pattern.urlPathPattern shouldBe
                    "^/hsk/v1/hotels/[^/]+/housekeepingOverview$"
                query.pattern.url shouldBe null
                query.pattern.urlPattern shouldBe null
                query.pattern.queryParameters shouldBe null
                query.pattern.bodyPatterns shouldBe null
                query.pattern.headers
                    ?.get(BAGGAGE_HEADER)
                    ?.matchesTestId(COUNT_TEST_ID) shouldBe true
            } finally {
                wiremock.close()
            }
        }
    })
