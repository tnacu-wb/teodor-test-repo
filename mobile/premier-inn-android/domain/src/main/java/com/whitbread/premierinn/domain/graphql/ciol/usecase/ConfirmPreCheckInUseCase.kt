package com.whitbread.premierinn.domain.graphql.ciol.usecase

import com.whitbread.premierinn.domain.error.DataError
import com.whitbread.premierinn.domain.graphql.ciol.entity.PreCheckInConfirmationDomain
import com.whitbread.premierinn.domain.graphql.ciol.repository.GraphQLPreCheckInConfirmationRepository
import com.whitbread.premierinn.domain.result.Result
import javax.inject.Inject

typealias PreCheckInConfirmationResult = Result<PreCheckInConfirmationDomain, DataError.Network>

class ConfirmPreCheckInUseCase @Inject constructor(
    private val graphQLPreCheckInConfirmationRepository: GraphQLPreCheckInConfirmationRepository,
) {
    suspend operator fun invoke(basketReferenceId: String, isPibaCnp: Boolean = false) =
        graphQLPreCheckInConfirmationRepository.confirmPreCheckIn(basketReferenceId, isPibaCnp)
}
