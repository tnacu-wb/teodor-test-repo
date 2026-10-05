package com.whitbread.premierinn.alternativeroomselection

import com.whitbread.premierinn.common.AsyncResult
import com.whitbread.premierinn.common.BookingFlowInput
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.domain.alternativeroom.entity.TwinRoomInfoItem
import com.whitbread.premierinn.domain.booking.entity.Booking
import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import com.whitbread.premierinn.domain.common.LettingType
import com.whitbread.premierinn.hoteldetails.BookingRoomOpera

data class AlternativeRoomSelectionState(
    val fullAvailability: List<BookingRoomOpera>,
    val deviceLocaleProvider: DeviceLocaleProvider,
    val bookingFlowInput: BookingFlowInput? = null,
    val twinRoomInfo: AsyncResult<List<TwinRoomInfoItem>>? = null,
    val listOfSelectedRooms: List<SelectedTwinRooms>? = null,
    val listOfOriginalRooms: MutableList<RoomTypeChoices>? = null,
    val totalPrice: String? = null) {

    val isLoading: Boolean
        get() = twinRoomInfo is AsyncResult.Loading

    val getListOfOriginalRooms: List<RoomTypeChoices>
        get() = listOfOriginalRooms ?: emptyList()

    fun getTwinRoomInfo(): List<TwinRoomInfoItem> {
        return if (twinRoomInfo is AsyncResult.Success && twinRoomInfo.data != null
                && (fullAvailability.isNotEmpty())) {
            val listOfItems = mutableListOf<TwinRoomInfoItem>()

            getUniqueTwinRoomLettingType().forEach { lettingType ->
                twinRoomInfo.data.forEach { infoItem ->
                        if (infoItem.lettingType.substring(0, 2) == lettingType.substring(0, 2)) {
                            listOfItems.add(infoItem)
                        }
                }
            }
            return listOfItems
        } else emptyList()
    }

    private fun getUniqueTwinRoomLettingType(): MutableList<String> {
        val listOfUniqueLettingType = emptyList<String>().toMutableList()

        fullAvailability.forEach { rooms ->
            if (rooms.lettingType.isNotEmpty() && LettingType(rooms.lettingType).twinRoomType != null) {
                if (listOfUniqueLettingType.indexOf(rooms.lettingType) == -1) {
                    listOfUniqueLettingType.add(rooms.lettingType)
                }
                rooms.alternativeRooms?.forEach { alternativeRoom ->
                    alternativeRoom.lettingType.let { lettingType ->
                        if (listOfUniqueLettingType.indexOf(lettingType) == -1) {
                            listOfUniqueLettingType.add(lettingType)
                        }
                    }
                }
            }
        }

        return listOfUniqueLettingType
    }
}