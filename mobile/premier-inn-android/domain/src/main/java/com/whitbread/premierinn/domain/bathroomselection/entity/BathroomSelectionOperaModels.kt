package com.whitbread.premierinn.domain.bathroomselection.entity

import com.whitbread.premierinn.domain.common.RoomTypeCode

data class BathroomSelectionInfo(val bathroomAvailability: Map<RoomTypeCode, Int>,
                                 val roomSelections: List<RoomBathroomChoices>) {

    fun getRoomTypesForAvailableBathroomType(bathroomType: BathroomType): List<RoomTypeCode> {
        return bathroomAvailability
            .entries
            .filter {
                it.key.bathroomType == bathroomType
            }
            .filter { it.value > 0 }
            .map { it.key }
    }
}
