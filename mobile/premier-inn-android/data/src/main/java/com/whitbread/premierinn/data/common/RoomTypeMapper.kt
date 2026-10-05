package com.whitbread.premierinn.data.common

import com.whitbread.premierinn.data.remote.ACCESSIBLE_KEY
import com.whitbread.premierinn.data.remote.DOUBLE_KEY
import com.whitbread.premierinn.data.remote.FAMILY_KEY
import com.whitbread.premierinn.data.remote.SINGLE_KEY
import com.whitbread.premierinn.data.remote.TWIN_KEY
import com.whitbread.premierinn.domain.common.RoomType
import com.whitbread.premierinn.domain.common.RoomType.ACCESSIBLE
import com.whitbread.premierinn.domain.common.RoomType.DOUBLE
import com.whitbread.premierinn.domain.common.RoomType.FAMILY
import com.whitbread.premierinn.domain.common.RoomType.SINGLE
import com.whitbread.premierinn.domain.common.RoomType.TWIN
import com.whitbread.premierinn.domain.common.RoomType.UNKNOWN

fun RoomType.toRoomString(): String? {
    return when (this) {
        SINGLE -> SINGLE_KEY
        DOUBLE -> DOUBLE_KEY
        TWIN -> TWIN_KEY
        FAMILY -> FAMILY_KEY
        ACCESSIBLE -> ACCESSIBLE_KEY
        else -> null
    }
}

fun String.toRoomType(): RoomType {
    return when (this) {
        SINGLE_KEY -> SINGLE
        DOUBLE_KEY -> DOUBLE
        TWIN_KEY -> TWIN
        ACCESSIBLE_KEY -> ACCESSIBLE
        FAMILY_KEY -> FAMILY
        else -> UNKNOWN
    }
}