package com.whitbread.premierinn.data.remote.graphql.contracts

import com.google.gson.annotations.SerializedName
import com.whitbread.premierinn.data.remote.graphql.GraphQLBase

interface BookingHistoryGraphQLContract {
    data class BookingHistoryData(
        @SerializedName("data") val data: Data?,
        @SerializedName("errors") val errors: List<GraphQLBase.BaseError>?
    )

    data class Data(
        @SerializedName("bookingHistory") val bookingHistory: BookingHistory
    )

    data class BookingHistory(
        @SerializedName("bookings") val bookings: List<BookingsFromBookingHistory>,
    )

    data class BookingsFromBookingHistory(
        @SerializedName("hotelName") val hotelName: String,
        @SerializedName("arrivalDate") val arrivalDate: String,
        @SerializedName("bookingReference") val bookingReference: String,
        @SerializedName("totalCost") val totalCost: Price,
        @SerializedName("hotelCode") val hotelCode: String,
        @SerializedName("departureDate") val departureDate: String,
        @SerializedName("noOfRooms") val noOfRooms: Int,
        @SerializedName("prePaidAmount") val prePaidAmount: Price?,
        @SerializedName("cancelled") val cancelled: Boolean,
        @SerializedName("checkInOnline") val checkInOnline: Boolean,
        @SerializedName("checkedIn") val checkedIn: String,
        @SerializedName("amendable") val amendable: Boolean,
        @SerializedName("rateName") val rateName: String,
        @SerializedName("leadGuest") val leadGuest: String,
        @SerializedName("leadGuestSurname") val leadGuestSurname: String,
        @SerializedName("bookingStatus") val bookingStatus: String?,
        @SerializedName("isCheckInOnlineAvailable") val isCheckInOnlineAvailable: Boolean,
        @SerializedName("basketStatus") val basketStatus: String?,
        @SerializedName("hotelCountry") val hotelCountry: String?
    )

    data class Price(
        @SerializedName("amount") val amount: Float,
        @SerializedName("currency") val currency: String
    )

}