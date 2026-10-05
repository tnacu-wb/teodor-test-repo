package com.whitbread.premierinn.domain.error

interface DataError : Error {

    sealed class Network: DataError {
        abstract val message: String?

        data class GraphQlError(override val message: String?): Network()
        data class UnauthorizedCustomerError(override val message: String?): Network()
        data class BaseError(override val message: String?): Network()
        data class Unknown(override val message: String?): Network()
    }
}