package com.whitbread.premierinn.domain.dashboard.entity

import org.threeten.bp.LocalDate

data class DashboardItem(val type: String,
                         val content: Content?)

data class Content(val hotelImage: String,
                   val hotelName: String,
                   val hotelCode: String,
                   val map: Map,
                   val checkedIn: Boolean,
                   val confirmationNumber: String,
                   val arrivalDate: LocalDate?,
                   val departureDate: LocalDate?,
                   val rooms: List<Room>,
                   val guests: Int,
                   val actions: List<Action>,
                   val frequentBookings: List<FrequentBooking>)

data class Map(val latitude: Double?,
               val longitude: Double?)

data class Room(val type: String)

data class Action(val type: String,
                  val title: String)

data class FrequentBooking(val hotelImage: String,
                           val hotelName: String,
                           val hotelCode: String)