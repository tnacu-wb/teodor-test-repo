package com.whitbread.premierinn.data.graphql.mapper

import com.whitbread.premierinn.data.remote.graphql.contracts.AddNewRoomGraphQLContract
import com.whitbread.premierinn.domain.graphql.amend.entity.TempBookingRefDomain

fun AddNewRoomGraphQLContract.AddNewRoomData.mapToAddNewRoomGraphQL(): TempBookingRefDomain {
    return TempBookingRefDomain(
        tempBookingRef = this.data.addNewRoom.tempBookingRef
    )
}