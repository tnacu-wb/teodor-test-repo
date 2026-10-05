package com.whitbread.premierinn.data.alternativeroom.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.whitbread.premierinn.data.remote.TwinRoomInfoApi
import com.whitbread.premierinn.domain.alternativeroom.entity.TwinRoomInfoItem
import com.whitbread.premierinn.domain.alternativeroom.repository.AlternativeRoomRepository
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository.Key.TWIN_ROOM_INFO
import io.reactivex.Single
import javax.inject.Inject

class AlternativeRoomRepositoryImpl @Inject constructor(val resourceRepository: ContentManagedResourceRepository,
                                    val gson: Gson
) : AlternativeRoomRepository {

    override fun getTwinRoomInfo(): Single<List<TwinRoomInfoItem>> {
        return resourceRepository.getStringSingle(TWIN_ROOM_INFO.value)
            .map { jsonString ->
                if (jsonString.isNotEmpty()) {
                    val type = object : TypeToken<List<TwinRoomInfoApi>>() {}.type
                    gson.fromJson<List<TwinRoomInfoApi>>(jsonString, type)
                } else emptyList()
            }.toObservable()
            .flatMapIterable { it }
            .map { it.toDomain() }
            .toList()
    }
}