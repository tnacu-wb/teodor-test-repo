package com.whitbread.premierinn.data.remote

import com.google.gson.annotations.SerializedName

sealed class ApiThrowable : Throwable() {
    data class Network(val throwable: Throwable) : ApiThrowable()
    data class Http @JvmOverloads constructor(val httpCode: Int, var apiErrorBody: ApiError? = null,
                    var apiErrorResponseBody: ApiErrorResponse? = null) : ApiThrowable() {
        val isErrorSessionId: Boolean
            get() = httpCode == 400 && apiErrorBody?.code == 100
    }

    data class Generic(val throwable: Throwable) : ApiThrowable()
}

data class ApiError(@SerializedName("code") val code: Int,
                    @SerializedName("details") val details: List<String>)

data class ApiErrorResponse(@SerializedName("errorCode") val errorCode: Int,
                    @SerializedName("message") val details: String)