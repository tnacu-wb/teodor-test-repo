package com.whitbread.premierinn.domain.common

import com.google.gson.annotations.SerializedName

data class Address (
    @SerializedName("a") val line1: String,
    @SerializedName("b") val line2: String? = null,
    @SerializedName("c") val line3: String? = null,
    @SerializedName("d") val line4: String? = null,
    @SerializedName("e") val line5: String? = null,
    @SerializedName("f") val postCode: String? = null,
    @SerializedName("g") val companyName: String? = null,
    @SerializedName("h") val countryCode: String? = null) {

    fun isWorkAddress(): Boolean = !this.companyName.isNullOrBlank()

    companion object {
        val EMPTY = Address(
            line1 = EMPTY_STRING_DOMAIN,
            line2 = null,
            line3 = null,
            line4 = null,
            line5 = null,
            postCode = null,
            companyName = null,
            countryCode = null
        )
    }
}