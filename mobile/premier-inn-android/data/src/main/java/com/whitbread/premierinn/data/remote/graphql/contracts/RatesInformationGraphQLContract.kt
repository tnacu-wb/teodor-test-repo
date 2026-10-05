package com.whitbread.premierinn.data.remote.graphql.contracts

import com.google.gson.annotations.SerializedName
import com.whitbread.premierinn.data.remote.graphql.GraphQLBase

interface RatesInformationGraphQLContract {

    data class RatesInformationData(
            @SerializedName("data") val data: Data?,
            @SerializedName("errors") val errors: List<GraphQLBase.BaseError>?)

    data class Data(
        @SerializedName("ratesInformationV2") val ratesInformationV2: RatesInformation?,
    )

    data class RatesInformation(
            @SerializedName("rateClassifications") val rateClassifications: List<RateClassificationExtraInfo>
    )

    data class RateClassificationExtraInfo(
            @SerializedName("rateClassification") val rateClassification: String,
            @SerializedName("rateOrder") val rateOrder: String,
            @SerializedName("rateName") val rateName: String,
            @SerializedName("rateDescription") val rateDescription: String,
            @SerializedName("rateLongDescription") val rateLongDescription: String,
            @SerializedName("rateNotes") val rateNotes: String,
            @SerializedName("rateTags") val rateTags: List<String>
    )

}