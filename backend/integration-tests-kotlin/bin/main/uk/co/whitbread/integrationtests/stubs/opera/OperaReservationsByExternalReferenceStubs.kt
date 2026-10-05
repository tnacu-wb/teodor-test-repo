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
import uk.co.whitbread.integrationtests.testkit.model.ReservationStatus
import java.time.temporal.ChronoUnit

const val OPERA_EXTERNAL_REFERENCE_SEARCH_STUB_ID = "booking.opera.reservations-by-external-reference"

/**
 * Builds Opera's reservation-summary searches for references held in [booking].
 *
 * The external-reference query selects the returned world state and is therefore pinned. OHIP
 * appends `-%` when the fourth character is `R`, so the matcher applies that search convention
 * while the returned reservation retains the original external reference. Fetch instructions are
 * deliberately not matched because they describe the caller's projection, not a different
 * reservation. When `cdhBookingReference` is present and differs from the external reference, an
 * additional empty result is installed for it: a CDH confirmation is not an Opera external
 * reference, so that request must miss before confirmation fallback begins. An empty [rooms] list
 * represents a successful search with no matches and returns a null `reservationInfo`, which is
 * the shape OHIP uses for its not-found branch. A populated response includes the
 * ReservationContact link consumed by OHIP's profile enrichment and the non-null rate
 * amount/currency fields its enhanced mapper requires.
 */
fun reservationsByExternalReference(
    booking: Booking,
    rooms: List<BookingRoom>,
): PlannedStub {
    val references =
        buildList {
            booking.bookingReference?.let { externalReference ->
                add(externalReference to rooms)
            }
            booking.cdhBookingReference
                ?.takeIf { it != booking.bookingReference }
                ?.let { cdhReference -> add(cdhReference to emptyList()) }
        }
    require(references.isNotEmpty()) {
        "booking.bookingReference or booking.cdhBookingReference must be configured for " +
            "external-reference search stubs"
    }

    return PlannedStub(
        id = OPERA_EXTERNAL_REFERENCE_SEARCH_STUB_ID,
        target = WireMockTarget.OPERA,
        mappings =
            references.map { (reference, matchingRooms) ->
                externalReferenceSearchMapping(booking, reference, matchingRooms)
            },
    )
}

private fun externalReferenceSearchMapping(
    booking: Booking,
    reference: String,
    rooms: List<BookingRoom>,
): StubMapping {
    val searchReference = if (reference.getOrNull(3) == 'R') "$reference-%" else reference
    return StubMapping(
        request =
            RequestPattern(
                method = "GET",
                urlPath = "/rsv/v1/reservations",
                queryParameters =
                    mapOf(
                        "externalReferenceIds" to StringValuePattern(equalTo = searchReference),
                    ),
            ),
        response =
            jsonResponse(
                jsonBody = externalReferenceSearchResponse(booking, rooms, reference),
            ),
    )
}

private fun externalReferenceSearchResponse(
    booking: Booking,
    rooms: List<BookingRoom>,
    externalReference: String,
) = stubJsonObject(
    "reservations" to
        mapOf(
            "reservationInfo" to
                rooms.takeIf { it.isNotEmpty() }?.map { room ->
                    externalReferenceReservation(booking, room, externalReference)
                },
            "totalPages" to 1,
            "offset" to 0,
            "limit" to rooms.size,
            "hasMore" to false,
            "totalResults" to rooms.size,
        ),
    "links" to emptyList<Any>(),
)

private fun externalReferenceReservation(
    booking: Booking,
    room: BookingRoom,
    externalReference: String,
): Map<String, Any?> {
    val arrival =
        requireNotNull(booking.arrival) {
            "booking.arrival must be configured for external-reference search stubs"
        }
    val departure =
        requireNotNull(booking.departure) {
            "booking.departure must be configured for external-reference search stubs"
        }
    val reservationId =
        requireNotNull(room.reservationId) {
            "BookingRoom.reservationId must be configured for external-reference search stubs"
        }
    val rate =
        selectedRateOrNull(booking, room)
            ?: error(
                "booking.hotel.availableRates must contain a rate matching room $reservationId " +
                    "for external-reference search stubs",
            )
    val guest = room.guestProfile
    val total = rate.nightlyRate * ChronoUnit.DAYS.between(arrival, departure)

    return mapOf(
        "reservationIdList" to
            listOf(
                mapOf("id" to reservationId, "type" to "Reservation"),
                mapOf("id" to confirmationNumberFor(reservationId), "type" to "Confirmation"),
            ),
        "externalReferences" to
            listOf(
                mapOf(
                    "id" to externalReference,
                    "idContext" to booking.bookingReferenceIdContext,
                ),
            ),
        "roomStay" to
            mapOf(
                "arrivalDate" to arrival.toString(),
                "departureDate" to departure.toString(),
                "originalTimeSpan" to
                    mapOf(
                        "startDate" to arrival.toString(),
                        "endDate" to departure.toString(),
                    ),
                "adultCount" to room.adults,
                "childCount" to room.children,
                "roomClass" to "ST",
                "roomType" to room.roomType,
                "numberOfRooms" to 1,
                "ratePlanCode" to rate.ratePlan,
                "rateAmount" to
                    mapOf(
                        "amount" to total,
                        "currencyCode" to booking.hotel.currency,
                    ),
                "rateSuppressed" to false,
                "fixedRate" to true,
                "guarantee" to mapOf("guaranteeCode" to "NON"),
                "marketCode" to "DIRECT",
                "sourceCode" to room.sourceCode,
                "balance" to
                    mapOf(
                        "amount" to total - room.amountAlreadyPaid,
                        "currencyCode" to booking.hotel.currency,
                    ),
                "roomTypeCharged" to room.roomType,
                "roomNumberLocked" to false,
                "pseudoRoom" to false,
            ),
        "reservationGuest" to
            mapOf(
                "givenName" to (guest?.firstName ?: "TEMP"),
                "surname" to (guest?.lastName ?: "GUEST"),
                "language" to (guest?.language ?: "E"),
                "vip" to emptyMap<String, Any>(),
                "address" to mapOf("country" to emptyMap<String, Any>()),
                "anonymization" to emptyMap<String, Any>(),
                "accompanyGuests" to emptyList<Any>(),
                "guestRestricted" to false,
                "nameType" to "Guest",
                "id" to (guest?.profileId ?: "TEMP-$reservationId"),
                "type" to "Profile",
            ),
        "sharedGuests" to emptyList<Any>(),
        "attachedProfiles" to reservationContactProfile(room),
        "reservationPaymentMethod" to mapOf("paymentMethod" to "CA"),
        "reservationFolioWindows" to emptyList<Any>(),
        "sourceOfSale" to mapOf("sourceType" to "WEB", "sourceCode" to room.sourceCode),
        "hotelId" to booking.hotel.hotelId,
        "hotelName" to booking.hotel.name,
        "roomStayReservation" to true,
        "reservationStatus" to operaSummaryStatus(room.status),
        "computedReservationStatus" to operaSummaryStatus(room.status),
        "walkInIndicator" to false,
        "paymentMethod" to "CA",
        "preRegistered" to false,
        "openFolio" to false,
        "allowMobileCheckout" to false,
        "optedForCommunication" to false,
    )
}

private fun reservationContactProfile(room: BookingRoom): List<Map<String, Any?>> =
    room.guestProfile?.let { guest ->
        listOf(
            mapOf(
                "name" to "${guest.lastName}, ${guest.firstName}",
                "profileIdList" to listOf(mapOf("id" to guest.profileId, "type" to "Profile")),
                "reservationProfileType" to "ReservationContact",
            ),
        )
    } ?: emptyList()

private fun operaSummaryStatus(status: ReservationStatus): String =
    when (status) {
        ReservationStatus.ON_HOLD -> "Reserved"
        ReservationStatus.RESERVED -> "Reserved"
        ReservationStatus.CONFIRMED -> "Reserved"
        ReservationStatus.CHECKED_IN -> "InHouse"
        ReservationStatus.CHECKED_OUT -> "CheckedOut"
        ReservationStatus.CANCELLED -> "Cancelled"
        ReservationStatus.NO_SHOW -> "NoShow"
    }
