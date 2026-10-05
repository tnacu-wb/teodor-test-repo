package uk.co.whitbread.integrationtests.stubs.opera

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import uk.co.whitbread.integrationtests.framework.wiremock.model.StubMapping
import uk.co.whitbread.integrationtests.stubs.defaultStubsFor
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.Company
import uk.co.whitbread.integrationtests.testkit.model.CompanyAddress
import uk.co.whitbread.integrationtests.testkit.model.Hotel
import uk.co.whitbread.integrationtests.testkit.model.HotelRoomType
import uk.co.whitbread.integrationtests.testkit.model.Rate
import uk.co.whitbread.integrationtests.testkit.model.ReservationStatus
import uk.co.whitbread.integrationtests.testkit.model.RoomInventoryPeriod
import java.time.LocalDate

class OperaAvailabilityStubsTest :
    FunSpec({
        test("rate-plan-set and promotion mappings retain the single-room search contract") {
            val booking = reservationAvailabilityBooking()
            val mappings = hotelAvailability(booking).mappings
            val ratePlanSet = mappings.singleWithQueryValue("ratePlanSet", "BAR")
            val promotion = mappings.singleWithQueryValue("promotionCode", "SUMMER")

            listOf(ratePlanSet, promotion).forEach { mapping ->
                val query = requireNotNull(mapping.request.queryParameters)
                mapping.request.method shouldBe "GET"
                mapping.request.urlPath shouldBe "/par/v1/hotels/AVA001/availability"
                mapping.request.headers
                    ?.getValue("x-hotelid")
                    ?.equalTo shouldBe "AVA001"
                query.getValue("roomStayStartDate").equalTo shouldBe "2026-09-10"
                query.getValue("roomStayEndDate").equalTo shouldBe "2026-09-13"
                query.getValue("roomStayQuantity").equalTo shouldBe "1"
                query.getValue("limit").equalTo shouldBe "20"
                query.getValue("reservationGuestIdType").equalTo shouldBe "Profile"
                query["reservationGuestId"] shouldBe null
            }
            ratePlanSet.request.queryParameters!!
                .getValue("ratePlanSet")
                .equalTo shouldBe "BAR"
            promotion.request.queryParameters!!
                .getValue("promotionCode")
                .equalTo shouldBe "SUMMER"
        }

        test("single-company legacy mappings retain the exact company matcher") {
            val booking =
                reservationAvailabilityBooking().copy(
                    companies = listOf(availabilityCompany(corpId = "7101", companyId = "COMPANY-A")),
                )
            val mappings = hotelAvailability(booking).mappings

            mappings
                .singleWithQueryValue("ratePlanSet", "BAR")
                .request
                .queryParameters
                ?.getValue("reservationGuestId")
                ?.equalTo shouldBe "COMPANY-A"
            mappings
                .singleWithQueryValue("promotionCode", "SUMMER")
                .request
                .queryParameters
                ?.getValue("reservationGuestId")
                ?.equalTo shouldBe "COMPANY-A"
        }

        test("multi-company reservation availability scopes legacy mappings and keeps rate-code mappings neutral") {
            val booking =
                reservationAvailabilityBooking().copy(
                    companies =
                        listOf(
                            availabilityCompany(corpId = "7101", companyId = "COMPANY-A"),
                            availabilityCompany(corpId = "7102", companyId = "COMPANY-B"),
                            availabilityCompany(corpId = "7103", companyId = "COMPANY-B"),
                        ),
                    rooms =
                        listOf(
                            reservationAvailabilityBooking().room.copy(roomTypeAfterUpdate = "DOUBLE"),
                        ),
                )

            defaultStubsFor(booking).map { stub -> stub.id } shouldContain OPERA_AVAILABILITY_STUB_ID

            val mappings = hotelAvailability(booking).mappings
            val ratePlanSetCompanyIds =
                mappings
                    .filterWithQueryValue("ratePlanSet", "BAR")
                    .map { mapping ->
                        mapping.request.queryParameters
                            ?.getValue("reservationGuestId")
                            ?.equalTo
                    }
            val promotionCompanyIds =
                mappings
                    .filterWithQueryValue("promotionCode", "SUMMER")
                    .map { mapping ->
                        mapping.request.queryParameters
                            ?.getValue("reservationGuestId")
                            ?.equalTo
                    }
            val rateCodeMapping = mappings.singleWithRateCodeAndRoomType("FLEX", "DOUBLE")

            ratePlanSetCompanyIds shouldContainExactly listOf("COMPANY-A", "COMPANY-B")
            promotionCompanyIds shouldContainExactly listOf("COMPANY-A", "COMPANY-B")
            rateCodeMapping.request.queryParameters?.get("reservationGuestId") shouldBe null
        }

        test("rate-code mappings retain the positive quantity fallback without target room facts") {
            val mapping =
                hotelAvailability(reservationAvailabilityBooking())
                    .mappings
                    .singleWithRateCodeAndRoomType("FLEX", "DOUBLE")
            val query = requireNotNull(mapping.request.queryParameters)
            val roomRate = mapping.roomRates().single()
            val nightlyRates =
                roomRate
                    .getValue("rates")
                    .jsonObject
                    .getValue("rate")
                    .jsonArray

            query.keys.shouldContainExactly(
                setOf(
                    "roomStayStartDate",
                    "roomStayEndDate",
                    "roomStayQuantity",
                    "ratePlanCode",
                    "roomType",
                    "limit",
                    "reservationGuestIdType",
                ),
            )
            query.getValue("ratePlanCode").equalTo shouldBe "FLEX"
            query.getValue("roomType").equalTo shouldBe "DOUBLE"
            query.getValue("roomStayQuantity").matches shouldBe "^[1-9][0-9]*$"
            Regex(requireNotNull(query.getValue("roomStayQuantity").matches)).matches("12") shouldBe true
            Regex(requireNotNull(query.getValue("roomStayQuantity").matches)).matches("0") shouldBe false

            roomRate.getValue("ratePlanCode").jsonPrimitive.content shouldBe "FLEX"
            roomRate.getValue("roomType").jsonPrimitive.content shouldBe "DOUBLE"
            roomRate.getValue("numberOfUnits").jsonPrimitive.content shouldBe "4"
            roomRate
                .getValue("total")
                .jsonObject
                .getValue("amountBeforeTax")
                .jsonPrimitive.content shouldBe "180.0"
            nightlyRates
                .map { night ->
                    night.jsonObject
                        .getValue("start")
                        .jsonPrimitive.content
                }.shouldContainExactly("2026-09-10", "2026-09-11", "2026-09-12")
        }

        test("rate-code mappings require exact Booking-driven grouped target quantities") {
            val baseBooking = reservationAvailabilityBooking()
            val booking =
                baseBooking.copy(
                    rooms =
                        listOf(
                            baseBooking.room.copy(
                                reservationId = "RSV-AVA-1",
                                roomTypeAfterUpdate = "DOUBLE",
                            ),
                            baseBooking.room.copy(
                                reservationId = "RSV-AVA-2",
                                roomTypeAfterUpdate = "DOUBLE",
                            ),
                            baseBooking.room.copy(
                                reservationId = "RSV-AVA-3",
                                roomTypeAfterUpdate = "TWIN",
                            ),
                        ),
                )
            val mappings = hotelAvailability(booking).mappings
            val doubleQuantity =
                mappings
                    .singleWithRateCodeAndRoomType("FLEX", "DOUBLE")
                    .request
                    .queryParameters
                    ?.getValue("roomStayQuantity")
            val twinQuantity =
                mappings
                    .singleWithRateCodeAndRoomType("FLEX", "TWIN")
                    .request
                    .queryParameters
                    ?.getValue("roomStayQuantity")

            doubleQuantity?.equalTo shouldBe "2"
            doubleQuantity?.matches shouldBe null
            twinQuantity?.equalTo shouldBe "1"
            twinQuantity?.matches shouldBe null
        }

        test("a known target quantity at interval capacity returns its rates") {
            val booking =
                reservationAvailabilityBooking()
                    .withTargetRooms(roomType = "TWIN", quantity = 2)
            val mapping =
                hotelAvailability(booking)
                    .mappings
                    .singleWithRateCodeAndRoomType("FLEX", "TWIN")

            mapping.request.queryParameters
                ?.getValue("roomStayQuantity")
                ?.equalTo shouldBe "2"
            mapping
                .roomRates()
                .single()
                .getValue("numberOfUnits")
                .jsonPrimitive.content shouldBe "2"
        }

        test("a known target quantity above interval capacity returns empty room rates") {
            val booking =
                reservationAvailabilityBooking()
                    .withTargetRooms(roomType = "DOUBLE", quantity = 2)
                    .withRoomInventory(roomType = "DOUBLE", numberOfRooms = 1)

            hotelAvailability(booking)
                .mappings
                .singleWithRateCodeAndRoomType("FLEX", "DOUBLE")
                .roomRates()
                .shouldHaveSize(0)
        }

        test("a nightly inventory bottleneck makes a known target quantity unavailable") {
            val baseBooking = reservationAvailabilityBooking()
            val bottleneckDate = requireNotNull(baseBooking.arrival).plusDays(1)
            val booking =
                baseBooking
                    .withTargetRooms(roomType = "DOUBLE", quantity = 2)
                    .withRoomInventory(
                        roomType = "DOUBLE",
                        numberOfRooms = 4,
                        inventoryPeriods =
                            listOf(
                                RoomInventoryPeriod(
                                    startDate = bottleneckDate,
                                    endDate = bottleneckDate,
                                    numberOfRooms = 1,
                                ),
                            ),
                    )

            hotelAvailability(booking)
                .mappings
                .singleWithRateCodeAndRoomType("FLEX", "DOUBLE")
                .roomRates()
                .shouldHaveSize(0)
        }

        test("availability numberOfUnits uses the interval's minimum inventory") {
            val baseBooking = reservationAvailabilityBooking()
            val bottleneckDate = requireNotNull(baseBooking.arrival).plusDays(1)
            val booking =
                baseBooking.withRoomInventory(
                    roomType = "DOUBLE",
                    numberOfRooms = 4,
                    inventoryPeriods =
                        listOf(
                            RoomInventoryPeriod(
                                startDate = bottleneckDate,
                                endDate = bottleneckDate,
                                numberOfRooms = 2,
                            ),
                        ),
                )

            hotelAvailability(booking)
                .mappings
                .singleWithRateCodeAndRoomType("FLEX", "DOUBLE")
                .roomRates()
                .single()
                .getValue("numberOfUnits")
                .jsonPrimitive.content shouldBe "2"
        }

        test("sold-out room types are removed from fixed-one legacy availability") {
            val booking =
                reservationAvailabilityBooking()
                    .withRoomInventory(roomType = "DOUBLE", numberOfRooms = 0)
                    .withRoomInventory(roomType = "TWIN", numberOfRooms = 0)
            val mappings = hotelAvailability(booking).mappings

            mappings.singleWithQueryValue("ratePlanSet", "BAR").roomRates().shouldHaveSize(0)
            mappings.singleWithQueryValue("promotionCode", "SUMMER").roomRates().shouldHaveSize(0)
        }

        test("rate-code fallbacks return empty room rates for unavailable combinations") {
            val mappings = hotelAvailability(reservationAvailabilityBooking()).mappings
            val unavailableRoomType =
                mappings.single { mapping ->
                    val query = mapping.request.queryParameters.orEmpty()
                    query["ratePlanCode"]?.equalTo == "FLEX" && query["roomType"]?.matches != null
                }
            val unavailableRateCode =
                mappings.single { mapping ->
                    val query = mapping.request.queryParameters.orEmpty()
                    query["ratePlanCode"]?.matches != null && query["roomType"]?.matches == ".+"
                }

            Regex(requireNotNull(unavailableRoomType.request.queryParameters!!["roomType"]?.matches))
                .matches("SINGLE") shouldBe true
            Regex(requireNotNull(unavailableRoomType.request.queryParameters["roomType"]?.matches))
                .matches("DOUBLE") shouldBe false
            Regex(requireNotNull(unavailableRateCode.request.queryParameters!!["ratePlanCode"]?.matches))
                .matches("UNKNOWN") shouldBe true
            Regex(requireNotNull(unavailableRateCode.request.queryParameters["ratePlanCode"]?.matches))
                .matches("FLEX") shouldBe false
            unavailableRoomType.roomRates().shouldHaveSize(0)
            unavailableRateCode.roomRates().shouldHaveSize(0)
        }

        test("long stays install overlapping rate-code intervals with continuous nightly rates") {
            val arrival = LocalDate.of(2026, 9, 10)
            val departure = arrival.plusDays(120)
            val booking =
                reservationAvailabilityBooking().let { booking ->
                    booking.copy(
                        departure = departure,
                        rooms = booking.rooms.map { room -> room.copy(roomTypeAfterUpdate = "DOUBLE") },
                    )
                }
            val mappings =
                hotelAvailability(booking)
                    .mappings
                    .filterWithRateCodeAndRoomType("FLEX", "DOUBLE")

            mappings shouldHaveSize 2
            val firstQuery = requireNotNull(mappings[0].request.queryParameters)
            val secondQuery = requireNotNull(mappings[1].request.queryParameters)
            firstQuery.getValue("roomStayStartDate").equalTo shouldBe arrival.toString()
            firstQuery.getValue("roomStayEndDate").equalTo shouldBe arrival.plusDays(88).toString()
            secondQuery.getValue("roomStayStartDate").equalTo shouldBe arrival.plusDays(88).toString()
            secondQuery.getValue("roomStayEndDate").equalTo shouldBe departure.toString()
            firstQuery.getValue("roomStayQuantity").equalTo shouldBe "1"
            firstQuery.getValue("roomStayQuantity").matches shouldBe null
            secondQuery.getValue("roomStayQuantity").equalTo shouldBe "1"
            secondQuery.getValue("roomStayQuantity").matches shouldBe null

            val allNightlyDates =
                mappings.flatMap { mapping ->
                    mapping
                        .roomRates()
                        .single()
                        .getValue("rates")
                        .jsonObject
                        .getValue("rate")
                        .jsonArray
                        .map { night ->
                            night.jsonObject
                                .getValue("start")
                                .jsonPrimitive.content
                        }
                }
            val expectedDates =
                generateSequence(arrival) { date -> date.plusDays(1) }
                    .takeWhile { date -> date.isBefore(departure) }
                    .map(LocalDate::toString)
                    .toList()
            allNightlyDates shouldContainExactly expectedDates
        }

        test("reservation availability facts select GET, availability, and PUT defaults together") {
            val ids = defaultStubsFor(reservationAvailabilityBooking()).map { stub -> stub.id }
            val withoutRoomInventory =
                defaultStubsFor(
                    reservationAvailabilityBooking().let { booking ->
                        booking.copy(
                            hotels = listOf(booking.hotel.copy(availableRoomTypes = emptyList())),
                        )
                    },
                ).map { stub -> stub.id }

            ids shouldContain OPERA_RESERVATION_GET_STUB_ID
            ids shouldContain OPERA_AVAILABILITY_STUB_ID
            ids shouldContain OPERA_RESERVATION_PUT_STUB_ID
            withoutRoomInventory shouldContain OPERA_RESERVATION_GET_STUB_ID
            withoutRoomInventory shouldContain OPERA_RESERVATION_PUT_STUB_ID
            withoutRoomInventory shouldNotContain OPERA_AVAILABILITY_STUB_ID
        }

        test("reservation availability can use a rate code without a rate-plan set") {
            val booking =
                reservationAvailabilityBooking().let { booking ->
                    booking.copy(
                        hotels =
                            listOf(
                                booking.hotel.copy(
                                    availableRates = booking.hotel.availableRates.map { rate -> rate.copy(ratePlanSet = null) },
                                ),
                            ),
                    )
                }

            defaultStubsFor(booking).map { stub -> stub.id } shouldContain OPERA_AVAILABILITY_STUB_ID
            hotelAvailability(booking)
                .mappings
                .singleWithRateCodeAndRoomType("FLEX", "DOUBLE")
                .roomRates()
                .single()
                .getValue("ratePlanCode")
                .jsonPrimitive.content shouldBe "FLEX"
        }

        test("availability-search bookings still require a rate-plan set") {
            val booking =
                reservationAvailabilityBooking().let { reservationBooking ->
                    reservationBooking.copy(
                        hotels =
                            listOf(
                                reservationBooking.hotel.copy(
                                    availableRates =
                                        reservationBooking.hotel.availableRates
                                            .filter { rate -> rate.promotionCode == null }
                                            .map { rate -> rate.copy(ratePlanSet = null) },
                                ),
                            ),
                        rooms = reservationBooking.rooms.map { room -> room.copy(reservationId = null) },
                    )
                }

            shouldThrow<IllegalArgumentException> {
                defaultStubsFor(booking)
            }.message shouldBe "Availability rates must declare ratePlanSet: FLEX, FLEX"
        }
    })

private fun List<StubMapping>.singleWithQueryValue(
    name: String,
    value: String,
): StubMapping = filterWithQueryValue(name, value).single()

private fun List<StubMapping>.filterWithQueryValue(
    name: String,
    value: String,
): List<StubMapping> =
    filter { mapping ->
        mapping.request.queryParameters
            ?.get(name)
            ?.equalTo == value
    }

private fun List<StubMapping>.singleWithRateCodeAndRoomType(
    ratePlanCode: String,
    roomType: String,
): StubMapping = filterWithRateCodeAndRoomType(ratePlanCode, roomType).single()

private fun List<StubMapping>.filterWithRateCodeAndRoomType(
    ratePlanCode: String,
    roomType: String,
): List<StubMapping> =
    filter { mapping ->
        val query = mapping.request.queryParameters.orEmpty()
        query["ratePlanCode"]?.equalTo == ratePlanCode && query["roomType"]?.equalTo == roomType
    }

private fun StubMapping.roomRates(): List<JsonObject> =
    response.jsonBody!!
        .jsonObject
        .getValue("hotelAvailability")
        .jsonArray
        .single()
        .jsonObject
        .getValue("roomStays")
        .jsonArray
        .single()
        .jsonObject
        .getValue("roomRates")
        .jsonArray
        .map { roomRate -> roomRate.jsonObject }

private fun Booking.withTargetRooms(
    roomType: String,
    quantity: Int,
): Booking =
    copy(
        rooms =
            (1..quantity).map { index ->
                room.copy(
                    reservationId = "RSV-AVA-$index",
                    roomTypeAfterUpdate = roomType,
                )
            },
    )

private fun Booking.withRoomInventory(
    roomType: String,
    numberOfRooms: Int,
    inventoryPeriods: List<RoomInventoryPeriod> = emptyList(),
): Booking =
    copy(
        hotels =
            listOf(
                hotel.copy(
                    availableRoomTypes =
                        hotel.availableRoomTypes.map { configuredRoomType ->
                            if (configuredRoomType.roomType == roomType) {
                                configuredRoomType.copy(
                                    numberOfRooms = numberOfRooms,
                                    inventoryPeriods = inventoryPeriods,
                                )
                            } else {
                                configuredRoomType
                            }
                        },
                ),
            ),
    )

private fun reservationAvailabilityBooking(): Booking {
    val arrival = LocalDate.of(2026, 9, 10)
    return Booking(
        hotels =
            listOf(
                Hotel(
                    hotelId = "AVA001",
                    shortId = "availability-hotel",
                    name = "Availability Hotel",
                    addressLine = "1 Availability Street",
                    city = "London",
                    postcode = "SW1A 1AA",
                    phone = "02079460000",
                    availableRoomTypes =
                        listOf(
                            HotelRoomType(roomClass = "ST", roomType = "DOUBLE", numberOfRooms = 4),
                            HotelRoomType(roomClass = "ST", roomType = "TWIN", numberOfRooms = 2),
                        ),
                    availableRates =
                        listOf(
                            Rate(
                                ratePlan = "FLEX",
                                ratePlanSet = "BAR",
                                roomType = "DOUBLE",
                                adults = 2,
                                nightlyRate = 60.0,
                            ),
                            Rate(
                                ratePlan = "FLEX",
                                ratePlanSet = "BAR",
                                roomType = "TWIN",
                                adults = 2,
                                nightlyRate = 55.0,
                            ),
                            Rate(
                                ratePlan = "PROMO",
                                promotionCode = "SUMMER",
                                roomType = "DOUBLE",
                                adults = 2,
                                nightlyRate = 50.0,
                            ),
                        ),
                ),
            ),
        arrival = arrival,
        departure = arrival.plusDays(3),
        rooms =
            listOf(
                BookingRoom(
                    reservationId = "RSV-AVA-1",
                    roomType = "DOUBLE",
                    ratePlan = "FLEX",
                    adults = 2,
                    status = ReservationStatus.RESERVED,
                ),
            ),
    )
}

private fun availabilityCompany(
    corpId: String,
    companyId: String,
): Company =
    Company(
        name = "Availability Company $companyId",
        corpId = corpId,
        companyId = companyId,
        telephoneNumber = "02079460000",
        address = CompanyAddress(addressLine1 = "1 Availability Street", postalCode = "SW1A 1AA"),
    )
