package com.whitbread.premierinn.domain.customer.repository

import com.whitbread.premierinn.domain.authentication.repository.IdToken
import com.whitbread.premierinn.domain.common.Address
import com.whitbread.premierinn.domain.common.NewsletterPreferenceDomain
import com.whitbread.premierinn.domain.customer.entity.BookingPreferences
import com.whitbread.premierinn.domain.customer.entity.Customer
import com.whitbread.premierinn.domain.customer.entity.PaymentCard
import io.reactivex.Completable
import io.reactivex.Single

/**
 *
 */
interface CustomerRepository {
    fun createCustomer(customer: Customer, password: String, deviceLanguage: String): Completable
    fun getCustomer(idToken: String): Single<Customer>
    fun updatePassword(idToken: IdToken, username: String, currentPassword: String, newPassword: String): Completable
    fun updatePersonalDetails(idToken: IdToken, username: String, customer: Customer): Completable
    fun updatePaymentDetails(idToken: IdToken, username: String, card: PaymentCard, address: Address?): Completable
    fun updateBookingPreferences(idToken: IdToken, username: String, preferences: BookingPreferences): Completable

    fun getLoggedInCustomerEmail(): String
    fun getLoggedInCustomerPassword(): String
    fun saveLoggedInCustomerEmail(value: String)
    fun saveLoggedInCustomerPassword(value: String)
    fun saveLoggedInCustomerBookingPreferences(value: BookingPreferences?)
    fun storeBookingPrefAsHomeScreenCriteriaLeisure(value: BookingPreferences?)
    fun clearLoggedInCustomerDetails()
    fun createCustomerMarketingPrefs(idToken: IdToken, customer: Customer, marketingOptIn: Boolean, deviceLanguage: String) : Completable
    fun getMarketingPreference(email: String, idToken: IdToken): Single<NewsletterPreferenceDomain>
    fun getCustomerFromSharedPref(): Customer
    fun saveCustomerToSharedPref(customer: Customer)
    fun updateCampaignDevice(isLoggedIn: Boolean)
    fun saveContactChannelId(contactChannelId: String)
}