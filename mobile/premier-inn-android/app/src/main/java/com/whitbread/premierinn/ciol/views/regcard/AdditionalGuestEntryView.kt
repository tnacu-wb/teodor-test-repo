package com.whitbread.premierinn.ciol.views.regcard

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.whitbread.premierinn.R
import com.whitbread.premierinn.calendar.convertDateFormat
import com.whitbread.premierinn.ciol.CheckInOnlineActivity
import com.whitbread.premierinn.ciol.entity.AdditionalGuestUiModel
import com.whitbread.premierinn.ciol.fragments.EditGuestDetailsFragment
import com.whitbread.premierinn.common.format.DateFormat
import com.whitbread.premierinn.databinding.RegCardGuestDetailsContainerBinding

class AdditionalGuestEntryView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    private val binding = RegCardGuestDetailsContainerBinding.inflate(LayoutInflater.from(context), this, true)
    fun bind(
        activity: Activity,
        additionalGuest: AdditionalGuestUiModel,
        showGuestError: Boolean,
        hasEmptyFields: Boolean,
        nationality: String,
        isPassportRequiredForCountry: Boolean,
        action: (() -> Unit)
    ) {
        binding.apply {
            guestError.isVisible = showGuestError
            regCardGuestLabel.text = resources.getString(R.string.reg_card_additional_guest_label)
            if (showGuestError) {
                guestError.setText(resources.getString(R.string.reg_card_missing_guest_info_warning_message))
            }

            regCardGuestNameLayout.apply {
                regCardElementLabel.text = resources.getString(R.string.reg_card_guest_name_label)
                regCardElementValue.maxLines = Integer.MAX_VALUE
                regCardElementValue.text =
                    listOf(additionalGuest.firstName, additionalGuest.lastName).joinToString(" ")
            }
            regCardGuestDateOfBirthLayout.apply {
                regCardElementLabel.text = resources.getString(R.string.reg_card_date_of_birth_label)
                regCardElementValue.text = convertDateFormat(
                    dateString = additionalGuest.dateOfBirth,
                    currentFormat = DateFormat.DASHED_YEAR_MONTH_DAY,
                    newFormat = DateFormat.SLASHED_DAY_MONTH_YEAR
                )
            }
            regCardGuestNationalityLayout.apply {
                regCardElementLabel.text = resources.getString(R.string.reg_card_nationality_hint)
                regCardElementValue.text = nationality
            }
            regCardGuestPassportNumberLayout.apply {
                regCardElementContainer.isVisible = isPassportRequiredForCountry == true
                if (regCardElementContainer.isVisible) {
                    regCardElementLabel.text = resources.getString(R.string.guest_details_form_passport_number)
                    regCardElementValue.maxLines = Integer.MAX_VALUE
                    regCardElementValue.text = additionalGuest.passportNumber
                }
            }

            binding.addOrEditDetailsButton.setText(if (hasEmptyFields) R.string.pre_stay_add else R.string.reg_card_edit_button_label)
            binding.addOrEditDetailsButton.setTextColor(
                ContextCompat.getColor(activity, if (showGuestError) R.color.new_error_red else R.color.teal_dark)
            )
            binding.addOrEditDetailsButton.setOnClickListener {
                action.invoke()
                (activity as CheckInOnlineActivity).supportFragmentManager.beginTransaction()
                    .replace(
                        R.id.container,
                        EditGuestDetailsFragment.newInstance(
                            isLeadGuest = false,
                            additionalGuest
                        )
                    )
                    .addToBackStack(null)
                    .commit()
            }
        }
    }
}
