package com.whitbread.premierinn.data.graphql

import android.util.Log
import com.google.gson.Gson
import com.whitbread.premierinn.data.common.FileDataProvider
import com.whitbread.premierinn.data.common.onGraphQLError
import com.whitbread.premierinn.data.graphql.error.handleError
import com.whitbread.premierinn.data.graphql.mapper.mapToAddNewRoomGraphQL
import com.whitbread.premierinn.data.graphql.mapper.mapToAmendEditRoomGQL
import com.whitbread.premierinn.data.graphql.mapper.mapToAmendSummaryGQL
import com.whitbread.premierinn.data.graphql.mapper.mapToChangeBookingDatesGraphQL
import com.whitbread.premierinn.data.graphql.mapper.mapToConfirmAmendLogicGQL
import com.whitbread.premierinn.data.graphql.mapper.mapToCopyBookingGQL
import com.whitbread.premierinn.data.graphql.mapper.mapToPackagesAndAncillaryCloseoutDomain
import com.whitbread.premierinn.data.graphql.mapper.mapToRemoveRoomGQL
import com.whitbread.premierinn.data.graphql.mapper.mapToUpdateReservationPackagesByReservationGQL
import com.whitbread.premierinn.data.remote.graphql.AUTHORIZATION_BEARER
import com.whitbread.premierinn.data.remote.graphql.NonRxGraphQLServicesApi
import com.whitbread.premierinn.data.remote.graphql.WBGraphQLServicesApi
import com.whitbread.premierinn.domain.common.AppDispatchers
import com.whitbread.premierinn.domain.error.DataError
import com.whitbread.premierinn.domain.graphql.amend.entity.AmendSummaryDomain
import com.whitbread.premierinn.domain.graphql.amend.entity.ConfirmAmendLogicDomain
import com.whitbread.premierinn.domain.graphql.amend.entity.CopyBookingDomain
import com.whitbread.premierinn.domain.graphql.amend.entity.PackagesAndAncillaryCloseoutDomain
import com.whitbread.premierinn.domain.graphql.amend.entity.TempBookingRefDomain
import com.whitbread.premierinn.domain.graphql.amend.entity.UpdateReservationPackagesResponseDomain
import com.whitbread.premierinn.domain.graphql.amend.repository.GraphQLAmendRepository
import com.whitbread.premierinn.domain.graphql.bookingDetails.entity.ChangeBookingDatesDomain
import com.whitbread.premierinn.domain.graphql.requestBodyModels.AddNewRoomRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.AmendEditRoomRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.AmendSummaryRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.ChangeBookingDatesRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.ConfirmAmendLogicRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.CopyBookingRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.HotelPackagesRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.RemoveRoomRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.UpdateReservationWithAncillariesRequestBody
import com.whitbread.premierinn.domain.result.Result
import io.reactivex.Single
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject

private val TAG = GraphQLAmendRepositoryImpl::class.simpleName

class GraphQLAmendRepositoryImpl @Inject constructor(
    private val wbGraphQLServicesApi: WBGraphQLServicesApi,
    private val nonRxGraphQLServicesApi: NonRxGraphQLServicesApi,
    private val fileDataProvider: FileDataProvider,
    private val jsonObject: JSONObject,
    private val jsonObjectForVariablesCopyBooking: JSONObject,
    private val jsonObjectForVariablesRemoveRoom: JSONObject,
    private val jsonObjectForVariablesAmendEditRoom: JSONObject,
    private val jsonObjectForVariablesAddNewRoom: JSONObject,
    private val jsonObjectForVariablesAmendSummary: JSONObject,
    private val jsonObjectForVariablesPackages: JSONObject,
    private val jsonObjectForVariablesPackagesAndAncillaryCloseout: JSONObject,
    private val jsonObjectForVariablesConfirmAmend: JSONObject,
    private val jsonObjectForVariablesChangeBookingDates: JSONObject,
    private val jsonObjectForVariablesUpdateReservationPackagesByReservation: JSONObject,
    private val dispatchers: AppDispatchers
) : GraphQLAmendRepository {

    override fun copyBooking(
        copyBookingRequestBody: CopyBookingRequestBody
    ): Single<CopyBookingDomain> {

        val mutation =
            fileDataProvider.loadFileFromAssetGQL("graphql/CopyBookingMutationGQL.txt")

        val createJsonObjectForVariables =
            copyBookingRequestBody.constructVariables(jsonObjectForVariablesCopyBooking)

        jsonObject.put("query", mutation)
        jsonObject.put("variables", createJsonObjectForVariables)

        return wbGraphQLServicesApi.copyBookingGraphQL(
            query = jsonObject.toString())
            .onGraphQLError()
            .map { response ->
                return@map response.mapToCopyBookingGQL()
            }.doOnError { throwable ->
                println("GraphQl not able to connect: " + throwable.localizedMessage)
            }
    }

    override fun changeBookingDates(changeBookingDatesRequestBody: ChangeBookingDatesRequestBody
    ): Single<ChangeBookingDatesDomain> {

        val mutation =
            fileDataProvider.loadFileFromAssetGQL("graphql/ChangeBookingDatesMutationGQL.txt")

        val createVariableJsonObj = changeBookingDatesRequestBody.constructVariables(jsonObjectForVariablesChangeBookingDates)
        jsonObject.put("query", mutation)
        jsonObject.put("variables", createVariableJsonObj)

        return wbGraphQLServicesApi.changeBookingDatesGraphQL(
            query = jsonObject.toString())
            .onGraphQLError()
            .map { response ->
                return@map response.mapToChangeBookingDatesGraphQL()
            }
            .doOnError { throwable ->
                println("GraphQl not able to connect: " + throwable.localizedMessage)
            }
    }



    override fun addNewRoom(addNewRoomRequestBody: AddNewRoomRequestBody): Single<TempBookingRefDomain> {
        val mutation =
            fileDataProvider.loadFileFromAssetGQL("graphql/AddNewRoomMutationGQL.txt")

        val createJsonObjectForVariables =
            addNewRoomRequestBody.constructVariables(jsonObjectForVariablesAddNewRoom)

        jsonObject.put("query", mutation)
        jsonObject.put("variables", createJsonObjectForVariables)

        return wbGraphQLServicesApi.addNewRoomGraphQL(
            query = jsonObject.toString()
        )
            .onGraphQLError()
            .map { response ->
                return@map response.mapToAddNewRoomGraphQL()
            }
    }

    override fun packagesAndAncillariesCloseoutInfo(packagesRequestBody: HotelPackagesRequestBody): Single<PackagesAndAncillaryCloseoutDomain> {
        val query = fileDataProvider.loadFileFromAssetGQL("graphql/PackagesAndAncillariesQueryGQL.txt")

        val createJsonObjectForVariables =
            packagesRequestBody.constructVariables(jsonObjectForVariablesPackages)

        jsonObject.put("query", query)
        jsonObject.put("variables", createJsonObjectForVariables)

        return wbGraphQLServicesApi.getPackagesAndAncillariesCloseoutGraphQL(query = jsonObject.toString())
            .onGraphQLError()
            .map { response -> return@map response.mapToPackagesAndAncillaryCloseoutDomain(packagesRequestBody.nightsNumber)
            }.doOnError { throwable ->
                println("GraphQl not able to connect: " + throwable.localizedMessage)
            }
    }

    override suspend fun getPackagesAndAncillariesCloseoutInfo(input: HotelPackagesRequestBody) = flow {
        jsonObject.apply {
            put("query", fileDataProvider.loadFileFromAssetGQL("graphql/PackagesAndAncillariesQueryGQL.txt"))
            put("variables", input.constructVariables(jsonObjectForVariablesPackagesAndAncillaryCloseout))
        }

        runCatching {
            nonRxGraphQLServicesApi.getPackagesAndAncillariesCloseout(jsonObject.toString())
        }.onFailure { error ->
            emit(Result.Error(error.handleError()))
        }.onSuccess { packagesAndAncillariesCloseoutData ->
            packagesAndAncillariesCloseoutData.errors?.firstOrNull()?.let { error ->
                Log.e(TAG, "Error: $error")
                emit(Result.Error(DataError.Network.BaseError(error.message)))
            } ?: run {
                emit(Result.Success(packagesAndAncillariesCloseoutData.mapToPackagesAndAncillaryCloseoutDomain(input.nightsNumber)))
            }

            Log.w(TAG, "packagesAndAncillariesCloseoutData: $packagesAndAncillariesCloseoutData")
        }
    }.flowOn(dispatchers.io)


    override fun confirmAmendLogic(confirmAmendLogicRequestBody: ConfirmAmendLogicRequestBody): Single<ConfirmAmendLogicDomain> {
        val mutation = fileDataProvider.loadFileFromAssetGQL("graphql/ConfirmAmendLogicMutationGQL.txt")
        val createJsonObjectForVariables =
            confirmAmendLogicRequestBody.constructVariables(jsonObjectForVariablesConfirmAmend)

        jsonObject.put("query", mutation)
        jsonObject.put("variables", createJsonObjectForVariables)

        return wbGraphQLServicesApi.confirmAmendGraphQL(query = jsonObject.toString())
            .onGraphQLError()
            .map { response -> return@map response.mapToConfirmAmendLogicGQL()
            }.doOnError { throwable ->
                println("GraphQl not able to connect: " + throwable.localizedMessage)
            }
    }
    
    override fun amendEditRoom(
        amendEditRoomRequestBody: AmendEditRoomRequestBody,
        token: String?
    ): Single<TempBookingRefDomain> {
        val mutation =
            fileDataProvider.loadFileFromAssetGQL("graphql/AmendEditRoomMutationGQL.txt")

        val createJsonObjectForVariables =
            amendEditRoomRequestBody.constructVariables(jsonObjectForVariablesAmendEditRoom)

        jsonObject.put("query", mutation)
        jsonObject.put("variables", createJsonObjectForVariables)

        return wbGraphQLServicesApi.amendEditRoomGraphQL(
            bearerToken = token?.let { "$AUTHORIZATION_BEARER $token" },
            query = jsonObject.toString()
        )
            .onGraphQLError()
            .map { response ->
                return@map response.mapToAmendEditRoomGQL()
            }.doOnError { throwable ->
                println("GraphQl not able to connect: " + throwable.localizedMessage)
            }
    }

    override fun removeRoom(removeRoomRequestBody: RemoveRoomRequestBody): Single<TempBookingRefDomain> {
        val mutation =
            fileDataProvider.loadFileFromAssetGQL("graphql/RemoveRoomMutationGQL.txt")

        val createJsonObjectForVariables =
            removeRoomRequestBody.constructVariables(jsonObjectForVariablesRemoveRoom)

        jsonObject.put("query", mutation)
        jsonObject.put("variables", createJsonObjectForVariables)

        return wbGraphQLServicesApi.removeRoomGraphQL(
            query = jsonObject.toString()
        )
            .onGraphQLError()
            .map { response ->
                return@map response.mapToRemoveRoomGQL()
            }.doOnError { throwable ->
                println("GraphQl not able to connect: " + throwable.localizedMessage)
            }
    }

    override fun updateReservationPackagesByReservation(
        updateReservationWithAncillariesRequestBody: UpdateReservationWithAncillariesRequestBody
    ): Single<UpdateReservationPackagesResponseDomain> {

        val mutation =
            fileDataProvider.loadFileFromAssetGQL("graphql/UpdateReservationPackagesByReservationMutationGQL.txt")
        val createVariableJsonObj = updateReservationWithAncillariesRequestBody.constructVariables(jsonObjectForVariablesUpdateReservationPackagesByReservation)

        jsonObject.put("query", mutation)
        jsonObject.put("variables", createVariableJsonObj)

        return wbGraphQLServicesApi.updateReservationPackagesByReservationGQL(
            query = jsonObject.toString())
            .onGraphQLError()
            .map { response ->
                return@map response.mapToUpdateReservationPackagesByReservationGQL()
            }.doOnError { throwable ->
                println("GraphQl not able to connect: " + throwable.localizedMessage)
            }
    }

    override fun amendSummary(amendSummaryRequestBody: AmendSummaryRequestBody): Single<AmendSummaryDomain> {
        val query = fileDataProvider.loadFileFromAssetGQL("graphql/AmendSummaryQueryGQL.txt")

        val createJsonObjectForVariables =
            amendSummaryRequestBody.constructVariables(jsonObjectForVariablesAmendSummary)

        jsonObject.put("query", query)
        jsonObject.put("variables", createJsonObjectForVariables)

        return wbGraphQLServicesApi.amendSummaryGraphQL(query = jsonObject.toString())
            .onGraphQLError()
            .map { response -> return@map response.mapToAmendSummaryGQL()
            }.doOnError { throwable ->
                println("GraphQl not able to connect: " + throwable.localizedMessage)
            }
    }

    private fun CopyBookingRequestBody.constructVariables(jsonObject: JSONObject): JSONObject {
        return jsonObject.apply {
            put("copyBookingCriteria", JSONObject().apply {
                put("originalBasketReference", this@constructVariables.originalBasketReference)
                put("token", this@constructVariables.token)
                put(
                    "bookingChannel",
                    JSONObject(Gson().toJson(this@constructVariables.bookingChannel))
                )
            })
        }
    }

    private fun HotelPackagesRequestBody.constructVariables(jsonObject: JSONObject): JSONObject {
        return jsonObject.apply {
            put("packagesCriteria", JSONObject().apply {
                put("hotelId", hotelId)
                put("startDate", startDate)
                put("endDate", endDate)
                put("adultsNumber", adultsNumber)
                put("childrenNumber", childrenNumber)
                put("nightsNumber", nightsNumber)
                put("language", language)
                put("country", country)
                put("bookingFlowId", bookingFlowId)
                put("basketReferenceId", basketReferenceId)
                put("channel", channel)
            })
            put("country", country)
            put("hotelId", hotelId)
            put("language", language)
        }
    }

    private fun AddNewRoomRequestBody.constructVariables(jsonObject: JSONObject): JSONObject {
        return jsonObject.apply {
            put("addNewRoomCriteria", JSONObject().apply {
                put("bookingChannel", JSONObject(Gson().toJson(this@constructVariables.bookingChannel)))
                put("tempBookingRef", this@constructVariables.tempBookingRef)
                put("roomOccupancy", JSONObject(Gson().toJson(this@constructVariables.roomOccupancy)))
                put("leadGuest", JSONObject(Gson().toJson(this@constructVariables.leadGuest)))
                put("roomType", this@constructVariables.roomType)
                put("token", this@constructVariables.token)
                put("ratePlanCode", this@constructVariables.ratePlanCode)
            })
        }
    }

    private fun ConfirmAmendLogicRequestBody.constructVariables(jsonObject: JSONObject): JSONObject {
        return jsonObject.apply {
            put("confirmAmendLogicCriteria", JSONObject().apply {
                put("bookingChannel", JSONObject(Gson().toJson(this@constructVariables.bookingChannel)))
                put("tempBookingRef", this@constructVariables.tempBookingRef)
                put("originalBookingRef", this@constructVariables.originalBookingRef)
                put("token", this@constructVariables.token)
                put("environment", this@constructVariables.environment)
                put("paymentOptionSelected", this@constructVariables.paymentOptionSelected)
            })
        }
    }

    private fun AmendEditRoomRequestBody.constructVariables(jsonObject: JSONObject): JSONObject {
        return jsonObject.apply {
            put("editRoomCriteria", JSONObject().apply {
                put("tempBookingRef", this@constructVariables.tempBookingRef)
                put("reservationId", this@constructVariables.reservationId)
                put("bookingChannel", JSONObject(Gson().toJson(this@constructVariables.bookingChannel)))
                put("roomOccupancy", JSONObject(Gson().toJson(this@constructVariables.roomOccupancy)))
                put("leadGuest", JSONObject().apply {
                    put("title", this@constructVariables.leadGuest.title)
                    put("firstName", this@constructVariables.leadGuest.firstName)
                    put("lastName", this@constructVariables.leadGuest.lastName)
                    put("emailAddress", this@constructVariables.leadGuest.emailAddress)
                })
                put("roomType", this@constructVariables.roomType)
                put("token", this@constructVariables.token)
            })
        }
    }

    private fun RemoveRoomRequestBody.constructVariables(jsonObject: JSONObject): JSONObject {
        return jsonObject.apply {
            put("tempBookingRef", this@constructVariables.tempBookingRef)
            put("reservationId", this@constructVariables.reservationId)
            put("token", this@constructVariables.token)
            put("bookingChannel", JSONObject(Gson().toJson(this@constructVariables.bookingChannel)))
        }
    }

    private fun ChangeBookingDatesRequestBody.constructVariables(jsonObject: JSONObject) : JSONObject {
        return jsonObject.apply {
            put("amendStayDatesCriteria", JSONObject().apply {
                put("tempBookingRef", this@constructVariables.tempBookingRef)
                put("newStartDate", this@constructVariables.newStartDate)
                put("newEndDate", this@constructVariables.newEndDate)
                put("token", this@constructVariables.token)
                put("bookingChannel", JSONObject(Gson().toJson(this@constructVariables.bookingChannel)))
            })
        }
    }

    private fun UpdateReservationWithAncillariesRequestBody.constructVariables(jsonObject: JSONObject) : JSONObject {
        return jsonObject.apply {
            put("updateReservationPackagesRequest", JSONObject().apply {
                put("basketReferenceId", this@constructVariables.basketReferenceId)
                put("hotelId", this@constructVariables.hotelId)
                put("arrivalDate", this@constructVariables.arrival)
                put("departureDate", this@constructVariables.departure)
                put("roomsSelections", JSONArray(Gson().toJson(this@constructVariables.roomsSelections)))
                put("previousRoomsSelections", JSONArray(Gson().toJson(this@constructVariables.previousRoomsSelections)))
            })
        }
    }

    private fun AmendSummaryRequestBody.constructVariables(jsonObject: JSONObject): JSONObject {
        return jsonObject.apply {
            put("copyBasketRef", this@constructVariables.tempBasketReference)
            put("originalBasketRef", this@constructVariables.originalBasketReference)
            put("token", this@constructVariables.token)
            put("country", this@constructVariables.country)
            put("bookingChannel", JSONObject(Gson().toJson(this@constructVariables.bookingChannel)))
        }
    }
}