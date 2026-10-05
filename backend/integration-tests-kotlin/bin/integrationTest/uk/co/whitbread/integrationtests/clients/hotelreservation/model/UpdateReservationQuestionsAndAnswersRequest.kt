package uk.co.whitbread.integrationtests.clients.hotelreservation.model

import kotlinx.serialization.Serializable

/** Request body for `PUT /v1/reservations/questions-and-answers`. */
@Serializable
data class UpdateReservationQuestionsAndAnswersRequest(
    val reservationIds: Set<String>,
    val hotelId: String,
    val companyQuestionAndAnswerDetails: ReservationQuestionAndAnswerDetails,
)

/** Optional purchase-order, customer-reference, and user-defined Q&A values. */
@Serializable
data class ReservationQuestionAndAnswerDetails(
    val purchaseOrderQuestionAndAnswer: ReservationQuestionAndAnswer? = null,
    val customerReferenceQuestionAndAnswer: ReservationQuestionAndAnswer? = null,
    val userDefinedQuestionAndAnswers: List<ReservationQuestionAndAnswer?>? = null,
)

/** One question, its display header, and the answer stored in Opera. */
@Serializable
data class ReservationQuestionAndAnswer(
    val question: String,
    val questionHeader: String,
    val answer: String,
)
