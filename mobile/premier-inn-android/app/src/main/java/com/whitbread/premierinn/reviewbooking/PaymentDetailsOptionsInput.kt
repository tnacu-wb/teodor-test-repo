package com.whitbread.premierinn.reviewbooking

data class PaymentDetailsOptionsInput(
    val listOfSavedCardPaymentOptions: List<String>? = emptyList(),
    val listOfNewCardPaymentOptions: List<String> = emptyList(),
    val listOfNewPIBACardPaymentOptions: List<String>? = emptyList())
