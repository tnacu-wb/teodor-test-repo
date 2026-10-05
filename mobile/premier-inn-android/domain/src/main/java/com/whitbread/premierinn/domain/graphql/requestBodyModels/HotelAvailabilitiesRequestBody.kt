package com.whitbread.premierinn.domain.graphql.requestBodyModels

data class HotelAvailabilitiesRequestBody(
    val place: Place,
    val startDate: String,
    val endDate: String,
    val rooms: List<Room>,
    val ratePlanCodes: List<String>?,
    val country: String,
    val language: String,
    val oldWorldChannel: String,
    val channel: String,
    val subChannel: String,
    val sort: String,
    val page: Int,
    val initialPageSize: Int,
    val lazyLoadPageSize: Int,
    val companyId: String?
)

data class Place(
    val location: String,
    val locationFormat: String,
    val radiusUnit: String,
    val radius: Int
)

data class Room(
    val type: String,
    val adultsNumber: Int,
    val childrenNumber: Int,
)


