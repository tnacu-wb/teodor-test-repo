package com.whitbread.premierinn.common.forms

import android.content.Context
import com.whitbread.premierinn.R
import com.whitbread.premierinn.common.Validator.COMPANY_NAME_MAX_LENGTH
import com.whitbread.premierinn.common.Validator.COMPANY_NAME_MIN_LENGTH
import javax.inject.Inject

class FormInputErrorMessageProvider @Inject constructor(
    private val context: Context
){

    fun getFieldRequiresString(): String {
        return context.getString(R.string.form_field_is_required)
    }

    fun getEmailInvalidString(): String {
        return context.getString(R.string.form_field_email_not_valid)
    }

    fun getPhoneNumberInvalidString(): String {
        return context.getString(R.string.form_field_phone_number_not_valid)
    }

    fun getDateInvalidString(): String {
        return context.getString(R.string.form_field_date_is_not_valid)
    }

    fun getEnterValidCardString(): String {
        return context.getString(R.string.form_field_enter_valid_card)
    }

    fun getPasswordRequirementString(): String {
        return context.getString(R.string.form_field_password_must_be_eight_chars_uppercase_lowercase_digit)
    }

    fun getPasswordInvalidString(): String {
        return context.getString(R.string.form_field_password_not_meet_validation_criteria)
    }

    fun getPasswordDoNotMatchString(): String {
        return context.getString(R.string.form_field_passwords_do_not_match)
    }

    fun getFirstNameInvalidString(): String {
        return context.getString(R.string.form_field_first_name_not_valid)
    }

    fun getLastNameInvalidString(): String {
        return context.getString(R.string.form_field_last_name_not_valid)
    }

    fun getPostcodeInvalidString(): String {
        return context.getString(R.string.address_form_postcode_validation_error)
    }

    fun getCompanyNameInvalidString(): String {
        return context.getString(R.string.form_field_company_name_criteria, COMPANY_NAME_MIN_LENGTH, COMPANY_NAME_MAX_LENGTH)
    }

    fun getCompanyNameInvalidSpecialCharacter(): String {
        return context.getString(R.string.form_field_company_name_special_character_criteria)
    }
}