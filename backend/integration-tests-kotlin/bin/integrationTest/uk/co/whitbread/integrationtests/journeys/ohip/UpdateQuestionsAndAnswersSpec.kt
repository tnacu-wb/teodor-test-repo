package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.clients.ohip.model.CompanyQuestionAndAnswer
import uk.co.whitbread.integrationtests.clients.ohip.model.CompanyQuestionAndAnswerDetails
import uk.co.whitbread.integrationtests.clients.ohip.model.QuestionsAndAnswersRequest
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_RESERVATION_PUT_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.putReservationBadRequest
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.featureflags.FeatureFlag
import uk.co.whitbread.integrationtests.testkit.featureflags.OhipFeatureFlag
import uk.co.whitbread.integrationtests.testkit.mocks.OperaEndpoint
import uk.co.whitbread.integrationtests.testkit.mocks.Upstream
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.CharacterUdf
import uk.co.whitbread.integrationtests.testkit.model.Rate
import uk.co.whitbread.integrationtests.testkit.model.ReservationComment
import uk.co.whitbread.integrationtests.testkit.model.ReservationStatus
import uk.co.whitbread.integrationtests.testkit.presets.Hotels
import java.time.LocalDate

// The flow lists only the two Opera transport-auth flags; both are environment-pinned OFF and
// evaluated outside request scope, so every scenario states them at that fixed value.
private val updateQuestionsAndAnswersFlagPins =
    mapOf<FeatureFlag, Boolean>(
        OhipFeatureFlag.USE_TOKEN_SERVICE to false,
        OhipFeatureFlag.USE_TOKEN_REFRESH_SKEW to false,
    )

private const val PURCHASE_ORDER_QUESTION = "Purchase order number?"
private const val PURCHASE_ORDER_HEADER = "Purchase order"
private const val PURCHASE_ORDER_ANSWER = "PO-4471"
private const val CUSTOMER_REFERENCE_QUESTION = "Customer reference?"
private const val CUSTOMER_REFERENCE_HEADER = "Customer reference"
private const val CUSTOMER_REFERENCE_ANSWER = "CR-8890"
private const val USER_DEFINED_QUESTION = "Site contact?"
private const val USER_DEFINED_HEADER = "Site contact"
private const val USER_DEFINED_ANSWER = "Jane Fielding"

/**
 * Proves `PUT /ohip/v1/reservations/questions-and-answers` at the adapter boundary: every
 * requested reservation is read first, then — unless the first reservation Opera returns already
 * carries a protected Q&A comment — each one receives an update carrying the mapped Q&A comments,
 * customer reference, and purchase-order character UDF, with a retryable Opera rejection mapped to
 * the exhausted-retries error.
 *
 * Flow: backend/integration-tests-kotlin/flows/ohip-adapter-service/UpdateQuestionsAndAnswers.md
 */
class UpdateQuestionsAndAnswersSpec :
    JourneySpec(
        "OHIP adapter updates reservation questions and answers",
        {
            val ohipApi = OhipApi()

            scenario("two unprotected reservations each receive the mapped questions and answers") {
                val booking =
                    questionsAndAnswersBooking(
                        reservationIds = listOf("6152101", "6152102"),
                        commentsAfterUpdate =
                            listOf(
                                ReservationComment(
                                    title = "$CUSTOMER_REFERENCE_QUESTION|$CUSTOMER_REFERENCE_HEADER",
                                    text = CUSTOMER_REFERENCE_ANSWER,
                                    type = "CUST_REF_QNA",
                                ),
                                ReservationComment(
                                    title = "$PURCHASE_ORDER_QUESTION|$PURCHASE_ORDER_HEADER",
                                    text = PURCHASE_ORDER_ANSWER,
                                    type = "PUR_ORD_QNA",
                                ),
                                ReservationComment(
                                    title = "$USER_DEFINED_QUESTION|$USER_DEFINED_HEADER",
                                    text = USER_DEFINED_ANSWER,
                                    type = "USR_DEF_QNA",
                                ),
                            ),
                        customReferenceAfterUpdate = CUSTOMER_REFERENCE_ANSWER,
                        characterUdfsAfterUpdate =
                            listOf(CharacterUdf(name = "UDFC11", value = PURCHASE_ORDER_ANSWER)),
                    )

                installFor(booking)

                val result =
                    ohipApi.updateQuestionsAndAnswers(
                        request =
                            QuestionsAndAnswersRequest(
                                hotelId = booking.hotel.hotelId,
                                reservationIds = booking.rooms.map { requireNotNull(it.reservationId) },
                                companyQuestionAndAnswerDetails =
                                    CompanyQuestionAndAnswerDetails(
                                        purchaseOrderQuestionAndAnswer =
                                            CompanyQuestionAndAnswer(
                                                question = PURCHASE_ORDER_QUESTION,
                                                questionHeader = PURCHASE_ORDER_HEADER,
                                                answer = PURCHASE_ORDER_ANSWER,
                                            ),
                                        customerReferenceQuestionAndAnswer =
                                            CompanyQuestionAndAnswer(
                                                question = CUSTOMER_REFERENCE_QUESTION,
                                                questionHeader = CUSTOMER_REFERENCE_HEADER,
                                                answer = CUSTOMER_REFERENCE_ANSWER,
                                            ),
                                        userDefinedQuestionAndAnswers =
                                            listOf(
                                                CompanyQuestionAndAnswer(
                                                    question = USER_DEFINED_QUESTION,
                                                    questionHeader = USER_DEFINED_HEADER,
                                                    answer = USER_DEFINED_ANSWER,
                                                ),
                                            ),
                                    ),
                            ),
                        testId = testId,
                        featureFlagOverrides = updateQuestionsAndAnswersFlagPins,
                    )

                result.attachEvidence("Update Questions And Answers")

                expect("returns an empty 200 response") {
                    result.response.status.value shouldBe 200
                }

                expect("reads both reservations then updates both with the Q&A body") {
                    // Two GET /rsv/v1/hotels/{hotelId}/reservations/{reservationId}, then two
                    // PUT /rsv/v1/hotels/{hotelId}/reservations/{reservationId}, each accepted only
                    // by the Q&A-pinned update mapping for its own reservation.
                    callCount(Upstream.OPERA) shouldBe 4
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 2
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 2
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a protected Q&A comment on the read reservations suppresses every update") {
                val booking =
                    questionsAndAnswersBooking(
                        reservationIds = listOf("6152103", "6152104"),
                        existingComments =
                            listOf(
                                ReservationComment(
                                    title = "$PURCHASE_ORDER_QUESTION|$PURCHASE_ORDER_HEADER",
                                    text = "PO-0001",
                                    commentId = "QNA-EXISTING-1",
                                    type = "PUR_ORD_QNA",
                                ),
                            ),
                    )

                installFor(booking)

                val result =
                    ohipApi.updateQuestionsAndAnswers(
                        request =
                            QuestionsAndAnswersRequest(
                                hotelId = booking.hotel.hotelId,
                                reservationIds = booking.rooms.map { requireNotNull(it.reservationId) },
                                companyQuestionAndAnswerDetails =
                                    CompanyQuestionAndAnswerDetails(
                                        purchaseOrderQuestionAndAnswer =
                                            CompanyQuestionAndAnswer(
                                                question = PURCHASE_ORDER_QUESTION,
                                                questionHeader = PURCHASE_ORDER_HEADER,
                                                answer = PURCHASE_ORDER_ANSWER,
                                            ),
                                    ),
                            ),
                        testId = testId,
                        featureFlagOverrides = updateQuestionsAndAnswersFlagPins,
                    )

                result.attachEvidence("Update Questions And Answers Already Present")

                expect("returns an empty 200 response") {
                    result.response.status.value shouldBe 200
                }

                expect("reads both reservations and updates neither") {
                    // Two GET /rsv/v1/hotels/{hotelId}/reservations/{reservationId}; the permissive
                    // PUT default stays installed, so the zero proves the write phase never starts.
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 2
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a user-defined list holding only a null entry still sends a comment-free update") {
                val booking =
                    questionsAndAnswersBooking(
                        reservationIds = listOf("6152105"),
                        commentsAfterUpdate = emptyList(),
                    )

                installFor(booking)

                val result =
                    ohipApi.updateQuestionsAndAnswers(
                        request =
                            QuestionsAndAnswersRequest(
                                hotelId = booking.hotel.hotelId,
                                reservationIds = booking.rooms.map { requireNotNull(it.reservationId) },
                                companyQuestionAndAnswerDetails =
                                    CompanyQuestionAndAnswerDetails(
                                        userDefinedQuestionAndAnswers = listOf(null),
                                    ),
                            ),
                        testId = testId,
                        featureFlagOverrides = updateQuestionsAndAnswersFlagPins,
                    )

                result.attachEvidence("Update Questions And Answers Null Entry")

                expect("returns an empty 200 response") {
                    result.response.status.value shouldBe 200
                }

                expect("reads the reservation then updates it with an empty comments collection") {
                    // One GET /rsv/v1/hotels/{hotelId}/reservations/{reservationId} then one
                    // PUT /rsv/v1/hotels/{hotelId}/reservations/{reservationId}, accepted only by
                    // the mapping requiring an empty comments collection.
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a retryable Opera rejection of the Q&A update exhausts the retries") {
                val booking = questionsAndAnswersBooking(reservationIds = listOf("6152106"))
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking, excluded = setOf(OPERA_RESERVATION_PUT_STUB_ID))
                installStub(putReservationBadRequest(booking.hotel.hotelId, reservationId))

                val result =
                    ohipApi.updateQuestionsAndAnswers(
                        request =
                            QuestionsAndAnswersRequest(
                                hotelId = booking.hotel.hotelId,
                                reservationIds = listOf(reservationId),
                                companyQuestionAndAnswerDetails =
                                    CompanyQuestionAndAnswerDetails(
                                        customerReferenceQuestionAndAnswer =
                                            CompanyQuestionAndAnswer(
                                                question = CUSTOMER_REFERENCE_QUESTION,
                                                questionHeader = CUSTOMER_REFERENCE_HEADER,
                                                answer = CUSTOMER_REFERENCE_ANSWER,
                                            ),
                                    ),
                            ),
                        testId = testId,
                        featureFlagOverrides = updateQuestionsAndAnswersFlagPins,
                    )

                result.attachEvidence("Update Questions And Answers Opera Retry Exhausted")

                expect("returns the mapped exhausted-retries error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 971
                }

                expect("reads once then attempts the update four times") {
                    // One GET /rsv/v1/hotels/{hotelId}/reservations/{reservationId}, then the
                    // rejected PUT /rsv/v1/hotels/{hotelId}/reservations/{reservationId} once plus
                    // the adapter's three retries of the retryable Bad Request body.
                    callCount(Upstream.OPERA) shouldBe 5
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 4
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
        },
    )

private fun questionsAndAnswersBooking(
    reservationIds: List<String>,
    existingComments: List<ReservationComment> = emptyList(),
    commentsAfterUpdate: List<ReservationComment>? = null,
    customReferenceAfterUpdate: String? = null,
    characterUdfsAfterUpdate: List<CharacterUdf>? = null,
): Booking {
    val arrival = LocalDate.now().plusDays(21)
    val rates =
        listOf(
            Rate(ratePlan = "SEMIFLEX", roomType = "LOWDBL", adults = 2),
            Rate(ratePlan = "SEMIFLEX", roomType = "TWINRM", adults = 1),
        ).take(reservationIds.size)

    return Booking(
        hotels = listOf(Hotels.HEAPTI.copy(availableRates = rates)),
        arrival = arrival,
        departure = arrival.plusDays(2),
        rooms =
            reservationIds.mapIndexed { index, reservationId ->
                BookingRoom(
                    reservationId = reservationId,
                    roomType = rates[index].roomType,
                    adults = rates[index].adults,
                    status = ReservationStatus.RESERVED,
                    reservationComments = existingComments,
                    reservationCommentsAfterUpdate = commentsAfterUpdate,
                    customReferenceAfterUpdate = customReferenceAfterUpdate,
                    characterUdfsAfterUpdate = characterUdfsAfterUpdate,
                )
            },
    )
}
