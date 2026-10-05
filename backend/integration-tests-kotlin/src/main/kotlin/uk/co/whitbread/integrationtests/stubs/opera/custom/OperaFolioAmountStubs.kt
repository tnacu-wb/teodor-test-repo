package uk.co.whitbread.integrationtests.stubs.opera.custom

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.opera.reservationFolios
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom

const val OPERA_RESERVATION_FOLIOS_NO_WINDOWS_STUB_ID = "opera.reservation-folios.no-windows"

/**
 * Models a reservation Opera has opened no cashiering folio window for: the folio information block
 * is present but its window list is empty.
 *
 * Nothing has been posted, so the reservation's own money summary is the whole truth. Derived from
 * the default folio builder to keep the matchers identical; install with the default excluded.
 */
fun reservationFoliosWithoutWindows(
    booking: Booking,
    rooms: List<BookingRoom> = booking.rooms,
): PlannedStub {
    val default = reservationFolios(booking, rooms)
    return default.copy(
        id = OPERA_RESERVATION_FOLIOS_NO_WINDOWS_STUB_ID,
        mappings =
            default.mappings.map { mapping ->
                val body = requireNotNull(mapping.response.jsonBody) { "folio read must carry a JSON body" }
                mapping.copy(response = mapping.response.copy(jsonBody = withoutFolioWindows(body)))
            },
    )
}

/** Empties the folio-window list, keeping the folio information block itself. */
private fun withoutFolioWindows(body: JsonElement): JsonElement {
    val root = body.jsonObject
    val folioInformation = requireNotNull(root["reservationFolioInformation"]).jsonObject

    return JsonObject(
        root +
            (
                "reservationFolioInformation" to
                    JsonObject(folioInformation + ("folioWindows" to JsonArray(emptyList())))
            ),
    )
}
