package com.whitbread.premierinn.common

/**
 *
 */
sealed class Result<T> {
    class Loading<T> : Result<T>()
    data class Success<T>(val type: T) : Result<T>()
    data class Error<T>(val type: T) : Result<T>()
}