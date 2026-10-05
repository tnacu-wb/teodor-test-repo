package com.whitbread.premierinn.summary

import com.whitbread.premierinn.common.BookingFlowInput
import com.whitbread.premierinn.reviewbooking.ReviewBookingInput

sealed class SummaryNavigation {
    data class OpenGuestDetailsActivity(val bookingFlowInput: BookingFlowInput) : SummaryNavigation()
    data class OpenReviewBookActivity(val reviewBookingInput: ReviewBookingInput) : SummaryNavigation()
    data class OpenAdditionalInfoActivity(val reviewBookingInput: ReviewBookingInput) : SummaryNavigation()
    data object OpenLoginActivity : SummaryNavigation()
}
