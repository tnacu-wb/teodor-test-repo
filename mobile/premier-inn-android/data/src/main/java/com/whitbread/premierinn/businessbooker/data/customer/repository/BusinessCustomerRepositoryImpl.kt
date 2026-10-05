package com.whitbread.premierinn.businessbooker.data.customer.repository

import com.whitbread.premierinn.businessbooker.data.common.persistence.BusinessPersistenceManager
import com.whitbread.premierinn.businessbooker.data.remote.BusinessAccountApi
import com.whitbread.premierinn.businessbooker.domain.company.Company
import com.whitbread.premierinn.businessbooker.domain.customer.repository.BusinessCustomerRepository
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.data.common.onErrorThrowWhenUserSessionExpired
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManager
import com.whitbread.premierinn.data.common.toApiCustomerBookingPreferenceBody
import com.whitbread.premierinn.data.common.toApiCustomerBookingPreferences
import com.whitbread.premierinn.data.common.toApiCustomerContactDetailsBody
import com.whitbread.premierinn.data.common.toDomain
import com.whitbread.premierinn.data.remote.AUTHORIZATION_BEARER
import com.whitbread.premierinn.data.remote.AccountApiContract
import com.whitbread.premierinn.domain.authentication.repository.IdToken
import com.whitbread.premierinn.domain.common.GBP
import com.whitbread.premierinn.domain.common.RoomCriteria
import com.whitbread.premierinn.domain.common.RoomType
import com.whitbread.premierinn.domain.customer.entity.BookingPreferences
import com.whitbread.premierinn.domain.customer.entity.Customer
import com.whitbread.premierinn.domain.customer.entity.PaymentCard
import com.whitbread.premierinn.domain.home.entity.SelectedHomeScreenCriteria
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider

class BusinessCustomerRepositoryImpl @Inject constructor(
    private val businessAccountApi: BusinessAccountApi,
    private val businessPersistenceManager: BusinessPersistenceManager,
    private val simplePersistenceManager: SimplePersistenceManager,
    private val deviceLocaleProvider: DeviceLocaleProvider,
    @Named("AkamaiSensorData") private val sensorData: Provider<String>
) : BusinessCustomerRepository {

    override fun getBusinessCustomer(idToken: IdToken): Single<Customer> {
        val deviceLocale = deviceLocaleProvider.getDeviceLocale()
        val country = deviceLocaleProvider.getCountryIfRegion(deviceLocale).lowercase()
        val language = deviceLocale.language
        return businessAccountApi.getBusinessCustomer(
            getLoggedInBusinessEmail(),
            "$AUTHORIZATION_BEARER $idToken",
            country = country, language = language)
                .onErrorThrowWhenUserSessionExpired()
                .map { businessCustomerResponse ->
                    val bookingPreferences = businessCustomerResponse.bookingPreference.toDomain()
                    storeBookingPrefAsHomeScreenCriteriaBB(bookingPreferences)
                    businessCustomerResponse.toDomain()
                }
                .doOnSuccess { if (it.business != null) {
                    businessPersistenceManager.storeCustomerAccessLevel(it.business!!.accessLevel)
                }}
                .subscribeOn(Schedulers.io())
    }

    override fun getLoggedInBusinessEmail(): String {
        return businessPersistenceManager.getBusinessCustomerEmail()
    }

    override fun getLoggedInBusinessPassword(): String {
        return businessPersistenceManager.getBusinessCustomerPass()
    }

    override fun saveLoggedInBusinessEmail(value: String) {
        businessPersistenceManager.setBusinessCustomerEmail(value)
    }

    override fun saveLoggedInBusinessPassword(value: String) {
        businessPersistenceManager.setBusinessCustomerPass(value)
    }

    override fun saveLoggedInBusinessCustomerBookingPreferences(bookingPreferences: BookingPreferences?) {
        businessPersistenceManager.setCustomerBookingPreferences(bookingPreferences)
    }

    override fun getLoggedInBusinessCustomerBookingPreferences(): BookingPreferences {
        return businessPersistenceManager.getCustomerBookingPreferences()
    }

    override fun storeBookingPrefAsHomeScreenCriteriaBB(value: BookingPreferences?) {
        simplePersistenceManager.storeSelectedHomeScreenCriteria(
            SelectedHomeScreenCriteria(
                roomCriteria = listOf(
               RoomCriteria(numberOfAdults = value?.roomCriteriaPreference?.numberOfAdults ?: 1,
                numberOfChildren = value?.roomCriteriaPreference?.numberOfChildren ?:0,
                roomType = value?.roomCriteriaPreference?.roomType ?: RoomType.DOUBLE,
                   includeCot = false)),
                searchText = null,
                latitude = null,
                longitude = null,
                hotelId = null,
                brand = null,
                arrival = null,
                departure = null
            ))
    }

    override fun updateBookingPreferences(username: String, preferences: BookingPreferences): Completable {
        return businessAccountApi.updateCustomerBookingPreferences(username,
                businessPersistenceManager.retrieveBusinessSessionId(),
                preferences.toApiCustomerBookingPreferenceBody((Customer.emptyCustomer(GBP))))
                .onErrorThrowWhenUserSessionExpired()
    }

    override fun getOperaCompanyId(): String {
        return businessPersistenceManager.getOperaCompanyId()
    }

    override fun saveOperaCompanyId(value: String) {
        businessPersistenceManager.storeOperaCompanyId(value)
    }

    override fun storePersonalCard(paymentCard: PaymentCard?) {
        simplePersistenceManager.storePersonalCard(paymentCard)
    }

    override fun getPersonalCard(): PaymentCard? {
       return simplePersistenceManager.getPersonalCard()
    }

    override fun getCompany(): Company? {
        return businessPersistenceManager.getCompany()
    }

    override fun storeCompany(company: Company?) {
        businessPersistenceManager.storeCompany(company)
    }

    override fun updatePassword(token: String,
                                emailId: String,
                                currentPassword: String,
                                newPassword: String): Completable {
        return businessAccountApi.updateCustomerPassword(
            emailId,
            "$AUTHORIZATION_BEARER $token",
            sensorData.get(),
            AccountApiContract.CustomerChangePasswordBody(
                newPassword = newPassword,
                currentPassword = currentPassword,
                companyId = simplePersistenceManager.getCustomer().companyId,
                contactDetails = simplePersistenceManager.getCustomer()
                    .toApiCustomerContactDetailsBody(),
                bookingPreference = simplePersistenceManager.getCustomer()
                    .toApiCustomerBookingPreferences()
            )
        )
            .onErrorThrowWhenUserSessionExpired()
    }

    override fun clearLoggedInCustomerDetails() {
        businessPersistenceManager.setBusinessCustomerEmail(null)
        businessPersistenceManager.setBusinessCustomerPass(null)
        businessPersistenceManager.setCustomerBookingPreferences(null)
        businessPersistenceManager.setCompanyName(EMPTY_STRING)
        businessPersistenceManager.storeOperaCompanyId(null)
        businessPersistenceManager.storeCompany(null)
        businessPersistenceManager.storeBusinessAccountCard(null)
        businessPersistenceManager.clearCustomerAccessLevel()
        businessPersistenceManager.clearBusinessSessionId()
        businessPersistenceManager.clearValuesForBusinessRulesInnBusiness()
        businessPersistenceManager.clearCompanyCardAllocated()
        businessPersistenceManager.clearPersonalCardAllowed()
        simplePersistenceManager.storePersonalCard(null)
    }
}