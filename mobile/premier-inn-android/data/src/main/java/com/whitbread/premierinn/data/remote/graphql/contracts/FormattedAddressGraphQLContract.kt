package com.whitbread.premierinn.data.remote.graphql.contracts

import com.google.gson.annotations.SerializedName
import com.whitbread.premierinn.data.remote.graphql.GraphQLBase

interface FormattedAddressGraphQLContract {

    data class FormattedAddressData(
        @SerializedName("data") val data: Data,
        @SerializedName("errors") val errors: List<GraphQLBase.BaseError>?
    )

    data class Data(
        @SerializedName("formattedAddress") val formattedAddress: FormattedAddress
    )

    data class FormattedAddress(
        @SerializedName("addressLine1") val addressLine1: String,
        @SerializedName("addressLine2") val addressLine2: String?,
        @SerializedName("addressLine3") val addressLine3: String?,
        @SerializedName("addressLine4") val addressLine4: String?,
        @SerializedName("addressLine5") val addressLine5: String?,
        @SerializedName("postalCode") val postalCode: String,
        @SerializedName("companyName") val companyName: String?,
        @SerializedName("country") val country: String?
    )
}