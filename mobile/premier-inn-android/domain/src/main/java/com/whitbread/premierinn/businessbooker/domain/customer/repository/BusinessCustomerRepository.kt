package com.whitbread.premierinn.businessbooker.domain.customer.repository

import com.whitbread.premierinn.businessbooker.domain.company.Company
import com.whitbread.premierinn.domain.customer.entity.BookingPreferences
import com.whitbread.premierinn.domain.customer.entity.Customer
import com.whitbread.premierinn.domain.customer.entity.PaymentCard
import io.reactivex.Completable
import io.reactivex.Single

interface BusinessCustomerRepository {
    fun getBusinessCustomer(idToken: String): Single<Customer>
    fun getLoggedInBusinessEmail(): String
    fun getLoggedInBusinessPassword(): String
    fun saveLoggedInBusinessEmail(value: String)
    fun saveLoggedInBusinessPassword(value: String)
    fun saveLoggedInBusinessCustomerBookingPreferences(bookingPreferences: BookingPreferences?)
    fun getLoggedInBusinessCustomerBookingPreferences():  BookingPreferences
    fun storeBookingPrefAsHomeScreenCriteriaBB(value: BookingPreferences?)
    fun getOperaCompanyId(): String
    fun saveOperaCompanyId(value: String)
    fun storePersonalCard(paymentCard: PaymentCard?)
    fun getPersonalCard(): PaymentCard?
    fun storeCompany(company: Company?)
    fun getCompany(): Company?
    fun updateBookingPreferences(username: String, preferences: BookingPreferences): Completable
    fun updatePassword(token: String, emailId: String, currentPassword: String, newPassword: String): Completable
    fun clearLoggedInCustomerDetails()
}