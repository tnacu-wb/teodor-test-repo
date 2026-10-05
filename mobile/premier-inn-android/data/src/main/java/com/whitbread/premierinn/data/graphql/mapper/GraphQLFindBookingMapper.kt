package com.whitbread.premierinn.data.graphql.mapper

import com.whitbread.premierinn.data.remote.graphql.contracts.FindBookingGraphQLContract
import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import com.whitbread.premierinn.domain.common.OPERA_SOURCE
import com.whitbread.premierinn.domain.graphql.findBooking.entity.FindBookingDomain

fun FindBookingGraphQLContract.FindBookingData.mapToFindBookingDomain(reference: String): FindBookingDomain {
    return FindBookingDomain(
        sourcePms = this.data?.findBooking?.sourcePms ?: OPERA_SOURCE,
        bookingReference = this.data?.findBooking?.bookingReference ?: reference,
        uuidBasketReference = this.data?.findBooking?.basketReference ?: EMPTY_STRING_DOMAIN,
        token = this.data?.findBooking?.token,
        hotelId = this.data?.findBooking?.hotelId ?: EMPTY_STRING_DOMAIN,
        isThirdPartyBooking = this.data?.findBooking?.isThirdPartyBooking ?: false
    )
}
