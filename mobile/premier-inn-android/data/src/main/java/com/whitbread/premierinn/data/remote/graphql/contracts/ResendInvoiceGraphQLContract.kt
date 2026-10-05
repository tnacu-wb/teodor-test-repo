package com.whitbread.premierinn.data.remote.graphql.contracts

import com.whitbread.premierinn.data.remote.graphql.GraphQLBase

interface ResendInvoiceGraphQLContract {

    data class ResendInvoiceData(
        val data: Data,
        val errors: List<GraphQLBase.BaseError>?
    )

    data class Data(
        val resendInvoiceEmail: String
    )
}