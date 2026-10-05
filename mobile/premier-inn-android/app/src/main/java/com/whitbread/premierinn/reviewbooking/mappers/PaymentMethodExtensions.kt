package com.whitbread.premierinn.reviewbooking.mappers

import com.whitbread.premierinn.common.PaymentMethodType
import com.whitbread.premierinn.common.PaymentTimingRule
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.reviewbooking.ParcelableCard
import com.whitbread.premierinn.reviewbooking.ParcelablePaymentMethod
import com.whitbread.premierinn.reviewbooking.ParcelablePaymentOption


const val EXPIRY_DATE_SEPARATOR = "/"

fun List<ParcelablePaymentMethod>.getListOfApplicablePaymentMethod() : List<ParcelablePaymentMethod> {
    return this.filter { it.enabled }
}

fun List<ParcelablePaymentMethod>.getReasonForDisabledSavedCard() : String {
    val disabledCard =  this.filter { it.type == PaymentMethodType.SAVED_CARD.name }.filter { !it.enabled }
    if (disabledCard.isNotEmpty()) {
        disabledCard.first().reasons?.let { reasons ->
            return reasons.takeIf { reasons.isNotEmpty() }?.let { reasons[0].name } ?: EMPTY_STRING
        } ?: return EMPTY_STRING
    } else return EMPTY_STRING
}

fun List<ParcelablePaymentMethod>.getLastFourDigitsForDisabledSavedCard() : String {
    val savedCard =  this.filter { it.type == PaymentMethodType.SAVED_CARD.name }.filter { !it.enabled }
    if (savedCard.isNotEmpty()) {
        savedCard.first().parcelableCard?.let {
            return it.token.getLastNChars(4)
        } ?: return EMPTY_STRING
    } else return EMPTY_STRING
}

fun ParcelableCard.getExpiryDate(): String {
    return this.expiryMonth + EXPIRY_DATE_SEPARATOR + this.expiryYear
}

fun ParcelablePaymentMethod.getPaymentOptions(): List<String> {
    return this.parcelablePaymentOptions.filter { it.enabled }.toListOfPaymentOptions()
}

fun List<ParcelablePaymentOption>.toListOfPaymentOptions() : List<String> {
    val paymentOptionTypeList = mutableListOf<String>()
    this.forEach { paymentOption ->
        paymentOptionTypeList.add(paymentOption.type)
    }

    return paymentOptionTypeList
}

fun List<String>.toPaymentTimingRule(): PaymentTimingRule {
    val filterPaymentOptionsWithoutRwaC = this.filter { it != "RESERVE_WITHOUT_CARD" }
    return when (filterPaymentOptionsWithoutRwaC.size) {
        0 -> PaymentTimingRule.PAY_NOW
        1 -> filterPaymentOptionsWithoutRwaC.first().toPaymentTimingValue()
        2 -> PaymentTimingRule.PAY_NOW_OR_PAY_LATER
        else -> PaymentTimingRule.PAY_LATER
    }
}

private fun String.toPaymentTimingValue(): PaymentTimingRule {
    return when (this) {
        "PAY_NOW" -> PaymentTimingRule.PAY_NOW
        "PAY_ON_ARRIVAL" -> PaymentTimingRule.PAY_LATER
        else -> PaymentTimingRule.PAY_LATER
    }
}

fun List<ParcelablePaymentMethod>.getSavedCard() : ParcelablePaymentMethod {
    return this.filter { it.enabled }.first { it.type == "SAVED_CARD" }
}

fun List<ParcelablePaymentMethod>.getNewCard() : ParcelablePaymentMethod {
    return this.filter { it.enabled }.first { it.type == "NEW_CARD" }
}

fun List<ParcelablePaymentMethod>.getNewPIBACard() : ParcelablePaymentMethod {
    return this.filter { it.enabled }.first { it.type == "NEW_PIBA" }
}

fun String.getLastNChars(numberOfChars: Int): String {
    return this.takeLast(numberOfChars)
}