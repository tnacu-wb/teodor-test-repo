package com.whitbread.premierinn.domain.graphql.ciol.repository

import com.whitbread.premierinn.domain.booking.entity.HotelPreferenceDomain
import com.whitbread.premierinn.domain.error.DataError
import com.whitbread.premierinn.domain.graphql.ciol.usecase.UpdateReservationPreferencesResult
import com.whitbread.premierinn.domain.graphql.requestBodyModels.UpdateReservationPreferencesRequestBody
import com.whitbread.premierinn.domain.result.Result
import kotlinx.coroutines.flow.Flow

typealias HotelPreferencesResult = Result<List<HotelPreferenceDomain>?, DataError.Network>

interface GraphQLHotelPreferencesRepository {

    suspend fun getHotelPreferences(hotelId: String, language: String): Flow<HotelPreferencesResult>

    suspend fun updateReservationPreferences(input: UpdateReservationPreferencesRequestBody): Flow<UpdateReservationPreferencesResult>
}
