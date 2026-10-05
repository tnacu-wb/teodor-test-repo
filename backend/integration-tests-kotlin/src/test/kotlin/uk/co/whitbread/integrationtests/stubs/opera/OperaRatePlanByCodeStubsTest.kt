package uk.co.whitbread.integrationtests.stubs.opera

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import uk.co.whitbread.integrationtests.stubs.defaultStubsFor
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.Hotel
import uk.co.whitbread.integrationtests.testkit.model.Rate

/** Pins the generic promotional rate-plan details capability and its Booking gate. */
class OperaRatePlanByCodeStubsTest :
    FunSpec({
        val promotionalRate =
            Rate(
                ratePlan = "PROMOFLEX",
                roomType = "DOUBLE",
                adults = 2,
                promotionCode = "SUMMER20",
                dynamicBaseRatePlan = "FLEXRATE",
            )
        val hotel =
            Hotel(
                hotelId = "HEAPTI",
                shortId = "HAP",
                name = "Rate Plan Hotel",
                addressLine = "1 Rate Street",
                city = "London",
                postcode = "SW1A 1AA",
                phone = "02079460000",
                availableRates = listOf(promotionalRate),
            )

        test("the rate-plan mapping requires the exact path and authenticated hotel headers") {
            val request = ratePlanInfo(hotel, promotionalRate).mappings.single().request

            request.method shouldBe "GET"
            request.urlPath shouldBe "/rtp/v1/hotels/HEAPTI/ratePlans/PROMOFLEX"
            request.queryParameters shouldBe null
            val headers = request.headers.orEmpty()
            headers.getValue("Authorization").matches shouldBe "Bearer .+"
            headers.getValue("x-app-key").matches shouldBe ".+"
            headers.getValue("x-hotelid").equalTo shouldBe "HEAPTI"
            headers.getValue("ratePlanCode").equalTo shouldBe "PROMOFLEX"
        }

        test("the rate-plan response is driven by the hotel and promotional rate") {
            val ratePlan =
                ratePlanInfo(hotel, promotionalRate)
                    .mappings
                    .single()
                    .response
                    .jsonBody!!
                    .jsonObject
                    .getValue("ratePlans")
                    .jsonArray
                    .single()
                    .jsonObject

            ratePlan.getValue("hotelId").jsonPrimitive.content shouldBe "HEAPTI"
            ratePlan.getValue("ratePlanCode").jsonPrimitive.content shouldBe "PROMOFLEX"
            ratePlan
                .getValue("ratePlanBasedOnRates")
                .jsonArray
                .single()
                .jsonObject
                .getValue("dynamicBaseRate")
                .jsonObject
                .getValue("dynamicBasedOnRatePlan")
                .jsonPrimitive
                .content shouldBe "FLEXRATE"
        }

        test("one mapping is created for each promotional rate") {
            val secondRate =
                promotionalRate.copy(
                    ratePlan = "PROMOSAVER",
                    promotionCode = "AUTUMN20",
                    dynamicBaseRatePlan = null,
                )

            ratePlanInfo(hotel, listOf(promotionalRate, secondRate))
                .mappings
                .map { it.request.urlPath } shouldContainExactly
                listOf(
                    "/rtp/v1/hotels/HEAPTI/ratePlans/PROMOFLEX",
                    "/rtp/v1/hotels/HEAPTI/ratePlans/PROMOSAVER",
                )
        }

        test("the default installs only when a hotel offers a promotional rate") {
            val withPromotion = Booking(hotels = listOf(hotel))
            val withoutPromotion =
                Booking(
                    hotels =
                        listOf(
                            hotel.copy(
                                availableRates =
                                    listOf(
                                        promotionalRate.copy(
                                            promotionCode = null,
                                            dynamicBaseRatePlan = null,
                                        ),
                                    ),
                            ),
                        ),
                )

            defaultStubsFor(withPromotion).map { it.id }.contains(OPERA_RATE_PLAN_INFO_STUB_ID) shouldBe true
            defaultStubsFor(withoutPromotion).map { it.id } shouldNotContain OPERA_RATE_PLAN_INFO_STUB_ID
        }
    })
