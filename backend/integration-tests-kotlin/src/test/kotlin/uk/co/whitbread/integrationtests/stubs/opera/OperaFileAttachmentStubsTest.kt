package uk.co.whitbread.integrationtests.stubs.opera

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import uk.co.whitbread.integrationtests.stubs.defaultStubsFor
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.Hotel
import uk.co.whitbread.integrationtests.testkit.model.PreRegistration
import uk.co.whitbread.integrationtests.testkit.model.Rate
import java.time.LocalDate

class OperaFileAttachmentStubsTest :
    FunSpec({
        test("file-attachment mapping matches the reservation-linked upload shape") {
            val booking = attachmentBooking()
            val mapping =
                addFileAttachment(booking, listOf(booking.room))
                    .mappings
                    .single()

            mapping.request.method shouldBe "POST"
            mapping.request.urlPath shouldBe "/med/config/v1/fileAttachments"
            mapping.request.headers!!
                .getValue("x-hotelid")
                .equalTo shouldBe "ATHOTL"
            val jsonPaths = mapping.request.bodyPatterns!!.mapNotNull { it.matchesJsonPath }
            jsonPaths shouldContain
                "$[?(@.linkType == \"Reservation\" && @.linkId == \"RSV-AT-1\")]"
            jsonPaths shouldContain "$.fileAttachment"
        }

        test("file-attachment response acknowledges the stored reservation link") {
            val booking = attachmentBooking()
            val response =
                addFileAttachment(booking, listOf(booking.room))
                    .mappings
                    .single()
                    .response

            response.status shouldBe 200
            val body = response.jsonBody!!.jsonObject
            body.getValue("linkId").jsonPrimitive.content shouldBe "RSV-AT-1"
            body.getValue("linkType").jsonPrimitive.content shouldBe "Reservation"
        }

        test("attachment-deletion mapping matches the stored attachment's DELETE and returns 204") {
            val booking = preRegisteredBooking()
            val mapping =
                deleteReservationAttachment(booking, listOf(booking.room))
                    .mappings
                    .single()

            mapping.request.method shouldBe "DELETE"
            mapping.request.urlPath shouldBe
                "/rsv/v1/hotels/ATHOTL/reservations/RSV-AT-1/attachments/ATT-1"
            mapping.request.headers!!
                .getValue("x-hotelid")
                .equalTo shouldBe "ATHOTL"
            mapping.response.status shouldBe 204
        }

        test("attachment-deletion stub installs only for rooms holding a reg-card attachment id") {
            val withAttachment = defaultStubsFor(preRegisteredBooking())
            val alertOnly =
                defaultStubsFor(
                    preRegisteredBooking().let { booking ->
                        booking.copy(
                            rooms =
                                booking.rooms.map {
                                    it.copy(
                                        preRegistration =
                                            PreRegistration(preCheckInAlertId = "ALERT-1"),
                                    )
                                },
                        )
                    },
                )

            withAttachment.map { it.id } shouldContain OPERA_RESERVATION_ATTACHMENT_DELETE_STUB_ID
            alertOnly.map { it.id } shouldNotContain OPERA_RESERVATION_ATTACHMENT_DELETE_STUB_ID
        }

        test("file-attachment stub installs for every reservation room and only those") {
            val withReservation = defaultStubsFor(attachmentBooking())
            val withoutReservation =
                defaultStubsFor(
                    attachmentBooking().let { booking ->
                        booking.copy(
                            rooms = booking.rooms.map { it.copy(reservationId = null) },
                        )
                    },
                )

            withReservation.map { it.id } shouldContain OPERA_FILE_ATTACHMENT_ADD_STUB_ID
            withoutReservation.map { it.id } shouldNotContain OPERA_FILE_ATTACHMENT_ADD_STUB_ID
        }
    })

private fun preRegisteredBooking(): Booking =
    attachmentBooking().let { booking ->
        booking.copy(
            rooms =
                booking.rooms.map {
                    it.copy(
                        preRegistration =
                            PreRegistration(
                                regCardAttachmentId = "ATT-1",
                                preCheckInAlertId = "ALERT-1",
                            ),
                    )
                },
        )
    }

private fun attachmentBooking(): Booking =
    Booking(
        hotels =
            listOf(
                Hotel(
                    hotelId = "ATHOTL",
                    shortId = "at-hotel",
                    name = "Attachment Hotel",
                    addressLine = "1 Registration Road",
                    city = "Leeds",
                    postcode = "AT1 TEST",
                    country = "GB",
                    phone = "+441120000000",
                    currency = "GBP",
                    availableRates =
                        listOf(
                            Rate(
                                ratePlan = "SEMIFLEX",
                                roomType = "DOUBLE",
                                adults = 1,
                                ratePlanSet = "PBF",
                            ),
                        ),
                ),
            ),
        arrival = LocalDate.of(2026, 9, 15),
        departure = LocalDate.of(2026, 9, 17),
        rooms =
            listOf(
                BookingRoom(
                    reservationId = "RSV-AT-1",
                    roomType = "DOUBLE",
                    adults = 1,
                ),
            ),
    )
