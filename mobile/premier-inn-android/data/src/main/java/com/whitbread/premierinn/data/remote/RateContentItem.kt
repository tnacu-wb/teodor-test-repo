package com.whitbread.premierinn.data.remote

import com.google.gson.annotations.SerializedName

data class RateContentItem(
        @SerializedName("classification") val classification : String,
        @SerializedName("name") val name : String?,
        @SerializedName("description") val description : String?,
        @SerializedName("bookingTermsMessage") val bookingTermsMessage : String?)