package com.whitbread.premierinn.common.retrofitConfiguration

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.whitbread.premierinn.data.remote.GraphQLErrorBody
import com.whitbread.premierinn.data.remote.GraphQlThrowable
import com.whitbread.premierinn.data.remote.graphql.GraphQLBase
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.io.IOException

class GraphQLInterceptor(private val consumerType: ConsumerType): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(chain.request())

        if (request.url.toString().contains("graphql")) {
            return if (response.isSuccessful) {

                val responseBodyAsString = response.body?.string()

                responseBodyAsString?.let {
                    val listType = object : TypeToken<GraphQLBase.GraphQLBaseError>() {}.type
                    val graphQlErrorData = Gson().fromJson<GraphQLBase.GraphQLBaseError>(it, listType)

                    val newResponse =
                        response.newBuilder()
                            .body(it.toResponseBody("UTF-8".toMediaTypeOrNull()))
                            .build()
                    if (graphQlErrorData.errors != null) {
                        if (graphQlErrorData.errors!!.isNotEmpty()) {
                            // Since we only care about 401 so that we can login in background, only rewriting the response for this
                            val errorType = 401
                            val sessionExpiryAppSynch = graphQlErrorData.errors!!.find { baseError -> baseError.errorType == "401" }
                            val sessionExpiryApollo = graphQlErrorData.errors!!.find { baseError -> baseError.extensions?.apolloErrorType == "401" }
                            if (sessionExpiryAppSynch != null || sessionExpiryApollo != null) {
                                when (consumerType) {
                                    ConsumerType.RX -> throw GraphQlThrowable.GraphQLError(
                                        errorType,
                                        GraphQLErrorBody(errorType, "Session expired")
                                    )
                                    ConsumerType.NON_RX -> throw IOException("Session expired")
                                }
                            } else {
                                newResponse
                            }
                        } else {
                            newResponse
                        }
                    } else {
                        newResponse
                    }
                } ?: response
            } else {
                response
            }
        }
        return response
    }
}