package com.whitbread.premierinn.data.graphql

import com.google.gson.Gson
import com.whitbread.premierinn.data.common.FileDataProvider
import com.whitbread.premierinn.data.common.onGraphQLError
import com.whitbread.premierinn.data.graphql.mapper.mapToBasketStatusRevisedPaymentsGQL
import com.whitbread.premierinn.data.graphql.mapper.mapToCreateReservationGuestGQL
import com.whitbread.premierinn.data.graphql.mapper.mapToInitiatePaymentGQL
import com.whitbread.premierinn.data.graphql.mapper.mapToInitiatePaypalPaymentGQL
import com.whitbread.premierinn.data.graphql.mapper.mapToPaymentMethodsGQL
import com.whitbread.premierinn.data.remote.graphql.AUTHORIZATION_BEARER
import com.whitbread.premierinn.data.remote.graphql.WBGraphQLServicesApi
import com.whitbread.premierinn.domain.graphql.guestDetails.entity.CreateReservationGuestDomain
import com.whitbread.premierinn.domain.graphql.guestDetails.entity.PaymentMethodsWithDonationAndBookingConfirmationGQLDomain
import com.whitbread.premierinn.domain.graphql.requestBodyModels.CreateReservationGuestRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.DonationsRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.InitiatePaymentRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.PaymentMethodsRequestBody
import com.whitbread.premierinn.domain.graphql.reviewBooking.entity.BasketStatusRevisedPaymentsDomain
import com.whitbread.premierinn.domain.graphql.reviewBooking.entity.InitiatePaymentDomain
import com.whitbread.premierinn.domain.graphql.reviewBooking.entity.InitiatePaypalPaymentDomain
import com.whitbread.premierinn.domain.graphql.reviewBooking.repository.GraphQLReviewBookingRepository
import io.reactivex.Single
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider

class GraphQLReviewBookingRepositoryImpl @Inject constructor(
    private val wbGraphQLServicesApi: WBGraphQLServicesApi,
    private val fileDataProvider: FileDataProvider,
    private val jsonObject: JSONObject,
    private val jsonObjectForVariablesInitiatePayment: JSONObject,
    private val jsonObjectForVariablesInitiatePaypalPayment: JSONObject,
    private val jsonObjectForVariablesBasketStatusRevisedPaymentsDomain: JSONObject,
    private val jsonObjectForVariablesCreateReservation: JSONObject,
    private val jsonObjectForVariablesPaymentMethodsAndBookingConfirmation: JSONObject,
    @Named("AkamaiSensorData") private val sensorData: Provider<String>
): GraphQLReviewBookingRepository {


    override fun initiatePayment(input: InitiatePaymentRequestBody): Single<InitiatePaymentDomain> {

        val mutation = fileDataProvider.loadFileFromAssetGQL("graphql/InitiatePaymentMutationGQL.txt")
        val constructVariablesJsonObj = input.constructVariables(jsonObjectForVariablesInitiatePayment)

        jsonObject.put("query", mutation)
        jsonObject.put("variables", constructVariablesJsonObj)

        return wbGraphQLServicesApi.initiatePaymentGraphQL(
            sensorData.get(),
            query = jsonObject.toString())
            .onGraphQLError()
            .map { response ->
                return@map response.mapToInitiatePaymentGQL()
            }.doOnError { throwable ->
                println("GraphQl not able to connect: "+throwable.localizedMessage)
            }
    }

    override fun initiatePaypalPayment(input: InitiatePaymentRequestBody): Single<InitiatePaypalPaymentDomain> {

        val mutation = fileDataProvider.loadFileFromAssetGQL("graphql/InitiatePaypalPaymentMutationGQL.txt")
        val constructVariablesJsonObj = input.constructVariables(jsonObjectForVariablesInitiatePaypalPayment)

        jsonObject.put("query", mutation)
        jsonObject.put("variables", constructVariablesJsonObj)

        return wbGraphQLServicesApi.initiatePaypalPaymentGraphQL(
            query = jsonObject.toString())
            .onGraphQLError()
            .map { response ->
                return@map response.mapToInitiatePaypalPaymentGQL()
            }.doOnError { throwable ->
                println("GraphQl not able to connect: "+throwable.localizedMessage)
            }
    }

    private fun InitiatePaymentRequestBody.constructVariables(jsonObject: JSONObject) : JSONObject {
        return jsonObject.apply {
            put("basketReference", this@constructVariables.basketReference)
            put("createPaymentCriteria", JSONObject(Gson().toJson(this@constructVariables.createPaymentCriteria)))
        }
    }

    override fun getBasketStatusRevisedPayments(basketReference: String): Single<BasketStatusRevisedPaymentsDomain> {
        val query = fileDataProvider.loadFileFromAssetGQL("graphql/BasketStatusRevisedPaymentsQueryGQL.txt")
        val constructVariablesJsonObj = jsonObjectForVariablesBasketStatusRevisedPaymentsDomain.apply {
            put("basketReference", basketReference)
        }

        jsonObject.put("query", query)
        jsonObject.put("variables", constructVariablesJsonObj)

        return wbGraphQLServicesApi.getBasketStatusRevisedPaymentsGraphQL(
            query = jsonObject.toString())
            .onGraphQLError()
            .map { response ->
                return@map response.mapToBasketStatusRevisedPaymentsGQL()
            }.doOnError { throwable ->
                println("GraphQl not able to connect: " + throwable.localizedMessage)
            }
    }

    override fun createReservationGuest(
        token: String?,
        input: CreateReservationGuestRequestBody
    ): Single<CreateReservationGuestDomain> {

        val mutation = fileDataProvider.loadFileFromAssetGQL("graphql/CreateReservationGuestMutationGQL.txt")
        val constructVariablesJsonObj = input.constructVariables(jsonObjectForVariablesCreateReservation)

        jsonObject.put("query", mutation)
        jsonObject.put("variables", constructVariablesJsonObj)

        return wbGraphQLServicesApi.createReservationGuestGraphQL(
            token?.let { "$AUTHORIZATION_BEARER $token" },
            query = jsonObject.toString())
            .onGraphQLError()
            .map { response ->
                return@map response.mapToCreateReservationGuestGQL()
            }.doOnError { throwable ->
                println("GraphQl not able to connect: "+throwable.localizedMessage)
            }
    }


    override fun getPaymentMethodsAndDonationsAndBookingConfirmation(
        paymentMethodsRequestBody: PaymentMethodsRequestBody,
        token: String?,
        donationInput: DonationsRequestBody?,
        isInnBusinessUser: Boolean
    ): Single<PaymentMethodsWithDonationAndBookingConfirmationGQLDomain> {
        val queryFile = if (isInnBusinessUser) {
            "graphql/PaymentMethodsAndBookingConfirmationQueryGQL.txt"
        } else {
            "graphql/PaymentMethodsDonationAndBookingConfirmationQueryGQL.txt"
        }
        val query = fileDataProvider.loadFileFromAssetGQL(queryFile)
        val constructVariablesJsonObj = constructVariables(paymentMethodsRequestBody, donationInput)

        jsonObject.put("query", query)
        jsonObject.put("variables", constructVariablesJsonObj)

        return wbGraphQLServicesApi.getPaymentMethodsGraphQL(
            token?.let { "$AUTHORIZATION_BEARER $token" },
            query = jsonObject.toString())
            .onGraphQLError()
            .map { response ->
                return@map response.mapToPaymentMethodsGQL()
            }.doOnError { throwable ->
                println("GraphQl not able to connect: "+throwable.localizedMessage)
            }
    }

    private fun constructVariables(
        paymentMethodsRequestBody: PaymentMethodsRequestBody,
        donationInput: DonationsRequestBody?
    ): JSONObject =
        jsonObjectForVariablesPaymentMethodsAndBookingConfirmation.apply {
            put("paymentMethodsCriteria", JSONObject().apply {
                put("basketReference", paymentMethodsRequestBody.basketReference)
                put("language", paymentMethodsRequestBody.language)
                put("country", paymentMethodsRequestBody.country)
                put("clientChannel", paymentMethodsRequestBody.clientChannel)
                put("userType", paymentMethodsRequestBody.userType)
            })
            donationInput?.let {
                put("bookingFlowCriteria", JSONObject().apply {
                    put("hotelId", donationInput.hotelId)
                    put("language", donationInput.language)
                    put("country", donationInput.country)
                    put("rateCode", donationInput.rateCode)
                })
            }
            put("basketReference", paymentMethodsRequestBody.basketReference)
            put("country", paymentMethodsRequestBody.country)
            put("language", paymentMethodsRequestBody.language)
            put("bookingChannel", paymentMethodsRequestBody.clientChannel)
        }

    private fun CreateReservationGuestRequestBody.constructVariables(jsonObject: JSONObject) : JSONObject {
        return jsonObject.apply {
            put("createReservationGuestCriteria", JSONObject().apply {
                put("basketReference", this@constructVariables.basketReference)
                put("hotelId", this@constructVariables.hotelId)
                put("reasonForStay", this@constructVariables.reasonForStay)
                put("booker", JSONObject(Gson().toJson(this@constructVariables.booker)))
                put("stayingGuests", JSONArray(Gson().toJson(this@constructVariables.stayingGuests)))
            })
        }
    }
}