package com.whitbread.premierinn.domain.graphql.summary.usecase

import com.whitbread.premierinn.domain.authentication.usecase.RefreshIdTokenAndRetryOnce
import com.whitbread.premierinn.domain.error.DataError
import com.whitbread.premierinn.domain.graphql.guestDetails.entity.CreateReservationGuestDomain
import com.whitbread.premierinn.domain.graphql.guestDetails.entity.PaymentMethodsWithDonationAndBookingConfirmationGQLDomain
import com.whitbread.premierinn.domain.graphql.requestBodyModels.CreateReservationGuestRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.CreateReservationRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.PaymentMethodsRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.SaveReservationWithAncillariesRequestBody
import com.whitbread.premierinn.domain.graphql.summary.entity.SaveReservationWithAncillariesDomain
import com.whitbread.premierinn.domain.graphql.summary.repository.GraphQLSummaryRepository
import com.whitbread.premierinn.domain.result.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

typealias GetCreateReservationsResult = Result<String, DataError.Network>
typealias GetSaveResWithAncillariesResult = Result<SaveReservationWithAncillariesDomain, DataError.Network>
typealias GetPaymentMethodsAndBookingConfirmationResult = Result<PaymentMethodsWithDonationAndBookingConfirmationGQLDomain, DataError.Network>
typealias GetCreateReservationGuestResult = Result<CreateReservationGuestDomain, DataError.Network>

class GraphQLSummaryUseCase @Inject constructor (
    private val graphQLSummaryRepository: GraphQLSummaryRepository,
    private val refreshIdTokenAndRetryOnce: RefreshIdTokenAndRetryOnce // coroutines
) {

    suspend fun createReservation(input: CreateReservationRequestBody): Flow<GetCreateReservationsResult> =
        graphQLSummaryRepository.createReservation(input)
            .retry(retries = 1) { cause ->
                refreshIdTokenAndRetryOnce(cause)
            }.catch {
                emit(Result.Error(DataError.Network.GraphQlError(it.message)))
            }.transform { result ->
                when (result) {
                    is Result.Error -> emit(result)
                    is Result.Success -> emit(
                        Result.Success(result.data)
                    )
                }
            }

    suspend fun saveResWithAncillaries(input: SaveReservationWithAncillariesRequestBody): Flow<GetSaveResWithAncillariesResult> {
        return graphQLSummaryRepository.saveReservationWithAncillaries(input)
    }

    suspend fun getPaymentMethodsAndBookingConfirmation(
        paymentInput: PaymentMethodsRequestBody, basketReference: String, country: String,
        language: String, bookingChannel: String
    ): Flow<GetPaymentMethodsAndBookingConfirmationResult> =
        graphQLSummaryRepository.getPaymentMethodsAndBookingConfirmation(
            paymentInput, basketReference, country, language, bookingChannel)
            .retry(retries = 1) { cause ->
                refreshIdTokenAndRetryOnce(cause)
            }.catch {
                emit(Result.Error(DataError.Network.GraphQlError(it.message)))
            }.transform { result ->
                when (result) {
                    is Result.Error -> emit(result)
                    is Result.Success -> emit(
                        Result.Success(result.data)
                    )
                }
            }


    suspend fun createReservationGuest(
        input: CreateReservationGuestRequestBody
    ): Flow<GetCreateReservationGuestResult> =
        graphQLSummaryRepository.createReservationGuest(input)
            .retry(retries = 1) { cause ->
                refreshIdTokenAndRetryOnce(cause)
            }.catch {
                emit(Result.Error(DataError.Network.GraphQlError(it.message ?: "Unknown error")))
            }.transform { result ->
                when (result) {
                    is Result.Error -> emit(result)
                    is Result.Success -> emit(Result.Success(result.data))
                }
            }
}