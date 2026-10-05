package uk.co.whitbread.integrationtests.stubs.opera

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.stubs.defaultStubsFor
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.Hotel
import uk.co.whitbread.integrationtests.testkit.model.Rate
import uk.co.whitbread.integrationtests.testkit.model.RoutingFolioInstruction
import uk.co.whitbread.integrationtests.testkit.model.RoutingInstruction
import java.time.LocalDate

class OperaRoutingInstructionDeleteStubsTest :
    FunSpec({
        test("a daily instruction pins the folio identity, duration flags, and stay time span") {
            val booking = routingBooking(instructions = listOf(RoutingFolioInstruction(daily = true)))
            val mapping =
                deleteRoutingInstructions(booking, listOf(booking.room))
                    .mappings
                    .single()

            mapping.request.method shouldBe "DELETE"
            mapping.request.urlPath shouldBe
                "/csh/v1/hotels/RIHOTL/reservations/RSV-RI-1/routingInstructions/folio"
            mapping.request.headers!!
                .getValue("x-hotelid")
                .equalTo shouldBe "RIHOTL"
            val query = mapping.request.queryParameters!!
            query.getValue("payeeId").equalTo shouldBe "500001"
            query.getValue("folioWindowNo").equalTo shouldBe "1"
            query.getValue("daily").equalTo shouldBe "true"
            listOf("sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday")
                .forEach { day -> query.getValue(day).equalTo shouldBe "true" }
            query.getValue("startDate").equalTo shouldBe "2026-09-15"
            query.getValue("endDate").equalTo shouldBe "2026-09-17"
            query.getValue("retrievePostingsForRoomRouting").equalTo shouldBe "false"
            query.containsKey("creditLimit") shouldBe false
            query.containsKey("routingLinkId") shouldBe false
            mapping.response.status shouldBe 200
        }

        test("a non-daily instruction with optional identifiers pins them without a time span") {
            val booking =
                routingBooking(
                    instructions =
                        listOf(
                            RoutingFolioInstruction(
                                daily = false,
                                creditLimit = "250.00",
                                routingLinkId = "RL-1",
                                transactionCodes = listOf("1000", "2000"),
                                billingCodes = listOf("BC1"),
                            ),
                        ),
                )
            val query =
                deleteRoutingInstructions(booking, listOf(booking.room))
                    .mappings
                    .single()
                    .request.queryParameters!!

            query.getValue("daily").equalTo shouldBe "false"
            query.containsKey("startDate") shouldBe false
            query.containsKey("endDate") shouldBe false
            query.getValue("creditLimit").equalTo shouldBe "250.00"
            query.getValue("routingLinkId").equalTo shouldBe "RL-1"
            query.getValue("transactionCode").equalTo shouldBe "1000"
            query.getValue("billingCode").equalTo shouldBe "BC1"
        }

        test("each instruction in each folio gets its own mapping") {
            val booking =
                routingBooking(
                    instructions =
                        listOf(
                            RoutingFolioInstruction(daily = true),
                            RoutingFolioInstruction(daily = false),
                        ),
                )

            deleteRoutingInstructions(booking, listOf(booking.room)).mappings.size shouldBe 2
        }

        test("routing-instruction delete stub installs only for rooms carrying instructions") {
            val withInstructions =
                defaultStubsFor(routingBooking(instructions = listOf(RoutingFolioInstruction())))
            val instructionlessFolio = defaultStubsFor(routingBooking(instructions = emptyList()))

            withInstructions.map { it.id } shouldContain OPERA_ROUTING_INSTRUCTION_DELETE_STUB_ID
            instructionlessFolio.map { it.id } shouldNotContain
                OPERA_ROUTING_INSTRUCTION_DELETE_STUB_ID
        }
    })

private fun routingBooking(instructions: List<RoutingFolioInstruction>): Booking =
    Booking(
        hotels =
            listOf(
                Hotel(
                    hotelId = "RIHOTL",
                    shortId = "ri-hotel",
                    name = "Routing Hotel",
                    addressLine = "1 Folio Way",
                    city = "Leeds",
                    postcode = "RI1 TEST",
                    country = "GB",
                    phone = "+441140000000",
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
                    reservationId = "RSV-RI-1",
                    roomType = "DOUBLE",
                    adults = 1,
                    routingInstructions =
                        listOf(
                            RoutingInstruction(
                                folioWindowNumber = 1,
                                instructions = instructions,
                            ),
                        ),
                ),
            ),
    )
