package com.whitbread.premierinn.data.graphql.mapper

import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.data.remote.graphql.contracts.RemoveRoomGraphQLContract
import com.whitbread.premierinn.domain.graphql.amend.entity.TempBookingRefDomain


fun RemoveRoomGraphQLContract.RemoveRoomData.mapToRemoveRoomGQL(): TempBookingRefDomain {
    return TempBookingRefDomain(
        tempBookingRef = this.data?.removeRoom?.tempBookingRef ?: EMPTY_STRING
    )
}