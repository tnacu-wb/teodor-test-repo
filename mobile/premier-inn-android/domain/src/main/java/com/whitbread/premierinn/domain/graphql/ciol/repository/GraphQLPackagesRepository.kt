package com.whitbread.premierinn.domain.graphql.ciol.repository

import com.whitbread.premierinn.domain.error.DataError
import com.whitbread.premierinn.domain.graphql.ciol.usecase.UpdateReservationPackagesResult
import com.whitbread.premierinn.domain.graphql.hdp.entity.PackagesPackagesDomain
import com.whitbread.premierinn.domain.graphql.requestBodyModels.HotelPackagesRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.UpdateReservationPackagesRequestBody
import com.whitbread.premierinn.domain.result.Result
import kotlinx.coroutines.flow.Flow

typealias GetPackagesResult = Result<PackagesPackagesDomain, DataError.Network>

interface GraphQLPackagesRepository {
    suspend fun getPackages(input: HotelPackagesRequestBody): Flow<GetPackagesResult>

    suspend fun updateReservationPackages(input: UpdateReservationPackagesRequestBody): Flow<UpdateReservationPackagesResult>
}
