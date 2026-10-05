package com.whitbread.premierinn.data.graphql

import com.whitbread.premierinn.data.GsonFactory
import com.whitbread.premierinn.data.common.FileDataProvider
import com.whitbread.premierinn.data.remote.graphql.WBGraphQLServicesApi
import com.whitbread.premierinn.data.remote.graphql.contracts.BookingHistoryGraphQLContract
import com.whitbread.premierinn.data.utils.FileUtils
import com.whitbread.premierinn.domain.authentication.UnAuthorizedCustomerError
import com.whitbread.premierinn.domain.booking.entity.Booking
import com.whitbread.premierinn.domain.common.AmendRestrictions
import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import com.whitbread.premierinn.domain.common.GBP
import com.whitbread.premierinn.domain.common.PriceDomain
import com.whitbread.premierinn.domain.graphql.myBookings.repository.GraphQLMyBookingsRepository
import com.whitbread.premierinn.domain.graphql.requestBodyModels.BookingChannelDetails
import com.whitbread.premierinn.domain.graphql.requestBodyModels.BookingHistoryRequestBody
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Single
import junitparams.JUnitParamsRunner
import org.json.JSONObject
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.threeten.bp.LocalDate
import java.util.*

@RunWith(JUnitParamsRunner::class)
class GraphQLMyBookingRepositoryImplTest {
    private val graphQlApi: WBGraphQLServicesApi = mockk()
    private val fileDataProvider: FileDataProvider = mockk()
    private var graphqlMyBookingRepo: GraphQLMyBookingsRepository = mockk()
    private var jsonObject: JSONObject = mockk(relaxed = true)
    private val jsonObjectForVariablesBookingHistory: JSONObject = mockk(relaxed = true)

    val BOOKING_HISTORY_SUCCESS = GsonFactory.create().fromJson(
        FileUtils.loadFileFromResource("api/graphql-responses/booking_history_success_gql.json"),
        BookingHistoryGraphQLContract.BookingHistoryData::class.java)
    val BOOKING_HISTORY_QUERY_STRING = "Booking history query"


    @Before
    fun setUp() {
        graphqlMyBookingRepo = GraphQLMyBookingsRepositoryImpl(graphQlApi, fileDataProvider,
                jsonObject, jsonObjectForVariablesBookingHistory)

        every { fileDataProvider.loadFileFromAssetGQL(any()) } returns BOOKING_HISTORY_QUERY_STRING
        every { jsonObject.toString() } returns BOOKING_HISTORY_QUERY_STRING
    }

    @Test
    fun `Given Booking history is successful then no error is thrown and mapping works`() {
        every { graphQlApi.getBookingHistoryGraphQL(any(), any()) } returns Single.just(BOOKING_HISTORY_SUCCESS)

        val expectedBooking = Booking(bookingReference = "AEC4618374",
            leadGuestSurname = "Vj",
            arrivalDate = LocalDate.of(2023, 9, 21),
            departureDate = LocalDate.of(2023, 9, 22),
            hotelCode = "BANBRI",
            hotelName = "Bangor (Gwynedd, North Wales)",
            numberOfRooms = 2,
            leadGuestFullName = "Mr Tester Vj",
            rateType = "FLEXRATE",
            prepaidAmount = PriceDomain.createWithGBPCurrency(0.0f),
            totalCost = PriceDomain(1998.0f, GBP),
            balanceOutstanding = null,
            amendable = true,
            cardFeeApplies = false,
            isCancelled = false,
            amendRestrictions = AmendRestrictions.createWithDefaults(),
            isLinkedToAccount = true,
            isBusinessBooking = false,
            bookingStatus = "FUTURE",
            isCheckInOnlineAvailable = true,
            hotelCountry = "DE")
        graphqlMyBookingRepo.getBookingHistory(
            BookingHistoryRequestBody(business = false,
            includeCheckInBookings = true,
            sortOrder = "DEFAULT",
            continuationToken = null,
            pageSize = 40,
            pageIndex = 1,
            bookingChannel = BookingChannelDetails("PI", "MOBILE", "en")),
            "Eyjhdjhhjdjhhdjhf")
            .test()
            .assertNoErrors()
            .assertValue { it.size == 10 }
            .assertValue { list ->
                list[1] == expectedBooking
            }
    }

    @Test
    fun `Given Booking history is  contains Error then return response as GraphQl Error`() {
        every { graphQlApi.getBookingHistoryGraphQL(any(), any()) }  returns Single.error(UnAuthorizedCustomerError)
        graphqlMyBookingRepo.getBookingHistory(
            BookingHistoryRequestBody(business = false,
                includeCheckInBookings = true,
                sortOrder = "DEFAULT",
                continuationToken = null,
                pageSize = 40,
                pageIndex = 1,
                bookingChannel = BookingChannelDetails("PI", "MOBILE", "en")),
           "Eyjhdjhhjdjhhdjhf")
            .test().assertError {it is UnAuthorizedCustomerError}
    }


}