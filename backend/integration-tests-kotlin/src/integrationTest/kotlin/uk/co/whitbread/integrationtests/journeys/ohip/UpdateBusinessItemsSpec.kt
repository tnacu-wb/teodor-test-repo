package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.clients.ohip.model.BusinessAllowance
import uk.co.whitbread.integrationtests.clients.ohip.model.BusinessItems
import uk.co.whitbread.integrationtests.clients.ohip.model.BusinessItemsUpdateRequest
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_RESERVATION_GET_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.getReservationEmpty
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.mocks.OperaEndpoint
import uk.co.whitbread.integrationtests.testkit.mocks.Upstream
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.OperaPaymentCard
import uk.co.whitbread.integrationtests.testkit.model.Rate
import uk.co.whitbread.integrationtests.testkit.model.ReservationStatus
import uk.co.whitbread.integrationtests.testkit.presets.Hotels
import java.time.LocalDate

/**
 * Proves `PUT /ohip/v1/reservations/business`: the adapter loads every requested reservation
 * from Opera, then applies exactly one branch — business allowances (rules-agent driven),
 * Distribution/BusinessBooker company routing, or Prepaid folio-window-3 card routing — with
 * one Opera change-reservation PUT per reservation, returning `200 OK` with no body.
 *
 * rules-agent-entity-service is a real compose service here, not a WireMock target, so its
 * allowance reads are not counted or stubbed. No endpoint-specific feature flags; the opera
 * token-service flags are environment-pinned OFF (infrastructure scope, not overridable per
 * request), so no overrides are sent.
 *
 * Flow: backend/integration-tests-kotlin/flows/ohip-adapter-service/UpdateBusinessItems.md
 */
class UpdateBusinessItemsSpec :
    JourneySpec(
        "OHIP adapter applies business items to reservations",
        {
            val ohipApi = OhipApi()

            scenario("business allowances from rules-agent are routed onto the reservation") {
                val booking = businessItemsBooking(reservationId = "6011001")
                val room = booking.room

                installFor(booking)

                val result =
                    ohipApi.updateBusinessItems(
                        request =
                            BusinessItemsUpdateRequest(
                                reservationIds = listOf(room.reservationId!!),
                                hotelId = booking.hotel.hotelId,
                                businessItems =
                                    BusinessItems(
                                        purchaseOrderNumber = "PO-1001",
                                        businessAllowances =
                                            listOf(
                                                BusinessAllowance(
                                                    budget = 20.0,
                                                    allowance = "mealDeal",
                                                    isAuthorised = true,
                                                ),
                                            ),
                                        businessNotes = "Integration test business note",
                                    ),
                            ),
                        testId = testId,
                    )

                result.attachEvidence("Update Business Items Allowances")

                expect("acknowledges the applied business allowances with 200 and no body") {
                    result.response.status.value shouldBe 200
                }

                expect("loads the reservation and applies the allowances in one change PUT") {
                    // Two calls: the reservation GET and the change-reservation PUT. The
                    // allowance rules come from the real rules-agent service, not WireMock.
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a Distribution-channel update routes the company to a folio window") {
                val booking = businessItemsBooking(reservationId = "6011002")
                val room = booking.room

                installFor(booking)

                val result =
                    ohipApi.updateBusinessItems(
                        request =
                            BusinessItemsUpdateRequest(
                                reservationIds = listOf(room.reservationId!!),
                                hotelId = booking.hotel.hotelId,
                                companyId = "500123",
                                channel = "DISTR",
                                pibaCardPresent = false,
                            ),
                        testId = testId,
                    )

                result.attachEvidence("Update Business Items Distribution")

                expect("acknowledges the company routing with 200 and no body") {
                    result.response.status.value shouldBe 200
                }

                expect("loads the reservation and attaches the company in one change PUT") {
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a Prepaid-channel update enriches the saved card and routes payment to folio window 3") {
                val booking =
                    businessItemsBooking(
                        reservationId = "6011003",
                        operaPaymentCard =
                            OperaPaymentCard(
                                cardId = "77003",
                                cardNumber = "4111111111111111",
                                expirationDate = LocalDate.now().plusYears(2).toString(),
                                cardHolderName = "Amelia Wright",
                            ),
                    )
                val room = booking.room

                installFor(booking)

                val result =
                    ohipApi.updateBusinessItems(
                        request =
                            BusinessItemsUpdateRequest(
                                reservationIds = listOf(room.reservationId!!),
                                hotelId = booking.hotel.hotelId,
                                channel = "PREPAID",
                            ),
                        testId = testId,
                    )

                result.attachEvidence("Update Business Items Prepaid")

                expect("acknowledges the prepaid routing with 200 and no body") {
                    result.response.status.value shouldBe 200
                }

                expect("loads the reservation, card, and amounts, then routes payment in one change PUT") {
                    // Four calls: reservation GET, Front Desk credit-card-info GET, rate-info
                    // amounts GET, and the change-reservation PUT. Transaction-code rules come
                    // from the real rules-agent service, not WireMock.
                    callCount(Upstream.OPERA) shouldBe 4
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_CREDIT_CARD_INFO) shouldBe 1
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a reservation Opera holds nothing for fails the update without any change PUT") {
                val booking = businessItemsBooking(reservationId = "6011101")
                val room = booking.room

                installFor(booking, excluded = setOf(OPERA_RESERVATION_GET_STUB_ID))
                installStub(getReservationEmpty(booking.hotel.hotelId, room.reservationId!!))

                val result =
                    ohipApi.updateBusinessItems(
                        request =
                            BusinessItemsUpdateRequest(
                                reservationIds = listOf(room.reservationId),
                                hotelId = booking.hotel.hotelId,
                                companyId = "500123",
                                channel = "DISTR",
                                pibaCardPresent = false,
                            ),
                        testId = testId,
                    )

                result.attachEvidence("Update Business Items Missing Reservation")

                expect("returns the mapped apply-business-items error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 28
                }

                expect("stops after the empty reservation read, never PUTting a change") {
                    // One Opera call: the empty reservation GET. The change-reservation PUT
                    // default is installed, so the exact count proves no update was attempted.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an unrecognized channel without business items makes no update after the reservation read") {
                val booking = businessItemsBooking(reservationId = "6011102")
                val room = booking.room

                installFor(booking)

                val result =
                    ohipApi.updateBusinessItems(
                        request =
                            BusinessItemsUpdateRequest(
                                reservationIds = listOf(room.reservationId!!),
                                hotelId = booking.hotel.hotelId,
                                channel = "LEISURE",
                            ),
                        testId = testId,
                    )

                result.attachEvidence("Update Business Items Unrecognized Channel")

                expect("acknowledges with 200 and no body") {
                    result.response.status.value shouldBe 200
                }

                expect("reads the reservation but never PUTs a change") {
                    // One Opera call: the reservation GET only. The change-reservation PUT
                    // default is installed, so the exact count proves the no-op branch.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
        },
    )

private fun businessItemsBooking(
    reservationId: String,
    operaPaymentCard: OperaPaymentCard? = null,
): Booking {
    val arrival = LocalDate.now().plusDays(14)
    val rate = Rate(ratePlan = "SEMIFLEX", roomType = "LOWDBL", adults = 2)

    return Booking(
        hotels = listOf(Hotels.HEAPTI.copy(availableRates = listOf(rate))),
        arrival = arrival,
        departure = arrival.plusDays(2),
        rooms =
            listOf(
                BookingRoom(
                    reservationId = reservationId,
                    roomType = rate.roomType,
                    adults = rate.adults,
                    status = ReservationStatus.RESERVED,
                    operaPaymentCard = operaPaymentCard,
                ),
            ),
    )
}
