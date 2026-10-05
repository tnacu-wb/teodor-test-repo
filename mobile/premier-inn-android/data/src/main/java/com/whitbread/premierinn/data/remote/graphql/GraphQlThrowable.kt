package com.whitbread.premierinn.data.remote

import com.google.gson.annotations.SerializedName

/**
 *
 */
sealed class GraphQlThrowable : ApiThrowable() {

    data class GraphQLError @JvmOverloads constructor(val httpCode: Int,
                                                      var graphqlErrorBody: GraphQLErrorBody) : GraphQlThrowable()
}

data class GraphQLErrorBody(@SerializedName("code") val code: Int,
                            @SerializedName("message") val message: String?)
