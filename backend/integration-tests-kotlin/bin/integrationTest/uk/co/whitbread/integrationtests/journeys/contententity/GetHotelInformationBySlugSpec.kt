package uk.co.whitbread.integrationtests.journeys.contententity

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.contententity.ContentEntityApi
import uk.co.whitbread.integrationtests.stubs.aem.allHotels
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_DONATION_PACKAGES_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_HOTEL_CONFIG_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_PACKAGE_GROUP_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_ROOM_TYPES_STUB_ID
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.Hotel
import uk.co.whitbread.integrationtests.testkit.presets.Hotels

private val contentHotelFixtures =
    listOf(
        ContentHotelFixture(
            hotel = Hotels.FRAMTI,
            slug = "/hotels/england/greater-london/london/london-farringdon-smithfield.html",
            detailsPage = "/england/greater-london/london/london-farringdon-smithfield",
        ),
        ContentHotelFixture(
            hotel = Hotels.HEAPTI,
            slug = "/hotels/england/greater-london/hayes/london-heathrow-airport-m4j4.html",
            detailsPage = "/england/greater-london/hayes/london-heathrow-airport-m4j4",
        ),
    )

class GetHotelInformationBySlugSpec :
    JourneySpec(
        "get hotel information by slug",
        {
            val contentEntityApi = ContentEntityApi()
            val hotels = contentHotelFixtures.map { it.hotel }

            contentHotelFixtures.forEach { fixture ->
                scenario("GET /v1/content/hotels for ${fixture.hotel.hotelId}") {
                    val booking = Booking(hotels = hotels)

                    installFor(
                        booking,
                        excluded =
                            setOf(
                                OPERA_HOTEL_CONFIG_STUB_ID,
                                OPERA_ROOM_TYPES_STUB_ID,
                                OPERA_PACKAGE_GROUP_STUB_ID,
                                OPERA_DONATION_PACKAGES_STUB_ID,
                            ),
                    )

                    val result =
                        contentEntityApi.getHotelBySlug(
                            slug = fixture.slug,
                            testId = testId,
                        )

                    result.attachEvidence("Get Hotel Information By Slug ${fixture.hotel.hotelId}")

                    expect("returns hotel content derived from the Hotel fixture") {
                        result.response.status.value shouldBe 200
                        result.body.hotelId shouldBe fixture.hotel.hotelId
                        result.body.name shouldBe fixture.hotel.name
                        result.body.title shouldBe "${fixture.hotel.name} hotel"
                        result.body.brand shouldBe fixture.hotel.brandCode
                        result.body.headline shouldBe fixture.hotel.name
                        result.body.hotelDescription shouldBe "<p>${fixture.hotel.name}</p>"
                        result.body.countryCodeISO shouldBe fixture.hotel.country
                    }

                    expect("maps address and contact details from the Hotel fixture") {
                        val address = result.body.address.shouldNotBeNull()
                        address.addressLine1 shouldBe fixture.hotel.addressLine
                        address.postalCode shouldBe fixture.hotel.postcode

                        val contactDetails = result.body.contactDetails.shouldNotBeNull()
                        contactDetails.phone shouldBe fixture.hotel.phone
                        contactDetails.hotelNationalPhone shouldBe fixture.hotel.phone
                    }

                    expect("returns slug-derived content links") {
                        result.body.links
                            .shouldNotBeNull()
                            .detailsPage shouldBe fixture.detailsPage
                    }
                }
            }

            scenario("GET /v1/content/hotels returns not found when slug is absent from directory") {
                val missingSlug = "/hotels/england/greater-london/london/not-in-directory.html"

                installStub(allHotels(hotels))

                val result =
                    contentEntityApi.getHotelBySlug(
                        slug = missingSlug,
                        testId = testId,
                    )

                result.attachEvidence("Get Hotel Information By Missing Slug")

                expect("returns the content slug not found error") {
                    val error = result.errorBody.shouldNotBeNull()

                    result.response.status.value shouldBe 404
                    error.errCode shouldBe 3
                    error.debugMessage shouldBe
                        "HotelDetails From Aem with hotelShortInformationDtoList=2, slug=$missingSlug not found"
                    error.globalErrTextTemplate shouldBe "internal.server.exception"
                }
            }
        },
    )

private data class ContentHotelFixture(
    val hotel: Hotel,
    val slug: String,
    val detailsPage: String,
)
