package com.whitbread.premierinn.common.view.form


sealed class FormField

data class Text(val value: String? = null) : FormField()
data class DropDown<T>(val options: List<T>, var selectedOption: Int? = null, val emptyOptionsText: String = "") : FormField()
data class CheckBox(var checked: Boolean) : FormField()

sealed class Validity
object Valid : Validity()
data class Invalid(val error: String) : Validity()


fun <T> DropDown<T>.selectedValue(): T? {
    return selectedOption?.let {
        options[it]
    }
}

fun FormField.update(value: Any?): FormField {
    return when (this) {
        is Text -> Text(value as String)
        is DropDown<*> -> {
            selectedOption = options.indexOf(value).takeIf { it >= 0 }
            this
        }
        is CheckBox -> {
            checked = value as Boolean
            this
        }
    }
}

fun FormField.value(): Any? {
    return when (this) {
        is Text -> value
        is DropDown<*> -> this.selectedValue()
        is CheckBox -> checked
    }
}