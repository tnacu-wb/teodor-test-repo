package com.whitbread.premierinn.roomcriteria.viewmodel

import com.whitbread.premierinn.domain.common.RoomCriteria
import com.whitbread.premierinn.domain.common.RoomType
import com.whitbread.premierinn.domain.roomcriteria.RoomCriteriaOrderedCollection
import com.whitbread.premierinn.domain.roomcriteria.compatibleSortedRoomTypes

data class RoomCriteriaState(private val roomCriteriaCollection: RoomCriteriaOrderedCollection) {

    fun getRoom(roomId: Int): RoomCriteria {
        return checkNotNull(roomCriteriaCollection[roomId]) { "Room $roomId does not exist" }
    }

    fun getRooms(): List<RoomCriteria> {
        return roomCriteriaCollection.map { it.value }
    }

    fun getRoomIds(): List<Int> = roomCriteriaCollection.roomIds()

    fun getNumberOfRooms(): Int = roomCriteriaCollection.totalNumberOfRooms()

    fun getAdultsTotal(): Int = roomCriteriaCollection.getAdultsTotal()

    fun getChildrenTotal(): Int = roomCriteriaCollection.getChildrenTotal()

    fun getInfantsTotal(): Int = roomCriteriaCollection.getInfantsTotal()

    fun hasMoreThanOneRoom(): Boolean = !roomCriteriaCollection.minRoomNumber()

    fun hasMaxRooms(): Boolean = roomCriteriaCollection.maxRoomRoomNumber()

    fun copyWithAdultsIncremented(roomId: Int): RoomCriteriaState {
        val room = getRoom(roomId)
        return copyWithUpdatedRoom(roomId, room.copy(numberOfAdults = room.numberOfAdults.inc()))
    }

    fun copyWithAdultsDecremented(roomId: Int): RoomCriteriaState {
        val room = getRoom(roomId)
        return copyWithUpdatedRoom(roomId, room.copy(numberOfAdults = room.numberOfAdults.dec()))
    }

    fun copyWithChildrenIncremented(roomId: Int): RoomCriteriaState {
        val room = getRoom(roomId)
        return copyWithUpdatedRoom(roomId, room.copy(numberOfChildren = room.numberOfChildren.inc()))
    }

    fun copyWithChildrenDecremented(roomId: Int): RoomCriteriaState {
        val room = getRoom(roomId)
        return copyWithUpdatedRoom(roomId, room.copy(numberOfChildren = room.numberOfChildren.dec()))
    }

    fun copyWithInfantsIncremented(roomId: Int): RoomCriteriaState {
        val room = getRoom(roomId)
        return copyWithUpdatedRoom(roomId, room.copy(numberOfInfants = room.numberOfInfants.inc()))
    }

    fun copyWithInfantsDecremented(roomId: Int): RoomCriteriaState {
        val room = getRoom(roomId)
        return copyWithUpdatedRoom(roomId, room.copy(numberOfInfants = room.numberOfInfants.dec(), includeCot = false))
    }

    fun copyWithCotToggled(roomId: Int, enabled: Boolean): RoomCriteriaState {
        val room = getRoom(roomId)
        return copyWithUpdatedRoom(roomId, room.copy(includeCot = enabled))
    }

    fun copyWithRoomTypeChanged(roomId: Int, roomType: RoomType): RoomCriteriaState {
        val room = getRoom(roomId)
        return this.copy(roomCriteriaCollection = updateRoomMap(roomId, room.copy(roomType = roomType)))
    }

    fun copyWithRoomAdded(): RoomCriteriaState {
        return this.copy(roomCriteriaCollection = roomCriteriaCollection.addRoomInOrder(
                RoomCriteria.createWithDefaults()))
    }

    fun copyWithRoomRemoved(roomId: Int): RoomCriteriaState {
        return this.copy(roomCriteriaCollection = roomCriteriaCollection.removeRoom(roomId))
    }

    fun copyWithUpdatedRoom(roomId: Int, updatedRoom: RoomCriteria): RoomCriteriaState {
        val compatibleTypes = updatedRoom.compatibleSortedRoomTypes()
        if (updatedRoom.roomType !in compatibleTypes) {
            return this.copy(roomCriteriaCollection = updateRoomMap(roomId, updatedRoom.copy(roomType = compatibleTypes.first())))
        }
        return this.copy(roomCriteriaCollection = updateRoomMap(roomId, updatedRoom))
    }

    private fun updateRoomMap(roomId: Int, roomCriteria: RoomCriteria): RoomCriteriaOrderedCollection {
        return roomCriteriaCollection.updateRoom(roomId, roomCriteria)
    }

    fun getRoomNumber(roomId: Int): Int {
        return roomCriteriaCollection.getRoomOrderedNumber(roomId)
    }

    companion object {
        fun createFromCriteriaList(list: List<RoomCriteria>? = null): RoomCriteriaState {
            return if (list != null) {
                RoomCriteriaState(roomCriteriaCollection = RoomCriteriaOrderedCollection.createFromExisting(list))
            } else RoomCriteriaState(RoomCriteriaOrderedCollection.createWithDefaults())
        }
    }
}