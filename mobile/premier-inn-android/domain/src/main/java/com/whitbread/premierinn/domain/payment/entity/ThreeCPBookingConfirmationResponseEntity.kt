package com.whitbread.premierinn.domain.payment.entity


data class ThreeCPBookingConfirmationResponseEntity (
        val bookingStatus: PaymentBookingStatusDomain,
        val confirmationNumber: String?,
        val prepaymentSuccess: Boolean,
        val code: String?)

enum class PaymentBookingStatusDomain {
        PENDING,
        FAILED,
        COMPLETE
}
