package com.whitbread.premierinn.data.alternativeroom.repository

import com.whitbread.premierinn.data.remote.TwinRoomInfoApi
import com.whitbread.premierinn.domain.alternativeroom.entity.TwinRoomInfoItem


fun TwinRoomInfoApi.toDomain(): TwinRoomInfoItem {
    return TwinRoomInfoItem(
        lettingType = lettingType,
        label = label,
        description = description,
        image = image
    )
}
