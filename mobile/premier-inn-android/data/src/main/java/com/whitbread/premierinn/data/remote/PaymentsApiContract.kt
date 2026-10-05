package com.whitbread.premierinn.data.remote

import com.google.gson.annotations.SerializedName

interface PaymentsApiContract {

    data class ThreeCPaymentServiceResponse(
            @SerializedName("paymentId") val paymentID: String,
            @SerializedName("providerResponse") val providerResponse: ProviderResponse,
            @SerializedName("revisedSolution") val revisedSolution: Boolean
    )

    data class ProviderResponse(
            @SerializedName("threecResponse") val threeCResponse: ThreeCResponse
    )

    data class ThreeCResponse(
            @SerializedName("sessionId") val sessionID: String,
            @SerializedName("template") val template: String,
            @SerializedName("iPageHtml") val iPageHtml: String
    )
}