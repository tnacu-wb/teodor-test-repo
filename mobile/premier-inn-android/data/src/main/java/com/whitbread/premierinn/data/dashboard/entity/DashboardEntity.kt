package com.whitbread.premierinn.data.dashboard.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.LocalDate

@Entity(tableName = "dashboard")
data class DashboardEntity(@PrimaryKey(autoGenerate = true)
                           @ColumnInfo(name = "id") val id: Long = 0,
                           @ColumnInfo(name = "type") val type: String,
                           @Embedded(prefix = "content_") val content: ContentEntity?)

data class ContentEntity(@ColumnInfo(name = "hotel_image") val hotelImage: String?,
                         @ColumnInfo(name = "hotel_name") val hotelName: String?,
                         @ColumnInfo(name = "hotel_code") val hotelCode: String?,
                         @Embedded(prefix = "map_") val map: MapEntity?,
                         @ColumnInfo(name = "checked_in") val checkedIn: Boolean,
                         @ColumnInfo(name = "confirmation_number") val confirmationNumber: String?,
                         @ColumnInfo(name = "arrival_date") val arrivalDate: LocalDate?,
                         @ColumnInfo(name = "departure_date") val departureDate: LocalDate?,
                         @ColumnInfo(name = "rooms") val rooms: List<RoomEntity>,
                         @ColumnInfo(name = "guests") val guests: Int,
                         @ColumnInfo(name = "actions") val actions: List<ActionEntity>,
                         @ColumnInfo(name = "frequent_bookings") val frequentBookings: List<FrequentBookingEntity>)

data class MapEntity(@ColumnInfo(name = "latitude") val latitude: Double?,
                     @ColumnInfo(name = "longitude") val longitude: Double?)

data class RoomEntity(@ColumnInfo(name = "type") val type: String)

data class ActionEntity(@ColumnInfo(name = "type") val type: String,
                        @ColumnInfo(name = "title") val title: String)

data class FrequentBookingEntity(@ColumnInfo(name = "hotel_image") val hotelImage: String,
                                 @ColumnInfo(name = "hotel_name") val hotelName: String,
                                 @ColumnInfo(name = "hotel_code") val hotelCode: String)