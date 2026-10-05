package com.whitbread.premierinn.domain.graphql.ciol.usecase

import com.whitbread.premierinn.domain.booking.entity.BookingConfirmation
import com.whitbread.premierinn.domain.common.AppDispatchers
import com.whitbread.premierinn.domain.error.DomainError
import com.whitbread.premierinn.domain.graphql.bookingDetails.repository.GraphQLBookingDetailsRepository
import com.whitbread.premierinn.domain.result.Result
import com.whitbread.premierinn.domain.utils.await
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

typealias BookingConfirmationResult = Result<BookingConfirmation, DomainError>

class GetBookingConfirmationUseCase @Inject constructor(
    private val graphQLBookingDetailsRepository: GraphQLBookingDetailsRepository,
    private val dispatchers: AppDispatchers
) {

    operator fun invoke(
        uuidBasketReference: String,
        country: String,
        language: String
    ) = flow<BookingConfirmationResult> {
        try {
            val bookingConfirmation = graphQLBookingDetailsRepository.bookingConfirmationForFindBooking(
                uuidBasketReference,
                country,
                language
            ).await()
            emit(Result.Success(bookingConfirmation))
        } catch (error: Exception) {
            emit(Result.Error(DomainError.GenericError()))
        }
    }.flowOn(dispatchers.io)
}