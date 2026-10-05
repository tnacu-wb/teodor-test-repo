package uk.co.whitbread.integrationtests.stubs.opera

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import uk.co.whitbread.integrationtests.stubs.defaultStubsFor
import uk.co.whitbread.integrationtests.testkit.model.Booking

class OperaHousekeepingStubsTest :
    FunSpec({
        test("a known room's mapping matches roomIdText exactly and reports its status") {
            val mapping =
                housekeepingOverview(listOf(vacantRoomsHotel()))
                    .mappings
                    .single {
                        it.request.queryParameters!!
                            .getValue("roomIdText")
                            .equalTo == "103"
                    }

            mapping.request.method shouldBe "GET"
            mapping.request.urlPath shouldBe "/hsk/v1/hotels/VACHTL/housekeepingOverview"
            mapping.request.headers!!
                .getValue("x-hotelid")
                .equalTo shouldBe "VACHTL"

            val room =
                mapping.response.jsonBody!!
                    .jsonObject
                    .getValue("housekeepingRoomInfo")
                    .jsonObject
                    .getValue("housekeepingRooms")
                    .jsonObject
                    .getValue("room")
                    .jsonArray
                    .single()
                    .jsonObject
            room.getValue("roomId").jsonPrimitive.content shouldBe "103"
            room
                .getValue("housekeeping")
                .jsonObject
                .getValue("housekeepingRoomStatus")
                .jsonObject
                .getValue("housekeepingRoomStatus")
                .jsonPrimitive
                .content shouldBe "Dirty"
        }

        test("the catch-all comes first and keeps the wrappers without a room array") {
            val mappings = housekeepingOverview(listOf(vacantRoomsHotel())).mappings

            // Catch-all first so later-installed per-room mappings win for known rooms.
            val catchAll = mappings.first()
            catchAll.request.queryParameters!!
                .getValue("roomIdText")
                .matches shouldBe ".+"
            val housekeepingRooms =
                catchAll.response.jsonBody!!
                    .jsonObject
                    .getValue("housekeepingRoomInfo")
                    .jsonObject
                    .getValue("housekeepingRooms")
                    .jsonObject
            housekeepingRooms.getValue("hotelId").jsonPrimitive.content shouldBe "VACHTL"
            housekeepingRooms["room"].shouldBeNull()
        }

        test("housekeeping stub installs only for hotels declaring physical rooms") {
            val gated = defaultStubsFor(Booking(hotels = listOf(vacantRoomsHotel())))
            val ungated =
                defaultStubsFor(Booking(hotels = listOf(vacantRoomsHotel(physicalRooms = emptyList()))))

            gated.map { it.id } shouldContain OPERA_HOUSEKEEPING_OVERVIEW_STUB_ID
            // One catch-all plus one mapping per physical room.
            gated.single { it.id == OPERA_HOUSEKEEPING_OVERVIEW_STUB_ID }.mappings shouldHaveSize 5
            ungated.map { it.id } shouldNotContain OPERA_HOUSEKEEPING_OVERVIEW_STUB_ID
        }
    })
