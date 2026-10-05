package com.whitbread.premierinn.businessbooker.data.common.persistence

import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.whitbread.premierinn.businessbooker.data.common.persistence.BusinessPersistenceManagerImpl.Constants.KEY_ACCESS_LEVEL
import com.whitbread.premierinn.businessbooker.data.common.persistence.BusinessPersistenceManagerImpl.Constants.KEY_BUSINESS_ACCOUNT_CARD
import com.whitbread.premierinn.businessbooker.data.common.persistence.BusinessPersistenceManagerImpl.Constants.KEY_BUSINESS_BOOKING_PREFERENCES
import com.whitbread.premierinn.businessbooker.data.common.persistence.BusinessPersistenceManagerImpl.Constants.KEY_BUSINESS_EMAIL
import com.whitbread.premierinn.businessbooker.data.common.persistence.BusinessPersistenceManagerImpl.Constants.KEY_BUSINESS_PASSWORD
import com.whitbread.premierinn.businessbooker.data.common.persistence.BusinessPersistenceManagerImpl.Constants.KEY_BUSINESS_SESSION_ID
import com.whitbread.premierinn.businessbooker.data.common.persistence.BusinessPersistenceManagerImpl.Constants.KEY_COMPANY
import com.whitbread.premierinn.businessbooker.data.common.persistence.BusinessPersistenceManagerImpl.Constants.KEY_COMPANY_CARD_ALLOCATED
import com.whitbread.premierinn.businessbooker.data.common.persistence.BusinessPersistenceManagerImpl.Constants.KEY_COMPANY_NAME
import com.whitbread.premierinn.businessbooker.data.common.persistence.BusinessPersistenceManagerImpl.Constants.KEY_MAX_ARRIVAL_DATE_INN_BUSINESS
import com.whitbread.premierinn.businessbooker.data.common.persistence.BusinessPersistenceManagerImpl.Constants.KEY_MAX_NIGHTS_INN_BUSINESS
import com.whitbread.premierinn.businessbooker.data.common.persistence.BusinessPersistenceManagerImpl.Constants.KEY_MAX_ROOMS_INN_BUSINESS
import com.whitbread.premierinn.businessbooker.data.common.persistence.BusinessPersistenceManagerImpl.Constants.KEY_OPERA_COMPANY_ID
import com.whitbread.premierinn.businessbooker.data.common.persistence.BusinessPersistenceManagerImpl.Constants.KEY_PERSONAL_CARD_ALLOWED
import com.whitbread.premierinn.businessbooker.domain.company.Company
import com.whitbread.premierinn.businessbooker.domain.company.PaymentCard
import com.whitbread.premierinn.data.common.DEFAULT_MAX_ARRIVAL_DATE
import com.whitbread.premierinn.data.common.DEFAULT_MAX_NIGHTS_INN_BUSINESS
import com.whitbread.premierinn.data.common.DEFAULT_MAX_ROOMS_INN_BUSINESS
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.data.common.toAccessLevelEnum
import com.whitbread.premierinn.domain.customer.entity.AccessLevel
import com.whitbread.premierinn.domain.customer.entity.BookingPreferences
import javax.inject.Inject

class BusinessPersistenceManagerImpl @Inject constructor(
        private val gson: Gson,
        private val preference: SharedPreferences
) : BusinessPersistenceManager {

    override fun setBusinessCustomerEmail(value: String?) {
        preference.edit { putString(KEY_BUSINESS_EMAIL, value) }
    }

    override fun getBusinessCustomerEmail(): String {
        return preference.getString(KEY_BUSINESS_EMAIL, EMPTY_STRING).orEmpty()
    }

    override fun setBusinessCustomerPass(value: String?) {
        preference.edit { putString(KEY_BUSINESS_PASSWORD, value) }
    }

    override fun getBusinessCustomerPass(): String {
        return preference.getString(KEY_BUSINESS_PASSWORD, EMPTY_STRING).orEmpty()
    }

    override fun storeBusinessSessionId(sessionId: String) { // Might Be needed later
        preference.edit { putString(KEY_BUSINESS_SESSION_ID, sessionId) }
    }

    override fun clearBusinessSessionId() {
        preference.edit { remove(KEY_BUSINESS_SESSION_ID) }
    }

    override fun saveValuesForBusinessRulesInnBusiness(
        maxNights: Int,
        maxRooms: Int,
        maxArrivalDate: Int
    ) {
        preference.edit{
            putInt(KEY_MAX_NIGHTS_INN_BUSINESS, maxNights)
            putInt(KEY_MAX_ROOMS_INN_BUSINESS, maxRooms)
            putInt(KEY_MAX_ARRIVAL_DATE_INN_BUSINESS, maxArrivalDate)
        }
    }

    override fun clearValuesForBusinessRulesInnBusiness() {
        preference.edit {
            remove(KEY_MAX_NIGHTS_INN_BUSINESS)
            remove(KEY_MAX_ROOMS_INN_BUSINESS)
            remove(KEY_MAX_ARRIVAL_DATE_INN_BUSINESS)
        }
    }

    override fun getMaxNightsInnBusiness(): Int {
        return preference.getInt(KEY_MAX_NIGHTS_INN_BUSINESS, DEFAULT_MAX_NIGHTS_INN_BUSINESS)
    }

    override fun getMaxRoomsInnBusiness(): Int {
        return preference.getInt(KEY_MAX_ROOMS_INN_BUSINESS, DEFAULT_MAX_ROOMS_INN_BUSINESS)
    }

    override fun getMaxArrivalDateInnBusiness(): Int {
        return preference.getInt(KEY_MAX_ARRIVAL_DATE_INN_BUSINESS, DEFAULT_MAX_ARRIVAL_DATE)
    }

    override fun retrieveBusinessSessionId(): String {
        return preference.getString(KEY_BUSINESS_SESSION_ID, EMPTY_STRING).orEmpty()
    }

    override fun setCustomerBookingPreferences(bookingPreferences: BookingPreferences?) {
        val value = if (bookingPreferences != null) { gson.toJson(bookingPreferences) } else null
        preference.edit { putString(KEY_BUSINESS_BOOKING_PREFERENCES, value) }
    }

    override fun getCustomerBookingPreferences(): BookingPreferences {
        val savedBookingPreferences = preference.getString(KEY_BUSINESS_BOOKING_PREFERENCES, null)
        return if (savedBookingPreferences != null) {
            try {
                gson.fromJson(savedBookingPreferences, BookingPreferences::class.java)
            } catch (e: JsonSyntaxException) {
                BookingPreferences.EMPTY
            }

        } else BookingPreferences.EMPTY
    }

    override fun setCompanyName(companyName: String) {
        preference.edit { putString(KEY_COMPANY_NAME, companyName) }
    }

    override fun getCompanyName(): String {
        return preference.getString(KEY_COMPANY_NAME, EMPTY_STRING).orEmpty()
    }

    override fun storeBusinessAccountCard(paymentCard: PaymentCard?) {
        val value = if (paymentCard != null) { gson.toJson(paymentCard) } else null
        preference.edit { putString(KEY_BUSINESS_ACCOUNT_CARD, value) }
    }

    override fun getBusinessAccountCard(): PaymentCard? {
        val businessAccountCard = preference.getString(KEY_BUSINESS_ACCOUNT_CARD, null)
        return if (businessAccountCard != null) {
            try {
                gson.fromJson(businessAccountCard, PaymentCard::class.java)
            } catch (e: JsonSyntaxException) {
                null
            }
        } else null
    }

    override fun storeCustomerAccessLevel(accessLevel: AccessLevel) {
        preference.edit { putString(KEY_ACCESS_LEVEL, accessLevel.name) }
    }

    override fun getCustomerAccessLevel(): AccessLevel {
        return preference.getString(KEY_ACCESS_LEVEL, EMPTY_STRING).orEmpty().toAccessLevelEnum()
    }

    override fun clearCustomerAccessLevel() {
        preference.edit { remove(KEY_ACCESS_LEVEL) }
    }

    override fun companyCardAllocated(allocated: Boolean) {
        preference.edit { putBoolean(KEY_COMPANY_CARD_ALLOCATED, allocated) }
    }

    override fun isCompanyCardAllocated(): Boolean {
        return preference.getBoolean(KEY_COMPANY_CARD_ALLOCATED, false)
    }

    override fun clearCompanyCardAllocated() {
        preference.edit { remove(KEY_COMPANY_CARD_ALLOCATED) }
    }

    override fun personalCardAllowed(allowed: Boolean) {
        preference.edit { putBoolean(KEY_PERSONAL_CARD_ALLOWED, allowed) }
    }

    override fun isPersonalCardAllowed(): Boolean {
        return preference.getBoolean(KEY_PERSONAL_CARD_ALLOWED, false)
    }

    override fun clearPersonalCardAllowed() {
        preference.edit { remove(KEY_PERSONAL_CARD_ALLOWED) }
    }

    override fun storeOperaCompanyId(value: String?) {
        preference.edit { putString(KEY_OPERA_COMPANY_ID, value) }
    }

    override fun getOperaCompanyId(): String {
        return preference.getString(KEY_OPERA_COMPANY_ID, EMPTY_STRING).orEmpty()
    }

    override fun storeCompany(company: Company?) {
        val value = company?.let { gson.toJson(it) }
        preference.edit { putString(KEY_COMPANY, value) }
    }

    override fun getCompany(): Company? {
        val companyJson = preference.getString(KEY_COMPANY, null)
        return companyJson?.let {
            gson.fromJson(it, Company::class.java)
        }
    }

    object Constants {
        const val KEY_BUSINESS_SESSION_ID = "KEY_HOTEL_COUNTRY"
        const val KEY_BUSINESS_EMAIL = "KEY_BUSINESS_EMAIL"
        const val KEY_BUSINESS_PASSWORD = "KEY_BUSINESS_PASSWORD"
        const val KEY_BUSINESS_BOOKING_PREFERENCES = "KEY_BUSINESS_BOOKING_PREFERENCES"
        const val KEY_COMPANY_NAME = "KEY_COMPANY_NAME"
        const val KEY_BUSINESS_ACCOUNT_CARD = "KEY_BUSINESS_ACCOUNT_CARD"
        const val KEY_ACCESS_LEVEL = "KEY_ACCESS_LEVEL"
        const val KEY_COMPANY_CARD_ALLOCATED = "KEY_COMPANY_CARD_ALLOCATED"
        const val KEY_PERSONAL_CARD_ALLOWED = "KEY_PERSONAL_CARD_ALLOWED"

        const val KEY_MAX_NIGHTS_INN_BUSINESS = "KEY_MAX_NIGHTS_INN_BUSINESS"
        const val KEY_MAX_ROOMS_INN_BUSINESS = "KEY_MAX_ROOMS_INN_BUSINESS"
        const val KEY_MAX_ARRIVAL_DATE_INN_BUSINESS= "KEY_MAX_ARRIVAL_DATE_INN_BUSINESS"
        const val KEY_OPERA_COMPANY_ID = "KEY_OPERA_COMPANY_ID"
        const val KEY_COMPANY = "KEY_COMPANY"
    }
}
