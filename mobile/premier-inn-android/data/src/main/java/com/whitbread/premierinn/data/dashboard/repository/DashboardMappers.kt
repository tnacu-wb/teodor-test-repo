package com.whitbread.premierinn.data.dashboard.repository

import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.data.dashboard.entity.*
import com.whitbread.premierinn.data.remote.DashboardApiContract
import com.whitbread.premierinn.domain.dashboard.entity.*
import com.whitbread.premierinn.domain.dashboard.entity.Map

fun List<DashboardApiContract.DashboardResponse>.toEntity(): List<DashboardEntity> {
    return this.map { it.mapToDashboardEntity() }
}

fun DashboardApiContract.DashboardResponse.mapToDashboardEntity(): DashboardEntity {
    return DashboardEntity(type = this.type, content = this.content?.mapToContentEntity())
}

private fun DashboardApiContract.Content?.mapToContentEntity(): ContentEntity? {
    return if (this != null) {
        ContentEntity(
                hotelImage = this.hotelImage,
                hotelName = this.hotelName,
                hotelCode = this.hotelCode,
                map = this.map.toMapEntity(),
                checkedIn = this.checkedIn,
                confirmationNumber = confirmationNumber,
                arrivalDate = arrivalDate,
                departureDate = departureDate,
                rooms = this.rooms.toRoomsEntity(),
                guests = this.guests,
                actions = this.actions.toActionsEntity(),
                frequentBookings = this.frequentBookings.toFrequentBookingsEntity()
        )
    } else null
}

private fun DashboardApiContract.Map?.toMapEntity(): MapEntity {
    return MapEntity(latitude = this?.latitude, longitude = this?.longitude)
}

private fun List<DashboardApiContract.Room>?.toRoomsEntity(): List<RoomEntity> {
    return if (this.isNullOrEmpty()) emptyList() else this.mapNotNull{ it.type}.map { RoomEntity(type = it)}
}


private fun List<DashboardApiContract.Actions>?.toActionsEntity(): List<ActionEntity> {
    return if (this.isNullOrEmpty()) emptyList() else this.map { it.toActionEntity() }
}

private fun List<DashboardApiContract.FrequentBooking>?.toFrequentBookingsEntity(): List<FrequentBookingEntity> {
    return if (this.isNullOrEmpty()) emptyList() else this.map { it.toFrequentBookingEntity() }
}

private fun DashboardApiContract.Actions.toActionEntity(): ActionEntity {
    return ActionEntity(type = this.type, title = title)
}

private fun DashboardApiContract.FrequentBooking.toFrequentBookingEntity(): FrequentBookingEntity {
    return FrequentBookingEntity(hotelImage = this.hotelImage, hotelName = this.hotelName, hotelCode = this.hotelCode)
}

fun List<DashboardEntity>.toDashboardItems(): List<DashboardItem> {
    return this.map { it.toDashboardItem() }
}

fun DashboardEntity.toDashboardItem(): DashboardItem {
    return DashboardItem(type = type, content = content?.toContentDomain())
}

private fun ContentEntity?.toContentDomain(): Content? {
    return if (this != null) {
        Content(
                hotelImage = hotelImage ?: EMPTY_STRING,
                hotelName = hotelName?: EMPTY_STRING,
                hotelCode = hotelCode?: EMPTY_STRING,
                map = map.toMapDomain(),
                checkedIn = checkedIn,
                confirmationNumber = confirmationNumber ?: EMPTY_STRING,
                arrivalDate = arrivalDate,
                departureDate = departureDate,
                rooms = rooms.toRoomsDomain(),
                guests = guests,
                actions = actions.toActionsDomain(),
                frequentBookings = frequentBookings.toFrequentBookingsDomain()
        )
    } else null
}

private fun MapEntity?.toMapDomain(): Map {
    return Map(latitude = this?.latitude , longitude = this?.longitude)
}

private fun List<RoomEntity>.toRoomsDomain(): List<Room> {
    return this.map { it.toRoomDomain() }
}

private fun RoomEntity.toRoomDomain(): Room {
    return Room(type = type)
}

private fun List<ActionEntity>.toActionsDomain(): List<Action> {
    return this.map { it.toActionDomain() }
}

private fun ActionEntity.toActionDomain(): Action {
    return Action(type = type, title = title)
}

private fun List<FrequentBookingEntity>.toFrequentBookingsDomain(): List<FrequentBooking> {
    return this.map { it.toFrequentBookingDomain() }
}

private fun FrequentBookingEntity.toFrequentBookingDomain(): FrequentBooking {
    return FrequentBooking(hotelImage = this.hotelImage, hotelName = this.hotelName, hotelCode = this.hotelCode)
}