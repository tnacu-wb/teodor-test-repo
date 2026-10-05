package com.whitbread.premierinn.domain.recentsearch.entity

import org.threeten.bp.LocalDate

data class RecentSearch(val searchTerm: String,
                        val hotelCode: String?,
                        val latitude: Float,
                        val longitude: Float,
                        val hotelBrand: String?,
                        val arrivalDate: LocalDate,
                        val departureDate: LocalDate,
                        val roomsCount: Int,
                        val adults: List<Int>,
                        val children: List<Int>,
                        val infants: List<Int>,
                        val cots: List<Boolean>,
                        val roomTypeCodes: List<String>)