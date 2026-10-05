package com.whitbread.premierinn.domain.alternativeroom.repository

import com.whitbread.premierinn.domain.alternativeroom.entity.TwinRoomInfoItem
import io.reactivex.Single

interface AlternativeRoomRepository {
    fun getTwinRoomInfo() : Single<List<TwinRoomInfoItem>>
}