package uk.co.whitbread.integrationtests.stubs.opera

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import uk.co.whitbread.integrationtests.stubs.defaultStubsFor
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.CancellationPolicyAmountPercent
import uk.co.whitbread.integrationtests.testkit.model.Hotel
import uk.co.whitbread.integrationtests.testkit.model.HotelCancellationReason
import uk.co.whitbread.integrationtests.testkit.model.OperaPaymentCard
import uk.co.whitbread.integrationtests.testkit.model.Rate
import uk.co.whitbread.integrationtests.testkit.model.ReservationCancellationPolicy
import uk.co.whitbread.integrationtests.testkit.model.SelectedPackage
import java.time.LocalDate

class OperaCancellationStubsTest :
    FunSpec({
        test("a hotel without a reason catalogue does not select cancellation-reasons") {
            defaultStubsFor(hotelOnlyBooking()).map { it.id } shouldNotContain OPERA_CANCELLATION_REASONS_STUB_ID
        }

        test("a hotel reason catalogue selects the Opera LOV mapping") {
            val booking =
                hotelOnlyBooking().copy(
                    hotels =
                        listOf(
                            stayHotel().copy(
                                cancellationReasons =
                                    listOf(
                                        HotelCancellationReason(
                                            code = "ILL",
                                            name = "MA Illness",
                                            description = "MA Illness",
                                        ),
                                        HotelCancellationReason(
                                            code = "OTH",
                                            name = "Other",
                                            description = "Other",
                                        ),
                                    ),
                            ),
                        ),
                )
            val mapping =
                defaultStubsFor(booking)
                    .single { it.id == OPERA_CANCELLATION_REASONS_STUB_ID }
                    .mappings
                    .single()
            val items =
                mapping.response.jsonBody!!
                    .jsonObject
                    .getValue("listOfValues")
                    .jsonObject
                    .getValue("items")
                    .jsonArray

            mapping.request.method shouldBe "GET"
            mapping.request.urlPath shouldBe "/lov/v1/listOfValues/CancellationReasons"
            mapping.request.headers
                ?.get("x-hotelid")
                ?.equalTo shouldBe booking.hotel.hotelId
            items.size shouldBe 2
            items
                .first()
                .jsonObject
                .getValue("code")
                .jsonPrimitive
                .content shouldBe "ILL"
        }

        test("a reservation Booking selects cancel and cancellation-policy stubs") {
            val ids = defaultStubsFor(reservationBooking()).map { it.id }

            ids shouldContain OPERA_CANCEL_RESERVATION_STUB_ID
            ids shouldContain OPERA_DELETE_CANCELLATION_POLICY_STUB_ID
            ids shouldContain OPERA_CREATE_CANCELLATION_POLICY_STUB_ID
        }

        test("a reservation ID selects cancellation without stay or occupancy facts") {
            val booking =
                hotelOnlyBooking().copy(
                    rooms = listOf(BookingRoom(reservationId = "RSV-MINIMAL-1")),
                )
            val mapping =
                defaultStubsFor(booking)
                    .single { it.id == OPERA_CANCEL_RESERVATION_STUB_ID }
                    .mappings
                    .single()

            mapping.request.method shouldBe "POST"
            mapping.request.urlPath shouldBe
                "/rsv/v1/hotels/${booking.hotel.hotelId}/reservations/RSV-MINIMAL-1/cancellations"
        }

        test("cancel POST is urlPath-only and returns a Cancellation unique id") {
            val booking = reservationBooking()
            val mapping =
                defaultStubsFor(booking)
                    .single { it.id == OPERA_CANCEL_RESERVATION_STUB_ID }
                    .mappings
                    .single()
            val ids =
                mapping.response.jsonBody!!
                    .jsonObject
                    .getValue("reservations")
                    .jsonArray
                    .single()
                    .jsonObject
                    .getValue("reservationIdList")
                    .jsonArray
                    .map { it.jsonObject }

            mapping.request.method shouldBe "POST"
            mapping.request.urlPath shouldBe
                "/rsv/v1/hotels/${booking.hotel.hotelId}/reservations/${booking.room.reservationId}/cancellations"
            mapping.request.bodyPatterns.shouldBeNull()
            ids
                .single { it.getValue("type").jsonPrimitive.content == "Cancellation" }
                .getValue("id")
                .jsonPrimitive
                .content shouldBe cancellationIdFor(booking.room.reservationId!!)
        }

        test("reservation GET omits cancellationPolicies until the room carries that fact") {
            val withoutPolicy =
                defaultStubsFor(reservationBooking())
                    .single { it.id == OPERA_RESERVATION_GET_STUB_ID }
                    .mappings
                    .first()
                    .response
                    .jsonBody!!
                    .jsonObject
                    .getValue("reservations")
                    .jsonObject
                    .getValue("reservation")
                    .jsonArray
                    .single()
                    .jsonObject
                    .getValue("reservationPolicies")
                    .jsonObject

            withoutPolicy.containsKey("cancellationPolicies") shouldBe false
            withoutPolicy.containsKey("depositPolicies") shouldBe true
        }

        test("reservation GET emits cancellationPolicies from the room fact") {
            val booking =
                reservationBooking().copy(
                    rooms =
                        listOf(
                            reservationBooking().room.copy(
                                cancellationPolicies =
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
                                    ),
                            ),
                        ),
                )
            val policies =
                defaultStubsFor(booking)
                    .single { it.id == OPERA_RESERVATION_GET_STUB_ID }
                    .mappings
                    .first()
                    .response
                    .jsonBody!!
                    .jsonObject
                    .getValue("reservations")
                    .jsonObject
                    .getValue("reservation")
                    .jsonArray
                    .single()
                    .jsonObject
                    .getValue("reservationPolicies")
                    .jsonObject
                    .getValue("cancellationPolicies")
                    .jsonArray
                    .single()
                    .jsonObject
            val deleteMapping =
                defaultStubsFor(booking)
                    .single { it.id == OPERA_DELETE_CANCELLATION_POLICY_STUB_ID }
                    .mappings
                    .single()

            policies
                .getValue("policyId")
                .jsonObject
                .getValue("id")
                .jsonPrimitive
                .content shouldBe "699382"
            policies
                .getValue("policy")
                .jsonObject
                .getValue("deadline")
                .jsonObject
                .getValue("absoluteDeadline")
                .jsonPrimitive
                .content shouldBe "2026-08-01"
            policies.getValue("revenueType").jsonPrimitive.content shouldBe "Rooms"
            policies
                .getValue("policy")
                .jsonObject
                .getValue("amountPercent")
                .jsonObject
                .let { amountPercent ->
                    amountPercent.getValue("basisType").jsonPrimitive.content shouldBe "FlatAmount"
                    amountPercent.getValue("nights").jsonPrimitive.content shouldBe "1"
                    amountPercent.getValue("percent").jsonPrimitive.content shouldBe "100.0"
                    amountPercent.getValue("amount").jsonPrimitive.content shouldBe "59.0"
                }
            policies
                .getValue("policy")
                .jsonObject
                .getValue("policyCode")
                .jsonPrimitive
                .content shouldBe "DOA"
            policies
                .getValue("policy")
                .jsonObject
                .getValue("manual")
                .jsonPrimitive
                .content shouldBe "false"
            policies
                .getValue("policy")
                .jsonObject
                .getValue("effective")
                .jsonPrimitive
                .content shouldBe "false"
            policies.getValue("percentageDue").jsonPrimitive.content shouldBe "100.0"
            policies.getValue("comments").jsonPrimitive.content shouldBe "Cancellations after 1pm"
            deleteMapping.request.queryParameters
                ?.get("policyId")
                ?.equalTo shouldBe "699382"
        }

        test("multiple room policies all render on reservation GET and delete pins the first") {
            val firstPolicy =
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
                )
            val secondPolicy =
                firstPolicy.copy(policyId = "699383", policyCode = "48H", deadline = "2026-07-30")
            val booking =
                reservationBooking().copy(
                    rooms =
                        listOf(
                            reservationBooking().room.copy(
                                cancellationPolicies = listOf(firstPolicy, secondPolicy),
                            ),
                        ),
                )
            val policyIds =
                defaultStubsFor(booking)
                    .single { it.id == OPERA_RESERVATION_GET_STUB_ID }
                    .mappings
                    .first()
                    .response
                    .jsonBody!!
                    .jsonObject
                    .getValue("reservations")
                    .jsonObject
                    .getValue("reservation")
                    .jsonArray
                    .single()
                    .jsonObject
                    .getValue("reservationPolicies")
                    .jsonObject
                    .getValue("cancellationPolicies")
                    .jsonArray
                    .map { policy ->
                        policy.jsonObject
                            .getValue("policyId")
                            .jsonObject
                            .getValue("id")
                            .jsonPrimitive
                            .content
                    }
            val deleteMapping =
                defaultStubsFor(booking)
                    .single { it.id == OPERA_DELETE_CANCELLATION_POLICY_STUB_ID }
                    .mappings
                    .single()

            policyIds shouldBe listOf("699382", "699383")
            deleteMapping.request.queryParameters
                ?.get("policyId")
                ?.equalTo shouldBe "699382"
        }

        test("delete policy without a room fact matches the path only") {
            val mapping =
                defaultStubsFor(reservationBooking())
                    .single { it.id == OPERA_DELETE_CANCELLATION_POLICY_STUB_ID }
                    .mappings
                    .single()

            mapping.request.method shouldBe "DELETE"
            mapping.request.urlPath shouldBe
                "/rsv/v1/hotels/${reservationBooking().hotel.hotelId}/reservations/${reservationBooking().room.reservationId}/cancellationPolicies"
            mapping.request.queryParameters.shouldBeNull()
            mapping.response.status shouldBe 200
        }

        test("a CITYTAX package selects a detail rate-info mapping") {
            val booking =
                reservationBooking().copy(
                    rooms =
                        listOf(
                            reservationBooking().room.copy(
                                selectedPackages = listOf(SelectedPackage(code = "CITYTAX")),
                            ),
                        ),
                )
            val mapping =
                defaultStubsFor(booking)
                    .single { it.id == OPERA_CITY_TAX_RATE_INFO_STUB_ID }
                    .mappings
                    .single()
            val cityTax =
                mapping.response.jsonBody!!
                    .jsonObject
                    .getValue("detail")
                    .jsonObject
                    .getValue("packages")
                    .jsonArray
                    .single()
                    .jsonObject

            mapping.request.method shouldBe "GET"
            mapping.request.urlPath shouldBe
                "/rsv/v1/hotels/${booking.hotel.hotelId}/reservations/rateInfo"
            mapping.request.queryParameters
                ?.get("summaryInfo")
                ?.equalTo shouldBe "false"
            mapping.request.queryParameters
                ?.get("detailDate")
                ?.equalTo shouldBe stayArrival.toString()
            cityTax.getValue("code").jsonPrimitive.content shouldBe "CITYTAX"
            defaultStubsFor(reservationBooking()).map { it.id } shouldNotContain OPERA_CITY_TAX_RATE_INFO_STUB_ID
        }

        test("an Opera payment card selects credit-card-info and reservation GET card identity") {
            val card =
                OperaPaymentCard(
                    cardId = "12345",
                    cardType = "Va",
                    cardNumber = "4764776852337921103",
                    expirationDate = "2025-03-31",
                    cardHolderName = "Charlie",
                )
            val booking =
                reservationBooking().copy(
                    rooms = listOf(reservationBooking().room.copy(operaPaymentCard = card)),
                )
            val creditCardMapping =
                defaultStubsFor(booking)
                    .single { it.id == OPERA_CREDIT_CARD_INFO_STUB_ID }
                    .mappings
                    .single()
            val creditCardInfo =
                creditCardMapping.response
                    .jsonBody!!
                    .jsonObject
                    .getValue("creditCard")
                    .jsonObject
            val paymentMethod =
                defaultStubsFor(booking)
                    .single { it.id == OPERA_RESERVATION_GET_STUB_ID }
                    .mappings
                    .first()
                    .response
                    .jsonBody!!
                    .jsonObject
                    .getValue("reservations")
                    .jsonObject
                    .getValue("reservation")
                    .jsonArray
                    .single()
                    .jsonObject
                    .getValue("reservationPaymentMethods")
                    .jsonArray
                    .single()
                    .jsonObject

            creditCardMapping.request.urlPath shouldBe "/fof/config/v1/creditCardInfo"
            creditCardMapping.request.queryParameters
                ?.get("cardId")
                ?.equalTo shouldBe card.cardId
            creditCardInfo.getValue("cardNumberMasked").jsonPrimitive.content shouldBe "XXXXXXXXXXXX1103"
            creditCardInfo.getValue("expirationDateMasked").jsonPrimitive.content shouldBe "03/25"
            paymentMethod.getValue("paymentMethod").jsonPrimitive.content shouldBe "VA"
            paymentMethod
                .getValue("paymentCard")
                .jsonObject
                .getValue("cardId")
                .jsonObject
                .getValue("id")
                .jsonPrimitive
                .content shouldBe card.cardId
            paymentMethod
                .getValue("paymentCard")
                .jsonObject
                .getValue("cardNumberMasked")
                .jsonPrimitive
                .content shouldBe "XXXXXXXXXXXX1103"
            paymentMethod
                .getValue("paymentCard")
                .jsonObject
                .getValue("expirationDateMasked")
                .jsonPrimitive
                .content shouldBe "XX/XX"
            defaultStubsFor(reservationBooking()).map { it.id } shouldNotContain OPERA_CREDIT_CARD_INFO_STUB_ID
        }

        test("rooms sharing one Opera card select one credit-card-info mapping") {
            val card =
                OperaPaymentCard(
                    cardId = "shared-card",
                    cardType = "Va",
                    cardNumber = "4764776852337921103",
                    expirationDate = "2025-03-31",
                    cardHolderName = "Charlie",
                )
            val room = reservationBooking().room
            val booking =
                reservationBooking().copy(
                    rooms =
                        listOf(
                            room.copy(reservationId = "RSV-CARD-1", operaPaymentCard = card),
                            room.copy(reservationId = "RSV-CARD-2", operaPaymentCard = card),
                        ),
                )

            val mappings =
                defaultStubsFor(booking)
                    .single { it.id == OPERA_CREDIT_CARD_INFO_STUB_ID }
                    .mappings

            mappings.size shouldBe 1
        }

        test("rooms using one Opera card ID with conflicting facts are rejected") {
            val firstCard =
                OperaPaymentCard(
                    cardId = "shared-card",
                    cardType = "Va",
                    cardNumber = "4764776852337921103",
                    expirationDate = "2025-03-31",
                    cardHolderName = "Charlie",
                )
            val secondCard = firstCard.copy(cardNumber = "4764776852337929876")
            val room = reservationBooking().room
            val booking =
                reservationBooking().copy(
                    rooms =
                        listOf(
                            room.copy(reservationId = "RSV-CARD-1", operaPaymentCard = firstCard),
                            room.copy(reservationId = "RSV-CARD-2", operaPaymentCard = secondCard),
                        ),
                )

            val failure =
                shouldThrow<IllegalArgumentException> {
                    defaultStubsFor(booking)
                }

            failure.message shouldBe
                "Opera cardId 'shared-card' must have identical card facts across booking rooms"
        }
    })

private val stayArrival: LocalDate = LocalDate.of(2026, 9, 1)
private val stayDeparture: LocalDate = LocalDate.of(2026, 9, 3)

private fun stayHotel(): Hotel =
    Hotel(
        hotelId = "GATE01",
        shortId = "gate-hotel",
        name = "Gate Hotel",
        addressLine = "1 Gate Street",
        city = "London",
        postcode = "SW1A 1AA",
        phone = "02079460000",
    )

private fun hotelOnlyBooking(): Booking = Booking(hotels = listOf(stayHotel()))

private fun reservationBooking(): Booking =
    Booking(
        hotels =
            listOf(
                stayHotel().copy(
                    availableRates =
                        listOf(
                            Rate(
                                ratePlan = "FLEX",
                                roomType = "DOUBLE",
                                adults = 2,
                            ),
                        ),
                ),
            ),
        arrival = stayArrival,
        departure = stayDeparture,
        rooms =
            listOf(
                BookingRoom(
                    reservationId = "RSV-GATE-1",
                    roomType = "DOUBLE",
                    adults = 2,
                ),
            ),
    )
