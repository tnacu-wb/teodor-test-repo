package com.whitbread.premierinn.criteria.model

import com.whitbread.premierinn.criteria.roomselector.Room
import com.whitbread.premierinn.criteria.view.CotState
import com.whitbread.premierinn.domain.common.RoomType

object RoomRules {

    fun apply(input: RoomConfiguration, forPreferences: Boolean): RoomConfiguration {
        val roomType = applyRulesToRoomSelection(
                adults = input.adults().value(),
                children = input.children().value(),
                isCotSelected = input.cot() == CotState.SELECTED,
                selectedRoomType = input.roomType(),
                forPreferences = forPreferences)
        val cotState = cotState(oldCotState = input.cot(), roomType = roomType)
        return RoomConfiguration.builder()
                .adults(input.adults())
                .children(input.children())
                .infants(input.infants())
                .cot(cotState)
                .roomType(roomType)
                .number(input.number())
                .autobuild()
    }

    private fun applyRulesToRoomSelection(adults: Int,
                                          children: Int,
                                          isCotSelected: Boolean,
                                          selectedRoomType: RoomType,
                                          forPreferences: Boolean): RoomType {
        val roomAvailabilityList = Room.createRoomAvailabilityList(adults, children, isCotSelected, forPreferences)
        return if (roomAvailabilityList[selectedRoomType] == Room.State.NOT_AVAILABLE) {
            Room.replaceUnavailableRoom(children)
        } else {
            selectedRoomType
        }
    }

    private fun cotState(oldCotState: CotState, roomType: RoomType): CotState {
        return if (roomType == RoomType.ACCESSIBLE) {
            CotState.NOT_AVAILABLE
        } else if (oldCotState == CotState.NOT_AVAILABLE) {
            CotState.NOT_SELECTED
        } else {
            oldCotState
        }
    }
}