package com.whitbread.premierinn.domain.graphql.ciol.repository

import com.whitbread.premierinn.domain.graphql.ciol.usecase.PreCheckInConfirmationResult
import kotlinx.coroutines.flow.Flow

interface GraphQLPreCheckInConfirmationRepository {
    suspend fun confirmPreCheckIn(basketReferenceId: String, isPibaCnp: Boolean): Flow<PreCheckInConfirmationResult>
}
