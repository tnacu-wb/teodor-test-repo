package com.whitbread.premierinn.domain.graphql.reviewBooking.entity

data class BasketStatusRevisedPaymentsDomain(
        val basketStatus: BasketStatusRevised,
        val createdAt: String,
        val basketError: BasketErrorDomain?
)

data class BasketErrorDomain(
        val code: String,
        val description: String,
        val type: String
)

enum class BasketStatusRevised {
        PAY_PENDING,
        AMENDING,
        PROCESSING,
        COMPLETED,
        PRE_CHECKED_IN,
        AMENDED,
        FAILED,
        AMEND_FAILED,
        OPEN
}