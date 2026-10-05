package com.whitbread.premierinn.data.graphql.error

import com.whitbread.premierinn.data.remote.GraphQlThrowable
import com.whitbread.premierinn.domain.authentication.UnAuthorizedCustomerError
import com.whitbread.premierinn.domain.error.DataError
import java.io.IOException

fun Throwable.handleError(): DataError.Network {
    // TODO adapt later with onGraphQLError() method
    return when (this) {
        is UnAuthorizedCustomerError -> DataError.Network.UnauthorizedCustomerError(this.message)
        is IOException -> DataError.Network.BaseError(this.message)
        is GraphQlThrowable.GraphQLError -> DataError.Network.GraphQlError(this.message)
        else -> DataError.Network.Unknown(this.message)
    }
}