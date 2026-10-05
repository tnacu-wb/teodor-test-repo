package com.whitbread.premierinn.data.recentsearch.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import org.threeten.bp.LocalDate

@Entity(tableName = "dashboard_recent_search",
        primaryKeys = ["search_term", "hotel_code", "latitude", "longitude", "hotel_brand", "arrival_date",
            "departure_date", "rooms_count", "adults", "children", "infants", "cots", "room_type_codes"])
data class RecentSearchEntity(@ColumnInfo(name = "search_term") val searchTerm: String,
                              @ColumnInfo(name = "hotel_code") val hotelCode: String,
                              @ColumnInfo(name = "latitude") val latitude: Float,
                              @ColumnInfo(name = "longitude") val longitude: Float,
                              @ColumnInfo(name = "hotel_brand") val hotelBrand: String,
                              @ColumnInfo(name = "arrival_date") val arrivalDate: LocalDate,
                              @ColumnInfo(name = "departure_date") val departureDate: LocalDate,
                              @ColumnInfo(name = "rooms_count") val roomsCount: Int,
                              @ColumnInfo(name = "adults") val adults: List<Int>,
                              @ColumnInfo(name = "children") val children: List<Int>,
                              @ColumnInfo(name = "infants") val infants: List<Int>,
                              @ColumnInfo(name = "cots") val cots: List<Boolean>,
                              @ColumnInfo(name = "room_type_codes") val roomTypeCodes: List<String>,
                              @ColumnInfo(name = "date_created") val dateCreated: Long)