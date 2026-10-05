package uk.co.whitbread.integrationtests.testkit.model

import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import uk.co.whitbread.integrationtests.framework.http.BAGGAGE_HEADER
import uk.co.whitbread.integrationtests.framework.http.testIdHeaderMatcher
import uk.co.whitbread.integrationtests.framework.reporting.StubInstallEvidenceSink
import uk.co.whitbread.integrationtests.framework.wiremock.model.StubMapping
import uk.co.whitbread.integrationtests.support.wiremock.recordingInstallWireMock
import uk.co.whitbread.integrationtests.testkit.mocks.MockInstaller
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.writeText

private const val TEST_ID = "t07-characterization"
private const val GOLDEN_FILE = "booking-provisioning.golden.txt"

/**
 * Locks the Booking installer's output while its implementation moves into main sources.
 *
 * Covers the mappings reachable from [characterizedBooking], not every mapping the installer can
 * produce; see that fixture for what it deliberately leaves out and why.
 */
class BookingProvisioningCharacterizationTest :
    FunSpec({
        test("representative Booking produces the characterized WireMock mapping snapshot") {
            val targets = mutableListOf<String>()
            val sink = CapturingStubInstallEvidenceSink()
            val wiremock =
                recordingInstallWireMock { target ->
                    targets += target
                    sink.expectCallbackFrom(target)
                }

            try {
                MockInstaller(wiremock.instances, sink).installFor(characterizedBooking(), TEST_ID)

                // Pair each evidence callback with the target that accepted its mapping.
                targets.shouldContainExactly(sink.installed.map { it.target })
                // The golden pins the mapping count, targets, IDs, request matchers, and responses.
                assertMatchesGolden(snapshotText(sink.installed))
            } finally {
                wiremock.close()
            }
        }

        test("every installed mapping is owned by the installing scenario") {
            val sink = CapturingStubInstallEvidenceSink()
            val wiremock = recordingInstallWireMock { target -> sink.expectCallbackFrom(target) }

            try {
                MockInstaller(wiremock.instances, sink).installFor(characterizedBooking(), TEST_ID)

                // No exceptions. Authentication plumbing, the one thing that could not carry this
                // matcher, is served by the stack's WireMock startup mappings and never installed
                // here — so an unowned mapping means cleanup would leak it, not that it is special.
                sink.installed.forEach { captured ->
                    withClue("${captured.mockName} must be owned by $TEST_ID") {
                        captured.mapping.request.headers
                            ?.get(BAGGAGE_HEADER) shouldBe testIdHeaderMatcher(TEST_ID)
                    }
                }
            } finally {
                wiremock.close()
            }
        }
    })

/** One target-qualified mapping captured from the reusable provisioning pipeline. */
private data class CapturedMapping(
    /** WireMock host that received the mapping. */
    val target: String,
    /** Stable logical identifier selected from the Booking defaults. */
    val mockName: String,
    /** Mapping supplied to WireMock. */
    val mapping: StubMapping,
)

/** Captures successful install callbacks so the characterization never requires real WireMock. */
private class CapturingStubInstallEvidenceSink : StubInstallEvidenceSink {
    /** Target names observed by the MockEngine in mapping-install order. */
    private val pendingTargets = ArrayDeque<String>()

    /** Target-qualified mappings emitted after successful registration. */
    val installed = mutableListOf<CapturedMapping>()

    /** Adds the WireMock target that is awaiting its successful-install callback. */
    fun expectCallbackFrom(target: String) {
        pendingTargets += target
    }

    /** Captures one successfully registered mapping with its pending WireMock target. */
    override fun recordInstalledStub(
        mockName: String,
        mapping: StubMapping,
        testId: String,
    ) {
        check(testId == TEST_ID)
        installed += CapturedMapping(pendingTargets.removeFirst(), mockName, mapping)
    }

    /** Rejects an empty-plan callback because the representative Booking must install mappings. */
    override fun recordEmptyPlan(
        modelName: String,
        testId: String,
    ) {
        error("Unexpected empty plan for $modelName/$testId")
    }

    /** Rejects the fully-excluded callback because the characterization excludes nothing. */
    override fun recordFullyExcludedPlan(
        modelName: String,
        excluded: Set<String>,
        testId: String,
    ) {
        error("Unexpected fully excluded plan for $modelName/$testId")
    }
}

/**
 * Builds representative data that reaches the CDH, AEM, Opera, and Worldline installers.
 *
 * Leaves `rooms`, `arrival`, and `departure` unset, which switches off every gate keyed on
 * requestable or reservation rooms: reservation create/get/put, availability, packages, guest
 * profiles, and Opera rate-info. Reservation-create mappings embed `ZonedDateTime.now()`
 * (`OperaReservationStubs.defaultCreateDateTime`), so the golden would differ on every run; the rest
 * are excluded as a side effect of that omission rather than by intent. Auth is not covered here at
 * all — the JWKS is a startup mapping owned by the Compose stack, guarded by `BakedAuthMappingsTest`.
 */
private fun characterizedBooking(): Booking {
    val company =
        Company(
            name = "Characterization Company",
            corpId = "1370",
            companyId = "2569623",
            telephoneNumber = "02079460000",
            address =
                CompanyAddress(
                    addressLine1 = "1 Synthetic Street",
                    addressLine4 = "London",
                    postalCode = "SW1A 1AA",
                ),
        )
    val hotel =
        Hotel(
            hotelId = "CHAR01",
            shortId = "characterization-hotel",
            name = "Characterization Hotel",
            addressLine = "2 Synthetic Road",
            city = "London",
            postcode = "SW1A 2AA",
            phone = "02079460001",
            availableRoomTypes =
                listOf(
                    HotelRoomType(
                        roomClass = "ST",
                        roomType = "DOUBLE",
                        numberOfRooms = 10,
                    ),
                ),
            availableRates =
                listOf(
                    Rate(
                        ratePlan = "FLEX",
                        roomType = "DOUBLE",
                        adults = 2,
                    ),
                ),
        )

    return Booking(
        hotels = listOf(hotel),
        companies = listOf(company),
        aem =
            Aem(
                footer =
                    AemFooter(
                        country = "gb",
                        language = "en",
                        site = AemSite.BUSINESS_BOOKER,
                    ),
            ),
        loggedUser =
            LoggedUser(
                accessLevel = "SUPER",
                companyAccountId = "characterization-company-account",
                companyId = company.companyId,
                employeeId = "characterization-employee",
                email = "characterization@example.test",
                tetheredAccount =
                    TetheredAccount(
                        pibaAccountId = "characterization-piba",
                        tetheredUserGuid = "characterization-guid",
                        accountActivity =
                            AccountActivity(
                                worldlineAccount =
                                    WorldlineAccount(
                                        billingFrequency = BillingFrequency.MONTHLY,
                                        status = "Active",
                                        currency = "GBP",
                                    ),
                                paymentHistory =
                                    PaymentHistory(
                                        payments =
                                            listOf(
                                                PaymentInfoItem(
                                                    paymentDate = "2026-07-01",
                                                    paymentDescription = "Synthetic payment",
                                                    value = "42.00",
                                                ),
                                            ),
                                    ),
                            ),
                    ),
            ),
    )
}

/**
 * Renders the installed mappings as reviewable text after replacing generated JWKS key material.
 *
 * Deliberately not a digest. This snapshot exists to guard refactors of the default collection and stub
 * layers, and a hash answers only *that* the output moved, never *how* — which is the one thing
 * a reviewer needs when the whole premise of a change is "the mappings come out identical".
 */
private fun snapshotText(installed: List<CapturedMapping>): String {
    val json =
        Json {
            encodeDefaults = false
            prettyPrint = true
        }
    return installed.joinToString("\n\n") { captured ->
        val mapping = normalizeHeaderOrder(json.encodeToJsonElement(StubMapping.serializer(), captured.mapping))
        "=== ${captured.target} | ${captured.mockName}\n" +
            json.encodeToString(JsonElement.serializer(), mapping)
    } + "\n"
}

/**
 * Compares [actual] against the checked-in golden file.
 *
 * On mismatch the actual snapshot is written under `build/characterization` so an intended
 * change can be adopted by copying one file, rather than by hand-editing expected text.
 */
private fun assertMatchesGolden(actual: String) {
    val goldenPath = "/contracts/$GOLDEN_FILE"
    val expected = BookingProvisioningCharacterizationTest::class.java.getResource(goldenPath)?.readText()
    val canonicalActual = canonicalSnapshot(actual)
    val canonicalExpected = expected?.let(::canonicalSnapshot)

    if (canonicalExpected != canonicalActual) {
        val written = Path("build/characterization").createDirectories().resolve(GOLDEN_FILE)
        written.writeText(canonicalActual)
        withClue(
            "Installer output differs from src/test/resources$goldenPath. " +
                "If the change is intended, copy $written over it.",
        ) {
            canonicalActual shouldBe canonicalExpected
        }
    }
}

/** Makes the golden comparison independent of default stub installation order. */
private fun canonicalSnapshot(snapshot: String): String =
    Regex("""(?ms)^=== .*?(?=^=== |\z)""")
        .findAll(snapshot)
        .map { it.value.trimEnd() }
        .sorted()
        .joinToString(separator = "\n\n", postfix = "\n")

/**
 * Sorts header maps so the snapshot is insensitive to the order entries were added.
 *
 * WireMock matches headers as a JSON object, so key order carries no meaning. Normalising it keeps
 * a pure reordering — such as scenario ownership being stamped by the installer rather than added
 * by each builder — out of the golden diff, leaving only changes to what a mapping matches or
 * returns.
 */
private fun normalizeHeaderOrder(element: JsonElement): JsonElement =
    when (element) {
        is JsonObject ->
            JsonObject(
                element.mapValues { (key, value) ->
                    if (key == "headers" && value is JsonObject) {
                        JsonObject(value.toSortedMap())
                    } else {
                        normalizeHeaderOrder(value)
                    }
                },
            )

        is JsonArray -> JsonArray(element.map(::normalizeHeaderOrder))
        else -> element
    }
