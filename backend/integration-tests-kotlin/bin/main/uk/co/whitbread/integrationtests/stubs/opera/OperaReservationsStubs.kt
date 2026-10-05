package uk.co.whitbread.integrationtests.stubs.opera

import uk.co.whitbread.integrationtests.framework.wiremock.WireMockTarget
import uk.co.whitbread.integrationtests.framework.wiremock.model.BodyPattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.RequestPattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StubMapping
import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.jsonPathStringLiteral
import uk.co.whitbread.integrationtests.stubs.jsonResponse
import uk.co.whitbread.integrationtests.stubs.stubJsonObject
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import java.util.UUID

private const val CREATE_RESERVATION_SCENARIO_PREFIX = "opera-create-reservation"
private const val SCENARIO_STARTED = "Started"
const val OPERA_CREATE_RESERVATION_STUB_ID = "booking.opera.create-reservation"

/**
 * Builds one Opera create-reservation mapping per requestable Booking room.
 *
 * Multi-room creates share an identical request pattern. They use a UUID-based WireMock Scenario
 * name so every build creates an independent sequence.
 */
fun createReservation(booking: Booking): PlannedStub {
    val rooms = booking.rooms.filter { it.roomType != null && it.adults != null }
    require(rooms.isNotEmpty()) {
        "booking.rooms must contain at least one room with roomType and adults"
    }
    require(rooms.all { it.reservationId != null }) {
        "every requestable booking room must include reservationId for create-reservation stubs"
    }

    val reservationIds = rooms.map { room -> room.reservationId!! }
    require(reservationIds.distinct().size == reservationIds.size) {
        "every requestable booking room must include a unique reservationId for create-reservation stubs"
    }

    val hotelId = booking.hotel.hotelId
    val scenarioName =
        if (rooms.size > 1) {
            "$CREATE_RESERVATION_SCENARIO_PREFIX:${UUID.randomUUID()}"
        } else {
            null
        }

    return PlannedStub(
        id = OPERA_CREATE_RESERVATION_STUB_ID,
        target = WireMockTarget.OPERA,
        mappings =
            rooms.mapIndexed { index, room ->
                val reservationId = reservationIds[index]
                StubMapping(
                    request =
                        RequestPattern(
                            method = "POST",
                            urlPath = "/rsv/v1/hotels/$hotelId/reservations",
                            bodyPatterns = createReservationBodyPatterns(booking, room),
                        ),
                    response =
                        jsonResponse(
                            jsonBody = stubJsonObject("links" to reservationLinks(hotelId, reservationId)),
                        ),
                    scenarioName = scenarioName,
                    requiredScenarioState =
                        scenarioName?.let {
                            if (index == 0) SCENARIO_STARTED else reservationCreatedState(index)
                        },
                    newScenarioState = scenarioName?.let { reservationCreatedState(index + 1) },
                )
            },
    )
}

/** Builds the request-body matchers for one Opera create-reservation call. */
private fun createReservationBodyPatterns(
    booking: Booking,
    room: BookingRoom,
): List<BodyPattern> =
    buildList {
        add(BodyPattern(matchesJsonPath = "$.reservations.reservation[*].reservationPaymentMethods[?(@.paymentMethod == 'CA')]"))
        add(BodyPattern(matchesJsonPath = "$.reservations.reservation[*].userDefinedFields.characterUDFs[?(@.name == 'UDFC09')]"))
        add(BodyPattern(matchesJsonPath = "$.reservations.reservation[*].userDefinedFields.characterUDFs[?(@.value == 'ANON')]"))
        add(
            BodyPattern(
                matchesJsonPath =
                    "$.reservations.reservation[?(@.hotelId == ${jsonPathStringLiteral(booking.hotel.hotelId)})]",
            ),
        )
        add(
            BodyPattern(
                matchesJsonPath =
                    "$.reservations.reservation[?(@.roomStay.roomRates[0].roomType == " +
                        "${jsonPathStringLiteral(requireNotNull(room.roomType))})]",
            ),
        )
        selectedRateOrNull(booking, room)?.let { rate ->
            add(
                BodyPattern(
                    matchesJsonPath =
                        "$.reservations.reservation[?(@.roomStay.roomRates[0].ratePlanCode == " +
                            "${jsonPathStringLiteral(rate.ratePlan)})]",
                ),
            )
        }
    }

/** Builds the links returned for exactly one created Opera reservation. */
private fun reservationLinks(
    hotelId: String,
    reservationId: String,
): List<Map<String, Any?>> {
    val confirmationNumber = confirmationNumberFor(reservationId)
    val hotelReservationsUrl =
        "https://whitbce1ua.whb.hospitality-api.eu-frankfurt-1.ocs.oc-test.com" +
            "/rsv/v1/hotels/$hotelId/reservations"
    val reservationUrl = "$hotelReservationsUrl/$reservationId"
    return listOf(
        reservationLink(reservationUrl, "GET", "getReservation"),
        reservationLink(reservationUrl, "PUT", "putReservation"),
        reservationLink("$reservationUrl/cancellations", "POST", "postCancelReservation"),
        reservationLink(
            "$hotelReservationsUrl?confirmationNumberList=$confirmationNumber",
            "GET",
            "getHotelReservations",
        ),
    )
}

/** Names the state reached after the given number of reservations have been created. */
private fun reservationCreatedState(createdReservations: Int): String = "reservation-$createdReservations-created"

/** Builds one create-reservation response link. */
private fun reservationLink(
    href: String,
    method: String,
    operationId: String,
): Map<String, Any?> =
    mapOf(
        "href" to href,
        "rel" to "self",
        "templated" to false,
        "method" to method,
        "operationId" to operationId,
    )
