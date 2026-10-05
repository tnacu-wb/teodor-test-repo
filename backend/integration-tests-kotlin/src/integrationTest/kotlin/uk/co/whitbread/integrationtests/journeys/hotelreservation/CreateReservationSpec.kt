package uk.co.whitbread.integrationtests.journeys.hotelreservation

import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.hotelreservation.HotelReservationApi
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.CreateReservationBookingChannel
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.CreateReservationRequest
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.CreateReservationRoomRateRequest
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.CreateReservationRoomRequest
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.featureflags.FeatureFlag
import uk.co.whitbread.integrationtests.testkit.featureflags.HotelReservationFeatureFlag
import uk.co.whitbread.integrationtests.testkit.featureflags.OhipFeatureFlag
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.Rate
import uk.co.whitbread.integrationtests.testkit.presets.Hotels
import java.time.LocalDate

private val arrival: LocalDate = LocalDate.now().plusDays(14)
private val departure: LocalDate = arrival.plusDays(1)

/**
 * Flags pinned on every create so the exercised path never depends on a deployed default:
 * DS payment method off (Opera create stubs match CA), city tax off (no content-entity fixtures
 * in these scenarios), occupancy supplement off (the dedicated scenario pins it on explicitly).
 */
private val createReservationFlagPins =
    mapOf<FeatureFlag, Boolean>(
        OhipFeatureFlag.SET_DEFAULT_PAYMENT_METHOD_DS to false,
        HotelReservationFeatureFlag.CITY_TAX_UK to false,
        HotelReservationFeatureFlag.APPLY_OCCUPANCY_SUPPLEMENT to false,
    )

class CreateReservationSpec :
    JourneySpec(
        "hotel reservations can be created through the reservation entity service",
        {
            val hotelReservationApi = HotelReservationApi()

            scenario("POST /v1/reservations creates a booking with one room on the SEMIFLEX rate") {
                val booking = singleRoomBooking()
                val request = singleRoomRequest(booking)

                installFor(booking)

                val result =
                    hotelReservationApi.createReservation(
                        request = request,
                        testId = testId,
                        featureFlagOverrides = createReservationFlagPins,
                    )

                result.attachEvidence("Create One-Room SEMIFLEX Booking")

                expect("returns the created one-room booking") {
                    result.response.status.value shouldBe 201
                    result.body.hotelId shouldBe booking.hotel.hotelId
                    result.body.basketReference.isNotBlank() shouldBe true
                    result.body.reservations shouldHaveSize 1
                    result.body.reservations
                        .single()
                        .createDateTime
                        .shouldNotBeNull()
                        .isNotBlank() shouldBe true
                }
            }

            scenario("POST /v1/reservations creates a booking with two rooms on the SEMIFLEX rate") {
                val booking = twoRoomBooking()
                val request = twoRoomRequest(booking)

                installFor(booking)

                val result =
                    hotelReservationApi.createReservation(
                        request = request,
                        testId = testId,
                        featureFlagOverrides = createReservationFlagPins,
                    )

                result.attachEvidence("Create Two-Room SEMIFLEX Booking")

                expect("returns the created two-room booking") {
                    result.response.status.value shouldBe 201
                    result.body.hotelId shouldBe booking.hotel.hotelId
                    result.body.basketReference.isNotBlank() shouldBe true
                    result.body.reservations shouldHaveSize booking.rooms.size
                    result.body.reservations.forEach { reservation ->
                        reservation.createDateTime
                            .shouldNotBeNull()
                            .isNotBlank() shouldBe true
                    }
                }
            }

            scenario("an enabled occupancy supplement flag calculates the two-room total") {
                val booking = twoRoomBooking()
                val request = twoRoomRequest(booking)

                installFor(booking)

                val result =
                    hotelReservationApi.createReservation(
                        request = request,
                        testId = testId,
                        featureFlagOverrides =
                            createReservationFlagPins +
                                mapOf(HotelReservationFeatureFlag.APPLY_OCCUPANCY_SUPPLEMENT to true),
                    )

                result.attachEvidence("Create Two-Room Booking With Occupancy Supplement Override")

                expect("returns the total from the created Opera reservations") {
                    result.response.status.value shouldBe 201
                    result.body.reservations shouldHaveSize booking.rooms.size
                    result.body.totalCost shouldBe 118.0
                }
            }
        },
    )

private fun singleRoomBooking(): Booking {
    val rate =
        Rate(
            ratePlan = "SEMIFLEX",
            roomType = "VPPDBL",
            adults = 1,
        )

    return Booking(
        hotels = listOf(Hotels.HEAPTI.copy(availableRates = listOf(rate))),
        arrival = arrival,
        departure = departure,
        rooms =
            listOf(
                BookingRoom(
                    reservationId = "6003001",
                    roomType = rate.roomType,
                    adults = rate.adults,
                    children = rate.children,
                ),
            ),
    )
}

private fun singleRoomRequest(booking: Booking): CreateReservationRequest =
    createReservationRequest(
        booking = booking,
        bookingFlowId = "single-room-create",
    )

private fun twoRoomBooking(): Booking {
    val rate =
        Rate(
            ratePlan = "SEMIFLEX",
            roomType = "VPPDBL",
            adults = 1,
        )

    return Booking(
        hotels = listOf(Hotels.HEAPTI.copy(availableRates = listOf(rate))),
        arrival = arrival,
        departure = departure,
        rooms =
            listOf(
                BookingRoom(
                    reservationId = "6003002",
                    roomType = rate.roomType,
                    adults = rate.adults,
                    children = rate.children,
                ),
                BookingRoom(
                    reservationId = "6003003",
                    roomType = rate.roomType,
                    adults = rate.adults,
                    children = rate.children,
                ),
            ),
    )
}

private fun twoRoomRequest(booking: Booking): CreateReservationRequest =
    createReservationRequest(
        booking = booking,
        bookingFlowId = "two-room-create",
    )

private fun createReservationRequest(
    booking: Booking,
    bookingFlowId: String,
): CreateReservationRequest {
    val rate = booking.hotel.availableRates.single()
    val arrivalDate = requireNotNull(booking.arrival).toString()
    val departureDate = requireNotNull(booking.departure).toString()

    return CreateReservationRequest(
        reservations =
            booking.rooms.map { room ->
                CreateReservationRoomRequest(
                    hotelId = booking.hotel.hotelId,
                    arrival = arrivalDate,
                    departure = departureDate,
                    adultsNumber = requireNotNull(room.adults),
                    childrenNumber = room.children,
                    cotRequired = false,
                    roomRates =
                        CreateReservationRoomRateRequest(
                            ratePlanCode = rate.ratePlan,
                            pmsRoomType = requireNotNull(room.roomType),
                            specialRequests = listOf("SING"),
                            startDate = arrivalDate,
                            endDate = departureDate,
                        ),
                )
            },
        bookingChannel =
            CreateReservationBookingChannel(
                channel = "PI",
                subchannel = "WEB",
                language = "EN",
            ),
        bookingFlowId = bookingFlowId,
    )
}
