package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.clients.ohip.model.ReservationIdEntry
import uk.co.whitbread.integrationtests.clients.ohip.model.RoomAllocationCriteria
import uk.co.whitbread.integrationtests.clients.ohip.model.RoomAllocationRequest
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_ROOM_ASSIGNMENT_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.roomAssignmentFailure
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.featureflags.OhipFeatureFlag
import uk.co.whitbread.integrationtests.testkit.mocks.OperaEndpoint
import uk.co.whitbread.integrationtests.testkit.mocks.Upstream
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.Rate
import uk.co.whitbread.integrationtests.testkit.model.ReservationStatus
import uk.co.whitbread.integrationtests.testkit.presets.Hotels
import java.time.LocalDate

/**
 * Proves `POST /ohip/v1/rooms/allocate`: the adapter passes the caller's criteria wrapper
 * through to Opera's room-assignment POST for the first reservation in the list and returns
 * only Opera's links.
 *
 * No endpoint-specific feature flags; the opera token-service flags are environment-pinned OFF
 * (infrastructure scope), re-pinned here only for explicitness.
 *
 * Flow: backend/integration-tests-kotlin/flows/ohip-adapter-service/AllocateRooms.md
 */
class AllocateRoomsSpec :
    JourneySpec(
        "OHIP adapter allocates a room to a reservation",
        {
            val ohipApi = OhipApi()

            scenario("allocating a room to a reservation returns Opera's links") {
                val booking = allocateRoomsBooking(reservationId = "6008101")

                installFor(booking)

                val result =
                    ohipApi.allocateRooms(
                        request =
                            RoomAllocationRequest(
                                criteria =
                                    RoomAllocationCriteria(
                                        hotelId = booking.hotel.hotelId,
                                        reservationIdList =
                                            listOf(
                                                ReservationIdEntry(
                                                    id =
                                                        requireNotNull(
                                                            booking.room.reservationId,
                                                        ),
                                                ),
                                            ),
                                        // The room number is caller input, not reservation
                                        // state: Opera accepts any room the kiosk offers.
                                        roomId = "101",
                                    ),
                            ),
                        testId = testId,
                        featureFlagOverrides = mapOf(OhipFeatureFlag.USE_TOKEN_SERVICE to false),
                    )

                result.attachEvidence("Room Allocated")

                expect("returns 200 with Opera's assignment links") {
                    result.response.status.value shouldBe 200
                    result.body.links.shouldNotBeEmpty()
                }

                expect("makes exactly one Opera room-assignment call") {
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.ASSIGN_ROOM) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an Opera assignment refusal is mapped to the allocate-rooms error") {
                val booking = allocateRoomsBooking(reservationId = "6008102")

                // A downstream refusal is exceptional behavior, not a normal Booking world state.
                installFor(booking, excluded = setOf(OPERA_ROOM_ASSIGNMENT_STUB_ID))
                installStub(roomAssignmentFailure(booking, booking.rooms))

                val result =
                    ohipApi.allocateRooms(
                        request =
                            RoomAllocationRequest(
                                criteria =
                                    RoomAllocationCriteria(
                                        hotelId = booking.hotel.hotelId,
                                        reservationIdList =
                                            listOf(
                                                ReservationIdEntry(
                                                    id =
                                                        requireNotNull(
                                                            booking.room.reservationId,
                                                        ),
                                                ),
                                            ),
                                        roomId = "101",
                                    ),
                            ),
                        testId = testId,
                        featureFlagOverrides = mapOf(OhipFeatureFlag.USE_TOKEN_SERVICE to false),
                    )

                result.attachEvidence("Room Allocation Opera Error")

                expect("returns the mapped allocate-rooms error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 931
                }

                expect("makes exactly one Opera room-assignment call, unretried") {
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.ASSIGN_ROOM) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
        },
    )

private fun allocateRoomsBooking(reservationId: String): Booking {
    val arrival = LocalDate.now().plusDays(7)
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
                ),
            ),
    )
}
