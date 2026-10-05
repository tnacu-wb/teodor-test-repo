package com.whitbread.premierinn.data.remote.graphql

import com.whitbread.premierinn.data.remote.graphql.contracts.AttachFileToReservationGraphQLContract
import com.whitbread.premierinn.data.remote.graphql.contracts.AuthorizeCardGraphQLContract
import com.whitbread.premierinn.data.remote.graphql.contracts.BookingConfirmationGraphQLContract
import com.whitbread.premierinn.data.remote.graphql.contracts.ConfirmPreCheckOutGraphQLContract
import com.whitbread.premierinn.data.remote.graphql.contracts.CreateReservationGraphQLContract
import com.whitbread.premierinn.data.remote.graphql.contracts.CreateReservationGuestGraphQLContract
import com.whitbread.premierinn.data.remote.graphql.contracts.HotelPreferencesGraphQLContract
import com.whitbread.premierinn.data.remote.graphql.contracts.PackagesAndAncillariesCloseoutGraphQLContract
import com.whitbread.premierinn.data.remote.graphql.contracts.PackagesGraphQLContract
import com.whitbread.premierinn.data.remote.graphql.contracts.PaymentMethodsGraphQLContract
import com.whitbread.premierinn.data.remote.graphql.contracts.PreCheckInConfirmationGraphQLContract
import com.whitbread.premierinn.data.remote.graphql.contracts.PreCheckInGraphQLContract
import com.whitbread.premierinn.data.remote.graphql.contracts.SaveReservationWithAncillariesGraphQLContract
import com.whitbread.premierinn.data.remote.graphql.contracts.UpdateReservationGraphQLContract
import com.whitbread.premierinn.data.remote.graphql.contracts.UpdateReservationPackagesByReservationGraphQLContract
import com.whitbread.premierinn.data.remote.graphql.contracts.UpdateReservationPreferencesGraphQLContract
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface NonRxGraphQLServicesApi {

    @Headers("Content-Type: application/json")
    @POST("/graphql")
    suspend fun createReservationGraphQL(
        @Header("Authorization") bearerToken: String?,
        @Body query: String
    ): CreateReservationGraphQLContract.CreateReservationData

    @Headers("Content-Type: application/json")
    @POST("/graphql")
    suspend fun saveReservationWithAncillariesGraphQL(
        @Body query: String
    ): SaveReservationWithAncillariesGraphQLContract.SaveReservationWithAncillariesData

    @Headers("Content-Type: application/json")
    @POST("/graphql")
    suspend fun getPackages(
        @Body query: String
    ): PackagesGraphQLContract.PackagesData

    @Headers("Content-Type: application/json")
    @POST("/graphql")
    suspend fun getPackagesAndAncillariesCloseout(
        @Body query: String
    ): PackagesAndAncillariesCloseoutGraphQLContract.PackagesAndAncillariesCloseoutData

    @Headers("Content-Type: application/json")
    @POST("/graphql")
    suspend fun getPaymentMethods(
        @Header("Authorization") bearerToken: String?,
        @Body query: String
    ): PaymentMethodsGraphQLContract.PaymentMethodsData

    @Headers("Content-Type: application/json")
    @POST("/graphql")
    suspend fun updatePreStayInfo(
        @Header("Authorization") bearerToken: String?,
        @Body query: String
    ): UpdateReservationGraphQLContract.UpdateReservationData

    @Headers("Content-Type: application/json")
    @POST("/graphql")
    suspend fun confirmPreCheckIn(
        @Body query: String
    ): PreCheckInConfirmationGraphQLContract.PreCheckInConfirmationData

    @Headers("Content-Type: application/json")
    @POST("/graphql")
    suspend fun updateReservationPackages(
        @Body query: String
    ): UpdateReservationPackagesByReservationGraphQLContract.UpdateReservationPackagesByReservationData

    @Headers("Content-Type: application/json")
    @POST("/graphql")
    suspend fun getHotelPreferences(
        @Body query: String
    ): HotelPreferencesGraphQLContract.HotelPreferencesGraphQLData

    @Headers("Content-Type: application/json")
    @POST("/graphql")
    suspend fun updateReservationPreferences(
        @Body query: String
    ): UpdateReservationPreferencesGraphQLContract.UpdateReservationPreferencesGraphQLData

    @Headers("Content-Type: application/json")
    @POST("/graphql")
    suspend fun attachFileToReservation(
        @Body query: String
    ): AttachFileToReservationGraphQLContract.AttachFileToReservationData

    @Headers("Content-Type: application/json")
    @POST("/graphql")
    suspend fun authorizeCard(
        @Body query: String
    ): AuthorizeCardGraphQLContract.AuthorizeCardData

    @Headers("Content-Type: application/json")
    @POST("/graphql")
    suspend fun preCheckIn(
        @Body query: String
    ): PreCheckInGraphQLContract.PreCheckInData

    @Headers("Content-Type: application/json")
    @POST("/graphql")
    suspend fun confirmPreCheckOut(
        @Body query: String
    ): ConfirmPreCheckOutGraphQLContract.ConfirmPreCheckOutData

    @Headers("Content-Type: application/json")
    @POST("/graphql")
    suspend fun createReservationGuestGraphQL(
        @Header("Authorization") bearerToken: String?,
        @Body query: String
    ): CreateReservationGuestGraphQLContract.CreateReservationGuestData
}