package uk.co.whitbread.integrationtests.testkit.model

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import java.time.LocalDate

class BookingJsonContractTest :
    FunSpec({
        test("minimal JSON uses Booking defaults") {
            bookingJson.decodeFromString<Booking>(contractJson("booking-minimal.json")) shouldBe Booking()
        }

        test("representative JSON decodes Booking data and defaults") {
            val booking = bookingJson.decodeFromString<Booking>(representativeJson)

            booking.hotels shouldHaveSize 1
            booking.hotel.hotelId shouldBe "HEAPTI"
            booking.hotel.country shouldBe "GB"
            booking.hotel.vatRegion shouldBe "UK"
            booking.hotel.availableRates
                .single()
                .nightlyRate shouldBe 59.0
            booking.hotel.availableRates
                .single()
                .ratePlanSet shouldBe "PBN"
            booking.hotel.itemInventory
                ?.items
                ?.single()
                ?.available shouldBe 2
            booking.companies.single().active shouldBe true
            booking.arrival shouldBe LocalDate.parse("2026-08-01")
            booking.departure shouldBe LocalDate.parse("2026-08-03")
            booking.room.status shouldBe ReservationStatus.RESERVED
            booking.room.children shouldBe 0
            booking.room.sourceCode shouldBe "44"
            booking.room.ratePlan shouldBe null
            booking.room.roomTypeAfterUpdate shouldBe null
            booking.room.depositPolicyCodeAfterUpdate shouldBe null
            booking.room.depositPaymentReference shouldBe null
            booking.room.selectedPackages
                .single()
                .quantity shouldBe 1
            booking.aem?.footer?.site shouldBe AemSite.BUSINESS_BOOKER
            booking.loggedUser?.tetheredAccount?.userRole shouldBe TetheredUserRole.ACCOUNT_HOLDER
            booking.loggedUser
                ?.tetheredAccount
                ?.accountActivity
                ?.transactionAggregates
                ?.single()
                ?.toDate shouldBe LocalDate.parse("2026-08-31")
            booking.bookingReferenceIdContext shouldBe "WB_DIGITAL"
        }

        test("representative Booking round trips through the shared JSON contract") {
            val booking = bookingJson.decodeFromString<Booking>(representativeJson)

            bookingJson.decodeFromString<Booking>(bookingJson.encodeToString(booking)) shouldBe booking
        }

        test("promotional rate facts round trip through the shared JSON contract") {
            val rate =
                bookingJson.decodeFromString<Rate>(
                    """{"ratePlan":"PROMOFLEX","promotionCode":"SUMMER20","dynamicBaseRatePlan":"FLEXRATE","roomType":"DOUBLE","adults":2}""",
                )

            rate.promotionCode shouldBe "SUMMER20"
            rate.dynamicBaseRatePlan shouldBe "FLEXRATE"
            bookingJson.decodeFromString<Rate>(bookingJson.encodeToString(rate)) shouldBe rate
        }

        test("reservation deposit policy transitions round trip through the shared JSON contract") {
            val room =
                bookingJson.decodeFromString<BookingRoom>(
                    """{"depositPolicyCode":"DEP","depositPolicyCodeAfterUpdate":"ADV"}""",
                )

            room.depositPolicyCode shouldBe "DEP"
            room.depositPolicyCodeAfterUpdate shouldBe "ADV"
            bookingJson.decodeFromString<BookingRoom>(bookingJson.encodeToString(room)) shouldBe room
        }

        test("reservation room type transitions round trip through the shared JSON contract") {
            val room =
                bookingJson.decodeFromString<BookingRoom>(
                    """{"roomType":"DOUBLE","roomTypeAfterUpdate":"TWIN"}""",
                )

            room.roomType shouldBe "DOUBLE"
            room.roomTypeAfterUpdate shouldBe "TWIN"
            bookingJson.decodeFromString<BookingRoom>(bookingJson.encodeToString(room)) shouldBe room
        }

        test("reservation rate plan round trips through the shared JSON contract") {
            val room =
                bookingJson.decodeFromString<BookingRoom>(
                    """{"roomType":"DOUBLE","ratePlan":"SEMIFLEX"}""",
                )

            room.roomType shouldBe "DOUBLE"
            room.ratePlan shouldBe "SEMIFLEX"
            bookingJson.decodeFromString<BookingRoom>(bookingJson.encodeToString(room)) shouldBe room
        }

        test("reservation routing instructions round trip through the shared JSON contract") {
            val room =
                bookingJson.decodeFromString<BookingRoom>(
                    """{"routingInstructions":[{"folioWindowNumber":2}]}""",
                )

            room.routingInstructions shouldBe listOf(RoutingInstruction(folioWindowNumber = 2))
            bookingJson.decodeFromString<BookingRoom>(bookingJson.encodeToString(room)) shouldBe room
        }

        test("reservation amount already paid round trips through the shared JSON contract") {
            val room =
                bookingJson.decodeFromString<BookingRoom>(
                    """{"amountAlreadyPaid":50.0}""",
                )

            room.amountAlreadyPaid shouldBe 50.0
            bookingJson.decodeFromString<BookingRoom>(bookingJson.encodeToString(room)) shouldBe room
        }

        test("posted reservation deposit facts round trip through the shared JSON contract") {
            val room =
                bookingJson.decodeFromString<BookingRoom>(
                    """{"amountAlreadyPaid":72.5,"depositPaymentReference":"PAY-7421"}""",
                )

            room.amountAlreadyPaid shouldBe 72.5
            room.depositPaymentReference shouldBe "PAY-7421"
            bookingJson.decodeFromString<BookingRoom>(bookingJson.encodeToString(room)) shouldBe room
        }

        test("reservation cancellation policy round trips through the shared JSON contract") {
            val room =
                bookingJson.decodeFromString<BookingRoom>(
                    """{"cancellationPolicies":[{"policyId":"699382","deadline":"2026-08-01","revenueType":"Rooms","amountPercent":{"basisType":"FlatAmount","nights":1,"percent":100.0,"amount":59.0},"policyCode":"DOA","manual":false,"effective":false,"percentageDue":100.0,"comments":"Cancellations after 1pm"}]}""",
                )

            room.cancellationPolicies shouldBe
                listOf(
                    ReservationCancellationPolicy(
                        policyId = "699382",
                        deadline = "2026-08-01",
                        revenueType = "Rooms",
                        amountPercent =
                            CancellationPolicyAmountPercent(
                                basisType = "FlatAmount",
                                nights = 1,
                                percent = 100.0,
                                amount = 59.0,
                            ),
                        policyCode = "DOA",
                        manual = false,
                        effective = false,
                        percentageDue = 100.0,
                        comments = "Cancellations after 1pm",
                    ),
                )
            bookingJson.decodeFromString<BookingRoom>(bookingJson.encodeToString(room)) shouldBe room
        }

        test("Opera payment card facts round trip through the shared JSON contract") {
            val room =
                bookingJson.decodeFromString<BookingRoom>(
                    """{"operaPaymentCard":{"cardId":"12345","cardType":"Va","cardNumber":"4764776852337921103","expirationDate":"2025-03-31"}}""",
                )

            room.operaPaymentCard shouldBe
                OperaPaymentCard(
                    cardId = "12345",
                    cardType = "Va",
                    cardNumber = "4764776852337921103",
                    expirationDate = "2025-03-31",
                )
            bookingJson.decodeFromString<BookingRoom>(bookingJson.encodeToString(room)) shouldBe room
        }

        test("hotel cancellation reasons round trip through the shared JSON contract") {
            val hotel =
                bookingJson.decodeFromString<Hotel>(
                    """{"hotelId":"HEAPTI","shortId":"AQN","name":"Test","addressLine":"1 Street","city":"London","postcode":"SW1A 1AA","phone":"02000000000","cancellationReasons":[{"code":"CXL","name":"Trip Cancelled","description":"MA Trip Cancelled"}]}""",
                )

            hotel.cancellationReasons shouldBe
                listOf(
                    HotelCancellationReason(
                        code = "CXL",
                        name = "Trip Cancelled",
                        description = "MA Trip Cancelled",
                    ),
                )
            bookingJson.decodeFromString<Hotel>(bookingJson.encodeToString(hotel)) shouldBe hotel
        }

        test("Opera external-reference facts round trip through the shared JSON contract") {
            val booking =
                bookingJson.decodeFromString<Booking>(
                    """{"bookingReference":"BART7421","bookingReferenceIdContext":"BART_OHIP","rooms":[{"sourceCode":"44"}]}""",
                )

            booking.bookingReference shouldBe "BART7421"
            booking.bookingReferenceIdContext shouldBe "BART_OHIP"
            booking.room.sourceCode shouldBe "44"
            bookingJson.decodeFromString<Booking>(bookingJson.encodeToString(booking)) shouldBe booking
        }

        test("booker identity round trips through the shared JSON contract") {
            val booking =
                bookingJson.decodeFromString<Booking>(
                    """{"booker":{"firstName":"Ada","lastName":"Blackwood","title":"Ms","email":"ada.blackwood@example.com"}}""",
                )

            booking.booker shouldBe
                Booker(
                    firstName = "Ada",
                    lastName = "Blackwood",
                    title = "Ms",
                    email = "ada.blackwood@example.com",
                )
            bookingJson.decodeFromString<Booking>(bookingJson.encodeToString(booking)) shouldBe booking
        }

        test("booker identity needs a first and last name") {
            listOf("", "   ").forEach { blank ->
                shouldThrow<IllegalArgumentException> {
                    Booker(firstName = blank, lastName = "Blackwood")
                }.message.orEmpty() shouldContain "booker.firstName must not be blank"
                shouldThrow<IllegalArgumentException> {
                    Booker(firstName = "Ada", lastName = blank)
                }.message.orEmpty() shouldContain "booker.lastName must not be blank"
            }
        }

        test("CDH booking reference round trips through the shared JSON contract") {
            val booking =
                bookingJson.decodeFromString<Booking>(
                    """{"cdhBookingReference":"00600421"}""",
                )

            booking.cdhBookingReference shouldBe "00600421"
            bookingJson.decodeFromString<Booking>(bookingJson.encodeToString(booking)) shouldBe booking
        }

        test("unknown fields are rejected") {
            val failure =
                shouldThrow<SerializationException> {
                    bookingJson.decodeFromString<Booking>("""{"unknown":true}""")
                }

            failure.message.orEmpty() shouldContain "unknown"
        }

        test("malformed ISO date is rejected") {
            val failure =
                shouldThrow<SerializationException> {
                    bookingJson.decodeFromString<Booking>("""{"arrival":"01-08-2026"}""")
                }

            failure.message.orEmpty() shouldContain "Invalid ISO-8601 date"
        }

        test("departure must be after arrival") {
            listOf("2026-08-01", "2026-07-31").forEach { departure ->
                val failure =
                    shouldThrow<IllegalArgumentException> {
                        bookingJson.decodeFromString<Booking>(
                            """{"arrival":"2026-08-01","departure":"$departure"}""",
                        )
                    }

                failure.message.orEmpty() shouldContain "booking.departure must be after booking.arrival"
            }
        }

        test("reservation ID must be null or non-blank") {
            listOf("", "   ").forEach { reservationId ->
                val failure =
                    shouldThrow<IllegalArgumentException> {
                        bookingJson.decodeFromString<Booking>(
                            """{"rooms":[{"reservationId":"$reservationId"}]}""",
                        )
                    }

                failure.message.orEmpty() shouldContain "bookingRoom.reservationId must not be blank"
            }
        }

        test("external-reference context and source code must not be blank") {
            shouldThrow<IllegalArgumentException> {
                Booking(bookingReferenceIdContext = " ")
            }.message.orEmpty() shouldContain "booking.bookingReferenceIdContext must not be blank"

            shouldThrow<IllegalArgumentException> {
                BookingRoom(sourceCode = " ")
            }.message.orEmpty() shouldContain "bookingRoom.sourceCode must not be blank"
        }

        test("a cancellation date round trips and requires a cancelled room") {
            val room =
                bookingJson.decodeFromString<BookingRoom>(
                    """{"status":"CANCELLED","cancellationDate":"2026-08-20"}""",
                )

            room.cancellationDate shouldBe LocalDate.parse("2026-08-20")
            bookingJson.decodeFromString<BookingRoom>(bookingJson.encodeToString(room)) shouldBe room

            shouldThrow<IllegalArgumentException> {
                bookingJson.decodeFromString<BookingRoom>(
                    """{"status":"RESERVED","cancellationDate":"2026-08-20"}""",
                )
            }.message.orEmpty() shouldContain "bookingRoom.cancellationDate requires status CANCELLED"
        }

        test("the Opera hold fact round trips through the shared JSON contract and defaults to false") {
            val room = bookingJson.decodeFromString<BookingRoom>("""{"heldInOpera":true}""")

            room.heldInOpera shouldBe true
            bookingJson.decodeFromString<BookingRoom>(bookingJson.encodeToString(room)) shouldBe room
            bookingJson.decodeFromString<BookingRoom>("{}").heldInOpera shouldBe false
        }

        test("the de-reg-card fact round trips through the shared JSON contract and defaults to false") {
            val room =
                bookingJson.decodeFromString<BookingRoom>(
                    """{"preRegistration":{"deRegCardCompleted":true}}""",
                )

            room.preRegistration?.deRegCardCompleted shouldBe true
            bookingJson.decodeFromString<BookingRoom>(bookingJson.encodeToString(room)) shouldBe room
            bookingJson
                .decodeFromString<BookingRoom>("""{"preRegistration":{}}""")
                .preRegistration
                ?.deRegCardCompleted shouldBe false
        }

        test("CDH booking reference must be null or non-blank") {
            listOf("", "   ").forEach { reference ->
                shouldThrow<IllegalArgumentException> {
                    Booking(cdhBookingReference = reference)
                }.message.orEmpty() shouldContain "booking.cdhBookingReference must not be blank"
            }
        }

        test("updated deposit policy code must be null or non-blank") {
            listOf("", "   ").forEach { policyCode ->
                val failure =
                    shouldThrow<IllegalArgumentException> {
                        bookingJson.decodeFromString<BookingRoom>(
                            """{"depositPolicyCodeAfterUpdate":"$policyCode"}""",
                        )
                    }

                failure.message.orEmpty() shouldContain
                    "bookingRoom.depositPolicyCodeAfterUpdate must not be blank"
            }
        }

        test("updated room type must be null or non-blank") {
            listOf("", "   ").forEach { roomType ->
                val failure =
                    shouldThrow<IllegalArgumentException> {
                        bookingJson.decodeFromString<BookingRoom>(
                            """{"roomTypeAfterUpdate":"$roomType"}""",
                        )
                    }

                failure.message.orEmpty() shouldContain
                    "bookingRoom.roomTypeAfterUpdate must not be blank"
            }
        }

        test("reservation rate plan must be null or non-blank") {
            listOf("", "   ").forEach { ratePlan ->
                val failure =
                    shouldThrow<IllegalArgumentException> {
                        bookingJson.decodeFromString<BookingRoom>(
                            """{"ratePlan":"$ratePlan"}""",
                        )
                    }

                failure.message.orEmpty() shouldContain "bookingRoom.ratePlan must not be blank"
            }
        }

        test("routing folio window number must be positive") {
            listOf(0, -1).forEach { folioWindowNumber ->
                val failure =
                    shouldThrow<IllegalArgumentException> {
                        bookingJson.decodeFromString<BookingRoom>(
                            """{"routingInstructions":[{"folioWindowNumber":$folioWindowNumber}]}""",
                        )
                    }

                failure.message.orEmpty() shouldContain
                    "routingInstruction.folioWindowNumber must be greater than zero"
            }
        }

        test("amount already paid must be finite and non-negative") {
            listOf(-0.01, Double.POSITIVE_INFINITY, Double.NaN).forEach { amountAlreadyPaid ->
                val failure =
                    shouldThrow<IllegalArgumentException> {
                        BookingRoom(amountAlreadyPaid = amountAlreadyPaid)
                    }

                failure.message.orEmpty() shouldContain
                    "bookingRoom.amountAlreadyPaid must be finite and non-negative"
            }
        }

        test("deposit payment reference must be null or non-blank") {
            listOf("", "   ").forEach { paymentReference ->
                val failure =
                    shouldThrow<IllegalArgumentException> {
                        BookingRoom(depositPaymentReference = paymentReference)
                    }

                failure.message.orEmpty() shouldContain
                    "bookingRoom.depositPaymentReference must not be blank"
            }
        }

        test("invalid enum value is rejected") {
            val failure =
                shouldThrow<SerializationException> {
                    bookingJson.decodeFromString<Booking>(
                        """{"rooms":[{"status":"UNKNOWN"}]}""",
                    )
                }

            failure.message.orEmpty() shouldContain "ReservationStatus"
        }
    })

private val representativeJson: String = contractJson("booking-representative.json")

private fun contractJson(fileName: String): String =
    checkNotNull(BookingJsonContractTest::class.java.getResource("/contracts/$fileName")) {
        "Missing contract resource: $fileName"
    }.readText()
