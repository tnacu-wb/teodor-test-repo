package com.whitbread.premierinn.domain.graphql.roomClassConfig.repository

import com.whitbread.premierinn.domain.graphql.requestBodyModels.RoomClassConfigRequestBody
import com.whitbread.premierinn.domain.graphql.roomClassConfig.entity.RoomClassConfig
import io.reactivex.Observable

interface GraphQLRoomClassConfigRepository {
    fun roomClassConfigObservable(input: RoomClassConfigRequestBody): Observable<List<RoomClassConfig>>
}