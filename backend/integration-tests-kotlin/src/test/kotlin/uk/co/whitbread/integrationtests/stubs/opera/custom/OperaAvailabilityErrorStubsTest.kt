package uk.co.whitbread.integrationtests.stubs.opera.custom

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import uk.co.whitbread.integrationtests.framework.wiremock.model.StubMapping
import uk.co.whitbread.integrationtests.stubs.defaultStubsFor
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_AVAILABILITY_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.hotelAvailability
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.Hotel
import uk.co.whitbread.integrationtests.testkit.model.HotelRoomType
import uk.co.whitbread.integrationtests.testkit.model.Rate
import java.time.LocalDate

class OperaAvailabilityErrorStubsTest :
    FunSpec({
        test("availability failure retains every generic matcher and interval") {
            val booking = customAvailabilityBooking(longStay = true)
            val default = hotelAvailability(booking)
            val failure = hotelAvailabilityFailure(booking)

            failure.id shouldBe CUSTOM_HOTEL_AVAILABILITY_ERROR_STUB_ID
            failure.mappings shouldHaveSize default.mappings.size
            failure.mappings.zip(default.mappings).forEach { (failed, healthy) ->
                failed.request shouldBe healthy.request
                failed.response.status shouldBe 500
                failed.response.jsonBody!!
                    .jsonObject
                    .getValue("type")
                    .jsonPrimitive.content shouldBe "Internal Server Error"
                failed.response.jsonBody
                    .jsonObject
                    .getValue("detail")
                    .jsonPrimitive.content shouldBe "hotel availability search failed"
            }
            val failedRateCode =
                failure.mappings
                    .filterWithRateCodeAndRoomType("FLEX", "DOUBLE")
                    .first()
            val query = requireNotNull(failedRateCode.request.queryParameters)
            failedRateCode.request.method shouldBe "GET"
            failedRateCode.request.urlPath shouldBe "/par/v1/hotels/AVAERR/availability"
            failedRateCode.request.headers
                ?.getValue("x-hotelid")
                ?.equalTo shouldBe "AVAERR"
            query.getValue("roomStayStartDate").equalTo shouldBe "2026-09-10"
            query.getValue("roomStayEndDate").equalTo shouldBe "2026-12-07"
            query.getValue("roomStayQuantity").matches shouldBe "^[1-9][0-9]*$"
            query.getValue("ratePlanCode").equalTo shouldBe "FLEX"
            query.getValue("roomType").equalTo shouldBe "DOUBLE"
            query.getValue("limit").equalTo shouldBe "20"
            query.getValue("reservationGuestIdType").equalTo shouldBe "Profile"
        }

        test("mismatched-room-type response changes only selected priced mappings") {
            val booking = customAvailabilityBooking(longStay = true)
            val default = hotelAvailability(booking)
            val mismatch =
                hotelAvailabilityWithMismatchedRoomType(
                    booking = booking,
                    requestedRoomType = "DOUBLE",
                    reportedRoomType = "SUITE",
                )
            val selected = mismatch.mappings.filterWithRateCodeAndRoomType("FLEX", "DOUBLE")
            val untouched = mismatch.mappings.filterWithRateCodeAndRoomType("FLEX", "TWIN")

            mismatch.id shouldBe CUSTOM_HOTEL_AVAILABILITY_MISMATCHED_ROOM_TYPE_STUB_ID
            mismatch.mappings.map { mapping -> mapping.request } shouldBe
                default.mappings.map { mapping -> mapping.request }
            selected shouldHaveSize 2
            selected.flatMap(StubMapping::reportedRoomTypes).distinct() shouldBe listOf("SUITE")
            untouched shouldHaveSize 2
            untouched.flatMap(StubMapping::reportedRoomTypes).distinct() shouldBe listOf("TWIN")
        }

        test("mismatched-room-type response requires a configured requested room type") {
            shouldThrow<IllegalArgumentException> {
                hotelAvailabilityWithMismatchedRoomType(
                    booking = customAvailabilityBooking(),
                    requestedRoomType = "SINGLE",
                    reportedRoomType = "SUITE",
                )
            }.message shouldBe
                "Expected at least one rate-plan-code availability mapping for room type SINGLE"
        }

        test("custom availability builders remain outside the default stub plan") {
            val ids = defaultStubsFor(customAvailabilityBooking()).map { stub -> stub.id }

            ids shouldNotContain CUSTOM_HOTEL_AVAILABILITY_ERROR_STUB_ID
            ids shouldNotContain CUSTOM_HOTEL_AVAILABILITY_MISMATCHED_ROOM_TYPE_STUB_ID
            ids.contains(OPERA_AVAILABILITY_STUB_ID) shouldBe true
        }
    })

private fun List<StubMapping>.filterWithRateCodeAndRoomType(
    ratePlanCode: String,
    roomType: String,
): List<StubMapping> =
    filter { mapping ->
        val query = mapping.request.queryParameters.orEmpty()
        query["ratePlanCode"]?.equalTo == ratePlanCode && query["roomType"]?.equalTo == roomType
    }

private fun StubMapping.reportedRoomTypes(): List<String> =
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
        .map { roomRate ->
            roomRate.jsonObject
                .getValue("roomType")
                .jsonPrimitive.content
        }

private fun customAvailabilityBooking(longStay: Boolean = false): Booking {
    val arrival = LocalDate.of(2026, 9, 10)
    return Booking(
        hotels =
            listOf(
                Hotel(
                    hotelId = "AVAERR",
                    shortId = "availability-error-hotel",
                    name = "Availability Error Hotel",
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
                            Rate(ratePlan = "FLEX", roomType = "DOUBLE", adults = 2, nightlyRate = 60.0),
                            Rate(ratePlan = "FLEX", roomType = "TWIN", adults = 2, nightlyRate = 55.0),
                        ),
                ),
            ),
        arrival = arrival,
        departure = if (longStay) arrival.plusDays(120) else arrival.plusDays(3),
        rooms =
            listOf(
                BookingRoom(
                    reservationId = "RSV-AVA-ERR-1",
                    roomType = "DOUBLE",
                    adults = 2,
                ),
            ),
    )
}
