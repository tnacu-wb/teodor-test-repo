package com.whitbread.premierinn.data.remote.graphql.contracts

import com.google.gson.annotations.SerializedName
import com.whitbread.premierinn.data.remote.graphql.GraphQLBase

interface AnonymousNewsletterPreferencesGraphQLContract {

    data class AnonymousNewsletterPreferencesData(
        @SerializedName("data") val data: Data?,
        @SerializedName("errors") val errors: List<GraphQLBase.BaseError>?
    )

    data class Data(
        @SerializedName("anonymousNewsletterPreferences") val anonymousNewsletterPreferences: AnonymousNewsletterPreferences?
    )

    data class AnonymousNewsletterPreferences(
        @SerializedName("optIn") val optIn: Boolean?,
        @SerializedName("suppressMarketingCheckbox") val suppressMarketingCheckbox: Boolean?
    )
}