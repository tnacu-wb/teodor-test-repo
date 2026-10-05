package com.whitbread.premierinn.domain.common

import com.google.gson.annotations.SerializedName

data class Passport(
    @SerializedName("a") val number: String = EMPTY_STRING_DOMAIN,
    @SerializedName("b") val placeOfIssue: String? = null
) {

    fun passportNumber(): String { return number}
}