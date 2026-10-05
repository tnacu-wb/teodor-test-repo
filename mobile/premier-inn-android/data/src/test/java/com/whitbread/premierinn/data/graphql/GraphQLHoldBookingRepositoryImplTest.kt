package com.whitbread.premierinn.data.graphql

import com.whitbread.premierinn.data.GsonFactory
import com.whitbread.premierinn.data.common.FileDataProvider
import com.whitbread.premierinn.data.common.LANGUAGE_ENGLISH
import com.whitbread.premierinn.data.graphql.mapper.mapToPackagesGraphQL
import com.whitbread.premierinn.data.remote.GraphQLErrorBody
import com.whitbread.premierinn.data.remote.GraphQlThrowable
import com.whitbread.premierinn.data.remote.graphql.WBGraphQLServicesApi
import com.whitbread.premierinn.data.remote.graphql.contracts.BookingInformationGraphQLContract
import com.whitbread.premierinn.data.remote.graphql.contracts.CreateReservationGraphQLContract
import com.whitbread.premierinn.data.remote.graphql.contracts.PackagesGraphQLContract
import com.whitbread.premierinn.data.utils.FileUtils
import com.whitbread.premierinn.domain.common.BOOKING_CHANNEL_MOBILE
import com.whitbread.premierinn.domain.graphql.GraphQLServerError
import com.whitbread.premierinn.domain.graphql.requestBodyModels.BookingChannelDetails
import com.whitbread.premierinn.domain.graphql.requestBodyModels.Channel
import com.whitbread.premierinn.domain.graphql.requestBodyModels.CreateReservationRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.HoldBookingRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.HotelPackagesRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.Reservations
import com.whitbread.premierinn.domain.graphql.requestBodyModels.RoomRates
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import io.reactivex.Single
import org.json.JSONObject
import org.junit.After
import org.junit.Before
import org.junit.Test
import javax.inject.Provider

class GraphQLHoldBookingRepositoryImplTest {

    private var wbGraphQLServicesApi: WBGraphQLServicesApi = mockk()
    private var fileDataProvider: FileDataProvider = mockk()
    private var jsonObject: JSONObject = mockk(relaxed = true)
    private var jsonObjectForVariablesCreateReservation: JSONObject = mockk(relaxed = true)
    private var jsonObjectForBookingInformation: JSONObject = mockk(relaxed = true)
    private var jsonObjectForVariablesPackages: JSONObject = mockk(relaxed = true)
    private var repository: GraphQLHoldBookingRepositoryImpl = mockk(relaxed = true)
    private var sensorData: Provider<String> = mockk(relaxed = true)

    private val FILE_CONTENT_AS_STRING = "File query or mutation content as string"
    val HOLD_QUERY_STRING = "Hold query string"

    val CREATE_RESERVATION_SUCCESS = GsonFactory.create().fromJson(
        FileUtils.loadFileFromResource("api/graphql-responses/create_reservation_success_gql.json"),
        CreateReservationGraphQLContract.CreateReservationData::class.java
    )

    val BOOKING_INFORMATION_SUCCESS = GsonFactory.create().fromJson(
        FileUtils.loadFileFromResource("api/graphql-responses/booking_information_success_gql.json"),
        BookingInformationGraphQLContract.BookingInformationData::class.java
    )

    val PACKAGES_SUCCESS = GsonFactory.create().fromJson(
        FileUtils.loadFileFromResource("api/graphql-responses/packages_success_gql.json"),
        PackagesGraphQLContract.PackagesData::class.java
    )


    @Before
    fun setUp() {
        repository = GraphQLHoldBookingRepositoryImpl(
            wbGraphQLServicesApi,
            fileDataProvider,
            jsonObject,
            jsonObjectForVariablesCreateReservation,
            jsonObjectForBookingInformation,
            jsonObjectForVariablesPackages,
            sensorData
        )

        every { fileDataProvider.loadFileFromAssetGQL(any()) } returns FILE_CONTENT_AS_STRING
        every { jsonObject.toString() } returns HOLD_QUERY_STRING
        every { sensorData.get() } returns "sensor-data-test"
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `holdBooking returns DataPackagesDomain when createReservation, booking information and packages are successful`() {
        val holdBookingRequestBody = createMockHoldBookingRequestBody()
        val token = "Token"
        every { wbGraphQLServicesApi.createReservationGraphQL(any(), any(), any()) } returns Single.just(
            CREATE_RESERVATION_SUCCESS
        )
        every { wbGraphQLServicesApi.getBookingInformationGraphQL(any()) } returns Single.just(
            BOOKING_INFORMATION_SUCCESS
        )
        every { wbGraphQLServicesApi.packagesGraphQL(any()) } returns Single.just(PACKAGES_SUCCESS)

        val expectedPackagesResp = PACKAGES_SUCCESS.data?.packages.mapToPackagesGraphQL(2)

        repository.holdBooking(holdBookingRequestBody, token).test()
            .assertValue(Pair(expectedPackagesResp, "BANBRI7430915"))
            .assertValue { it.first.packages.meals.first().id == "BFADBF"}
            .assertValue { it.first.packages.meals.first().name == "Premier Inn Breakfast"}
            .assertValue { it.second == "BANBRI7430915"}
            .assertNoErrors()
            .assertComplete()

        verify(exactly = 1) { wbGraphQLServicesApi.createReservationGraphQL("Bearer Token", "sensor-data-test", HOLD_QUERY_STRING)}
        verify(exactly = 1) { wbGraphQLServicesApi.getBookingInformationGraphQL(HOLD_QUERY_STRING)}
        verify(exactly = 1) { wbGraphQLServicesApi.packagesGraphQL(HOLD_QUERY_STRING)}

    }

    @Test
    fun `holdBooking returns error when createReservation fails`() {
        val holdBookingRequestBody = createMockHoldBookingRequestBody()
        val token = "Token"
        every { wbGraphQLServicesApi.createReservationGraphQL(any(), any(), any()) } returns  Single.error(
            CreateReservationException())

        repository.holdBooking(holdBookingRequestBody, token).test()
            .assertError { it is CreateReservationException }

        verify(exactly = 1) { wbGraphQLServicesApi.createReservationGraphQL(any(), any(), any())}
        verify(exactly = 0) { wbGraphQLServicesApi.getBookingInformationGraphQL(any())}
        verify(exactly = 0) { wbGraphQLServicesApi.packagesGraphQL(any())}
    }

    @Test
    fun `holdBooking returns error when createReservation is success but bookingInfo fails`() {
        val holdBookingRequestBody = createMockHoldBookingRequestBody()
        val token = "Token"

        every { wbGraphQLServicesApi.createReservationGraphQL(any(), any(), any()) } returns Single.just(
            CREATE_RESERVATION_SUCCESS
        )
        every { wbGraphQLServicesApi.getBookingInformationGraphQL(any()) } returns  Single.error(
            BookingInformationException("BANBRI7430915"))


        repository.holdBooking(holdBookingRequestBody, token).test()
            .assertError { it is BookingInformationException && it.basketReference == "BANBRI7430915" }


        verify(exactly = 1) { wbGraphQLServicesApi.createReservationGraphQL(any(), any(), any())}
        verify(exactly = 1) { wbGraphQLServicesApi.getBookingInformationGraphQL(any())}
        verify(exactly = 0) { wbGraphQLServicesApi.packagesGraphQL(any())}
    }

    @Test
    fun `holdBooking returns error when createReservation, booking info is success but packages fails`() {
        val holdBookingRequestBody = createMockHoldBookingRequestBody()
        val token = "Token"

        every { wbGraphQLServicesApi.createReservationGraphQL(any(), any(), any()) } returns Single.just(
            CREATE_RESERVATION_SUCCESS
        )
        every { wbGraphQLServicesApi.getBookingInformationGraphQL(any()) } returns Single.just(
            BOOKING_INFORMATION_SUCCESS
        )

        every { wbGraphQLServicesApi.packagesGraphQL(any()) } returns  Single.error(
            GraphQlThrowable.GraphQLError(500, GraphQLErrorBody(500,"Server Error")))

        repository.holdBooking(holdBookingRequestBody, token).test()
            .assertError { it is GraphQLServerError }

        verify(exactly = 1) { wbGraphQLServicesApi.createReservationGraphQL(any(), any(), any())}
        verify(exactly = 1) { wbGraphQLServicesApi.getBookingInformationGraphQL(any())}
        verify(exactly = 1) { wbGraphQLServicesApi.packagesGraphQL(any())}
    }

    private fun createMockHoldBookingRequestBody(): HoldBookingRequestBody {
        return HoldBookingRequestBody(
            createReservationRequestBody = mockCreateReservationRequestBody(),
            packagesRequestBody = mockHotelPackagesRequestBody,
            bookingChannel = BookingChannelDetails(
                Channel.PI.name,
                BOOKING_CHANNEL_MOBILE,
                LANGUAGE_ENGLISH
            ),
            country = "gb",
            language = "en"
        )
    }


    private val mockHotelPackagesRequestBody =
        HotelPackagesRequestBody(
            "BANBRI", "2022-06-23", "2022-06-25", 1, 0, 2,
            "en", "gb", "booking-a1", channel = Channel.PI
        )

    private fun mockCreateReservationRequestBody(): CreateReservationRequestBody {
        return CreateReservationRequestBody(
            reservations = listOf(
                Reservations(
                    "BANBRI", "2022-06-23", "2022-06-25",
                    "NEWDRO3701317", 1, 0, false,
                    RoomRates(
                        startDate = "2027-06-23",
                        endDate = "2027-06-25",
                        pmsRoomType = "DOUBLE",
                        specialRequests = listOf("SING", "LOWB"),
                        ratePlanCode = "FLEXRATE",
                        promotionCode = null,
                        promoKind = null
                    ),
                    reservationPackages = emptyList(),
                )
            ), BookingChannelDetails("PI", "MOBILE", "en")
        )
    }
}




