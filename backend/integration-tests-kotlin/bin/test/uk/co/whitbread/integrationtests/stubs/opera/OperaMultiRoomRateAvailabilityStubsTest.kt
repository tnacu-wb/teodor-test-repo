package uk.co.whitbread.integrationtests.stubs.opera

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import uk.co.whitbread.integrationtests.framework.wiremock.model.StubMapping
import uk.co.whitbread.integrationtests.stubs.defaultStubsFor
import uk.co.whitbread.integrationtests.stubs.opera.custom.multiRoomRateAvailabilityEmptyForRoomType
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.Hotel
import uk.co.whitbread.integrationtests.testkit.model.HotelRoomType
import uk.co.whitbread.integrationtests.testkit.model.Rate
import java.time.LocalDate

class OperaMultiRoomRateAvailabilityStubsTest :
    FunSpec({
        test("configured room-type combinations create mutually exclusive mappings and a fallback") {
            val mappings = multiRoomRateAvailability(multiRoomRateBooking()).mappings

            mappings shouldHaveSize 4

            val doubleOnly =
                mappings.single { mapping ->
                    requiredRoomTags(mapping) == setOf("DOUBLE")
                }
            excludedRoomTags(doubleOnly) shouldBe setOf("TWINRM")
            roomTypesInResponse(doubleOnly) shouldBe listOf("DOUBLE")

            val twinOnly =
                mappings.single { mapping ->
                    requiredRoomTags(mapping) == setOf("TWINRM")
                }
            excludedRoomTags(twinOnly) shouldBe setOf("DOUBLE")
            roomTypesInResponse(twinOnly) shouldBe listOf("TWINRM")

            val combined =
                mappings.single { mapping ->
                    requiredRoomTags(mapping) == setOf("DOUBLE", "TWINRM")
                }
            excludedRoomTags(combined) shouldBe emptySet()
            roomTypesInResponse(combined) shouldBe listOf("DOUBLE", "TWINRM")

            val fallback = mappings.single { mapping -> requiredRoomTags(mapping).isEmpty() }
            excludedRoomTags(fallback) shouldBe setOf("DOUBLE", "TWINRM")
            hotelIdsInResponse(fallback) shouldBe emptyList()
        }

        test("every room-type mapping keeps the common request and hotel-batch constraints") {
            val booking = multiRoomRateBooking()
            val mappings = multiRoomRateAvailability(booking).mappings

            mappings shouldHaveSize 4
            mappings.forEach { mapping ->
                mapping.request.method shouldBe "POST"
                mapping.request.urlPath shouldBe "/parext/v1/hotels/multiRoomRateAvailability"
                mapping.request.headers
                    .shouldNotBeNull()
                    .getValue("x-hotelid")
                    .equalTo shouldBe "MRR001"
                val jsonPaths = mapping.request.bodyPatterns!!.mapNotNull { it.matchesJsonPath }
                jsonPaths shouldContain "$[?(@.arrivalDate == '2026-09-10')]"
                jsonPaths shouldContain "$[?(@.departureDate == '2026-09-12')]"
                jsonPaths shouldContain "$[?(@.hotelIds.size() == 1)]"
                jsonPaths shouldContain "$.hotelIds[?(@ == \"MRR001\")]"
            }
        }

        test("room-tag matching is order-independent, duplicate-safe, and permits unknown tags") {
            val mappings = multiRoomRateAvailability(multiRoomRateBooking()).mappings

            matchingMappings(mappings, "DOUBLE", "TWINRM").single().let(::roomTypesInResponse) shouldBe
                listOf("DOUBLE", "TWINRM")
            matchingMappings(mappings, "TWINRM", "DOUBLE").single().let(::roomTypesInResponse) shouldBe
                listOf("DOUBLE", "TWINRM")
            matchingMappings(mappings, "DOUBLE", "DOUBLE").single().let(::roomTypesInResponse) shouldBe
                listOf("DOUBLE")
            matchingMappings(mappings, "DOUBLE", "NOSUCHROOM").single().let(::roomTypesInResponse) shouldBe
                listOf("DOUBLE")
            matchingMappings(mappings, "NOSUCHROOM").single().let(::hotelIdsInResponse) shouldBe emptyList()
        }

        test("the response echoes the room type as its tag with the hotel's rates and room class") {
            val booking = multiRoomRateBooking()
            val doubleMapping =
                mappingsForRoomType(booking, "DOUBLE").single()

            val hotel =
                doubleMapping.response.jsonBody!!
                    .jsonObject
                    .getValue("hotelAvailability")
                    .jsonArray
                    .single()
                    .jsonObject
            hotel.getValue("hotelId").jsonPrimitive.content shouldBe "MRR001"
            val roomStay =
                hotel
                    .getValue("roomStays")
                    .jsonArray
                    .single()
                    .jsonObject
            roomStay.getValue("roomClass").jsonPrimitive.content shouldBe "ST"
            val roomType =
                roomStay
                    .getValue("roomTypes")
                    .jsonArray
                    .single()
                    .jsonObject
            roomType.getValue("tag").jsonPrimitive.content shouldBe "DOUBLE"
            roomType.getValue("roomType").jsonPrimitive.content shouldBe "DOUBLE"
            val roomRate =
                roomType
                    .getValue("roomRates")
                    .jsonArray
                    .single()
                    .jsonObject
            roomRate.getValue("ratePlanCode").jsonPrimitive.content shouldBe "FLEXRATE"
            roomRate.getValue("ratePlanSet").jsonPrimitive.content shouldBe "PBF"
            roomRate.getValue("currencyCode").jsonPrimitive.content shouldBe "GBP"
        }

        test("a grouped response separates selected room types by Opera room class") {
            val booking =
                multiRoomRateBooking().let { original ->
                    original.copy(
                        hotels =
                            original.hotels.map { hotel ->
                                hotel.copy(
                                    availableRoomTypes =
                                        hotel.availableRoomTypes.map { roomType ->
                                            if (roomType.roomType == "TWINRM") roomType.copy(roomClass = "DL") else roomType
                                        },
                                )
                            },
                    )
                }
            val combined =
                multiRoomRateAvailability(booking).mappings.single { mapping ->
                    requiredRoomTags(mapping) == setOf("DOUBLE", "TWINRM")
                }

            roomTypesByClassInResponse(combined) shouldBe
                mapOf(
                    "ST" to listOf("DOUBLE"),
                    "DL" to listOf("TWINRM"),
                )
        }

        test("eleven hotels create two non-overlapping mappings per PMS room type") {
            val booking = multiRoomRateBooking(hotelCount = 11)

            listOf("DOUBLE", "TWINRM").forEach { pmsRoomType ->
                val mappings = mappingsForRoomType(booking, pmsRoomType)

                mappings shouldHaveSize 2
                mappings.map(::hotelIdsInResponse) shouldBe
                    listOf(
                        (1..10).map { index -> "MRR${index.toString().padStart(3, '0')}" },
                        listOf("MRR011"),
                    )
                mappings
                    .flatMap(::hotelIdsInResponse)
                    .groupingBy { hotelId -> hotelId }
                    .eachCount() shouldBe booking.hotels.associate { hotel -> hotel.hotelId to 1 }
            }
        }

        test("ten hotels create one mapping per PMS room type") {
            val booking = multiRoomRateBooking(hotelCount = 10)

            mappingsForRoomType(booking, "DOUBLE") shouldHaveSize 1
            mappingsForRoomType(booking, "TWINRM") shouldHaveSize 1
        }

        test("each batch matcher requires its exact hotel ids without depending on order") {
            val mappings = mappingsForRoomType(multiRoomRateBooking(hotelCount = 11), "DOUBLE")

            mappings.map { mapping ->
                val jsonPaths =
                    mapping.request.bodyPatterns
                        .orEmpty()
                        .mapNotNull { pattern -> pattern.matchesJsonPath }
                mapping.request.headers
                    .shouldNotBeNull()
                    .getValue("x-hotelid")
                    .equalTo to jsonPaths
            } shouldBe
                listOf(
                    "MRR001" to
                        listOf(
                            "$[?(@.arrivalDate == '2026-09-10')]",
                            "$[?(@.departureDate == '2026-09-12')]",
                            "$[?(@.hotelIds.size() == 10)]",
                            *(1..10)
                                .map { index ->
                                    "$.hotelIds[?(@ == \"MRR${index.toString().padStart(3, '0')}\")]"
                                }.toTypedArray(),
                            "$.rooms[?(@.tag == \"DOUBLE\")]",
                        ),
                    "MRR011" to
                        listOf(
                            "$[?(@.arrivalDate == '2026-09-10')]",
                            "$[?(@.departureDate == '2026-09-12')]",
                            "$[?(@.hotelIds.size() == 1)]",
                            "$.hotelIds[?(@ == \"MRR011\")]",
                            "$.rooms[?(@.tag == \"DOUBLE\")]",
                        ),
                )
        }

        test("empty-room override removes only the selected PMS room type from every batch") {
            val booking = multiRoomRateBooking(hotelCount = 11)
            val stub = multiRoomRateAvailabilityEmptyForRoomType(booking, pmsRoomType = "TWINRM")

            val twinMappings = mappingsForRoomType(stub.mappings, "TWINRM")
            twinMappings shouldHaveSize 2
            twinMappings.forEach { mapping -> hotelIdsInResponse(mapping) shouldBe emptyList() }
            mappingsForRoomType(stub.mappings, "DOUBLE").forEach { mapping ->
                hotelIdsInResponse(mapping).isNotEmpty() shouldBe true
            }
            val combinedMappings =
                stub.mappings.filter { mapping -> requiredRoomTags(mapping) == setOf("DOUBLE", "TWINRM") }
            combinedMappings shouldHaveSize 2
            combinedMappings.forEach { mapping ->
                roomTypesInResponse(mapping).toSet() shouldBe setOf("DOUBLE")
            }
        }

        test("gate: availability bookings with rates install the stub, rate-less ones do not") {
            val ids = defaultStubsFor(multiRoomRateBooking()).map { it.id }
            val rateLessIds =
                defaultStubsFor(
                    multiRoomRateBooking().let { booking ->
                        booking.copy(hotels = booking.hotels.map { it.copy(availableRates = emptyList()) })
                    },
                ).map { it.id }

            ids shouldContain OPERA_MULTI_ROOM_RATE_AVAILABILITY_STUB_ID
            rateLessIds shouldNotContain OPERA_MULTI_ROOM_RATE_AVAILABILITY_STUB_ID
        }
    })

private fun multiRoomRateBooking(hotelCount: Int = 1): Booking =
    Booking(
        hotels =
            (1..hotelCount).map { index ->
                Hotel(
                    hotelId = "MRR${index.toString().padStart(3, '0')}",
                    shortId = "mrr-$index",
                    name = "Multi Room Rate Hotel $index",
                    addressLine = "1 Distr Street",
                    city = "London",
                    postcode = "SW1A 1AA",
                    phone = "02079460000",
                    availableRoomTypes =
                        listOf(
                            HotelRoomType(roomClass = "ST", roomType = "DOUBLE", numberOfRooms = 5),
                            HotelRoomType(roomClass = "ST", roomType = "TWINRM", numberOfRooms = 2),
                        ),
                    availableRates =
                        listOf(
                            Rate(ratePlan = "FLEXRATE", ratePlanSet = "PBF", roomType = "DOUBLE", adults = 2),
                            Rate(ratePlan = "FLEXRATE", ratePlanSet = "PBF", roomType = "TWINRM", adults = 2),
                        ),
                )
            },
        arrival = LocalDate.of(2026, 9, 10),
        departure = LocalDate.of(2026, 9, 12),
        rooms = listOf(BookingRoom(roomType = "DB", adults = 2)),
    )

private fun mappingsForRoomType(
    booking: Booking,
    pmsRoomType: String,
) = mappingsForRoomType(multiRoomRateAvailability(booking).mappings, pmsRoomType)

private fun mappingsForRoomType(
    mappings: List<StubMapping>,
    pmsRoomType: String,
) = mappings.filter { mapping ->
    requiredRoomTags(mapping) == setOf(pmsRoomType)
}

private fun hotelIdsInResponse(mapping: StubMapping): List<String> =
    mapping.response.jsonBody!!
        .jsonObject
        .getValue("hotelAvailability")
        .jsonArray
        .map { hotel ->
            hotel.jsonObject
                .getValue("hotelId")
                .jsonPrimitive.content
        }

private fun requiredRoomTags(mapping: StubMapping): Set<String> =
    mapping.request.bodyPatterns
        .orEmpty()
        .mapNotNull { pattern -> pattern.matchesJsonPath?.roomTag() }
        .toSet()

private fun excludedRoomTags(mapping: StubMapping): Set<String> =
    mapping.request.bodyPatterns
        .orEmpty()
        .mapNotNull { pattern -> pattern.not?.matchesJsonPath?.roomTag() }
        .toSet()

private fun String.roomTag(): String? =
    Regex("""^\$\.rooms\[\?\(@\.tag == \"(.*)\"\)]$""")
        .matchEntire(this)
        ?.groupValues
        ?.get(1)

private fun roomTypesInResponse(mapping: StubMapping): List<String> =
    mapping.response.jsonBody!!
        .jsonObject
        .getValue("hotelAvailability")
        .jsonArray
        .flatMap { hotel ->
            hotel.jsonObject
                .getValue("roomStays")
                .jsonArray
                .flatMap { roomStay ->
                    roomStay.jsonObject
                        .getValue("roomTypes")
                        .jsonArray
                        .map { roomType ->
                            roomType.jsonObject
                                .getValue("roomType")
                                .jsonPrimitive.content
                        }
                }
        }

private fun matchingMappings(
    mappings: List<StubMapping>,
    vararg requestedRoomTags: String,
): List<StubMapping> {
    val requested = requestedRoomTags.toSet()
    return mappings.filter { mapping ->
        requested.containsAll(requiredRoomTags(mapping)) && requested.intersect(excludedRoomTags(mapping)).isEmpty()
    }
}

private fun roomTypesByClassInResponse(mapping: StubMapping): Map<String, List<String>> =
    mapping.response.jsonBody!!
        .jsonObject
        .getValue("hotelAvailability")
        .jsonArray
        .single()
        .jsonObject
        .getValue("roomStays")
        .jsonArray
        .associate { roomStay ->
            val roomStayObject = roomStay.jsonObject
            roomStayObject.getValue("roomClass").jsonPrimitive.content to
                roomStayObject
                    .getValue("roomTypes")
                    .jsonArray
                    .map { roomType ->
                        roomType.jsonObject
                            .getValue("roomType")
                            .jsonPrimitive.content
                    }
        }
