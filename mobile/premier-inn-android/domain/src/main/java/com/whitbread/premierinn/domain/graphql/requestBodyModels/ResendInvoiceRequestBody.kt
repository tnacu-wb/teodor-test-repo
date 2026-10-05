package com.whitbread.premierinn.domain.graphql.requestBodyModels

data class ResendInvoiceRequestBody(
    val invoiceRecordNumber: String = "0",
    val hotelId: String,
    val email: String,
    val bookingReference: String,
    val bookingChannel: BookingChannelDetails
)
