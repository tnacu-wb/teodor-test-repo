package com.whitbread.premierinn.ciol.usecase

fun interface ValidatorUseCase {
    operator fun invoke(inputToValidate: String): Boolean
}
