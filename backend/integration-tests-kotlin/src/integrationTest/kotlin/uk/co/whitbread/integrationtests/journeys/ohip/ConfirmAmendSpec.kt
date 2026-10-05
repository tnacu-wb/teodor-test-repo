package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.clients.ohip.model.ConfirmAmendRequest
import uk.co.whitbread.integrationtests.clients.ohip.model.UpdateBookingChannel
import uk.co.whitbread.integrationtests.stubs.opera.custom.getReservationEmpty
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.mocks.OperaEndpoint
import uk.co.whitbread.integrationtests.testkit.mocks.Upstream
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.GuestProfile
import uk.co.whitbread.integrationtests.testkit.model.OperaPaymentCard
import uk.co.whitbread.integrationtests.testkit.model.Rate
import uk.co.whitbread.integrationtests.testkit.model.ReservationStatus
import uk.co.whitbread.integrationtests.testkit.presets.Hotels
import java.time.LocalDate

/**
 * Proves `PUT /ohip/v1/reservations/confirmAmend`: the adapter light-reads each original
 * reservation, loads its linked temp reservation through the full basket read, copies the
 * temp state onto the original with one Opera change-reservation PUT, optionally converts
 * the originals to pay-on-arrival, and returns the refreshed originals as a basket response
 * with rate and folio enrichment.
 *
 * No endpoint-specific feature flags; the opera token-service flags are environment-pinned
 * OFF (infrastructure scope, not overridable per request), so no overrides are sent.
 * rules-agent-entity-service source-info reads are real compose calls, not WireMock.
 *
 * Flow: backend/integration-tests-kotlin/flows/ohip-adapter-service/ConfirmAmend.md
 */
class ConfirmAmendSpec :
    JourneySpec(
        "OHIP adapter confirms an amend onto the original reservation",
        {
            val ohipApi = OhipApi()

            scenario("a linked temp reservation's state is confirmed onto the original and the refreshed basket returned") {
                val booking = confirmAmendBooking(originalId = "6014001", tempId = "6014002")

                installFor(booking)

                val result =
                    ohipApi.confirmAmend(
                        request =
                            ConfirmAmendRequest(
                                hotelId = booking.hotel.hotelId,
                                bookingChannel = UpdateBookingChannel(channel = "CCUI", subchannel = "WEB", language = "en"),
                                tempReservations = listOf("6014002"),
                                originalReservations = listOf("6014001"),
                                linkAmendReservations = mapOf("6014001" to "6014002"),
                                sendEmailConfirmation = true,
                                sendEmailInvoice = false,
                            ),
                        testId = testId,
                    )

                result.attachEvidence("Confirm Amend")

                expect("returns the refreshed original as a basket response") {
                    result.response.status.value shouldBe 200
                    result.body.reservationByIdList shouldHaveSize 1
                    result.body.reservationByIdList
                        .first()
                        .reservationId shouldBe "6014001"
                }

                expect("reads the original and temp, applies one change PUT, and re-reads the enriched original") {
                    // Eleven calls: the original's light read (1), the temp's basket read
                    // (reservation, CRM profile, hotel config — 3), the pre-update re-read of
                    // the original for UDF/profile carry-over (1), the change-reservation PUT
                    // onto the original (1), and the final enriched basket read of the original
                    // (reservation, rate-info amounts, folios, CRM profile, hotel config — 5).
                    callCount(Upstream.OPERA) shouldBe 11
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 4
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 2
                    callCount(OperaEndpoint.GET_HOTEL_CONFIGURATION) shouldBe 2
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe 1
                    callCount(OperaEndpoint.GET_FOLIOS) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("marking as pay-on-arrival adds a read-and-update round on the original") {
                // Pay-on-arrival remaps the saved card, so this world keeps a card on both
                // reservations; card enrichment adds Front Desk credit-card-info reads.
                val booking = confirmAmendBooking(originalId = "6014003", tempId = "6014004", withCard = true)

                installFor(booking)

                val result =
                    ohipApi.confirmAmend(
                        request =
                            ConfirmAmendRequest(
                                hotelId = booking.hotel.hotelId,
                                bookingChannel = UpdateBookingChannel(channel = "CCUI", subchannel = "WEB", language = "en"),
                                tempReservations = listOf("6014004"),
                                originalReservations = listOf("6014003"),
                                linkAmendReservations = mapOf("6014003" to "6014004"),
                                markAsPayOnArrival = true,
                            ),
                        testId = testId,
                    )

                result.attachEvidence("Confirm Amend Pay On Arrival")

                expect("returns the refreshed original as a basket response") {
                    result.response.status.value shouldBe 200
                    result.body.reservationByIdList shouldHaveSize 1
                    result.body.reservationByIdList
                        .first()
                        .reservationId shouldBe "6014003"
                }

                expect("adds the pay-on-arrival read-and-update round and card reads to the base flow") {
                    // Eighteen calls: the eleven of the plain confirm flow, plus Front Desk
                    // credit-card-info enrichment on each basket read of the carded
                    // reservations, plus the pay-on-arrival round's original read and
                    // change-reservation PUT.
                    callCount(Upstream.OPERA) shouldBe 18
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 5
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 3
                    callCount(OperaEndpoint.GET_HOTEL_CONFIGURATION) shouldBe 3
                    callCount(OperaEndpoint.GET_CREDIT_CARD_INFO) shouldBe 3
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 2
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe 1
                    callCount(OperaEndpoint.GET_FOLIOS) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a linked temp reservation Opera holds nothing for fails the confirm without a change PUT") {
                val booking = confirmAmendBooking(originalId = "6014101", tempId = "6014102")
                val missingTempId = "6014199"

                installFor(booking)
                // The linked temp id is foreign to the Booking; Opera answers its read with an
                // empty success body, driving the basket read's not-found branch.
                installStub(getReservationEmpty(booking.hotel.hotelId, missingTempId))

                val result =
                    ohipApi.confirmAmend(
                        request =
                            ConfirmAmendRequest(
                                hotelId = booking.hotel.hotelId,
                                bookingChannel = UpdateBookingChannel(channel = "CCUI", subchannel = "WEB", language = "en"),
                                tempReservations = listOf(missingTempId),
                                originalReservations = listOf("6014101"),
                                linkAmendReservations = mapOf("6014101" to missingTempId),
                            ),
                        testId = testId,
                    )

                result.attachEvidence("Confirm Amend Missing Temp Reservation")

                expect("returns the mapped reservation-not-found error") {
                    result.response.status.value shouldBe 404
                    result.errorBody?.errCode shouldBe 16
                }

                expect("stops after the empty temp read, never PUTting the original") {
                    // Two calls: the original's light read and the empty temp reservation GET.
                    // The change-reservation PUT default is installed, so the exact count
                    // proves no update reached the original.
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 2
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an original without a linked temp reservation is returned refreshed without any change PUT") {
                val booking = confirmAmendBooking(originalId = "6014105", tempId = "6014106")

                installFor(booking)

                val result =
                    ohipApi.confirmAmend(
                        request =
                            ConfirmAmendRequest(
                                hotelId = booking.hotel.hotelId,
                                bookingChannel = UpdateBookingChannel(channel = "CCUI", subchannel = "WEB", language = "en"),
                                tempReservations = emptyList(),
                                originalReservations = listOf("6014105"),
                                linkAmendReservations = emptyMap(),
                            ),
                        testId = testId,
                    )

                result.attachEvidence("Confirm Amend Unlinked Original")

                expect("returns the refreshed original as a basket response") {
                    result.response.status.value shouldBe 200
                    result.body.reservationByIdList shouldHaveSize 1
                    result.body.reservationByIdList
                        .first()
                        .reservationId shouldBe "6014105"
                }

                expect("light-reads and re-reads the original but never PUTs a change") {
                    // Six calls: the original's light read (1) and the final enriched basket
                    // read (reservation, rate-info amounts, folios, CRM profile, hotel
                    // config — 5). The change-reservation PUT default is installed, so the
                    // exact count proves no update was mapped for the unlinked original.
                    callCount(Upstream.OPERA) shouldBe 6
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 2
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe 1
                    callCount(OperaEndpoint.GET_FOLIOS) shouldBe 1
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.GET_HOTEL_CONFIGURATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
        },
    )

private fun confirmAmendBooking(
    originalId: String,
    tempId: String,
    withCard: Boolean = false,
): Booking {
    val arrival = LocalDate.now().plusDays(14)
    val rate = Rate(ratePlan = "SEMIFLEX", roomType = "LOWDBL", adults = 2)

    // The amend keeps the same lead guest, so both reservations share one profile; matching
    // profile ids also mean the adapter skips its profile-updated comparison round.
    fun guest(profileId: String = "PROF-AMELIA") =
        GuestProfile(
            profileId = profileId,
            firstName = "Amelia",
            lastName = "Wright",
            email = "amelia.wright@test.com",
            phone = "+447700900001",
            addressLine = "1 High Street",
            city = "London",
            postcode = "SW1A 1AA",
        )

    fun card(cardId: String) =
        if (withCard) {
            OperaPaymentCard(
                cardId = cardId,
                cardNumber = "4111111111111111",
                expirationDate = arrival.plusYears(2).toString(),
                cardHolderName = "Amelia Wright",
            )
        } else {
            null
        }

    return Booking(
        hotels = listOf(Hotels.HEAPTI.copy(availableRates = listOf(rate))),
        arrival = arrival,
        departure = arrival.plusDays(2),
        rooms =
            listOf(
                BookingRoom(
                    reservationId = originalId,
                    roomType = rate.roomType,
                    adults = rate.adults,
                    status = ReservationStatus.RESERVED,
                    guestProfile = guest(),
                    operaPaymentCard = card("77021"),
                ),
                BookingRoom(
                    reservationId = tempId,
                    roomType = rate.roomType,
                    adults = rate.adults,
                    status = ReservationStatus.RESERVED,
                    guestProfile = guest(),
                    operaPaymentCard = card("77022"),
                ),
            ),
    )
}
