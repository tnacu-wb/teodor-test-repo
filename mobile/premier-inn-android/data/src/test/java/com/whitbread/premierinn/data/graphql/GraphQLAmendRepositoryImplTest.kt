package com.whitbread.premierinn.data.graphql

import android.content.SharedPreferences
import com.whitbread.premierinn.data.GsonFactory
import com.whitbread.premierinn.data.common.FileDataProvider
import com.whitbread.premierinn.data.common.LANGUAGE_ENGLISH
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.data.graphql.mapper.mapToAddNewRoomGraphQL
import com.whitbread.premierinn.data.graphql.mapper.mapToAmendEditRoomGQL
import com.whitbread.premierinn.data.graphql.mapper.mapToAmendSummaryGQL
import com.whitbread.premierinn.data.graphql.mapper.mapToChangeBookingDatesGraphQL
import com.whitbread.premierinn.data.graphql.mapper.mapToConfirmAmendLogicGQL
import com.whitbread.premierinn.data.graphql.mapper.mapToCopyBookingGQL
import com.whitbread.premierinn.data.graphql.mapper.mapToPackagesAndAncillaryCloseoutDomain
import com.whitbread.premierinn.data.graphql.mapper.mapToRemoveRoomGQL
import com.whitbread.premierinn.data.graphql.mapper.mapToUpdateReservationPackagesByReservationGQL
import com.whitbread.premierinn.data.remote.GraphQLErrorBody
import com.whitbread.premierinn.data.remote.GraphQlThrowable
import com.whitbread.premierinn.data.remote.graphql.NonRxGraphQLServicesApi
import com.whitbread.premierinn.data.remote.graphql.WBGraphQLServicesApi
import com.whitbread.premierinn.data.remote.graphql.contracts.AddNewRoomGraphQLContract
import com.whitbread.premierinn.data.remote.graphql.contracts.AmendEditRoomGraphQLContract
import com.whitbread.premierinn.data.remote.graphql.contracts.AmendSummaryGraphQLContract
import com.whitbread.premierinn.data.remote.graphql.contracts.ChangeBookingDatesGraphQLContract
import com.whitbread.premierinn.data.remote.graphql.contracts.ConfirmAmendGraphQLContract
import com.whitbread.premierinn.data.remote.graphql.contracts.CopyBookingGraphQLContract
import com.whitbread.premierinn.data.remote.graphql.contracts.PackagesAndAncillariesCloseoutGraphQLContract
import com.whitbread.premierinn.data.remote.graphql.contracts.RemoveRoomGraphQLContract
import com.whitbread.premierinn.data.remote.graphql.contracts.UpdateReservationPackagesByReservationGraphQLContract
import com.whitbread.premierinn.data.utils.FileUtils
import com.whitbread.premierinn.domain.common.AppDispatchers
import com.whitbread.premierinn.domain.common.BOOKING_CHANNEL_MOBILE
import com.whitbread.premierinn.domain.graphql.GraphQLServerError
import com.whitbread.premierinn.domain.graphql.amend.repository.GraphQLAmendRepository
import com.whitbread.premierinn.domain.graphql.requestBodyModels.AddNewRoomRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.AmendEditRoomRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.AmendRoomsSelections
import com.whitbread.premierinn.domain.graphql.requestBodyModels.AmendSelectedPackages
import com.whitbread.premierinn.domain.graphql.requestBodyModels.AmendSummaryRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.BookingChannelDetails
import com.whitbread.premierinn.domain.graphql.requestBodyModels.ChangeBookingDatesRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.Channel
import com.whitbread.premierinn.domain.graphql.requestBodyModels.ConfirmAmendLogicRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.CopyBookingRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.HotelPackagesRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.LeadGuestAmend
import com.whitbread.premierinn.domain.graphql.requestBodyModels.RemoveRoomRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.RoomOccupancyAmend
import com.whitbread.premierinn.domain.graphql.requestBodyModels.UpdateReservationWithAncillariesRequestBody
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Single
import junitparams.JUnitParamsRunner
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.json.JSONObject
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Locale

@RunWith(JUnitParamsRunner::class)
class GraphQLAmendRepositoryImplTest {

    private val graphQlApi: WBGraphQLServicesApi = mockk()
    private val nonRxGraphQlApi: NonRxGraphQLServicesApi = mockk()
    private val preferences: SharedPreferences = mockk()
    private val fileDataProvider: FileDataProvider = mockk()
    private var graphqlAmendRepository: GraphQLAmendRepository = mockk()
    private var jsonObject: JSONObject = mockk(relaxed = true)
    private var jsonObjectForVariablesCopyBooking: JSONObject = mockk(relaxed = true)
    private var jsonObjectForVariablesRemoveRoom: JSONObject = mockk(relaxed = true)
    private var jsonObjectForVariablesAmendEditRoom: JSONObject = mockk(relaxed = true)
    private var jsonObjectForVariablesAddNewRoom: JSONObject = mockk(relaxed = true)
    private var jsonObjectForVariablesConfirmAmend: JSONObject = mockk(relaxed = true)
    private var jsonObjectForVariablesPackages: JSONObject = mockk(relaxed = true)
    private var jsonObjectForVariablesPackagesAndAncillaryCloseOut: JSONObject = mockk(relaxed = true)
    private var jsonObjectForVariablesUpdateReservation: JSONObject = mockk(relaxed = true)
    private var jsonObjectForVariablesAmendSummary: JSONObject = mockk(relaxed = true)
    private var jsonObjectForVariablesChangeBookingDates: JSONObject = mockk(relaxed = true)
    private var deviceLocaleProvider = mockk<DeviceLocaleProvider>()
    private var testDispatcher = UnconfinedTestDispatcher()
    private val dispatchers: AppDispatchers = mockk(relaxed = true)

    private val FILE_CONTENT_AS_STRING = "File query or mutation content as string"
    private val JSON_OBJECT_AS_STRING = "Json object as string"
    private val COPY_BOOKING_SUCCESS = GsonFactory.create().fromJson(
        FileUtils.loadFileFromResource("api/graphql-responses/copy_booking_success_gql.json"),
        CopyBookingGraphQLContract.CopyBookingData::class.java)
    private val REMOVE_ROOM_SUCCESS = GsonFactory.create().fromJson(
        FileUtils.loadFileFromResource("api/graphql-responses/remove_room_success_gql.json"),
        RemoveRoomGraphQLContract.RemoveRoomData::class.java)
    private val AMEND_EDIT_ROOM_SUCCESS = GsonFactory.create().fromJson(
        FileUtils.loadFileFromResource("api/graphql-responses/amend_edit_room_success_gql.json"),
        AmendEditRoomGraphQLContract.AmendEditRoomData::class.java)
    private val ADD_NEW_ROOM_SUCCESS = GsonFactory.create().fromJson(
        FileUtils.loadFileFromResource("api/graphql-responses/add_new_room_success_gql.json"),
        AddNewRoomGraphQLContract.AddNewRoomData::class.java)
    private val CONFIRM_AMEND_LOGIC_SUCCESS = GsonFactory.create().fromJson(
        FileUtils.loadFileFromResource("api/graphql-responses/confirm_amend_success_gql.json"),
        ConfirmAmendGraphQLContract.ConfirmAmendData::class.java)
    private val PACKAGES_AND_ANCILLARIES_CLOSEOUT_SUCCESS = GsonFactory.create().fromJson(
        FileUtils.loadFileFromResource("api/graphql-responses/packages_and_ancillaries_closeout_success_gql.json"),
        PackagesAndAncillariesCloseoutGraphQLContract.PackagesAndAncillariesCloseoutData::class.java)
    val CHANGE_BOOKING_DATES_SUCCESS = GsonFactory.create().fromJson(
        FileUtils.loadFileFromResource("api/graphql-responses/change_booking_dates_success_gql.json"),
        ChangeBookingDatesGraphQLContract.ChangeBookingDatesData::class.java)
    private val UPDATE_RESERVATION_SUCCESS = GsonFactory.create().fromJson(
        FileUtils.loadFileFromResource("api/graphql-responses/update_reservation_success_gql.json"),
        UpdateReservationPackagesByReservationGraphQLContract.UpdateReservationPackagesByReservationData::class.java)
    private val AMEND_SUMMARY_SUCCESS = GsonFactory.create().fromJson(
        FileUtils.loadFileFromResource("api/graphql-responses/amend_summary_success_gql.json"),
        AmendSummaryGraphQLContract.AmendSummaryData::class.java)

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup(){
        every { dispatchers.io } returns testDispatcher

        graphqlAmendRepository = GraphQLAmendRepositoryImpl(graphQlApi, nonRxGraphQlApi, fileDataProvider,
            jsonObject, jsonObjectForVariablesCopyBooking, jsonObjectForVariablesRemoveRoom,
            jsonObjectForVariablesAmendEditRoom, jsonObjectForVariablesAddNewRoom, jsonObjectForVariablesAmendSummary,
            jsonObjectForVariablesPackages, jsonObjectForVariablesConfirmAmend,
            jsonObjectForVariablesPackagesAndAncillaryCloseOut, jsonObjectForVariablesChangeBookingDates,
            jsonObjectForVariablesUpdateReservation, dispatchers)
        every { fileDataProvider.loadFileFromAssetGQL(any()) } returns FILE_CONTENT_AS_STRING
        every { preferences.getString(any(), any()) } returns " Some API key"
        every { jsonObject.toString() } returns JSON_OBJECT_AS_STRING
        every { deviceLocaleProvider.getDeviceLocale() } returns Locale.UK
    }

    @Test
    fun `Given CopyBooking is successful then no error is thrown and mapping works`() {
        every { graphQlApi.copyBookingGraphQL(any()) } returns Single.just(COPY_BOOKING_SUCCESS)

        val expectedChangeBookingDatesResp = COPY_BOOKING_SUCCESS.mapToCopyBookingGQL()

        graphqlAmendRepository.copyBooking(mockCopyBookingRequestBody).test()
            .assertNoErrors()
            .assertValue { it == expectedChangeBookingDatesResp }
            .assertValue { it.copyBasketReference == "BANBRI7430915" }
    }

    @Test
    fun `Given CopyBooking contains Error then return response as GraphQl Error`() {
        every { graphQlApi.copyBookingGraphQL(any()) } returns Single.error(
            GraphQlThrowable.GraphQLError(500, GraphQLErrorBody(500, "Server Error"))
        )

        graphqlAmendRepository.copyBooking(mockCopyBookingRequestBody).test()
            .assertError { it is GraphQLServerError }
    }

    private val mockCopyBookingRequestBody = CopyBookingRequestBody(
        "BANBRI1234", "sometoken%2Chhd", BookingChannelDetails("PI", "MOBILE", "en")
    )

    @Test
    fun `Given removeRoom is successful then no error is thrown and mapping works`() {
        every { graphQlApi.removeRoomGraphQL(any()) } returns Single.just(REMOVE_ROOM_SUCCESS)

        val expectedChangeBookingDatesResp = REMOVE_ROOM_SUCCESS.mapToRemoveRoomGQL()

        graphqlAmendRepository.removeRoom(mockRemoveRoomRequestBody_leisure).test()
            .assertNoErrors()
            .assertValue { it == expectedChangeBookingDatesResp }
            .assertValue { it.tempBookingRef == "BANBRI7430915" }
    }

    fun `Given ConfirmAmendLogic is successful then no error is thrown and mapping works`() {
        every { graphQlApi.confirmAmendGraphQL(any()) } returns Single.just(CONFIRM_AMEND_LOGIC_SUCCESS)

        val expectedConfirmAmendResp = CONFIRM_AMEND_LOGIC_SUCCESS.mapToConfirmAmendLogicGQL()

        graphqlAmendRepository.confirmAmendLogic(mockConfirmAmendLogicRequestBody_leisure).test()
            .assertNoErrors()
            .assertValue { it == expectedConfirmAmendResp }
            .assertValue { it.status == "PAYMENT_REQUIRED" }
            .assertValue {it.paymentRequiredDetailsDomain!!.paymentRedirectDomain == "uqyeuwyu82739283"}
    }

    fun `Given ConfirmAmendLogic is successful for business booking then no error is thrown and mapping works`() {
        every { graphQlApi.confirmAmendGraphQL(any()) } returns Single.just(CONFIRM_AMEND_LOGIC_SUCCESS)

        val expectedConfirmAmendResp = CONFIRM_AMEND_LOGIC_SUCCESS.mapToConfirmAmendLogicGQL()

        graphqlAmendRepository.confirmAmendLogic(mockConfirmAmendLogicRequestBody_business).test()
            .assertNoErrors()
            .assertValue { it == expectedConfirmAmendResp }
            .assertValue { it.status == "PAYMENT_REQUIRED" }
            .assertValue { it.paymentRequiredDetailsDomain!!.paymentRedirectDomain == "uqyeuwyu82739283" }
    }

    @Test
    fun `Given ConfirmAmendLogic contains Error then return response as GraphQl Error`() {
        every { graphQlApi.confirmAmendGraphQL(any()) } returns Single.error(
            GraphQlThrowable.GraphQLError(500, GraphQLErrorBody(500, "Server Error"))
        )
        graphqlAmendRepository.confirmAmendLogic(mockConfirmAmendLogicRequestBody_leisure).test()
            .assertError { it is GraphQLServerError }
    }

    @Test
    fun `Given ConfirmAmendLogic contains Error for business booking then return response as GraphQl Error`() {
        every { graphQlApi.confirmAmendGraphQL(any()) } returns Single.error(
            GraphQlThrowable.GraphQLError(500, GraphQLErrorBody(500, "Server Error"))
        )
        graphqlAmendRepository.confirmAmendLogic(mockConfirmAmendLogicRequestBody_business).test()
            .assertError { it is GraphQLServerError }
    }


    fun `Given AddNewRoom is successful then no error is thrown and mapping works`() {
        every { graphQlApi.addNewRoomGraphQL(any()) } returns Single.just(ADD_NEW_ROOM_SUCCESS)

        val expectedChangeBookingDatesResp = ADD_NEW_ROOM_SUCCESS.mapToAddNewRoomGraphQL()

        graphqlAmendRepository.addNewRoom(mockAddNewRoomRequestBody_leisure).test()
            .assertNoErrors()
            .assertValue { it == expectedChangeBookingDatesResp }
            .assertValue { it.tempBookingRef == "BANBRI7430915" }
    }

    fun `Given AddNewRoom is successful for business booking then no error is thrown and mapping works`() {
        every { graphQlApi.addNewRoomGraphQL(any()) } returns Single.just(ADD_NEW_ROOM_SUCCESS)

        val expectedChangeBookingDatesResp = ADD_NEW_ROOM_SUCCESS.mapToAddNewRoomGraphQL()

        graphqlAmendRepository.addNewRoom(mockAddNewRoomRequestBody_business).test()
            .assertNoErrors()
            .assertValue { it == expectedChangeBookingDatesResp }
            .assertValue { it.tempBookingRef == "BANBRI7430915" }
    }

    fun `Given AddNewRoom contains Error then return response as GraphQl Error`() {
        every { graphQlApi.addNewRoomGraphQL(any()) } returns Single.error(
            GraphQlThrowable.GraphQLError(500, GraphQLErrorBody(500, "Server Error"))
        )
        graphqlAmendRepository.addNewRoom(mockAddNewRoomRequestBody_leisure).test()
            .assertError { it is GraphQLServerError }
    }

    fun `Given AddNewRoom contains Error for business booking then return response as GraphQl Error`() {
        every { graphQlApi.addNewRoomGraphQL(any()) } returns Single.error(
            GraphQlThrowable.GraphQLError(500, GraphQLErrorBody(500, "Server Error"))
        )
        graphqlAmendRepository.addNewRoom(mockAddNewRoomRequestBody_business).test()
            .assertError { it is GraphQLServerError }
    }

    @Test
    fun `Given amendEditRoom is successful then no error is thrown and mapping works`() {
        every { graphQlApi.amendEditRoomGraphQL(any(), any()) } returns Single.just(AMEND_EDIT_ROOM_SUCCESS)

        val expectedChangeBookingDatesResp = AMEND_EDIT_ROOM_SUCCESS.mapToAmendEditRoomGQL()

        graphqlAmendRepository.amendEditRoom(mockAmendEditRoomRequestBody_leisure, null).test()
            .assertNoErrors()
            .assertValue { it == expectedChangeBookingDatesResp }
            .assertValue { it.tempBookingRef == "BANBRI7430915" }
    }

    @Test
    fun `Given amendEditRoom is successful for business booking then no error is thrown and mapping works`() {
        every { graphQlApi.amendEditRoomGraphQL(any(), any()) } returns Single.just(AMEND_EDIT_ROOM_SUCCESS)

        val expectedChangeBookingDatesResp = AMEND_EDIT_ROOM_SUCCESS.mapToAmendEditRoomGQL()

        graphqlAmendRepository.amendEditRoom(mockAmendEditRoomRequestBody_business, null).test()
            .assertNoErrors()
            .assertValue { it == expectedChangeBookingDatesResp }
            .assertValue { it.tempBookingRef == "BANBRI7430915" }
    }

    fun `Given amendEditRoom contains Error then return response as GraphQl Error`() {
        every { graphQlApi.amendEditRoomGraphQL(any(), any()) } returns Single.error(
            GraphQlThrowable.GraphQLError(500, GraphQLErrorBody(500, "Server Error"))
        )

        graphqlAmendRepository.amendEditRoom(mockAmendEditRoomRequestBody_leisure, null).test()
            .assertError { it is GraphQLServerError }
    }

    fun `Given amendEditRoom contains Error for business booking then return response as GraphQl Error`() {
        every { graphQlApi.amendEditRoomGraphQL(any(), any()) } returns Single.error(
            GraphQlThrowable.GraphQLError(500, GraphQLErrorBody(500, "Server Error"))
        )

        graphqlAmendRepository.amendEditRoom(mockAmendEditRoomRequestBody_business, null).test()
            .assertError { it is GraphQLServerError }
    }

    @Test
    fun `Given removeRoom contains Error then return response as GraphQl Error`() {
        every { graphQlApi.removeRoomGraphQL(any()) } returns Single.error(
            GraphQlThrowable.GraphQLError(500, GraphQLErrorBody(500, "Server Error"))
        )

        graphqlAmendRepository.removeRoom(mockRemoveRoomRequestBody_leisure).test()
            .assertError { it is GraphQLServerError }
    }

    @Test
    fun `Given removeRoom contains Error for business booking then return response as GraphQl Error`() {
        every { graphQlApi.removeRoomGraphQL(any()) } returns Single.error(
            GraphQlThrowable.GraphQLError(500, GraphQLErrorBody(500, "Server Error"))
        )

        graphqlAmendRepository.removeRoom(mockRemoveRoomRequestBody_business).test()
            .assertError { it is GraphQLServerError }
    }

    fun `Given Packages is successful then no error is thrown and mapping works`() {
        every { graphQlApi.getPackagesAndAncillariesCloseoutGraphQL(any()) } returns Single.just(PACKAGES_AND_ANCILLARIES_CLOSEOUT_SUCCESS)

        val expectedPackagesResponse = PACKAGES_AND_ANCILLARIES_CLOSEOUT_SUCCESS.mapToPackagesAndAncillaryCloseoutDomain(1)

        graphqlAmendRepository.packagesAndAncillariesCloseoutInfo(mockHotelPackagesRequestBody_leisure).test()
            .assertNoErrors()
            .assertValue { it == expectedPackagesResponse }
            .assertValue {
                it.ancillaryCloseOutItems.first().startDate == "25/12/2027"
                        && it.ancillaryCloseOutItems.first().endDate == "31/12/2027"
                        && it.ancillaryCloseOutItems.first().upsellCodes == "MDP"
            }
    }

    fun `Given Packages is successful for business booking then no error is thrown and mapping works`() {
        every { graphQlApi.getPackagesAndAncillariesCloseoutGraphQL(any()) } returns Single.just(PACKAGES_AND_ANCILLARIES_CLOSEOUT_SUCCESS)

        val expectedPackagesResponse = PACKAGES_AND_ANCILLARIES_CLOSEOUT_SUCCESS.mapToPackagesAndAncillaryCloseoutDomain(1)

        graphqlAmendRepository.packagesAndAncillariesCloseoutInfo(mockHotelPackagesRequestBody_business).test()
            .assertNoErrors()
            .assertValue { it == expectedPackagesResponse }
    }

    @Test
    fun `Given Packages contains Error then return response as GraphQl Error`() {
        every { graphQlApi.getPackagesAndAncillariesCloseoutGraphQL(any()) } returns Single.error(
            GraphQlThrowable.GraphQLError(500, GraphQLErrorBody(500, "Server Error"))
        )
        graphqlAmendRepository.packagesAndAncillariesCloseoutInfo(mockHotelPackagesRequestBody_leisure).test()
            .assertError { it is GraphQLServerError }
    }


    @Test
    fun `Given Packages contains Error for business booking then return response as GraphQl Error`() {
        every { graphQlApi.getPackagesAndAncillariesCloseoutGraphQL(any()) } returns Single.error(
            GraphQlThrowable.GraphQLError(500, GraphQLErrorBody(500, "Server Error"))
        )
        graphqlAmendRepository.packagesAndAncillariesCloseoutInfo(mockHotelPackagesRequestBody_business).test()
            .assertError { it is GraphQLServerError }
    }

    @Test
    fun `Give Change Booking Dates is successful then no error is thrown and mapping works`() {
        every { graphQlApi.changeBookingDatesGraphQL(any()) } returns Single.just(CHANGE_BOOKING_DATES_SUCCESS)

        val expectedChangeBookingDatesResp = CHANGE_BOOKING_DATES_SUCCESS.mapToChangeBookingDatesGraphQL()

        graphqlAmendRepository.changeBookingDates(mockChangeBookingDatesRequestBody_leisure).test()
            .assertNoErrors()
            .assertValue { it == expectedChangeBookingDatesResp }
            .assertValue {it.tempBasket == "BANBRI7430915"}
    }

    @Test
    fun `Give Change Booking Dates is successful for business booking then no error is thrown and mapping works`() {

        every { graphQlApi.changeBookingDatesGraphQL(any()) } returns Single.just(CHANGE_BOOKING_DATES_SUCCESS)

        val expectedChangeBookingDatesResp = CHANGE_BOOKING_DATES_SUCCESS.mapToChangeBookingDatesGraphQL()

        graphqlAmendRepository.changeBookingDates(mockChangeBookingDatesRequestBody_business).test()
            .assertNoErrors()
            .assertValue { it == expectedChangeBookingDatesResp }
            .assertValue { it.tempBasket == "BANBRI7430915" }
    }

    @Test
    fun `Given Change Booking Dates contains Error then return response as GraphQl Error`() {
        every { graphQlApi.changeBookingDatesGraphQL(any()) }  returns Single.error(
            GraphQlThrowable.GraphQLError(500, GraphQLErrorBody(500,"Server Error")))

        graphqlAmendRepository.changeBookingDates(mockChangeBookingDatesRequestBody_leisure).test()
            .assertError {it is GraphQLServerError }
    }


    @Test
    fun `Given Change Booking Dates contains Error for business booking then return response as GraphQl Error`() {

        every { graphQlApi.changeBookingDatesGraphQL(any()) } returns Single.error(
            GraphQlThrowable.GraphQLError(500, GraphQLErrorBody(500, "Server Error"))
        )

        graphqlAmendRepository.changeBookingDates(mockChangeBookingDatesRequestBody_business).test()
            .assertError { it is GraphQLServerError }
    }

    fun `Given Update Reservation is successful then no error is thrown and mapping works`() {
        every { graphQlApi.updateReservationPackagesByReservationGQL(any()) } returns Single.just(UPDATE_RESERVATION_SUCCESS)

        val expectedUpdateReservationResponse = UPDATE_RESERVATION_SUCCESS.mapToUpdateReservationPackagesByReservationGQL()

        graphqlAmendRepository.updateReservationPackagesByReservation(mockUpdateReservationInput()).test()
            .assertNoErrors()
            .assertValue { it == expectedUpdateReservationResponse }
    }

    @Test
    fun `Given Update Reservation contains Error then return response as GraphQl Error`() {
        every { graphQlApi.updateReservationPackagesByReservationGQL(any()) }  returns Single.error(
            GraphQlThrowable.GraphQLError(500, GraphQLErrorBody(500,"Server Error")))

        graphqlAmendRepository.updateReservationPackagesByReservation(mockUpdateReservationInput()).test()
            .assertError {it is GraphQLServerError }
    }

    fun `Given Amend Summary is successful then no error is thrown and mapping works`() {
        every { graphQlApi.amendSummaryGraphQL(any()) } returns Single.just(AMEND_SUMMARY_SUCCESS)

        val expectedAmendSummaryResponse = AMEND_SUMMARY_SUCCESS.mapToAmendSummaryGQL()

        graphqlAmendRepository.amendSummary(mockAmendSummaryRequest_leisure()).test()
            .assertNoErrors()
            .assertValue { it == expectedAmendSummaryResponse }
    }

    fun `Given Amend Summary is successful for business booking then no error is thrown and mapping works`() {
        every { graphQlApi.amendSummaryGraphQL(any()) } returns Single.just(AMEND_SUMMARY_SUCCESS)

        val expectedAmendSummaryResponse = AMEND_SUMMARY_SUCCESS.mapToAmendSummaryGQL()

        graphqlAmendRepository.amendSummary(mockAmendSummaryRequest_business()).test()
            .assertNoErrors()
            .assertValue { it == expectedAmendSummaryResponse }
    }

    @Test
    fun `Given Amend Summary contains Error then return response as GraphQl Error`() {
        every { graphQlApi.amendSummaryGraphQL(any())  }  returns Single.error(
            GraphQlThrowable.GraphQLError(500, GraphQLErrorBody(500,"Server Error")))

        graphqlAmendRepository.amendSummary(mockAmendSummaryRequest_leisure()).test()
            .assertError {it is GraphQLServerError }
    }

    val mockHotelPackagesRequestBody_leisure =
        HotelPackagesRequestBody(
            "BANBRI", "2022-06-05", "2022-06-07", 1, 0, 1,
            "en", "gb", "booking-nm-a1", channel = Channel.PI
        )

    private val mockConfirmAmendLogicRequestBody_leisure =
        ConfirmAmendLogicRequestBody(
            BookingChannelDetails(
                Channel.PI.name,
                BOOKING_CHANNEL_MOBILE,
                LANGUAGE_ENGLISH
            ),
            "BEHR211740",
            "BEHR211740",
            "CfiYrAs4Ks787uRmO+9A0HWnkl7/i",
            "pn",
            "testenvt",
        )

    private val mockAddNewRoomRequestBody_leisure =
        AddNewRoomRequestBody(
            BookingChannelDetails(
                Channel.PI.name,
                BOOKING_CHANNEL_MOBILE,
                LANGUAGE_ENGLISH
            ),
            "BANBRI7430915",
            RoomOccupancyAmend(1, 1, true),
            LeadGuestAmend(
                title = "Mr",
                firstName = "First",
                lastName = "Name",
                emailAddress = "first.name@whitbread.com",
            ),
            "FAM",
            "CfiYrAs4Ks787uRmO+9A0HWnkl/i",
            "FLEXRATE"
        )

    private val mockAmendEditRoomRequestBody_leisure =
        AmendEditRoomRequestBody(
            "BANBRI1234", "sometoken%2Chhd",
            BookingChannelDetails(
                Channel.PI.name,
                BOOKING_CHANNEL_MOBILE,
                LANGUAGE_ENGLISH
            ),
            RoomOccupancyAmend(1, 1, true),
            LeadGuestAmend(
                title = "Mr",
                firstName = "First",
                lastName = "Name",
                emailAddress = "first.name@whitbread.com"
            ),
            "FAM",
            "CfiYrAs4Ks787uRmO+9A0HWnkl/i"
        )

    private val mockChangeBookingDatesRequestBody_leisure =
        ChangeBookingDatesRequestBody(
            "BANBRI1234",
            "BANBRI",
            "123456",
            "65675ghg",
            BookingChannelDetails(
                Channel.PI.name,
                BOOKING_CHANNEL_MOBILE,
                LANGUAGE_ENGLISH
            )
        )

    private val mockRemoveRoomRequestBody_leisure =
        RemoveRoomRequestBody(
            "BANBRI1234",
            "WiAeFVuIlL1wOwsf",
            "CfiYrAs4Ks787uRmO+9A0HWnkl7/i",
            BookingChannelDetails(
                Channel.PI.name,
                BOOKING_CHANNEL_MOBILE,
                LANGUAGE_ENGLISH
            )
        )

    private val mockChangeBookingDatesRequestBody_business =
        mockChangeBookingDatesRequestBody_leisure.copy(
            bookingChannel = BookingChannelDetails(
                Channel.BB.name,
                BOOKING_CHANNEL_MOBILE,
                LANGUAGE_ENGLISH
            )
        )

    private val mockHotelPackagesRequestBody_business =
        mockHotelPackagesRequestBody_leisure.copy(channel = Channel.BB)

    private val mockConfirmAmendLogicRequestBody_business =
        mockConfirmAmendLogicRequestBody_leisure.copy(
            bookingChannel = BookingChannelDetails(
                Channel.BB.name,
                BOOKING_CHANNEL_MOBILE,
                LANGUAGE_ENGLISH
            )
        )

    private val mockAddNewRoomRequestBody_business =
        mockAddNewRoomRequestBody_leisure.copy(
            bookingChannel = BookingChannelDetails(
                Channel.BB.name,
                BOOKING_CHANNEL_MOBILE,
                LANGUAGE_ENGLISH
            )
        )

    private val mockAmendEditRoomRequestBody_business =
        mockAmendEditRoomRequestBody_leisure.copy(
            bookingChannel = BookingChannelDetails(
                Channel.BB.name,
                BOOKING_CHANNEL_MOBILE,
                LANGUAGE_ENGLISH
            )
        )

    private val mockRemoveRoomRequestBody_business =
        mockRemoveRoomRequestBody_leisure.copy(
            bookingChannel = BookingChannelDetails(
                Channel.BB.name,
                BOOKING_CHANNEL_MOBILE,
                LANGUAGE_ENGLISH
            )
        )

    private fun mockUpdateReservationInput(): UpdateReservationWithAncillariesRequestBody {
        val listOfRoomSelections = mutableListOf<AmendRoomsSelections>()
        val listOfPreviousRoomSelections = mutableListOf<AmendRoomsSelections>()
        val listOfSelectedPackages1 = mutableListOf<AmendSelectedPackages>()
        val listOfSelectedPackages2 = mutableListOf<AmendSelectedPackages>()
        val selectedPackages1 = AmendSelectedPackages("BFADBF", 1)
        val selectedPackages2 = AmendSelectedPackages("MDP", 1)
        listOfSelectedPackages1.add(selectedPackages1)
        listOfSelectedPackages1.add(selectedPackages2)
        listOfSelectedPackages2.add(selectedPackages1)
        listOfSelectedPackages2.add(selectedPackages2)

        listOfRoomSelections.add(AmendRoomsSelections("749580", listOfSelectedPackages1))
        listOfRoomSelections.add(AmendRoomsSelections("749580", listOfSelectedPackages2))
        listOfPreviousRoomSelections.add(AmendRoomsSelections("749579", listOfSelectedPackages1))
        listOfPreviousRoomSelections.add(AmendRoomsSelections("749579", listOfSelectedPackages2))

        return UpdateReservationWithAncillariesRequestBody(
            "MANOLD1234",
            "MANOLD",
            "2022-09-06",
            "2022-09-07",
            listOfRoomSelections,
            listOfPreviousRoomSelections
        )
    }

    private fun mockAmendSummaryRequest_leisure(): AmendSummaryRequestBody {
        return AmendSummaryRequestBody(
            tempBasketReference = "AJK-b93b4f82-b43a-400b-a2a6-937565eee7f0",
            originalBasketReference = "AJK-452e481a-832e-4769-aead-a089ac3c6c15",
            token = "Fhg8kIOVumfT3WAEfE47EiG3THktskVsxrj29pXWPyO9RTDgmR0JG8BCQEFsWpPAcQfZOXnsY=",
            country = "gb",
            bookingChannel = BookingChannelDetails(
                Channel.PI.name,
                BOOKING_CHANNEL_MOBILE,
                LANGUAGE_ENGLISH
            )

        )
    }

    private fun mockAmendSummaryRequest_business(): AmendSummaryRequestBody {
        return mockAmendSummaryRequest_leisure().copy(
            bookingChannel = BookingChannelDetails(
                Channel.BB.name,
                BOOKING_CHANNEL_MOBILE,
                LANGUAGE_ENGLISH
            )
        )
    }
}