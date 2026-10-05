package com.whitbread.premierinn.data.graphql.mapper

import com.whitbread.premierinn.data.remote.graphql.contracts.BookingInformationGraphQLContract
import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import com.whitbread.premierinn.domain.graphql.hdp.entity.BookingInformationDomain

fun BookingInformationGraphQLContract.BookingInformationData.mapToBookingInformationGQL(): BookingInformationDomain {
    return BookingInformationDomain(
            bookingFlowId = this.data?.bookingInformation?.bookingFlowId ?: EMPTY_STRING_DOMAIN
    )
}