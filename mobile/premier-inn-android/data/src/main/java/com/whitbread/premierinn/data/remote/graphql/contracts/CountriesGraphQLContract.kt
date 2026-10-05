package com.whitbread.premierinn.data.remote.graphql.contracts

import com.google.gson.annotations.SerializedName
import com.whitbread.premierinn.data.remote.graphql.GraphQLBase

interface CountriesGraphQLContract {
    data class CountriesData(
        @SerializedName("data") val data: Data,
        @SerializedName("errors") val errors: List<GraphQLBase.BaseError>?
    )

    data class Data(
        @SerializedName("countries") val countries: Countries
    )

    data class Countries(
        @SerializedName("countries") val countries: List<Country>
    )

    data class Country(
        @SerializedName("countryCode") val countryCode: String,
        @SerializedName("countryCodeLegacy") val countryCodeLegacy: String,
        @SerializedName("countryName") val countryName: String,
        @SerializedName("dialingCode") val dialingCode: String,
        @SerializedName("flagSrc") val flagSrc: String,
        @SerializedName("passportRequired") val passportRequired: Boolean,
        @SerializedName("nationality") val nationality: String
    )

}