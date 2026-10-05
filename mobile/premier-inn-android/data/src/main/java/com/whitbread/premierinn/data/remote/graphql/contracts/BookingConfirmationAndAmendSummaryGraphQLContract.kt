package com.whitbread.premierinn.data.remote.graphql.contracts

import com.google.gson.annotations.SerializedName
import com.whitbread.premierinn.data.common.toLocalDate
import com.whitbread.premierinn.data.remote.graphql.GraphQLBase
import com.whitbread.premierinn.data.remote.graphql.contracts.AmendSummaryGraphQLContract.AmendSummary
import com.whitbread.premierinn.data.remote.graphql.contracts.BookingConfirmationGraphQLContract.ManageBooking
import org.threeten.bp.temporal.ChronoUnit

interface BookingConfirmationAndAmendSummaryGraphQLContract {

    data class BookingConfirmationAndAmendSummaryData(
        @SerializedName("data") val data: Data,
        @SerializedName("errors") val errors: List<GraphQLBase.BaseError>?
    )

    data class Data(
        @SerializedName("bookingConfirmation") val bookingConfirmation: BookingConfirmationGraphQLContract.BookingConfirmation?,
        @SerializedName("manageBooking") val manageBooking: ManageBooking?,
        @SerializedName("packages") val packages: PackagesGraphQLContract.Packages?,
        @SerializedName("amendSummary") val amendSummary: AmendSummary?)
}