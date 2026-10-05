package com.whitbread.premierinn.domain.common

enum class RoomType(val code: String) {
    SINGLE(SINGLE_ROOM_CODE),
    DOUBLE(DOUBLE_ROOM_CODE),
    TWIN(TWIN_ROOM_CODE),
    FAMILY(FAMILY_ROOM_CODE),
    ACCESSIBLE(ACCESSIBLE_ROOM_CODE),
    UNKNOWN(EMPTY_STRING_DOMAIN)
}