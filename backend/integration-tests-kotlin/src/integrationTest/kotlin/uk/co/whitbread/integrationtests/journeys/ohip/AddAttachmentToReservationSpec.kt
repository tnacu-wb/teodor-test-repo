package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.clients.ohip.model.ReservationFileAttachmentRequest
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_FILE_ATTACHMENT_ADD_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.addFileAttachmentFailure
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.featureflags.FeatureFlag
import uk.co.whitbread.integrationtests.testkit.featureflags.OhipFeatureFlag
import uk.co.whitbread.integrationtests.testkit.mocks.OperaEndpoint
import uk.co.whitbread.integrationtests.testkit.mocks.Upstream
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.Rate
import uk.co.whitbread.integrationtests.testkit.model.ReservationStatus
import uk.co.whitbread.integrationtests.testkit.presets.Hotels
import uk.co.whitbread.integrationtests.testkit.presets.Pdfs
import java.time.LocalDate

// The opera token-service flags are environment-pinned OFF; no other flag gates this chain.
private val addAttachmentFlagPins =
    mapOf<FeatureFlag, Boolean>(
        OhipFeatureFlag.USE_TOKEN_SERVICE to false,
    )

/**
 * Proves `POST /ohip/v1/reservations/attachments`: a valid base64 PDF registration card
 * passes the adapter's local validation (base64 decode, 10MB cap, PDFBox parse) and is
 * uploaded to the Opera media-config file store linked to the reservation, returning 200
 * with a Success status.
 *
 * Flow: backend/integration-tests-kotlin/flows/ohip-adapter-service/AddAttachmentToReservation.md
 */
class AddAttachmentToReservationSpec :
    JourneySpec(
        "OHIP adapter uploads a reservation registration card",
        {
            val ohipApi = OhipApi()

            scenario("a valid PDF registration card is stored against the reservation") {
                val booking = attachmentBooking(reservationId = "6007401")
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking)

                val result =
                    ohipApi.addAttachmentToReservation(
                        request =
                            ReservationFileAttachmentRequest(
                                fileName = "REG_RES${reservationId}_ID232323_P76767676.pdf",
                                reservationId = reservationId,
                                hotelId = booking.hotel.hotelId,
                                fileAttachment = Pdfs.MINIMAL_PDF_BASE64,
                                description = "Registration card",
                            ),
                        testId = testId,
                        featureFlagOverrides = addAttachmentFlagPins,
                    )

                result.attachEvidence("Add Attachment")

                expect("returns 200 with a Success status and confirmation message") {
                    result.response.status.value shouldBe 200
                    result.body.status shouldBe "Success"
                    result.body.message shouldBe "Attachment added successfully"
                }

                expect("uploads exactly one file to the Opera media store") {
                    // One Opera call: POST /med/config/v1/fileAttachments.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.CREATE_FILE_ATTACHMENT) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an Opera media-store rejection is mapped to the change-reservation error") {
                val booking = attachmentBooking(reservationId = "6007402")
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking, excluded = setOf(OPERA_FILE_ATTACHMENT_ADD_STUB_ID))
                installStub(addFileAttachmentFailure(booking))

                val result =
                    ohipApi.addAttachmentToReservation(
                        request =
                            ReservationFileAttachmentRequest(
                                fileName = "REG_RES${reservationId}_ID232323_P76767676.pdf",
                                reservationId = reservationId,
                                hotelId = booking.hotel.hotelId,
                                fileAttachment = Pdfs.MINIMAL_PDF_BASE64,
                                description = "Registration card",
                            ),
                        testId = testId,
                        featureFlagOverrides = addAttachmentFlagPins,
                    )

                result.attachEvidence("Add Attachment Opera Error")

                expect("returns the mapped change-reservation error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 958
                }

                expect("attempts exactly one upload to the Opera media store") {
                    // One Opera call: the rejected POST /med/config/v1/fileAttachments.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.CREATE_FILE_ATTACHMENT) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
        },
    )

private fun attachmentBooking(reservationId: String): Booking {
    val arrival = LocalDate.now().plusDays(5)
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
                ),
            ),
    )
}
