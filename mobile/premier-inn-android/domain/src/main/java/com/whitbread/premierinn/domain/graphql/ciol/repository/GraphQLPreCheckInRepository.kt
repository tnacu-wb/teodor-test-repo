package com.whitbread.premierinn.domain.graphql.ciol.repository

import com.whitbread.premierinn.domain.graphql.ciol.usecase.PreCheckInResult
import com.whitbread.premierinn.domain.graphql.requestBodyModels.PreCheckInRequestBody
import kotlinx.coroutines.flow.Flow

interface GraphQLPreCheckInRepository {
    suspend fun preCheckIn(input: PreCheckInRequestBody): Flow<PreCheckInResult>
}
