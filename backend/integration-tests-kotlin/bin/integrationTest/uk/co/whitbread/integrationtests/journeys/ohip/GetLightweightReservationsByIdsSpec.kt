package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.stubs.opera.custom.getReservationBadRequest
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.GuestProfile
import uk.co.whitbread.integrationtests.testkit.model.Rate
import uk.co.whitbread.integrationtests.testkit.model.ReservationStatus
import uk.co.whitbread.integrationtests.testkit.model.SelectedPackage
import uk.co.whitbread.integrationtests.testkit.presets.Hotels
import java.time.LocalDate

private val arrival: LocalDate = LocalDate.now().plusDays(14)
private val departure: LocalDate = arrival.plusDays(2)

class GetLightweightReservationsByIdsSpec :
    JourneySpec(
        "HEAPTI lightweight reservations can be fetched by id",
        {
            val ohipApi = OhipApi()

            scenario("GET /ohip/v1/reservations/ids returns one lightweight reservation") {
                val breakfastPackage =
                    SelectedPackage(
                        code = "BFADBF",
                    )
                val booking =
                    Booking(
                        hotels =
                            listOf(
                                Hotels.HEAPTI.copy(
                                    availableRates =
                                        listOf(
                                            Rate(ratePlan = "SEMIFLEX", roomType = "LOWDBL", adults = 2),
                                        ),
                                ),
                            ),
                        arrival = arrival,
                        departure = departure,
                        rooms =
                            listOf(
                                BookingRoom(
                                    reservationId = "6002001",
                                    roomType = "LOWDBL",
                                    adults = 2,
                                    status = ReservationStatus.RESERVED,
                                    selectedPackages = listOf(breakfastPackage),
                                    guestProfile =
                                        GuestProfile(
                                            profileId = "PROF-6201",
                                            firstName = "Amelia",
                                            lastName = "Wright",
                                            email = "amelia.wright@test.com",
                                            phone = "+447700900001",
                                            addressLine = "1 High Street",
                                            city = "London",
                                            postcode = "SW1A 1AA",
                                        ),
                                ),
                            ),
                    )
                val room = booking.room

                installFor(booking)

                val result =
                    ohipApi.getLightweightReservationsByIds(
                        reservationId = room.reservationId!!,
                        hotelId = booking.hotel.hotelId,
                        testId = testId,
                    )

                result.attachEvidence("Get Lightweight Reservations By Ids")

                expect("returns lightweight reservation details") {
                    result.response.status.value shouldBe 200
                    result.body.reservationByIdList shouldHaveSize 1
                }

                val reservation = result.body.reservationByIdList.first()

                expect("reservation matches booking fixture") {
                    reservation.reservationId shouldBe room.reservationId
                    reservation.hotelId shouldBe booking.hotel.hotelId
                    reservation.checkInTime shouldBe "15:00"
                    reservation.checkOutTime shouldBe "12:00"
                    reservation.email shouldBe room.guestProfile?.email
                    reservation.reservationPackageList shouldHaveSize 1
                    val reservationPackage = reservation.reservationPackageList.first()
                    reservationPackage.packageCode shouldBe breakfastPackage.code
                    reservationPackage.totalQuantity shouldBe 1
                    reservationPackage.packageGroup shouldBe "ADDON"
                    reservationPackage.startDate shouldBe booking.arrival.toString()
                    reservationPackage.endDate shouldBe booking.departure.toString()
                }
            }

            scenario("GET /ohip/v1/reservations/ids returns multiple lightweight reservations") {
                val firstRoom =
                    BookingRoom(
                        reservationId = "6002002",
                        roomType = "LOWDBL",
                        adults = 2,
                        status = ReservationStatus.RESERVED,
                    )
                val secondRoom =
                    BookingRoom(
                        reservationId = "6002003",
                        roomType = "ZPLDBL",
                        adults = 1,
                        status = ReservationStatus.RESERVED,
                    )
                val booking =
                    Booking(
                        hotels =
                            listOf(
                                Hotels.HEAPTI.copy(
                                    availableRates =
                                        listOf(
                                            Rate(ratePlan = "SEMIFLEX", roomType = "LOWDBL", adults = 2),
                                            Rate(ratePlan = "BAR", roomType = "ZPLDBL", adults = 1),
                                        ),
                                ),
                            ),
                        arrival = arrival,
                        departure = departure,
                        rooms = listOf(firstRoom, secondRoom),
                    )

                installFor(booking)

                val result =
                    ohipApi.getLightweightReservationsByIds(
                        reservationIds = listOf(firstRoom.reservationId!!, secondRoom.reservationId!!),
                        hotelId = booking.hotel.hotelId,
                        testId = testId,
                    )

                result.attachEvidence("Get Lightweight Reservations By Ids")

                expect("returns all requested reservation ids") {
                    result.response.status.value shouldBe 200
                    result.body.reservationByIdList shouldHaveSize 2
                    result.body.reservationByIdList
                        .map { it.reservationId }
                        .toSet() shouldBe
                        setOf(firstRoom.reservationId, secondRoom.reservationId)
                    result.body.reservationByIdList.all { it.hotelId == booking.hotel.hotelId } shouldBe true
                }
            }

            scenario("GET /ohip/v1/reservations/ids maps Opera invalid reservation id error") {
                val hotelId = "HEAPTI"
                val reservationId = "asd123"

                installStub(getReservationBadRequest(hotelId = hotelId, reservationId = reservationId))

                val result =
                    ohipApi.getLightweightReservationsByIds(
                        reservationId = reservationId,
                        hotelId = hotelId,
                        testId = testId,
                    )

                result.attachEvidence("Get Lightweight Reservations By Ids Invalid Id")

                expect("returns the adapter error for an Opera bad request") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 960
                    result.errorBody?.debugMessage shouldBe
                        "Error while trying to get reservations by ids for hotelId=HEAPTI and asd123 ids"
                    result.errorBody?.globalErrTextTemplate shouldBe "internal.server.exception"
                }
            }
        },
    )
