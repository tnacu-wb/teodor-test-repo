package com.whitbread.premierinn.domain.common

import com.whitbread.premierinn.domain.roomcriteria.compatibleSortedRoomTypes
import org.junit.Test

//todo finish it off
class RoomCriteriaTest {

    @Test
    fun `All room types except TWIN and FAMILY`() {
        val roomCriteria = RoomCriteria(roomNumber = 1, numberOfAdults = 1, numberOfChildren = 0, numberOfInfants = 0, includeCot = false, roomType = RoomType.DOUBLE)

        listOf(RoomType.SINGLE, RoomType.DOUBLE, RoomType.ACCESSIBLE).forEach {
            assert(it in roomCriteria.compatibleSortedRoomTypes())
        }
    }

    @Test
    fun `All room types except SINGLE and FAMILY`() {
        val roomCriteria = RoomCriteria(roomNumber = 1, numberOfAdults = 2, numberOfChildren = 0, numberOfInfants = 0, includeCot = false, roomType = RoomType.DOUBLE)

        listOf(RoomType.DOUBLE, RoomType.TWIN, RoomType.ACCESSIBLE).forEach {
            assert(it in roomCriteria.compatibleSortedRoomTypes())
        }
    }

    @Test
    fun `Just DOUBLE available`() {
        val roomCriteria = RoomCriteria(roomNumber = 1, numberOfAdults = 2, numberOfChildren = 0, numberOfInfants = 0, includeCot = true, roomType = RoomType.DOUBLE)

        print(roomCriteria.compatibleSortedRoomTypes())

        listOf(RoomType.DOUBLE).forEach {
            assert(it in roomCriteria.compatibleSortedRoomTypes())
        }
    }

    @Test
    fun `Just FAMILY Room available `() {
        val roomCriteria1 = RoomCriteria.createWithDefaults(adults = 1, children = 1, infants = 0, cotIncluded = false)
        val roomCriteria2 = RoomCriteria.createWithDefaults(adults = 1, children = 1, infants = 1, cotIncluded = false)
        val roomCriteria3 = RoomCriteria.createWithDefaults(adults = 1, children = 2, infants = 1, cotIncluded = false)
        val roomCriteria4 = RoomCriteria.createWithDefaults(adults = 2, children = 1, infants = 1, cotIncluded = false)
        val roomCriteria5 = RoomCriteria.createWithDefaults(adults = 2, children = 2, infants = 1, cotIncluded = false)
        val roomCriteria6 = RoomCriteria.createWithDefaults(adults = 2, children = 2, infants = 1, cotIncluded = true)

        println(roomCriteria1.compatibleSortedRoomTypes())
        println(roomCriteria2.compatibleSortedRoomTypes())

        listOf(roomCriteria1, roomCriteria2, roomCriteria3, roomCriteria4, roomCriteria5, roomCriteria6)
                .forEach {
                    assert(it.compatibleSortedRoomTypes() ==  listOf(RoomType.FAMILY))
                }
    }
}