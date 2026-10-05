package com.whitbread.premierinn.businessbooker.data.remote

import com.google.gson.annotations.SerializedName

interface LoginApiContract {
    data class BusinessBookerLoginBody(@SerializedName("username") val username: String,
                                       @SerializedName("password") val password: String)

    data class BusinessBookerLoginResponse(@SerializedName("sessionId") val sessionId: String)

    data class BusinessBookerLogoutBody(@SerializedName("sessionId") val sessionId: String)

    data class BusinessBookerLogoutResponse(@SerializedName("logoutSuccessful") val logoutSuccessful: Boolean)
}