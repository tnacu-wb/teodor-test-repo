package uk.co.whitbread.integrationtests.stubs.opera

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
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
import java.time.LocalDate

class OperaMultiHotelAvailabilityStubsTest :
    FunSpec({
        test("rate-plan-set mapping matches the multi-hotel search's query shape") {
            val booking = multiHotelBooking()
            val mapping = setMapping(booking, "PBF")

            mapping.request.method shouldBe "GET"
            mapping.request.urlPath shouldBe "/par/v1/availability"
            val parameters = mapping.request.queryParameters.shouldNotBeNull()
            parameters
                .getValue("hotelIds")
                .hasExactly
                .shouldNotBeNull()
                .map { pattern -> pattern.equalTo } shouldBe listOf("MHA001", "MHA002")
            parameters.getValue("limit").equalTo shouldBe "20"
            parameters.getValue("roomStayStartDate").equalTo shouldBe "2026-09-10"
            parameters.getValue("roomStayEndDate").equalTo shouldBe "2026-09-12"
            parameters.getValue("roomStayQuantity").matches shouldBe ".+"
            parameters.getValue("ratePlanSet").equalTo shouldBe "PBF"
            mapping.request.headers
                .shouldNotBeNull()
                .getValue("x-hubid")
                .matches shouldBe ".+"
        }

        test("rate-plan-set response returns each hotel's rates for that set only") {
            val booking = multiHotelBooking()
            val segments = hotelSegments(setMapping(booking, "PBF"))

            segments.keys shouldBe setOf("MHA001", "MHA002")
            ratePlanCodesOf(segments.getValue("MHA001")) shouldBe listOf("FLEXRATE")
            ratePlanCodesOf(segments.getValue("MHA002")) shouldBe listOf("FLEXRATE")
        }

        test("five hotels create one mapping per rate-plan-set selector") {
            val booking = multiHotelBooking(hotelCount = 5)

            listOf(setMappings(booking, "PBN"), setMappings(booking, "PBF"), setFallbackMappings(booking))
                .forEach { mappings ->
                    mappings shouldHaveSize 1
                    hotelSegments(mappings.single()).keys shouldBe booking.hotels.map(Hotel::hotelId).toSet()
                }
        }

        test("six hotels create two mappings per rate-plan-set selector without duplicate hotels") {
            val booking = multiHotelBooking(hotelCount = 6)

            listOf(setMappings(booking, "PBN"), setMappings(booking, "PBF"), setFallbackMappings(booking))
                .forEach { mappings ->
                    mappings shouldHaveSize 2
                    mappings.map { mapping -> hotelSegments(mapping).keys } shouldBe
                        listOf(
                            setOf("MHA001", "MHA002", "MHA003", "MHA004", "MHA005"),
                            setOf("MHA006"),
                        )
                    mappings
                        .flatMap { mapping -> hotelSegments(mapping).keys }
                        .groupingBy { hotelId -> hotelId }
                        .eachCount() shouldBe booking.hotels.associate { hotel -> hotel.hotelId to 1 }
                }
        }

        test("rate-plan-set batches match every repeated hotel id exactly") {
            val mappings = setMappings(multiHotelBooking(hotelCount = 6), "PBF")

            mappings.map { mapping ->
                mapping.request.queryParameters
                    .shouldNotBeNull()
                    .getValue("hotelIds")
                    .hasExactly
                    .shouldNotBeNull()
                    .map { pattern -> pattern.equalTo }
            } shouldBe
                listOf(
                    listOf("MHA001", "MHA002", "MHA003", "MHA004", "MHA005"),
                    listOf("MHA006"),
                )
        }

        test("an unknown rate plan set falls back to empty room rates for every hotel") {
            val booking = multiHotelBooking()
            val fallback =
                multiHotelAvailability(booking).mappings.first { mapping ->
                    mapping.request.queryParameters
                        ?.get("ratePlanSet")
                        ?.matches
                        ?.contains("?!") == true
                }

            hotelSegments(fallback).values.forEach { segment ->
                ratePlanCodesOf(segment) shouldBe emptyList()
            }
        }

        test("rate-plan-code mapping matches comma lists containing the plan and returns only it") {
            val booking = multiHotelBooking()
            val mapping =
                multiHotelAvailability(booking).mappings.first { candidate ->
                    candidate.request.queryParameters
                        ?.get("ratePlanCode")
                        ?.matches
                        ?.contains(Regex.escape("FLEXRATE")) == true
                }

            val pattern =
                Regex(
                    mapping.request.queryParameters!!
                        .getValue("ratePlanCode")
                        .matches!!,
                )
            pattern.matches("FLEXRATE") shouldBe true
            pattern.matches("SEMIFLEX,FLEXRATE") shouldBe true
            pattern.matches("FLEXRATED") shouldBe false
            ratePlanCodesOf(hotelSegments(mapping).getValue("MHA001")) shouldBe listOf("FLEXRATE")
        }

        test("a comma list with two configured plans matches once and returns both plans") {
            val matches = ratePlanMappingsMatching(multiPlanBooking(), "FLEXRATE,SEMIFLEX")

            matches shouldHaveSize 1
            ratePlanCodesOf(hotelSegments(matches.single()).getValue("MHA001")) shouldContainExactlyInAnyOrder
                listOf("FLEXRATE", "SEMIFLEX")
        }

        test("configured plan matching does not depend on comma-list order") {
            val matches = ratePlanMappingsMatching(multiPlanBooking(), "SEMIFLEX,FLEXRATE")

            matches shouldHaveSize 1
            ratePlanCodesOf(hotelSegments(matches.single()).getValue("MHA001")) shouldContainExactlyInAnyOrder
                listOf("FLEXRATE", "SEMIFLEX")
        }

        test("an unknown plan beside a configured plan returns the configured plan") {
            val matches = ratePlanMappingsMatching(multiPlanBooking(), "FLEXRATE,NOSUCHPLAN")

            matches shouldHaveSize 1
            ratePlanCodesOf(hotelSegments(matches.single()).getValue("MHA001")) shouldBe listOf("FLEXRATE")
        }

        test("an unknown plan matches only the empty fallback") {
            val matches = ratePlanMappingsMatching(multiPlanBooking(), "NOSUCHPLAN")

            matches shouldHaveSize 1
            ratePlanCodesOf(hotelSegments(matches.single()).getValue("MHA001")) shouldBe emptyList()
        }

        test("a plan with a configured plan as its prefix matches only the empty fallback") {
            val matches = ratePlanMappingsMatching(multiPlanBooking(), "FLEXRATED")

            matches shouldHaveSize 1
            ratePlanCodesOf(hotelSegments(matches.single()).getValue("MHA001")) shouldBe emptyList()
        }

        test("rate-plan-code mappings keep all six hotels in one request and response") {
            val mapping = ratePlanMappingsMatching(multiHotelBooking(hotelCount = 6), "FLEXRATE").single()

            mapping.request.queryParameters
                .shouldNotBeNull()
                .getValue("hotelIds")
                .hasExactly
                .shouldNotBeNull()
                .map { pattern -> pattern.equalTo } shouldBe
                listOf("MHA001", "MHA002", "MHA003", "MHA004", "MHA005", "MHA006")
            hotelSegments(mapping).keys shouldBe setOf("MHA001", "MHA002", "MHA003", "MHA004", "MHA005", "MHA006")
        }

        test("negotiated mapping requires the company profile parameters") {
            val booking = negotiatedBooking()
            val mapping = multiHotelNegotiatedAvailability(booking).mappings.single()

            val parameters = mapping.request.queryParameters.shouldNotBeNull()
            parameters
                .getValue("hotelIds")
                .hasExactly
                .shouldNotBeNull()
                .map { pattern -> pattern.equalTo } shouldBe booking.hotels.map(Hotel::hotelId)
            parameters.getValue("reservationProfileType").equalTo shouldBe "Company"
            parameters.getValue("attachedProfileId").equalTo shouldBe "9911223"
            ratePlanCodesOf(hotelSegments(mapping).getValue("MHA001")) shouldBe listOf("CORPFLEX")
        }

        test("gates: availability bookings select the multi-hotel stub; negotiated needs a company") {
            val ids = defaultStubsFor(multiHotelBooking()).map { it.id }
            val negotiatedIds = defaultStubsFor(negotiatedBooking()).map { it.id }
            val reservationIds =
                defaultStubsFor(
                    multiHotelBooking().copy(
                        rooms =
                            listOf(
                                BookingRoom(
                                    reservationId = "RSV-1",
                                    roomType = "DOUBLE",
                                    ratePlan = "FLEXRATE",
                                    adults = 2,
                                ),
                            ),
                    ),
                ).map { it.id }

            ids shouldContain OPERA_MULTI_HOTEL_AVAILABILITY_STUB_ID
            ids shouldNotContain OPERA_MULTI_HOTEL_NEGOTIATED_AVAILABILITY_STUB_ID
            negotiatedIds shouldContain OPERA_MULTI_HOTEL_NEGOTIATED_AVAILABILITY_STUB_ID
            reservationIds shouldNotContain OPERA_MULTI_HOTEL_AVAILABILITY_STUB_ID
        }
    })

private fun setMapping(
    booking: Booking,
    ratePlanSet: String,
): StubMapping =
    multiHotelAvailability(booking).mappings.first { mapping ->
        mapping.request.queryParameters
            ?.get("ratePlanSet")
            ?.equalTo == ratePlanSet
    }

private fun setMappings(
    booking: Booking,
    ratePlanSet: String,
): List<StubMapping> =
    multiHotelAvailability(booking).mappings.filter { mapping ->
        mapping.request.queryParameters
            ?.get("ratePlanSet")
            ?.equalTo == ratePlanSet
    }

private fun setFallbackMappings(booking: Booking): List<StubMapping> =
    multiHotelAvailability(booking).mappings.filter { mapping ->
        mapping.request.queryParameters
            ?.get("ratePlanSet")
            ?.matches
            ?.contains("?!") == true
    }

private fun ratePlanMappingsMatching(
    booking: Booking,
    requestValue: String,
): List<StubMapping> =
    multiHotelAvailability(booking).mappings.filter { mapping ->
        mapping.request.queryParameters
            ?.get("ratePlanCode")
            ?.matches
            ?.let(::Regex)
            ?.matches(requestValue) == true
    }

private fun hotelSegments(mapping: StubMapping) =
    mapping.response.jsonBody!!
        .jsonObject
        .getValue("hotelAvailability")
        .jsonArray
        .associateBy { segment ->
            segment.jsonObject
                .getValue("hotelId")
                .jsonPrimitive.content
        }

private fun ratePlanCodesOf(segment: kotlinx.serialization.json.JsonElement): List<String> =
    segment.jsonObject
        .getValue("roomStays")
        .jsonArray
        .single()
        .jsonObject
        .getValue("roomRates")
        .jsonArray
        .map { roomRate ->
            roomRate.jsonObject
                .getValue("ratePlanCode")
                .jsonPrimitive.content
        }

private fun availabilityHotel(
    hotelId: String,
    rates: List<Rate>,
): Hotel =
    Hotel(
        hotelId = hotelId,
        shortId = "mh-$hotelId",
        name = "Multi Hotel $hotelId",
        addressLine = "1 Search Street",
        city = "London",
        postcode = "SW1A 1AA",
        phone = "02079460000",
        availableRoomTypes = listOf(HotelRoomType(roomClass = "ST", roomType = "DOUBLE", numberOfRooms = 5)),
        availableRates = rates,
    )

private fun multiHotelBooking(hotelCount: Int = 2): Booking =
    Booking(
        hotels =
            (1..hotelCount).map { index ->
                availabilityHotel(
                    "MHA${index.toString().padStart(3, '0')}",
                    listOf(
                        Rate(ratePlan = "FLEXRATE", ratePlanSet = "PBF", roomType = "DOUBLE", adults = 2),
                        Rate(ratePlan = "STANDARD", ratePlanSet = "PBN", roomType = "DOUBLE", adults = 2),
                    ),
                )
            },
        arrival = LocalDate.of(2026, 9, 10),
        departure = LocalDate.of(2026, 9, 12),
        rooms = listOf(BookingRoom(roomType = "DB", adults = 2)),
    )

private fun multiPlanBooking(): Booking =
    multiHotelBooking().let { booking ->
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
                                ),
                    )
                },
        )
    }

private fun negotiatedBooking(): Booking =
    multiHotelBooking().let { booking ->
        booking.copy(
            companies =
                listOf(
                    Company(
                        name = "Gate Corp",
                        corpId = "4411",
                        companyId = "9911223",
                        telephoneNumber = "02079460000",
                        address = CompanyAddress(addressLine1 = "1 Corp Way", postalCode = "SW1A 1AA"),
                    ),
                ),
            hotels =
                listOf(
                    availabilityHotel(
                        "MHA001",
                        listOf(
                            Rate(ratePlan = "FLEXRATE", ratePlanSet = "PBF", roomType = "DOUBLE", adults = 2),
                            Rate(ratePlan = "CORPFLEX", ratePlanSet = "NEGOTIATED", roomType = "DOUBLE", adults = 2),
                        ),
                    ),
                ),
        )
    }
