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

const val DATATRANS_MOBILE_SDK_INIT_STUB_ID = "booking.datatrans.mobile-sdk-init"

/**
 * Builds the Datatrans transaction-init mapping for a native app card payment.
 *
 * The Payment Orchestration Service calls `POST /v2/transactions` once per Mobile SDK
 * initialisation and returns the `transactionId` to the app, which hands it to Datatrans Mobile
 * SDK v4. The path is the generic v2 transaction endpoint, distinct from the Secure Fields
 * `/v2/transactions/secure-fields` sibling, so both mappings coexist on this WireMock.
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
 * **The amount matcher is the only check on the conversion**, for the same reason it is on the
 * Secure Fields mapping: a journey can read call counts from the scenario-owned request journal
 * but not request bodies, so a broken conversion is only visible as this mapping ceasing to match.
 * Do not relax it.
 *
 * Three request fields are deliberately left unmatched. `refno` is the booking reference the
 * reservation service allocates at create time, so a journey cannot know it up front. `option`
 * (the service always asks for `createAlias`) and `webhook.url` (present only when the service is
 * configured with a callback base URL, which the integration stack is not) are facts about the
 * caller's configuration rather than about the booking, so pinning them would couple this mapping
 * to how the payment service happens to be deployed.
 */
fun mobileSdkInit(booking: Booking): PlannedStub {
    val cardPayment =
        booking.cardPayment
            ?: error("booking.cardPayment must be configured for Datatrans Mobile SDK stubs")
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
        id = DATATRANS_MOBILE_SDK_INIT_STUB_ID,
        target = WireMockTarget.WORLDLINE,
        mappings = listOf(mobileSdkInitMapping(booking, rate, cardPayment)),
    )
}

private fun mobileSdkInitMapping(
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
                urlPath = "/v2/transactions",
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
