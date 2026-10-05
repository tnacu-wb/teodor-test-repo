package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.clients.ohip.model.HotelAvailabilityRequest
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.featureflags.FeatureFlag
import uk.co.whitbread.integrationtests.testkit.featureflags.OhipFeatureFlag
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.HotelInventoryItem
import uk.co.whitbread.integrationtests.testkit.model.HotelItemInventory
import uk.co.whitbread.integrationtests.testkit.model.Rate
import uk.co.whitbread.integrationtests.testkit.presets.Companies
import uk.co.whitbread.integrationtests.testkit.presets.Hotels
import java.time.LocalDate

private val arrival: LocalDate = LocalDate.now().plusDays(14)
private val departure: LocalDate = arrival.plusDays(2)

private val availabilityFlagPins =
    mapOf<FeatureFlag, Boolean>(
        OhipFeatureFlag.AVAILABILITY_FROM_DIFFERENT_ROOM_CLASSES to false,
    )

class GetHotelAvailabilitySpec :
    JourneySpec(
        "Hotel availability can be fetched from OHIP adapter service",
        {
            val ohipApi = OhipApi()

            scenario("a leisure search returns an available public rate with its full stay price") {
                val booking = standardAvailabilityBooking()
                val requestedRoom = booking.room
                val expectedRate = booking.hotel.availableRates.single()

                installFor(booking)

                val result =
                    ohipApi.getHotelAvailability(
                        request = availabilityRequest(booking),
                        testId = testId,
                        featureFlagOverrides = availabilityFlagPins,
                    )

                result.attachEvidence("Get Hotel Availability Standard PI")

                expect("returns the requested hotel stay and availability flags") {
                    result.response.status.value shouldBe 200
                    result.body.hotelId shouldBe booking.hotel.hotelId
                    result.body.startDate shouldBe booking.arrival.toString()
                    result.body.endDate shouldBe booking.departure.toString()
                    result.body.available shouldBe true
                    result.body.limitedAvailability shouldBe false
                }

                val roomRate = result.body.roomRates.single()
                val roomType = roomRate.roomTypes.single()
                val room = roomType.rooms.single()
                val price = room.roomPriceBreakdown.shouldNotBeNull()

                expect("maps the standard rate and substituted Opera room") {
                    roomRate.ratePlanCode shouldBe expectedRate.ratePlan
                    roomRate.promotionCode.shouldBeNull()
                    roomType.roomType shouldBe requestedRoom.roomType
                    roomType.adults shouldBe requestedRoom.adults
                    roomType.children shouldBe requestedRoom.children
                    roomType.cotRequested shouldBe false
                    room.pmsRoomType shouldBe expectedRate.roomType
                    room.roomClass shouldBe "ST"
                    room.numberOfRoomsAvailable shouldBe 171
                    room.specialRequests shouldBe listOf("DBLE")
                }

                expect("returns a two-night price breakdown without optional enrichments") {
                    price.totalNetAmount shouldBe 118.0
                    price.totalGrossAmount shouldBe 98.34
                    price.totalTaxAmount shouldBe 19.66
                    price.effectiveRateAmount shouldBe 118.0
                    price.currencyCode shouldBe booking.hotel.currency
                    price.dailyPrices shouldHaveSize 2
                    price.dailyPrices.map { dailyPrice -> dailyPrice.netPrice } shouldBe listOf(59.0, 59.0)
                    price.dailyPrices.map { dailyPrice -> dailyPrice.effectiveRate } shouldBe listOf(59.0, 59.0)
                    price.baseRateAmount.shouldBeNull()
                    room.mealsIncluded.shouldBeNull()
                    room.cotAvailable shouldBe false
                }

                expect("includes the real Rules Agent substitution used for the rate") {
                    result.body.substitutionList.any { substitution -> substitution.type == expectedRate.roomType } shouldBe true
                }
            }

            scenario("one public rate can satisfy every room in a multi-room search") {
                val booking = multiRoomAvailabilityBooking()
                val expectedRate = booking.hotel.availableRates.single()

                installFor(booking)

                val result =
                    ohipApi.getHotelAvailability(
                        request = availabilityRequest(booking),
                        testId = testId,
                        featureFlagOverrides = availabilityFlagPins,
                    )

                result.attachEvidence("Get Hotel Availability Multiple Rooms")

                expect("returns one available option for every requested room") {
                    result.response.status.value shouldBe 200
                    val roomRate = result.body.roomRates.single()

                    roomRate.ratePlanCode shouldBe expectedRate.ratePlan
                    roomRate.roomTypes shouldHaveSize booking.rooms.size
                    roomRate.roomTypes.forEach { roomType ->
                        roomType.roomType shouldBe "DB"
                        roomType.adults shouldBe 2
                        roomType.children shouldBe 0
                        val room = roomType.rooms.single()
                        room.pmsRoomType shouldBe expectedRate.roomType
                        room.roomPriceBreakdown
                            .shouldNotBeNull()
                            .totalNetAmount shouldBe expectedRate.nightlyRate * 2
                    }
                }
            }

            scenario("a rate is excluded when its inventory cannot satisfy every requested room") {
                val booking = insufficientMultiRoomAvailabilityBooking()

                installFor(booking)

                val result =
                    ohipApi.getHotelAvailability(
                        request = availabilityRequest(booking),
                        testId = testId,
                        featureFlagOverrides = availabilityFlagPins,
                    )

                result.attachEvidence("Get Hotel Availability Insufficient Multi Room Inventory")

                expect("keeps the hotel available but removes the incomplete rate option") {
                    result.response.status.value shouldBe 200
                    result.body.hotelId shouldBe booking.hotel.hotelId
                    result.body.available shouldBe true
                    result.body.limitedAvailability shouldBe false
                    result.body.roomRates shouldBe emptyList()
                }
            }

            scenario("a twin-room search keeps every available twin substitution") {
                val booking = twinAvailabilityBooking()
                val expectedRate = booking.hotel.availableRates.first()

                installFor(booking)

                val result =
                    ohipApi.getHotelAvailability(
                        request = availabilityRequest(booking),
                        testId = testId,
                        featureFlagOverrides = availabilityFlagPins,
                    )

                result.attachEvidence("Get Hotel Availability Twin Alternatives")

                expect("keeps every available TWIN substitution for the public rate") {
                    result.response.status.value shouldBe 200
                    val roomRate = result.body.roomRates.single()
                    val roomType = roomRate.roomTypes.single()

                    roomRate.ratePlanCode shouldBe expectedRate.ratePlan
                    roomType.roomType shouldBe "TWIN"
                    roomType.rooms shouldHaveSize 2
                    roomType.rooms.map { room -> room.pmsRoomType } shouldContainExactlyInAnyOrder
                        listOf("TWINRM", "ZPLDBL")
                    roomType.rooms.forEach { room ->
                        room.specialRequests shouldBe listOf("TW2S")
                        room.roomPriceBreakdown
                            .shouldNotBeNull()
                            .totalNetAmount shouldBe expectedRate.nightlyRate * 2
                    }
                }
            }

            scenario("an accessible-room search keeps distinct low-bath and wet-room variants") {
                val booking = accessibleAvailabilityBooking()
                val expectedRate = booking.hotel.availableRates.first()

                installFor(booking)

                val result =
                    ohipApi.getHotelAvailability(
                        request = availabilityRequest(booking),
                        testId = testId,
                        featureFlagOverrides = availabilityFlagPins,
                    )

                result.attachEvidence("Get Hotel Availability Accessible Alternatives")

                expect("keeps the low-bath and wet-room variants for the public rate") {
                    result.response.status.value shouldBe 200
                    val roomRate = result.body.roomRates.single()
                    val roomType = roomRate.roomTypes.single()
                    val roomsByPmsType = roomType.rooms.associateBy { room -> room.pmsRoomType }

                    roomRate.ratePlanCode shouldBe expectedRate.ratePlan
                    roomType.roomType shouldBe "DIS"
                    roomType.rooms shouldHaveSize 2
                    roomsByPmsType.keys shouldContainExactlyInAnyOrder setOf("LOWDBL", "WETDBL")
                    roomsByPmsType.getValue("LOWDBL").specialRequests shouldBe listOf("DBLE", "LOWB")
                    roomsByPmsType.getValue("WETDBL").specialRequests shouldBe listOf("DBLE", "WETR")
                    roomType.rooms.forEach { room ->
                        room.roomPriceBreakdown
                            .shouldNotBeNull()
                            .totalNetAmount shouldBe expectedRate.nightlyRate * 2
                    }
                }
            }

            scenario("the hotel remains available when Opera has no matching rates") {
                val booking = emptyAvailabilityBooking()

                installFor(booking)

                val result =
                    ohipApi.getHotelAvailability(
                        request = availabilityRequest(booking),
                        testId = testId,
                        featureFlagOverrides = availabilityFlagPins,
                    )

                result.attachEvidence("Get Hotel Availability Empty")

                expect("returns an available hotel without room rates") {
                    result.response.status.value shouldBe 200
                    result.body.hotelId shouldBe booking.hotel.hotelId
                    result.body.available shouldBe true
                    result.body.limitedAvailability shouldBe false
                    result.body.roomRates shouldBe emptyList()
                }
            }

            scenario("the hotel is unavailable when house inventory is sold out") {
                val booking = soldOutAvailabilityBooking()

                installFor(booking)

                val result =
                    ohipApi.getHotelAvailability(
                        request = availabilityRequest(booking),
                        testId = testId,
                        featureFlagOverrides = availabilityFlagPins,
                    )

                result.attachEvidence("Get Hotel Availability Sold Out")

                expect("returns an unavailable hotel without room rates") {
                    result.response.status.value shouldBe 200
                    result.body.hotelId shouldBe booking.hotel.hotelId
                    result.body.available shouldBe false
                    result.body.limitedAvailability shouldBe false
                    result.body.roomRates shouldBe emptyList()
                }
            }

            scenario("a cot request is marked available when cot stock covers demand") {
                val booking = cotAvailabilityBooking()
                val expectedRate = booking.hotel.availableRates.single()

                installFor(booking)

                val result =
                    ohipApi.getHotelAvailability(
                        request = availabilityRequest(booking, cotsRequired = listOf(true)),
                        testId = testId,
                        featureFlagOverrides = availabilityFlagPins,
                    )

                result.attachEvidence("Get Hotel Availability With Cot")

                expect("marks the requested room as cot compatible") {
                    result.response.status.value shouldBe 200
                    val roomRate = result.body.roomRates.single()
                    val roomType = roomRate.roomTypes.single()
                    val room = roomType.rooms.single()

                    roomRate.ratePlanCode shouldBe expectedRate.ratePlan
                    roomType.cotRequested shouldBe true
                    room.cotAvailable shouldBe true
                    room.roomPriceBreakdown
                        .shouldNotBeNull()
                        .totalNetAmount shouldBe 118.0
                }

                expect("keeps the real Rules Agent substitution for the cot request") {
                    result.body.substitutionList.any { substitution -> substitution.type == expectedRate.roomType } shouldBe true
                }
            }

            scenario("a cot request is unavailable when cot stock is exhausted") {
                val booking = cotAvailabilityBooking(availableCots = 0)
                val expectedRate = booking.hotel.availableRates.single()

                installFor(booking)

                val result =
                    ohipApi.getHotelAvailability(
                        request = availabilityRequest(booking, cotsRequired = listOf(true)),
                        testId = testId,
                        featureFlagOverrides = availabilityFlagPins,
                    )

                result.attachEvidence("Get Hotel Availability Without Cot Inventory")

                expect("keeps the available room but marks the requested cot as unavailable") {
                    result.response.status.value shouldBe 200
                    val roomRate = result.body.roomRates.single()
                    val roomType = roomRate.roomTypes.single()
                    val room = roomType.rooms.single()

                    roomRate.ratePlanCode shouldBe expectedRate.ratePlan
                    roomType.cotRequested shouldBe true
                    room.cotAvailable shouldBe false
                    room.roomPriceBreakdown
                        .shouldNotBeNull()
                        .totalNetAmount shouldBe 118.0
                }
            }

            scenario("cot availability considers total demand across every requested room") {
                val booking = insufficientMultiRoomCotAvailabilityBooking()
                val expectedRate = booking.hotel.availableRates.single()

                installFor(booking)

                val result =
                    ohipApi.getHotelAvailability(
                        request = availabilityRequest(booking, cotsRequired = listOf(true, true)),
                        testId = testId,
                        featureFlagOverrides = availabilityFlagPins,
                    )

                result.attachEvidence("Get Hotel Availability Insufficient Cots For Multiple Rooms")

                expect("keeps both rooms but marks their cots unavailable when one cot cannot cover demand") {
                    result.response.status.value shouldBe 200
                    val roomRate = result.body.roomRates.single()

                    roomRate.ratePlanCode shouldBe expectedRate.ratePlan
                    roomRate.roomTypes shouldHaveSize booking.rooms.size
                    roomRate.roomTypes.forEach { roomType ->
                        roomType.cotRequested shouldBe true
                        val room = roomType.rooms.single()
                        room.cotAvailable shouldBe false
                        room.roomPriceBreakdown
                            .shouldNotBeNull()
                            .totalNetAmount shouldBe expectedRate.nightlyRate * 2
                    }
                }
            }

            scenario("a cheaper promotion includes the original public price for comparison") {
                val booking = promotionalAvailabilityBooking()
                val promotionalRate = booking.hotel.availableRates.single { rate -> rate.promotionCode != null }
                val baseRate = booking.hotel.availableRates.single { rate -> rate.ratePlan == promotionalRate.dynamicBaseRatePlan }

                installFor(booking)

                val result =
                    ohipApi.getHotelAvailability(
                        request = availabilityRequest(booking, promotionCode = promotionalRate.promotionCode),
                        testId = testId,
                        featureFlagOverrides = availabilityFlagPins,
                    )

                result.attachEvidence("Get Hotel Availability Promotion")

                expect("returns only the cheaper promotional rate") {
                    result.response.status.value shouldBe 200
                    val roomRate = result.body.roomRates.single()
                    val room =
                        roomRate
                            .roomTypes
                            .single()
                            .rooms
                            .single()
                    val price = room.roomPriceBreakdown.shouldNotBeNull()

                    roomRate.ratePlanCode shouldBe promotionalRate.ratePlan
                    roomRate.promotionCode shouldBe promotionalRate.promotionCode
                    price.totalNetAmount shouldBe 98.0
                    price.baseRateAmount shouldBe baseRate.nightlyRate * 2
                    price.dailyPrices.map { dailyPrice -> dailyPrice.netPrice } shouldBe listOf(49.0, 49.0)
                }
            }

            scenario("a standalone promotion remains available alongside the public rate") {
                val booking = standalonePromotionalAvailabilityBooking()
                val promotionalRate = booking.hotel.availableRates.single { rate -> rate.promotionCode != null }
                val publicRate = booking.hotel.availableRates.single { rate -> rate.promotionCode == null }

                installFor(booking)

                val result =
                    ohipApi.getHotelAvailability(
                        request = availabilityRequest(booking, promotionCode = promotionalRate.promotionCode),
                        testId = testId,
                        featureFlagOverrides = availabilityFlagPins,
                    )

                result.attachEvidence("Get Hotel Availability Standalone Promotion")

                expect("returns the promotion and public rate as separate options") {
                    result.response.status.value shouldBe 200
                    result.body.roomRates shouldHaveSize 2

                    val promotionalRoomRate =
                        result.body.roomRates.single { roomRate -> roomRate.ratePlanCode == promotionalRate.ratePlan }
                    val promotionalPrice =
                        promotionalRoomRate
                            .roomTypes
                            .single()
                            .rooms
                            .single()
                            .roomPriceBreakdown
                            .shouldNotBeNull()
                    val publicPrice =
                        result.body.roomRates
                            .single { roomRate -> roomRate.ratePlanCode == publicRate.ratePlan }
                            .roomTypes
                            .single()
                            .rooms
                            .single()
                            .roomPriceBreakdown
                            .shouldNotBeNull()

                    promotionalRoomRate.promotionCode shouldBe promotionalRate.promotionCode
                    promotionalPrice.totalNetAmount shouldBe promotionalRate.nightlyRate * 2
                    promotionalPrice.baseRateAmount.shouldBeNull()
                    publicPrice.totalNetAmount shouldBe publicRate.nightlyRate * 2
                    publicPrice.baseRateAmount.shouldBeNull()
                }
            }

            scenario("an unavailable promotion falls back to the unaffected public rate") {
                val booking = standardAvailabilityBooking()
                val publicRate = booking.hotel.availableRates.single()

                installFor(booking)

                val result =
                    ohipApi.getHotelAvailability(
                        request = availabilityRequest(booking, promotionCode = "SOLDOUT20"),
                        testId = testId,
                        featureFlagOverrides = availabilityFlagPins,
                    )

                result.attachEvidence("Get Hotel Availability Unavailable Promotion")

                expect("returns only the unaffected public rate") {
                    result.response.status.value shouldBe 200
                    val roomRate = result.body.roomRates.single()
                    val price =
                        roomRate
                            .roomTypes
                            .single()
                            .rooms
                            .single()
                            .roomPriceBreakdown
                            .shouldNotBeNull()

                    roomRate.ratePlanCode shouldBe publicRate.ratePlan
                    roomRate.promotionCode.shouldBeNull()
                    price.totalNetAmount shouldBe publicRate.nightlyRate * 2
                    price.baseRateAmount.shouldBeNull()
                }
            }

            scenario("a cheaper Business Booker flex rate includes its public comparison price") {
                val booking = businessBookerAvailabilityBooking()
                val businessRate = booking.hotel.availableRates.single { rate -> rate.ratePlan == "BUSIFLEX" }
                val baseRate = booking.hotel.availableRates.single { rate -> rate.ratePlan == "FLEXRATE" }

                installFor(booking)

                val result =
                    ohipApi.getHotelAvailability(
                        request = availabilityRequest(booking, channel = "BB"),
                        testId = testId,
                        featureFlagOverrides =
                            availabilityFlagPins +
                                mapOf(OhipFeatureFlag.BB_FLEX_RATE_STRIKETHROUGH to true),
                    )

                result.attachEvidence("Get Hotel Availability Business Booker")

                expect("adds the Rules Agent base price to the cheaper BB flex rate") {
                    result.response.status.value shouldBe 200
                    result.body.roomRates shouldHaveSize 2
                    val businessRoom =
                        result.body.roomRates
                            .single { roomRate -> roomRate.ratePlanCode == businessRate.ratePlan }
                            .roomTypes
                            .single()
                            .rooms
                            .single()
                    val businessPrice = businessRoom.roomPriceBreakdown.shouldNotBeNull()

                    businessPrice.totalNetAmount shouldBe 98.0
                    businessPrice.baseRateAmount shouldBe baseRate.nightlyRate * 2
                    businessPrice.dailyPrices.map { dailyPrice -> dailyPrice.netPrice } shouldBe listOf(49.0, 49.0)
                }

                expect("keeps the standard base rate as a separate option") {
                    val basePrice =
                        result.body.roomRates
                            .single { roomRate -> roomRate.ratePlanCode == baseRate.ratePlan }
                            .roomTypes
                            .single()
                            .rooms
                            .single()
                            .roomPriceBreakdown
                            .shouldNotBeNull()

                    basePrice.totalNetAmount shouldBe 118.0
                    basePrice.baseRateAmount.shouldBeNull()
                }
            }

            scenario("a disabled BB flex strikethrough flag omits the public comparison price") {
                val booking = businessBookerAvailabilityBooking()
                val businessRate = booking.hotel.availableRates.single { rate -> rate.ratePlan == "BUSIFLEX" }

                installFor(booking)

                val result =
                    ohipApi.getHotelAvailability(
                        request = availabilityRequest(booking, channel = "BB"),
                        testId = testId,
                        featureFlagOverrides =
                            availabilityFlagPins +
                                mapOf(OhipFeatureFlag.BB_FLEX_RATE_STRIKETHROUGH to false),
                    )

                result.attachEvidence("Get Hotel Availability Business Booker Override Off")

                expect("does not add the Rules Agent base price to the BB flex rate") {
                    result.response.status.value shouldBe 200
                    result.body.roomRates shouldHaveSize 2
                    val businessPrice =
                        result.body.roomRates
                            .single { roomRate -> roomRate.ratePlanCode == businessRate.ratePlan }
                            .roomTypes
                            .single()
                            .rooms
                            .single()
                            .roomPriceBreakdown
                            .shouldNotBeNull()

                    businessPrice.totalNetAmount shouldBe 98.0
                    businessPrice.baseRateAmount.shouldBeNull()
                }
            }

            scenario("a company search returns its negotiated rate alongside public availability") {
                val booking = negotiatedAvailabilityBooking()
                val publicRate = booking.hotel.availableRates.single { rate -> rate.ratePlanSet == "PBF" }
                val negotiatedRate = booking.hotel.availableRates.single { rate -> rate.ratePlanSet == "NEGOTIATED" }

                installFor(booking)

                val result =
                    ohipApi.getHotelAvailability(
                        request = availabilityRequest(booking),
                        testId = testId,
                        featureFlagOverrides = availabilityFlagPins,
                    )

                result.attachEvidence("Get Hotel Availability Negotiated Company Rate")

                expect("returns both the public and company negotiated options") {
                    result.response.status.value shouldBe 200
                    result.body.roomRates shouldHaveSize 2

                    val publicPrice =
                        result.body.roomRates
                            .single { roomRate -> roomRate.ratePlanCode == publicRate.ratePlan }
                            .roomTypes
                            .single()
                            .rooms
                            .single()
                            .roomPriceBreakdown
                            .shouldNotBeNull()
                    val negotiatedPrice =
                        result.body.roomRates
                            .single { roomRate -> roomRate.ratePlanCode == negotiatedRate.ratePlan }
                            .roomTypes
                            .single()
                            .rooms
                            .single()
                            .roomPriceBreakdown
                            .shouldNotBeNull()

                    publicPrice.totalNetAmount shouldBe publicRate.nightlyRate * 2
                    negotiatedPrice.totalNetAmount shouldBe negotiatedRate.nightlyRate * 2
                }
            }

            scenario("a company search falls back to public availability when no negotiated rate exists") {
                val booking = publicCompanyAvailabilityBooking()
                val publicRate = booking.hotel.availableRates.single()

                installFor(booking)

                val result =
                    ohipApi.getHotelAvailability(
                        request = availabilityRequest(booking),
                        testId = testId,
                        featureFlagOverrides = availabilityFlagPins,
                    )

                result.attachEvidence("Get Hotel Availability Company Without Negotiated Rate")

                expect("returns the unaffected public option when the company lookup has no rates") {
                    result.response.status.value shouldBe 200
                    val roomRate = result.body.roomRates.single()
                    val price =
                        roomRate
                            .roomTypes
                            .single()
                            .rooms
                            .single()
                            .roomPriceBreakdown
                            .shouldNotBeNull()

                    roomRate.ratePlanCode shouldBe publicRate.ratePlan
                    roomRate.promotionCode.shouldBeNull()
                    price.totalNetAmount shouldBe publicRate.nightlyRate * 2
                    price.baseRateAmount.shouldBeNull()
                }
            }
        },
    )

private fun availabilityRequest(
    booking: Booking,
    cotsRequired: List<Boolean> = List(booking.rooms.size) { false },
    promotionCode: String? = null,
    channel: String = "PI",
): HotelAvailabilityRequest {
    require(cotsRequired.size == booking.rooms.size) {
        "cotsRequired must contain one value for every requested room"
    }
    return HotelAvailabilityRequest(
        hotelId = booking.hotel.hotelId,
        arrivalDate = booking.arrival!!,
        departureDate = booking.departure!!,
        roomTypes = booking.rooms.map { room -> requireNotNull(room.roomType) },
        adults = booking.rooms.map { room -> requireNotNull(room.adults) },
        children = booking.rooms.map { room -> room.children },
        cotsRequired = cotsRequired,
        channel = channel,
        subchannel = "WEB",
        language = "EN",
        companyId = booking.companies.singleOrNull()?.companyId,
        promotionCode = promotionCode,
    )
}

private fun standardAvailabilityBooking(): Booking {
    val rate =
        Rate(
            ratePlan = "FLEXRATE",
            ratePlanSet = "PBF",
            roomType = "DOUBLE",
            adults = 2,
            nightlyRate = 59.0,
        )
    return Booking(
        hotels = listOf(Hotels.HEAPTI.copy(availableRates = listOf(rate))),
        arrival = arrival,
        departure = departure,
        rooms =
            listOf(
                BookingRoom(
                    roomType = "DB",
                    adults = 2,
                ),
            ),
    )
}

private fun multiRoomAvailabilityBooking(): Booking =
    standardAvailabilityBooking().copy(
        rooms =
            listOf(
                BookingRoom(roomType = "DB", adults = 2),
                BookingRoom(roomType = "DB", adults = 2),
            ),
    )

private fun insufficientMultiRoomAvailabilityBooking(): Booking =
    multiRoomAvailabilityBooking().let { booking ->
        booking.copy(
            hotels =
                listOf(
                    booking.hotel.copy(
                        availableRoomTypes =
                            booking.hotel.availableRoomTypes.map { roomType ->
                                if (roomType.roomType == "DOUBLE") {
                                    roomType.copy(numberOfRooms = 1)
                                } else {
                                    roomType
                                }
                            },
                    ),
                ),
        )
    }

private fun twinAvailabilityBooking(): Booking {
    val twinRates =
        listOf("TWINRM", "ZPLDBL").map { operaRoomType ->
            Rate(
                ratePlan = "FLEXRATE",
                ratePlanSet = "PBF",
                roomType = operaRoomType,
                adults = 2,
                nightlyRate = 59.0,
            )
        }
    return standardAvailabilityBooking().let { booking ->
        booking.copy(
            hotels = listOf(booking.hotel.copy(availableRates = twinRates)),
            rooms = listOf(BookingRoom(roomType = "TWIN", adults = 2)),
        )
    }
}

private fun accessibleAvailabilityBooking(): Booking {
    val accessibleRates =
        listOf("LOWDBL", "WETDBL").map { operaRoomType ->
            Rate(
                ratePlan = "FLEXRATE",
                ratePlanSet = "PBF",
                roomType = operaRoomType,
                adults = 2,
                nightlyRate = 59.0,
            )
        }
    return standardAvailabilityBooking().let { booking ->
        booking.copy(
            hotels =
                listOf(
                    booking.hotel.copy(
                        availableRates = accessibleRates,
                        availableRoomTypes =
                            booking.hotel.availableRoomTypes.map { roomType ->
                                if (roomType.roomType == "WETDBL") {
                                    roomType.copy(numberOfRooms = 3)
                                } else {
                                    roomType
                                }
                            },
                    ),
                ),
            rooms = listOf(BookingRoom(roomType = "DIS", adults = 2)),
        )
    }
}

private fun emptyAvailabilityBooking(): Booking =
    standardAvailabilityBooking().let { booking ->
        booking.copy(
            hotels = listOf(booking.hotel.copy(availableRates = emptyList())),
        )
    }

private fun soldOutAvailabilityBooking(): Booking =
    standardAvailabilityBooking().let { booking ->
        booking.copy(
            hotels =
                listOf(
                    booking.hotel.copy(
                        availableRoomTypes =
                            booking.hotel.availableRoomTypes.map { roomType ->
                                roomType.copy(numberOfRooms = 0)
                            },
                    ),
                ),
        )
    }

private fun cotAvailabilityBooking(availableCots: Int = 2): Booking = standardAvailabilityBooking().withCotInventory(availableCots)

private fun insufficientMultiRoomCotAvailabilityBooking(): Booking = multiRoomAvailabilityBooking().withCotInventory(availableCots = 1)

private fun Booking.withCotInventory(availableCots: Int): Booking =
    copy(
        hotels =
            listOf(
                hotel.copy(
                    itemInventory =
                        HotelItemInventory(
                            items =
                                listOf(
                                    HotelInventoryItem(
                                        code = "COT",
                                        name = "Cot",
                                        total = 5,
                                        available = availableCots,
                                    ),
                                ),
                        ),
                ),
            ),
    )

private fun promotionalAvailabilityBooking(): Booking {
    val baseRate =
        Rate(
            ratePlan = "FLEXRATE",
            ratePlanSet = "PBF",
            roomType = "DOUBLE",
            adults = 2,
            nightlyRate = 59.0,
        )
    val promotionalRate =
        Rate(
            ratePlan = "PROMOFLEX",
            promotionCode = "SUMMER20",
            dynamicBaseRatePlan = baseRate.ratePlan,
            roomType = "DOUBLE",
            adults = 2,
            nightlyRate = 49.0,
        )
    return standardAvailabilityBooking().let { booking ->
        booking.copy(
            hotels = listOf(booking.hotel.copy(availableRates = listOf(baseRate, promotionalRate))),
        )
    }
}

private fun standalonePromotionalAvailabilityBooking(): Booking {
    val baseRate =
        Rate(
            ratePlan = "FLEXRATE",
            ratePlanSet = "PBF",
            roomType = "DOUBLE",
            adults = 2,
            nightlyRate = 59.0,
        )
    val promotionalRate =
        Rate(
            ratePlan = "PROMOSAVER",
            promotionCode = "SAVE20",
            roomType = "DOUBLE",
            adults = 2,
            nightlyRate = 49.0,
        )
    return standardAvailabilityBooking().let { booking ->
        booking.copy(
            hotels = listOf(booking.hotel.copy(availableRates = listOf(baseRate, promotionalRate))),
        )
    }
}

private fun businessBookerAvailabilityBooking(): Booking {
    val baseRate =
        Rate(
            ratePlan = "FLEXRATE",
            ratePlanSet = "PBF",
            roomType = "DOUBLE",
            adults = 2,
            nightlyRate = 59.0,
        )
    val businessRate =
        Rate(
            ratePlan = "BUSIFLEX",
            ratePlanSet = "BFL",
            roomType = "DOUBLE",
            adults = 2,
            nightlyRate = 49.0,
        )
    return standardAvailabilityBooking().let { booking ->
        booking.copy(
            hotels = listOf(booking.hotel.copy(availableRates = listOf(baseRate, businessRate))),
        )
    }
}

private fun publicCompanyAvailabilityBooking(): Booking =
    standardAvailabilityBooking().copy(
        companies = listOf(Companies.NEILL_TECHNICAL_SERVICES),
    )

private fun negotiatedAvailabilityBooking(): Booking {
    val negotiatedRate =
        Rate(
            ratePlan = "CORPFLEX",
            ratePlanSet = "NEGOTIATED",
            roomType = "DOUBLE",
            adults = 2,
            nightlyRate = 45.0,
        )
    return publicCompanyAvailabilityBooking().let { booking ->
        booking.copy(
            hotels = listOf(booking.hotel.copy(availableRates = booking.hotel.availableRates + negotiatedRate)),
        )
    }
}
