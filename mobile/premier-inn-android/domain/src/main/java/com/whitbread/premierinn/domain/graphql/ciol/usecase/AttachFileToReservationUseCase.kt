package com.whitbread.premierinn.domain.graphql.ciol.usecase

import com.whitbread.premierinn.domain.error.DataError
import com.whitbread.premierinn.domain.graphql.ciol.entity.AttachFileToReservationDomain
import com.whitbread.premierinn.domain.graphql.ciol.repository.GraphQLAttachFileToReservationRepository
import com.whitbread.premierinn.domain.graphql.requestBodyModels.AttachFileToReservationRequestBody
import com.whitbread.premierinn.domain.result.Result
import javax.inject.Inject

typealias AttachFileToReservationResult = Result<AttachFileToReservationDomain?, DataError.Network>

class AttachFileToReservationUseCase @Inject constructor(
    private val attachFileToReservationRepository: GraphQLAttachFileToReservationRepository
) {

    suspend operator fun invoke(attachFileToReservationRequestBody: AttachFileToReservationRequestBody) =
        attachFileToReservationRepository.attachFileToReservation(attachFileToReservationRequestBody)
}
