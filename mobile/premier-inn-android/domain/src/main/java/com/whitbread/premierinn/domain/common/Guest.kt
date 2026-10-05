package com.whitbread.premierinn.domain.common

data class Guest(val roomNumber: Int = 0,
                 val roomId: String = "",
                 val title: String,
                 val firstName: String,
                 val lastName: String,
                 val guestHistoryNumber: String?,
                 val address: Address?,
                 val emailAddress: String?,
                 val phoneNumber: String?) {
    companion object {
        fun createDefault(): Guest {
            return Guest(roomNumber = -1, roomId = EMPTY_STRING_DOMAIN, title = EMPTY_STRING_DOMAIN,
                firstName = EMPTY_STRING_DOMAIN, lastName = EMPTY_STRING_DOMAIN,
                guestHistoryNumber = EMPTY_STRING_DOMAIN, address = null,
                emailAddress = EMPTY_STRING_DOMAIN, phoneNumber = EMPTY_STRING_DOMAIN)
        }
    }
}