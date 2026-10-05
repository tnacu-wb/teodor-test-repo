package uk.co.whitbread.integrationtests.journeys.hotelreservation

import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.hotelreservation.HotelReservationApi
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.UpdateRoomTypeRequest
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_AVAILABILITY_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_RESERVATION_GET_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_RESERVATION_PUT_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.getReservationBadRequest
import uk.co.whitbread.integrationtests.stubs.opera.custom.getReservationEmpty
import uk.co.whitbread.integrationtests.stubs.opera.custom.hotelAvailabilityFailure
import uk.co.whitbread.integrationtests.stubs.opera.custom.hotelAvailabilityWithMismatchedRoomType
import uk.co.whitbread.integrationtests.stubs.opera.custom.putReservationFailure
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.mocks.OperaEndpoint
import uk.co.whitbread.integrationtests.testkit.mocks.Upstream
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.Rate
import uk.co.whitbread.integrationtests.testkit.model.ReservationStatus
import uk.co.whitbread.integrationtests.testkit.presets.Hotels
import java.time.LocalDate

private const val RATE_CODE = "SEMIFLEX"
private const val CURRENT_ROOM_TYPE = "DOUBLE"
private const val LOW_DOUBLE_ROOM_TYPE = "LOWDBL"
private const val TWIN_ROOM_TYPE = "TWINRM"

/**
 * Proves `PUT /v1/reservations/roomTypeUpdate`: reservation reads are priced by grouped
 * room-type availability, including split long stays, before one final Opera update. It also
 * proves the missing-data, mismatched-price, and downstream-failure boundaries.
 *
 * No endpoint-specific feature flag gates this path. Opera transport-auth flags are
 * environment-pinned OFF and are not request overrides.
 *
 * Flow: backend/integration-tests-kotlin/flows/hotel-reservation-entity-service/UpdateRoomType.md
 */
class UpdateRoomTypeSpec :
    JourneySpec(
        "Hotel Reservation Entity updates reservation room types",
        {
            val hotelReservationApi = HotelReservationApi()

            scenario("distinct target room types are priced before one reservation update") {
                val booking =
                    roomTypeUpdateBooking(
                        reservationIds = listOf("6127401", "6127402", "6127403"),
                        roomTypesAfterUpdate =
                            listOf(LOW_DOUBLE_ROOM_TYPE, LOW_DOUBLE_ROOM_TYPE, TWIN_ROOM_TYPE),
                    )
                val request = roomTypeUpdateRequest(booking)

                installFor(booking)

                val result = hotelReservationApi.updateRoomType(request, testId)

                result.attachEvidence("Update Room Type Grouped Availability")

                expect("returns the request basket reference") {
                    result.response.status.value shouldBe 200
                    result.body.basketReference shouldBe request.basketReferenceId
                }

                expect("reads three reservations, prices two room types, and updates once") {
                    callCount(Upstream.OPERA) shouldBe 6
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 3
                    callCount(OperaEndpoint.GET_AVAILABILITY) shouldBe 2
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a long stay is priced through split availability intervals") {
                val booking =
                    roomTypeUpdateBooking(
                        reservationIds = listOf("6127404"),
                        stayNights = 100,
                    )
                val request = roomTypeUpdateRequest(booking)

                installFor(booking)

                val result = hotelReservationApi.updateRoomType(request, testId)

                result.attachEvidence("Update Room Type Split Availability")

                expect("returns the request basket reference") {
                    result.response.status.value shouldBe 200
                    result.body.basketReference shouldBe request.basketReferenceId
                }

                expect("prices both date intervals before the single update") {
                    callCount(Upstream.OPERA) shouldBe 4
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_AVAILABILITY) shouldBe 2
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a duplicated reservation id is rejected before availability") {
                val booking = roomTypeUpdateBooking(reservationIds = listOf("6127405"))
                val reservationId = requireNotNull(booking.room.reservationId)
                val request =
                    roomTypeUpdateRequest(
                        booking = booking,
                        reservationIds = listOf(reservationId, reservationId),
                        roomTypes = listOf(LOW_DOUBLE_ROOM_TYPE, LOW_DOUBLE_ROOM_TYPE),
                    )

                installFor(booking)

                val result = hotelReservationApi.updateRoomType(request, testId)

                result.attachEvidence("Update Room Type Duplicate Reservation")

                expect("returns the wrong-reservation error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 26
                }

                expect("deduplicates the read and stops before pricing or updating") {
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_AVAILABILITY) shouldBe 0
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("missing Opera reservation content is rejected before availability") {
                val booking = roomTypeUpdateBooking(reservationIds = listOf("6127406"))
                val reservationId = requireNotNull(booking.room.reservationId)
                val request = roomTypeUpdateRequest(booking)

                installFor(booking, excluded = setOf(OPERA_RESERVATION_GET_STUB_ID))
                installStub(getReservationEmpty(booking.hotel.hotelId, reservationId))

                val result = hotelReservationApi.updateRoomType(request, testId)

                result.attachEvidence("Update Room Type Missing Reservation")

                expect("returns the wrong-reservation error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 26
                }

                expect("attempts one read and stops before pricing or updating") {
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_AVAILABILITY) shouldBe 0
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an Opera reservation-read rejection stops the room-type update") {
                val booking = roomTypeUpdateBooking(reservationIds = listOf("6127407"))
                val reservationId = requireNotNull(booking.room.reservationId)
                val request = roomTypeUpdateRequest(booking)

                installFor(booking, excluded = setOf(OPERA_RESERVATION_GET_STUB_ID))
                installStub(getReservationBadRequest(booking.hotel.hotelId, reservationId))

                val result = hotelReservationApi.updateRoomType(request, testId)

                result.attachEvidence("Update Room Type Opera Read Error")

                expect("returns the mapped reservation-read error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 960
                }

                expect("attempts one read and stops before pricing or updating") {
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_AVAILABILITY) shouldBe 0
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an Opera availability rejection stops the room-type update") {
                val booking = roomTypeUpdateBooking(reservationIds = listOf("6127408"))
                val request = roomTypeUpdateRequest(booking)

                installFor(booking, excluded = setOf(OPERA_AVAILABILITY_STUB_ID))
                installStub(hotelAvailabilityFailure(booking))

                val result = hotelReservationApi.updateRoomType(request, testId)

                result.attachEvidence("Update Room Type Opera Availability Error")

                expect("returns the mapped availability error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 913
                }

                expect("reads and prices once without updating the reservation") {
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_AVAILABILITY) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an unavailable room type is rejected before the reservation update") {
                val booking =
                    roomTypeUpdateBooking(
                        reservationIds = listOf("6127409"),
                        roomTypesAfterUpdate = listOf("SINGLE"),
                    )
                val request = roomTypeUpdateRequest(booking)

                installFor(booking)

                val result = hotelReservationApi.updateRoomType(request, testId)

                result.attachEvidence("Update Room Type Empty Opera Rates")

                expect("returns the wrong-Opera-price error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 27
                }

                expect("reads and prices once without updating the reservation") {
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_AVAILABILITY) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("insufficient room inventory is rejected before the reservation update") {
                val booking =
                    roomTypeUpdateBooking(
                        reservationIds = listOf("6127412", "6127413"),
                        roomTypeCapacities = mapOf(LOW_DOUBLE_ROOM_TYPE to 1),
                    )
                val request = roomTypeUpdateRequest(booking)

                installFor(booking)

                val result = hotelReservationApi.updateRoomType(request, testId)

                result.attachEvidence("Update Room Type Insufficient Inventory")

                expect("returns the wrong-Opera-price error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 27
                }

                expect("reads both reservations and prices once without updating") {
                    callCount(Upstream.OPERA) shouldBe 3
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 2
                    callCount(OperaEndpoint.GET_AVAILABILITY) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a mismatched Opera room type is rejected before the reservation update") {
                val booking = roomTypeUpdateBooking(reservationIds = listOf("6127410"))
                val request = roomTypeUpdateRequest(booking)

                installFor(booking, excluded = setOf(OPERA_AVAILABILITY_STUB_ID))
                installStub(
                    hotelAvailabilityWithMismatchedRoomType(
                        booking = booking,
                        requestedRoomType = LOW_DOUBLE_ROOM_TYPE,
                        reportedRoomType = TWIN_ROOM_TYPE,
                    ),
                )

                val result = hotelReservationApi.updateRoomType(request, testId)

                result.attachEvidence("Update Room Type Mismatched Opera Rate")

                expect("returns the price-breakdown mismatch error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 42
                }

                expect("reads and prices once without updating the reservation") {
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_AVAILABILITY) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an Opera reservation-update rejection is surfaced without retry") {
                val booking = roomTypeUpdateBooking(reservationIds = listOf("6127411"))
                val request = roomTypeUpdateRequest(booking)

                installFor(booking, excluded = setOf(OPERA_RESERVATION_PUT_STUB_ID))
                installStub(putReservationFailure(booking))

                val result = hotelReservationApi.updateRoomType(request, testId)

                result.attachEvidence("Update Room Type Opera Update Error")

                expect("returns the mapped reservation-update error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 958
                }

                expect("reads and prices before attempting one reservation update") {
                    callCount(Upstream.OPERA) shouldBe 3
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_AVAILABILITY) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
        },
    )

private fun roomTypeUpdateBooking(
    reservationIds: List<String>,
    roomTypesAfterUpdate: List<String> = List(reservationIds.size) { LOW_DOUBLE_ROOM_TYPE },
    roomTypeCapacities: Map<String, Int> = emptyMap(),
    stayNights: Long = 2,
): Booking {
    require(roomTypesAfterUpdate.size == reservationIds.size) {
        "roomTypesAfterUpdate must contain one target for each reservation"
    }
    val arrival = LocalDate.now().plusDays(14)
    val rates =
        listOf(
            Rate(ratePlan = RATE_CODE, roomType = CURRENT_ROOM_TYPE, adults = 2, nightlyRate = 59.0),
            Rate(ratePlan = RATE_CODE, roomType = LOW_DOUBLE_ROOM_TYPE, adults = 2, nightlyRate = 79.0),
            Rate(ratePlan = RATE_CODE, roomType = TWIN_ROOM_TYPE, adults = 2, nightlyRate = 89.0),
        )

    val hotel = Hotels.HEAPTI

    return Booking(
        hotels =
            listOf(
                hotel.copy(
                    availableRates = rates,
                    availableRoomTypes =
                        hotel.availableRoomTypes.map { roomType ->
                            roomTypeCapacities[roomType.roomType]
                                ?.let { capacity -> roomType.copy(numberOfRooms = capacity) }
                                ?: roomType
                        },
                ),
            ),
        arrival = arrival,
        departure = arrival.plusDays(stayNights),
        rooms =
            reservationIds.zip(roomTypesAfterUpdate).map { (reservationId, roomTypeAfterUpdate) ->
                BookingRoom(
                    reservationId = reservationId,
                    roomType = CURRENT_ROOM_TYPE,
                    roomTypeAfterUpdate = roomTypeAfterUpdate,
                    adults = 2,
                    children = 0,
                    status = ReservationStatus.RESERVED,
                    sourceCode = "WEB",
                )
            },
    )
}

private fun roomTypeUpdateRequest(
    booking: Booking,
    roomTypes: List<String> = booking.rooms.map { room -> requireNotNull(room.roomTypeAfterUpdate) },
    reservationIds: List<String> = booking.rooms.map { room -> requireNotNull(room.reservationId) },
): UpdateRoomTypeRequest =
    UpdateRoomTypeRequest(
        basketReferenceId = "BASKET-ROOM-TYPE-${reservationIds.first()}",
        reservationIds = reservationIds,
        hotelId = booking.hotel.hotelId,
        rateCode = RATE_CODE,
        roomTypes = roomTypes,
        startDate = requireNotNull(booking.arrival).toString(),
        endDate = requireNotNull(booking.departure).toString(),
        currency = booking.hotel.currency,
        adultsNumber = List(reservationIds.size) { 2 },
        childrenNumber = List(reservationIds.size) { 0 },
    )
