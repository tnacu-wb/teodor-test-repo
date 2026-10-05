package com.whitbread.premierinn.domain.graphql.requestBodyModels

data class RoomOccupancyAmend(
    val adultsNumber: Int,
    val childrenNumber: Int,
    val cotRequired: Boolean
)