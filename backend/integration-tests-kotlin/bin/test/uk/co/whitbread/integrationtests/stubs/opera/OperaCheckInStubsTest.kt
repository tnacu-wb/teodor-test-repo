package uk.co.whitbread.integrationtests.stubs.opera

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import uk.co.whitbread.integrationtests.stubs.defaultStubsFor

class OperaCheckInStubsTest :
    FunSpec({
        test("check-in mapping matches the assigned room and the forced-warnings flag") {
            val booking = frontOfficeBooking(assignedRoomId = "101")
            val mapping =
                checkIn(booking, listOf(booking.room))
                    .mappings
                    .single()

            mapping.request.method shouldBe "POST"
            mapping.request.urlPath shouldBe
                "/fof/v1/hotels/FOHOTL/reservations/RSV-FO-1/checkIns"
            mapping.request.headers!!
                .getValue("x-hotelid")
                .equalTo shouldBe "FOHOTL"
            val jsonPaths = mapping.request.bodyPatterns!!.mapNotNull { it.matchesJsonPath }
            jsonPaths shouldContain "$[?(@.reservation.roomId == \"101\")]"
            jsonPaths shouldContain "$[?(@.reservation.ignoreWarnings == true)]"
        }

        test("check-in response reports the in-house reservation in its assigned room") {
            val booking = frontOfficeBooking(assignedRoomId = "101")
            val reservation =
                checkIn(booking, listOf(booking.room))
                    .mappings
                    .single()
                    .response.jsonBody!!
                    .jsonObject
                    .getValue("reservation")
                    .jsonArray
                    .single()
                    .jsonObject

            reservation.getValue("reservationStatus").jsonPrimitive.content shouldBe "InHouse"
            reservation
                .getValue("roomStay")
                .jsonObject
                .getValue("currentRoomInfo")
                .jsonObject
                .getValue("roomId")
                .jsonPrimitive
                .content shouldBe "101"
        }

        test("check-in stub installs only for reservation rooms with an assigned room") {
            val gated = defaultStubsFor(frontOfficeBooking(assignedRoomId = "101"))
            val ungated = defaultStubsFor(frontOfficeBooking(assignedRoomId = null))

            gated.map { it.id } shouldContain OPERA_CHECK_IN_STUB_ID
            gated.single { it.id == OPERA_CHECK_IN_STUB_ID }.mappings shouldHaveSize 1
            ungated.map { it.id } shouldNotContain OPERA_CHECK_IN_STUB_ID
        }
    })
