package com.whitbread.premierinn.landing

import com.whitbread.premierinn.api.response.search.SearchItemInput
import com.whitbread.premierinn.common.format.DateFormat
import com.whitbread.premierinn.data.common.ErrorLogger
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.domain.common.RoomCriteria
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter


class UiMapper(private val messageProvider: MessageProvider, private val logger: ErrorLogger,
               private val deviceLocaleProvider: DeviceLocaleProvider) {

    private val dateFormatter = DateTimeFormatter.ofPattern(DateFormat.SHORT_DATE_MONTH)

    fun toUIModel(state: State): UIModel {
        return UIModel(
                location = locationText(state.location),
                dates = datesText(state.arrival, state.departure),
                rooms = roomsText(state.roomCriteria, state.companyName),
                showSubmitSpinner = showSubmitSpinner(state),
                showCovidBanner = state.covidBanner != null,
                covidBannerMessage = state.covidBanner,
                leadGuestDetails = state.leadGuestDetails,
                dashboardItem = state.dashboardItem,
                recentSearches = state.recentSearches,
                recentSearchesCleared = state.recentSearchesCleared,
                companyName = state.companyName,
                language = deviceLocaleProvider.getDeviceLanguage(),
                bottomSheetContent = state.bottomSheetContent,
                isLoading = state.findingHotelAvailability
            )
    }

    private fun locationText(location: SearchItemInput?) = location?.searchText()
            ?: messageProvider.currentLocation()

    private fun datesText(arrival: LocalDate, departure: LocalDate): String {
        return when {
            arrival == LocalDate.now() && departure == LocalDate.now().plusDays(1) ->
                messageProvider.todaysArrivalOneNight()
            arrival == LocalDate.now() && departure > LocalDate.now().plusDays(1) ->
                messageProvider.todaysArrivalMultipleNights(dateFormatter.format(departure))
            else -> messageProvider.arrivalAndDeparture(dateFormatter.format(arrival), dateFormatter.format(departure))
        }
    }

    private fun roomsText(roomCriteria: List<RoomCriteria>, companyName: String): String {
        val roomCriteriaSize = roomCriteria.size
        val roomCriteriaSizeUpd = if (companyName.isNotEmpty()) 1 else roomCriteriaSize
        val roomCriteriaUpd = if (companyName.isNotEmpty()) roomCriteria.take(1) else roomCriteria

        return try {
            val adults = roomCriteriaUpd.map { it.numberOfAdults }.sum()
            val children = roomCriteriaUpd.map { it.numberOfChildren + it.numberOfInfants }.sum()
            if (children > 0) {
                messageProvider.guestsAndRooms(adults + children, roomCriteriaSizeUpd)
            } else {
                messageProvider.adultsAndRooms(adults, roomCriteriaSizeUpd)
            }
        } catch (e: NullPointerException) {
            logger.logException(e, "Exception while mapping this roomcriteria :$roomCriteriaUpd ")
            messageProvider.adultsAndRooms(1, roomCriteriaSizeUpd)
        }
    }

    private fun showSubmitSpinner(state: State) =
            state.findingLocation || state.requestingPermission || state.findingHotelAvailability
}