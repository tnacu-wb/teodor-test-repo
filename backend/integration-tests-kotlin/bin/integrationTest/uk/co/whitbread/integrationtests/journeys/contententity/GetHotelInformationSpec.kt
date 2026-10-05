package uk.co.whitbread.integrationtests.journeys.contententity

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.contententity.ContentEntityApi
import uk.co.whitbread.integrationtests.clients.contententity.model.HotelInformationResponse
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.Hotel
import uk.co.whitbread.integrationtests.testkit.model.hotelPagePath
import uk.co.whitbread.integrationtests.testkit.presets.Hotels

class GetHotelInformationSpec :
    JourneySpec(
        "get hotel information",
        {
            val contentEntityApi = ContentEntityApi()

            scenario("GET /v1/content/hotels/{hotelId}/information returns hotel content for FRAMTI") {
                val hotel = Hotels.FRAMTI
                val booking = Booking(hotels = listOf(hotel))

                installFor(booking)

                val result =
                    contentEntityApi.getHotelInformation(
                        hotelId = hotel.hotelId,
                        testId = testId,
                    )

                result.attachEvidence("Get Hotel Information ${hotel.hotelId}")

                expect("returns hotel content derived from the Hotel fixture") {
                    result.response.status.value shouldBe 200
                    assertHotelContent(result.body, hotel)
                }

                expect("maps address and contact details from the Hotel fixture") {
                    assertHotelContactContent(result.body, hotel)
                }

                expect("returns hotel content links") {
                    result.body.links
                        .shouldNotBeNull()
                        .detailsPage shouldBe expectedDetailsPage(hotel)
                }
            }

            scenario("GET /v1/content/hotels/{hotelId}/information returns hotel content for HEAPTI") {
                val hotel = Hotels.HEAPTI
                val booking = Booking(hotels = listOf(hotel))

                installFor(booking)

                val result =
                    contentEntityApi.getHotelInformation(
                        hotelId = hotel.hotelId,
                        testId = testId,
                    )

                result.attachEvidence("Get Hotel Information ${hotel.hotelId}")

                expect("returns hotel content derived from the Hotel fixture") {
                    result.response.status.value shouldBe 200
                    assertHotelContent(result.body, hotel)
                }

                expect("maps address and contact details from the Hotel fixture") {
                    assertHotelContactContent(result.body, hotel)
                }

                expect("returns hotel content links") {
                    result.body.links
                        .shouldNotBeNull()
                        .detailsPage shouldBe expectedDetailsPage(hotel)
                }
            }

            scenario("GET /v1/content/hotels/{hotelId}/information enriches distribution time zone") {
                val hotel = Hotels.FRAMTI.copy(timeZone = "Europe/Paris")
                val booking = Booking(hotels = listOf(hotel))

                installFor(booking)

                val result =
                    contentEntityApi.getHotelInformation(
                        hotelId = hotel.hotelId,
                        testId = testId,
                        channel = "DISTR",
                        subchannel = "WEB",
                    )

                result.attachEvidence("Get Distribution Hotel Information ${hotel.hotelId}")

                expect("returns hotel content derived from the Hotel fixture") {
                    result.response.status.value shouldBe 200
                    assertHotelContent(result.body, hotel)
                }

                expect("maps time zone from OHIP hotel info") {
                    result.body.timeZone shouldBe hotel.timeZone
                }
            }

            scenario("GET /v1/content/hotels/{hotelId}/information rejects missing required country") {
                val result =
                    contentEntityApi.getHotelInformation(
                        hotelId = Hotels.FRAMTI.hotelId,
                        country = null,
                        language = "en",
                        testId = testId,
                    )

                result.attachEvidence("Get Hotel Information Missing Country")

                expect("returns a validation error without AEM stubs") {
                    result.response.status.value shouldBe 400
                    result.bodyText.isNotBlank() shouldBe true
                }
            }
        },
    )

private fun assertHotelContent(
    body: HotelInformationResponse,
    hotel: Hotel,
) {
    body.hotelId shouldBe hotel.hotelId
    body.name shouldBe hotel.name
    body.title shouldBe "${hotel.name} hotel"
    body.brand shouldBe hotel.brandCode
    body.headline shouldBe hotel.name
    body.hotelDescription shouldBe "<p>${hotel.name}</p>"
    body.countryCodeISO shouldBe hotel.country
}

private fun assertHotelContactContent(
    body: HotelInformationResponse,
    hotel: Hotel,
) {
    val address = body.address.shouldNotBeNull()
    address.addressLine1 shouldBe hotel.addressLine
    address.postalCode shouldBe hotel.postcode

    val contactDetails = body.contactDetails.shouldNotBeNull()
    contactDetails.phone shouldBe hotel.phone
    contactDetails.hotelNationalPhone shouldBe hotel.phone
}

private fun expectedDetailsPage(hotel: Hotel): String = hotelPagePath(hotel).removePrefix("/hotels").removeSuffix(".html")
