package uk.co.whitbread.integrationtests.stubs

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import uk.co.whitbread.integrationtests.framework.wiremock.model.StubMapping
import uk.co.whitbread.integrationtests.stubs.cdh.CDH_COMPANY_SEARCH_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_AVAILABILITY_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_CANCEL_RESERVATION_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_COMPANY_NEGOTIATED_RATES_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_COMPANY_PROFILE_BY_ID_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_COMPANY_PROFILE_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_CREATE_CANCELLATION_POLICY_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_CREATE_RESERVATION_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_DELETE_CANCELLATION_POLICY_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_DEPOSIT_FOLIOS_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_EXTERNAL_REFERENCE_SEARCH_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_HOTEL_CONFIG_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_HOTEL_INVENTORY_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_ITEM_INVENTORY_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_PROFILE_UPDATE_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_RESERVATION_AMOUNTS_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_RESERVATION_DEPOSITS_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_RESERVATION_FOLIOS_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_RESERVATION_GET_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_RESERVATION_PUT_STUB_ID
import uk.co.whitbread.integrationtests.support.fixtures.companyBooking
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.GuestProfile
import uk.co.whitbread.integrationtests.testkit.model.Hotel
import uk.co.whitbread.integrationtests.testkit.model.HotelInventoryItem
import uk.co.whitbread.integrationtests.testkit.model.HotelItemInventory
import uk.co.whitbread.integrationtests.testkit.model.HotelRoomType
import uk.co.whitbread.integrationtests.testkit.model.Rate
import uk.co.whitbread.integrationtests.testkit.model.RoutingInstruction
import java.math.BigDecimal
import java.time.LocalDate

private const val SELECTION_COMPANY_NAME = "Selection Company"

/**
 * Pins [selectedStubs] without WireMock: leftover-check algebra, and the Opera reservation
 * vs availability IDs that characterization cannot snapshot because create-reservation
 * mappings embed `now()`.
 */
class SelectedStubsTest :
    FunSpec({
        test("exclusion drops only the excluded id and keeps remaining default order") {
            val booking = companyBooking(SELECTION_COMPANY_NAME)

            val selected =
                selectedStubs(
                    booking,
                    excluded = setOf(OPERA_COMPANY_PROFILE_BY_ID_STUB_ID),
                )

            selected.map { it.id } shouldContainExactly
                listOf(
                    CDH_COMPANY_SEARCH_STUB_ID,
                    OPERA_PROFILE_UPDATE_STUB_ID,
                    OPERA_COMPANY_PROFILE_STUB_ID,
                    OPERA_COMPANY_NEGOTIATED_RATES_STUB_ID,
                )
        }

        test("unknown exclude IDs fail naming the plan") {
            val booking = companyBooking(SELECTION_COMPANY_NAME)

            val failure =
                shouldThrow<IllegalArgumentException> {
                    selectedStubs(booking, excluded = setOf("not-a-stub"))
                }

            failure.message shouldContain "not-a-stub"
            failure.message shouldContain CDH_COMPANY_SEARCH_STUB_ID
        }

        test("an empty Booking selects nothing") {
            selectedStubs(Booking()).shouldBeEmpty()
        }

        test("a reservation Booking selects create, GET, and deposit folios, not availability") {
            val ids = defaultStubsFor(reservationBooking()).map { it.id }

            ids shouldContain OPERA_CREATE_RESERVATION_STUB_ID
            ids shouldContain OPERA_RESERVATION_GET_STUB_ID
            ids shouldContain OPERA_DEPOSIT_FOLIOS_STUB_ID
            ids shouldContain OPERA_CANCEL_RESERVATION_STUB_ID
            ids shouldContain OPERA_DELETE_CANCELLATION_POLICY_STUB_ID
            ids shouldContain OPERA_CREATE_CANCELLATION_POLICY_STUB_ID
            ids shouldNotContain OPERA_AVAILABILITY_STUB_ID
        }

        test("deposit folios use one generic POST mapping per reservation room") {
            val booking = reservationBooking()
            val mapping =
                defaultStubsFor(booking)
                    .single { it.id == OPERA_DEPOSIT_FOLIOS_STUB_ID }
                    .mappings
                    .single()

            mapping.request.method shouldBe "POST"
            mapping.request.urlPath shouldBe
                "/csh/v1/hotels/${booking.hotel.hotelId}/reservations/${booking.room.reservationId}/depositFolios"
            mapping.request.bodyPatterns shouldBe null
            mapping.response.status shouldBe 201
            mapping.response.jsonBody!!
                .jsonObject
                .getValue("deposits")
                .jsonArray
                .shouldBeEmpty()
        }

        test("reservation deposit reads keep the mapper-safe empty folio shape") {
            val booking = reservationBooking()
            val mapping =
                defaultStubsFor(booking)
                    .single { it.id == OPERA_RESERVATION_DEPOSITS_STUB_ID }
                    .mappings
                    .single()

            mapping.request.method shouldBe "GET"
            mapping.request.urlPath shouldBe "/csh/v1/hotels/${booking.hotel.hotelId}/depositFolio"
            mapping.request.queryParameters
                ?.get("id")
                ?.equalTo shouldBe booking.room.reservationId
            mapping.response.jsonBody!!
                .jsonObject
                .getValue("reservationDepositFoliosInfo")
                .jsonArray
                .single()
                .jsonObject
                .getValue("deposits")
                .jsonArray
                .shouldBeEmpty()
        }

        test("reservation responses use the hotel's VAT region") {
            val booking = reservationBooking(vatRegion = "DE")

            listOf(OPERA_RESERVATION_GET_STUB_ID, OPERA_RESERVATION_PUT_STUB_ID).forEach { stubId ->
                val reservation =
                    defaultStubsFor(booking)
                        .single { it.id == stubId }
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

                reservation
                    .getValue("cashiering")
                    .jsonObject
                    .getValue("taxType")
                    .jsonObject
                    .getValue("code")
                    .jsonPrimitive
                    .content shouldBe "DE"
            }
        }

        test("reservation responses expose nested nightly dates used by deposit posting") {
            val booking = reservationBooking()
            val nestedRate =
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
                    .getValue("roomStay")
                    .jsonObject
                    .getValue("roomRates")
                    .jsonArray
                    .first()
                    .jsonObject
                    .getValue("rates")
                    .jsonObject
                    .getValue("rate")
                    .jsonArray
                    .single()
                    .jsonObject

            nestedRate.getValue("start").jsonPrimitive.content shouldBe stayArrival.toString()
            nestedRate.getValue("end").jsonPrimitive.content shouldBe stayArrival.plusDays(1).toString()
        }

        test("reservation responses expose Booking routing instructions as Opera folio windows") {
            val baseBooking = reservationBooking()
            val booking =
                baseBooking.copy(
                    rooms =
                        listOf(
                            baseBooking.room.copy(
                                routingInstructions = listOf(RoutingInstruction(folioWindowNumber = 2)),
                            ),
                        ),
                )
            val routingInstruction =
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
                    .getValue("routingInstructions")
                    .jsonArray
                    .single()
                    .jsonObject

            routingInstruction
                .getValue("folio")
                .jsonObject
                .getValue("folioWindowNo")
                .jsonPrimitive
                .content shouldBe "2"
        }

        test("part-paid reservations drive the generic amount and folio responses") {
            val baseBooking = reservationBooking()
            val booking =
                baseBooking.copy(
                    rooms =
                        listOf(
                            baseBooking.room.copy(amountAlreadyPaid = 25.0),
                        ),
                )
            val stubs = defaultStubsFor(booking)
            val amountSummary =
                stubs
                    .single { it.id == OPERA_RESERVATION_AMOUNTS_STUB_ID }
                    .mappings
                    // Two idContext caller-shape variants share one response body.
                    .first()
                    .response
                    .jsonBody!!
                    .jsonObject
                    .getValue("summary")
                    .jsonObject
            val folioWindow =
                stubs
                    .single { it.id == OPERA_RESERVATION_FOLIOS_STUB_ID }
                    .mappings
                    .single()
                    .response
                    .jsonBody!!
                    .jsonObject
                    .getValue("reservationFolioInformation")
                    .jsonObject
                    .getValue("folioWindows")
                    .jsonArray
                    .single()
                    .jsonObject

            BigDecimal(amountSummary.getValue("deposit").jsonPrimitive.content)
                .compareTo(BigDecimal("-25")) shouldBe 0
            BigDecimal(amountSummary.getValue("outStandingCostOfStay").jsonPrimitive.content)
                .compareTo(BigDecimal("93")) shouldBe 0
            folioWindow.getValue("emptyFolio").jsonPrimitive.content shouldBe "false"
            BigDecimal(
                folioWindow
                    .getValue("payment")
                    .jsonObject
                    .getValue("amount")
                    .jsonPrimitive
                    .content,
            ).compareTo(BigDecimal("25")) shouldBe 0
            val amountPaid =
                stubs
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
                    .getValue("depositPolicies")
                    .jsonArray
                    .single()
                    .jsonObject
                    .getValue("amountPaid")
                    .jsonObject
                    .getValue("amount")
            BigDecimal(amountPaid.jsonPrimitive.content).compareTo(BigDecimal("25")) shouldBe 0
        }

        test("a configured deposit policy transition links the generic reservation GET and PUT stubs") {
            val baseBooking = reservationBooking()
            val booking =
                baseBooking.copy(
                    rooms =
                        listOf(
                            baseBooking.room.copy(
                                depositPolicyCode = "DEP",
                                depositPolicyCodeAfterUpdate = "ADV",
                            ),
                        ),
                )
            val stubs = defaultStubsFor(booking)
            val reads = stubs.single { it.id == OPERA_RESERVATION_GET_STUB_ID }.mappings
            val update = stubs.single { it.id == OPERA_RESERVATION_PUT_STUB_ID }.mappings.single()
            // Each scenario state is modelled once per known caller fetch-instruction variant.
            val initialReads = reads.filter { it.requiredScenarioState == "Started" }
            val updatedReads = reads.filter { it.requiredScenarioState == "deposit-policy-updated" }
            val initialRead = initialReads.first()
            val updatedRead = updatedReads.first()

            initialReads.size shouldBe updatedReads.size
            reads.size shouldBe initialReads.size + updatedReads.size
            depositPolicyCode(initialRead) shouldBe "DEP"
            depositPolicyCode(updatedRead) shouldBe "ADV"
            initialRead.scenarioName shouldBe updatedRead.scenarioName
            update.scenarioName shouldBe initialRead.scenarioName
            update.requiredScenarioState shouldBe "Started"
            update.newScenarioState shouldBe "deposit-policy-updated"
        }

        test("an external-reference Booking selects a summary search driven by Booking facts") {
            val baseBooking = reservationBooking()
            val guest =
                GuestProfile(
                    profileId = "PROFILE-GATE-1",
                    firstName = "Ada",
                    lastName = "Lovelace",
                    email = "ada.lovelace@test.com",
                    phone = "+447700900100",
                    addressLine = "1 Computing Lane",
                    city = "London",
                    postcode = "SW1A 1AA",
                )
            val booking =
                baseBooking.copy(
                    bookingReference = "BART7421",
                    bookingReferenceIdContext = "BART_OHIP",
                    rooms = listOf(baseBooking.room.copy(sourceCode = "44", guestProfile = guest)),
                )
            val mapping =
                defaultStubsFor(booking)
                    .single { it.id == OPERA_EXTERNAL_REFERENCE_SEARCH_STUB_ID }
                    .mappings
                    .single()
            val reservation =
                mapping.response.jsonBody!!
                    .jsonObject
                    .getValue("reservations")
                    .jsonObject
                    .getValue("reservationInfo")
                    .jsonArray
                    .single()
                    .jsonObject

            mapping.request.method shouldBe "GET"
            mapping.request.urlPath shouldBe "/rsv/v1/reservations"
            mapping.request.queryParameters
                ?.get("externalReferenceIds")
                ?.equalTo shouldBe booking.bookingReference
            mapping.request.queryParameters?.containsKey("fetchInstructions") shouldBe false
            reservation
                .getValue("externalReferences")
                .jsonArray
                .single()
                .jsonObject
                .getValue("idContext")
                .jsonPrimitive
                .content shouldBe "BART_OHIP"
            reservation
                .getValue("roomStay")
                .jsonObject
                .getValue("sourceCode")
                .jsonPrimitive
                .content shouldBe "44"
            reservation
                .getValue("attachedProfiles")
                .jsonArray
                .single()
                .jsonObject
                .getValue("profileIdList")
                .jsonArray
                .single()
                .jsonObject
                .getValue("id")
                .jsonPrimitive
                .content shouldBe guest.profileId
        }

        test("excluding GET on a reservation Booking drops only that id") {
            val ids =
                selectedStubs(
                    reservationBooking(),
                    excluded = setOf(OPERA_RESERVATION_GET_STUB_ID),
                ).map { it.id }

            ids shouldNotContain OPERA_RESERVATION_GET_STUB_ID
            ids shouldContain OPERA_CREATE_RESERVATION_STUB_ID
        }

        test("an availability Booking selects inventory, not reservation GET") {
            val ids = defaultStubsFor(availabilityBooking()).map { it.id }

            ids shouldContain OPERA_AVAILABILITY_STUB_ID
            ids shouldContain OPERA_HOTEL_INVENTORY_STUB_ID
            ids shouldNotContain OPERA_RESERVATION_GET_STUB_ID
        }

        test("hotel room stock selects inventory without Booking rooms") {
            val booking =
                Booking(
                    hotels =
                        listOf(
                            stayHotel().copy(
                                availableRoomTypes =
                                    listOf(
                                        HotelRoomType(
                                            roomClass = "ST",
                                            roomType = "DOUBLE",
                                            numberOfRooms = 3,
                                        ),
                                    ),
                            ),
                        ),
                    arrival = stayArrival,
                    departure = stayDeparture,
                )

            val inventory =
                defaultStubsFor(booking)
                    .single { stub -> stub.id == OPERA_HOTEL_INVENTORY_STUB_ID }

            inventory.mappings
                .single()
                .request
                .queryParameters
                ?.containsKey("roomCountRequested") shouldBe false
        }

        test("hotel item stock selects item inventory without Booking rooms") {
            val booking =
                Booking(
                    hotels =
                        listOf(
                            stayHotel().copy(
                                itemInventory =
                                    HotelItemInventory(
                                        items =
                                            listOf(
                                                HotelInventoryItem(
                                                    code = "COT",
                                                    name = "Travel Cot",
                                                    total = 2,
                                                ),
                                            ),
                                    ),
                            ),
                        ),
                    arrival = stayArrival,
                    departure = stayDeparture,
                )

            defaultStubsFor(booking).map { stub -> stub.id } shouldContain OPERA_ITEM_INVENTORY_STUB_ID
        }

        test("excluding GET on an availability Booking fails naming the plan") {
            val failure =
                shouldThrow<IllegalArgumentException> {
                    selectedStubs(
                        availabilityBooking(),
                        excluded = setOf(OPERA_RESERVATION_GET_STUB_ID),
                    )
                }

            failure.message shouldContain OPERA_RESERVATION_GET_STUB_ID
            failure.message shouldContain OPERA_HOTEL_CONFIG_STUB_ID
        }
    })

private val stayArrival: LocalDate = LocalDate.of(2026, 9, 1)
private val stayDeparture: LocalDate = LocalDate.of(2026, 9, 3)

private fun stayHotel(
    availableRates: List<Rate> = emptyList(),
    vatRegion: String = "UK",
): Hotel =
    Hotel(
        hotelId = "GATE01",
        shortId = "gate-hotel",
        name = "Gate Hotel",
        addressLine = "1 Gate Street",
        city = "London",
        postcode = "SW1A 1AA",
        phone = "02079460000",
        vatRegion = vatRegion,
        availableRates = availableRates,
    )

private fun reservationBooking(vatRegion: String = "UK"): Booking =
    Booking(
        hotels =
            listOf(
                stayHotel(
                    availableRates =
                        listOf(
                            Rate(
                                ratePlan = "FLEX",
                                roomType = "DOUBLE",
                                adults = 2,
                            ),
                        ),
                    vatRegion = vatRegion,
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

private fun availabilityBooking(): Booking =
    Booking(
        hotels =
            listOf(
                stayHotel(
                    availableRates =
                        listOf(
                            Rate(
                                ratePlan = "FLEX",
                                ratePlanSet = "BAR",
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
                    roomType = "DOUBLE",
                    adults = 2,
                ),
            ),
    )

private fun depositPolicyCode(mapping: StubMapping): String =
    mapping.response
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
        .getValue("depositPolicies")
        .jsonArray
        .single()
        .jsonObject
        .getValue("policy")
        .jsonObject
        .getValue("policyCode")
        .jsonPrimitive
        .content
