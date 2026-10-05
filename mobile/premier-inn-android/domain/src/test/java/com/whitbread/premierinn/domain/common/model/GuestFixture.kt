package com.whitbread.premierinn.domain.common.model

import com.whitbread.premierinn.domain.common.Address
import com.whitbread.premierinn.domain.common.Guest

object GuestFixture {

    fun aGuest(
            roomNumber: Int = 1,
            roomId: String = "",
            title: String = "Sir",
            firstName: String = "Joshua",
            lastName: String = "Onabanjo",
            guestHistoryNumber: String? = null,
            address: Address? = null,
            emailAddress: String? = null,
            phoneNumber: String? = null) : Guest {
        return Guest(
                roomNumber,
                roomId,
                title,
                firstName,
                lastName,
                guestHistoryNumber,
                address,
                emailAddress,
                phoneNumber
        )
    }

}