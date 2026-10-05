package com.whitbread.premierinn.alternativeroomselection

import com.whitbread.premierinn.domain.alternativeroom.entity.TwinRoomInfoItem
import com.whitbread.premierinn.domain.alternativeroom.repository.AlternativeRoomRepository
import io.reactivex.Single
import javax.inject.Inject

class GetTwinRoomInfoUseCase @Inject constructor(private val repository: AlternativeRoomRepository) {

    operator fun invoke(): Single<List<TwinRoomInfoItem>> {
        return repository.getTwinRoomInfo()
    }
}