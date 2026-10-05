package com.whitbread.premierinn.domain.graphql.ciol.repository

import com.whitbread.premierinn.domain.graphql.ciol.usecase.AttachFileToReservationResult
import com.whitbread.premierinn.domain.graphql.requestBodyModels.AttachFileToReservationRequestBody
import kotlinx.coroutines.flow.Flow

interface GraphQLAttachFileToReservationRepository {
    suspend fun attachFileToReservation(input: AttachFileToReservationRequestBody): Flow<AttachFileToReservationResult>
}
