package com.whitbread.premierinn.data.roombreakdown

import com.whitbread.premierinn.data.common.mapToPrice
import com.whitbread.premierinn.data.common.toPriceEntity
import com.whitbread.premierinn.data.remote.graphql.contracts.BookingConfirmationGraphQLContract
import com.whitbread.premierinn.domain.common.PriceDomain
import com.whitbread.premierinn.domain.common.RoomBreakdown

fun RoomBreakdownEntity.toDomain() : RoomBreakdown {
    return RoomBreakdown(
            roomId = roomId,
            totalRoomCost = unitCost.mapToPrice()!!
    )
}

fun BookingConfirmationGraphQLContract.RoomStayConfirmation.toAmendedRoomBreakdownEntity(reservationReference: String,
                                                                                         reservationId: String,
                                                                                         currency: String): RoomBreakdownEntity {
    return RoomBreakdownEntity.forAmendedReservation(
        roomId = reservationId,
        amendedReservationReference = reservationReference,
        unitCost = PriceDomain(roomPrice, currency).toPriceEntity()
    )
}