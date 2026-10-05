package com.whitbread.premierinn.alternativeroomselection

import com.whitbread.premierinn.common.ParcelableDailyRate
import com.whitbread.premierinn.common.mapper.toPriceDomain
import com.whitbread.premierinn.domain.common.RoomType
import com.whitbread.premierinn.domain.common.toRoomStringGQL
import com.whitbread.premierinn.hoteldetails.BathroomSelectionInput
import com.whitbread.premierinn.hoteldetails.BookingRoomOpera
import com.whitbread.premierinn.hoteldetails.ParcelableRoomOpera
import com.whitbread.premierinn.common.RoomBooking
import com.whitbread.premierinn.domain.common.toLettingTypeBart
import io.reactivex.subjects.BehaviorSubject

class TwinRoomAvailabilityManager(input: BathroomSelectionInput?) {

    private var fullAvailability: List<BookingRoomOpera> = mutableListOf()

    private var listOfSelectedRooms: List<SelectedTwinRooms> = mutableListOf()

    private var listOfOriginalRooms: List<BookingRoomOpera> = mutableListOf()

    private var listOfOriginalRoomBookings: List<RoomBooking> = mutableListOf()

    private var listOfTwinRoomBookings: List<RoomBooking> = mutableListOf()

    private var listOfSelectedRoomBookings: List<RoomBooking> = mutableListOf()

    private var listOfRoomsRelay: BehaviorSubject<List<SelectedTwinRooms>> =
        BehaviorSubject.create<List<SelectedTwinRooms>>()

    init {

        fullAvailability = toBookingRoom(input?.parcelableRoom, input?.parcelableTwinRoom)

        listOfSelectedRooms = fullAvailability.toListOfSelectedRoom()
        listOfOriginalRooms = fullAvailability
        listOfOriginalRoomBookings = input?.provisionalSummaryInput?.roomBookings()!!

        listOfTwinRoomBookings = input.provisionalSummaryInput.twinRoomBookings()!!

        listOfRoomsRelay.onNext(listOfSelectedRooms)

    }

    private fun BookingRoomOpera.findRatesForLettingCode(lettingCodeToFind: String): List<ParcelableDailyRate>? {
        return if (lettingType == lettingCodeToFind) {
            dailyRates
        } else {
            alternativeRooms?.find { it.lettingType == lettingCodeToFind }?.dailyRates
        }
    }

    private fun List<BookingRoomOpera>.findDailyRatesFromList(
        lettingCodeToFind: String,
        roomNumber: Int
    ): List<ParcelableDailyRate>? {
        val selectedBookingRoom = this.find { it.roomNumber == roomNumber }

        return selectedBookingRoom?.findRatesForLettingCode(lettingCodeToFind)
    }

    fun fullAvailability(): List<BookingRoomOpera> {
        return fullAvailability
    }

    fun listOfSelectedRooms(): BehaviorSubject<List<SelectedTwinRooms>> {
        return listOfRoomsRelay
    }

    fun listOfOriginalRooms(): List<BookingRoomOpera> {
        return listOfOriginalRooms
    }

    fun listOfOriginalRoomBookings(): List<RoomBooking> {
        return listOfOriginalRoomBookings
    }

    fun listOfSelectedTwinRoomBookings(): List<RoomBooking> {
        val selectedTwinRoomBooking = ArrayList<RoomBooking>()

        listOfSelectedRooms.forEach { selectedRoom ->
            listOfOriginalRooms.forEach orig@{ room ->
                if (room.roomNumber == selectedRoom.roomNumber
                    && room.lettingType == selectedRoom.lettingType
                ) {
                    listOfTwinRoomBookings.forEach {
                        if (it.roomNumber == room.roomNumber
                            && it.lettingCode == room.pmsRoomType
                        ) {
                            selectedTwinRoomBooking.add(it)
                        }
                    }
                    return@orig
                }
                room.alternativeRooms?.forEach alt@{ altRoom ->
                    if (altRoom.roomNumber == selectedRoom.roomNumber
                        && altRoom.lettingType == selectedRoom.lettingType
                    ) {
                        listOfTwinRoomBookings.forEach {
                            if (it.roomNumber == altRoom.roomNumber
                                && it.lettingCode == altRoom.pmsRoomType
                            ) {
                                selectedTwinRoomBooking.add(it)
                            }
                        }
                        return@alt
                    }
                }

            }
        }

        listOfSelectedRoomBookings = selectedTwinRoomBooking
        return listOfSelectedRoomBookings
    }

    private fun List<BookingRoomOpera>.toListOfSelectedRoom(): List<SelectedTwinRooms> {
        val listOfSelectedRooms = mutableListOf<SelectedTwinRooms>()
        this.map {
            val ratesForSelectedRoomType =
                it.findRatesForLettingCode(it.lettingType) ?: it.dailyRates
            listOfSelectedRooms.add(
                SelectedTwinRooms(
                    it.roomNumber,
                    it.lettingType,
                    it.totalCost.toPriceDomain(),
                    ratesForSelectedRoomType
                )
            )
        }
        return listOfSelectedRooms
    }

    fun updateRoom(roomId: Int, twinRoomOption: TwinRoomOption) {
        val updatedRoomSelectionsList = mutableListOf<SelectedTwinRooms>()

        listOfSelectedRooms.forEach { selectedTwinRoom ->
            if (selectedTwinRoom.roomNumber == roomId) {
                val selectedDailyRates = listOfOriginalRooms.findDailyRatesFromList(
                    twinRoomOption.lettingType,
                    selectedTwinRoom.roomNumber
                ) ?: selectedTwinRoom.dailyRates
                val updatedSelectedTwinRoom = SelectedTwinRooms(
                    roomId,
                    twinRoomOption.lettingType,
                    twinRoomOption.price,
                    selectedDailyRates
                )
                updatedRoomSelectionsList.add(updatedSelectedTwinRoom)
            } else {
                updatedRoomSelectionsList.add(selectedTwinRoom)
            }
        }

        listOfSelectedRooms = updatedRoomSelectionsList
        listOfRoomsRelay.onNext(listOfSelectedRooms)
    }

    private fun toBookingRoom(
        room: List<ParcelableRoomOpera>?,
        twinRoom: List<ParcelableRoomOpera>?
    ): List<BookingRoomOpera> {

        val bookingRoom = mutableListOf<BookingRoomOpera>()

        val allRooms: List<ParcelableRoomOpera> = twinRoom.orEmpty() + room.orEmpty()
        val encounteredTypes = HashSet<BookingRoomOpera>()

        allRooms.forEach { room -> // Iterate over list but skip Twin if its already in there.
            if (!encounteredTypes.any { it.type == room.type.toString() && it.roomNumber == room.number }) {
                val alternativeRoom = mutableListOf<BookingRoomOpera>()
                if (room.type == RoomType.TWIN) {
                    twinRoom?.forEach {
                        if (it.lettingType != room.lettingType) {
                            if (it.number == room.number) {
                                val altRoom = BookingRoomOpera(
                                    it.number, it.dailyRates, it.type.toRoomStringGQL(),
                                    it.lettingType, it.adults, it.children,
                                    it.cot, it.cost, it.cityTax,
                                    it.specialRequests?.get(0).toLettingTypeBart(), emptyList()
                                )
                                alternativeRoom.add(altRoom)
                            }
                        }
                    }
                }

                val roomChoice = BookingRoomOpera(
                    room.number, room.dailyRates, room.type.toRoomStringGQL(),
                    room.lettingType, room.adults, room.children,
                    room.cot, room.cost, room.cityTax,
                    if (room.type != RoomType.FAMILY) room.specialRequests?.get(0)
                        .toLettingTypeBart() else room.specialRequests?.get(0).toString(),
                    alternativeRoom
                )

                bookingRoom.add(roomChoice)
                encounteredTypes.add(roomChoice)
            }
        }

        return bookingRoom
    }
}