package com.whitbread.premierinn.landing

import com.whitbread.premierinn.data.common.toRoomString
import com.whitbread.premierinn.domain.common.RoomCriteria

object SearchPayloadMapper {

    fun toSearchPayload(state: State): SearchPayload {
        return SearchPayload(
                arrivalDate = state.arrival,
                departureDate = state.departure,
                placeName = state.location!!.searchText(),
                latitude = state.location.location().latitude(),
                longitude = state.location.location().longitude(),
                adults = adults(state.roomCriteria),
                children = children(state.roomCriteria),
                infants = infants(state.roomCriteria),
                cots = cots(state.roomCriteria),
                roomTypeCodes = roomTypeCodes(state.roomCriteria),
                roomsCount = state.roomCriteria.size)
    }

    private fun adults(roomCriteria: List<RoomCriteria>): List<Int> {
        return roomCriteria.map { room -> room.numberOfAdults }
    }

    private fun children(roomCriteria: List<RoomCriteria>): List<Int> {
        return roomCriteria.map { room -> room.numberOfChildren }
    }

    private fun infants(roomCriteria: List<RoomCriteria>): List<Int> {
        return roomCriteria.map { room -> room.numberOfInfants }
    }

    private fun cots(roomCriteria: List<RoomCriteria>): List<Boolean> {
        return roomCriteria.map { room -> room.includeCot }
    }

    private fun roomTypeCodes(roomCriteria: List<RoomCriteria>): List<String> {
        return roomCriteria.mapNotNull { room -> room.roomType.toRoomString() }
    }
}