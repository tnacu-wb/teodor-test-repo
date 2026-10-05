package com.whitbread.premierinn.domain.graphql.ciol.usecase

import com.whitbread.premierinn.domain.error.DataError
import com.whitbread.premierinn.domain.graphql.ciol.repository.GraphQLPackagesRepository
import com.whitbread.premierinn.domain.graphql.requestBodyModels.UpdateReservationPackagesRequestBody
import com.whitbread.premierinn.domain.result.Result
import javax.inject.Inject

typealias UpdateReservationPackagesResult = Result<Unit, DataError.Network>

class UpdateReservationPackagesUseCase @Inject constructor(
    private val packagesRepository: GraphQLPackagesRepository
) {
    suspend operator fun invoke(
        updateReservationPackagesRequestBody: UpdateReservationPackagesRequestBody
    ) = packagesRepository.updateReservationPackages(updateReservationPackagesRequestBody)
}
