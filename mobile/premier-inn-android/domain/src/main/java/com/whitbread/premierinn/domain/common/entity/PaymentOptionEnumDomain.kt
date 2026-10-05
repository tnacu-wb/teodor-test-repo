package com.whitbread.premierinn.domain.common.entity

enum class PaymentOptionEnumDomain {
    CC,
    PIBA_CP,
    PIBA_CNP
}

fun String.isPibaCNPBooking(): Boolean = this == PaymentOptionEnumDomain.PIBA_CNP.name
fun String.isPibaCPBooking(): Boolean = this == PaymentOptionEnumDomain.PIBA_CP.name

