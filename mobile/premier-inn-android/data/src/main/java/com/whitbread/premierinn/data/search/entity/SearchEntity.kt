package com.whitbread.premierinn.data.search.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent_search")
data class SearchEntity(@PrimaryKey
                        @ColumnInfo(name = "search_term") val searchTerm: String,
                        @ColumnInfo(name = "lat") val lat: Double,
                        @ColumnInfo(name = "lon") val lon: Double,
                        @ColumnInfo(name = "date_created") val date: Long,
                        @ColumnInfo(name = "hotel_id") val hotelId: String? = null,
                        @ColumnInfo(name = "hotel_brand") val hotelBrand: String? = null)

