package com.whitbread.premierinn.summary

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class SummaryError : Parcelable{
    @Parcelize data object GenericError : SummaryError()
    @Parcelize data object CreateReservationError : SummaryError()
    @Parcelize data object CreateReservationGuestError : SummaryError()
    @Parcelize data class  SaveReservationWithAncillariesError(val mealError: MealSelectionStatus?, val isLoggedIn: Boolean) : SummaryError()
    @Parcelize data object PaymentMethodsAndBookingConfirmationError : SummaryError()
    @Parcelize data object BookingConfirmationError : SummaryError()

    class SummaryErrorException(val error: SummaryError) : Exception("Encountered error: $error")
}

enum class MealSelectionStatus {
    NO_SELECTION,
    NEW_MEAL_ERROR,
    PREVIOUS_MEAL_ERROR
}
