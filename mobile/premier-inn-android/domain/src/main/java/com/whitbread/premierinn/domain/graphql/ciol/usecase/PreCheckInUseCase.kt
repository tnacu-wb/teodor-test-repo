package com.whitbread.premierinn.domain.graphql.ciol.usecase

import com.whitbread.premierinn.domain.error.DataError
import com.whitbread.premierinn.domain.graphql.ciol.entity.PreCheckInStatusDomain
import com.whitbread.premierinn.domain.graphql.ciol.repository.GraphQLPreCheckInRepository
import com.whitbread.premierinn.domain.graphql.requestBodyModels.PreCheckInRequestBody
import com.whitbread.premierinn.domain.result.Result
import javax.inject.Inject

typealias PreCheckInResult = Result<PreCheckInStatusDomain?, DataError.Network>

class PreCheckInUseCase @Inject constructor(
    private val preCheckInRepository: GraphQLPreCheckInRepository
) {

    suspend operator fun invoke(input: PreCheckInRequestBody) = preCheckInRepository.preCheckIn(input)
}
