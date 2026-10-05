package uk.co.whitbread.integrationtests.journeys.hotelreservation

import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.hotelreservation.HotelReservationApi
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.ReservationQuestionAndAnswer
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.ReservationQuestionAndAnswerDetails
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.UpdateReservationQuestionsAndAnswersRequest
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_RESERVATION_GET_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_RESERVATION_PUT_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.getReservationBadRequest
import uk.co.whitbread.integrationtests.stubs.opera.custom.getReservationEmpty
import uk.co.whitbread.integrationtests.stubs.opera.custom.putReservationFailure
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.mocks.OperaEndpoint
import uk.co.whitbread.integrationtests.testkit.mocks.Upstream
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.Rate
import uk.co.whitbread.integrationtests.testkit.model.ReservationComment
import uk.co.whitbread.integrationtests.testkit.model.ReservationStatus
import uk.co.whitbread.integrationtests.testkit.presets.Hotels
import java.time.LocalDate

/**
 * Proves `PUT /v1/reservations/questions-and-answers`: OHIP conditionally adds supplied company
 * and user-defined Q&A after a reservation read, silently skips writes for existing protected
 * Q&A, absent request data, or missing reservation content, and surfaces Opera read and update
 * failures.
 *
 * No endpoint-specific feature flag gates this path. Opera transport-auth flags are
 * environment-pinned OFF and are not request overrides.
 *
 * Flow: backend/integration-tests-kotlin/flows/hotel-reservation-entity-service/UpdateQuestionsAndAnswers.md
 */
class UpdateReservationQuestionsAndAnswersSpec :
    JourneySpec(
        "Hotel Reservation Entity updates reservation questions and answers",
        {
            val hotelReservationApi = HotelReservationApi()

            scenario("supplied company and user-defined questions are added after the reservation read") {
                val booking = questionsAndAnswersBooking(reservationId = "6127201")

                installFor(booking)

                val result =
                    hotelReservationApi.updateReservationQuestionsAndAnswers(
                        request = questionsAndAnswersRequest(booking),
                        testId = testId,
                    )

                result.attachEvidence("Update Reservation Questions And Answers")

                expect("returns an empty 200 response") {
                    result.response.status.value shouldBe 200
                }

                expect("reads and updates the Opera reservation once") {
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an existing protected Q&A comment returns success without updating Opera") {
                val booking =
                    questionsAndAnswersBooking(
                        reservationId = "6127206",
                        comments = listOf(protectedQuestionAndAnswerComment()),
                    )

                installFor(booking)

                val result =
                    hotelReservationApi.updateReservationQuestionsAndAnswers(
                        request = questionsAndAnswersRequest(booking),
                        testId = testId,
                    )

                result.attachEvidence("Update Reservation Questions And Answers Existing Protected Q&A")

                expect("returns an empty 200 response") {
                    result.response.status.value shouldBe 200
                }

                expect("reads the protected reservation but does not update it") {
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an empty Q&A collection returns success without updating Opera") {
                val booking = questionsAndAnswersBooking(reservationId = "6127202")

                installFor(booking)

                val result =
                    hotelReservationApi.updateReservationQuestionsAndAnswers(
                        request = questionsAndAnswersRequest(booking, details = emptyQuestionAndAnswerDetails()),
                        testId = testId,
                    )

                result.attachEvidence("Update Reservation Questions And Answers Empty Request")

                expect("returns an empty 200 response") {
                    result.response.status.value shouldBe 200
                }

                expect("reads the reservation but does not update it") {
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("missing Opera reservation content returns success without an update") {
                val booking = questionsAndAnswersBooking(reservationId = "6127203")
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking, excluded = setOf(OPERA_RESERVATION_GET_STUB_ID))
                installStub(getReservationEmpty(booking.hotel.hotelId, reservationId))

                val result =
                    hotelReservationApi.updateReservationQuestionsAndAnswers(
                        request = questionsAndAnswersRequest(booking),
                        testId = testId,
                    )

                result.attachEvidence("Update Reservation Questions And Answers Missing Reservation")

                expect("returns an empty 200 response") {
                    result.response.status.value shouldBe 200
                }

                expect("attempts one reservation read and no update") {
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an Opera reservation-read rejection stops the Q&A update") {
                val booking = questionsAndAnswersBooking(reservationId = "6127204")
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking, excluded = setOf(OPERA_RESERVATION_GET_STUB_ID))
                installStub(getReservationBadRequest(booking.hotel.hotelId, reservationId))

                val result =
                    hotelReservationApi.updateReservationQuestionsAndAnswers(
                        request = questionsAndAnswersRequest(booking),
                        testId = testId,
                    )

                result.attachEvidence("Update Reservation Questions And Answers Opera Read Error")

                expect("returns the mapped reservation-read error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 960
                }

                expect("attempts one reservation read and no update") {
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an Opera Q&A update rejection is surfaced without retry") {
                val booking = questionsAndAnswersBooking(reservationId = "6127205")

                installFor(booking, excluded = setOf(OPERA_RESERVATION_PUT_STUB_ID))
                installStub(putReservationFailure(booking))

                val result =
                    hotelReservationApi.updateReservationQuestionsAndAnswers(
                        request = questionsAndAnswersRequest(booking),
                        testId = testId,
                    )

                result.attachEvidence("Update Reservation Questions And Answers Opera Update Error")

                expect("returns the mapped change-reservation error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 958
                }

                expect("reads once and attempts one reservation update") {
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
        },
    )

private fun questionsAndAnswersBooking(
    reservationId: String,
    comments: List<ReservationComment> = emptyList(),
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
                    reservationComments = comments,
                ),
            ),
    )
}

private fun protectedQuestionAndAnswerComment(): ReservationComment =
    ReservationComment(
        title = "Purchase order?|Purchase order",
        text = "PO-EXISTING",
        commentId = "QNA-COMMENT-1",
        type = "PUR_ORD_QNA",
    )

private fun questionsAndAnswersRequest(
    booking: Booking,
    details: ReservationQuestionAndAnswerDetails = populatedQuestionAndAnswerDetails(),
): UpdateReservationQuestionsAndAnswersRequest =
    UpdateReservationQuestionsAndAnswersRequest(
        reservationIds = setOf(requireNotNull(booking.room.reservationId)),
        hotelId = booking.hotel.hotelId,
        companyQuestionAndAnswerDetails = details,
    )

private fun populatedQuestionAndAnswerDetails(): ReservationQuestionAndAnswerDetails =
    ReservationQuestionAndAnswerDetails(
        purchaseOrderQuestionAndAnswer =
            ReservationQuestionAndAnswer(
                question = "Purchase order number?",
                questionHeader = "Purchase order",
                answer = "PO-61272",
            ),
        customerReferenceQuestionAndAnswer =
            ReservationQuestionAndAnswer(
                question = "Customer reference?",
                questionHeader = "Customer reference",
                answer = "CUST-61272",
            ),
        userDefinedQuestionAndAnswers =
            listOf(
                ReservationQuestionAndAnswer(
                    question = "Cost centre?",
                    questionHeader = "Cost centre",
                    answer = "ENG",
                ),
            ),
    )

private fun emptyQuestionAndAnswerDetails(): ReservationQuestionAndAnswerDetails =
    ReservationQuestionAndAnswerDetails(userDefinedQuestionAndAnswers = emptyList())
