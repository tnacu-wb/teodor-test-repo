package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.clients.ohip.model.HotelStatusResponse
import uk.co.whitbread.integrationtests.stubs.opera.custom.hotelDetailsStatusFailure
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.featureflags.FeatureFlag
import uk.co.whitbread.integrationtests.testkit.featureflags.OhipFeatureFlag
import uk.co.whitbread.integrationtests.testkit.mocks.OperaEndpoint
import uk.co.whitbread.integrationtests.testkit.mocks.Upstream
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.HotelOnSaleStatus
import uk.co.whitbread.integrationtests.testkit.presets.Hotels

private val hotelsStatusFlagPins =
    mapOf<FeatureFlag, Boolean>(
        OhipFeatureFlag.USE_TOKEN_SERVICE to false,
    )

/**
 * Proves the OHIP adapter's hotel status lookup: `GET /ohip/hotels/status` reads each requested
 * hotel's Opera enterprise hotel-details and maps the ONSALE category to `onSale` and the PMS
 * category to `pmsSource`, one entry per hotel.
 *
 * Flow: backend/integration-tests-kotlin/flows/ohip-adapter-service/GetOnSaleFlagFromOpera.md
 */
class GetHotelsStatusSpec :
    JourneySpec(
        "OHIP adapter reports per-hotel on-sale status",
        {
            val ohipApi = OhipApi()

            scenario("two hotels report their own on-sale flag and PMS source") {
                val onSaleHotel = Hotels.HEAPTI.copy(onSaleStatus = HotelOnSaleStatus(onSale = true, pmsSource = "OPERA"))
                val offSaleHotel = Hotels.FRAMTI.copy(onSaleStatus = HotelOnSaleStatus(onSale = false, pmsSource = "BART"))
                val booking = Booking(hotels = listOf(onSaleHotel, offSaleHotel))

                installFor(booking)

                val result =
                    ohipApi.getHotelsStatus(
                        hotelIds = booking.hotels.map { it.hotelId },
                        testId = testId,
                        featureFlagOverrides = hotelsStatusFlagPins,
                    )

                result.attachEvidence("Get Hotels Status")

                expect("maps each hotel's own ONSALE and PMS categories") {
                    result.response.status.value shouldBe 200
                    result.body.size shouldBe 2
                    result.body.statusFor(onSaleHotel.hotelId).onSale shouldBe true
                    result.body.statusFor(onSaleHotel.hotelId).pmsSource shouldBe "OPERA"
                    result.body.statusFor(offSaleHotel.hotelId).onSale shouldBe false
                    result.body.statusFor(offSaleHotel.hotelId).pmsSource shouldBe "BART"
                }

                expect("reads one Opera hotel-details per hotel") {
                    // Two Opera calls: one GET hotelDetails per hotel id.
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_HOTEL_DETAILS) shouldBe 2
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a hotel whose Opera details read fails falls back to BART without failing the batch") {
                val healthyHotel = Hotels.HEAPTI.copy(onSaleStatus = HotelOnSaleStatus(onSale = true, pmsSource = "OPERA"))
                // The failing hotel carries no onSaleStatus, so no default competes with the rejection.
                val failingHotel = Hotels.FRAMTI
                val booking = Booking(hotels = listOf(healthyHotel, failingHotel))

                installFor(booking)
                installStub(hotelDetailsStatusFailure(failingHotel.hotelId))

                val result =
                    ohipApi.getHotelsStatus(
                        hotelIds = listOf(healthyHotel.hotelId, failingHotel.hotelId),
                        testId = testId,
                        featureFlagOverrides = hotelsStatusFlagPins,
                    )

                result.attachEvidence("Get Hotels Status One Failure")

                expect("absorbs the failure into the BART fallback inside a 200") {
                    result.response.status.value shouldBe 200
                    result.body.size shouldBe 2
                    result.body.statusFor(healthyHotel.hotelId).onSale shouldBe true
                    result.body.statusFor(healthyHotel.hotelId).pmsSource shouldBe "OPERA"
                    // The catch branch deliberately reports a failed hotel as on sale with the
                    // BART source, so a hotel never drops off sale because of an Opera outage.
                    result.body.statusFor(failingHotel.hotelId).onSale shouldBe true
                    result.body.statusFor(failingHotel.hotelId).pmsSource shouldBe "BART"
                }

                expect("attempted both Opera hotel-details reads") {
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_HOTEL_DETAILS) shouldBe 2
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
        },
    )

private fun List<HotelStatusResponse>.statusFor(hotelId: String): HotelStatusResponse =
    requireNotNull(firstOrNull { it.hotelId == hotelId }) { "no status entry for hotel $hotelId" }
