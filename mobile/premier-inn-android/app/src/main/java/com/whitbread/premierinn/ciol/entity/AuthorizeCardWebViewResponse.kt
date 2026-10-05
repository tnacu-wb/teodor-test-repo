package com.whitbread.premierinn.ciol.entity

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.whitbread.premierinn.data.common.EMPTY_STRING
import kotlinx.parcelize.Parcelize

@Parcelize
data class AuthorizeCardWebViewResponse(
    @SerializedName("transactionId") val transactionId: String = EMPTY_STRING,
    @SerializedName("paymentStatus") val authorizationStatus: String = EMPTY_STRING
): Parcelable
