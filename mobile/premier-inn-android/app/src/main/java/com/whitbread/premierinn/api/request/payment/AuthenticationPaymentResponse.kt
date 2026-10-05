package com.whitbread.premierinn.api.request.payment

import com.google.gson.annotations.SerializedName

data class AuthenticationPaymentResponse(@SerializedName("sessionId") val sessionId: String,
                                         @SerializedName("transactionStatus") val transactionStatus: String,
                                         @SerializedName("authenticationStatus") val authenticationStatus: String,
                                         @SerializedName("redirectHtml") val redirectHtml: String,
                                         @SerializedName("success") val success: Boolean)