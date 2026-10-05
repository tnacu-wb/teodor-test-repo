package com.whitbread.premierinn.domain.authentication

data class AuthenticationError(val errorType: Type,
                               override val cause: Throwable? = null,
                               override val message: String? = null) : Throwable(message) {
    companion object {
        fun ofType(errorType: Type, cause: Throwable? = null): AuthenticationError {
            return AuthenticationError(errorType, cause)
        }

        fun unexpected(message: String): AuthenticationError {
            return AuthenticationError(Type.UNEXPECTED, null, message)
        }
    }

    enum class Type {
        SAVE_CREDENTIALS,
        UNEXPECTED,
        NETWORK,
        TOO_MANY_ATTEMPTS,
        INVALID_CREDENTIALS,
        NO_LONGER_VALID_CREDENTIALS
    }
}