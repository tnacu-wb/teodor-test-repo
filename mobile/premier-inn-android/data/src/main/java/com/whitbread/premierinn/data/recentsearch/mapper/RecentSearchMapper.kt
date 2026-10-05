package com.whitbread.premierinn.data.recentsearch.mapper

import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.data.recentsearch.entity.RecentSearchEntity
import com.whitbread.premierinn.domain.recentsearch.entity.RecentSearch

fun RecentSearchEntity.toDomain(): RecentSearch {
    return RecentSearch(
            searchTerm = searchTerm,
            hotelCode = hotelCode,
            latitude = latitude,
            longitude = longitude,
            hotelBrand = hotelBrand,
            arrivalDate = arrivalDate,
            departureDate = departureDate,
            roomsCount = roomsCount,
            adults = adults,
            children = children,
            infants = infants,
            cots = cots,
            roomTypeCodes = roomTypeCodes)
}

fun RecentSearch.toEntity(): RecentSearchEntity {
    return RecentSearchEntity(
            searchTerm = searchTerm,
            hotelCode = hotelCode ?: EMPTY_STRING,
            latitude = latitude,
            longitude = longitude,
            hotelBrand = hotelBrand ?: EMPTY_STRING,
            arrivalDate = arrivalDate,
            departureDate = departureDate,
            roomsCount = roomsCount,
            adults = adults,
            children = children,
            infants = infants,
            cots = cots,
            roomTypeCodes = roomTypeCodes,
            dateCreated = 0L
    )
}