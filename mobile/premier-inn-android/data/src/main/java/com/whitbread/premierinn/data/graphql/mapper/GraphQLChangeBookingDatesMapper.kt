package com.whitbread.premierinn.data.graphql.mapper

import com.whitbread.premierinn.data.remote.graphql.contracts.ChangeBookingDatesGraphQLContract
import com.whitbread.premierinn.domain.graphql.bookingDetails.entity.ChangeBookingDatesDomain

fun ChangeBookingDatesGraphQLContract.ChangeBookingDatesData.mapToChangeBookingDatesGraphQL(): ChangeBookingDatesDomain {
    return ChangeBookingDatesDomain(
        tempBasket = this.data?.changeBookingDates?.tempBasket
    )
}