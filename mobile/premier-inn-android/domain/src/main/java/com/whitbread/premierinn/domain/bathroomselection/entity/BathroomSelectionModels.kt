package com.whitbread.premierinn.domain.bathroomselection.entity

import com.whitbread.premierinn.domain.common.LettingType

data class RoomBathroomChoices(val roomId: Int, val selectedLettingType: LettingType, val compatibleBathrooms: List<LettingType>) {
    val unselectedCompatibleBathrooms: List<LettingType>
        get() = compatibleBathrooms.filter { it != selectedLettingType }
}

enum class BathroomType {
    WET_ROOM, LOWERED_BATH
}
