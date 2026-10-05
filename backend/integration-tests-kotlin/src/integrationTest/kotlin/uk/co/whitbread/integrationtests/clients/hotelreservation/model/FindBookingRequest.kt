package uk.co.whitbread.integrationtests.clients.hotelreservation.model

data class FindBookingRequest(
    val resNo: String,
    val arrivalDate: String,
    val lastName: String,
    val channel: String,
    val subchannel: String,
    val country: String = "gb",
    val language: String = "en",
    val isOldBooking: Boolean = false,
)
