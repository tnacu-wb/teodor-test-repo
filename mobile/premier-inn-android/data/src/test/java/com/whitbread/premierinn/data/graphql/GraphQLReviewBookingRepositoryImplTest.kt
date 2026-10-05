package com.whitbread.premierinn.data.graphql

import com.whitbread.premierinn.data.GsonFactory
import com.whitbread.premierinn.data.common.FileDataProvider
import com.whitbread.premierinn.data.graphql.mapper.mapToBasketStatusRevisedPaymentsGQL
import com.whitbread.premierinn.data.graphql.mapper.mapToInitiatePaymentGQL
import com.whitbread.premierinn.data.graphql.mapper.mapToInitiatePaypalPaymentGQL
import com.whitbread.premierinn.data.graphql.mapper.mapToPaymentMethodsGQL
import com.whitbread.premierinn.data.remote.GraphQLErrorBody
import com.whitbread.premierinn.data.remote.GraphQlThrowable
import com.whitbread.premierinn.data.remote.graphql.WBGraphQLServicesApi
import com.whitbread.premierinn.data.remote.graphql.contracts.BasketStatusRevisedPaymentsGraphQLContract
import com.whitbread.premierinn.data.remote.graphql.contracts.InitiatePaymentGraphQLContract
import com.whitbread.premierinn.data.remote.graphql.contracts.InitiatePaypalPaymentGraphQLContract
import com.whitbread.premierinn.data.remote.graphql.contracts.PaymentMethodsGraphQLContract
import com.whitbread.premierinn.data.utils.FileUtils
import com.whitbread.premierinn.domain.common.ANDROID_APPS_CHANNEL
import com.whitbread.premierinn.domain.common.BUSINESS_FULL_NAME
import com.whitbread.premierinn.domain.graphql.GraphQLServerError
import com.whitbread.premierinn.domain.graphql.requestBodyModels.Address
import com.whitbread.premierinn.domain.graphql.requestBodyModels.Billing
import com.whitbread.premierinn.domain.graphql.requestBodyModels.Booking
import com.whitbread.premierinn.domain.graphql.requestBodyModels.BusinessAllowances
import com.whitbread.premierinn.domain.graphql.requestBodyModels.BusinessItems
import com.whitbread.premierinn.domain.graphql.requestBodyModels.BusinessSite
import com.whitbread.premierinn.domain.graphql.requestBodyModels.CreatePaymentCriteria
import com.whitbread.premierinn.domain.graphql.requestBodyModels.InitiatePaymentRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.LeadGuestBooking
import com.whitbread.premierinn.domain.graphql.requestBodyModels.Payment
import com.whitbread.premierinn.domain.graphql.requestBodyModels.PaymentMethodsRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.Rooms
import com.whitbread.premierinn.domain.graphql.reviewBooking.repository.GraphQLReviewBookingRepository
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Single
import junitparams.JUnitParamsRunner
import org.json.JSONObject
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Provider

@RunWith(JUnitParamsRunner::class)
class GraphQLReviewBookingRepositoryImplTest {
    private val graphQlApi: WBGraphQLServicesApi = mockk()
    private val fileDataProvider: FileDataProvider = mockk()
    private var graphQLReviewBookingRepo: GraphQLReviewBookingRepository = mockk()
    private var jsonObject: JSONObject = mockk(relaxed = true)
    private val jsonObjectForVariablesInitiatePayment: JSONObject = mockk(relaxed = true)
    private val jsonObjectForVariablesInitiatePaypalPayment: JSONObject = mockk(relaxed = true)
    private val jsonObjectForVariablesBasketStatusRevisedPaymentsDomain: JSONObject = mockk(relaxed = true)
    private val jsonObjectForVariablesPaymentMethodsAndBookingConfirmation: JSONObject = mockk(relaxed = true)
    private val jsonObjectForVariablesCreateReservation: JSONObject = mockk(relaxed = true)
    private var sensorData: Provider<String> = mockk(relaxed = true)

    val INITIATE_PAYMENT_SUCCESS = GsonFactory.create().fromJson(
        FileUtils.loadFileFromResource("api/graphql-responses/initiate_payment_success_gql.json"),
        InitiatePaymentGraphQLContract.InitiatePaymentData::class.java
    )

    val INITIATE_PAYPAL_PAYMENT_SUCCESS = GsonFactory.create().fromJson(
        FileUtils.loadFileFromResource("api/graphql-responses/initiate_paypal_payment_success_gql.json"),
        InitiatePaypalPaymentGraphQLContract.InitiatePaypalPaymentData::class.java
    )

    val BASKET_STATUS_REVISED_PAYMENTS_SUCCESS_OPERA = GsonFactory.create().fromJson(
        FileUtils.loadFileFromResource("api/graphql-responses/basket_status_Revised_Payment_success_gql.json"),
        BasketStatusRevisedPaymentsGraphQLContract.BasketStatusRevisedPaymentsData::class.java
    )

    val INITIATE_PAYMENTY_QUERY_STRING = "Initiate payment query"

    val PAYMENT_METHODS_DONATION_AND_BOOKING_CONFIRMATION = GsonFactory.create().fromJson(
        FileUtils.loadFileFromResource("api/graphql-responses/payment_methods_and_booking_confirmation_success_gql.json"),
        PaymentMethodsGraphQLContract.PaymentMethodsData::class.java
    )

    @Before
    fun setUp() {
        graphQLReviewBookingRepo = GraphQLReviewBookingRepositoryImpl(
            graphQlApi,
            fileDataProvider,
            jsonObject,
            jsonObjectForVariablesInitiatePayment,
            jsonObjectForVariablesInitiatePaypalPayment,
            jsonObjectForVariablesBasketStatusRevisedPaymentsDomain,
            jsonObjectForVariablesCreateReservation,
            jsonObjectForVariablesPaymentMethodsAndBookingConfirmation,
            sensorData
        )

        every { fileDataProvider.loadFileFromAssetGQL(any()) } returns INITIATE_PAYMENTY_QUERY_STRING
        every { jsonObject.toString() } returns INITIATE_PAYMENTY_QUERY_STRING
        every { sensorData.get() } returns "sensor-data-test"
    }

    @Test
    fun `Given Initiate Payment is successful then no error is thrown and mapping works`() {
        every { graphQlApi.initiatePaymentGraphQL(any(), any()) } returns Single.just(
            INITIATE_PAYMENT_SUCCESS
        )

        val expectedPaymentResponse = INITIATE_PAYMENT_SUCCESS.mapToInitiatePaymentGQL()
        graphQLReviewBookingRepo.initiatePayment(mockPaymentData()).test()
            .assertNoErrors()
            .assertValue { it == expectedPaymentResponse }
    }

    @Test
    fun `Given Initiate payment contains Error then return response as GraphQl Error`() {
        every { graphQlApi.initiatePaymentGraphQL(any(), any()) } returns Single.error(
            GraphQlThrowable.GraphQLError(500, GraphQLErrorBody(500, "Server Error"))
        )

        graphQLReviewBookingRepo.initiatePayment(mockPaymentData()).test()
            .assertError { it is GraphQLServerError }
    }

    @Test
    fun `Given Initiate Paypal Payment is successful then no error is thrown and mapping works`() {
        every { graphQlApi.initiatePaypalPaymentGraphQL(any()) } returns Single.just(
            INITIATE_PAYPAL_PAYMENT_SUCCESS
        )

        val expectedPaymentResponse =
            INITIATE_PAYPAL_PAYMENT_SUCCESS.mapToInitiatePaypalPaymentGQL()
        graphQLReviewBookingRepo.initiatePaypalPayment(mockPaymentData()).test()
            .assertNoErrors()
            .assertValue { it == expectedPaymentResponse }
    }

    @Test
    fun `Given Initiate Paypal Payment contains Error then return response as GraphQl Error`() {
        every { graphQlApi.initiatePaypalPaymentGraphQL(any()) } returns Single.error(
            GraphQlThrowable.GraphQLError(500, GraphQLErrorBody(500, "Server Error"))
        )

        graphQLReviewBookingRepo.initiatePaypalPayment(mockPaymentData()).test()
            .assertError { it is GraphQLServerError }
    }

    @Test
    fun `Given payment method and booking confirmation is successful then no error is thrown and mapping works`() {
        every { graphQlApi.getPaymentMethodsGraphQL(any(), any()) } returns Single.just(
            PAYMENT_METHODS_DONATION_AND_BOOKING_CONFIRMATION
        )

        val expectedPaymentMethods =
            PAYMENT_METHODS_DONATION_AND_BOOKING_CONFIRMATION.mapToPaymentMethodsGQL()

        val paymentMethodsRequestBody = PaymentMethodsRequestBody(
            "NEWDRO6806399", "en", "gb", ANDROID_APPS_CHANNEL, BUSINESS_FULL_NAME
        )

        graphQLReviewBookingRepo.getPaymentMethodsAndDonationsAndBookingConfirmation(
            paymentMethodsRequestBody,
            "NEWDRO36365",
            null,
            true
        ).test()
            .assertNoErrors()
            .assertValue { it == expectedPaymentMethods }
            .assertValue { it.isPaymentMethodAvailable }
            .assertValue { it.bookingConfirmation!!.bookingReference == "AVG673993" }
            .assertValue { it.bookingConfirmation!!.totalCost == 159.08f }
    }

    @Test
    fun `Given payment method and booking confirmation contains Error then return response as GraphQl Error`() {
        every { graphQlApi.getPaymentMethodsGraphQL(any(), any()) } returns Single.error(
            GraphQlThrowable.GraphQLError(500, GraphQLErrorBody(500, "Server Error"))
        )
        val paymentMethodsRequestBody = PaymentMethodsRequestBody(
            "NEWDRO6806399", "en", "gb", ANDROID_APPS_CHANNEL, BUSINESS_FULL_NAME
        )

        graphQLReviewBookingRepo.getPaymentMethodsAndDonationsAndBookingConfirmation(
            paymentMethodsRequestBody,
            "NEWDRO36365",
            null,
            true
        ).test()
            .assertError { it is GraphQLServerError }
    }

    @Test
    fun `Given BasketStatusRevisedPayments is successful for Opera hotel then no error is thrown and mapping works`() {
        every { graphQlApi.getBasketStatusRevisedPaymentsGraphQL(any()) } returns Single.just(BASKET_STATUS_REVISED_PAYMENTS_SUCCESS_OPERA)

        val expectedBasketStatusRevisedResponse = BASKET_STATUS_REVISED_PAYMENTS_SUCCESS_OPERA.mapToBasketStatusRevisedPaymentsGQL()

        graphQLReviewBookingRepo.getBasketStatusRevisedPayments("BANBRI").test()
            .assertNoErrors()
            .assertValue { it == expectedBasketStatusRevisedResponse }
            .assertValue { it.basketStatus.name == "COMPLETED" }
    }

    @Test
    fun `Given BasketStatusRevisedPayments contains Error then return response as GraphQl Error`() {
        every { graphQlApi.getBasketStatusRevisedPaymentsGraphQL(any()) }  returns Single.error(
            GraphQlThrowable.GraphQLError(500, GraphQLErrorBody(500,"Server Error")))

        graphQLReviewBookingRepo.getBasketStatusRevisedPayments("BANBRI").test()
            .assertError {it is GraphQLServerError }
    }

private fun mockPaymentData(): InitiatePaymentRequestBody {
    return InitiatePaymentRequestBody(
        basketReference = "NEWDRO36365",
        createPaymentCriteria = CreatePaymentCriteria(
            booking = Booking(
                businessSite = BusinessSite(
                    identifier = "NEWDRO",
                    name = "Manchester",
                    type = "HOTEL",
                    location = "MANCHESTER"
                ),
                channel = "PI",
                journey = "BOOKING",
                language = "en",
                rooms = listOf(Rooms(
                    adultsNumber = 1,
                    rate = "SV344",
                    type = "FAM"
                )),
                type = "PAY_ON_ARRIVAL",
                leadGuest = LeadGuestBooking("Tester test",  false, 0, ""),
                arrivalDate = "2022-07-24",
                departureDate = "2022-07-25"
            ),
            payment = Payment(
                billing = Billing(
                    address = Address(
                        country = "GB",
                        addressLine1 = "Whitbread Group PLC",
                        postalCode = "LU5 5XE"
                    ),
                    email = "example@email.com",
                    firstName = "James",
                    lastName = "Bond",
                    title = "Mr"
                ),
                environment = "https://api.preprod.premierinn.digital",
                subType = "ECOMM",
                type = "CARD",
                businessItems =  BusinessItems(businessAllowances = listOf(
                    BusinessAllowances(budget = 0.0f, allowance = "dinner", isAuthorised = false),
                    BusinessAllowances(budget = 0.0f, allowance = "alcohol", isAuthorised = false),
                    BusinessAllowances(budget = 0.0f, allowance = "carParking", isAuthorised = false),
                    BusinessAllowances(budget = 0.0f, allowance = "ultimateWifi", isAuthorised = false))),
                pibaCardPresent = true,
                paypalNonce = null,
                paypalDeviceData = null,
                card = null
            ),
            requestId = "45673",
            charityPackageCode = "ZCHRY4",
            hotelId = "BANBRI",
            isCiol = false
        )
    )

}


}