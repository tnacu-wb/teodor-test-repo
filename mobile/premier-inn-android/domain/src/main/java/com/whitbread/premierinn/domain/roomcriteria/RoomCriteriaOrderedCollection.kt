package com.whitbread.premierinn.domain.roomcriteria

import com.whitbread.premierinn.domain.common.RoomCriteria

class RoomCriteriaOrderedCollection private constructor(private val roomRange: IntRange = ROOM_RANGE) : LinkedHashMap<Int, RoomCriteria>(roomRange.last) {

    companion object {
        var ROOM_RANGE: IntRange = 1..4 // OPERA - Temporary solution until refactor
        fun createFromExisting(roomCriteria: List<RoomCriteria>): RoomCriteriaOrderedCollection {
            return roomCriteria.map { it.roomNumber to it }.toMap(RoomCriteriaOrderedCollection())
        }

        fun createWithDefaults(): RoomCriteriaOrderedCollection {
            return RoomCriteriaOrderedCollection().apply {
                put(1, RoomCriteria.createWithDefaults())
            }
        }
        fun setMaxRooms(maxRooms: Int) { // OPERA - Temporary solution until refactor
            ROOM_RANGE = 1..maxRooms
        }
    }

    private fun copy(): RoomCriteriaOrderedCollection {
        return this.toMap(RoomCriteriaOrderedCollection())
    }

    fun addRoomInOrder(newRoom: RoomCriteria): RoomCriteriaOrderedCollection {
        val mapCopy = copy()
        val newRoomKey = mapCopy.roomIds().sorted().last().inc()
        mapCopy[newRoomKey] = newRoom.copy(roomNumber = newRoomKey)
        return mapCopy
    }

    fun removeRoom(roomId: Int): RoomCriteriaOrderedCollection {
        val mapCopy = copy()
        mapCopy.remove(roomId)
        return mapCopy
    }

    fun updateRoom(roomId: Int, updatedRoom: RoomCriteria): RoomCriteriaOrderedCollection {
        val mapCopy = copy()
        mapCopy[roomId] = updatedRoom
        return mapCopy
    }

    fun minRoomNumber(): Boolean {
        return size == roomRange.first
    }

    fun maxRoomRoomNumber(): Boolean {
        return size == roomRange.last
    }

    fun getAdultsTotal(): Int {
        return asIterable().map { it.value }.sumBy { it.numberOfAdults }
    }

    fun getChildrenTotal(): Int {
        return asIterable().map { it.value }.sumBy { it.numberOfChildren }
    }

    fun getInfantsTotal(): Int {
        return asIterable().map { it.value }.sumOf { it.numberOfInfants }
    }

    fun totalNumberOfRooms(): Int = size

    fun roomIds(): List<Int> = keys.toList()

    fun getRoomOrderedNumber(roomId: Int): Int = keys.indexOf(roomId).inc()
}