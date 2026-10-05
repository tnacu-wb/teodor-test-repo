package com.whitbread.premierinn.domain.graphql.roomClassConfig.usecase

import com.whitbread.premierinn.domain.graphql.requestBodyModels.RoomClassConfigRequestBody
import com.whitbread.premierinn.domain.graphql.roomClassConfig.entity.RoomClassConfig
import com.whitbread.premierinn.domain.graphql.roomClassConfig.repository.GraphQLRoomClassConfigRepository
import io.reactivex.Observable
import javax.inject.Inject

class GetRoomClassConfigUseCase @Inject constructor(
    private val graphQLRoomClassConfigRepository: GraphQLRoomClassConfigRepository
) {
    fun fetchRoomClassConfig(input: RoomClassConfigRequestBody): Observable<List<RoomClassConfig>> {
        return graphQLRoomClassConfigRepository.roomClassConfigObservable(input)
    }
}