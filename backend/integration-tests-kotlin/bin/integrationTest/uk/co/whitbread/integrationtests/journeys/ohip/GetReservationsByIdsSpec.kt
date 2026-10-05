package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_RESERVATION_GET_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.getReservationEmpty
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.featureflags.OhipFeatureFlag
import uk.co.whitbread.integrationtests.testkit.mocks.OperaEndpoint
import uk.co.whitbread.integrationtests.testkit.mocks.Upstream
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.GuestProfile
import uk.co.whitbread.integrationtests.testkit.model.Rate
import uk.co.whitbread.integrationtests.testkit.model.ReservationStatus
import uk.co.whitbread.integrationtests.testkit.presets.Hotels
import java.time.LocalDate

private val arrival: LocalDate = LocalDate.now().plusDays(14)
private val departure: LocalDate = arrival.plusDays(2)

class GetReservationsByIdsSpec :
    JourneySpec(
        "HEAPTI basket reservations can be fetched by id",
        {
            val ohipApi = OhipApi()

            scenario("a reserved room's basket lookup returns the reservation with its deposit policy code") {
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
                                    reservationId = "6003001",
                                    roomType = "LOWDBL",
                                    adults = 2,
                                    status = ReservationStatus.RESERVED,
                                    guestProfile =
                                        GuestProfile(
                                            profileId = "PROF-6301",
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
                    ohipApi.getReservationsByIds(
                        reservationId = room.reservationId!!,
                        hotelId = booking.hotel.hotelId,
                        testId = testId,
                        featureFlagOverrides = mapOf(OhipFeatureFlag.MOBILE_ACCEPTS_OTA_BOOKING to false),
                    )

                result.attachEvidence("Get Basket Reservations By Ids")

                expect("returns the reservation with the basket deposit policy code") {
                    result.response.status.value shouldBe 200
                    result.body.reservationByIdList shouldHaveSize 1
                    val reservation = result.body.reservationByIdList.first()
                    reservation.reservationId shouldBe room.reservationId
                    result.body.policyCode shouldBe "DEP"
                }

                expect("fetches the reservation, amounts, folios, profile, and hotel config from Opera and nothing else") {
                    // Five calls: reservation, rate-info amounts, folios, CRM profile, and hotel
                    // config. Hotel config is uncached only because the integration environment
                    // sets CACHE_TYPE=none for the service.
                    callCount(Upstream.OPERA) shouldBe 5
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe 1
                    callCount(OperaEndpoint.GET_FOLIOS) shouldBe 1
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.GET_HOTEL_CONFIGURATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a basket with mismatched deposit policies is accepted as an OTA booking when mobile accepts OTA bookings") {
                val firstRoom =
                    BookingRoom(
                        reservationId = "6003002",
                        roomType = "LOWDBL",
                        adults = 2,
                        status = ReservationStatus.RESERVED,
                        depositPolicyCode = "DEP",
                        guestProfile =
                            GuestProfile(
                                profileId = "PROF-6302",
                                firstName = "Amelia",
                                lastName = "Wright",
                                email = "amelia.wright@test.com",
                                phone = "+447700900001",
                                addressLine = "1 High Street",
                                city = "London",
                                postcode = "SW1A 1AA",
                            ),
                    )
                val secondRoom =
                    BookingRoom(
                        reservationId = "6003003",
                        roomType = "ZPLDBL",
                        adults = 1,
                        status = ReservationStatus.RESERVED,
                        depositPolicyCode = "PREPAY",
                        guestProfile =
                            GuestProfile(
                                profileId = "PROF-6303",
                                firstName = "Ben",
                                lastName = "Carter",
                                email = "ben.carter@test.com",
                                phone = "+447700900002",
                                addressLine = "2 High Street",
                                city = "London",
                                postcode = "SW1A 1AA",
                            ),
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
                    ohipApi.getReservationsByIds(
                        reservationIds = listOf(firstRoom.reservationId!!, secondRoom.reservationId!!),
                        hotelId = booking.hotel.hotelId,
                        testId = testId,
                        featureFlagOverrides = mapOf(OhipFeatureFlag.MOBILE_ACCEPTS_OTA_BOOKING to true),
                    )

                result.attachEvidence("Get Basket Reservations With Mismatched Policies")

                expect("accepts the mismatched basket and returns both reservations") {
                    result.response.status.value shouldBe 200
                    result.body.reservationByIdList shouldHaveSize 2
                    result.body.reservationByIdList
                        .map { it.reservationId }
                        .toSet() shouldBe setOf(firstRoom.reservationId, secondRoom.reservationId)
                    result.body.policyCode shouldBe firstRoom.depositPolicyCode
                }

                expect("fetches both reservations, amounts, folios, and profiles plus hotel config from Opera") {
                    // Nine calls: per reservation one lookup, one rate-info amounts, one folio,
                    // and one CRM profile, plus one uncached hotel config.
                    callCount(Upstream.OPERA) shouldBe 9
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 2
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe 2
                    callCount(OperaEndpoint.GET_FOLIOS) shouldBe 2
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 2
                    callCount(OperaEndpoint.GET_HOTEL_CONFIGURATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a basket with mismatched deposit policies is rejected when mobile does not accept OTA bookings") {
                val firstRoom =
                    BookingRoom(
                        reservationId = "6003004",
                        roomType = "LOWDBL",
                        adults = 2,
                        status = ReservationStatus.RESERVED,
                        depositPolicyCode = "DEP",
                        guestProfile =
                            GuestProfile(
                                profileId = "PROF-6304",
                                firstName = "Amelia",
                                lastName = "Wright",
                                email = "amelia.wright@test.com",
                                phone = "+447700900001",
                                addressLine = "1 High Street",
                                city = "London",
                                postcode = "SW1A 1AA",
                            ),
                    )
                val secondRoom =
                    BookingRoom(
                        reservationId = "6003005",
                        roomType = "ZPLDBL",
                        adults = 1,
                        status = ReservationStatus.RESERVED,
                        depositPolicyCode = "PREPAY",
                        guestProfile =
                            GuestProfile(
                                profileId = "PROF-6305",
                                firstName = "Ben",
                                lastName = "Carter",
                                email = "ben.carter@test.com",
                                phone = "+447700900002",
                                addressLine = "2 High Street",
                                city = "London",
                                postcode = "SW1A 1AA",
                            ),
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
                    ohipApi.getReservationsByIds(
                        reservationIds = listOf(firstRoom.reservationId!!, secondRoom.reservationId!!),
                        hotelId = booking.hotel.hotelId,
                        testId = testId,
                        featureFlagOverrides = mapOf(OhipFeatureFlag.MOBILE_ACCEPTS_OTA_BOOKING to false),
                    )

                result.attachEvidence("Get Basket Reservations Mismatched Policies Rejected")

                expect("rejects the basket with the policy-code-not-unique error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 19
                    // The service swaps the two message fields for this exception; the journey
                    // asserts the response as the caller actually receives it.
                    result.errorBody?.globalErrTextTemplate shouldBe "Policy code is not unique across reservations"
                }

                expect("rejects only after loading the full basket from Opera") {
                    // The policy check runs on the loaded reservations, so all nine calls of the
                    // accepted-basket scenario still happen before the rejection.
                    callCount(Upstream.OPERA) shouldBe 9
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 2
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe 2
                    callCount(OperaEndpoint.GET_FOLIOS) shouldBe 2
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 2
                    callCount(OperaEndpoint.GET_HOTEL_CONFIGURATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a reservation id Opera holds nothing for is reported as not found") {
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
                                    reservationId = "6003006",
                                    roomType = "LOWDBL",
                                    adults = 2,
                                    status = ReservationStatus.RESERVED,
                                ),
                            ),
                    )
                val room = booking.room

                installFor(booking, excluded = setOf(OPERA_RESERVATION_GET_STUB_ID))
                installStub(getReservationEmpty(hotelId = booking.hotel.hotelId, reservationId = room.reservationId!!))

                val result =
                    ohipApi.getReservationsByIds(
                        reservationId = room.reservationId,
                        hotelId = booking.hotel.hotelId,
                        testId = testId,
                        featureFlagOverrides = mapOf(OhipFeatureFlag.MOBILE_ACCEPTS_OTA_BOOKING to false),
                    )

                result.attachEvidence("Get Basket Reservations Not Found")

                expect("returns not found for the unknown reservation") {
                    result.response.status.value shouldBe 404
                    result.errorBody?.errCode shouldBe 16
                    result.errorBody?.debugMessage shouldBe "Could not find Opera reservation"
                }

                expect("stops after the single reservation lookup") {
                    // The empty reservation list fails the basket before any amounts, folio,
                    // profile, or hotel-config call is made.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
        },
    )
