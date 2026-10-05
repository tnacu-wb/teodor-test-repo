package uk.co.whitbread.integrationtests.clients.ohip.model

import kotlinx.serialization.Serializable

/**
 * Request body for `PUT /ohip/v1/reservations/questions-and-answers`
 * (`CompanyQuestionAndAnswerDetailsRequestDto`). The ids bind to a `Set<String>` server-side.
 */
@Serializable
data class QuestionsAndAnswersRequest(
    val hotelId: String,
    val reservationIds: List<String>,
    val companyQuestionAndAnswerDetails: CompanyQuestionAndAnswerDetails,
)

/**
 * The three optional question-and-answer categories one Q&A update carries
 * (`CompanyQuestionAndAnswerDetailsDto`). A user-defined entry may be null, which the adapter
 * counts towards the write guard while its mapper emits no comment for it.
 */
@Serializable
data class CompanyQuestionAndAnswerDetails(
    val purchaseOrderQuestionAndAnswer: CompanyQuestionAndAnswer? = null,
    val customerReferenceQuestionAndAnswer: CompanyQuestionAndAnswer? = null,
    val userDefinedQuestionAndAnswers: List<CompanyQuestionAndAnswer?>? = null,
)

/**
 * One question-and-answer entry (`CompanyQuestionAndAnswerDto`). Opera receives
 * `question|questionHeader` as the comment title and `answer` as the comment text.
 */
@Serializable
data class CompanyQuestionAndAnswer(
    val question: String? = null,
    val questionHeader: String? = null,
    val answer: String? = null,
)
