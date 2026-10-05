package com.whitbread.premierinn.data.remote

import com.whitbread.premierinn.data.common.HOTEL_BRAND
import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.*

// See https://app.swaggerhub.com/apis/whitbread/Account-API
const val AUTHORIZATION_BEARER = "Bearer"

interface AccountApi {

    //See https://app.swaggerhub.com/apis/whitbread/Register-API/1.4
    @Headers("Cache-Control: no-store")
    @POST("/customers/hotels")
    fun createCustomer(
        @Header("X-acf-sensor-data") sensorData: String,
        @Body customerBody: AccountApiContract.CreateCustomerBody): Completable

    @Headers("Cache-Control: no-store")
    @GET("/customers/hotels/{customer-id}")
    fun getCustomer(@Path("customer-id") customerEmail: String,
                    @Header("Authorization") bearerToken: String,
                    @Header("hotel-brand") hotelBrand: String = HOTEL_BRAND
    ): Single<AccountApiContract.CustomerResponse>

    @Headers("Cache-Control: no-store")
    @GET("/marketing/newsletter/email/{email}")
    fun getMarketingNewLetterPreferences(@Header("Authentication") bearerToken: String,
                                         @Path("email") email: String,
                                         @Query("brandCodes") brandCodes: String
    ): Single<AccountApiContract.NewsletterPreferenceResponse>

    @PUT("/customers/hotels/{customer-id}")
    fun updateCustomerPassword(@Header("Authorization") bearerToken: String,
                               @Header("X-acf-sensor-data") sensorData: String,
                               @Path("customer-id") customerEmail: String,
                               @Body customerBody: AccountApiContract.CustomerChangePasswordBody): Completable

    @PUT("/customers/hotels/{customer-id}")
    fun updateCustomerPersonalDetails(@Header("Authorization") bearerToken: String,
                                      @Header("X-acf-sensor-data") sensorData: String,
                                      @Path("customer-id") customerEmail: String,
                                      @Body customerBody: AccountApiContract.CustomerPersonalDetailsBody): Completable

    @PUT("/customers/hotels/{customer-id}")
    fun updateCustomerBookingPreferences(@Header("Authorization") bearerToken: String,
                                         @Header("X-acf-sensor-data") sensorData: String,
                                         @Path("customer-id") customerEmail: String,
                                         @Body customerBody: AccountApiContract.CustomerBookingPreferencesBody): Completable

    @PUT("/customers/hotels/{customer-id}")
    fun updateCustomerPaymentDetails(@Header("Authorization") bearerToken: String,
                                     @Header("X-acf-sensor-data") sensorData: String,
                                     @Path("customer-id") customerEmail: String,
                                     @Body customerBody: AccountApiContract.CustomerPaymentPreferencesBody): Completable


    @PUT("/marketing/newsletter/email/{email}")
    fun updateNewsletterPreferences(@Header("Authorization") authorization: String,
                                    @Path("email") email: String,
                                    @Body newsletterRequest: AccountApiContract.NewsletterPreferenceEditBody): Completable

}