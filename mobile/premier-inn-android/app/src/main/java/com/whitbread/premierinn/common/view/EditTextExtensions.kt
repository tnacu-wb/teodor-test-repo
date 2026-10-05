package com.whitbread.premierinn.common.view

import android.text.Editable
import android.text.TextWatcher
import com.google.android.material.textfield.TextInputLayout
import java.util.regex.Pattern

/**
 * Set the validation on a TextInputLayout instance as a {@see TextWatcher}.
 *
 * @param pattern: If the input pattern does not match the pattern, then the errorMessage is displayed
 * as part of the TextInputLayout {@see TextInputLayout#setError}
 *
 * @param errorMessage: the message to be displayed when the pattern doesn't match the input.
 *
 * @see Validator for Regex samples
 */
fun TextInputLayout.setValidation(pattern: Pattern, errorMessage: String) {
    editText?.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if (s.isNotBlank() && !pattern.matcher(s).find()) {
                isErrorEnabled = true
                error = errorMessage
            } else {
                isErrorEnabled = false
                error = null
            }
        }
        override fun afterTextChanged(s: Editable) {}
    })
}
