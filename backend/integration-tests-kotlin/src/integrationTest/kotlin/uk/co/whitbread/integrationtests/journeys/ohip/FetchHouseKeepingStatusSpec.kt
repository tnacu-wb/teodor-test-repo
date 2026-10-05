package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_HOUSEKEEPING_OVERVIEW_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.housekeepingOverviewFailure
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.featureflags.OhipFeatureFlag
import uk.co.whitbread.integrationtests.testkit.mocks.OperaEndpoint
import uk.co.whitbread.integrationtests.testkit.mocks.Upstream
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.HotelPhysicalRoom
import uk.co.whitbread.integrationtests.testkit.presets.Hotels

/**
 * Proves `GET /ohip/v1/rooms/fetchHouseKeepingStatus`: the adapter filters Opera's housekeeping
 * overview to one room and flattens it to hotelId/roomId/status; a room Opera does not report
 * is answered 200 with the literal status "RoomId Not Available", not an error.
 *
 * No endpoint-specific feature flags; the opera token-service flags are environment-pinned OFF
 * (infrastructure scope), re-pinned here only for explicitness.
 *
 * Flow: backend/integration-tests-kotlin/flows/ohip-adapter-service/FetchHouseKeepingRoomStatus.md
 */
class FetchHouseKeepingStatusSpec :
    JourneySpec(
        "OHIP adapter reports a room's housekeeping status",
        {
            val ohipApi = OhipApi()

            scenario("a known room reports its housekeeping status") {
                val booking = housekeepingBooking()

                installFor(booking)

                val result =
                    ohipApi.fetchHouseKeepingStatus(
                        hotelId = booking.hotel.hotelId,
                        roomId = "101",
                        testId = testId,
                        featureFlagOverrides = mapOf(OhipFeatureFlag.USE_TOKEN_SERVICE to false),
                    )

                result.attachEvidence("Known Room Status")

                expect("returns 200 with the room's status") {
                    result.response.status.value shouldBe 200
                    result.body.hotelId shouldBe booking.hotel.hotelId
                    result.body.roomId shouldBe "101"
                    result.body.status shouldBe "Clean"
                }

                expect("makes exactly one Opera housekeeping call") {
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_HOUSEKEEPING) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an unknown room is answered with RoomId Not Available") {
                val booking = housekeepingBooking()

                installFor(booking)

                val result =
                    ohipApi.fetchHouseKeepingStatus(
                        hotelId = booking.hotel.hotelId,
                        roomId = "999",
                        testId = testId,
                        featureFlagOverrides = mapOf(OhipFeatureFlag.USE_TOKEN_SERVICE to false),
                    )

                result.attachEvidence("Unknown Room Status")

                expect("returns 200 echoing the room id with the not-available status") {
                    result.response.status.value shouldBe 200
                    result.body.roomId shouldBe "999"
                    result.body.status shouldBe "RoomId Not Available"
                }

                expect("makes exactly one Opera housekeeping call") {
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_HOUSEKEEPING) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an Opera overview failure is mapped to the housekeeping error") {
                val booking = housekeepingBooking()

                // A downstream failure is exceptional behavior, distinct from the 200
                // "RoomId Not Available" branch Opera reports for an unknown room.
                installFor(booking, excluded = setOf(OPERA_HOUSEKEEPING_OVERVIEW_STUB_ID))
                installStub(housekeepingOverviewFailure(booking.hotels))

                val result =
                    ohipApi.fetchHouseKeepingStatus(
                        hotelId = booking.hotel.hotelId,
                        roomId = "101",
                        testId = testId,
                        featureFlagOverrides = mapOf(OhipFeatureFlag.USE_TOKEN_SERVICE to false),
                    )

                result.attachEvidence("Housekeeping Opera Error")

                expect("returns the mapped housekeeping-status error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 932
                }

                expect("makes exactly one Opera housekeeping call") {
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_HOUSEKEEPING) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
        },
    )

private fun housekeepingBooking(): Booking =
    Booking(
        hotels =
            listOf(
                Hotels.HEAPTI.copy(
                    physicalRooms =
                        listOf(
                            HotelPhysicalRoom(roomId = "101", roomType = "DOUBLE"),
                            HotelPhysicalRoom(
                                roomId = "103",
                                roomType = "DOUBLE",
                                housekeepingStatus = "Dirty",
                            ),
                        ),
                ),
            ),
    )
