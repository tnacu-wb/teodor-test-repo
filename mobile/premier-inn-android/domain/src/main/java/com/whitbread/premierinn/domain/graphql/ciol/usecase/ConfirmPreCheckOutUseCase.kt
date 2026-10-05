package com.whitbread.premierinn.domain.graphql.ciol.usecase

import com.whitbread.premierinn.domain.error.DataError
import com.whitbread.premierinn.domain.graphql.ciol.entity.PreCheckOutConfirmationDomain
import com.whitbread.premierinn.domain.graphql.ciol.repository.GraphQLPreCheckOutConfirmationRepository
import com.whitbread.premierinn.domain.result.Result
import javax.inject.Inject

typealias PreCheckOutConfirmationResult = Result<PreCheckOutConfirmationDomain?, DataError.Network>

class ConfirmPreCheckOutUseCase @Inject constructor(
        private val graphQLPreCheckOutConfirmationRepository: GraphQLPreCheckOutConfirmationRepository,
) {
    suspend operator fun invoke(basketReference: String) =
            graphQLPreCheckOutConfirmationRepository.confirmPreCheckOut(basketReference)
}
