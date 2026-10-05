package com.whitbread.premierinn.data.remote

import com.google.gson.annotations.SerializedName

interface PaymentsConfirmationStatusContract {

    data class PaymentsConfirmationStatusResponse(
            @SerializedName("bookingStatus")
            val bookingStatus: PaymentBookingStatus,
            @SerializedName("code")
            val code: String,
            @SerializedName("confirmationNumber")
            val confirmationNumber: String? = null,
            @SerializedName("prepaymentSuccess")
            val prepaymentSuccess: Boolean,
            @SerializedName("sessionId")
            val sessionId: String,
    )

    enum class PaymentBookingStatus {
        PENDING,
        FAILED,
        COMPLETE
    }
}