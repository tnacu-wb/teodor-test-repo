package com.whitbread.premierinn.domain.graphql.summary.repository

import com.whitbread.premierinn.domain.graphql.requestBodyModels.CreateReservationGuestRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.CreateReservationRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.PaymentMethodsRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.SaveReservationWithAncillariesRequestBody
import com.whitbread.premierinn.domain.graphql.summary.usecase.GetCreateReservationGuestResult
import com.whitbread.premierinn.domain.graphql.summary.usecase.GetCreateReservationsResult
import com.whitbread.premierinn.domain.graphql.summary.usecase.GetPaymentMethodsAndBookingConfirmationResult
import com.whitbread.premierinn.domain.graphql.summary.usecase.GetSaveResWithAncillariesResult
import kotlinx.coroutines.flow.Flow

interface GraphQLSummaryRepository {

    suspend fun createReservation(input: CreateReservationRequestBody): Flow<GetCreateReservationsResult>

    suspend fun saveReservationWithAncillaries(input: SaveReservationWithAncillariesRequestBody): Flow<GetSaveResWithAncillariesResult>

    suspend fun getPaymentMethodsAndBookingConfirmation(paymentInput: PaymentMethodsRequestBody,
                                                        basketReference: String, country: String,
                                                        language: String, bookingChannel: String): Flow<GetPaymentMethodsAndBookingConfirmationResult>

    suspend fun createReservationGuest(input: CreateReservationGuestRequestBody): Flow<GetCreateReservationGuestResult>
}