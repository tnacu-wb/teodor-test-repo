package com.whitbread.premierinn.data.graphql

import com.google.gson.Gson
import com.whitbread.premierinn.data.common.FileDataProvider
import com.whitbread.premierinn.data.common.onGraphQLError
import com.whitbread.premierinn.data.graphql.mapper.mapToCreateReservationGuestGQL
import com.whitbread.premierinn.data.graphql.mapper.mapToFormattedAddressGQL
import com.whitbread.premierinn.data.graphql.mapper.mapToPartialAddressGQL
import com.whitbread.premierinn.data.remote.graphql.AUTHORIZATION_BEARER
import com.whitbread.premierinn.data.remote.graphql.WBGraphQLServicesApi
import com.whitbread.premierinn.domain.common.LEISURE_FULL_NAME
import com.whitbread.premierinn.domain.graphql.guestDetails.entity.CreateReservationGuestDomain
import com.whitbread.premierinn.domain.graphql.guestDetails.entity.FormattedAddressDomain
import com.whitbread.premierinn.domain.graphql.guestDetails.entity.PartialAddressDomain
import com.whitbread.premierinn.domain.graphql.guestDetails.repository.GraphQLGuestDetailsRepository
import com.whitbread.premierinn.domain.graphql.requestBodyModels.CreateReservationGuestRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.DonationsRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.PartialAddressRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.PaymentMethodsRequestBody
import io.reactivex.Single
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject

class GraphQLGuestDetailsRepositoryImpl @Inject constructor(
        private val wbGraphQLServicesApi: WBGraphQLServicesApi,
        private val fileDataProvider: FileDataProvider,
        private val jsonObject: JSONObject,
        private val jsonObjectForVariablesCreateReservation: JSONObject,
        private val jsonObjectForVariablesPartialAddress: JSONObject,
        private val jsonObjectForVariablesFormattedAddress: JSONObject): GraphQLGuestDetailsRepository {

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

    override fun getPartialAddress(input: PartialAddressRequestBody): Single<PartialAddressDomain> {
        val query = fileDataProvider.loadFileFromAssetGQL("graphql/PartialAddressQueryGQL.txt")
        val constructVariablesJsonObject = input.constructVariables(jsonObjectForVariablesPartialAddress)

        jsonObject.put("query", query)
        jsonObject.put("variables", constructVariablesJsonObject)

        return wbGraphQLServicesApi.getPartialAddressGraphQL(
            query = jsonObject.toString())
            .onGraphQLError()
            .map { response ->
                return@map response.mapToPartialAddressGQL(input.searchTerm)
            }.doOnError { throwable ->
                println("GraphQl not able to connect: "+throwable.localizedMessage)
            }
    }

    override fun getFormattedAddress(id: String): Single<FormattedAddressDomain> {
        val query = fileDataProvider.loadFileFromAssetGQL("graphql/FormattedAddressQueryGQL.txt")
        val constructVariablesJsonObject = jsonObjectForVariablesFormattedAddress.apply {
            put("identifier", id)
        }

        jsonObject.put("query", query)
        jsonObject.put("variables", constructVariablesJsonObject)

        return wbGraphQLServicesApi.getFormattedAddressGraphQL(
            query = jsonObject.toString())
            .onGraphQLError()
            .map { response ->
                return@map response.mapToFormattedAddressGQL()
            }.doOnError { throwable ->
                println("GraphQl not able to connect: "+throwable.localizedMessage)
            }
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

    private fun PaymentMethodsRequestBody.constructVariables(jsonObject: JSONObject) : JSONObject {
        return jsonObject.apply {
            put("paymentMethodsCriteria", JSONObject().apply {
                put("basketReference", this@constructVariables.basketReference)
                put("language", this@constructVariables.language)
                put("country", this@constructVariables.country)
                put("clientChannel", this@constructVariables.clientChannel)
                put("userType", this@constructVariables.userType)
            })
        }
    }

    private fun constructVariables(paymentInput: PaymentMethodsRequestBody,
                                   donationInput: DonationsRequestBody,
                                   jsonObject: JSONObject) : JSONObject {
        return jsonObject.apply {
            put("paymentMethodsCriteria", JSONObject().apply {
                put("basketReference", paymentInput.basketReference)
                put("language", paymentInput.language)
                put("country", paymentInput.country)
                put("clientChannel", paymentInput.clientChannel)
            })
            put("bookingFlowCriteria", JSONObject().apply {
                put("hotelId", donationInput.hotelId)
                put("language", donationInput.language)
                put("country", donationInput.country)
                put("rateCode", donationInput.rateCode)
            })
        }
    }

    private fun constructVariables(paymentInput: PaymentMethodsRequestBody,
                                   donationInput: DonationsRequestBody,
                                   basketReference: String, country: String, language: String,
                                   jsonObject: JSONObject) : JSONObject {
        return jsonObject.apply {
            put("paymentMethodsCriteria", JSONObject().apply {
                put("basketReference", paymentInput.basketReference)
                put("language", paymentInput.language)
                put("country", paymentInput.country)
                put("clientChannel", paymentInput.clientChannel)
                put("userType", paymentInput.userType)
            })
            put("bookingFlowCriteria", JSONObject().apply {
                put("hotelId", donationInput.hotelId)
                put("language", donationInput.language)
                put("country", donationInput.country)
                put("rateCode", donationInput.rateCode)
            })
            put("basketReference", basketReference)
            put("country", country)
            put("language", language)
            put("userType", LEISURE_FULL_NAME)
        }
    }

    private fun PartialAddressRequestBody.constructVariables(jsonObject: JSONObject) : JSONObject {
        return jsonObject.apply {
            put("partialAddressCriteria", JSONObject().apply {
                put("searchTerm", this@constructVariables.searchTerm)
            })
        }
    }

}