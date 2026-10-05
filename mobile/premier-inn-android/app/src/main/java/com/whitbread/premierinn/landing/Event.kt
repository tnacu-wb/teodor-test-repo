package com.whitbread.premierinn.landing

import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import com.whitbread.premierinn.domain.common.OperaFallbackPopupInfoDomain
import com.whitbread.premierinn.domain.common.RoomCriteria
import com.whitbread.premierinn.domain.common.PromoContentDomain
import com.whitbread.premierinn.searchresults.SearchResultsInput
import org.threeten.bp.LocalDate

sealed class Event
object OpenSearch : Event()
data class OpenCalendar(val dates: Pair<LocalDate, LocalDate>?, val maxNights: Int, val maxArrivalDate: Int) : Event()
data class OpenRoomCriteria(val roomCriteria: List<RoomCriteria>, val isBusinessUser: Boolean) : Event()
data class OpenSearchResults(val searchResultsInput: SearchResultsInput, val placeId: String? = EMPTY_STRING_DOMAIN,
                             val country: String, val language: String) : Event()
data class OpenHotelAlternatives(val searchPayload: SearchPayload, val hotelCode: String, val placeId: String? = EMPTY_STRING_DOMAIN,
                                 val country: String, val language: String) : Event()
data class OpenHotelDetails(val searchPayload: SearchPayload, val hotelCode: String, val brand: String) : Event()
object OpenHotelInWebView: Event()
class OpenFallbackPopup(val fallbackPopupInfo: OperaFallbackPopupInfoDomain) : Event()
object RequestLocationPermission : Event()
object BusinessBookerRecentSearchError : Event()
data class MaxRoomsError(val message: String, val phoneNumber: String, val isGroupFormRequired: Boolean) : Event()
data class MaxNightsError(val nights: Int) : Event()
data class OpenCardDestination(val link: String, val shouldOpenInApp: Boolean) : Event()
data class OpenNotificationLinkDestination(val link: String, val shouldOpenInApp: Boolean) : Event()
data class OpenIncentiveScreen(val promoContent: PromoContentDomain, val promoCode: String) : Event()
object BusinessLoginCustomerOrCompanyError : Event()
