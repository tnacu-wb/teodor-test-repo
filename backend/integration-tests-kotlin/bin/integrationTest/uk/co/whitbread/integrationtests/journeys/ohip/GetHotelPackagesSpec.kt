package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.Hotel
import uk.co.whitbread.integrationtests.testkit.model.PackageCatalogue
import uk.co.whitbread.integrationtests.testkit.model.PackageDefinition
import uk.co.whitbread.integrationtests.testkit.model.SelectedPackage
import uk.co.whitbread.integrationtests.testkit.presets.Hotels
import java.time.LocalDate

private val arrival: LocalDate = LocalDate.now().plusDays(14)
private val departure: LocalDate = arrival.plusDays(2)

class GetHotelPackagesSpec :
    JourneySpec(
        "Hotel packages can be fetched from OHIP adapter service",
        {
            val ohipApi = OhipApi()

            scenario("GET /ohip/hotels/HEAPTI/packages returns the default hotel package catalogue") {
                val hotel = Hotels.HEAPTI
                val booking = packageBooking(hotel)
                val expectedPackages = hotel.packageCatalogue.shouldNotBeNull().packages

                installFor(booking)

                val result =
                    ohipApi.getHotelPackages(
                        hotelId = hotel.hotelId,
                        startDate = arrival,
                        endDate = departure,
                        adults = 1,
                        children = 0,
                        testId = testId,
                    )

                result.attachEvidence("Get Hotel Packages HEAPTI")

                val meals =
                    result.body.packages
                        ?.meals
                        .orEmpty()
                val mealsById = meals.associateBy { it.id }

                expect("returns all configured HEAPTI package codes") {
                    result.response.status.value shouldBe 200
                    meals shouldHaveSize expectedPackages.size
                    mealsById.keys shouldBe expectedPackages.map { it.code }.toSet()
                }

                expect("maps representative HEAPTI package fields") {
                    val breakfast = mealsById["BFADBF"].shouldNotBeNull()
                    breakfast.title shouldBe "Premier Inn Breakfast Food VEN"
                    breakfast.price shouldBe 110.99
                    breakfast.currency shouldBe "GBP"

                    val earlyCheckIn = mealsById["HSCKIN"].shouldNotBeNull()
                    earlyCheckIn.inventoryItem shouldBe "ECI"
                }

                expect("reports restaurant and city-tax flags") {
                    result.body.restaurant?.restaurantNotFound shouldBe false
                    result.body.restaurant?.noMealsFound shouldBe false
                    result.body.hotelHasCityTaxForLeisure shouldBe false
                    result.body.hotelHasCityTaxForBusiness shouldBe false
                }
            }

            scenario("GET /ohip/hotels/FRAMTI/packages maps city-tax package flags") {
                val hotel = Hotels.FRAMTI
                val booking = packageBooking(hotel)
                val expectedPackages = hotel.packageCatalogue.shouldNotBeNull().packages

                installFor(booking)

                val result =
                    ohipApi.getHotelPackages(
                        hotelId = hotel.hotelId,
                        startDate = arrival,
                        endDate = departure,
                        adults = 1,
                        children = 0,
                        testId = testId,
                    )

                result.attachEvidence("Get Hotel Packages FRAMTI")

                val meals =
                    result.body.packages
                        ?.meals
                        .orEmpty()
                val mealsById = meals.associateBy { it.id }

                expect("returns all configured FRAMTI package codes") {
                    result.response.status.value shouldBe 200
                    meals shouldHaveSize expectedPackages.size
                    mealsById.keys shouldBe expectedPackages.map { it.code }.toSet()
                }

                expect("maps CITYTAX and city-tax flags") {
                    val cityTax = mealsById["CITYTAX"].shouldNotBeNull()
                    cityTax.title shouldBe "City Tax"
                    cityTax.price shouldBe 2.0
                    cityTax.currency shouldBe "EUR"
                    result.body.hotelHasCityTaxForLeisure shouldBe true
                    result.body.hotelHasCityTaxForBusiness shouldBe true
                }
            }

            scenario("GET /ohip/hotels/HEAPTI/packages returns no meals for an empty catalogue") {
                val hotel = Hotels.HEAPTI.copy(packageCatalogue = PackageCatalogue())
                val booking = packageBooking(hotel)

                installFor(booking)

                val result =
                    ohipApi.getHotelPackages(
                        hotelId = hotel.hotelId,
                        startDate = arrival,
                        endDate = departure,
                        adults = 1,
                        children = 0,
                        testId = testId,
                    )

                result.attachEvidence("Get Hotel Packages Empty")

                expect("returns an empty package list") {
                    result.response.status.value shouldBe 200
                    result.body.packages?.meals shouldBe emptyList()
                    result.body.restaurant?.restaurantNotFound shouldBe false
                    result.body.restaurant?.noMealsFound shouldBe true
                    result.body.hotelHasCityTaxForLeisure shouldBe false
                    result.body.hotelHasCityTaxForBusiness shouldBe false
                }
            }

            scenario("GET /ohip/hotels/HEAPTI/packages returns a custom package catalogue") {
                val customPackage =
                    PackageDefinition(
                        code = "CUSTOM1",
                        description = "Custom Breakfast Bundle",
                        shortDescription = "Custom Bundle",
                        price = 12.34,
                        currency = "GBP",
                        calculationRule = "PER_ROOM",
                        postingRhythm = "ARRIVAL_NIGHT",
                        inventoryArticleNumber = "CSTM",
                    )
                val hotel =
                    Hotels.HEAPTI.copy(
                        packageCatalogue = PackageCatalogue(packages = listOf(customPackage)),
                    )
                val booking = packageBooking(hotel)

                installFor(booking)

                val result =
                    ohipApi.getHotelPackages(
                        hotelId = hotel.hotelId,
                        startDate = arrival,
                        endDate = departure,
                        adults = 1,
                        children = 0,
                        testId = testId,
                    )

                result.attachEvidence("Get Hotel Packages Custom Catalogue")

                expect("returns only the custom package") {
                    result.response.status.value shouldBe 200
                    val meal =
                        result.body.packages
                            ?.meals
                            .orEmpty()
                            .single()
                    meal.id shouldBe customPackage.code
                    meal.title shouldBe customPackage.description
                    meal.price shouldBe customPackage.price
                    meal.currency shouldBe customPackage.currency
                    meal.inventoryItem shouldBe customPackage.inventoryArticleNumber
                }

                expect("keeps non-city-tax flags false") {
                    result.body.restaurant?.restaurantNotFound shouldBe false
                    result.body.restaurant?.noMealsFound shouldBe false
                    result.body.hotelHasCityTaxForLeisure shouldBe false
                    result.body.hotelHasCityTaxForBusiness shouldBe false
                }
            }

            scenario("GET /ohip/hotels/HEAPTI/packages supports multi-adult public requests") {
                val hotel = Hotels.HEAPTI
                val booking =
                    packageBooking(
                        hotel = hotel,
                        room = BookingRoom(roomType = "DOUBLE", adults = 2),
                    )
                val expectedCodes =
                    hotel.packageCatalogue
                        .shouldNotBeNull()
                        .packages
                        .map { it.code }
                        .toSet()

                installFor(booking)

                val result =
                    ohipApi.getHotelPackages(
                        hotelId = hotel.hotelId,
                        startDate = arrival,
                        endDate = departure,
                        adults = 2,
                        children = 0,
                        testId = testId,
                    )

                result.attachEvidence("Get Hotel Packages Multi Adult Request")

                expect("returns the hotel catalogue") {
                    result.response.status.value shouldBe 200
                    result.body.packages
                        ?.meals
                        .orEmpty()
                        .map { it.id }
                        .toSet() shouldBe expectedCodes
                }
            }

            scenario("GET /ohip/hotels/HEAPTI/packages ignores reservation-selected packages") {
                val hotel = Hotels.HEAPTI
                val booking =
                    packageBooking(
                        hotel = hotel,
                        room =
                            BookingRoom(
                                roomType = "DOUBLE",
                                adults = 1,
                                selectedPackages = listOf(SelectedPackage(code = "RESVONLY")),
                            ),
                    )
                val expectedCodes =
                    hotel.packageCatalogue
                        .shouldNotBeNull()
                        .packages
                        .map { it.code }
                        .toSet()

                installFor(booking)

                val result =
                    ohipApi.getHotelPackages(
                        hotelId = hotel.hotelId,
                        startDate = arrival,
                        endDate = departure,
                        adults = 1,
                        children = 0,
                        testId = testId,
                    )

                result.attachEvidence("Get Hotel Packages Ignores Selected Packages")

                expect("returns the hotel catalogue, not the room selected package list") {
                    result.response.status.value shouldBe 200
                    result.body.packages
                        ?.meals
                        .orEmpty()
                        .map { it.id }
                        .toSet() shouldBe expectedCodes
                }
            }
        },
    )

private fun packageBooking(
    hotel: Hotel,
    room: BookingRoom = BookingRoom(roomType = "DOUBLE", adults = 1),
): Booking =
    Booking(
        hotels = listOf(hotel),
        arrival = arrival,
        departure = departure,
        rooms = listOf(room),
    )
