package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_RESERVATION_GET_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.getReservationBadRequest
import uk.co.whitbread.integrationtests.stubs.opera.custom.getReservationEmpty
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.featureflags.FeatureFlag
import uk.co.whitbread.integrationtests.testkit.mocks.OperaEndpoint
import uk.co.whitbread.integrationtests.testkit.mocks.Upstream
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.GuestProfile
import uk.co.whitbread.integrationtests.testkit.model.Rate
import uk.co.whitbread.integrationtests.testkit.model.ReservationStatus
import uk.co.whitbread.integrationtests.testkit.model.SelectedPackage
import uk.co.whitbread.integrationtests.testkit.presets.Hotels
import java.time.LocalDate

// No feature flag gates this chain: the flow doc lists none, and the Opera token-service flag
// (release_ohip_use_token_service) is evaluated outside the request context, so it is unpinnable.
private val ancillariesFlagPins = mapOf<FeatureFlag, Boolean>()

/**
 * Proves `GET /ohip/v1/reservations/ancillaries`: the adapter reads each requested Opera
 * reservation once and maps its reservation packages into one `roomsSelections` entry per
 * reservation, in the requested id order, touching no other collaborator.
 *
 * Flow: backend/integration-tests-kotlin/flows/ohip-adapter-service/GetReservationAncillaries.md
 */
class GetReservationAncillariesSpec :
    JourneySpec(
        "OHIP adapter reads the ancillaries attached to a reservation",
        {
            val ohipApi = OhipApi()

            scenario("ancillaries for one reservation return its selected packages") {
                val booking = ancillariesBooking(reservationId = "6001001")
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking)

                val result =
                    ohipApi.getReservationAncillaries(
                        hotelId = booking.hotel.hotelId,
                        reservationIds = listOf(reservationId),
                        testId = testId,
                        featureFlagOverrides = ancillariesFlagPins,
                    )

                result.attachEvidence("Get Reservation Ancillaries")

                expect("returns the reservation's selected package") {
                    result.response.status.value shouldBe 200
                    result.body.roomsSelections?.size shouldBe 1
                    val packagesSelection =
                        result.body.roomsSelections
                            ?.first()
                            ?.packagesSelection
                    packagesSelection?.size shouldBe 1
                    packagesSelection?.first()?.id shouldBe BREAKFAST_PACKAGE_CODE
                    packagesSelection?.first()?.noSelections shouldBe BREAKFAST_PACKAGE_QUANTITY
                }

                expect("reads the reservation from Opera exactly once") {
                    // One Opera call: GET /rsv/v1/hotels/{hotelId}/reservations/{reservationId}.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    // /crm/v1/profiles/{profileId} — no profile enrichment on this path.
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 0
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe 0
                    callCount(OperaEndpoint.GET_FOLIOS) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("two reservations return one selection block each and a package-less reservation maps to a null selection") {
                val firstRoom =
                    BookingRoom(
                        reservationId = "6001002",
                        roomType = "LOWDBL",
                        adults = 2,
                        status = ReservationStatus.RESERVED,
                        selectedPackages =
                            listOf(
                                SelectedPackage(
                                    code = BREAKFAST_PACKAGE_CODE,
                                    quantity = BREAKFAST_PACKAGE_QUANTITY,
                                    unitPrice = 12.50,
                                ),
                            ),
                    )
                val secondRoom =
                    BookingRoom(
                        reservationId = "6001003",
                        roomType = "ZPLDBL",
                        adults = 1,
                        status = ReservationStatus.RESERVED,
                    )
                val arrival = LocalDate.now().plusDays(30)
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
                        departure = arrival.plusDays(2),
                        rooms = listOf(firstRoom, secondRoom),
                    )

                installFor(booking)

                val result =
                    ohipApi.getReservationAncillaries(
                        hotelId = booking.hotel.hotelId,
                        reservationIds =
                            listOf(
                                requireNotNull(firstRoom.reservationId),
                                requireNotNull(secondRoom.reservationId),
                            ),
                        testId = testId,
                        featureFlagOverrides = ancillariesFlagPins,
                    )

                result.attachEvidence("Get Reservation Ancillaries Two Reservations")

                expect("returns one selection block per requested reservation, null for the package-less one") {
                    result.response.status.value shouldBe 200
                    result.body.roomsSelections?.size shouldBe 2
                    val withPackages =
                        result.body.roomsSelections
                            ?.get(0)
                            ?.packagesSelection
                    withPackages?.size shouldBe 1
                    withPackages?.first()?.id shouldBe BREAKFAST_PACKAGE_CODE
                    result.body.roomsSelections
                        ?.get(1)
                        ?.packagesSelection shouldBe null
                }

                expect("reads each requested reservation from Opera once") {
                    // Two Opera calls: one GET /rsv/v1/hotels/{hotelId}/reservations/{reservationId} per id.
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 2
                    // /crm/v1/profiles/{profileId} — no profile enrichment on this path.
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 0
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe 0
                    callCount(OperaEndpoint.GET_FOLIOS) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            // The one branch mealInclusiveRate actually selects: a package Opera holds with nothing
            // scheduled against it (scheduleList[0].totalQuantity = 0) is kept by the true branch
            // and dropped by the default false branch
            // (ReservationResponseOhipMapper.toRoomsSelectionsModel).
            scenario("mealInclusiveRate true keeps a package with nothing scheduled against it") {
                val booking =
                    ancillariesBooking(
                        reservationId = "6001008",
                        additionalPackages =
                            listOf(
                                SelectedPackage(code = UNSCHEDULED_PACKAGE_CODE, quantity = 0),
                            ),
                    )
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking)

                val result =
                    ohipApi.getReservationAncillaries(
                        hotelId = booking.hotel.hotelId,
                        reservationIds = listOf(reservationId),
                        testId = testId,
                        mealInclusiveRate = true,
                        featureFlagOverrides = ancillariesFlagPins,
                    )

                result.attachEvidence("Get Reservation Ancillaries Unscheduled Package Kept")

                expect("returns the unscheduled package alongside the consumed one") {
                    result.response.status.value shouldBe 200
                    result.body.roomsSelections?.size shouldBe 1
                    val packagesSelection =
                        result.body.roomsSelections
                            ?.first()
                            ?.packagesSelection
                    packagesSelection?.size shouldBe 2
                    packagesSelection?.map { it.id } shouldBe
                        listOf(BREAKFAST_PACKAGE_CODE, UNSCHEDULED_PACKAGE_CODE)
                    packagesSelection?.last()?.noSelections shouldBe 0
                }

                expect("reads the reservation from Opera exactly once") {
                    // One Opera call: GET /rsv/v1/hotels/{hotelId}/reservations/{reservationId}.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    // /crm/v1/profiles/{profileId} — no profile enrichment on this path.
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 0
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe 0
                    callCount(OperaEndpoint.GET_FOLIOS) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("reservation Opera returns nothing for answers an empty selection list") {
                val booking = ancillariesBooking(reservationId = "6001005")
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking, excluded = setOf(OPERA_RESERVATION_GET_STUB_ID))
                installStub(getReservationEmpty(booking.hotel.hotelId, reservationId))

                val result =
                    ohipApi.getReservationAncillaries(
                        hotelId = booking.hotel.hotelId,
                        reservationIds = listOf(reservationId),
                        testId = testId,
                        featureFlagOverrides = ancillariesFlagPins,
                    )

                result.attachEvidence("Get Reservation Ancillaries Empty Opera Body")

                expect("returns 200 with no selections at all") {
                    // This endpoint has no reservation-not-found branch: an undecodable Opera body
                    // collapses to an empty list and a 200 with roomsSelections null.
                    result.response.status.value shouldBe 200
                    result.body.roomsSelections shouldBe null
                }

                expect("still reads the reservation from Opera exactly once") {
                    // One Opera call: GET /rsv/v1/hotels/{hotelId}/reservations/{reservationId}.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    // /crm/v1/profiles/{profileId} — no profile enrichment on this path.
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 0
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe 0
                    callCount(OperaEndpoint.GET_FOLIOS) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("reservation read rejected by Opera surfaces a server error") {
                val booking = ancillariesBooking(reservationId = "6001006")
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking, excluded = setOf(OPERA_RESERVATION_GET_STUB_ID))
                installStub(getReservationBadRequest(booking.hotel.hotelId, reservationId))

                val result =
                    ohipApi.getReservationAncillaries(
                        hotelId = booking.hotel.hotelId,
                        reservationIds = listOf(reservationId),
                        testId = testId,
                        featureFlagOverrides = ancillariesFlagPins,
                    )

                result.attachEvidence("Get Reservation Ancillaries Opera Rejection")

                expect("returns the mapped get-reservation error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 960
                }

                expect("attempts exactly one reservation read against Opera") {
                    // One Opera call: the rejected GET /rsv/v1/hotels/{hotelId}/reservations/{reservationId}.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    // /crm/v1/profiles/{profileId} — no profile enrichment on this path.
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 0
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe 0
                    callCount(OperaEndpoint.GET_FOLIOS) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("ancillaries read leaves the guest profile untouched") {
                val booking =
                    ancillariesBooking(
                        reservationId = "6001007",
                        guestProfile =
                            GuestProfile(
                                profileId = "PROF-6401",
                                firstName = "Amelia",
                                lastName = "Wright",
                                email = "amelia.wright@test.com",
                                phone = "+447700900001",
                                addressLine = "1 High Street",
                                city = "London",
                                postcode = "SW1A 1AA",
                            ),
                    )
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking)

                val result =
                    ohipApi.getReservationAncillaries(
                        hotelId = booking.hotel.hotelId,
                        reservationIds = listOf(reservationId),
                        testId = testId,
                        featureFlagOverrides = ancillariesFlagPins,
                    )

                result.attachEvidence("Get Reservation Ancillaries Profile Absence")

                expect("returns the same selected package as the profile-less booking") {
                    result.response.status.value shouldBe 200
                    result.body.roomsSelections?.size shouldBe 1
                    val packagesSelection =
                        result.body.roomsSelections
                            ?.first()
                            ?.packagesSelection
                    packagesSelection?.size shouldBe 1
                    packagesSelection?.first()?.id shouldBe BREAKFAST_PACKAGE_CODE
                }

                expect("never reads the installed guest profile mock") {
                    // One Opera call: GET /rsv/v1/hotels/{hotelId}/reservations/{reservationId}.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    // /crm/v1/profiles/{profileId} is stubbed by the guestProfile fact and never called.
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 0
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe 0
                    callCount(OperaEndpoint.GET_FOLIOS) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
        },
    )

private const val BREAKFAST_PACKAGE_CODE = "BRKFST"
private const val BREAKFAST_PACKAGE_QUANTITY = 2
private const val UNSCHEDULED_PACKAGE_CODE = "DINNER"

private fun ancillariesBooking(
    reservationId: String,
    guestProfile: GuestProfile? = null,
    additionalPackages: List<SelectedPackage> = emptyList(),
): Booking {
    val arrival = LocalDate.now().plusDays(30)
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
                    guestProfile = guestProfile,
                    selectedPackages =
                        listOf(
                            SelectedPackage(
                                code = BREAKFAST_PACKAGE_CODE,
                                quantity = BREAKFAST_PACKAGE_QUANTITY,
                                unitPrice = 12.50,
                            ),
                        ) + additionalPackages,
                ),
            ),
    )
}
