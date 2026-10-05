package com.whitbread.premierinn.data.common.persistence

import com.whitbread.premierinn.domain.booking.entity.BookingHotelCountry
import com.whitbread.premierinn.domain.booking.entity.BookingStatus
import com.whitbread.premierinn.domain.countries.entity.CountryDomain
import com.whitbread.premierinn.domain.customer.entity.BookingPreferences
import com.whitbread.premierinn.domain.customer.entity.Customer
import com.whitbread.premierinn.domain.customer.entity.PaymentCard
import com.whitbread.premierinn.domain.graphql.hdp.entity.RoomSelectionDomain
import com.whitbread.premierinn.domain.home.entity.SelectedHomeScreenCriteria
import com.whitbread.premierinn.domain.reservation.entity.Reservation

// Ideally this needs to be broken down to smaller interfaces that represent a feature respectively
interface SimplePersistenceManager {

    fun setRemoteConfigAsStale(value: Boolean)
    fun isRemoteConfigStale(): Boolean
    fun isAppRated(): Boolean
    fun setAppAsRated(value: Boolean)
    fun numberOfBookingsSinceAppRating(): Int
    fun incrementNumberOfBookings()
    fun hasAcceptedPrivacyPolicy(): Boolean
    fun setHasAcceptedPrivacyPolicy()

    fun setCustomerEmail(value: String?)
    fun setCustomerPass(value: String?)
    fun setCustomerBookingPreferences(value: BookingPreferences?)
    fun storeBillingAddress(billingAddress: String)

    fun saveValuesForBusinessRulesLeisure(maxNights: Int, maxRooms: Int, maxRoomsAmend: Int, maxArrivalDate: Int)
    fun saveValuesForBusinessRulesEmployee(maxNights: Int, maxRooms: Int, maxRoomsAmend: Int, maxArrivalDate: Int)
    fun setMaxRoomLimitationForEmployee(maxRooms: Int, maxRoomsAmend: Int)
    fun getMaxRoomsAmendForEmployee(): Int
    fun getMaxNightsAmendForEmployee(): Int
    fun getMaxRoomsForEmployee(): Int
    fun getMaxNightsForEmployee(): Int
    fun getMaxArrivalDateForEmployee(): Int
    fun getMaxRoomsAmend(): Int
    fun getMaxRoomsLeisure(): Int
    fun getMaxNightsLeisure(): Int
    fun getMaxArrivalDateLeisure(): Int

    fun saveListOfCountries(value: List<CountryDomain>?)
    fun getListOfCountries(): List<CountryDomain>?

    fun saveCustomer(value: Customer)
    fun getCustomer(): Customer

    fun storeSelectedHomeScreenCriteria(value: SelectedHomeScreenCriteria?)
    fun getSelectedHomeScreenCriteria(): SelectedHomeScreenCriteria
    fun clearSelectedHomeScreenCriteria()

    fun storePersonalCard(paymentCard: PaymentCard?)
    fun getPersonalCard(): PaymentCard?

    fun getCustomerEmail(): String
    fun getCustomerPass(): String
    fun getCustomerBookingPreferences(): BookingPreferences
    fun isCovidNotificationSeen(): Boolean
    fun saveSeenCovidNotification()
    fun getBillingAddress(): String
    fun storeSeenNotification(notificationID: Int)
    fun getSeenNotification(): Int
    fun storeRoomCancelable(cancellable: Boolean)
    fun getRoomCancelable(): Boolean
    fun storeAmendReservationID(sessionID: String?)
    fun getAmendReservationID(): String

    fun storeTempBookingRef(tempBookingRef: String?)
    fun getTempBookingRef(): String

    fun storeAmendedReservation(amended: Reservation?)
    fun getStoredAmendedReservation(): Reservation?

    fun storeCheckinSessionIds(sessionIds: MutableMap<String, String>)
    fun getCheckinSessionIds(): MutableMap<String, String>

    fun storeReservation(value: Reservation)
    fun getReservation(): Reservation?
    fun clearSavedReservation()

    fun storeCharityCode(charityCode: String)
    fun getCharityCode(): String

    fun storeOriginalRoomSelection(roomsSelections: List<RoomSelectionDomain>)
    fun getOriginalRoomSelection(): List<RoomSelectionDomain>
    fun storeUpdatedRoomSelectionAfterRemoveRoom(roomsSelections: List<RoomSelectionDomain>)
    fun clearSavedOriginalRoomSelection()
    fun clearNewlyAddedRoomSelection()
    fun clearRemovedRoomSelection()
    fun clearBillingAddress()

    fun storeAmendedRoomSelection(roomsSelections: List<RoomSelectionDomain>)
    fun getAmendedRoomSelection(): List<RoomSelectionDomain>
    fun clearSavedAmendedRoomSelection()

    fun getAuthenticationRequiredFlag() : Boolean
    fun storeAuthenticationRequiredFlag(authenticationRequired: Boolean)

    fun getPaymentProvider(): String
    fun storePaymentProvider(paymentProvider: String)

    fun storePendingAmendId(pendingAmendId: String)
    fun getPendingAmendId(): String

    fun storeHotelCountry(hotelCountry: String)
    fun isGermanHotel(): Boolean

    fun lastRefreshedDashboard() : Long
    fun setRefreshDashboard(shouldRefresh: Boolean)
    fun shouldRefreshDashboard() : Boolean

    fun storeSelectedLoginTabPos(tabPosition: Int)
    fun getSelectedLoginTabPos(): Int
    fun storeBookingStatus(bookingStatusList: List<BookingStatus>)
    fun storeBookingHotelCountry(bookingHotelCountry: List<BookingHotelCountry?>)
    fun getBookingStatus(): List<BookingStatus>
    fun getBookingHotelCountry(): List<BookingHotelCountry>
    fun storeFirebaseToken(string: String)
    fun getFirebaseToken(): String

    fun storeContactChannelId(string: String)
    fun getContactChannelId(): String

    fun saveFlagForEmployeeOfferSection(showEmployeeOffer: Boolean)
    fun getFlagForEmployeeOfferSection(): Boolean

    fun saveEmployeeOfferToggleState(enabled: Boolean)
    fun getEmployeeOfferToggleState(): Boolean

    fun setIsAppPromotionalIncentiveAvailable(available: Boolean?)
    fun isAppPromotionalIncentiveAvailable(): Boolean?

    fun setIncentivePageViewed(enable: Boolean)
    fun isIncentivePageViewed(): Boolean

    fun setFreeBreakfastPromotionCode(promoCode: String)
    fun getFreeBreakfastPromotionCode(): String

}