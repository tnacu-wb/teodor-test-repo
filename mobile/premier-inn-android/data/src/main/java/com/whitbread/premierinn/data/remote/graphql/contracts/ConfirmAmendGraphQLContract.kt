package com.whitbread.premierinn.data.remote.graphql.contracts

import com.google.gson.annotations.SerializedName
import com.whitbread.premierinn.data.remote.graphql.GraphQLBase

interface ConfirmAmendGraphQLContract {

    data class ConfirmAmendData(
        @SerializedName("data") val data: Data?,
        @SerializedName("errors") val errors: List<GraphQLBase.BaseError>?
    )

    data class Data(
        @SerializedName("confirmAmendLogic") val confirmAmendLogic: ConfirmAmendLogic?
    )

    data class ConfirmAmendLogic(
        @SerializedName("payment")
        val payment: AmendPaymentDetails
    )

    data class AmendPaymentDetails(
        @SerializedName("status")
        val status: String,
        @SerializedName("paymentRequiredDetails")
        val paymentRequiredDetails: AmendPaymentRequiredDetails?,
    )

    data class AmendPaymentRequiredDetails(
        @SerializedName("paymentRedirect")
        val paymentRedirect: String,
        @SerializedName("sessionId")
        val sessionId: String
    )
}