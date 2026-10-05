package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.clients.ohip.model.AvailabilityByIdsRequest
import uk.co.whitbread.integrationtests.clients.ohip.model.HotelAvailabilityResponse
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_COMPANY_PROFILE_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_MULTI_HOTEL_NEGOTIATED_AVAILABILITY_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.companyProfileWithoutProfileId
import uk.co.whitbread.integrationtests.stubs.opera.custom.multiHotelNegotiatedAvailabilityFailure
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.featureflags.FeatureFlag
import uk.co.whitbread.integrationtests.testkit.featureflags.OhipFeatureFlag
import uk.co.whitbread.integrationtests.testkit.mocks.OperaEndpoint
import uk.co.whitbread.integrationtests.testkit.mocks.Upstream
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.Hotel
import uk.co.whitbread.integrationtests.testkit.model.HotelInventoryItem
import uk.co.whitbread.integrationtests.testkit.model.HotelItemInventory
import uk.co.whitbread.integrationtests.testkit.model.HotelRoomType
import uk.co.whitbread.integrationtests.testkit.model.Rate
import uk.co.whitbread.integrationtests.testkit.presets.Companies
import uk.co.whitbread.integrationtests.testkit.presets.Hotels
import java.time.LocalDate

private val arrival: LocalDate = LocalDate.now().plusDays(14)
private val departure: LocalDate = arrival.plusDays(2)

private val distrFlagPins =
    mapOf<FeatureFlag, Boolean>(
        OhipFeatureFlag.AVAILABILITY_FROM_DIFFERENT_ROOM_CLASSES to false,
    )

/**
 * Journeys for `GET /ohip/hotels/availabilities/distr` (distribution multi-hotel
 * availability by company profile and/or rate plan codes).
 *
 * Flow doc: backend/integration-tests-kotlin/flows/ohip-adapter-service/GetHotelAvailabilityByIds.md
 *
 * `release_availability_from_different_room_classes` is pinned on every call and covered
 * in both states by the room-class scenarios. The opera token-service flags are
 * environment-pinned OFF and evaluated outside the request context, so they are never
 * overridden.
 */
class GetHotelAvailabilityByIdsSpec :
    JourneySpec(
        "Distribution availability by hotel ids can be fetched from OHIP adapter service",
        {
            val ohipApi = OhipApi()

            scenario("a public rate-plan-code search prices rooms across two hotels") {
                val booking = publicTwoHotelBooking()

                installFor(booking)

                val result =
                    ohipApi.getHotelAvailabilityByIds(
                        request = byIdsRequest(booking, ratePlanCodes = listOf("FLEXRATE")),
                        testId = testId,
                        featureFlagOverrides = distrFlagPins,
                    )

                result.attachEvidence("Get Availability By Ids Public Rate")

                expect("returns an available entry with the public rate for both hotels") {
                    result.response.status.value shouldBe 200
                    result.body.hotelAvailability shouldHaveSize 2
                    result.body.hotelAvailability.map { hotel -> hotel.hotelId } shouldContainExactlyInAnyOrder
                        booking.hotels.map { hotel -> hotel.hotelId }
                    result.body.hotelAvailability.forEach { hotel ->
                        hotel.available shouldBe true
                        val roomRate = hotel.roomRates.single()
                        roomRate.ratePlanCode shouldBe "FLEXRATE"
                        val room =
                            roomRate.roomTypes
                                .single()
                                .rooms
                                .single()
                        room.pmsRoomType shouldBe "DOUBLE"
                        room.roomPriceBreakdown
                            .shouldNotBeNull()
                            .totalNetAmount shouldBe 118.0
                    }
                }

                expect("reads room types, inventory, one search, and one price per hotel from Opera") {
                    // 2 roomTypes + 2 hotelInventory + 1 availability + 2 rateInfo
                    callCount(Upstream.OPERA) shouldBe 7
                    callCount(OperaEndpoint.GET_ROOM_TYPES) shouldBe 2
                    callCount(OperaEndpoint.GET_HOTEL_INVENTORY) shouldBe 2
                    callCount(OperaEndpoint.GET_MULTI_HOTEL_AVAILABILITY) shouldBe 1
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe 2
                    callCount(Upstream.CDH) shouldBe 0
                }
            }

            scenario("a public search returns every requested rate plan from a comma list") {
                val booking = publicTwoHotelMultiPlanBooking()

                installFor(booking)

                val result =
                    ohipApi.getHotelAvailabilityByIds(
                        request = byIdsRequest(booking, ratePlanCodes = listOf("FLEXRATE", "SEMIFLEX")),
                        testId = testId,
                        featureFlagOverrides = distrFlagPins,
                    )

                result.attachEvidence("Get Availability By Ids Multiple Public Rates")

                expect("returns both public rate plans for each hotel") {
                    result.response.status.value shouldBe 200
                    result.body.hotelAvailability shouldHaveSize 2
                    result.body.hotelAvailability.forEach { hotel ->
                        hotel.available shouldBe true
                        hotel.roomRates.map { roomRate -> roomRate.ratePlanCode } shouldContainExactlyInAnyOrder
                            listOf("FLEXRATE", "SEMIFLEX")
                    }
                }

                expect("prices both rate plans for each hotel") {
                    // 2 roomTypes + 2 hotelInventory + 1 availability + 4 rateInfo
                    callCount(Upstream.OPERA) shouldBe 9
                    callCount(OperaEndpoint.GET_ROOM_TYPES) shouldBe 2
                    callCount(OperaEndpoint.GET_HOTEL_INVENTORY) shouldBe 2
                    callCount(OperaEndpoint.GET_MULTI_HOTEL_AVAILABILITY) shouldBe 1
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe 4
                }
            }

            scenario("a company search returns the negotiated rate for every hotel") {
                val booking = negotiatedTwoHotelBooking()
                val company = booking.companies.single()

                installFor(booking)

                val result =
                    ohipApi.getHotelAvailabilityByIds(
                        request = byIdsRequest(booking, globalCompanyId = company.corpId),
                        testId = testId,
                        featureFlagOverrides = distrFlagPins,
                    )

                result.attachEvidence("Get Availability By Ids Negotiated")

                expect("returns the negotiated rate for both hotels") {
                    result.response.status.value shouldBe 200
                    result.body.hotelAvailability shouldHaveSize 2
                    result.body.hotelAvailability.forEach { hotel ->
                        hotel.available shouldBe true
                        hotel.roomRates.single().ratePlanCode shouldBe "CORPFLEX"
                    }
                }

                expect("resolves the Opera profile once and searches negotiated rates once") {
                    // 2 roomTypes + 2 hotelInventory + 1 company profile + 1 negotiated availability + 2 rateInfo
                    callCount(Upstream.OPERA) shouldBe 8
                    callCount(OperaEndpoint.GET_ROOM_TYPES) shouldBe 2
                    callCount(OperaEndpoint.GET_HOTEL_INVENTORY) shouldBe 2
                    callCount(OperaEndpoint.GET_COMPANY) shouldBe 1
                    callCount(OperaEndpoint.GET_MULTI_HOTEL_AVAILABILITY) shouldBe 1
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe 2
                }
            }

            scenario("a negotiated display-set filter keeps only rates in the requested sets") {
                val booking = displaySetFilterBooking()
                val company = booking.companies.single()

                installFor(booking)

                val result =
                    ohipApi.getHotelAvailabilityByIds(
                        request =
                            byIdsRequest(
                                booking,
                                globalCompanyId = company.corpId,
                                negotiatedRateDisplaySets = listOf("BMD"),
                            ),
                        testId = testId,
                        featureFlagOverrides = distrFlagPins,
                    )

                result.attachEvidence("Get Availability By Ids Display Set Filter")

                expect("keeps the BMD-classified negotiated rate and drops the other") {
                    result.response.status.value shouldBe 200
                    val hotel = result.body.hotelAvailability.single()
                    hotel.roomRates.map { roomRate -> roomRate.ratePlanCode } shouldBe listOf("CORPBMD")
                }

                expect("fetches the rate-plan classification catalogue for the hotel") {
                    // 1 roomTypes + 1 hotelInventory + 1 company profile + 1 negotiated availability
                    // + 1 ratePlans + 1 rateInfo
                    callCount(Upstream.OPERA) shouldBe 6
                    callCount(OperaEndpoint.GET_ROOM_TYPES) shouldBe 1
                    callCount(OperaEndpoint.GET_HOTEL_INVENTORY) shouldBe 1
                    callCount(OperaEndpoint.GET_COMPANY) shouldBe 1
                    callCount(OperaEndpoint.GET_MULTI_HOTEL_AVAILABILITY) shouldBe 1
                    callCount(OperaEndpoint.GET_RATE_PLANS) shouldBe 1
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe 1
                }
            }

            scenario("supplied pmsRoomTypes bypass room substitution and search directly") {
                val booking = publicSingleHotelBooking()

                installFor(booking)

                val result =
                    ohipApi.getHotelAvailabilityByIds(
                        request =
                            byIdsRequest(
                                booking,
                                ratePlanCodes = listOf("FLEXRATE"),
                                pmsRoomTypes = listOf("DOUBLE"),
                            ),
                        testId = testId,
                        featureFlagOverrides = distrFlagPins,
                    )

                result.attachEvidence("Get Availability By Ids Pms Room Types")

                expect("prices the caller-supplied PMS room type") {
                    result.response.status.value shouldBe 200
                    val hotel = result.body.hotelAvailability.single()
                    hotel.available shouldBe true
                    val room =
                        hotel.roomRates
                            .single()
                            .roomTypes
                            .single()
                            .rooms
                            .single()
                    room.pmsRoomType shouldBe "DOUBLE"
                }

                expect("performs the search without extra Opera calls") {
                    // 1 roomTypes + 1 hotelInventory + 1 availability + 1 rateInfo
                    callCount(Upstream.OPERA) shouldBe 4
                    callCount(OperaEndpoint.GET_ROOM_TYPES) shouldBe 1
                    callCount(OperaEndpoint.GET_HOTEL_INVENTORY) shouldBe 1
                    callCount(OperaEndpoint.GET_MULTI_HOTEL_AVAILABILITY) shouldBe 1
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe 1
                }
            }

            scenario("a cot request is confirmed against the hotel's item inventory") {
                val booking = publicSingleHotelBooking(withCots = true)

                installFor(booking)

                val result =
                    ohipApi.getHotelAvailabilityByIds(
                        request =
                            byIdsRequest(
                                booking,
                                ratePlanCodes = listOf("FLEXRATE"),
                                cotsRequired = listOf(true),
                            ),
                        testId = testId,
                        featureFlagOverrides = distrFlagPins,
                    )

                result.attachEvidence("Get Availability By Ids Cot Stock")

                expect("marks the picked room cot-available") {
                    result.response.status.value shouldBe 200
                    val roomType =
                        result.body.hotelAvailability
                            .single()
                            .roomRates
                            .single()
                            .roomTypes
                            .single()
                    roomType.cotRequested shouldBe true
                    roomType.rooms.single().cotAvailable shouldBe true
                }

                expect("checks cot stock through the item-inventory read") {
                    // 1 roomTypes + 1 hotelInventory + 1 availability + 1 rateInfo + 1 itemInventory
                    callCount(Upstream.OPERA) shouldBe 5
                    callCount(OperaEndpoint.GET_ROOM_TYPES) shouldBe 1
                    callCount(OperaEndpoint.GET_HOTEL_INVENTORY) shouldBe 1
                    callCount(OperaEndpoint.GET_MULTI_HOTEL_AVAILABILITY) shouldBe 1
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe 1
                    callCount(OperaEndpoint.GET_ITEM_INVENTORY) shouldBe 1
                }
            }

            scenario("cross-room-class options are kept when the room-classes flag is enabled") {
                val booking = roomClassBooking()

                installFor(booking)

                val result =
                    ohipApi.getHotelAvailabilityByIds(
                        request = byIdsRequest(booking, ratePlanCodes = listOf("FLEXRATE")),
                        testId = testId,
                        featureFlagOverrides =
                            mapOf(OhipFeatureFlag.AVAILABILITY_FROM_DIFFERENT_ROOM_CLASSES to true),
                    )

                result.attachEvidence("Get Availability By Ids Room Classes Flag On")

                expect("keeps the standard-class pick that only satisfies the first room") {
                    result.response.status.value shouldBe 200
                    val roomTypes = roomTypesOf(result.body.hotelAvailability.single())
                    roomTypes shouldHaveSize 2
                    roomTypes.first().rooms.map { room -> room.pmsRoomType } shouldContainExactlyInAnyOrder
                        listOf("DOUBLE", "PPLDBL")
                    roomTypes.last().rooms.map { room -> room.pmsRoomType } shouldBe listOf("PPLDBL")
                }
            }

            scenario("cross-room-class options are dropped when the room-classes flag is disabled") {
                val booking = roomClassBooking()

                installFor(booking)

                val result =
                    ohipApi.getHotelAvailabilityByIds(
                        request = byIdsRequest(booking, ratePlanCodes = listOf("FLEXRATE")),
                        testId = testId,
                        featureFlagOverrides =
                            mapOf(OhipFeatureFlag.AVAILABILITY_FROM_DIFFERENT_ROOM_CLASSES to false),
                    )

                result.attachEvidence("Get Availability By Ids Room Classes Flag Off")

                expect("removes the class that cannot satisfy every requested room") {
                    result.response.status.value shouldBe 200
                    val roomTypes = roomTypesOf(result.body.hotelAvailability.single())
                    roomTypes shouldHaveSize 2
                    roomTypes.forEach { roomType ->
                        roomType.rooms.map { room -> room.pmsRoomType } shouldBe listOf("PPLDBL")
                    }
                }
            }

            scenario("a company record without an Opera profile id rejects the search") {
                val booking = displaySetFilterBooking()
                val company = booking.companies.single()

                installFor(booking, excluded = setOf(OPERA_COMPANY_PROFILE_STUB_ID))
                installStub(companyProfileWithoutProfileId(company))

                val result =
                    ohipApi.getHotelAvailabilityByIds(
                        request = byIdsRequest(booking, globalCompanyId = company.corpId),
                        testId = testId,
                        featureFlagOverrides = distrFlagPins,
                    )

                result.attachEvidence("Get Availability By Ids No Profile Id")

                expect("maps the missing profile id to the no-profile error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 33
                }

                expect("never searched negotiated availability after the profile read") {
                    // 1 roomTypes + 1 company profile; the availability, rate-plans, and
                    // rate-info reads are never reached.
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_ROOM_TYPES) shouldBe 1
                    callCount(OperaEndpoint.GET_COMPANY) shouldBe 1
                }
            }

            scenario("a negotiated availability failure maps to the by-ids availability error") {
                val booking = displaySetFilterBooking()
                val company = booking.companies.single()

                installFor(booking, excluded = setOf(OPERA_MULTI_HOTEL_NEGOTIATED_AVAILABILITY_STUB_ID))
                installStub(multiHotelNegotiatedAvailabilityFailure(booking))

                val result =
                    ohipApi.getHotelAvailabilityByIds(
                        request = byIdsRequest(booking, globalCompanyId = company.corpId),
                        testId = testId,
                        featureFlagOverrides = distrFlagPins,
                    )

                result.attachEvidence("Get Availability By Ids Negotiated Failure")

                expect("maps the Opera failure to the by-ids availability error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 963
                }

                expect("stopped after the failed negotiated search") {
                    // 1 roomTypes + 1 company profile + 1 failed availability; the deferred
                    // hotel-inventory read is never subscribed once the search has failed.
                    callCount(Upstream.OPERA) shouldBe 3
                    callCount(OperaEndpoint.GET_ROOM_TYPES) shouldBe 1
                    callCount(OperaEndpoint.GET_COMPANY) shouldBe 1
                    callCount(OperaEndpoint.GET_MULTI_HOTEL_AVAILABILITY) shouldBe 1
                }
            }

            scenario("an unknown rate plan code leaves the hotel unavailable without pricing reads") {
                val booking = publicSingleHotelBooking(withCots = true)

                installFor(booking)

                val result =
                    ohipApi.getHotelAvailabilityByIds(
                        request =
                            byIdsRequest(
                                booking,
                                ratePlanCodes = listOf("NOSUCHPLAN"),
                                cotsRequired = listOf(true),
                            ),
                        testId = testId,
                        featureFlagOverrides = distrFlagPins,
                    )

                result.attachEvidence("Get Availability By Ids Unknown Rate Plan")

                expect("returns the hotel unavailable with no room rates") {
                    result.response.status.value shouldBe 200
                    val hotel = result.body.hotelAvailability.single()
                    hotel.available shouldBe false
                    hotel.roomRates shouldBe emptyList()
                }

                expect("never fetched price breakdowns or cot stock for the rate-less result") {
                    // 1 roomTypes + 1 hotelInventory + 1 availability (empty fallback);
                    // the installed rate-info and item-inventory stubs stay unhit.
                    callCount(Upstream.OPERA) shouldBe 3
                    callCount(OperaEndpoint.GET_ROOM_TYPES) shouldBe 1
                    callCount(OperaEndpoint.GET_HOTEL_INVENTORY) shouldBe 1
                    callCount(OperaEndpoint.GET_MULTI_HOTEL_AVAILABILITY) shouldBe 1
                }
            }
        },
    )

private fun roomTypesOf(hotel: HotelAvailabilityResponse) =
    hotel.roomRates
        .single()
        .roomTypes

/** ST-class-only room stock keeps the substitution class map deterministic for the spec. */
private fun standardRoomStock(doubleRooms: Int = 19): List<HotelRoomType> =
    listOf(
        HotelRoomType(roomClass = "ST", roomType = "DOUBLE", numberOfRooms = doubleRooms),
        HotelRoomType(roomClass = "ST", roomType = "TWINRM", numberOfRooms = 2),
    )

private fun publicRate(): Rate = Rate(ratePlan = "FLEXRATE", ratePlanSet = "PBF", roomType = "DOUBLE", adults = 2, nightlyRate = 59.0)

private fun publicTwoHotelBooking(): Booking =
    Booking(
        hotels =
            listOf(
                Hotels.HEAPTI.copy(availableRates = listOf(publicRate()), availableRoomTypes = standardRoomStock()),
                Hotels.FRAMTI.copy(availableRates = listOf(publicRate()), availableRoomTypes = standardRoomStock()),
            ),
        arrival = arrival,
        departure = departure,
        rooms = listOf(BookingRoom(roomType = "DB", adults = 2)),
    )

private fun publicTwoHotelMultiPlanBooking(): Booking =
    publicTwoHotelBooking().let { booking ->
        booking.copy(
            hotels =
                booking.hotels.map { hotel ->
                    hotel.copy(
                        availableRates =
                            hotel.availableRates +
                                Rate(
                                    ratePlan = "SEMIFLEX",
                                    ratePlanSet = "PBF",
                                    roomType = "DOUBLE",
                                    adults = 2,
                                    nightlyRate = 69.0,
                                ),
                    )
                },
        )
    }

private fun publicSingleHotelBooking(withCots: Boolean = false): Booking =
    Booking(
        hotels =
            listOf(
                Hotels.HEAPTI.copy(
                    availableRates = listOf(publicRate()),
                    availableRoomTypes = standardRoomStock(),
                    itemInventory =
                        if (withCots) {
                            HotelItemInventory(items = listOf(HotelInventoryItem(code = "COT", name = "Cot", total = 5)))
                        } else {
                            null
                        },
                ),
            ),
        arrival = arrival,
        departure = departure,
        rooms = listOf(BookingRoom(roomType = "DB", adults = 2)),
    )

private fun negotiatedRate(
    ratePlan: String,
    displaySet: String? = null,
): Rate =
    Rate(
        ratePlan = ratePlan,
        ratePlanSet = "NEGOTIATED",
        displaySet = displaySet,
        roomType = "DOUBLE",
        adults = 2,
        nightlyRate = 45.0,
    )

private fun negotiatedTwoHotelBooking(): Booking =
    publicTwoHotelBooking().let { booking ->
        booking.copy(
            companies = listOf(Companies.NEILL_TECHNICAL_SERVICES),
            hotels =
                booking.hotels.map { hotel ->
                    hotel.copy(availableRates = listOf(negotiatedRate("CORPFLEX")))
                },
        )
    }

private fun displaySetFilterBooking(): Booking =
    Booking(
        hotels =
            listOf(
                Hotels.HEAPTI.copy(
                    availableRates =
                        listOf(
                            negotiatedRate("CORPBMD", displaySet = "BMD"),
                            negotiatedRate("CORPOTH", displaySet = "OTH"),
                        ),
                    availableRoomTypes = standardRoomStock(),
                ),
            ),
        companies = listOf(Companies.NEILL_TECHNICAL_SERVICES),
        arrival = arrival,
        departure = departure,
        rooms = listOf(BookingRoom(roomType = "DB", adults = 2)),
    )

/** One standard-class room left plus premier stock: the second requested room can only pick PP. */
private fun roomClassBooking(): Booking =
    Booking(
        hotels =
            listOf(
                Hotels.HEAPTI.copy(
                    availableRates =
                        listOf(
                            publicRate(),
                            Rate(ratePlan = "FLEXRATE", ratePlanSet = "PBF", roomType = "PPLDBL", adults = 2, nightlyRate = 69.0),
                        ),
                    availableRoomTypes =
                        listOf(
                            HotelRoomType(roomClass = "ST", roomType = "DOUBLE", numberOfRooms = 1),
                            HotelRoomType(roomClass = "PP", roomType = "PPLDBL", numberOfRooms = 5),
                        ),
                ),
            ),
        arrival = arrival,
        departure = departure,
        rooms =
            listOf(
                BookingRoom(roomType = "DB", adults = 2),
                BookingRoom(roomType = "DB", adults = 2),
            ),
    )

private fun byIdsRequest(
    booking: Booking,
    ratePlanCodes: List<String> = emptyList(),
    globalCompanyId: String? = null,
    negotiatedRateDisplaySets: List<String> = emptyList(),
    pmsRoomTypes: List<String> = emptyList(),
    cotsRequired: List<Boolean> = List(booking.rooms.size) { false },
): AvailabilityByIdsRequest =
    AvailabilityByIdsRequest(
        hotelIds = booking.hotels.map(Hotel::hotelId),
        arrivalDate = booking.arrival!!,
        departureDate = booking.departure!!,
        roomTypes = booking.rooms.map { room -> requireNotNull(room.roomType) },
        adults = booking.rooms.map { room -> requireNotNull(room.adults) },
        children = booking.rooms.map { room -> room.children },
        cotsRequired = cotsRequired,
        channel = "PI",
        subchannel = "WEB",
        language = "EN",
        ratePlanCodes = ratePlanCodes,
        globalCompanyId = globalCompanyId,
        negotiatedRateDisplaySets = negotiatedRateDisplaySets,
        pmsRoomTypes = pmsRoomTypes,
    )
