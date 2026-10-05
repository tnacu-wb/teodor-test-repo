package com.whitbread.premierinn.data.remote

import com.google.gson.annotations.SerializedName

data class OperaFallbackPopupInfo (
    @SerializedName("operaFallbackAlertTitle") val operaFallbackAlertTitle: String,
    @SerializedName("operaFallbackAlertMessage") val operaFallbackAlertMessage: String,
    @SerializedName("operaFallbackAlertClose") val operaFallbackAlertClose: String,
    @SerializedName("operaFallbackAlertContinue") val operaFallbackAlertContinue: String
)