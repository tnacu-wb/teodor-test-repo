package com.whitbread.premierinn.hoteldetails

sealed class HotelAction {
    object Loading : HotelAction()
    object Success : HotelAction()
    object Error : HotelAction()
}
