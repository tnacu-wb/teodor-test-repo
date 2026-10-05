package com.whitbread.premierinn.data.common.persistence

import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.whitbread.premierinn.data.common.COUNTRY_GERMANY
import com.whitbread.premierinn.data.common.DEFAULT_MAX_ARRIVAL_DATE
import com.whitbread.premierinn.data.common.DEFAULT_MAX_NIGHTS_LEISURE
import com.whitbread.premierinn.data.common.DEFAULT_MAX_ROOMS_AMEND
import com.whitbread.premierinn.data.common.DEFAULT_MAX_ROOMS_LEISURE
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManagerImpl.Constants.AMENDED_RESERVATION
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManagerImpl.Constants.AMENDED_RESERVATION_SESSION
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManagerImpl.Constants.AMENDED_ROOMS_SELECTIONS
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManagerImpl.Constants.AMENDED_TEMP_BOOKING_REF
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManagerImpl.Constants.AMEND_BILLING_ADDRESS
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManagerImpl.Constants.CONTACT_CHANNEL_ID
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManagerImpl.Constants.COVID_NOTIFICATION_ID
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManagerImpl.Constants.KEY_APP_BOOKINGS_NUMBER
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManagerImpl.Constants.KEY_APP_PROMOTIONAL_INCENTIVE
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManagerImpl.Constants.KEY_APP_RATED
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManagerImpl.Constants.KEY_AUTHENTICATION_REQUIRED_PAYMENT
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManagerImpl.Constants.KEY_BOOKING_HOTEL_COUNTRY
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManagerImpl.Constants.KEY_BOOKING_PREFERENCES
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManagerImpl.Constants.KEY_BOOKING_STATUS
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManagerImpl.Constants.KEY_CHECKIN_SESSION_IDS
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManagerImpl.Constants.KEY_CUSTOMER
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManagerImpl.Constants.KEY_EMPLOYEE_OFFER_SECTION
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManagerImpl.Constants.KEY_EMPLOYEE_OFFER_TOGGLE
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManagerImpl.Constants.KEY_FIREBASE_TOKEN
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManagerImpl.Constants.KEY_FREE_BREAKFAST_PROMO_CODE
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManagerImpl.Constants.KEY_HOTEL_COUNTRY
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManagerImpl.Constants.KEY_HOTEL_PAYMENT_PROVIDER
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManagerImpl.Constants.KEY_INCENTIVE_VIEWED
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManagerImpl.Constants.KEY_LAST_REFRESHED_DASHBOARD
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManagerImpl.Constants.KEY_LIST_COUNTRIES
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManagerImpl.Constants.KEY_LOGIN_TAB_POSITION
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManagerImpl.Constants.KEY_MAX_ARRIVAL_DATE_EMPLOYEE
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManagerImpl.Constants.KEY_MAX_ARRIVAL_DATE_LEISURE
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManagerImpl.Constants.KEY_MAX_NIGHTS_AMEND_EMPLOYEE
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManagerImpl.Constants.KEY_MAX_NIGHTS_EMPLOYEE
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManagerImpl.Constants.KEY_MAX_NIGHTS_LEISURE
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManagerImpl.Constants.KEY_MAX_ROOMS_AMEND
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManagerImpl.Constants.KEY_MAX_ROOMS_AMEND_EMPLOYEE
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManagerImpl.Constants.KEY_MAX_ROOMS_EMPLOYEE
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManagerImpl.Constants.KEY_MAX_ROOMS_LEISURE
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManagerImpl.Constants.KEY_NOTIFICATION_ID
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManagerImpl.Constants.KEY_PASSWORD
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManagerImpl.Constants.KEY_PENDING_AMEND_ID
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManagerImpl.Constants.KEY_PERSONAL_CARD
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManagerImpl.Constants.KEY_PRIVACYPOLICY_ACCEPTED
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManagerImpl.Constants.KEY_REMOTE_CONFIG_STALE
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManagerImpl.Constants.KEY_RESERVATION_ID
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManagerImpl.Constants.KEY_SEEN_COVID_NOTIFICATION
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManagerImpl.Constants.KEY_SELECTED_HOME_SCREEN_CRITERIA
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManagerImpl.Constants.KEY_UPCOMING_BOOKING
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManagerImpl.Constants.KEY_USERNAME
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManagerImpl.Constants.NEWLY_ADDED_ROOM_SELECTIONS
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManagerImpl.Constants.ORIGINAL_ROOMS_SELECTIONS
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManagerImpl.Constants.REMOVE_ROOM_SELECTIONS
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManagerImpl.Constants.ROOM_CANCELLABLE
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManagerImpl.Constants.THIRTY_MINUTES
import com.whitbread.premierinn.domain.booking.entity.BookingHotelCountry
import com.whitbread.premierinn.domain.booking.entity.BookingStatus
import com.whitbread.premierinn.domain.common.EMPLOYEE_RATE_MAX_ARRIVAL_DATE
import com.whitbread.premierinn.domain.common.EMPLOYEE_RATE_MAX_NIGHT
import com.whitbread.premierinn.domain.common.EMPLOYEE_RATE_MAX_ROOM
import com.whitbread.premierinn.domain.countries.entity.CountryDomain
import com.whitbread.premierinn.domain.customer.entity.BookingPreferences
import com.whitbread.premierinn.domain.customer.entity.Customer
import com.whitbread.premierinn.domain.customer.entity.PaymentCard
import com.whitbread.premierinn.domain.graphql.hdp.entity.RoomSelectionDomain
import com.whitbread.premierinn.domain.home.entity.SelectedHomeScreenCriteria
import com.whitbread.premierinn.domain.reservation.entity.Reservation
import java.lang.reflect.Type
import java.util.Date
import javax.inject.Inject
import javax.inject.Named

class SimplePersistenceManagerImpl @Inject constructor(
        private val gson: Gson,
        private val preference: SharedPreferences,
        @Named("SharedPrefCrypto") private val sharedPrefCrypto: SharedPreferences) : SimplePersistenceManager {

    override fun isRemoteConfigStale(): Boolean = preference.getBoolean(KEY_REMOTE_CONFIG_STALE, false)

    override fun setRemoteConfigAsStale(value: Boolean) {
        preference.edit { putBoolean(KEY_REMOTE_CONFIG_STALE, value) }
    }

    override fun isAppRated(): Boolean = preference.getBoolean(KEY_APP_RATED, false)

    override fun setAppAsRated(value: Boolean) {
        preference.edit { putBoolean(KEY_APP_RATED, value) }
    }

    override fun numberOfBookingsSinceAppRating(): Int {
        return try {
            return preference.getInt(KEY_APP_BOOKINGS_NUMBER, 0)
        } catch (exception: ClassCastException) {
            preference.edit { putInt(KEY_APP_BOOKINGS_NUMBER, 1) }
            preference.getInt(KEY_APP_BOOKINGS_NUMBER, 0)
        }
    }

    override fun incrementNumberOfBookings() {
        preference.edit { putInt(KEY_APP_BOOKINGS_NUMBER, numberOfBookingsSinceAppRating() + 1) }
    }

    override fun hasAcceptedPrivacyPolicy(): Boolean {
        return preference.getBoolean(KEY_PRIVACYPOLICY_ACCEPTED, false)
    }

    override fun setHasAcceptedPrivacyPolicy() {
        preference.edit { putBoolean(KEY_PRIVACYPOLICY_ACCEPTED, true) }
    }

    override fun setCustomerEmail(value: String?) {
        preference.edit { putString(KEY_USERNAME, value) }
    }

    override fun getCustomerEmail(): String {
        return preference.getString(KEY_USERNAME, EMPTY_STRING).orEmpty()
    }

    override fun setCustomerPass(value: String?) {
        preference.edit { putString(KEY_PASSWORD, value) }
    }

    override fun getCustomerPass(): String {
        return preference.getString(KEY_PASSWORD, EMPTY_STRING).orEmpty()
    }

    override fun setCustomerBookingPreferences(value: BookingPreferences?) {
        preference.edit { putString(KEY_BOOKING_PREFERENCES,
            if (value != null) gson.toJson(value) else gson.toJson(BookingPreferences.EMPTY)) }
    }

    override fun storeBillingAddress(billingAddress: String) {
        preference.edit { putString(AMEND_BILLING_ADDRESS, billingAddress) }
    }

    override fun saveValuesForBusinessRulesLeisure(maxNights: Int, maxRooms: Int, maxRoomsAmend: Int, maxArrivalDate: Int) {
       preference.edit{
           putInt(KEY_MAX_NIGHTS_LEISURE, maxNights)
           putInt(KEY_MAX_ROOMS_LEISURE, maxRooms)
           putInt(KEY_MAX_ROOMS_AMEND, maxRoomsAmend)
           putInt(KEY_MAX_ARRIVAL_DATE_LEISURE, maxArrivalDate)
       }
    }

    override fun saveValuesForBusinessRulesEmployee(maxNights: Int, maxRooms: Int, maxRoomsAmend: Int, maxArrivalDate: Int) {
        preference.edit {
            putInt(KEY_MAX_NIGHTS_EMPLOYEE, maxNights)
            putInt(KEY_MAX_ROOMS_EMPLOYEE, maxRooms)
            putInt(KEY_MAX_ROOMS_AMEND_EMPLOYEE, maxRoomsAmend)
            putInt(KEY_MAX_ARRIVAL_DATE_EMPLOYEE, maxArrivalDate)
        }
    }

    override fun setMaxRoomLimitationForEmployee(maxRooms: Int, maxRoomsAmend: Int) {
        preference.edit{
            putInt(KEY_MAX_ROOMS_EMPLOYEE, maxRooms)
            putInt(KEY_MAX_ROOMS_AMEND_EMPLOYEE, maxRoomsAmend)
        }
    }

    override fun getMaxRoomsAmendForEmployee(): Int {
        return preference.getInt(KEY_MAX_ROOMS_AMEND_EMPLOYEE, EMPLOYEE_RATE_MAX_ROOM)
    }

    override fun getMaxNightsAmendForEmployee(): Int {
        return preference.getInt(KEY_MAX_NIGHTS_AMEND_EMPLOYEE, EMPLOYEE_RATE_MAX_NIGHT)
    }

    override fun getMaxRoomsForEmployee(): Int {
        return preference.getInt(KEY_MAX_ROOMS_EMPLOYEE, EMPLOYEE_RATE_MAX_ROOM)
    }

    override fun getMaxNightsForEmployee(): Int {
        return preference.getInt(KEY_MAX_NIGHTS_EMPLOYEE, EMPLOYEE_RATE_MAX_NIGHT)
    }

    override fun getMaxArrivalDateForEmployee(): Int {
        return preference.getInt(KEY_MAX_ARRIVAL_DATE_EMPLOYEE, EMPLOYEE_RATE_MAX_ARRIVAL_DATE)
    }

    override fun getMaxNightsLeisure(): Int {
        return preference.getInt(KEY_MAX_NIGHTS_LEISURE, DEFAULT_MAX_NIGHTS_LEISURE)
    }

    override fun getMaxRoomsLeisure(): Int {
        return preference.getInt(KEY_MAX_ROOMS_LEISURE, DEFAULT_MAX_ROOMS_LEISURE)
    }

    override fun getMaxArrivalDateLeisure(): Int {
        return preference.getInt(KEY_MAX_ARRIVAL_DATE_LEISURE, DEFAULT_MAX_ARRIVAL_DATE)
    }

    override fun getMaxRoomsAmend(): Int {
        return preference.getInt(KEY_MAX_ROOMS_AMEND, DEFAULT_MAX_ROOMS_AMEND)
    }

    override fun saveListOfCountries(value: List<CountryDomain>?) {
        preference.edit { putString(KEY_LIST_COUNTRIES, gson.toJson(value))}
    }

    override fun getListOfCountries(): List<CountryDomain>? {
        val listOfCountries = preference.getString(KEY_LIST_COUNTRIES, EMPTY_STRING)

        val type: Type = object : TypeToken<List<CountryDomain>>() {}.type
        return gson.fromJson<List<CountryDomain>>(listOfCountries, type)
    }

    override fun saveCustomer(value: Customer) {
        put(value, KEY_CUSTOMER)
    }

    override fun getCustomer(): Customer {
        val savedCustomer = preference.getString(KEY_CUSTOMER, null)
        return if (savedCustomer != null) {
            GsonBuilder().create().fromJson(savedCustomer, Customer::class.java)
        } else {
            Customer.emptyCustomer("null")
        }
    }

    override fun storePersonalCard(paymentCard: PaymentCard?) {
        val value = if (paymentCard != null) { gson.toJson(paymentCard) } else null
        preference.edit { putString(KEY_PERSONAL_CARD, value) }
    }

    override fun getPersonalCard(): PaymentCard? {
        val personalCard = preference.getString(KEY_PERSONAL_CARD, null)
        return if (personalCard != null) {
            try {
                gson.fromJson(personalCard, PaymentCard::class.java)
            } catch (e: JsonSyntaxException) {
                null
            }
        } else null
    }
    override fun storeSelectedHomeScreenCriteria(value: SelectedHomeScreenCriteria?) {
        preference.edit { putString(KEY_SELECTED_HOME_SCREEN_CRITERIA,
                if (value != null) gson.toJson(value) else gson.toJson(SelectedHomeScreenCriteria.EMPTY)) }
    }

    override fun getSelectedHomeScreenCriteria(): SelectedHomeScreenCriteria {
        val savedSelectedHomeScreenCriteria = preference.getString(KEY_SELECTED_HOME_SCREEN_CRITERIA, null)
        return if (savedSelectedHomeScreenCriteria != null) {
            try {
                gson.fromJson(savedSelectedHomeScreenCriteria, SelectedHomeScreenCriteria::class.java)
            } catch (e: JsonSyntaxException) {
                SelectedHomeScreenCriteria.EMPTY
            }
        } else SelectedHomeScreenCriteria.EMPTY
    }

    override fun clearSelectedHomeScreenCriteria() {
        preference.edit().remove(KEY_SELECTED_HOME_SCREEN_CRITERIA).apply()
    }


    override fun storeAmendedReservation(amended: Reservation?) {
        preference.edit { putString(AMENDED_RESERVATION, if (amended != null) gson.toJson(amended) else null) }
    }

    override fun getStoredAmendedReservation(): Reservation? {
        val savedBookingPreferences = preference.getString(AMENDED_RESERVATION, null)
        return if (savedBookingPreferences != null) {
            try {
                gson.fromJson(savedBookingPreferences, Reservation::class.java)
            } catch (e: JsonSyntaxException) {
                null
            }

        } else null
    }

    override fun storeCheckinSessionIds(sessionIds: MutableMap<String, String>) {
        put(sessionIds, KEY_CHECKIN_SESSION_IDS)
    }

    override fun getCheckinSessionIds(): MutableMap<String, String> {
        val value = preference.getString(KEY_CHECKIN_SESSION_IDS, null)
        return if (value != null) {
            val typeOfMap: Type = object : TypeToken<MutableMap<String, String>>() {}.type
            GsonBuilder().create().fromJson(value, typeOfMap)
        } else {
            mutableMapOf()
        }
    }

    override fun getCustomerBookingPreferences(): BookingPreferences {
        val savedBookingPreferences = preference.getString(KEY_BOOKING_PREFERENCES, null)
        return if (savedBookingPreferences != null) {
            try {
                gson.fromJson(savedBookingPreferences, BookingPreferences::class.java)
            } catch (e: JsonSyntaxException) {
                BookingPreferences.EMPTY
            }
        } else BookingPreferences.EMPTY
    }

    override fun isCovidNotificationSeen(): Boolean {
        return try {
            preference.getBoolean(
                KEY_SEEN_COVID_NOTIFICATION,
                getSeenNotification() == COVID_NOTIFICATION_ID
            )
        } catch (exception: ClassCastException) {
            preference.edit().putBoolean(KEY_SEEN_COVID_NOTIFICATION, false).apply()
            preference.getBoolean(KEY_SEEN_COVID_NOTIFICATION, false)
        }
    }

    override fun saveSeenCovidNotification() {
        preference.edit().putBoolean(KEY_SEEN_COVID_NOTIFICATION, true).commit()
    }

    override fun getBillingAddress(): String {
        return preference.getString(AMEND_BILLING_ADDRESS, EMPTY_STRING).orEmpty()
    }
    override fun storeSeenNotification(notificationID: Int) {
        preference.edit().putInt(KEY_NOTIFICATION_ID, notificationID).apply()
    }

    override fun getSeenNotification(): Int {
        return try {
            preference.getInt(KEY_NOTIFICATION_ID, 0)
        } catch (exception: ClassCastException) {
            preference.edit().putInt(KEY_NOTIFICATION_ID, 0).apply()
            preference.getInt(KEY_NOTIFICATION_ID, 0)
        }
    }

    override fun getRoomCancelable(): Boolean {
        return preference.getBoolean(ROOM_CANCELLABLE, false)
    }

    override fun storeRoomCancelable(cancellable: Boolean) {
        preference.edit().putBoolean(ROOM_CANCELLABLE, cancellable).apply()
    }

    override fun getAmendReservationID(): String {
        return preference.getString(AMENDED_RESERVATION_SESSION, EMPTY_STRING).orEmpty()
    }

    override fun storeAmendReservationID(sessionID: String?) {
        preference.edit().putString(AMENDED_RESERVATION_SESSION, sessionID).commit()
    }

    override fun getTempBookingRef(): String {
        return preference.getString(AMENDED_TEMP_BOOKING_REF, EMPTY_STRING).orEmpty()
    }

    override fun storeTempBookingRef(tempBookingRef: String?) {
        preference.edit().putString(AMENDED_TEMP_BOOKING_REF, tempBookingRef).commit()
    }

    override fun storeReservation(value: Reservation) {
        val jsonString = GsonBuilder().create().toJson(value)
        sharedPrefCrypto.edit().putString(KEY_RESERVATION_ID, jsonString).apply()
        put(value, KEY_RESERVATION_ID)
    }

    override fun getReservation(): Reservation? {
        val value = preference.getString(KEY_RESERVATION_ID, EMPTY_STRING)
        val valueFromCrypto = sharedPrefCrypto.getString(KEY_RESERVATION_ID, EMPTY_STRING)
        return if (value != null && value.isNotEmpty()) {
            try {
                GsonBuilder().create().fromJson(value, Reservation::class.java)
            } catch (exception: Exception) {
                GsonBuilder().create().fromJson(valueFromCrypto, Reservation::class.java)
            }
        } else if (valueFromCrypto != null && valueFromCrypto.isNotEmpty()){
            return GsonBuilder().create().fromJson(valueFromCrypto, Reservation::class.java)
        } else {
            null
        }
    }

    override fun clearSavedReservation() {
       preference.edit().remove(KEY_RESERVATION_ID).apply()
    }

    override fun storeOriginalRoomSelection(roomsSelections: List<RoomSelectionDomain>) {
        preference.edit { putString(ORIGINAL_ROOMS_SELECTIONS, gson.toJson(roomsSelections)) }
    }

    override fun storeCharityCode(charityCode: String) {
        TODO("Not yet implemented")
    }

    override fun getCharityCode(): String {
        TODO("Not yet implemented")
    }

    override fun getOriginalRoomSelection(): List<RoomSelectionDomain> {
        val listOfRoomsSelections = preference.getString(ORIGINAL_ROOMS_SELECTIONS, EMPTY_STRING)
        if (!listOfRoomsSelections.isNullOrEmpty()) {
            val type: Type = object : TypeToken<List<RoomSelectionDomain>>() {}.type
            return gson.fromJson(listOfRoomsSelections, type)
        }
        return emptyList()
    }

    override fun storeUpdatedRoomSelectionAfterRemoveRoom(roomsSelections: List<RoomSelectionDomain>) {
        preference.edit { putString(REMOVE_ROOM_SELECTIONS, gson.toJson(roomsSelections)) }
    }

    override fun clearSavedOriginalRoomSelection() {
        preference.edit().remove(ORIGINAL_ROOMS_SELECTIONS).apply()
    }

    override fun clearNewlyAddedRoomSelection() {
        preference.edit().remove(NEWLY_ADDED_ROOM_SELECTIONS).apply()
    }

    override fun clearRemovedRoomSelection() {
        preference.edit().remove(REMOVE_ROOM_SELECTIONS).apply()
    }
    override fun clearBillingAddress() {
        preference.edit().remove(AMEND_BILLING_ADDRESS).apply()

    }
    override fun storeAmendedRoomSelection(roomsSelections: List<RoomSelectionDomain>) {
        preference.edit { putString(AMENDED_ROOMS_SELECTIONS, gson.toJson(roomsSelections)) }
    }

    override fun getAmendedRoomSelection(): List<RoomSelectionDomain> {
        val listOfRoomsSelections = preference.getString(AMENDED_ROOMS_SELECTIONS, EMPTY_STRING)
        if (!listOfRoomsSelections.isNullOrEmpty()) {
            val type: Type = object : TypeToken<List<RoomSelectionDomain>>() {}.type
            return gson.fromJson(listOfRoomsSelections, type)
        }
        return emptyList()
    }

    override fun clearSavedAmendedRoomSelection() {
        preference.edit().remove(AMENDED_ROOMS_SELECTIONS).apply()
    }

    override fun getAuthenticationRequiredFlag(): Boolean = preference.getBoolean(KEY_AUTHENTICATION_REQUIRED_PAYMENT, false)

    override fun storeAuthenticationRequiredFlag(authenticationRequired: Boolean) {
        preference.edit { putBoolean(KEY_AUTHENTICATION_REQUIRED_PAYMENT, authenticationRequired) }
    }

    override fun getPaymentProvider(): String {
        return preference.getString(KEY_HOTEL_PAYMENT_PROVIDER, EMPTY_STRING) ?: EMPTY_STRING
    }

    override fun storePaymentProvider(paymentProvider: String) {
        preference.edit { putString(KEY_HOTEL_PAYMENT_PROVIDER, paymentProvider) }
    }

    override fun storePendingAmendId(pendingAmendId: String) {
        preference.edit().putString(KEY_PENDING_AMEND_ID, pendingAmendId).apply()
    }

    override fun getPendingAmendId(): String {
        return preference.getString(KEY_PENDING_AMEND_ID, EMPTY_STRING).orEmpty()
    }

    override fun isGermanHotel(): Boolean {
        return preference.getString(KEY_HOTEL_COUNTRY, EMPTY_STRING).equals(COUNTRY_GERMANY)
    }

    override fun storeSelectedLoginTabPos(tabPosition: Int) {
        preference.edit().putInt(KEY_LOGIN_TAB_POSITION, tabPosition).apply()
    }

    override fun getSelectedLoginTabPos(): Int {
        return preference.getInt(KEY_LOGIN_TAB_POSITION, 0);
    }

    override fun storeHotelCountry(hotelCountry: String) {
        preference.edit { putString(KEY_HOTEL_COUNTRY, hotelCountry) }
    }

    override fun setRefreshDashboard(shouldRefresh: Boolean) {
        preference.edit { putBoolean(KEY_UPCOMING_BOOKING, shouldRefresh) }
        preference.edit { putLong(KEY_LAST_REFRESHED_DASHBOARD, Date().time) }
        sharedPrefCrypto.edit { putBoolean(KEY_UPCOMING_BOOKING, shouldRefresh) }
        sharedPrefCrypto.edit { putLong(KEY_LAST_REFRESHED_DASHBOARD, Date().time) }
    }

    override fun shouldRefreshDashboard(): Boolean {
        return if (lastRefreshedDashboard() == 0L) {
            true
        } else lastRefreshedDashboard() + (THIRTY_MINUTES) < Date().time || preference.getBoolean(KEY_UPCOMING_BOOKING, false)
    }

    override fun lastRefreshedDashboard(): Long {
        return try {
            preference.getLong(KEY_LAST_REFRESHED_DASHBOARD, 0L)
        } catch (exception: Exception){
            sharedPrefCrypto.getLong(KEY_LAST_REFRESHED_DASHBOARD, 0L)
        }
    }

    override fun storeBookingStatus(bookingStatusList: List<BookingStatus>) {
        val type = object : TypeToken<List<BookingStatus>>() {}.type
        preference.edit(commit = true) {
            putString(
                KEY_BOOKING_STATUS,
                gson.toJson(bookingStatusList, type)
            )
        }
    }

    override fun storeBookingHotelCountry(bookingHotelCountry: List<BookingHotelCountry?>) {
        val type = object : TypeToken<List<BookingHotelCountry>>() {}.type
        preference.edit(commit = true) {
            putString(
                KEY_BOOKING_HOTEL_COUNTRY,
                gson.toJson(bookingHotelCountry, type)
            )
        }
    }

    override fun getBookingStatus(): List<BookingStatus> {
        val type = object : TypeToken<List<BookingStatus>>() {}.type
        val bookingStatus = preference.getString(KEY_BOOKING_STATUS, EMPTY_STRING)
        return if (!bookingStatus.isNullOrEmpty()) {
            try {
                gson.fromJson(bookingStatus, type)
            } catch (e: JsonSyntaxException) {
                emptyList()
            }
        } else {
            emptyList()
        }
    }

    override fun getBookingHotelCountry(): List<BookingHotelCountry> {
        val type = object : TypeToken<List<BookingHotelCountry>>() {}.type
        val bookingHotelCountry = preference.getString(KEY_BOOKING_HOTEL_COUNTRY, EMPTY_STRING)
        return if (!bookingHotelCountry.isNullOrEmpty()) {
            try {
                gson.fromJson(bookingHotelCountry, type)
            } catch (e: JsonSyntaxException) {
                emptyList()
            }
        } else {
            emptyList()
        }
    }

    override fun storeFirebaseToken(string: String) {
        sharedPrefCrypto.edit(commit = true) { putString(KEY_FIREBASE_TOKEN, string) }
    }

    override fun getFirebaseToken(): String =
        sharedPrefCrypto.getString(KEY_FIREBASE_TOKEN, EMPTY_STRING) ?: EMPTY_STRING


    override fun storeContactChannelId(string: String) {
        sharedPrefCrypto.edit(commit = true) { putString(CONTACT_CHANNEL_ID, string) }
    }

    override fun getContactChannelId(): String =
        sharedPrefCrypto.getString(CONTACT_CHANNEL_ID, EMPTY_STRING) ?: EMPTY_STRING

    override fun saveFlagForEmployeeOfferSection(showEmployeeOffer: Boolean) {
        preference.edit { putBoolean(KEY_EMPLOYEE_OFFER_SECTION, showEmployeeOffer) }
    }

    override fun getFlagForEmployeeOfferSection(): Boolean {
        return preference.getBoolean(KEY_EMPLOYEE_OFFER_SECTION, false)
    }

    override fun saveEmployeeOfferToggleState(enabled: Boolean) {
        preference.edit { putBoolean(KEY_EMPLOYEE_OFFER_TOGGLE, enabled) }
    }

    override fun getEmployeeOfferToggleState(): Boolean {
        return preference.getBoolean(KEY_EMPLOYEE_OFFER_TOGGLE, false)
    }

    override fun setIsAppPromotionalIncentiveAvailable(available: Boolean?) {
        preference.edit {
            if (available == null) remove(KEY_APP_PROMOTIONAL_INCENTIVE)
            else putBoolean(KEY_APP_PROMOTIONAL_INCENTIVE, available)
        }
    }

    override fun isAppPromotionalIncentiveAvailable(): Boolean? {
        return if (preference.contains(KEY_APP_PROMOTIONAL_INCENTIVE)) {
            preference.getBoolean(KEY_APP_PROMOTIONAL_INCENTIVE, false)
        } else {
            null
        }
    }

    override fun setIncentivePageViewed(enable: Boolean) {
        preference.edit {
            putBoolean(KEY_INCENTIVE_VIEWED, enable)
        }
    }

    override fun isIncentivePageViewed(): Boolean {
        return preference.getBoolean(KEY_INCENTIVE_VIEWED, false)
    }

    override fun setFreeBreakfastPromotionCode(promoCode: String) {
        preference.edit { putString(KEY_FREE_BREAKFAST_PROMO_CODE, promoCode) }
    }

    override fun getFreeBreakfastPromotionCode(): String {
        return preference.getString(KEY_FREE_BREAKFAST_PROMO_CODE, EMPTY_STRING) ?: EMPTY_STRING
    }

    fun <T> put(`object`: T, key: String) {
        val jsonString = GsonBuilder().create().toJson(`object`)
        preference.edit().putString(key, jsonString).apply()
    }

    object Constants {
        const val THIRTY_MINUTES = 30 * 60000
        const val KEY_REMOTE_CONFIG_STALE = "KEY_REMOTE_CONFIG_STALE"
        const val KEY_APP_RATED = "KEY_APP_RATED"
        const val KEY_APP_BOOKINGS_NUMBER = "KEY_APP_BOOKINGS_NUMBER"
        const val KEY_PRIVACYPOLICY_ACCEPTED = "KEY_PRIVACYPOLICY_ACCEPTED"
        const val KEY_USERNAME = "KEY_USERNAME"
        const val KEY_PASSWORD = "PASSWORD"
        const val KEY_BOOKING_PREFERENCES = "KEY_BOOKING_PREFERENCES"
        const val KEY_NOTIFICATION_ID = "KEY_NOTIFICATION_ID"
        const val ROOM_CANCELLABLE = "ROOM_CANCELLABLE"
        const val AMENDED_RESERVATION = "AMENDED_RESERVATION"
        const val KEY_RESERVATION_ID = "RESERVATION_ID"
        const val KEY_PAYMENT_DETAILS = "KEY_PAYMENT_DETAILS"
        const val AMENDED_RESERVATION_SESSION = "AMENDED_RESERVATION_SESSION"
        const val AMENDED_TEMP_BOOKING_REF = "AMENDED_TEMP_BOOKING_REF"
        const val KEY_CHECKIN_SESSION_IDS = "KEY_CHECKIN_SESSION_IDS"
        const val KEY_AUTHENTICATION_REQUIRED_PAYMENT = "KEY_AUTHENTICATION_REQUIRED_PAYMENT"
        const val KEY_HOTEL_PAYMENT_PROVIDER = "KEY_HOTEL_PAYMENT_PROVIDER"
        const val KEY_PENDING_AMEND_ID = "KEY_PENDING_AMEND_ID"
        const val KEY_HOTEL_COUNTRY = "KEY_HOTEL_COUNTRY"
        const val KEY_UPCOMING_BOOKING = "KEY_UPCOMING_BOOKING"
        const val KEY_LAST_REFRESHED_DASHBOARD = "KEY_LAST_REFRESHED_DASHBOARD"
        const val KEY_LOGIN_TAB_POSITION = "KEY_LOGIN_TAB_POSITION"
        const val KEY_SELECTED_HOME_SCREEN_CRITERIA = "KEY_SELECTED_HOME_SCREEN_CRITERIA"
        const val KEY_LIST_COUNTRIES = "KEY_LIST_COUNTRIES"
        const val KEY_CUSTOMER = "KEY_CUSTOMER"
        const val KEY_PERSONAL_CARD = "KEY_PERSONAL_CARD"

        const val KEY_MAX_NIGHTS_LEISURE = "KEY_MAX_NIGHTS_LEISURE"
        const val KEY_MAX_ROOMS_LEISURE = "KEY_MAX_ROOMS_LEISURE"
        const val KEY_MAX_ROOMS_AMEND = "KEY_MAX_ROOMS_AMEND"
        const val KEY_MAX_ARRIVAL_DATE_LEISURE = "KEY_MAX_ARRIVAL_DATE_LEISURE"
        const val KEY_MAX_ROOMS_EMPLOYEE = "KEY_MAX_ROOMS_EMPLOYEE"
        const val KEY_MAX_ROOMS_AMEND_EMPLOYEE = "KEY_MAX_ROOMS_AMEND_EMPLOYEE"
        const val KEY_MAX_NIGHTS_EMPLOYEE = "KEY_MAX_NIGHTS_EMPLOYEE"
        const val KEY_MAX_NIGHTS_AMEND_EMPLOYEE = "KEY_MAX_NIGHTS_AMEND_EMPLOYEE"
        const val KEY_MAX_ARRIVAL_DATE_EMPLOYEE = "KEY_MAX_ARRIVAL_DATE_EMPLOYEE"

        const val KEY_SEEN_COVID_NOTIFICATION = "KEY_SEEN_COVID_NOTIFICATION"

        const val COVID_NOTIFICATION_ID = 5
        const val ORIGINAL_ROOMS_SELECTIONS = "ORIGINAL_ROOMS_SELECTIONS"
        const val AMENDED_ROOMS_SELECTIONS = "AMENDED_ROOMS_SELECTIONS"
        const val NEWLY_ADDED_ROOM_SELECTIONS = "NEWLY_ADDED_ROOM_SELECTIONS"
        const val REMOVE_ROOM_SELECTIONS = "REMOVE_ROOM_SELECTIONS"
        const val AMEND_BILLING_ADDRESS = "AMEND_BILLING_ADDRESS"
        const val KEY_FIREBASE_TOKEN = "KEY_FIREBASE_TOKEN"
        const val CONTACT_CHANNEL_ID = "CONTACT_CHANNEL_ID"
        const val KEY_BOOKING_STATUS = "KEY_BOOKING_STATUS"
        const val KEY_BOOKING_HOTEL_COUNTRY = "KEY_BOOKING_HOTEL_COUNTRY"

        const val KEY_EMPLOYEE_OFFER_SECTION= "KEY_EMPLOYEE_OFFER_SECTION"
        const val KEY_EMPLOYEE_OFFER_TOGGLE= "KEY_EMPLOYEE_OFFER_TOGGLE"

        const val KEY_APP_PROMOTIONAL_INCENTIVE= "KEY_APP_PROMOTIONAL_INCENTIVE"
        const val KEY_INCENTIVE_VIEWED= "KEY_INCENTIVE_VIEWED"

        const val KEY_FREE_BREAKFAST_PROMO_CODE = "KEY_FREE_BREAKFAST_PROMO_CODE"

    }
}