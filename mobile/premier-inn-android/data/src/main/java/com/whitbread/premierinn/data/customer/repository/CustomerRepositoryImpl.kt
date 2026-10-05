package com.whitbread.premierinn.data.customer.repository

import com.adobe.marketing.mobile.CampaignClassic
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.whitbread.premierinn.data.common.BRAND_CODE
import com.whitbread.premierinn.data.common.HOTEL_BRAND
import com.whitbread.premierinn.data.common.onErrorThrowWhenUserAccountExists
import com.whitbread.premierinn.data.common.onErrorThrowWhenUserSessionExpired
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManager
import com.whitbread.premierinn.data.common.toApiCustomerBookingPreferenceBody
import com.whitbread.premierinn.data.common.toApiCustomerBookingPreferences
import com.whitbread.premierinn.data.common.toApiCustomerContactDetailsBody
import com.whitbread.premierinn.data.common.toApiCustomerPaymentPreference
import com.whitbread.premierinn.data.common.toApiCustomerPaymentPreferenceBody
import com.whitbread.premierinn.data.common.toApiCustomerPersonalDetailsBody
import com.whitbread.premierinn.data.common.toApiNewsletterPreferenceBody
import com.whitbread.premierinn.data.common.toDomain
import com.whitbread.premierinn.data.common.toNewsletterPreferenceDomain
import com.whitbread.premierinn.data.remote.AUTHORIZATION_BEARER
import com.whitbread.premierinn.data.remote.AccountApi
import com.whitbread.premierinn.data.remote.AccountApiContract
import com.whitbread.premierinn.data.remote.AccountApiContract.CustomerChangePasswordBody
import com.whitbread.premierinn.data.remote.DOUBLE_KEY
import com.whitbread.premierinn.domain.authentication.repository.IdToken
import com.whitbread.premierinn.domain.common.Address
import com.whitbread.premierinn.domain.common.NewsletterPreferenceDomain
import com.whitbread.premierinn.domain.common.RoomCriteria
import com.whitbread.premierinn.domain.common.RoomType
import com.whitbread.premierinn.domain.customer.entity.BookingPreferences
import com.whitbread.premierinn.domain.customer.entity.Customer
import com.whitbread.premierinn.domain.customer.entity.PaymentCard
import com.whitbread.premierinn.domain.customer.repository.CustomerRepository
import com.whitbread.premierinn.domain.home.entity.SelectedHomeScreenCriteria
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider

const val ADOBE_LOGGED_IN_KEY = "loggedIn"
class CustomerRepositoryImpl @Inject constructor(private val api: AccountApi,
                                                 private val simplePersistenceManager: SimplePersistenceManager,
                                                 @Named("AkamaiSensorData") private val sensorData: Provider<String>

) : CustomerRepository {

    override fun createCustomer(customer: Customer, password: String, deviceLanguage: String): Completable {

        fun createDefaultBookingPrefs(): AccountApiContract.BookingPreference {
            return AccountApiContract.BookingPreference(
                    mealPreference = 0,
                    roomRequirements = AccountApiContract.RoomRequirements(adults = 1, children = 0, cotRequired = false,
                            hotelBrand = HOTEL_BRAND, type = DOUBLE_KEY)
            )
        }

        return api.createCustomer(
                sensorData = sensorData.get(),
                customerBody = AccountApiContract.CreateCustomerBody(
                contactDetails = customer.toApiCustomerPersonalDetailsBody().contactDetails,
                password = password,
                defaultBookingPreference = createDefaultBookingPrefs(),
                paymentPreference = customer.toApiCustomerPaymentPreference())
        ).onErrorThrowWhenUserAccountExists()
    }

    override fun getCustomer(idToken: IdToken): Single<Customer> {
        return api.getCustomer(getLoggedInCustomerEmail(), "$AUTHORIZATION_BEARER $idToken")
                .onErrorThrowWhenUserSessionExpired()
                .map { customerResponse ->
                    val bookingPreferences = customerResponse.bookingPreference.toDomain()
                    storeBookingPrefAsHomeScreenCriteriaLeisure(bookingPreferences)
                    customerResponse.toDomain() }
                .subscribeOn(Schedulers.io())
    }

    override fun saveLoggedInCustomerEmail(value: String) {
        simplePersistenceManager.setCustomerEmail(value)
    }

    override fun saveLoggedInCustomerPassword(value: String) {
        simplePersistenceManager.setCustomerPass(value)
    }

    override fun saveLoggedInCustomerBookingPreferences(value: BookingPreferences?) {
        var bookingPreference = value
        if (bookingPreference?.roomCriteriaPreference != null) {
            if (bookingPreference.roomCriteriaPreference.includeCot) {
                bookingPreference = bookingPreference.copy(
                        roomCriteriaPreference = bookingPreference.roomCriteriaPreference.copy(
                                numberOfInfants = 1
                        )
                )
            }
        }
        simplePersistenceManager.setCustomerBookingPreferences(bookingPreference)
    }

    override fun storeBookingPrefAsHomeScreenCriteriaLeisure(value: BookingPreferences?) {
        simplePersistenceManager.storeSelectedHomeScreenCriteria(
            SelectedHomeScreenCriteria(
                roomCriteria = listOf(
                    RoomCriteria(numberOfAdults = value?.roomCriteriaPreference?.numberOfAdults ?: 1,
                        numberOfChildren = value?.roomCriteriaPreference?.numberOfChildren ?:0,
                        roomType = value?.roomCriteriaPreference?.roomType ?: RoomType.DOUBLE,
                        includeCot = false)
                ),
                searchText = null,
                latitude = null,
                longitude = null,
                hotelId = null,
                brand = null,
                arrival = null,
                departure = null
            )
        )
    }

    override fun getLoggedInCustomerEmail(): String {
        return simplePersistenceManager.getCustomerEmail()
    }

    override fun createCustomerMarketingPrefs(idToken: IdToken,
                                              customer: Customer,
                                              marketingOptIn: Boolean,
                                              deviceLanguage: String): Completable {
        return api.updateNewsletterPreferences("$AUTHORIZATION_BEARER $idToken",
                customer.contact.email,
                newsletterRequest = customer.toApiNewsletterPreferenceBody(marketingOptIn, deviceLanguage))
                .onErrorResumeNext { exception ->
                    FirebaseCrashlytics.getInstance().recordException(exception)
                    Completable.error(exception)
                }
    }

    override fun getMarketingPreference(email: String, idToken: IdToken): Single<NewsletterPreferenceDomain> {
        return api.getMarketingNewLetterPreferences("$AUTHORIZATION_BEARER $idToken", email, listOf(BRAND_CODE).joinToString())
            .map { it.toNewsletterPreferenceDomain() }
            .doOnError { exception ->
                FirebaseCrashlytics.getInstance().recordException(exception)
            }
    }

    override fun getLoggedInCustomerPassword(): String {
        return simplePersistenceManager.getCustomerPass()
    }

    override fun clearLoggedInCustomerDetails() {
        simplePersistenceManager.setCustomerEmail(null)
        simplePersistenceManager.setCustomerPass(null)
        simplePersistenceManager.setCustomerBookingPreferences(null)
    }

    override fun updatePassword(idToken: IdToken, username: String,
                                currentPassword: String, //TODO to be removed POST auth0 MIGRATION
                                newPassword: String): Completable {
        return api.updateCustomerPassword("$AUTHORIZATION_BEARER $idToken",
            sensorData.get(), username, CustomerChangePasswordBody(
                newPassword = newPassword,
                currentPassword = currentPassword,
                contactDetails = getCustomerFromSharedPref().toApiCustomerContactDetailsBody(),
                bookingPreference = getCustomerFromSharedPref().toApiCustomerBookingPreferences()))
            .onErrorThrowWhenUserSessionExpired()
    }

    override fun updatePersonalDetails(idToken: IdToken, username: String, customer: Customer): Completable {
        return api.updateCustomerPersonalDetails("$AUTHORIZATION_BEARER $idToken", sensorData.get(),
            username, customer.toApiCustomerPersonalDetailsBody())
                .onErrorThrowWhenUserSessionExpired()
    }

    override fun updatePaymentDetails(idToken: IdToken, username: String, card: PaymentCard, address: Address?): Completable {
        return api.updateCustomerPaymentDetails("$AUTHORIZATION_BEARER $idToken", sensorData.get(),
            username, card.toApiCustomerPaymentPreferenceBody(address, getCustomerFromSharedPref()))
                .onErrorThrowWhenUserSessionExpired()
    }

    override fun updateBookingPreferences(idToken: IdToken, username: String, preferences: BookingPreferences): Completable {
        return api.updateCustomerBookingPreferences("$AUTHORIZATION_BEARER $idToken",
            sensorData.get(), username, preferences.toApiCustomerBookingPreferenceBody(getCustomerFromSharedPref()))
                .onErrorThrowWhenUserSessionExpired()
    }

    override fun getCustomerFromSharedPref(): Customer {
        return simplePersistenceManager.getCustomer()
    }

    override fun saveCustomerToSharedPref(customer: Customer) {
        simplePersistenceManager.saveCustomer(customer)
    }

    override fun saveContactChannelId(contactChannelId: String) {
        simplePersistenceManager.storeContactChannelId(contactChannelId)
    }

    override fun updateCampaignDevice(isLoggedIn: Boolean) {
        val additionalParams = mapOf(ADOBE_LOGGED_IN_KEY to isLoggedIn)
        CampaignClassic.registerDevice(
            simplePersistenceManager.getFirebaseToken(),
            simplePersistenceManager.getContactChannelId(),
            additionalParams
        )
    }
}