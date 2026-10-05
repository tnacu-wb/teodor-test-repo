package com.whitbread.premierinn.common

enum class PaymentMethodType {
    SAVED_CARD,
    NEW_CARD,
    NEW_PIBA,
    NEW_PIBA_EURO {
        override fun toString(): String {
            return "PIBADE"
        }
    },
    PAYPAL,
    GP
}