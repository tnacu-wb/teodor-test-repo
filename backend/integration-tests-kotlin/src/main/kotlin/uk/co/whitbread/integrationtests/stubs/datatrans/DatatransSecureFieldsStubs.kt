package uk.co.whitbread.integrationtests.stubs.datatrans

import uk.co.whitbread.integrationtests.framework.wiremock.WireMockTarget
import uk.co.whitbread.integrationtests.framework.wiremock.model.BodyPattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.RequestPattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StubMapping
import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.jsonPathStringLiteral
import uk.co.whitbread.integrationtests.stubs.jsonResponse
import uk.co.whitbread.integrationtests.stubs.stubJsonObject
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.CardPayment
import uk.co.whitbread.integrationtests.testkit.model.Rate

const val DATATRANS_SECURE_FIELDS_INIT_STUB_ID = "booking.datatrans.secure-fields-init"

/**
 * Builds the Datatrans Secure Fields initialisation mapping for a web card payment.
 *
 * The Payment Orchestration Service calls this once per initialisation and returns the
 * `transactionId` to the browser, which uses it to mount the hosted card fields.
 *
 * Booking fields consumed, and how:
 *
 * | Field | Use |
 * | --- | --- |
 * | `cardPayment.transactionId` | the transaction the gateway hands back, and what a journey asserts |
 * | `hotel.availableRates[].nightlyRate` | nightly rate times nights is the stay total the gateway is asked for |
 * | `arrival`, `departure` | night count behind that total |
 * | `hotel.currency` | request `currency`, and the exponent that converts the total to minor units |
 *
 * **The amount matcher is the only check on the conversion.** A journey can read call counts from
 * the scenario-owned request journal but not request bodies, so nothing else can observe what the
 * service actually asked the gateway for. Matching the minor-unit amount here means a broken
 * conversion — wrong exponent, per-night instead of per-stay, a reservation lookup returning the
 * wrong figure — stops matching this mapping and fails its journey. Do not relax it.
 *
 * `returnUrl` is deliberately not matched: it is a parameter of the client call rather than a fact
 * about the booking, so pinning it would couple this mapping to whatever a journey passes.
 *
 * The amount comes from the booking's first reservation room, because that is where the service
 * takes it from: the reservation response maps only `reservationByIdList` first entry's rate
 * summary. A multi-room booking is therefore charged its first room's total, and a journey covering
 * multi-room payment needs that behaviour confirmed with the product before it is stubbed as
 * correct.
 */
fun secureFieldsInit(booking: Booking): PlannedStub {
    val cardPayment =
        booking.cardPayment
            ?: error("booking.cardPayment must be configured for Datatrans Secure Fields stubs")
    val room =
        booking.rooms.firstOrNull()
            ?: error("booking.rooms must contain the room being paid for")
    val rate =
        selectedRateForPayment(booking, room)
            ?: error(
                "booking.hotel.availableRates must contain a rate matching the paid room " +
                    "(roomType=${room.roomType}, adults=${room.adults}, children=${room.children})",
            )

    return PlannedStub(
        id = DATATRANS_SECURE_FIELDS_INIT_STUB_ID,
        target = WireMockTarget.WORLDLINE,
        mappings = listOf(secureFieldsInitMapping(booking, rate, cardPayment)),
    )
}

private fun secureFieldsInitMapping(
    booking: Booking,
    rate: Rate,
    cardPayment: CardPayment,
): StubMapping {
    val amount = minorUnits(booking, rate)
    val currency = booking.hotel.currency

    return StubMapping(
        request =
            RequestPattern(
                method = "POST",
                urlPath = "/v2/transactions/secure-fields",
                bodyPatterns =
                    listOf(
                        BodyPattern(matchesJsonPath = "$[?(@.amount == $amount)]"),
                        BodyPattern(
                            matchesJsonPath =
                                "$[?(@.currency == ${jsonPathStringLiteral(currency)})]",
                        ),
                    ),
            ),
        response =
            jsonResponse(
                status = 201,
                jsonBody = stubJsonObject("transactionId" to cardPayment.transactionId),
            ),
    )
}
