package com.whitbread.premierinn.hoteldetails

// Assuming order of room info tabs so we can programmatically switch to correct tab
enum class HubRoomInfoOrder(val position: Int) {
    STANDARD(0), BIGGER(1), ACCESSIBLE(2)
}