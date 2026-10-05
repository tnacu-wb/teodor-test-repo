package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.presets.Hotels

private val hotelInfoHotels = listOf(Hotels.HEAPTI, Hotels.FRAMTI)
private const val EXPECTED_CHECK_IN_TIME = "1/1/70, 3:00\u202fPM"
private const val EXPECTED_CHECK_OUT_TIME = "1/1/70, 12:00\u202fPM"

class GetHotelInfoSpec :
    JourneySpec(
        "Hotel info can be fetched from OHIP adapter service",
        {
            val ohipApi = OhipApi()

            hotelInfoHotels.forEach { hotel ->
                scenario("GET /ohip/hotels/${hotel.hotelId}/info") {
                    val booking = Booking(hotels = listOf(hotel))

                    installFor(booking)

                    val result =
                        ohipApi.getHotelInfo(
                            hotelId = booking.hotel.hotelId,
                            testId = testId,
                        )

                    result.attachEvidence("Get Hotel Info ${hotel.hotelId}")

                    expect("returns mapped ${hotel.hotelId} hotel info") {
                        result.response.status.value shouldBe 200
                        result.body.threeLetterId shouldBe booking.hotel.shortId
                        result.body.hotelTimeZone shouldBe booking.hotel.timeZone
                        result.body.hotelCountryCode shouldBe booking.hotel.country
                        result.body.currencyCode shouldBe booking.hotel.currency
                        result.body.languageCode shouldBe "E"
                        result.body.checkInTime shouldBe EXPECTED_CHECK_IN_TIME
                        result.body.checkOutTime shouldBe EXPECTED_CHECK_OUT_TIME
                    }
                }
            }
        },
    )
