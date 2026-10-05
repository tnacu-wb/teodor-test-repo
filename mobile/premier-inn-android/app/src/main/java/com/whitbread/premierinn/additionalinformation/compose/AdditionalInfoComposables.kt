package com.whitbread.premierinn.additionalinformation.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.whitbread.premierinn.R
import com.whitbread.premierinn.additionalinformation.entity.AnswerType
import com.whitbread.premierinn.additionalinformation.entity.EmployeeQuestionsModel
import com.whitbread.premierinn.businessbooker.domain.company.QuestionType
import com.whitbread.premierinn.common.Validator.isBusinessAccountQuestionValid
import com.whitbread.premierinn.common.Validator.isCustomQuestionValid
import com.whitbread.premierinn.common.mapper.toSelectedAdditionalInformation
import com.whitbread.premierinn.compose.ui.components.AppBarTextWithProximaNovaSemiBold
import com.whitbread.premierinn.compose.ui.components.GenericButton
import com.whitbread.premierinn.compose.ui.components.LightGrey2
import com.whitbread.premierinn.compose.ui.components.LightGrey5
import com.whitbread.premierinn.compose.ui.components.Purple
import com.whitbread.premierinn.compose.ui.components.proximaNovaFontFamily
import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import com.whitbread.premierinn.reviewbooking.AdditionalInformation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdditionalInformationScreen(
    listOfQuestionListItem: List<EmployeeQuestionsModel>,
    onClickToRnB: (List<AdditionalInformation>) -> Unit,
    onBackClicked: () -> Unit
) {
    var listOfAllQuestions by remember { mutableStateOf(listOfQuestionListItem) }
    val validationStates = remember { mutableStateMapOf<String, Boolean>() }
    val errorStates = remember { mutableStateMapOf<String, Boolean>() }

    val allValid by remember {
        derivedStateOf {
            validationStates.values.all { it }
        }
    }
    val focusManager = LocalFocusManager.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            }
            .imePadding()
    ) {
        Scaffold(
            containerColor = LightGrey5,
            topBar = {
                TopAppBar(
                    title = { AppBarTextWithProximaNovaSemiBold(text = stringResource(R.string.additional_information_title)) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Purple, titleContentColor = Color.White
                    ),
                    navigationIcon = {
                        IconButton(onClick = onBackClicked) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back Arrow",
                                tint = Color.White
                            )
                        }
                    },
                    modifier = Modifier.heightIn(max = 56.dp)
                )
            }, content = { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(innerPadding)
                ) {

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = stringResource(R.string.additional_information_message),
                        modifier = Modifier.padding(17.dp),
                        style = TextStyle(
                            fontSize = 18.sp,
                            color = Purple,
                            fontFamily = proximaNovaFontFamily,
                            fontWeight = FontWeight.SemiBold
                        )
                    )

                    LazyColumn(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(
                            items = listOfAllQuestions
                        ) { questionItem ->
                            when (questionItem.answerType) {
                                AnswerType.TEXT_FIELD -> {
                                    EmployeeQuestionEditText(
                                        item = questionItem,
                                        onTextChanged = { questionName: String, newText: String ->
                                            listOfAllQuestions = listOfAllQuestions.map {
                                                if (it.id == questionItem.id) it.copy(inputText = newText) else it
                                            }
                                            val isValid =
                                                newText.isFieldValid(questionItem.questionType)
                                            validationStates[questionItem.id] = isValid
                                            errorStates[questionItem.id] = !isValid
                                        },
                                        shouldShowError = errorStates[questionItem.id] ?: false
                                    )
                                }

                                AnswerType.DROPDOWN -> {
                                    EmployeeQuestionDropDown(
                                        item = questionItem,
                                        onOptionSelected = { name: String, selectedOption: String ->
                                            listOfAllQuestions = listOfAllQuestions.map {
                                                if (it.id == questionItem.id) it.copy(selectedOption = selectedOption) else it
                                            }
                                            val isValid =
                                                selectedOption.isFieldValid(questionItem.questionType)
                                            validationStates[questionItem.id] = isValid
                                            errorStates[questionItem.id] = !isValid
                                        },
                                        shouldShowError = errorStates[questionItem.id] ?: false
                                    )
                                }
                            }
                        }

                        item {
                            val allMandatoryFieldsFilled =
                                allValid && listOfAllQuestions.none { question ->
                                    (question.answerType == AnswerType.TEXT_FIELD && question.isMandatory && question.inputText.isNullOrBlank()) ||
                                            (question.answerType == AnswerType.DROPDOWN && question.isMandatory && question.selectedOption.isNullOrBlank())
                                }

                            GenericButton(
                                color = Purple,
                                enabled = allMandatoryFieldsFilled,
                                onClick = {
                                    onClickToRnB(
                                        listOfAllQuestions.toList()
                                            .toSelectedAdditionalInformation()
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                text = stringResource(R.string.continue_to_payment)
                            )
                        }
                    }
                }
            })
    }
}

@Composable
fun EmployeeQuestionEditText(
    item: EmployeeQuestionsModel,
    onTextChanged: (questionName: String, enteredText: String) -> Unit,
    shouldShowError: Boolean
) {
    val currentText = item.inputText ?: EMPTY_STRING_DOMAIN

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val isOptional =
            if (!item.isMandatory) stringResource(R.string.additional_information_optional_text) else EMPTY_STRING_DOMAIN
        Column(
            modifier = Modifier
                .width(100.dp)
                .padding(2.dp)
        ) {
            Text(text = item.questionHeader)
            Spacer(modifier = Modifier.height(2.dp))
            if (isOptional.isNotEmpty()) {
                Text(text = isOptional)
            }
        }
        Spacer(modifier = Modifier.width(8.dp))

        OutlinedTextField(
            value = currentText,
            onValueChange = { inputTextEntered: String ->
                onTextChanged(
                    item.questionHeader, inputTextEntered
                )
            },
            label = {
                Text(
                    text = stringResource(R.string.additional_information_textfield_placeholder),
                    color = if (shouldShowError) Color.Red else Color.Black,
                    fontSize = 15.sp,
                    fontFamily = proximaNovaFontFamily,
                    fontWeight = FontWeight.ExtraLight
                )
            },
            isError = shouldShowError,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = if (shouldShowError) Color.Red else Color.Black,
                errorBorderColor = if (shouldShowError) Color.Red else Color.Black
            ),
            supportingText = {
                if (shouldShowError) {
                    Text(
                        text = stringResource(getErrorText(item.questionType)),
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeQuestionDropDown(
    item: EmployeeQuestionsModel,
    onOptionSelected: (name: String, selectedOption: String) -> Unit,
    shouldShowError: Boolean
) {
    var expanded by remember { mutableStateOf(false) }
    val currentSelectedOption = item.selectedOption ?: EMPTY_STRING_DOMAIN
    val currentOptions = item.listOfPreSetAnswers

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val isOptional =
            if (!item.isMandatory) stringResource(R.string.additional_information_optional_text) else EMPTY_STRING_DOMAIN
        Column(
            modifier = Modifier
                .width(100.dp)
                .padding(2.dp)
        ) {
            Text(text = item.questionHeader)
            Spacer(modifier = Modifier.height(2.dp))
            if (isOptional.isNotEmpty()) {
                Text(text = isOptional)
            }
        }

        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }) {
                OutlinedTextField(
                    value = currentSelectedOption,
                    onValueChange = {},
                    readOnly = true,
                    label = {
                        Text(
                            text = stringResource(R.string.additional_information_dropdown_placeholder),
                            color = if (shouldShowError) Color.Red else Color.Black,
                            fontSize = 15.sp,
                            fontFamily = proximaNovaFontFamily,
                            fontWeight = FontWeight.ExtraLight
                        )
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    isError = shouldShowError,
                    supportingText = {
                        if (shouldShowError) {
                            Text(
                                text = stringResource(R.string.dropdown_question_error),
                                color = Color.Red,
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        errorBorderColor = if (shouldShowError) Color.Red else Color.Black
                    )
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(color = LightGrey2)
                ) {
                    currentOptions.forEach { option ->
                        DropdownMenuItem(text = {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.Transparent)
                                    .padding(2.dp)
                            ) {
                                Text(
                                    option,
                                    color = Color.Black,
                                    fontSize = 18.sp,
                                    fontFamily = proximaNovaFontFamily,
                                    fontWeight = FontWeight.Normal
                                )
                            }
                        }, onClick = {
                            onOptionSelected(item.questionHeader, option)
                            expanded = false
                        })
                    }
                }
            }
        }
    }
}

private fun String.isFieldValid(questionType: QuestionType): Boolean {
    return when (questionType) {
        QuestionType.CUSTOMER_REF -> isBusinessAccountQuestionValid(this)
        QuestionType.PURCHASE_ORDER -> isBusinessAccountQuestionValid(this)
        QuestionType.USER_DEFINED -> isCustomQuestionValid(this)
    }
}

private fun getErrorText(questionType: QuestionType): Int {
    return when (questionType) {
        QuestionType.CUSTOMER_REF -> R.string.customer_reference_error
        QuestionType.PURCHASE_ORDER -> R.string.purchase_order_number_error
        QuestionType.USER_DEFINED -> R.string.user_defined_question_error
    }
}

@Preview(showBackground = true)
@Composable
fun AdditionalInformationScreenPreview() {
    val qu1 = EmployeeQuestionsModel(
        answerType = AnswerType.DROPDOWN,
        questionHeader = "Quest1",
        selectedOption = null,
        isMandatory = true,
        listOfPreSetAnswers = listOf("opt1", "opt2"),
        questionType = QuestionType.USER_DEFINED,
        id = ""
    )
    val qu2 = EmployeeQuestionsModel(
        answerType = AnswerType.DROPDOWN,
        questionHeader = "Quest2",
        selectedOption = null,
        isMandatory = false,
        listOfPreSetAnswers = listOf("opt3", "opt4"),
        questionType = QuestionType.USER_DEFINED,
        id = ""
    )
    val qu3 = EmployeeQuestionsModel(
        answerType = AnswerType.TEXT_FIELD,
        questionHeader = "Quest3",
        selectedOption = null,
        isMandatory = true,
        listOfPreSetAnswers = emptyList(),
        questionType = QuestionType.USER_DEFINED,
        id = ""
    )


    AdditionalInformationScreen(
        listOfQuestionListItem = listOf(qu1, qu2, qu3),
        onClickToRnB = { listOf(qu1, qu2, qu3).toSelectedAdditionalInformation() },
        onBackClicked = {}
    )
}

@Preview(showBackground = true)
@Composable
fun EmployeeQuestionEditTextPreview() {
    EmployeeQuestionEditText(
        item = EmployeeQuestionsModel(
            answerType = AnswerType.TEXT_FIELD,
            questionHeader = "Quest1",
            isMandatory = true,
            listOfPreSetAnswers = emptyList(),
            inputText = null,
            questionType = QuestionType.USER_DEFINED,
            id = ""
        ),
        onTextChanged = { _, _ -> run {} },
        shouldShowError = false
    )
}

@Preview(showBackground = true)
@Composable
fun EmployeeQuestionDropDownPreview() {
    EmployeeQuestionDropDown(
        item = EmployeeQuestionsModel(
            answerType = AnswerType.DROPDOWN,
            questionHeader = "Quest2",
            selectedOption = null,
            listOfPreSetAnswers = listOf("opt1", "opt2"),
            isMandatory = false,
            questionType = QuestionType.USER_DEFINED,
            id = ""
        ),
        onOptionSelected = { _, _ -> run {} },
        shouldShowError = false
    )
}
