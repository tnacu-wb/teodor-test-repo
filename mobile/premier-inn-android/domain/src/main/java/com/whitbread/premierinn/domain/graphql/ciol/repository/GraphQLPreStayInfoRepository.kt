package com.whitbread.premierinn.domain.graphql.ciol.repository

import com.whitbread.premierinn.domain.error.DataError
import com.whitbread.premierinn.domain.graphql.requestBodyModels.CreateReservationGuestRegCardRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.UpdatePreStayInfoRequestBody
import com.whitbread.premierinn.domain.result.Result
import kotlinx.coroutines.flow.Flow

typealias UpdatePreStayInfoResult = Result<Unit, DataError.Network>
typealias CreateReservationGuestForRegCardResult = Result<Unit, DataError.Network>

interface GraphQLPreStayInfoRepository {

    fun updatePreStayInfo(
        token: String?,
        input: UpdatePreStayInfoRequestBody
    ): Flow<UpdatePreStayInfoResult>

    fun createReservationGuestForRegCard(
        token: String?,
        input: CreateReservationGuestRegCardRequestBody
    ): Flow<CreateReservationGuestForRegCardResult>
}
