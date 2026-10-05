package com.whitbread.premierinn.data.remote.graphql

import com.google.gson.annotations.SerializedName

interface GraphQLBase {
    data class GraphQLBaseError(
        @SerializedName("errors") val errors: List<BaseError>?
    )
    data class BaseError(
            @SerializedName("path") val path: List<String>? = emptyList(),
            @SerializedName("errorType") val errorType: String?,
            @SerializedName("message") val message: String? = null,
            @SerializedName("extensions") val extensions: ApolloExtensions? = null,
    )

    data class ApolloExtensions(
        @SerializedName("errorType") val apolloErrorType: String?)
}