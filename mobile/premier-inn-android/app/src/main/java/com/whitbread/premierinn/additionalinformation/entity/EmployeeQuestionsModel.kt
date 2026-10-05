package com.whitbread.premierinn.additionalinformation.entity

import android.os.Parcelable
import com.whitbread.premierinn.businessbooker.domain.company.QuestionType
import com.whitbread.premierinn.data.common.EMPTY_STRING
import kotlinx.parcelize.Parcelize

@Parcelize
data class EmployeeQuestionsModel(
    val id: String = EMPTY_STRING,
    val questionHeader: String,
    val isMandatory: Boolean,
    val inputText: String? = null,
    val selectedOption: String? = null,
    val answerType: AnswerType,
    val listOfPreSetAnswers: List<String> = emptyList(),
    val questionType: QuestionType
): Parcelable

enum class AnswerType {
    TEXT_FIELD,
    DROPDOWN
}