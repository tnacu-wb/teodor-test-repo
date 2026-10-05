package com.whitbread.premierinn.data.graphql.mapper

import com.whitbread.premierinn.data.remote.graphql.contracts.CopyBookingGraphQLContract
import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import com.whitbread.premierinn.domain.graphql.amend.entity.CopyBookingDomain


fun CopyBookingGraphQLContract.CopyBookingData.mapToCopyBookingGQL(): CopyBookingDomain {
    return CopyBookingDomain(
        copyBasketReference = this.data.copyBooking?.copyBasketReference ?: EMPTY_STRING_DOMAIN
    )
}