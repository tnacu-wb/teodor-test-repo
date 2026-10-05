package com.whitbread.premierinn.data.roomcriteria

import com.whitbread.premierinn.data.remote.graphql.contracts.BookingConfirmationGraphQLContract
import com.whitbread.premierinn.domain.common.RoomCriteria
import com.whitbread.premierinn.domain.common.toRoomTypeGQL

fun RoomCriteriaEntity.toDomain(): RoomCriteria {
    return RoomCriteria(
            numberOfAdults = numberOfAdults,
            numberOfChildren = numberOfChildren,
            numberOfInfants = 0, //cannot populate that field for now
            includeCot = cot,
            roomType = roomType,
            roomId = roomId
    )
}

fun BookingConfirmationGraphQLContract.ReservationByIdConfirmation.toAmendRoomCriteriaEntity(reservationReference: String): RoomCriteriaEntity {
    return RoomCriteriaEntity.forAmendedReservation(
        amendedReservationReference = reservationReference,
        roomType = roomStay.roomType.toRoomTypeGQL(),
        numberOfAdults = roomStay.adultsNumber.toInt(),
        numberOfChildren = roomStay.childrenNumber.toInt(),
        cot = false,
        roomId = reservationId
    )
}

