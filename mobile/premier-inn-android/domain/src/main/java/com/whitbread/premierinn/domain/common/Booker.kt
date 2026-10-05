package com.whitbread.premierinn.domain.common

data class Booker(
        val address: Address,
        val emailAddress: String,
        val firstName: String,
        val lastName: String,
        val telephoneNumber: String,
        val title: String,
        val guestHistoryNumber:String
)