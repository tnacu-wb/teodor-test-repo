package com.whitbread.premierinn.landing

import com.whitbread.premierinn.domain.dashboard.entity.FrequentBooking
import com.whitbread.premierinn.domain.recentsearch.entity.RecentSearch
import com.whitbread.premierinn.searchresults.SearchResultsInput

fun SearchPayload.toSearchResultsInput(): SearchResultsInput {
    return SearchResultsInput.builder()
            .arrivalDate(arrivalDate)
            .departureDate(departureDate)
            .placeName(placeName)
            .latitude(latitude)
            .longitude(longitude)
            .adults(adults)
            .children(children)
            .infants(infants)
            .cots(cots)
            .roomTypeCodes(roomTypeCodes)
            .numRooms(roomsCount)
            .build()
}

fun RecentSearch.toSearchPayLoad(): SearchPayload {
    return SearchPayload(
            arrivalDate = arrivalDate,
            roomTypeCodes = roomTypeCodes,
            longitude = longitude,
            latitude = latitude,
            cots = cots,
            departureDate = departureDate,
            adults = adults,
            children = children,
            infants = infants,
            roomsCount = roomsCount,
            placeName = searchTerm
    )
}

fun FrequentBooking.toParcelable() : ParcelableFrequentBooking {
    return ParcelableFrequentBooking(
            hotelImage = hotelImage,
            hotelName = hotelName,
            hotelCode = hotelCode)
}

fun ParcelableFrequentBooking.toDomain() : FrequentBooking {
    return FrequentBooking(
            hotelImage = hotelImage,
            hotelName = hotelName,
            hotelCode = hotelCode)
}