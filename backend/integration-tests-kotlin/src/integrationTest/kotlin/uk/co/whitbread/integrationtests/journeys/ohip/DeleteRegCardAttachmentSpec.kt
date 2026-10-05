package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_RESERVATION_ATTACHMENT_DELETE_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_RESERVATION_GET_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_RESERVATION_PUT_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.deleteAnyReservationAttachment
import uk.co.whitbread.integrationtests.stubs.opera.custom.deleteReservationAttachmentFailure
import uk.co.whitbread.integrationtests.stubs.opera.custom.getReservationEmpty
import uk.co.whitbread.integrationtests.stubs.opera.custom.putReservationFailure
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.featureflags.FeatureFlag
import uk.co.whitbread.integrationtests.testkit.featureflags.OhipFeatureFlag
import uk.co.whitbread.integrationtests.testkit.mocks.OperaEndpoint
import uk.co.whitbread.integrationtests.testkit.mocks.Upstream
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.PreRegistration
import uk.co.whitbread.integrationtests.testkit.model.Rate
import uk.co.whitbread.integrationtests.testkit.model.ReservationStatus
import uk.co.whitbread.integrationtests.testkit.presets.Hotels
import java.time.LocalDate

// The opera token-service flags are environment-pinned OFF; no other flag gates this chain.
private val deleteRegCardFlagPins =
    mapOf<FeatureFlag, Boolean>(
        OhipFeatureFlag.USE_TOKEN_SERVICE to false,
    )

/**
 * Proves `DELETE /ohip/v1/reservations/attachments`: for a pre-registered reservation the
 * adapter deletes the REG_RES registration-card attachment and removes the Pre-Check-In
 * alert; a reservation that never pre-registered, or holds no reg card, degrades to fewer
 * Opera calls while still returning 204.
 *
 * Flow: backend/integration-tests-kotlin/flows/ohip-adapter-service/DeleteRegCardAttachment.md
 */
class DeleteRegCardAttachmentSpec :
    JourneySpec(
        "OHIP adapter deletes a reservation's registration-card attachment",
        {
            val ohipApi = OhipApi()

            scenario("a pre-registered reservation loses its reg card and pre-check-in alert") {
                val booking =
                    regCardBooking(
                        reservationId = "6007601",
                        preRegistration =
                            PreRegistration(
                                regCardAttachmentId = "ATT-6007601",
                                preCheckInAlertId = "ALERT-6007601",
                            ),
                    )

                installFor(booking)

                val result =
                    ohipApi.deleteRegCardAttachment(
                        hotelId = booking.hotel.hotelId,
                        reservationId = requireNotNull(booking.room.reservationId),
                        testId = testId,
                        featureFlagOverrides = deleteRegCardFlagPins,
                    )

                result.attachEvidence("Delete Reg Card Attachment")

                expect("returns 204 with no body") {
                    result.response.status.value shouldBe 204
                }

                expect("fetches the reservation, deletes the attachment, and removes the alert") {
                    // Three Opera calls: reservation GET, attachment DELETE, alert-removal PUT.
                    callCount(Upstream.OPERA) shouldBe 3
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.DELETE_FILE_ATTACHMENT) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a reservation that never pre-registered is a silent no-op") {
                val booking = regCardBooking(reservationId = "6007602", preRegistration = null)

                installFor(booking)
                // Absence proof: a permissive attachment-DELETE mock is installed so a
                // mistaken delete would be served and counted rather than failing to match.
                installStub(deleteAnyReservationAttachment(booking))

                val result =
                    ohipApi.deleteRegCardAttachment(
                        hotelId = booking.hotel.hotelId,
                        reservationId = requireNotNull(booking.room.reservationId),
                        testId = testId,
                        featureFlagOverrides = deleteRegCardFlagPins,
                    )

                result.attachEvidence("Delete Reg Card Attachment No-Op")

                expect("still returns 204 with no body") {
                    result.response.status.value shouldBe 204
                }

                expect("only fetches the reservation; no attachment DELETE and no alert PUT") {
                    // One Opera call: the reservation GET. The installed permissive DELETE
                    // mock and the default put-reservation stub both go unhit.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                }
            }

            scenario("a pre-registered reservation without a reg card still loses its alert") {
                val booking =
                    regCardBooking(
                        reservationId = "6007603",
                        preRegistration = PreRegistration(preCheckInAlertId = "ALERT-6007603"),
                    )

                installFor(booking)
                // Absence proof for the attachment DELETE, as above.
                installStub(deleteAnyReservationAttachment(booking))

                val result =
                    ohipApi.deleteRegCardAttachment(
                        hotelId = booking.hotel.hotelId,
                        reservationId = requireNotNull(booking.room.reservationId),
                        testId = testId,
                        featureFlagOverrides = deleteRegCardFlagPins,
                    )

                result.attachEvidence("Delete Reg Card Attachment Alert Only")

                expect("returns 204 with no body") {
                    result.response.status.value shouldBe 204
                }

                expect("fetches the reservation and removes the alert without an attachment DELETE") {
                    // Two Opera calls: reservation GET and alert-removal PUT.
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 1
                }
            }

            scenario("a reservation Opera holds nothing for maps to attachment-not-found error 45") {
                val booking =
                    regCardBooking(
                        reservationId = "6007611",
                        preRegistration =
                            PreRegistration(
                                regCardAttachmentId = "ATT-6007611",
                                preCheckInAlertId = "ALERT-6007611",
                            ),
                    )
                val reservationId = requireNotNull(booking.room.reservationId)

                // Opera answering the GET with an empty success body is exceptional
                // collaborator data, not a normal Booking world state. The default
                // attachment-DELETE and put-reservation stubs stay installed as absence
                // mocks: a mistaken call would be served and counted.
                installFor(booking, excluded = setOf(OPERA_RESERVATION_GET_STUB_ID))
                installStub(getReservationEmpty(booking.hotel.hotelId, reservationId))

                val result =
                    ohipApi.deleteRegCardAttachment(
                        hotelId = booking.hotel.hotelId,
                        reservationId = reservationId,
                        testId = testId,
                        featureFlagOverrides = deleteRegCardFlagPins,
                    )

                result.attachEvidence("Delete Reg Card Attachment Not Found")

                expect("returns the mapped attachment-not-found error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 45
                }

                expect("stops after the empty reservation GET") {
                    // One Opera call: the GET. No attachment DELETE, no alert PUT.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                }
            }

            scenario("an Opera rejection of the attachment delete maps to internal error 938") {
                val booking =
                    regCardBooking(
                        reservationId = "6007612",
                        preRegistration =
                            PreRegistration(
                                regCardAttachmentId = "ATT-6007612",
                                preCheckInAlertId = "ALERT-6007612",
                            ),
                    )

                installFor(booking, excluded = setOf(OPERA_RESERVATION_ATTACHMENT_DELETE_STUB_ID))
                installStub(deleteReservationAttachmentFailure(booking))

                val result =
                    ohipApi.deleteRegCardAttachment(
                        hotelId = booking.hotel.hotelId,
                        reservationId = requireNotNull(booking.room.reservationId),
                        testId = testId,
                        featureFlagOverrides = deleteRegCardFlagPins,
                    )

                result.attachEvidence("Delete Reg Card Attachment Opera Delete Error")

                expect("returns the mapped attachment-delete error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 938
                }

                expect("aborts before the alert-removal PUT") {
                    // Two Opera calls: the GET and the rejected attachment DELETE. The
                    // default put-reservation stub stays installed as the absence mock and
                    // goes unhit.
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.DELETE_FILE_ATTACHMENT) shouldBe 1
                }
            }

            scenario("an Opera rejection of the alert-removal put maps to internal error 952") {
                val booking =
                    regCardBooking(
                        reservationId = "6007613",
                        preRegistration =
                            PreRegistration(
                                regCardAttachmentId = "ATT-6007613",
                                preCheckInAlertId = "ALERT-6007613",
                            ),
                    )

                // The failure envelope's type is `Internal Server Error`, which the
                // adapter's shared retry spec does not retry, so the mapped exception
                // surfaces immediately instead of after the backoff window.
                installFor(booking, excluded = setOf(OPERA_RESERVATION_PUT_STUB_ID))
                installStub(putReservationFailure(booking))

                val result =
                    ohipApi.deleteRegCardAttachment(
                        hotelId = booking.hotel.hotelId,
                        reservationId = requireNotNull(booking.room.reservationId),
                        testId = testId,
                        featureFlagOverrides = deleteRegCardFlagPins,
                    )

                result.attachEvidence("Delete Reg Card Attachment Alert Put Error")

                expect("returns the mapped reservation-guest-put error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 952
                }

                expect("fails on the final alert-removal PUT without retrying") {
                    // Three Opera calls: the GET, the attachment DELETE, and the single
                    // rejected alert-removal PUT.
                    callCount(Upstream.OPERA) shouldBe 3
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.DELETE_FILE_ATTACHMENT) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 1
                }
            }
        },
    )

private fun regCardBooking(
    reservationId: String,
    preRegistration: PreRegistration?,
): Booking {
    val arrival = LocalDate.now().plusDays(3)
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
                    preRegistration = preRegistration,
                ),
            ),
    )
}
