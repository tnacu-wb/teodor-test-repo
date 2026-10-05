package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_HOTEL_CONFIG_STUB_ID
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.Rate
import uk.co.whitbread.integrationtests.testkit.presets.Hotels

class GetAllRatePlansForAHotelSpec :
    JourneySpec(
        "Fetch rate plans for a hotel",
        {
            scenario("Fetch rate plans for FRAMTI") {
                val hotel =
                    Hotels.FRAMTI.copy(
                        availableRates =
                            listOf(
                                Rate(ratePlan = "FLEXRATE", roomType = "LOWDBL", adults = 2),
                                Rate(ratePlan = "MIFIXB03", roomType = "LOWDBL", adults = 2),
                                Rate(ratePlan = "BUSIFLEX", roomType = "LOWDBL", adults = 2),
                            ),
                    )
                val booking = Booking(hotels = listOf(hotel))

                installFor(
                    booking,
                    excluded = setOf(OPERA_HOTEL_CONFIG_STUB_ID),
                )

                val result = OhipApi().getRatePlans(booking.hotel.hotelId, testId)

                result.attachEvidence("Get All Rate Plans FRAMTI")

                expect("returns configured rate plans") {
                    result.response.status.value shouldBe 200
                    result.body.ratePlans shouldHaveSize hotel.availableRates.size
                }

                expect("each rate plan belongs to FRAMTI and has required fields") {
                    result.body.ratePlans.forEach { plan ->
                        plan.hotelId shouldBe "FRAMTI"
                        plan.ratePlanCode.shouldNotBeNull()
                        plan.primaryDetails
                            ?.description
                            ?.defaultText
                            .shouldNotBeNull()
                        plan.classifications?.rateCategory.shouldNotBeNull()
                    }
                }

                expect("response includes only configured rate plan codes") {
                    val expectedCodes = hotel.availableRates.map { it.ratePlan }.toSet()
                    val actualCodes =
                        result.body.ratePlans
                            .map { it.ratePlanCode.shouldNotBeNull() }
                            .toSet()
                    actualCodes shouldBe expectedCodes
                }
            }

            scenario("Fetch rate plans for HEAPTI") {
                val hotel =
                    Hotels.HEAPTI.copy(
                        availableRates =
                            listOf(
                                Rate(ratePlan = "SEMIFLEX", roomType = "LOWDBL", adults = 2),
                                Rate(ratePlan = "ADVANCE", roomType = "LOWDBL", adults = 2),
                                Rate(ratePlan = "STANDARD", roomType = "LOWDBL", adults = 2),
                                Rate(ratePlan = "NONFLEX", roomType = "LOWDBL", adults = 2),
                            ),
                    )
                val booking = Booking(hotels = listOf(hotel))

                installFor(
                    booking,
                    excluded = setOf(OPERA_HOTEL_CONFIG_STUB_ID),
                )

                val result = OhipApi().getRatePlans(booking.hotel.hotelId, testId)

                result.attachEvidence("Get All Rate Plans HEAPTI")

                expect("returns configured rate plans") {
                    result.response.status.value shouldBe 200
                    result.body.ratePlans shouldHaveSize hotel.availableRates.size
                }

                expect("each rate plan belongs to HEAPTI and has required fields") {
                    result.body.ratePlans.forEach { plan ->
                        plan.hotelId shouldBe "HEAPTI"
                        plan.ratePlanCode.shouldNotBeNull()
                        plan.primaryDetails
                            ?.description
                            ?.defaultText
                            .shouldNotBeNull()
                        plan.classifications?.rateCategory.shouldNotBeNull()
                    }
                }

                expect("response includes only configured rate plan codes") {
                    val expectedCodes = hotel.availableRates.map { it.ratePlan }.toSet()
                    val actualCodes =
                        result.body.ratePlans
                            .map { it.ratePlanCode.shouldNotBeNull() }
                            .toSet()
                    actualCodes shouldBe expectedCodes
                }
            }
        },
    )
