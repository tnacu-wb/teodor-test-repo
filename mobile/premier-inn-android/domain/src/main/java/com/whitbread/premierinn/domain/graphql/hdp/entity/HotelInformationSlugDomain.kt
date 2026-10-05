package com.whitbread.premierinn.domain.graphql.hdp.entity

import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN

data class HotelInformationSlugDomain(
    val brand: String,
    val name: String,
    val hotelId: String
) {
    companion object {
        fun createEmptyDomain() = HotelInformationSlugDomain(
            brand = EMPTY_STRING_DOMAIN,
            name = EMPTY_STRING_DOMAIN,
            hotelId = EMPTY_STRING_DOMAIN
        )
    }
}