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
import uk.co.whitbread.integrationtests.testkit.model.Hotel
import uk.co.whitbread.integrationtests.testkit.model.Rate

const val OPERA_MULTI_ROOM_RATE_AVAILABILITY_STUB_ID = "booking.opera.multi-room-rate-availability"
private const val MULTI_ROOM_RATE_HOTEL_BATCH_SIZE = 10

/**
 * Models Opera's multi-room-rate availability search
 * (`POST /parext/v1/hotels/multiRoomRateAvailability`): per-room availability with rate
 * plans across the requested hotels.
 *
 * The adapter creates one Opera request per room group. It uses one request per room on the
 * `DISTR` channel. It splits hotels into batches of 10. This stub installs one mapping per
 * non-empty subset of configured PMS room types and hotel batch. It also installs one fallback
 * per batch for requests that contain no configured room type. Matchers inspect every room tag.
 *
 * **Tag-echo convention**: the adapter restores occupancy onto response rooms by matching
 * the response room's `tag` against the request room's `tag`, and a static mapping cannot
 * echo arbitrary tags. Journeys must therefore send each room's `tag` equal to its PMS
 * room type; the response emits `tag == roomType` so the match holds.
 *
 * Each mapping answers for hotels in its batch that have a rate for at least one selected room
 * type. It groups selected room types by Opera room class. It echoes each rate's `ratePlanSet`.
 */
fun multiRoomRateAvailability(booking: Booking): PlannedStub {
    val arrival = requireNotNull(booking.arrival) { "booking.arrival is required for multi-room-rate stubs" }
    val departure = requireNotNull(booking.departure) { "booking.departure is required for multi-room-rate stubs" }
    val pmsRoomTypes =
        booking.hotels
            .flatMap { hotel -> hotel.availableRates }
            .map(Rate::roomType)
            .distinct()
    require(pmsRoomTypes.isNotEmpty()) {
        "At least one hotel rate is required for multi-room-rate availability stubs"
    }
    val hotelBatches = booking.hotels.chunked(MULTI_ROOM_RATE_HOTEL_BATCH_SIZE)
    val roomTypeSubsets = pmsRoomTypes.nonEmptySubsets()

    return PlannedStub(
        id = OPERA_MULTI_ROOM_RATE_AVAILABILITY_STUB_ID,
        target = WireMockTarget.OPERA,
        mappings =
            roomTypeSubsets.flatMap { selectedRoomTypes ->
                hotelBatches.map { hotels ->
                    multiRoomRateMapping(
                        arrival = arrival.toString(),
                        departure = departure.toString(),
                        hotels = hotels,
                        selectedRoomTypes = selectedRoomTypes,
                        excludedRoomTypes = pmsRoomTypes.filterNot(selectedRoomTypes::contains),
                    )
                }
            } +
                hotelBatches.map { hotels ->
                    multiRoomRateMapping(
                        arrival = arrival.toString(),
                        departure = departure.toString(),
                        hotels = hotels,
                        selectedRoomTypes = emptyList(),
                        excludedRoomTypes = pmsRoomTypes,
                    )
                },
    )
}

private fun multiRoomRateMapping(
    arrival: String,
    departure: String,
    hotels: List<Hotel>,
    selectedRoomTypes: List<String>,
    excludedRoomTypes: List<String>,
): StubMapping =
    StubMapping(
        request =
            RequestPattern(
                method = "POST",
                urlPath = "/parext/v1/hotels/multiRoomRateAvailability",
                headers = hotelHeaders(hotels.first().hotelId),
                bodyPatterns =
                    listOf(
                        BodyPattern(matchesJsonPath = "$[?(@.arrivalDate == '$arrival')]"),
                        BodyPattern(matchesJsonPath = "$[?(@.departureDate == '$departure')]"),
                    ) +
                        hotelBatchBodyPatterns(hotels) +
                        selectedRoomTypes.map(::roomTagBodyPattern) +
                        excludedRoomTypes.map { roomType -> BodyPattern(not = roomTagBodyPattern(roomType)) },
            ),
        response =
            jsonResponse(
                jsonBody =
                    stubJsonObject(
                        "hotelAvailability" to hotels.mapNotNull { hotel -> hotelRoomTypeAvailability(hotel, selectedRoomTypes) },
                        "warnings" to emptyList<Any>(),
                    ),
            ),
    )

private fun roomTagBodyPattern(roomType: String): BodyPattern =
    BodyPattern(
        matchesJsonPath = "$.rooms[?(@.tag == ${jsonPathStringLiteral(roomType)})]",
    )

private fun hotelBatchBodyPatterns(hotels: List<Hotel>): List<BodyPattern> =
    listOf(BodyPattern(matchesJsonPath = "$[?(@.hotelIds.size() == ${hotels.size})]")) +
        hotels.map { hotel ->
            BodyPattern(
                matchesJsonPath = "$.hotelIds[?(@ == ${jsonPathStringLiteral(hotel.hotelId)})]",
            )
        }

private fun hotelRoomTypeAvailability(
    hotel: Hotel,
    selectedRoomTypes: List<String>,
): Map<String, Any?>? {
    val availableSelectedRoomTypes =
        selectedRoomTypes.filter { roomType -> hotel.availableRates.any { rate -> rate.roomType == roomType } }
    if (availableSelectedRoomTypes.isEmpty()) return null

    val roomTypesByClass = availableSelectedRoomTypes.groupBy { roomType -> hotel.roomClassFor(roomType) }

    return mapOf(
        "hotelId" to hotel.hotelId,
        "roomStays" to
            roomTypesByClass.map { (roomClass, roomTypes) ->
                mapOf(
                    "roomClass" to roomClass,
                    "roomTypes" to roomTypes.map { roomType -> hotelRoomType(hotel, roomType) },
                )
            },
    )
}

private fun Hotel.roomClassFor(roomType: String): String =
    availableRoomTypes
        .firstOrNull { configuredRoomType -> configuredRoomType.roomType == roomType }
        ?.roomClass ?: "ST"

private fun hotelRoomType(
    hotel: Hotel,
    pmsRoomType: String,
): Map<String, Any?> =
    mapOf(
        "tag" to pmsRoomType,
        "roomType" to pmsRoomType,
        "roomRates" to
            hotel.availableRates
                .filter { rate -> rate.roomType == pmsRoomType }
                .map { rate ->
                    mapOf(
                        "ratePlanCode" to rate.ratePlan,
                        "ratePlanSet" to rate.ratePlanSet,
                        "currencyCode" to hotel.currency,
                        "roomRateInfo" to
                            mapOf(
                                "priceInfo" to emptyList<Any>(),
                                "packages" to emptyList<Any>(),
                            ),
                    )
                },
    )

private fun <T> List<T>.nonEmptySubsets(): List<List<T>> =
    fold(listOf(emptyList<T>())) { subsets, item ->
        subsets + subsets.map { subset -> subset + item }
    }.filter { subset -> subset.isNotEmpty() }
