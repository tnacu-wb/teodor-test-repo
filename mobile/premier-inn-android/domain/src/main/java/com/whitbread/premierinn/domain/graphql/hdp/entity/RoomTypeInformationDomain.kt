package com.whitbread.premierinn.domain.graphql.hdp.entity

data class RoomTypeInfoDomain(
        val roomTypeCode: List<String>,
        val roomCategory: String,
        val roomLabel: String,
        val roomDescription: String,
        val roomImage: String)

