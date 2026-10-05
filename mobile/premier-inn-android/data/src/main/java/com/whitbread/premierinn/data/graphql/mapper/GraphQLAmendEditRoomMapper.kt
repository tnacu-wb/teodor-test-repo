package com.whitbread.premierinn.data.graphql.mapper

import com.whitbread.premierinn.data.remote.graphql.contracts.AmendEditRoomGraphQLContract
import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import com.whitbread.premierinn.domain.graphql.amend.entity.TempBookingRefDomain


fun AmendEditRoomGraphQLContract.AmendEditRoomData.mapToAmendEditRoomGQL(): TempBookingRefDomain {
    return TempBookingRefDomain(
        tempBookingRef = this.data?.amendEditRoom?.tempBookingRef ?: EMPTY_STRING_DOMAIN
    )
}