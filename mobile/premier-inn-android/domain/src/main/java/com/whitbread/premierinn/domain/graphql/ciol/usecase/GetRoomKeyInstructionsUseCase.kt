package com.whitbread.premierinn.domain.graphql.ciol.usecase

import com.whitbread.premierinn.domain.common.AppDispatchers
import com.whitbread.premierinn.domain.error.DataError
import com.whitbread.premierinn.domain.graphql.bookingDetails.entity.RoomKeyInstructionsDomain
import com.whitbread.premierinn.domain.graphql.bookingDetails.repository.GraphQLBookingDetailsRepository
import com.whitbread.premierinn.domain.graphql.requestBodyModels.CategoryLabelsRequestBody
import com.whitbread.premierinn.domain.result.Result
import com.whitbread.premierinn.domain.utils.await
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

typealias GetRoomKeyInstructionsResult = Result<RoomKeyInstructionsDomain, DataError.Network>
class GetRoomKeyInstructionsUseCase @Inject constructor(
    private val graphQLBookingDetailsRepository: GraphQLBookingDetailsRepository,
    private val dispatchers: AppDispatchers
) {
    operator fun invoke(categoryLabelsRequestBody: CategoryLabelsRequestBody) = flow<GetRoomKeyInstructionsResult> {
        runCatching {
            graphQLBookingDetailsRepository.getRoomKeyInstructions(categoryLabelsRequestBody).await()
        }.onSuccess { result ->
            emit(Result.Success(result))
        }.onFailure { error ->
            emit(Result.Error(DataError.Network.GraphQlError(error.message)))
        }
    }.flowOn(dispatchers.io)
}
