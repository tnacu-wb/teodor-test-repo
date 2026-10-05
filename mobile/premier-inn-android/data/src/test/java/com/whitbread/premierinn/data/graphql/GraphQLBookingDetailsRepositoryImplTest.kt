package com.whitbread.premierinn.data.graphql

import android.R.id.input
import android.content.SharedPreferences
import com.whitbread.premierinn.data.GsonFactory
import com.whitbread.premierinn.data.booking.dao.BookingDao
import com.whitbread.premierinn.data.common.FileDataProvider
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.data.graphql.mapper.mapToBookingConfirmationDomain
import com.whitbread.premierinn.data.graphql.mapper.mapToBookingDomain
import com.whitbread.premierinn.data.graphql.mapper.mapToCancelReservationGQL
import com.whitbread.premierinn.data.remote.GraphQLErrorBody
import com.whitbread.premierinn.data.remote.GraphQlThrowable
import com.whitbread.premierinn.data.remote.graphql.WBGraphQLServicesApi
import com.whitbread.premierinn.data.remote.graphql.contracts.BookingConfirmationGraphQLContract
import com.whitbread.premierinn.data.remote.graphql.contracts.CancelReservationGraphQLContract
import com.whitbread.premierinn.data.utils.FileUtils
import com.whitbread.premierinn.domain.countries.entity.CountryDomain
import com.whitbread.premierinn.domain.countries.repository.CountriesRepository
import com.whitbread.premierinn.domain.graphql.GraphQLServerError
import com.whitbread.premierinn.domain.graphql.bookingDetails.repository.GraphQLBookingDetailsRepository
import com.whitbread.premierinn.domain.graphql.requestBodyModels.BookingChannelDetails
import com.whitbread.premierinn.domain.graphql.requestBodyModels.CancelInformationRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.CancelReservationRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.Channel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Single
import junitparams.JUnitParamsRunner
import org.json.JSONObject
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Locale

@RunWith(JUnitParamsRunner::class)
class GraphQLBookingDetailsRepositoryImplTest {

    private val graphQlApi: WBGraphQLServicesApi = mockk()
    private val preferences: SharedPreferences = mockk()
    private val fileDataProvider: FileDataProvider = mockk()
    private var graphqlBookingDetailsRepo: GraphQLBookingDetailsRepository = mockk()
    private var jsonObject: JSONObject = mockk(relaxed = true)
    private var jsonObjectForVariablesBookingConfirmationAndManageBooking: JSONObject = mockk(relaxed = true)
    private var jsonObjectForVariablesCancelReservation: JSONObject = mockk(relaxed = true)
    private var jsonObjectForVariablesBookingConfirmation: JSONObject = mockk(relaxed = true)
    private var jsonObjectForVariablesRoomKey: JSONObject = mockk(relaxed = true)
    private var jsonObjectForVariablesPackages: JSONObject = mockk(relaxed = true)
    private val bookingDao: BookingDao = mockk()
    private val countriesRepository: CountriesRepository = mockk()

    private var deviceLocaleProvider = mockk<DeviceLocaleProvider>()

    val BOOKING_CONFIRMATION_AND_MANAGE_BOOKING_SUCCESS = GsonFactory.create().fromJson(
        FileUtils.loadFileFromResource("api/graphql-responses/booking_confirmation_manage_booking_success_gql.json"),
        BookingConfirmationGraphQLContract.BookingConfirmationData::class.java)
    val BOOKING_CONFIRMATION_FOR_FIND_BOOKING_SUCCESS = GsonFactory.create().fromJson(
            FileUtils.loadFileFromResource("api/graphql-responses/booking_confirmation_success_gql.json"),
            BookingConfirmationGraphQLContract.BookingConfirmationData::class.java)
    val CANCEL_RESERVATION_SUCCESS = GsonFactory.create().fromJson(
        FileUtils.loadFileFromResource("api/graphql-responses/cancel_reservation_success_gql.json"),
        CancelReservationGraphQLContract.CancelReservationData::class.java)
    val BOOKING_INFO_QUERY_STRING = "Booking info query string"


    @Before
    fun setup(){
        graphqlBookingDetailsRepo = GraphQLBookingDetailsRepositoryImpl(fileDataProvider,
            jsonObject, jsonObjectForVariablesBookingConfirmationAndManageBooking,
            jsonObjectForVariablesCancelReservation, jsonObjectForVariablesBookingConfirmation,
            jsonObjectForVariablesRoomKey, graphQlApi, bookingDao, countriesRepository,
            jsonObjectForVariablesPackages)
        every { fileDataProvider.loadFileFromAssetGQL(any()) } returns BOOKING_INFO_QUERY_STRING
        every { preferences.getString(any(), any()) } returns " Some API key"
        every { jsonObject.toString() } returns BOOKING_INFO_QUERY_STRING
        every { deviceLocaleProvider.getDeviceLocale() } returns Locale.UK
    }

    @Test
    fun `GivenBookingConfirmation & ManageBooking is successful then no error is thrown and mapping works`() {
        every { graphQlApi.bookingConfirmationGraphQL(any(), any()) } returns Single.just(BOOKING_CONFIRMATION_AND_MANAGE_BOOKING_SUCCESS)
        every { countriesRepository.getCountriesFromSharedPref() } returns listOf(
            CountryDomain("A", "AT", "Austria", true, "")
        )

        val expectedBookingConfirmatoionResp = BOOKING_CONFIRMATION_AND_MANAGE_BOOKING_SUCCESS
            .mapToBookingDomain("Manchester old trafford", false, emptyList())

        graphqlBookingDetailsRepo.bookingConfirmationAndManageBooking(
            "MANOLD0379120",
            deviceLocaleProvider.getDeviceLocale().country.lowercase(),
            deviceLocaleProvider.getDeviceLocale().language.lowercase(),
            Channel.PI.name,
            "Manchester old trafford",
            mockCancelInformationRequestBody,
            "token"
        ).test().assertNoErrors().assertValue {
            it == expectedBookingConfirmatoionResp
        }
    }

    @Test
    fun `Given BookingConfirmation & ManageBooking contains Error then return response as GraphQl Error`() {
        every { graphQlApi.bookingConfirmationGraphQL (any(), any()) }  returns Single.error(
            GraphQlThrowable.GraphQLError(500, GraphQLErrorBody(500,"Server Error")))

        graphqlBookingDetailsRepo.bookingConfirmationAndManageBooking(
            "MANOLD0379120",
            deviceLocaleProvider.getDeviceLocale().country.lowercase(),
            deviceLocaleProvider.getDeviceLocale().language.lowercase(),
                "Manchester old trafford",
            null,
            mockCancelInformationRequestBody,
            "token").test()
            .assertError {it is GraphQLServerError }
    }


    @Test
    fun `Give Cancel reservation is successful then no error is thrown and mapping works`() {
        every { graphQlApi.cancelReservationGraphQL(any()) } returns Single.just(CANCEL_RESERVATION_SUCCESS)

        val expectedCancelReservationResp = CANCEL_RESERVATION_SUCCESS.mapToCancelReservationGQL()

        graphqlBookingDetailsRepo.cancelReservation(mockCancelReservationRequestBody, "BANBRI7430915").test()
            .assertNoErrors()
            .assertValue { it == expectedCancelReservationResp }

        verify(exactly = 1) { bookingDao.updateAsCanceled("BANBRI7430915") }
    }

    @Test
    fun `Given Cancel reservation contains Error then return response as GraphQl Error`() {
        every { graphQlApi.cancelReservationGraphQL(any()) }  returns Single.error(
            GraphQlThrowable.GraphQLError(500, GraphQLErrorBody(500,"Server Error")))

        graphqlBookingDetailsRepo.cancelReservation(mockCancelReservationRequestBody, "BANBRI7430915").test()
            .assertError {it is GraphQLServerError }

        verify(inverse = true) { bookingDao.updateAsCanceled("BANBRI7430915") }
    }

    @Test
    fun `Given BookingConfirmation For find booking is successful then no error is thrown and mapping works`() {
        every { graphQlApi.bookingConfirmationGraphQL(any(), any()) } returns Single.just(BOOKING_CONFIRMATION_FOR_FIND_BOOKING_SUCCESS)
        every { countriesRepository.getCountriesFromSharedPref() } returns listOf(
            CountryDomain("A", "AT", "Austria", true, "")
        )

        val expectedBookingConfirmatoionResp = BOOKING_CONFIRMATION_FOR_FIND_BOOKING_SUCCESS
            .data.bookingConfirmation!!.mapToBookingConfirmationDomain()

        graphqlBookingDetailsRepo.bookingConfirmationForFindBooking(
                "MANOLD0379120",
                deviceLocaleProvider.getDeviceLocale().country.lowercase(),
                deviceLocaleProvider.getDeviceLocale().language.lowercase(),
                "")
                .test()
                .assertNoErrors()
                .assertValue { it == expectedBookingConfirmatoionResp }
                .assertValue {it.bookingReference == "AWM1255039"}
    }

    @Test
    fun `Given BookingConfirmation For find booking  contains Error then return response as GraphQl Error`() {
        every { graphQlApi.bookingConfirmationGraphQL (any(), any()) }  returns Single.error(
                GraphQlThrowable.GraphQLError(500, GraphQLErrorBody(500,"Server Error")))

        graphqlBookingDetailsRepo.bookingConfirmationForFindBooking(
                "MANOLD0379120",
                deviceLocaleProvider.getDeviceLocale().country.lowercase(),
                deviceLocaleProvider.getDeviceLocale().language.lowercase(),
                "")
                .test()
                .assertError {it is GraphQLServerError }
    }

    private val mockCancelInformationRequestBody = CancelInformationRequestBody(
            "BANBRI1234", "BANBRI", "123456", "65675ghg", BookingChannelDetails("PI", "MOBILE", "en"))

    private val mockCancelReservationRequestBody = CancelReservationRequestBody(
        "BANBRI1234", "BANBRI", "sometoken")}