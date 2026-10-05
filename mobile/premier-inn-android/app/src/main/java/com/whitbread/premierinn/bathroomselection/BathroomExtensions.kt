package com.whitbread.premierinn.bathroomselection

import com.whitbread.premierinn.accessiblebathroomselection.AccessibleRoomSizeOption
import com.whitbread.premierinn.domain.common.DISABLED_LOWERED_BATH_TWIN_LETTING_CODE_PREFIX
import com.whitbread.premierinn.domain.common.DISABLED_WET_ROOM_LETTING_CODE_TWIN_PREFIX
import com.whitbread.premierinn.domain.common.LettingType

fun LettingType.accessibleRoomSize(): AccessibleRoomSizeOption {
    return if (code.startsWith(DISABLED_LOWERED_BATH_TWIN_LETTING_CODE_PREFIX)
            || code.startsWith(DISABLED_WET_ROOM_LETTING_CODE_TWIN_PREFIX)) {
        AccessibleRoomSizeOption.TWIN
    } else {
        AccessibleRoomSizeOption.DOUBLE
    }
}