package com.whitbread.premierinn.data.remote.graphql

import com.whitbread.premierinn.data.remote.graphql.contracts.*
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

const val AUTHORIZATION_BEARER = "Bearer"

interface WBGraphQLServicesApi {
    @Headers("Content-Type: application/json")
    @POST("/graphql")
    fun getHotelInformationGraphQL(
        @Body query: String
    ): Single<HotelInfoGraphQLContract.HotelInfoData>

    @Headers("Content-Type: application/json")
    @POST("/graphql")
    fun getHotelInformationBySlugGraphQL(
        @Body query: String
    ): Single<HotelInfoSlugGraphQLContract.HotelInfoData>

    @Headers("Content-Type: application/json")
    @POST("/graphql")
    fun getHotelAvailabilityGraphQL(
        @Header("Authorization") bearerToken: String?,
        @Body query: String
    ): Single<HotelAvailabilityGraphQLContract.HotelAvailabilityData>

    @Headers("Content-Type: application/json")
    @POST("/graphql")
    fun getRatesInformationGraphQL(
        @Body query: String
    ): Single<RatesInformationGraphQLContract.RatesInformationData>

    @Headers("Content-Type: application/json")
    @POST("/graphql")
    fun createReservationGraphQL(
        @Header("Authorization") bearerToken: String?,
        @Header("X-acf-sensor-data") sensorData: String,
        @Body query: String
    ): Single<CreateReservationGraphQLContract.CreateReservationData>

    @Headers("Content-Type: application/json")
    @POST("/graphql")
    fun getBookingInformationGraphQL(
        @Body query: String
    ): Single<BookingInformationGraphQLContract.BookingInformationData>


    @Headers("Content-Type: application/json")
    @POST("/graphql")
    fun createReservationGuestGraphQL(
            @Header("Authorization") bearerToken: String?,
            @Body query: String
    ): Single<CreateReservationGuestGraphQLContract.CreateReservationGuestData>

    @Headers("Content-Type: application/json")
    @POST("/graphql")
    fun saveReservationWithAncillariesGraphQL(
        @Body query: String
    ): Single<SaveReservationWithAncillariesGraphQLContract.SaveReservationWithAncillariesData>

    @Headers("Content-Type: application/json")
    @POST("/graphql")
    fun updateReservationPackagesByReservationGQL(
        @Body query: String
    ): Single<UpdateReservationPackagesByReservationGraphQLContract.UpdateReservationPackagesByReservationData>

    @Headers("Content-Type: application/json")
    @POST("/graphql")
    fun initiatePaymentGraphQL(
        @Header("X-acf-sensor-data") sensorData: String,
        @Body query: String
    ): Single<InitiatePaymentGraphQLContract.InitiatePaymentData>

    @Headers("Content-Type: application/json")
    @POST("/graphql")
    fun saveCardGraphQL(
        @Header("Authorization") bearerToken: String,
        @Body query: String
    ): Single<MyAccountSaveCardGraphQLContract.SaveCardGraphQLContractData>

    @Headers("Content-Type: application/json")
    @POST("/graphql")
    fun initiatePaypalPaymentGraphQL(
        @Body query: String
    ): Single<InitiatePaypalPaymentGraphQLContract.InitiatePaypalPaymentData>

    @Headers("Content-Type: application/json")
    @POST("/graphql")
    fun bookingConfirmationGraphQL(
        @Header("Authorization") bearerToken: String?,
        @Body query: String
    ): Single<BookingConfirmationGraphQLContract.BookingConfirmationData>

    @Headers("Content-Type: application/json")
    @POST("/graphql")
    fun bookingConfirmationAndAmendSummaryGraphQL(
        @Header("Authorization") bearerToken: String?,
        @Body query: String
    ): Single<BookingConfirmationAndAmendSummaryGraphQLContract.BookingConfirmationAndAmendSummaryData>

    @Headers("Content-Type: application/json")
    @POST("/graphql")
    fun cancelReservationGraphQL(
        @Body query: String
    ): Single<CancelReservationGraphQLContract.CancelReservationData>

    @Headers("Content-Type: application/json")
    @POST("/graphql")
    fun getBusinessRulesGraphQL(
            @Body query: String
    ): Single<BusinessRulesGraphQLContract.BusinessRulesData>

    @Headers("Content-Type: application/json")
    @POST("/graphql")
    fun getPaymentMethodsGraphQL(
        @Header("Authorization") bearerToken: String?,
        @Body query: String
    ): Single<PaymentMethodsGraphQLContract.PaymentMethodsData>

    @Headers("Content-Type: application/json")
    @POST("/graphql")
    fun getPartialAddressGraphQL(
        @Body query: String
    ): Single<PartialAddressGraphQLContract.PartialAddressData>

    @Headers("Content-Type: application/json")
    @POST("/graphql")
    fun getFormattedAddressGraphQL(
        @Body query: String
    ): Single<FormattedAddressGraphQLContract.FormattedAddressData>

    @Headers("Content-Type: application/json")
    @POST("/graphql")
    fun findBookingGraphQL(
            @Body query: String
    ): Single<FindBookingGraphQLContract.FindBookingData>

    @Headers("Content-Type: application/json")
    @POST("/graphql")
    fun getBasketStatusRevisedPaymentsGraphQL(
        @Body query: String
    ): Single<BasketStatusRevisedPaymentsGraphQLContract.BasketStatusRevisedPaymentsData>

    @Headers("Content-Type: application/json")
    @POST("/graphql")
    fun cancelOnHoldReservationGraphQL(
        @Body query: String
    ): Single<CancelOnHoldReservationGraphQLContract.CancelOnHoldReservationData>

    @Headers("Content-Type: application/json")
    @POST("/graphql")
    fun copyBookingGraphQL(
        @Body query: String
    ): Single<CopyBookingGraphQLContract.CopyBookingData>

    @Headers("Content-Type: application/json")
    @POST("/graphql")
    fun removeRoomGraphQL(
        @Body query: String
    ): Single<RemoveRoomGraphQLContract.RemoveRoomData>

    @Headers("Content-Type: application/json")
    @POST("/graphql")
    fun amendEditRoomGraphQL(
        @Header("Authorization") bearerToken: String?,
        @Body query: String
    ): Single<AmendEditRoomGraphQLContract.AmendEditRoomData>

    @Headers("Content-Type: application/json")
    @POST("/graphql")
    fun addNewRoomGraphQL(
        @Body query: String
    ): Single<AddNewRoomGraphQLContract.AddNewRoomData>

    @Headers("Content-Type: application/json")
    @POST("/graphql")
    fun confirmAmendGraphQL(
        @Body query: String
    ): Single<ConfirmAmendGraphQLContract.ConfirmAmendData>

    @Headers("Content-Type: application/json")
    @POST("/graphql")
    fun packagesGraphQL(
        @Body query: String
    ): Single<PackagesGraphQLContract.PackagesData>

    @Headers("Content-Type: application/json")
    @POST("/graphql")
    fun getPackagesAndAncillariesCloseoutGraphQL(
        @Body query: String
    ): Single<PackagesAndAncillariesCloseoutGraphQLContract.PackagesAndAncillariesCloseoutData>

    @Headers("Content-Type: application/json")
    @POST("/graphql")
    fun getHotelAvailabilitiesGraphQL(
        @Header("Authorization") bearerToken: String?,
        @Body query: String
    ): Single<HotelAvailabilitiesGraphQLContract.HotelAvailabilitiesData>

    @Headers("Content-Type: application/json")
    @POST("/graphql")
    fun getCountriesGraphQL(
        @Body query: String
    ): Single<CountriesGraphQLContract.CountriesData>

    @Headers("Content-Type: application/json")
    @POST("/graphql")
    fun getBookingHistoryGraphQL(
            @Header("Authorization") bearerToken: String,
            @Body query: String
    ): Single<BookingHistoryGraphQLContract.BookingHistoryData>

    @Headers("Content-Type: application/json")
    @POST("/graphql")
    fun getRoomKeyInstructionsGraphQL(
        @Body query: String
    ): Single<RoomKeyInstructionsGraphQLContract.CategoryLabelsData>

    @Headers("Content-Type: application/json")
    @POST("/graphql")
    fun changeBookingDatesGraphQL(
        @Body query: String
    ): Single<ChangeBookingDatesGraphQLContract.ChangeBookingDatesData>

    @Headers("Content-Type: application/json")
    @POST("/graphql")
    fun amendSummaryGraphQL(
        @Body query: String
    ): Single<AmendSummaryGraphQLContract.AmendSummaryData>

    @Headers("Content-Type: application/json")
    @POST("/graphql")
    fun getHomePageAppsContent(
        @Body query: String
    ): Single<HomePageAppsContentGraphQLContract.HomePageAppsContentData>

    @Headers("Content-Type: application/json")
    @POST("/graphql")
    fun getHotelDisclaimerGraphQL(
        @Body query: String
    ): Single<HotelDisclaimerGraphQLContract.CategoryLabelsData>

    @Headers("Content-Type: application/json")
    @POST("/graphql")
    fun resendInvoiceEmail(
        @Body query: String
    ): Single<ResendInvoiceGraphQLContract.ResendInvoiceData>

    @Headers("Content-Type: application/json")
    @POST("/graphql")
    fun forgotPassword(
        @Body query: String
    ): Single<ForgotPasswordGraphQLContract.ForgotPasswordData>

    @Headers("Content-Type: application/json")
    @POST("/graphql")
    fun roomClassConfig(
        @Body query: String
    ): Single<RoomClassConfigGraphQLContract.RoomClassConfigData>

    @Headers("Content-Type: application/json")
    @POST("/graphql")
    fun getCombinedBusinessRestrictionsGraphQL(
        @Body query: String
    ): Single<CombinedBusinessRestrictionsGraphQLContract.CombinedBusinessRestrictionsResponse>

    @Headers("Content-Type: application/json")
    @POST("/graphql")
    fun getPromotionsInformationGraphQL(
        @Body query: String
    ): Single<PromotionsInformationGraphQLContract.PromotionsInformationData>

    @Headers("Content-Type: application/json")
    @POST("/graphql")
    fun updateMarketingPreferencesGraphQL(
        @Header("Authorization") bearerToken: String?,
        @Body query: String
    ): Single<UpdateMarketingPreferencesGraphQLContract.UpdateMarketingPreferencesData>

    @Headers("Content-Type: application/json")
    @POST("/graphql")
    fun getAnonymousNewsletterPreferencesGraphQL(
        @Body query: String
    ): Single<AnonymousNewsletterPreferencesGraphQLContract.AnonymousNewsletterPreferencesData>
}