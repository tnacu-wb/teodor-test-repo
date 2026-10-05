package com.whitbread.premierinn.domain.graphql.ciol.repository

import com.whitbread.premierinn.domain.graphql.ciol.usecase.PreCheckOutConfirmationResult
import kotlinx.coroutines.flow.Flow

interface GraphQLPreCheckOutConfirmationRepository {
    suspend fun confirmPreCheckOut(basketReference: String): Flow<PreCheckOutConfirmationResult>
}
