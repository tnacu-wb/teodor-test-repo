package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_EXTERNAL_REFERENCE_SEARCH_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_PROFILE_GET_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_RESERVATION_AMOUNTS_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.externalReservationAmountsFailure
import uk.co.whitbread.integrationtests.stubs.opera.custom.externalReservationProfileFailure
import uk.co.whitbread.integrationtests.stubs.opera.custom.externalReservationSearchUnavailable
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.mocks.OperaEndpoint
import uk.co.whitbread.integrationtests.testkit.mocks.Upstream
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.GuestProfile
import uk.co.whitbread.integrationtests.testkit.model.Rate
import uk.co.whitbread.integrationtests.testkit.model.ReservationStatus
import uk.co.whitbread.integrationtests.testkit.presets.Hotels
import java.time.LocalDate

class GetReservationsByExternalIdSpec :
    JourneySpec(
        "Opera reservations can be found by external reference",
        {
            val ohipApi = OhipApi()

            scenario("a migrated booking reference returns its enriched Opera reservation") {
                val booking = externalReferenceBooking()
                val room = booking.room
                val guest = requireNotNull(room.guestProfile)

                installFor(booking)

                val result =
                    ohipApi.getReservationsByExternalId(
                        externalReferenceId = requireNotNull(booking.bookingReference),
                        testId = testId,
                    )

                result.attachEvidence("Get Reservation By External Reference")

                expect("returns the matching reservation with its reference and billing details") {
                    result.response.status.value shouldBe 200
                    val reservations =
                        result.body.reservationsDetailsResponse
                            ?.reservations
                            .shouldNotBeNull()
                    val reservationInfo = reservations.reservationInfo.shouldNotBeNull()
                    reservationInfo shouldHaveSize 1
                    val reservation = reservationInfo.single()
                    reservation.reservationIdList
                        ?.single { it.type == "Reservation" }
                        ?.id shouldBe room.reservationId
                    reservation.externalReferences
                        ?.single { it.id == booking.bookingReference }
                        ?.idContext shouldBe booking.bookingReferenceIdContext
                    reservation.hotelId shouldBe booking.hotel.hotelId
                    result.body.billing?.email shouldBe guest.email
                    result.body.currencyCode shouldBe booking.hotel.currency
                }

                expect("searches Opera and enriches the reservation without other upstreams") {
                    // Three Opera calls: external-reference search, ReservationContact profile,
                    // and reservation amounts.
                    callCount(Upstream.OPERA) shouldBe 3
                    callCount(OperaEndpoint.GET_EXTERNAL_RESERVATIONS) shouldBe 1
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a fourth-character-R reference returns the reservation found by Opera wildcard search") {
                val booking = externalReferenceBooking(bookingReference = "BARREL7432", reservationId = "6004302")
                val room = booking.room

                installFor(booking)

                val result =
                    ohipApi.getReservationsByExternalId(
                        externalReferenceId = requireNotNull(booking.bookingReference),
                        testId = testId,
                    )

                result.attachEvidence("Get Reservation By Wildcard External Reference")

                expect("returns the matching reservation with the original external reference") {
                    result.response.status.value shouldBe 200
                    val reservation =
                        result.body.reservationsDetailsResponse
                            ?.reservations
                            ?.reservationInfo
                            .shouldNotBeNull()
                            .single()
                    reservation.reservationIdList
                        ?.single { it.type == "Reservation" }
                        ?.id shouldBe room.reservationId
                    reservation.externalReferences
                        ?.single()
                        ?.id shouldBe booking.bookingReference
                }

                expect("searches Opera using the wildcard branch and enriches the result") {
                    // Three Opera calls: wildcard external-reference search, ReservationContact
                    // profile, and reservation amounts.
                    callCount(Upstream.OPERA) shouldBe 3
                    callCount(OperaEndpoint.GET_EXTERNAL_RESERVATIONS) shouldBe 1
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("two reservations sharing one booker return one billing identity") {
                val firstGuest = externalGuest("PROF-6433")
                val booking =
                    externalReferenceBooking(
                        bookingReference = "BART7433",
                        reservationId = "6004303",
                        guest = firstGuest,
                    ).let { singleRoomBooking ->
                        singleRoomBooking.copy(
                            rooms =
                                listOf(
                                    singleRoomBooking.room,
                                    singleRoomBooking.room.copy(reservationId = "6004304"),
                                ),
                        )
                    }

                installFor(booking)

                val result =
                    ohipApi.getReservationsByExternalId(
                        externalReferenceId = requireNotNull(booking.bookingReference),
                        testId = testId,
                    )

                result.attachEvidence("Get Two Reservations With Shared Booker")

                expect("returns both reservations enriched with their shared booker") {
                    result.response.status.value shouldBe 200
                    val reservations =
                        result.body.reservationsDetailsResponse
                            ?.reservations
                            ?.reservationInfo
                            .shouldNotBeNull()
                    reservations shouldHaveSize 2
                    reservations
                        .mapNotNull { reservation ->
                            reservation.reservationIdList?.single { it.type == "Reservation" }?.id
                        }.toSet() shouldBe booking.rooms.mapNotNull { it.reservationId }.toSet()
                    result.body.billing?.email shouldBe firstGuest.email
                }

                expect("deduplicates the shared profile while loading both reservation amounts") {
                    // Four Opera calls: one external-reference search, one distinct
                    // ReservationContact profile, and two reservation-amount requests.
                    callCount(Upstream.OPERA) shouldBe 4
                    callCount(OperaEndpoint.GET_EXTERNAL_RESERVATIONS) shouldBe 1
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe 2
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a reservation without a ReservationContact is returned without billing") {
                val booking =
                    externalReferenceBooking(
                        bookingReference = "BART7434",
                        reservationId = "6004305",
                        guest = null,
                    )

                installFor(booking)

                val result =
                    ohipApi.getReservationsByExternalId(
                        externalReferenceId = requireNotNull(booking.bookingReference),
                        testId = testId,
                    )

                result.attachEvidence("Get Reservation Without Reservation Contact")

                expect("returns the reservation without billing details") {
                    result.response.status.value shouldBe 200
                    result.body.reservationsDetailsResponse
                        ?.reservations
                        ?.reservationInfo
                        .shouldNotBeNull() shouldHaveSize 1
                    result.body.billing.shouldBeNull()
                    result.body.currencyCode shouldBe booking.hotel.currency
                }

                expect("skips profile enrichment but still loads reservation amounts") {
                    // Two Opera calls: external-reference search and reservation amounts.
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_EXTERNAL_RESERVATIONS) shouldBe 1
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an external reference with no matching reservations returns not found") {
                val booking = Booking(bookingReference = "UNKNOWN7435")

                installFor(booking)

                val result =
                    ohipApi.getReservationsByExternalId(
                        externalReferenceId = requireNotNull(booking.bookingReference),
                        testId = testId,
                    )

                result.attachEvidence("Get Reservation By Unknown External Reference")

                expect("returns not found with no response body") {
                    result.response.status.value shouldBe 404
                    result.bodyText shouldBe ""
                }

                expect("stops after the external-reference search") {
                    // One Opera call: external-reference search. No reservation exists to enrich.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_EXTERNAL_RESERVATIONS) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an Opera external-reference search failure is returned as an OHIP error") {
                val booking = externalReferenceBooking(bookingReference = "BART7436", reservationId = "6004306")

                // A downstream failure cannot coexist with the generic successful Opera search.
                installFor(booking, excluded = setOf(OPERA_EXTERNAL_REFERENCE_SEARCH_STUB_ID))
                installStub(externalReservationSearchUnavailable(booking, booking.rooms))

                val result =
                    ohipApi.getReservationsByExternalId(
                        externalReferenceId = requireNotNull(booking.bookingReference),
                        testId = testId,
                    )

                result.attachEvidence("Get Reservation External Search Failure")

                expect("maps the Opera search failure to the external-reference error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 959
                }

                expect("stops before enriching a reservation") {
                    // One Opera call: the failed external-reference search.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_EXTERNAL_RESERVATIONS) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an Opera ReservationContact profile failure is returned as an OHIP error") {
                val booking = externalReferenceBooking(bookingReference = "BART7437", reservationId = "6004307")
                val room = booking.room

                // A downstream failure cannot coexist with the generic successful profile read.
                installFor(booking, excluded = setOf(OPERA_PROFILE_GET_STUB_ID))
                installStub(externalReservationProfileFailure(room))

                val result =
                    ohipApi.getReservationsByExternalId(
                        externalReferenceId = requireNotNull(booking.bookingReference),
                        testId = testId,
                    )

                result.attachEvidence("Get Reservation Profile Enrichment Failure")

                expect("maps the Opera profile failure to the profile error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 912
                }

                expect("stops before loading reservation amounts") {
                    // Two Opera calls: external-reference search and the failed profile read.
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_EXTERNAL_RESERVATIONS) shouldBe 1
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an Opera reservation-amount failure is returned as an OHIP error") {
                val booking = externalReferenceBooking(bookingReference = "BART7438", reservationId = "6004308")
                val room = booking.room

                // A downstream failure cannot coexist with the generic successful amount read.
                installFor(booking, excluded = setOf(OPERA_RESERVATION_AMOUNTS_STUB_ID))
                installStub(externalReservationAmountsFailure(booking, room))

                val result =
                    ohipApi.getReservationsByExternalId(
                        externalReferenceId = requireNotNull(booking.bookingReference),
                        testId = testId,
                    )

                result.attachEvidence("Get Reservation Amount Enrichment Failure")

                expect("maps the Opera amount failure to the reservation-amount error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 946
                }

                expect("fails after search and profile enrichment") {
                    // Three Opera calls: external-reference search, ReservationContact profile,
                    // and the failed reservation-amount request.
                    callCount(Upstream.OPERA) shouldBe 3
                    callCount(OperaEndpoint.GET_EXTERNAL_RESERVATIONS) shouldBe 1
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
        },
    )

private fun externalReferenceBooking(
    bookingReference: String = "BART7431",
    reservationId: String = "6004301",
    guest: GuestProfile? = externalGuest("PROF-6431"),
): Booking {
    val arrival = LocalDate.now().plusDays(21)
    val rate = Rate(ratePlan = "SEMIFLEX", roomType = "LOWDBL", adults = 2)

    return Booking(
        hotels = listOf(Hotels.HEAPTI.copy(availableRates = listOf(rate))),
        arrival = arrival,
        departure = arrival.plusDays(2),
        rooms =
            listOf(
                BookingRoom(
                    reservationId = reservationId,
                    roomType = rate.roomType,
                    adults = rate.adults,
                    status = ReservationStatus.RESERVED,
                    sourceCode = "44",
                    guestProfile = guest,
                ),
            ),
        bookingReference = bookingReference,
        bookingReferenceIdContext = "BART_OHIP",
    )
}

private fun externalGuest(profileId: String): GuestProfile =
    GuestProfile(
        profileId = profileId,
        firstName = "Jamie",
        lastName = "Taylor",
        email = "jamie.taylor.external@test.com",
        phone = "+447700900031",
        addressLine = "31 High Street",
        city = "London",
        postcode = "SW1A 1AA",
    )
