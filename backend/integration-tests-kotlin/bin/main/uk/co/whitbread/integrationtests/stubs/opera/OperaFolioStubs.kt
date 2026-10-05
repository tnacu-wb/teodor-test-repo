package uk.co.whitbread.integrationtests.stubs.opera

import uk.co.whitbread.integrationtests.framework.wiremock.WireMockTarget
import uk.co.whitbread.integrationtests.framework.wiremock.model.RequestPattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StubMapping
import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.jsonResponse
import uk.co.whitbread.integrationtests.stubs.stubJsonObject
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import java.math.BigDecimal

const val OPERA_RESERVATION_FOLIOS_STUB_ID = "booking.opera.reservation-folios"

/**
 * Builds the Opera cashiering folio mapping OHIP reads to find money already taken.
 *
 * `ohip-adapter-service` calls this for every reservation whenever `rateInfoNeeded` is on, sums
 * the payments on non-empty folio windows, and treats the total as an amount already paid: it
 * overwrites the deposit policy's `amountPaid`, subtracts it from `amountDue`, and reduces
 * `outStandingCostOfStay`. This mapping therefore decides whether the amount reaching the payment
 * gateway is the full stay total or a residual balance.
 *
 * By default it represents a reservation with nothing posted against it. When
 * `room.amountAlreadyPaid` is non-zero, the same generic response contains one non-empty folio
 * window carrying that payment.
 *
 * Booking fields consumed, and how:
 *
 * | Field | Use |
 * | --- | --- |
 * | `hotel.hotelId` | request path |
 * | `room.reservationId` | request path, one mapping per reservation |
 * | `hotel.currency` | `payment.currencyCode`, kept consistent with the reservation's amounts |
 * | `room.amountAlreadyPaid` | payment on the folio; zero keeps the canonical empty window |
 * | `room.amountPostedOnFolio` | overrides that payment when the folio holds more than the summary |
 *
 * Do not match on query parameters. OHIP sends repeated `fetchInstructions` values plus
 * `includeFolioHistory`, none of which distinguish one folio request from another, and pinning
 * them would only make the mapping brittle when OHIP adjusts its fetch set.
 *
 * Three shape traps, all of which surface as a NullPointerException inside the adapter rather than
 * as a readable stub failure:
 *
 * - `reservationFolioInformation` and a non-empty `folioWindows` are required. The client filters
 *   away any response missing either, and its caller then dereferences the filtered-out result.
 * - `emptyFolio` is a boxed `Boolean` dereferenced with `.equals(false)`, so it must be present.
 * - `payment` must be present on any window whose `emptyFolio` is false, because the sum reads it
 *   without a null check.
 *
 * The response shape is inferred from `FolioWindowsDto` and OHIP's folio-window extraction, not
 * captured from Opera: the adapter's own deposit-folio GET is a preview generated from the
 * reservation, so it cannot show what this endpoint returns. Only the fields OHIP reads are
 * modelled; another reusable state should extend Booking and this generic response.
 */
fun reservationFolios(
    booking: Booking,
    room: BookingRoom,
): PlannedStub = reservationFolios(booking, listOf(room))

fun reservationFolios(
    booking: Booking,
    rooms: List<BookingRoom>,
): PlannedStub =
    PlannedStub(
        id = OPERA_RESERVATION_FOLIOS_STUB_ID,
        target = WireMockTarget.OPERA,
        mappings = rooms.map { room -> reservationFoliosMapping(booking, room) },
    )

private fun reservationFoliosMapping(
    booking: Booking,
    room: BookingRoom,
): StubMapping {
    val reservationId =
        room.reservationId
            ?: error("BookingRoom.reservationId must be configured for reservation-folio stubs")

    return StubMapping(
        request =
            RequestPattern(
                method = "GET",
                urlPath = "/csh/v1/hotels/${booking.hotel.hotelId}/reservations/$reservationId/folios",
            ),
        response = jsonResponse(jsonBody = folioWindowsBody(booking, room)),
    )
}

/**
 * Builds a single empty or paid folio window from the room's reusable payment state.
 *
 * The posted amount is `amountPostedOnFolio` when the room states one and `amountAlreadyPaid`
 * otherwise, so by default the folio and the reservation's own money summary agree. Stating it
 * separately is the only way to express a folio holding more (or less) than the summary reports,
 * the temporal state a consumer's folio-wins reconciliation is visible in.
 */
private fun folioWindowsBody(
    booking: Booking,
    room: BookingRoom,
): kotlinx.serialization.json.JsonObject {
    val postedAmount = BigDecimal.valueOf(room.amountPostedOnFolio ?: room.amountAlreadyPaid)
    val hasPayment = postedAmount.compareTo(BigDecimal.ZERO) != 0

    return stubJsonObject(
        "reservationFolioInformation" to
            mapOf(
                "folioWindows" to
                    listOf(
                        mapOf(
                            "folioWindowNo" to 1,
                            "emptyFolio" to !hasPayment,
                            "emptyWindow" to !hasPayment,
                            "payment" to
                                mapOf(
                                    "amount" to postedAmount,
                                    "currencyCode" to booking.hotel.currency,
                                ),
                            "balance" to
                                mapOf(
                                    "amount" to BigDecimal.ZERO,
                                    "currencyCode" to booking.hotel.currency,
                                ),
                        ),
                    ),
            ),
        "links" to emptyList<Any>(),
    )
}
