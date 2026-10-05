package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_VACANT_ROOMS_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.vacantRoomsFailure
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.featureflags.OhipFeatureFlag
import uk.co.whitbread.integrationtests.testkit.mocks.OperaEndpoint
import uk.co.whitbread.integrationtests.testkit.mocks.Upstream
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.HotelPhysicalRoom
import uk.co.whitbread.integrationtests.testkit.presets.Hotels

/**
 * Proves `POST /ohip/v1/rooms/getVacant`: the adapter passes the hotel and room type through to
 * Opera's front-office rooms query (pinned to Clean, Vacant, all room conditions, page size 60)
 * and returns only the `hotelRoomsDetails` slice of Opera's response.
 *
 * No endpoint-specific feature flags; the opera token-service flags are environment-pinned OFF
 * (infrastructure scope), re-pinned here only for explicitness.
 *
 * Flow: backend/integration-tests-kotlin/flows/ohip-adapter-service/GetVacantRooms.md
 */
class GetVacantRoomsSpec :
    JourneySpec(
        "OHIP adapter lists a hotel's clean vacant rooms",
        {
            val ohipApi = OhipApi()

            scenario("clean vacant rooms of the requested type are returned") {
                val booking = vacantRoomsBooking()

                installFor(booking)

                val result =
                    ohipApi.getVacantRooms(
                        hotelId = booking.hotel.hotelId,
                        roomType = "DOUBLE",
                        testId = testId,
                        featureFlagOverrides = mapOf(OhipFeatureFlag.USE_TOKEN_SERVICE to false),
                    )

                result.attachEvidence("Vacant Rooms Found")

                expect("returns 200 with only the clean vacant DOUBLE rooms") {
                    result.response.status.value shouldBe 200
                    val details = result.body.hotelRoomsDetails!!
                    details.hotelId shouldBe booking.hotel.hotelId
                    // 103 is Dirty and 201 is a TWIN, so neither may appear.
                    details.room.map { room -> room.roomId } shouldContainExactly
                        listOf("101", "102")
                }

                expect("makes exactly one Opera front-office rooms call") {
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_VACANT_ROOMS) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a room type with no clean vacant rooms returns an empty list") {
                val booking = vacantRoomsBooking()

                installFor(booking)

                val result =
                    ohipApi.getVacantRooms(
                        hotelId = booking.hotel.hotelId,
                        // The only TWIN room is Occupied, so Opera has nothing to offer.
                        roomType = "TWIN",
                        testId = testId,
                        featureFlagOverrides = mapOf(OhipFeatureFlag.USE_TOKEN_SERVICE to false),
                    )

                result.attachEvidence("No Vacant Rooms")

                expect("returns 200 with an empty room list") {
                    result.response.status.value shouldBe 200
                    result.body.hotelRoomsDetails!!.room shouldBe emptyList()
                }

                expect("makes exactly one Opera front-office rooms call") {
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_VACANT_ROOMS) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an Opera rooms-query failure is mapped to the vacant-rooms error") {
                val booking = vacantRoomsBooking()

                // A downstream failure is exceptional behavior, not a normal Booking world state.
                installFor(booking, excluded = setOf(OPERA_VACANT_ROOMS_STUB_ID))
                installStub(vacantRoomsFailure(booking.hotels))

                val result =
                    ohipApi.getVacantRooms(
                        hotelId = booking.hotel.hotelId,
                        roomType = "DOUBLE",
                        testId = testId,
                        featureFlagOverrides = mapOf(OhipFeatureFlag.USE_TOKEN_SERVICE to false),
                    )

                result.attachEvidence("Vacant Rooms Opera Error")

                expect("returns the mapped get-vacant-rooms error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 930
                }

                expect("makes exactly one Opera front-office rooms call") {
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_VACANT_ROOMS) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
        },
    )

private fun vacantRoomsBooking(): Booking =
    Booking(
        hotels =
            listOf(
                Hotels.HEAPTI.copy(
                    physicalRooms =
                        listOf(
                            HotelPhysicalRoom(roomId = "101", roomType = "DOUBLE"),
                            HotelPhysicalRoom(roomId = "102", roomType = "DOUBLE", floor = "1"),
                            HotelPhysicalRoom(
                                roomId = "103",
                                roomType = "DOUBLE",
                                housekeepingStatus = "Dirty",
                            ),
                            HotelPhysicalRoom(
                                roomId = "201",
                                roomType = "TWIN",
                                frontOfficeStatus = "Occupied",
                            ),
                        ),
                ),
            ),
    )
