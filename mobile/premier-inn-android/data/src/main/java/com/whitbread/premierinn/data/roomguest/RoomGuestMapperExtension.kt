package com.whitbread.premierinn.data.roomguest

import com.whitbread.premierinn.data.remote.graphql.contracts.BookingConfirmationGraphQLContract
import com.whitbread.premierinn.domain.common.Guest

fun RoomGuestEntity.toDomain(): Guest {
        return Guest(
            roomId = roomId,
            title = title,
            firstName = firstName,
            lastName = lastName,
            guestHistoryNumber = guestHistoryNumber,
            address = null, //todo
            phoneNumber = null,
            emailAddress = emailAddress)
}

fun BookingConfirmationGraphQLContract.GuestInfo.toAmendedGuestEntity(bookingReference: String, roomId: String): RoomGuestEntity {
        return RoomGuestEntity.forAmendedReservation(
                amendedReservationReference = bookingReference,
                roomId = roomId,
                title = title?: "Mr",
                firstName = givenName,
                lastName = surName,
                guestHistoryNumber = bookingReference,
                emailAddress = email
        )
}