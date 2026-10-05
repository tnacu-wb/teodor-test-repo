package uk.co.whitbread.integrationtests.stubs.opera

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import uk.co.whitbread.integrationtests.stubs.defaultStubsFor
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.Hotel
import uk.co.whitbread.integrationtests.testkit.model.HotelPhysicalRoom

class OperaVacantRoomsStubsTest :
    FunSpec({
        test("vacant-rooms mapping matches the real caller's query and header shape") {
            val mapping =
                vacantRooms(listOf(vacantRoomsHotel()))
                    .mappings
                    .single {
                        it.request.queryParameters!!
                            .getValue("roomType")
                            .equalTo == "DOUBLE"
                    }

            mapping.request.method shouldBe "GET"
            mapping.request.urlPath shouldBe "/fof/v1/hotels/VACHTL/rooms"
            mapping.request.headers!!
                .getValue("x-hotelid")
                .equalTo shouldBe "VACHTL"
            val query = mapping.request.queryParameters!!
            query.getValue("hotelRoomStatus").equalTo shouldBe "Clean"
            query.getValue("hotelFORoomStatus").equalTo shouldBe "Vacant"
            query.getValue("includeAllRoomConditions").equalTo shouldBe "true"
            query.getValue("limit").equalTo shouldBe "60"
        }

        test("vacant-rooms response returns only the clean vacant rooms of the requested type") {
            val mapping =
                vacantRooms(listOf(vacantRoomsHotel()))
                    .mappings
                    .single {
                        it.request.queryParameters!!
                            .getValue("roomType")
                            .equalTo == "DOUBLE"
                    }

            val details =
                mapping.response.jsonBody!!
                    .jsonObject
                    .getValue("hotelRoomsDetails")
                    .jsonObject
            details.getValue("hotelId").jsonPrimitive.content shouldBe "VACHTL"
            val roomIds =
                details
                    .getValue("room")
                    .jsonArray
                    .map { room ->
                        room.jsonObject
                            .getValue("roomId")
                            .jsonPrimitive.content
                    }
            // 103 is Dirty and 201 is a TWIN, so neither may appear.
            roomIds shouldContainExactly listOf("101", "102")
        }

        test("a room type with no clean vacant rooms serves an empty room list") {
            val mapping =
                vacantRooms(listOf(vacantRoomsHotel()))
                    .mappings
                    .single {
                        it.request.queryParameters!!
                            .getValue("roomType")
                            .equalTo == "TWIN"
                    }

            mapping.response.jsonBody!!
                .jsonObject
                .getValue("hotelRoomsDetails")
                .jsonObject
                .getValue("room")
                .jsonArray
                .shouldHaveSize(0)
        }

        test("vacant-rooms stub installs only for hotels declaring physical rooms") {
            val gated = defaultStubsFor(Booking(hotels = listOf(vacantRoomsHotel())))
            val ungated = defaultStubsFor(Booking(hotels = listOf(vacantRoomsHotel(physicalRooms = emptyList()))))

            gated.map { it.id } shouldContain OPERA_VACANT_ROOMS_STUB_ID
            // One mapping per distinct room type: DOUBLE and TWIN.
            gated.single { it.id == OPERA_VACANT_ROOMS_STUB_ID }.mappings shouldHaveSize 2
            ungated.map { it.id } shouldNotContain OPERA_VACANT_ROOMS_STUB_ID
        }
    })

internal fun vacantRoomsHotel(
    physicalRooms: List<HotelPhysicalRoom> =
        listOf(
            HotelPhysicalRoom(roomId = "101", roomType = "DOUBLE"),
            HotelPhysicalRoom(roomId = "102", roomType = "DOUBLE", floor = "1"),
            HotelPhysicalRoom(roomId = "103", roomType = "DOUBLE", housekeepingStatus = "Dirty"),
            HotelPhysicalRoom(roomId = "201", roomType = "TWIN", frontOfficeStatus = "Occupied"),
        ),
): Hotel =
    Hotel(
        hotelId = "VACHTL",
        shortId = "vac-hotel",
        name = "Vacant Rooms Hotel",
        addressLine = "1 Allocation Way",
        city = "London",
        postcode = "VA1 TEST",
        country = "GB",
        phone = "+441110000001",
        currency = "GBP",
        physicalRooms = physicalRooms,
    )
