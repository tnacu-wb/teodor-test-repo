package com.whitbread.premierinn.data.common

import com.google.common.truth.Truth.assertThat
import com.whitbread.premierinn.domain.common.RoomCriteria
import com.whitbread.premierinn.domain.common.RoomType
import org.junit.Test

class DomainExtensionsKtTest {


    @Test
    fun roomAdultsCommaSeparatedList() {

        val listOfRooms = listOf(RoomCriteria(roomNumber = 1, numberOfAdults = 2, numberOfChildren = 1, numberOfInfants = 0, roomType = RoomType.FAMILY, includeCot = false),
                RoomCriteria(roomNumber = 2, numberOfAdults = 1, numberOfChildren = 0, numberOfInfants = 0, roomType = RoomType.SINGLE, includeCot = false),
                RoomCriteria(roomNumber = 3, numberOfAdults = 2, numberOfChildren = 1, numberOfInfants = 1, roomType = RoomType.FAMILY, includeCot = true))


        assertThat(listOfRooms.adultsPerRoomCommaSeparatedList()).isEqualTo("2,1,2")
    }

    @Test
    fun roomChildrenCommaSeparatedList() {

        val listOfRooms = listOf(RoomCriteria(roomNumber = 1, numberOfAdults = 2, numberOfChildren = 1, numberOfInfants = 0, roomType = RoomType.FAMILY, includeCot = false),
                RoomCriteria(roomNumber = 2, numberOfAdults = 1, numberOfChildren = 0, numberOfInfants = 0, roomType = RoomType.SINGLE, includeCot = false),
                RoomCriteria(roomNumber = 3, numberOfAdults = 2, numberOfChildren = 1, numberOfInfants = 1, roomType = RoomType.FAMILY, includeCot = true))


        assertThat(listOfRooms.childrenPerRoomCommaSeparatedList()).isEqualTo("1,0,1")
    }

    @Test
    fun cotCommaSeparatedList() {

        val listOfRooms = listOf(RoomCriteria(roomNumber = 1, numberOfAdults = 2, numberOfChildren = 1, numberOfInfants = 0, roomType = RoomType.FAMILY, includeCot = false),
                RoomCriteria(roomNumber = 2, numberOfAdults = 1, numberOfChildren = 0, numberOfInfants = 0, roomType = RoomType.SINGLE, includeCot = false),
                RoomCriteria(roomNumber = 3, numberOfAdults = 1, numberOfChildren = 0, numberOfInfants = 0, roomType = RoomType.DOUBLE, includeCot = false),
                RoomCriteria(roomNumber = 4, numberOfAdults = 2, numberOfChildren = 1, numberOfInfants = 1, roomType = RoomType.FAMILY, includeCot = true))


        assertThat(listOfRooms.cotPerRoomCommaSeparatedList()).isEqualTo("false,false,false,true")
    }

    @Test
    fun roomTypesCommaSeparatedList() {

        val listOfRooms = listOf(RoomCriteria(roomNumber = 1, numberOfAdults = 2, numberOfChildren = 1, numberOfInfants = 0, roomType = RoomType.FAMILY, includeCot = false),
                RoomCriteria(roomNumber = 2, numberOfAdults = 1, numberOfChildren = 0, numberOfInfants = 0, roomType = RoomType.SINGLE, includeCot = false),
                RoomCriteria(roomNumber = 3, numberOfAdults = 1, numberOfChildren = 0, numberOfInfants = 0, roomType = RoomType.DOUBLE, includeCot = false),
                RoomCriteria(roomNumber = 4, numberOfAdults = 2, numberOfChildren = 1, numberOfInfants = 1, roomType = RoomType.FAMILY, includeCot = true))


        assertThat(listOfRooms.roomTypesPerRoomCommaSeparatedList()).isEqualTo("FAM,SB,DB,FAM")
    }
}