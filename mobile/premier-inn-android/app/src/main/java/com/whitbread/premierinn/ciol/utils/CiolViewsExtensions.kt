package com.whitbread.premierinn.ciol.utils

import android.content.Context
import androidx.fragment.app.FragmentActivity
import android.text.InputType
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout
import com.whitbread.premierinn.R
import com.whitbread.premierinn.ciol.entity.RegCardGuest
import com.whitbread.premierinn.ciol.fragments.CheckInInformationComposeBottomSheetFragment
import com.whitbread.premierinn.ciol.uimodel.InfoBottomSheetData
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.databinding.RegCardDateOfBirthLayoutBinding
import com.whitbread.premierinn.databinding.RegCardTextInputLayoutItemBinding
import com.whitbread.premierinn.domain.ciol.usecase.DATE_OF_BIRTH_DELIMITER

const val DATE_OF_BIRTH_FORMAT = "%s-%s-%s"
private const val ERROR_MARGIN = 40
private const val NO_MARGIN = 0

fun changeLayoutMargin(layout: TextInputLayout, isError: Boolean) {
    val layoutParams = layout.layoutParams as LinearLayout.LayoutParams
    val marginDp = if (isError) ERROR_MARGIN else NO_MARGIN
    layoutParams.bottomMargin = (layout.resources.displayMetrics.density * marginDp).toInt()
    layout.layoutParams = layoutParams
}

fun RegCardTextInputLayoutItemBinding.showError(errorMessage: String) = this.apply {
    textInput.error = errorMessage
    changeLayoutMargin(textInput, true)
}

fun RegCardTextInputLayoutItemBinding.hideError() = this.apply {
    textInput.error = null
    changeLayoutMargin(textInput, false)
}

fun RegCardTextInputLayoutItemBinding.updateFieldHintWithVisibility(hint: String) = this.apply {
    root.visibility = View.VISIBLE
    textInput.hint = hint
}

fun RegCardTextInputLayoutItemBinding.setNonEmptyText(text: String?) {
    if (!text.isNullOrBlank()) {
        this.editText.setText(text)
    }
}

fun RegCardTextInputLayoutItemBinding.setupAsDateOfBirthField(hint: String) {
    textInput.isHintEnabled = false
    textInput.editText?.hint = hint
    editText.isClickable = true
    editText.isFocusable = false
    editText.inputType = InputType.TYPE_NULL
}

fun RegCardDateOfBirthLayoutBinding.populateDateOfBirthFromGuest(guest: RegCardGuest) {
    val dateOfBirth = guest.getDateOfBirth()
    if (dateOfBirth.isNotEmpty()) {
        val (year, month, day) = dateOfBirth.split(DATE_OF_BIRTH_DELIMITER)
        dayInputLayout.editText.setText(day)
        monthInputLayout.editText.setText(month)
        yearInputLayout.editText.setText(year)
    }
}

fun RegCardDateOfBirthLayoutBinding.showError(context: Context, errorText: String) {
    dayInputLayout.textInput.isErrorEnabled = false
    monthInputLayout.textInput.isErrorEnabled = false
    yearInputLayout.textInput.isErrorEnabled = false

    dateOfBirthHint.setTextColor(context.getColor(R.color.new_error_red))
    dateOfBirthError.isVisible = true
    dateOfBirthError.text = errorText
}

fun RegCardDateOfBirthLayoutBinding.hideError(context: Context) {
    dayInputLayout.textInput.isErrorEnabled = true
    monthInputLayout.textInput.isErrorEnabled = true
    yearInputLayout.textInput.isErrorEnabled = true
    dateOfBirthHint.setTextColor(context.getColor(R.color.grey_dark))
    dateOfBirthError.isVisible = false
}

fun RegCardDateOfBirthLayoutBinding.getDateOfBirthFormatted(): String {
    val year = yearInputLayout.editText.text.toString()
    val month = monthInputLayout.editText.text.toString()
    val day = dayInputLayout.editText.text.toString()

    return if (year.isEmpty() && month.isEmpty() && day.isEmpty()) {
        return EMPTY_STRING
    } else {
        String.format(DATE_OF_BIRTH_FORMAT, year, month, day)
    }
}

fun FragmentActivity.showCheckInInformationBottomSheet(infoBottomSheetData: InfoBottomSheetData, doAction: () -> Unit) {
    CheckInInformationComposeBottomSheetFragment.newInstance(
        infoBottomSheetData = infoBottomSheetData,
        onButtonClick = {
            doAction()
        }
    ).show(
        this.supportFragmentManager,
        CheckInInformationComposeBottomSheetFragment::class.java.simpleName
    )
}

fun Fragment.showErrorAlertDialog(dialogTitle: String, dialogMessage: String) {
    AlertDialog.Builder(this.requireContext(), R.style.PurpleDialog)
        .setTitle(dialogTitle)
        .setMessage(dialogMessage)
        .setPositiveButton(android.R.string.ok) { dialog, _ ->
            dialog.dismiss()
        }
        .create()
        .show()
}
