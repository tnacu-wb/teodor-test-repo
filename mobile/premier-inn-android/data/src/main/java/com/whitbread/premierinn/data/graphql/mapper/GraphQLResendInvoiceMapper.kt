package com.whitbread.premierinn.data.graphql.mapper

import com.whitbread.premierinn.data.remote.graphql.contracts.ResendInvoiceGraphQLContract
import com.whitbread.premierinn.domain.graphql.bookingDetails.entity.ResendInvoiceDomain

fun ResendInvoiceGraphQLContract.ResendInvoiceData.mapToDomain(): ResendInvoiceDomain {
    return ResendInvoiceDomain(
        resendInvoiceEmail = this.data.resendInvoiceEmail
    )
}
