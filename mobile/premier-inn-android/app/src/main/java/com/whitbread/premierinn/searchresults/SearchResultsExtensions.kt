package com.whitbread.premierinn.searchresults

import com.whitbread.premierinn.common.analytics.AnalyticsConstants
import com.whitbread.premierinn.common.analytics.FirebaseParams
import com.whitbread.premierinn.data.common.toRoomType
import com.whitbread.premierinn.domain.availability.AvailabilitySortBy
import com.whitbread.premierinn.domain.common.BookingCriteria
import com.whitbread.premierinn.domain.common.RoomCriteria
import com.whitbread.premierinn.domain.common.SUB_CHANNEL
import com.whitbread.premierinn.domain.common.latLongConcatenated
import com.whitbread.premierinn.domain.common.toListOfRoom
import com.whitbread.premierinn.domain.graphql.requestBodyModels.Channel
import com.whitbread.premierinn.domain.graphql.requestBodyModels.HotelAvailabilitiesRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.Place
import com.whitbread.premierinn.domain.graphql.srp.entity.HotelAvailabilitiesDomain
import com.whitbread.premierinn.domain.graphql.srp.entity.SingleHotelAvailabilityDomain
import com.whitbread.premierinn.domain.search.entity.Location
import com.whitbread.premierinn.domain.search.entity.SearchSuggetionItem
import com.whitbread.premierinn.searchresults.analytics.SearchResultsAnalyticsData

fun SearchResultsInput.toHotelAvailabilitiesRequest(
    sortBy: AvailabilitySortBy,
    placeId: String,
    country: String,
    language: String,
    listOfRatePlanCodes: List<String>,
    operaCompanyId: String?
): HotelAvailabilitiesRequestBody {
    val place: Place = if (placeId.isNotEmpty()) {
        Place(placeId, "PLACEID", "MILES", 30)
    } else {
        Place(latLongConcatenated(this.latitude().toString(), this.longitude().toString()),
            "LATLONG", "MILES", 30)
    }

    return HotelAvailabilitiesRequestBody(
        place = place,
        startDate = this.arrivalDate().toString(),
        endDate = this.departureDate().toString(),
        rooms = this.toListOfRoomCriteria()?.toListOfRoom() ?: emptyList(),
        ratePlanCodes = listOfRatePlanCodes,
        country = country,
        language = language,
        oldWorldChannel = SUB_CHANNEL,
        channel = if (operaCompanyId.isNullOrEmpty()) Channel.PI.name else Channel.BB.name,
        subChannel = SUB_CHANNEL, sort = sortBy.name, page = 1, initialPageSize = 40,
        lazyLoadPageSize = 40, operaCompanyId)
}

fun SearchResultsInput.toListOfRoomCriteria(): List<RoomCriteria>?{
    return this.roomTypeCodes()?.asSequence()?.withIndex()?.map {
        RoomCriteria(
            roomNumber = it.index + 1,
            numberOfAdults = this.adults()!![it.index],
            numberOfChildren = this.children()!![it.index],
            numberOfInfants = this.infants()!![it.index],
            includeCot = this.cots()!![it.index],
            roomType = this.roomTypeCodes()!![it.index].toRoomType())
    }?.toList()
}

fun SearchResultsInput.toBookingCriteria(): BookingCriteria{
    return  BookingCriteria(
        searchCriteria = SearchSuggetionItem(name = this.placeName(),
            location = Location(this.latitude().toDouble(), this.longitude().toDouble()),
            type = SearchSuggetionItem.Type.LOCATION),
        arrivalDate = this.arrivalDate()!!,
        numberOfNights = this.nights(), roomsCriteria = this.toListOfRoomCriteria()
            ?: emptyList())
}

fun createAnalyticsDataBuilderWithoutMode(searchResultsInput: SearchResultsInput,
                                          availabilityList: HotelAvailabilitiesDomain): SearchResultsAnalyticsData.Builder {

    return SearchResultsAnalyticsData.builder()
            .searchResultsOpera(availabilityList.multiHotelAvailabilities)
            .screenType(AnalyticsConstants.Type.LOOK_TO_BOOK)
            .placeName(searchResultsInput.placeName())
            .nights(searchResultsInput.nights())
            .numAdults(searchResultsInput.numAdults())
            .numRooms(searchResultsInput.numRooms())
            .arrivalDate(searchResultsInput.arrivalDate())
            .departureDate(searchResultsInput.departureDate())
            .numChildren(searchResultsInput.numChildren())
            .totalNumOfGuests(searchResultsInput.totalNumOfGuests())
            .roomTypes(searchResultsInput.roomTypeCodes())
            .searchedHotelCode(searchResultsInput.placeName().orEmpty())!!
}

fun createFirebaseData(
    searchResultsInput: SearchResultsInput,
    searchResults: List<SingleHotelAvailabilityDomain>?
): FirebaseParams {
    val firebaseParams = FirebaseParams()
    val RESULTS_TO_LOG = 12
    firebaseParams.putString(FirebaseParams.ParamName.SEARCH_TERM, searchResultsInput.placeName())
    firebaseParams.putFormattedDate(FirebaseParams.ParamName.ARRIVAL_DATE, searchResultsInput.arrivalDate())
    firebaseParams.putFormattedDate(FirebaseParams.ParamName.DEPARTURE_DATE, searchResultsInput.departureDate())
    firebaseParams.putInteger(FirebaseParams.ParamName.NUMBER_OF_NIGHTS, searchResultsInput.numChildren())
    firebaseParams.putInteger(FirebaseParams.ParamName.NUMBER_OF_ROOMS, searchResultsInput.numRooms())
    firebaseParams.putInteger(FirebaseParams.ParamName.NUMBER_OF_GUESTS, searchResultsInput.totalNumOfGuests())

    if (!searchResults.isNullOrEmpty()) {
        val hotelCodes = ArrayList<String>()
        for (searchResult in searchResults) {
            hotelCodes.add(searchResult.hotelId)
        }
        val loggableSearchResultsCount = if (searchResults.size < RESULTS_TO_LOG) searchResults.size else RESULTS_TO_LOG
        firebaseParams.putCommaSeparatedList(FirebaseParams.ParamName.HOTEL_CODE, hotelCodes.subList(0, loggableSearchResultsCount))
    }

    return firebaseParams
}

fun distance(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {

    val earthRadius = 3958.75 // in miles, change to 6371 for kilometer output

    val dLat = Math.toRadians(lat2 - lat1)
    val dLng = Math.toRadians(lng2 - lng1)

    val sindLat = Math.sin(dLat / 2)
    val sindLng = Math.sin(dLng / 2)

    val a = Math.pow(sindLat, 2.0) + (Math.pow(sindLng, 2.0)
            * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)))

    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

    return earthRadius * c // output distance, in MILES
}