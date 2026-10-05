package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_PROMOTION_CODES_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.promotionCodesFailure
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.featureflags.FeatureFlag
import uk.co.whitbread.integrationtests.testkit.featureflags.OhipFeatureFlag
import uk.co.whitbread.integrationtests.testkit.mocks.OperaEndpoint
import uk.co.whitbread.integrationtests.testkit.mocks.Upstream
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.HotelPromotionCode
import uk.co.whitbread.integrationtests.testkit.presets.Hotels
import java.time.LocalDate

private val promotionsFlagPins =
    mapOf<FeatureFlag, Boolean>(
        OhipFeatureFlag.USE_TOKEN_SERVICE to false,
    )

/**
 * Proves the OHIP adapter's promotion-code lookup: `GET /ohip/promotions` reads the hotel's
 * Opera promotion codes and maps each one's name, booking window, and stay window into the
 * public response.
 *
 * Flow: backend/integration-tests-kotlin/flows/ohip-adapter-service/GetPromotionCode.md
 */
class GetPromotionsSpec :
    JourneySpec(
        "OHIP adapter returns hotel promotion codes",
        {
            val ohipApi = OhipApi()

            scenario("a hotel promotion code is returned with its booking and stay windows") {
                val promotion =
                    HotelPromotionCode(
                        code = "SUMMER25",
                        name = "Summer Getaway",
                        bookingStartDate = LocalDate.now().minusDays(30),
                        bookingEndDate = LocalDate.now().plusDays(30),
                        stayStartDate = LocalDate.now(),
                        stayEndDate = LocalDate.now().plusDays(90),
                    )
                val booking = Booking(hotels = listOf(Hotels.HEAPTI.copy(promotionCodes = listOf(promotion))))

                installFor(booking)

                val result =
                    ohipApi.getPromotions(
                        promotionCodes = listOf(promotion.code),
                        hotelId = booking.hotel.hotelId,
                        testId = testId,
                        featureFlagOverrides = promotionsFlagPins,
                    )

                result.attachEvidence("Get Promotions")

                expect("returns the promotion with its windows") {
                    result.response.status.value shouldBe 200
                    result.body.size shouldBe 1
                    val entry = result.body.first()
                    entry.promotionCode shouldBe promotion.code
                    entry.promotionName shouldBe promotion.name
                    entry.bookingStartDate shouldBe promotion.bookingStartDate.toString()
                    entry.bookingEndDate shouldBe promotion.bookingEndDate.toString()
                    entry.stayStartDate shouldBe promotion.stayStartDate.toString()
                    entry.stayEndDate shouldBe promotion.stayEndDate.toString()
                }

                expect("reads only the Opera promotion codes") {
                    // One Opera call: GET promotionCodes.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_PROMOTION_CODES) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a rejected Opera promotion-codes read maps to the promotion error") {
                val promotion =
                    HotelPromotionCode(
                        code = "SUMMER26",
                        name = "Summer Getaway",
                        bookingStartDate = LocalDate.now().minusDays(30),
                        bookingEndDate = LocalDate.now().plusDays(30),
                        stayStartDate = LocalDate.now(),
                        stayEndDate = LocalDate.now().plusDays(90),
                    )
                val booking = Booking(hotels = listOf(Hotels.HEAPTI.copy(promotionCodes = listOf(promotion))))

                installFor(booking, excluded = setOf(OPERA_PROMOTION_CODES_STUB_ID))
                installStub(promotionCodesFailure(booking.hotel))

                val result =
                    ohipApi.getPromotions(
                        promotionCodes = listOf(promotion.code),
                        hotelId = booking.hotel.hotelId,
                        testId = testId,
                        featureFlagOverrides = promotionsFlagPins,
                    )

                result.attachEvidence("Get Promotions Opera Error")

                expect("returns the mapped promotion error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 974
                }

                expect("stops after the rejected promotion-codes read") {
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_PROMOTION_CODES) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
        },
    )
