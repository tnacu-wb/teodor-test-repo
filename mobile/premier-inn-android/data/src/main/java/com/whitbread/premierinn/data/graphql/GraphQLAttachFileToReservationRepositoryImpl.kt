package com.whitbread.premierinn.data.graphql

import android.util.Log
import com.whitbread.premierinn.data.common.FileDataProvider
import com.whitbread.premierinn.data.graphql.error.handleError
import com.whitbread.premierinn.data.graphql.mapper.toAttachFileToReservationDomain
import com.whitbread.premierinn.data.remote.graphql.NonRxGraphQLServicesApi
import com.whitbread.premierinn.domain.common.AppDispatchers
import com.whitbread.premierinn.domain.error.DataError
import com.whitbread.premierinn.domain.graphql.ciol.repository.GraphQLAttachFileToReservationRepository
import com.whitbread.premierinn.domain.graphql.requestBodyModels.AttachFileToReservationRequestBody
import com.whitbread.premierinn.domain.result.Result
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.json.JSONObject
import javax.inject.Inject

private val TAG = GraphQLAttachFileToReservationRepositoryImpl::class.simpleName
private const val ATTACH_FILE_STATUS_ERROR = "Error"

class GraphQLAttachFileToReservationRepositoryImpl @Inject constructor(
    private val nonRxGraphQLServicesApi: NonRxGraphQLServicesApi,
    private val fileDataProvider: FileDataProvider,
    private val jsonObject: JSONObject,
    private val dispatchers: AppDispatchers
) : GraphQLAttachFileToReservationRepository {

    override suspend fun attachFileToReservation(input: AttachFileToReservationRequestBody) = flow {
        jsonObject.apply {
            put("query", fileDataProvider.loadFileFromAssetGQL("graphql/AttachFileToReservationMutationGQL.txt"))
            put("variables", JSONObject().apply {
                put("reservationId", input.reservationId)
                put("hotelId", input.hotelId)
                put("fileAttachment", input.fileAttachment)
                put("global", input.global)
                put("overwriteExistingFile", input.overwriteExistingFile)
                put("description", input.description)
                put("fileName", input.fileName)
            })
        }

        runCatching {
            nonRxGraphQLServicesApi.attachFileToReservation(jsonObject.toString())
        }.onFailure { error ->
            emit(Result.Error(error.handleError()))
        }.onSuccess { attachFileToReservation ->
            attachFileToReservation.errors?.firstOrNull()?.let { error ->
                Log.e(TAG, "Error: $error")
                emit(Result.Error(DataError.Network.BaseError(error.message)))
            } ?: run {
                if (ATTACH_FILE_STATUS_ERROR.equals(attachFileToReservation.data?.attachFileToReservation?.status, true)) {
                    emit(Result.Error(DataError.Network.BaseError(attachFileToReservation.data?.attachFileToReservation?.message)))
                } else {
                    emit(Result.Success(attachFileToReservation.data?.attachFileToReservation?.toAttachFileToReservationDomain()))
                }
            }
        }
    }.flowOn(dispatchers.io)
}
