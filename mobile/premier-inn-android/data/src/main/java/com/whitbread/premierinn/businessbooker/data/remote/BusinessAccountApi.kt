package com.whitbread.premierinn.businessbooker.data.remote

import com.whitbread.premierinn.data.common.BUSINESS_BOOKING_CHANNEL
import com.whitbread.premierinn.data.common.HOTEL_BRAND
import com.whitbread.premierinn.data.remote.AccountApiContract
import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

const val AUTHORIZATION_BEARER = "Bearer"

interface BusinessAccountApi {

    @Headers("Cache-Control: no-store")
    @GET("/customers/hotels/{customer-id}")
    fun getBusinessCustomer(@Path("customer-id") customerEmail: String,
                            @Header("Authorization") bearerToken: String,
                            @Header("hotel-brand") hotelBrand: String = HOTEL_BRAND,
                            @Query("business") business : Boolean = true,
                            @Query("country") country : String,
                            @Query("language") language: String
    ): Single<AccountApiContract.CustomerResponse>

    @PUT("/customers/hotels/{customer-id}")
    fun updateCustomerPassword(@Path("customer-id") customerEmail: String,
                               @Header("Authorization") bearerToken: String,
                               @Header("X-acf-sensor-data") sensorData: String,
                               @Body customerBody: AccountApiContract.CustomerChangePasswordBody,
                               @Query ("business") business : Boolean = true): Completable

    @PUT("/customers/hotels/{customer-id}")
    fun updateCustomerBookingPreferences(@Path("customer-id") customerEmail: String,
                                         @Header("Authorization") bearerToken: String,
                                         @Body customerBody: AccountApiContract.CustomerBookingPreferencesBody,
                                         @Query("business") business : Boolean = true,
                                         @Header("bookingChannel") bookingChannel: String = BUSINESS_BOOKING_CHANNEL): Completable
}