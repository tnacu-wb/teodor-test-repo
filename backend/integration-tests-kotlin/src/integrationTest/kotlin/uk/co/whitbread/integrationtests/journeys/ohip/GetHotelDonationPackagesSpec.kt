package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.clients.ohip.model.HotelDonationPackage
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.Hotel
import uk.co.whitbread.integrationtests.testkit.model.PackageCatalogue
import uk.co.whitbread.integrationtests.testkit.model.PackageDefinition
import uk.co.whitbread.integrationtests.testkit.presets.Hotels

class GetHotelDonationPackagesSpec :
    JourneySpec(
        "Hotel donation packages can be fetched from OHIP adapter service",
        {
            val ohipApi = OhipApi()

            scenario("GET /ohip/hotels/HEAPTI/packages/donations returns default donation packages") {
                val hotel = Hotels.HEAPTI
                val expectedDonationPackages = hotel.packageCatalogue.shouldNotBeNull().donationPackages

                installFor(donationBooking(hotel))

                val result =
                    ohipApi.getHotelDonationPackages(
                        hotelId = hotel.hotelId,
                        packageCodes = expectedDonationPackages.map { it.code },
                        testId = testId,
                    )

                result.attachEvidence("Get Hotel Donation Packages HEAPTI")

                expect("returns mapped HEAPTI donation package details") {
                    result.response.status.value shouldBe 200
                    result.body.donationPackages shouldHaveSize expectedDonationPackages.size
                    result.body.donationPackages.associateBy { it.code } shouldBe
                        expectedDonationPackages.associate { donationPackage ->
                            donationPackage.code to donationPackage.toExpectedResponse()
                        }
                }
            }

            scenario("GET /ohip/hotels/FRAMTI/packages/donations returns default donation packages") {
                val hotel = Hotels.FRAMTI
                val expectedDonationPackages = hotel.packageCatalogue.shouldNotBeNull().donationPackages

                installFor(donationBooking(hotel))

                val result =
                    ohipApi.getHotelDonationPackages(
                        hotelId = hotel.hotelId,
                        packageCodes = expectedDonationPackages.map { it.code },
                        testId = testId,
                    )

                result.attachEvidence("Get Hotel Donation Packages FRAMTI")

                expect("returns mapped FRAMTI donation package details") {
                    result.response.status.value shouldBe 200
                    result.body.donationPackages shouldHaveSize expectedDonationPackages.size
                    result.body.donationPackages.associateBy { it.code } shouldBe
                        expectedDonationPackages.associate { donationPackage ->
                            donationPackage.code to donationPackage.toExpectedResponse()
                        }
                }
            }

            scenario("GET /ohip/hotels/HEAPTI/packages/donations returns a requested subset of default packages") {
                val hotel = Hotels.HEAPTI
                val requestedDonationPackages =
                    hotel.packageCatalogue
                        .shouldNotBeNull()
                        .donationPackages
                        .take(1)

                installFor(donationBooking(hotel))

                val result =
                    ohipApi.getHotelDonationPackages(
                        hotelId = hotel.hotelId,
                        packageCodes = requestedDonationPackages.map { it.code },
                        testId = testId,
                    )

                result.attachEvidence("Get Hotel Donation Packages Subset")

                expect("returns only the requested donation package details") {
                    result.response.status.value shouldBe 200
                    result.body.donationPackages shouldBe requestedDonationPackages.map { it.toExpectedResponse() }
                }
            }

            scenario("GET /ohip/hotels/HEAPTI/packages/donations returns empty response when no codes are supplied") {
                val result =
                    ohipApi.getHotelDonationPackages(
                        hotelId = Hotels.HEAPTI.hotelId,
                        packageCodes = emptyList(),
                        testId = testId,
                    )

                result.attachEvidence("Get Hotel Donation Packages Empty Codes")

                expect("does not require Opera package stubs") {
                    result.response.status.value shouldBe 200
                    result.body.donationPackages shouldBe emptyList()
                }
            }

            scenario("GET /ohip/hotels/HEAPTI/packages/donations returns custom donation packages") {
                val customDonationPackage =
                    PackageDefinition(
                        code = "ZCUS9",
                        description = "Custom Charity Pledge GBP 9.99",
                        price = 9.99,
                        currency = "GBP",
                        calculationRule = "FlatRate",
                        postingRhythm = "ArrivalNight",
                    )
                val hotel =
                    Hotels.HEAPTI.copy(
                        packageCatalogue =
                            PackageCatalogue(
                                donationPackages = listOf(customDonationPackage),
                            ),
                    )

                installFor(donationBooking(hotel))

                val result =
                    ohipApi.getHotelDonationPackages(
                        hotelId = hotel.hotelId,
                        packageCodes = listOf(customDonationPackage.code),
                        testId = testId,
                    )

                result.attachEvidence("Get Hotel Donation Packages Custom")

                expect("returns only the custom donation package") {
                    result.response.status.value shouldBe 200
                    result.body.donationPackages shouldBe listOf(customDonationPackage.toExpectedResponse())
                }
            }
        },
    )

private fun donationBooking(hotel: Hotel): Booking = Booking(hotels = listOf(hotel))

private fun PackageDefinition.toExpectedResponse() =
    HotelDonationPackage(
        code = code,
        unitPrice = price,
        currency = currency,
    )
