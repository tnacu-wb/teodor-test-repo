package com.whitbread.premierinn.data.remote

import com.google.gson.annotations.SerializedName
import org.threeten.bp.LocalDate

interface DashboardApiContract {

    data class DashboardResponse(@SerializedName("type") val type: String,
                                 @SerializedName("content") val content: Content?)

    data class Content(@SerializedName("hotelImage") val hotelImage: String?,
                       @SerializedName("hotelName") val hotelName: String?,
                       @SerializedName("hotelCode") val hotelCode: String?,
                       @SerializedName("map") val map: Map?,
                       @SerializedName("checkedIn") val checkedIn: Boolean,
                       @SerializedName("confirmationNumber") val confirmationNumber: String?,
                       @SerializedName("arrivalDate") val arrivalDate: LocalDate?,
                       @SerializedName("departureDate") val departureDate: LocalDate?,
                       @SerializedName("rooms") val rooms: List<Room>?,
                       @SerializedName("guests") val guests: Int,
                       @SerializedName("actions") val actions: List<Actions>?,
                       @SerializedName("frequentBookings") val frequentBookings: List<FrequentBooking>?)

    data class Map(@SerializedName("latitude") val latitude: Double?,
                   @SerializedName("longitude") val longitude: Double?)

    data class Room(@SerializedName("type") val type: String)

    data class Actions(@SerializedName("type") val type: String,
                       @SerializedName("title") val title: String)

    data class FrequentBooking(@SerializedName("hotelImage") val hotelImage: String,
                               @SerializedName("hotelName") val hotelName: String,
                               @SerializedName("hotelCode") val hotelCode: String)
}