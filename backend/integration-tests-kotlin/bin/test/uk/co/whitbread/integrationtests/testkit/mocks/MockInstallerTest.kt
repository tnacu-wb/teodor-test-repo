package uk.co.whitbread.integrationtests.testkit.mocks

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import uk.co.whitbread.integrationtests.framework.http.BAGGAGE_HEADER
import uk.co.whitbread.integrationtests.framework.http.testIdHeaderMatcher
import uk.co.whitbread.integrationtests.framework.reporting.StubInstallEvidenceSink
import uk.co.whitbread.integrationtests.framework.wiremock.WireMockTarget
import uk.co.whitbread.integrationtests.framework.wiremock.model.RequestPattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.ResponseDefinition
import uk.co.whitbread.integrationtests.framework.wiremock.model.StubMapping
import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.aem.AEM_HOTEL_DIRECTORY_STUB_ID
import uk.co.whitbread.integrationtests.stubs.aem.allHotels
import uk.co.whitbread.integrationtests.stubs.cdh.CDH_COMPANY_SEARCH_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_COMPANY_NEGOTIATED_RATES_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_COMPANY_PROFILE_BY_ID_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_COMPANY_PROFILE_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_PROFILE_UPDATE_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.getReservationBadRequest
import uk.co.whitbread.integrationtests.support.fixtures.aemBooking
import uk.co.whitbread.integrationtests.support.fixtures.companyBooking
import uk.co.whitbread.integrationtests.support.wiremock.installOnlyWireMock
import uk.co.whitbread.integrationtests.support.wiremock.isOwnedBy
import uk.co.whitbread.integrationtests.testkit.model.Booking

private const val INSTALL_TEST_ID = "stub-install"
private const val INSTALL_COMPANY_NAME = "Installer Test Company"

class MockInstallerTest :
    FunSpec({
        test("installFor installs every gated default") {
            val wiremock = installOnlyWireMock()
            val sink = CapturingEvidenceSink()

            try {
                MockInstaller(wiremock.instances, sink).installFor(companyBooking(INSTALL_COMPANY_NAME), INSTALL_TEST_ID)

                // The company-profile default carries two mappings: the empty companyId read
                // that drives the corporate-id fallback, plus the populated corpId read. The
                // profile-by-company-id default carries one mapping per real caller shape
                // (six fetch instructions, and no query at all). A Booking that knows a
                // company also installs the profile-amend capability.
                sink.installed.map { it.stubId } shouldContainExactlyInAnyOrder
                    listOf(
                        CDH_COMPANY_SEARCH_STUB_ID,
                        OPERA_PROFILE_UPDATE_STUB_ID,
                        OPERA_COMPANY_PROFILE_STUB_ID,
                        OPERA_COMPANY_PROFILE_STUB_ID,
                        OPERA_COMPANY_PROFILE_BY_ID_STUB_ID,
                        OPERA_COMPANY_PROFILE_BY_ID_STUB_ID,
                        OPERA_COMPANY_NEGOTIATED_RATES_STUB_ID,
                    )
                wiremock.installed.map { it.host } shouldContainExactlyInAnyOrder
                    listOf("cdh.test", "opera.test", "opera.test", "opera.test", "opera.test", "opera.test", "opera.test")
            } finally {
                wiremock.close()
            }
        }

        test("installFor applies exclusions") {
            val wiremock = installOnlyWireMock()
            val sink = CapturingEvidenceSink()

            try {
                MockInstaller(wiremock.instances, sink).installFor(
                    booking = companyBooking(INSTALL_COMPANY_NAME),
                    testId = INSTALL_TEST_ID,
                    excluded = setOf(OPERA_COMPANY_PROFILE_BY_ID_STUB_ID),
                )

                sink.installed.map { it.stubId } shouldNotContain OPERA_COMPANY_PROFILE_BY_ID_STUB_ID
                sink.installed.map { it.stubId } shouldContainExactlyInAnyOrder
                    listOf(
                        CDH_COMPANY_SEARCH_STUB_ID,
                        OPERA_PROFILE_UPDATE_STUB_ID,
                        OPERA_COMPANY_PROFILE_STUB_ID,
                        OPERA_COMPANY_PROFILE_STUB_ID,
                        OPERA_COMPANY_NEGOTIATED_RATES_STUB_ID,
                    )
            } finally {
                wiremock.close()
            }
        }

        test("direct-install ownership is stamped before registration") {
            val wiremock = installOnlyWireMock()
            val installer = MockInstaller(wiremock.instances)

            try {
                installer.installStub(
                    planned("custom.missing-test-id", listOf("/missing-test-id"), testId = null),
                    INSTALL_TEST_ID,
                )
                installer.installStub(
                    planned("custom.foreign-test-id", listOf("/foreign-test-id"), testId = "another-test"),
                    INSTALL_TEST_ID,
                )

                wiremock.installed
                    .filter { it.mapping.request.url in setOf("/missing-test-id", "/foreign-test-id") }
                    .forEach { it.mapping.isOwnedBy(INSTALL_TEST_ID) shouldBe true }
            } finally {
                wiremock.close()
            }
        }

        test("exclusions that empty the plan record fully-excluded evidence, not an empty plan") {
            val wiremock = installOnlyWireMock()
            val sink = CapturingEvidenceSink()
            val allSelected =
                setOf(
                    CDH_COMPANY_SEARCH_STUB_ID,
                    OPERA_PROFILE_UPDATE_STUB_ID,
                    OPERA_COMPANY_PROFILE_STUB_ID,
                    OPERA_COMPANY_PROFILE_BY_ID_STUB_ID,
                    OPERA_COMPANY_NEGOTIATED_RATES_STUB_ID,
                )

            try {
                MockInstaller(wiremock.instances, sink).installFor(
                    booking = companyBooking(INSTALL_COMPANY_NAME),
                    testId = INSTALL_TEST_ID,
                    excluded = allSelected,
                )

                wiremock.installed.shouldBeEmpty()
                sink.emptyPlans.shouldBeEmpty()
                sink.fullyExcludedPlans shouldContainExactly
                    listOf(Triple("Booking", allSelected, INSTALL_TEST_ID))
            } finally {
                wiremock.close()
            }
        }

        test("empty Booking records empty-plan evidence without WireMock I/O") {
            val wiremock = installOnlyWireMock()
            val sink = CapturingEvidenceSink()

            try {
                MockInstaller(wiremock.instances, sink).installFor(Booking(), INSTALL_TEST_ID)

                wiremock.installed.shouldBeEmpty()
                sink.installed.shouldBeEmpty()
                sink.emptyPlans shouldContainExactly listOf("Booking" to INSTALL_TEST_ID)
            } finally {
                wiremock.close()
            }
        }

        test("failed registration produces no success evidence") {
            val wiremock = installOnlyWireMock(mappingStatus = { HttpStatusCode.InternalServerError })
            val sink = CapturingEvidenceSink()

            try {
                shouldThrow<IllegalStateException> {
                    MockInstaller(wiremock.instances, sink).installFor(aemBooking(), INSTALL_TEST_ID)
                }
                sink.installed.shouldBeEmpty()
            } finally {
                wiremock.close()
            }
        }

        test("concurrent installFor calls retain their exact test-ID matchers") {
            val wiremock = installOnlyWireMock()
            val installer = MockInstaller(wiremock.instances)
            val testIds = listOf("session-a", "session-b")

            try {
                coroutineScope {
                    testIds.map { testId -> async { installer.installFor(aemBooking(), testId) } }.awaitAll()
                }

                wiremock.installed
                    .map {
                        it.mapping.request.headers
                            ?.get(BAGGAGE_HEADER)
                    }.toSet() shouldBe testIds.map(::testIdHeaderMatcher).toSet()
            } finally {
                wiremock.close()
            }
        }

        test("direct installation uses the stub ID and installs every mapping in order") {
            val wiremock = installOnlyWireMock()
            val sink = CapturingEvidenceSink()
            val installer = MockInstaller(wiremock.instances, sink)
            val stub = planned("direct.stub", listOf("/one", "/two"), target = WireMockTarget.AEM)

            try {
                installer.installStub(stub, INSTALL_TEST_ID)

                sink.installed.map { it.stubId } shouldContainExactly listOf("direct.stub", "direct.stub")
                wiremock.installed.map { it.mapping.request.url } shouldContainExactly listOf("/one", "/two")
                wiremock.installed.forEach { it.mapping.isOwnedBy(INSTALL_TEST_ID) shouldBe true }
            } finally {
                wiremock.close()
            }
        }

        test("direct builders carry their own evidence ID and target") {
            val wiremock = installOnlyWireMock()
            val sink = CapturingEvidenceSink()
            val installer = MockInstaller(wiremock.instances, sink)

            try {
                installer.installStub(allHotels(emptyList()), INSTALL_TEST_ID)
                installer.installStub(getReservationBadRequest("HOTEL", "reservation"), INSTALL_TEST_ID)

                sink.installed.map { it.stubId } shouldContainExactly
                    listOf(AEM_HOTEL_DIRECTORY_STUB_ID, "opera.get-reservation.bad-request")
                wiremock.installed.map { it.host } shouldContainExactly listOf("aem.test", "opera.test")
            } finally {
                wiremock.close()
            }
        }
    })

private data class CapturedEvidence(
    val stubId: String,
    val mapping: StubMapping,
    val testId: String,
)

private class CapturingEvidenceSink : StubInstallEvidenceSink {
    val installed = mutableListOf<CapturedEvidence>()
    val emptyPlans = mutableListOf<Pair<String, String>>()
    val fullyExcludedPlans = mutableListOf<Triple<String, Set<String>, String>>()

    override fun recordInstalledStub(
        mockName: String,
        mapping: StubMapping,
        testId: String,
    ) {
        installed += CapturedEvidence(mockName, mapping, testId)
    }

    override fun recordEmptyPlan(
        modelName: String,
        testId: String,
    ) {
        emptyPlans += modelName to testId
    }

    override fun recordFullyExcludedPlan(
        modelName: String,
        excluded: Set<String>,
        testId: String,
    ) {
        fullyExcludedPlans += Triple(modelName, excluded, testId)
    }
}

private fun planned(
    id: String,
    paths: List<String>,
    target: WireMockTarget = WireMockTarget.OPERA,
    testId: String? = INSTALL_TEST_ID,
): PlannedStub =
    PlannedStub(
        id = id,
        target = target,
        mappings =
            paths.map { path ->
                StubMapping(
                    request =
                        RequestPattern(
                            method = "GET",
                            url = path,
                            headers = testId?.let { mapOf(BAGGAGE_HEADER to testIdHeaderMatcher(it)) },
                        ),
                    response = ResponseDefinition(status = 200),
                )
            },
    )
