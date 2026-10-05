package com.whitbread.premierinn.landing

import org.threeten.bp.LocalDate

data class SearchPayload(
        val arrivalDate: LocalDate,
        val departureDate: LocalDate,
        val placeName: String?,
        val latitude: Float,
        val longitude: Float,
        val adults: List<Int>,
        val children: List<Int>,
        val infants: List<Int>,
        val cots: List<Boolean>,
        val roomTypeCodes: List<String>,
        val roomsCount: Int)