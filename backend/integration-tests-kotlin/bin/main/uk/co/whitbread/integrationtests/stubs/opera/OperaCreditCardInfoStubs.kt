package uk.co.whitbread.integrationtests.stubs.opera

import uk.co.whitbread.integrationtests.framework.wiremock.WireMockTarget
import uk.co.whitbread.integrationtests.framework.wiremock.model.RequestPattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StringValuePattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StubMapping
import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.jsonResponse
import uk.co.whitbread.integrationtests.stubs.stubJsonObject
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.OperaPaymentCard

const val OPERA_CREDIT_CARD_INFO_STUB_ID = "booking.opera.credit-card-info"

/**
 * Builds Front Desk credit-card-info mappings for reservation rooms that carry an Opera card.
 *
 * The query pins hotel id, card id, and Opera's credit-card id context. The body is driven by
 * the room's [OperaPaymentCard] so GET reservation and this lookup describe the same card.
 */
fun creditCardInfo(
    booking: Booking,
    rooms: List<BookingRoom>,
): PlannedStub =
    PlannedStub(
        id = OPERA_CREDIT_CARD_INFO_STUB_ID,
        target = WireMockTarget.OPERA,
        mappings =
            rooms
                .map { room ->
                    requireNotNull(room.operaPaymentCard) {
                        "BookingRoom.operaPaymentCard must be configured for credit-card-info stubs"
                    }
                }.groupBy { card -> card.cardId }
                .map { (cardId, cards) ->
                    require(cards.all { card -> card == cards.first() }) {
                        "Opera cardId '$cardId' must have identical card facts across booking rooms"
                    }
                    cards.first()
                }.map { card -> creditCardInfoMapping(booking, card) },
    )

private fun creditCardInfoMapping(
    booking: Booking,
    card: OperaPaymentCard,
): StubMapping =
    StubMapping(
        request =
            RequestPattern(
                method = "GET",
                urlPath = "/fof/config/v1/creditCardInfo",
                queryParameters =
                    mapOf(
                        "hotelId" to StringValuePattern(equalTo = booking.hotel.hotelId),
                        "cardId" to StringValuePattern(equalTo = card.cardId),
                        "cardIdContext" to StringValuePattern(equalTo = "OPERA"),
                        "cardIdType" to StringValuePattern(equalTo = "CreditCard"),
                    ),
                headers = hotelHeaders(booking.hotel.hotelId),
            ),
        response =
            jsonResponse(
                jsonBody =
                    stubJsonObject(
                        "creditCard" to
                            mapOf(
                                "cardId" to mapOf("id" to card.cardId, "type" to "CreditCard"),
                                "cardType" to card.cardType,
                                "cardNumber" to card.cardNumber,
                                "cardNumberMasked" to maskedFrontDeskCardNumber(card.cardNumber),
                                "expirationDate" to card.expirationDate,
                                "expirationDateMasked" to maskedFrontDeskExpirationDate(card.expirationDate),
                            ),
                        "links" to emptyList<Any>(),
                    ),
            ),
    )

private fun maskedFrontDeskCardNumber(cardNumber: String): String = "X".repeat(12) + cardNumber.takeLast(4)

private fun maskedFrontDeskExpirationDate(expirationDate: String): String =
    expirationDate.substring(5, 7) + "/" + expirationDate.substring(2, 4)
