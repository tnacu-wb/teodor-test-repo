package com.whitbread.premierinn.data.graphql

import android.content.SharedPreferences
import com.whitbread.premierinn.data.GsonFactory
import com.whitbread.premierinn.data.common.FileDataProvider
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.data.graphql.mapper.mapToCancelOnHoldReservationGQL
import com.whitbread.premierinn.data.graphql.mapper.mapToHotelAvailabilityGQL
import com.whitbread.premierinn.data.graphql.mapper.mapToRatesInformationGQL
import com.whitbread.premierinn.data.remote.GraphQLErrorBody
import com.whitbread.premierinn.data.remote.GraphQlThrowable
import com.whitbread.premierinn.data.remote.graphql.WBGraphQLServicesApi
import com.whitbread.premierinn.data.remote.graphql.contracts.CancelOnHoldReservationGraphQLContract
import com.whitbread.premierinn.data.remote.graphql.contracts.HotelAvailabilityGraphQLContract
import com.whitbread.premierinn.data.remote.graphql.contracts.RatesInformationGraphQLContract
import com.whitbread.premierinn.data.utils.FileUtils
import com.whitbread.premierinn.domain.common.EMPLOYEE_CODE
import com.whitbread.premierinn.domain.graphql.GraphQLServerError
import com.whitbread.premierinn.domain.graphql.hdp.repository.GraphQLHDPRepository
import com.whitbread.premierinn.domain.graphql.requestBodyModels.BookingChannelDetails
import com.whitbread.premierinn.domain.graphql.requestBodyModels.CancelOnHoldReservationRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.Channel
import com.whitbread.premierinn.domain.graphql.requestBodyModels.HotelAvailabilityRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.HotelInfoDetails
import com.whitbread.premierinn.domain.graphql.requestBodyModels.RoomSearch
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Single
import junitparams.JUnitParamsRunner
import org.json.JSONObject
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Collections
import java.util.Locale

@RunWith(JUnitParamsRunner::class)
class GraphQLHDPRepositoryImplTest {
    private val graphQlApi: WBGraphQLServicesApi = mockk()
    private val preferences: SharedPreferences = mockk()
    private val fileDataProvider: FileDataProvider = mockk()
    private var graphqlHdpRepo: GraphQLHDPRepository = mockk()
    private var jsonObject: JSONObject = mockk(relaxed = true)
    private val jsonObjectForVariablesHotelInfoSlug: JSONObject = mockk(relaxed = true)
    private val jsonObjectForVariablesHotelAvailabilityRatesInfoAndRoomTypeInfo: JSONObject = mockk(relaxed = true)
    private val jsonObjectForVariablesCancelOnHoldRes: JSONObject = mockk(relaxed = true)
    private val jsonObjectForVariablesRatesInformation: JSONObject = mockk(relaxed = true)
    private val deviceLocaleProviderMock: DeviceLocaleProvider = mockk()


    val HOTEL_AVAILABILITY_RATESINFO_ROOMTYPEINFO_SUCCESS = GsonFactory.create().fromJson(
            FileUtils.loadFileFromResource("api/graphql-responses/hotel_availability_ratesinfo_roomtypeinfo_success_gql.json"),
            HotelAvailabilityGraphQLContract.HotelAvailabilityData::class.java)
    val HOTEL_AVAILABILITY_RATESINFO_ROOMTYPEINFO_EMPLOYEE_SUCCESS = GsonFactory.create().fromJson(
        FileUtils.loadFileFromResource("api/graphql-responses/hotel_availability_ratesinfo_roomtypeinfo_success_employee_gql.json"),
        HotelAvailabilityGraphQLContract.HotelAvailabilityData::class.java)
    val HOTEL_AVAILABILITY_RATESINFO_ROOMTYPEINFO_BUSINESS_SUCCESS = GsonFactory.create().fromJson(
        FileUtils.loadFileFromResource("api/graphql-responses/hotel_availability_ratesinfo_roomtypeinfo_success_business_gql.json"),
        HotelAvailabilityGraphQLContract.HotelAvailabilityData::class.java)

    val RATES_INFORMATION_SUCCESS = GsonFactory.create().fromJson(
        FileUtils.loadFileFromResource("api/graphql-responses/rates_information_success_gql.json"),
        RatesInformationGraphQLContract.RatesInformationData::class.java)
    val HOTEL_DETAILS_QUERY_STRING = "Hotel info query"
    val CANCEL_ON_HOLD_RESERVATION_SUCCESS = GsonFactory.create().fromJson(
        FileUtils.loadFileFromResource("api/graphql-responses/cancel_on_hold_reservation_success_gql.json"),
        CancelOnHoldReservationGraphQLContract.CancelOnHoldReservationData::class.java)

    @Before
    fun setUp() {
        graphqlHdpRepo = GraphQLHDPRepositoryImpl(graphQlApi, fileDataProvider,
            jsonObject, jsonObjectForVariablesHotelInfoSlug, jsonObjectForVariablesHotelAvailabilityRatesInfoAndRoomTypeInfo,
            jsonObjectForVariablesRatesInformation, jsonObjectForVariablesCancelOnHoldRes)

        every { fileDataProvider.loadFileFromAssetGQL(any()) } returns HOTEL_DETAILS_QUERY_STRING
        every { preferences.getString(any(), any()) } returns " Some API key"
        every { jsonObject.toString() } returns HOTEL_DETAILS_QUERY_STRING
        every { deviceLocaleProviderMock.getDeviceLocale() } returns Locale.UK
    }

    //Leisure Tests
    @Test
    fun `Given Hotel availability_ratesInfo_roomType req being made is successful then no error is thrown and mapping works`() {
        every { graphQlApi.getHotelAvailabilityGraphQL(any(), any()) } returns Single.just(HOTEL_AVAILABILITY_RATESINFO_ROOMTYPEINFO_SUCCESS)
        val mockAvailabilityRequestBody =  HotelAvailabilityRequestBody("2022-10-04", "2022-10-05", HotelInfoDetails("BANBRI"),
                Collections.singletonList(RoomSearch(2, 0, true, "DB")),
                BookingChannelDetails("PI", "MOBILE", "en"), "pi", emptyList(), "")

        val expectedHotelAvailability = HOTEL_AVAILABILITY_RATESINFO_ROOMTYPEINFO_SUCCESS.mapToHotelAvailabilityGQL()


        graphqlHdpRepo.getHotelAvailabilityAndRatesInfoAndRoomTypeInfo(
            mockAvailabilityRequestBody, "en", "gb","BANBRI", "PI", "", "", null).test()
                .assertNoErrors()
                .assertValue { it == expectedHotelAvailability }
                .assertValue { it.roomRateDomainList!!.isNotEmpty() &&
                 it.packages == null &&
                 it.listOfRoomTypeInfo.isNotEmpty() &&
                 it.listOfRatesClassification.isNotEmpty() }
    }

    @Test
    fun `Given Hotel availability_ratesInfo_roomType req being made contains Error then return response as GraphQl Error`() {
        every { graphQlApi.getHotelAvailabilityGraphQL(any(), any()) }  returns Single.error(
            GraphQlThrowable.GraphQLError(500, GraphQLErrorBody(500,"Server Error")))

        val mockAvailabilityRequestBody =  HotelAvailabilityRequestBody("2022-10-04", "2022-10-05", HotelInfoDetails("BANBRI"),
            Collections.singletonList(RoomSearch(2, 0, true, "DB")),
            BookingChannelDetails("PI", "MOBILE", "en"), "pi", emptyList(), "")

        graphqlHdpRepo.getHotelAvailabilityAndRatesInfoAndRoomTypeInfo(
            mockAvailabilityRequestBody, "en", "gb","BANBRI", "PI", "", "", null).test()
            .assertError {it is GraphQLServerError}
    }

    @Test
    fun `Given ratesInformation call is successful no error is thrown and mapping works`() {
        every { graphQlApi.getRatesInformationGraphQL(any()) } returns Single.just(RATES_INFORMATION_SUCCESS)

        val expectedHotelAvailability = RATES_INFORMATION_SUCCESS.mapToRatesInformationGQL()

        graphqlHdpRepo.getRatesInformation(
            "pi", Channel.PI.name, listOf(EMPLOYEE_CODE),"en", "gb","BANBRI").test()
            .assertNoErrors()
            .assertValue { it == expectedHotelAvailability }
            .assertValue { it.listOfRatesClassification.isNotEmpty()}
    }

    @Test
    fun `Given ratesInformation call contains Error then return response as GraphQl Error`() {
        every { graphQlApi.getRatesInformationGraphQL(any()) }  returns Single.error(
            GraphQlThrowable.GraphQLError(500, GraphQLErrorBody(500,"Server Error")))

        graphqlHdpRepo.getRatesInformation(
            "pi", Channel.PI.name, listOf(EMPLOYEE_CODE),"en", "gb","BANBRI").test()
            .assertError {it is GraphQLServerError}
    }

    @Test
    fun `Given Cancel On Hold Reservation request is success then no error is thrown and mapping works`() {
        every { graphQlApi.cancelOnHoldReservationGraphQL(any()) } returns Single.just(CANCEL_ON_HOLD_RESERVATION_SUCCESS)

        val expectedCancelOnHoldReservationDomain = CANCEL_ON_HOLD_RESERVATION_SUCCESS.mapToCancelOnHoldReservationGQL()

        graphqlHdpRepo.cancelOnHoldReservation("AXR-8E23F668-0884-67AI-67RT-3CHSJFLSB", "BIRTOB").test()
            .assertNoErrors()
            .assertValue { it == expectedCancelOnHoldReservationDomain }
    }

    @Test
    fun `Given Cancel On Hold Reservation request contains Error then return response as GraphQl Error`() {
        every { graphQlApi.cancelOnHoldReservationGraphQL(any()) }  returns Single.error(
            GraphQlThrowable.GraphQLError(500, GraphQLErrorBody(500,"Server Error")))

        val cancelOnHoldReservationResponseBody =  CancelOnHoldReservationRequestBody("AXR-8E23F668-0884-67AI-67RT-3CHSJFLSB", "BIRTOB")

        graphqlHdpRepo.cancelOnHoldReservation(cancelOnHoldReservationResponseBody.hotelId, cancelOnHoldReservationResponseBody.basketReference).test()
            .assertError {it is GraphQLServerError}
    }

    //Employee Rate Tests
    @Test
    fun `Given Hotel availability_ratesInfo_roomType req being made is successful for Employee rate then no error is thrown and mapping works`() {
        every { graphQlApi.getHotelAvailabilityGraphQL(any(), any()) } returns Single.just(HOTEL_AVAILABILITY_RATESINFO_ROOMTYPEINFO_EMPLOYEE_SUCCESS)
        val mockAvailabilityRequestBody =  HotelAvailabilityRequestBody("2022-10-04", "2022-10-05", HotelInfoDetails("BANBRI"),
            Collections.singletonList(RoomSearch(2, 0, true, "DB")),
            BookingChannelDetails("PI", "MOBILE", "en"), "pi", listOf(EMPLOYEE_CODE), "")

        val expectedHotelAvailability = HOTEL_AVAILABILITY_RATESINFO_ROOMTYPEINFO_EMPLOYEE_SUCCESS.mapToHotelAvailabilityGQL()


        graphqlHdpRepo.getHotelAvailabilityAndRatesInfoAndRoomTypeInfo(
            mockAvailabilityRequestBody, "en", "gb","BANBRI", "PI", "", "", null).test()
            .assertNoErrors()
            .assertValue { it == expectedHotelAvailability }
            .assertValue { it.roomRateDomainList!!.isNotEmpty() &&
                    it.packages == null &&
                    it.listOfRoomTypeInfo.isNotEmpty() &&
                    it.listOfRatesClassification.isNotEmpty() &&
                    it.roomRateDomainList!!.first().cellCode == EMPLOYEE_CODE}
    }

    @Test
    fun `Given Hotel availability_ratesInfo_roomType req for employee rate being made contains Error then return response as GraphQl Error`() {
        every { graphQlApi.getHotelAvailabilityGraphQL(any(), any()) }  returns Single.error(
            GraphQlThrowable.GraphQLError(500, GraphQLErrorBody(500,"Server Error")))

        val mockAvailabilityRequestBody =  HotelAvailabilityRequestBody("2022-10-04", "2022-10-05", HotelInfoDetails("BANBRI"),
            Collections.singletonList(RoomSearch(2, 0, true, "DB")),
            BookingChannelDetails("PI", "MOBILE", "en"), "pi", listOf(EMPLOYEE_CODE), "")

        graphqlHdpRepo.getHotelAvailabilityAndRatesInfoAndRoomTypeInfo(
            mockAvailabilityRequestBody, "en", "gb","BANBRI", "PI", "", "", null).test()
            .assertError {it is GraphQLServerError}
    }

    //Business Booking Tests
    @Test
    fun `Given Hotel availability_ratesInfo_roomType req being made is successful for Business booking then no error is thrown and mapping works`() {
        every { graphQlApi.getHotelAvailabilityGraphQL(any(), any()) } returns Single.just(HOTEL_AVAILABILITY_RATESINFO_ROOMTYPEINFO_BUSINESS_SUCCESS)
        val mockAvailabilityRequestBody = HotelAvailabilityRequestBody(
            "2025-11-20", "2025-11-22", HotelInfoDetails("HEAPTI"),
            Collections.singletonList(RoomSearch(1, 0, false, "DB")),
            BookingChannelDetails("BB", "MOBILE", "en"), "pi", emptyList(), ""
        )

        val expectedHotelAvailability = HOTEL_AVAILABILITY_RATESINFO_ROOMTYPEINFO_BUSINESS_SUCCESS.mapToHotelAvailabilityGQL()

        graphqlHdpRepo.getHotelAvailabilityAndRatesInfoAndRoomTypeInfo(
            mockAvailabilityRequestBody, "en", "gb", "HEAPTI", "BB", "", "", null
        ).test()
            .assertNoErrors()
            .assertValue { it == expectedHotelAvailability }
            .assertValue { it.roomRateDomainList!!.isNotEmpty() &&
                    it.packages == null &&
                    it.listOfRoomTypeInfo.isNotEmpty() &&
                    it.listOfRatesClassification.isNotEmpty()
            }
    }

    @Test
    fun `Given Hotel availability_ratesInfo_roomType req for Business booking contains Error then return response as GraphQl Error`() {
        every { graphQlApi.getHotelAvailabilityGraphQL(any(), any()) } returns Single.error(
            GraphQlThrowable.GraphQLError(500, GraphQLErrorBody(500, "Server Error"))
        )

        val mockAvailabilityRequestBody = HotelAvailabilityRequestBody(
            "2025-11-20", "2025-11-22", HotelInfoDetails("HEAPTI"),
            Collections.singletonList(RoomSearch(1, 0, false, "DB")),
            BookingChannelDetails("BB", "MOBILE", "en"), "pi", emptyList(), ""
        )

        graphqlHdpRepo.getHotelAvailabilityAndRatesInfoAndRoomTypeInfo(
            mockAvailabilityRequestBody, "en", "gb", "HEAPTI", "BB", "", "", null
        ).test()
            .assertError { it is GraphQLServerError }
    }

    @Test
    fun `Given ratesInformation call is successful for Business booking then no error is thrown and mapping works`() {
        every { graphQlApi.getRatesInformationGraphQL(any()) } returns Single.just(RATES_INFORMATION_SUCCESS)

        val expectedHotelAvailability = RATES_INFORMATION_SUCCESS.mapToRatesInformationGQL()

        graphqlHdpRepo.getRatesInformation(
            "pi", Channel.BB.name, listOf(EMPLOYEE_CODE), "en", "gb", "BANBRI"
        ).test()
            .assertNoErrors()
            .assertValue { it == expectedHotelAvailability }
            .assertValue { it.listOfRatesClassification.isNotEmpty() }
    }

    @Test
    fun `Given ratesInformation call contains Error for Business booking then return response as GraphQl Error`() {
        every { graphQlApi.getRatesInformationGraphQL(any()) } returns Single.error(
            GraphQlThrowable.GraphQLError(500, GraphQLErrorBody(500, "Server Error"))
        )

        graphqlHdpRepo.getRatesInformation(
            "pi", Channel.BB.name, listOf(EMPLOYEE_CODE), "en", "gb", "BANBRI"
        ).test()
            .assertError { it is GraphQLServerError }
    }

}