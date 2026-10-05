package com.whitbread.premierinn.domain.graphql.requestBodyModels

data class UpdateReservationPreferencesRequestBody (
    val hotelId: String,
    val reservationsIds: List<String>,
    val preferencesCollections: List<UpdateHotelPreference>
)

data class UpdateHotelPreference(
    val preferenceType: String,
    val preferences: List<String>
)
