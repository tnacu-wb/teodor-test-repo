package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_RESTRICTIONS_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.restrictionsWithRejectedChunk
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.featureflags.FeatureFlag
import uk.co.whitbread.integrationtests.testkit.featureflags.OhipFeatureFlag
import uk.co.whitbread.integrationtests.testkit.mocks.OperaEndpoint
import uk.co.whitbread.integrationtests.testkit.mocks.Upstream
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.HotelRestriction
import uk.co.whitbread.integrationtests.testkit.presets.Hotels
import java.time.LocalDate

private val restrictionsFlagPins =
    mapOf<FeatureFlag, Boolean>(
        OhipFeatureFlag.USE_TOKEN_SERVICE to false,
    )

/**
 * Proves the OHIP adapter's sell-restriction reads: `GET /ohip/hotels/{hotelId}/restrictions`
 * splits ranges over 90 days into chunks, calls Opera once per chunk concurrently, and merges
 * the results; `GET /ohip/hotels/restrictions` fans the same lookup out per requested hotel and
 * returns results in request order.
 *
 * Flows: backend/integration-tests-kotlin/flows/ohip-adapter-service/GetRestrictionsByDateRange.md
 * and GetMultiHotelRestrictionsByDateRange.md
 */
class GetRestrictionsSpec :
    JourneySpec(
        "OHIP adapter returns hotel sell restrictions",
        {
            val ohipApi = OhipApi()

            scenario("a short range returns the hotel's restrictions from one Opera call") {
                val start = LocalDate.now().plusDays(14)
                val end = start.plusDays(2)
                val restriction = HotelRestriction(status = "Close", start = start, end = end, roomType = "LOWDBL")
                val booking =
                    Booking(
                        hotels = listOf(Hotels.HEAPTI.copy(restrictions = listOf(restriction))),
                        arrival = start,
                        departure = end,
                    )

                installFor(booking)

                val result =
                    ohipApi.getRestrictions(
                        hotelId = booking.hotel.hotelId,
                        startDate = start.toString(),
                        endDate = end.toString(),
                        testId = testId,
                        featureFlagOverrides = restrictionsFlagPins,
                    )

                result.attachEvidence("Get Restrictions Short Range")

                expect("returns the restriction for the hotel") {
                    result.response.status.value shouldBe 200
                    result.body.hotelId shouldBe booking.hotel.hotelId
                    val sets = result.body.restrictionSets.orEmpty()
                    sets.size shouldBe 1
                    sets.first().restrictionStatus?.code shouldBe restriction.status
                    sets.first().restrictionControl?.roomType shouldBe restriction.roomType
                }

                expect("reads Opera once for the single chunk") {
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_RESTRICTIONS) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a range over 90 days is chunked into two Opera calls and merged") {
                val start = LocalDate.now().plusDays(14)
                val end = start.plusDays(99)
                val firstChunkRestriction =
                    HotelRestriction(status = "Close", start = start.plusDays(5), end = start.plusDays(6))
                val secondChunkRestriction =
                    HotelRestriction(status = "Open", start = start.plusDays(95), end = start.plusDays(96))
                val booking =
                    Booking(
                        hotels =
                            listOf(
                                Hotels.HEAPTI.copy(
                                    restrictions = listOf(firstChunkRestriction, secondChunkRestriction),
                                ),
                            ),
                        arrival = start,
                        departure = end,
                    )

                installFor(booking)

                val result =
                    ohipApi.getRestrictions(
                        hotelId = booking.hotel.hotelId,
                        startDate = start.toString(),
                        endDate = end.toString(),
                        testId = testId,
                        featureFlagOverrides = restrictionsFlagPins,
                    )

                result.attachEvidence("Get Restrictions Chunked Range")

                expect("merges the restrictions from both chunks") {
                    result.response.status.value shouldBe 200
                    result.body.restrictionSets
                        .orEmpty()
                        .map { it.restrictionStatus?.code }
                        .shouldContainExactlyInAnyOrder("Close", "Open")
                }

                expect("reads Opera once per chunk") {
                    // Two Opera calls: one per <=90-day chunk of the 100-day range.
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_RESTRICTIONS) shouldBe 2
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("multi-hotel restrictions return per-hotel results in request order") {
                val start = LocalDate.now().plusDays(14)
                val end = start.plusDays(2)
                val firstHotel =
                    Hotels.HEAPTI.copy(
                        restrictions = listOf(HotelRestriction(status = "Close", start = start, end = end)),
                    )
                val secondHotel =
                    Hotels.FRAMTI.copy(
                        restrictions = listOf(HotelRestriction(status = "Open", start = start, end = end)),
                    )
                val booking = Booking(hotels = listOf(firstHotel, secondHotel), arrival = start, departure = end)

                installFor(booking)

                val result =
                    ohipApi.getMultiHotelRestrictions(
                        hotelIds = listOf(firstHotel.hotelId, secondHotel.hotelId),
                        startDate = start.toString(),
                        endDate = end.toString(),
                        testId = testId,
                        featureFlagOverrides = restrictionsFlagPins,
                    )

                result.attachEvidence("Get Multi Hotel Restrictions")

                expect("attributes each hotel's restriction to its slot in request order") {
                    result.response.status.value shouldBe 200
                    result.body.size shouldBe 2
                    result.body[0].hotelId shouldBe firstHotel.hotelId
                    result.body[0]
                        .restrictionSets
                        .orEmpty()
                        .first()
                        .restrictionStatus
                        ?.code shouldBe "Close"
                    result.body[1].hotelId shouldBe secondHotel.hotelId
                    result.body[1]
                        .restrictionSets
                        .orEmpty()
                        .first()
                        .restrictionStatus
                        ?.code shouldBe "Open"
                }

                expect("reads Opera once per hotel") {
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_RESTRICTIONS) shouldBe 2
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a failed chunk fails the whole single-hotel request with the restrictions error") {
                val start = LocalDate.now().plusDays(14)
                val end = start.plusDays(99)
                val booking =
                    Booking(
                        hotels =
                            listOf(
                                Hotels.HEAPTI.copy(
                                    restrictions =
                                        listOf(HotelRestriction(status = "Close", start = start.plusDays(5), end = start.plusDays(6))),
                                ),
                            ),
                        arrival = start,
                        departure = end,
                    )

                // A single failing chunk cannot be expressed by the generic default, which bundles
                // every chunk under one stub ID; rebuild it with one chunk rejected.
                installFor(booking, excluded = setOf(OPERA_RESTRICTIONS_STUB_ID))
                installStub(
                    restrictionsWithRejectedChunk(
                        booking,
                        failingHotelId = booking.hotel.hotelId,
                        failingChunkStart = start.plusDays(90),
                    ),
                )

                val result =
                    ohipApi.getRestrictions(
                        hotelId = booking.hotel.hotelId,
                        startDate = start.toString(),
                        endDate = end.toString(),
                        testId = testId,
                        featureFlagOverrides = restrictionsFlagPins,
                    )

                result.attachEvidence("Get Restrictions Chunk Failure")

                expect("fails the whole request with the mapped restrictions error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 970
                }

                expect("attempted both chunk reads") {
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_RESTRICTIONS) shouldBe 2
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a failed hotel fails the whole multi-hotel request with the restrictions error") {
                val start = LocalDate.now().plusDays(14)
                val end = start.plusDays(2)
                val healthyHotel =
                    Hotels.HEAPTI.copy(restrictions = listOf(HotelRestriction(status = "Close", start = start, end = end)))
                val failingHotel =
                    Hotels.FRAMTI.copy(restrictions = listOf(HotelRestriction(status = "Open", start = start, end = end)))
                val booking = Booking(hotels = listOf(healthyHotel, failingHotel), arrival = start, departure = end)

                installFor(booking, excluded = setOf(OPERA_RESTRICTIONS_STUB_ID))
                installStub(
                    restrictionsWithRejectedChunk(
                        booking,
                        failingHotelId = failingHotel.hotelId,
                        failingChunkStart = start,
                    ),
                )

                val result =
                    ohipApi.getMultiHotelRestrictions(
                        hotelIds = listOf(healthyHotel.hotelId, failingHotel.hotelId),
                        startDate = start.toString(),
                        endDate = end.toString(),
                        testId = testId,
                        featureFlagOverrides = restrictionsFlagPins,
                    )

                result.attachEvidence("Get Multi Hotel Restrictions One Failure")

                expect("fails the whole request with the mapped restrictions error") {
                    // The multi-hotel join surfaces a raw CompletionException, but Spring's
                    // exception resolver falls back to its cause, so both restriction endpoints
                    // map a failed Opera read to the same errCode.
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 970
                }

                expect("attempted both hotels' reads") {
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_RESTRICTIONS) shouldBe 2
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
        },
    )
