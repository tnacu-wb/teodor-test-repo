package com.whitbread.premierinn.landing.model

data class CardNavigationModel(
    val trackingId: String,
    val openLinkInApp: Boolean,
    val linkPath: String,
    val latitude: String,
    val longitude: String
)
