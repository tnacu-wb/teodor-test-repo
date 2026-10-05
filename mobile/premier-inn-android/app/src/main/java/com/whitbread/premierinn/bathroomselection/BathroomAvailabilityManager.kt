package com.whitbread.premierinn.bathroomselection

import com.whitbread.premierinn.accessiblebathroomselection.AccessibleRoomSizeOption
import com.whitbread.premierinn.domain.bathroomselection.entity.BathroomSelectionInfo
import com.whitbread.premierinn.domain.bathroomselection.entity.BathroomType
import com.whitbread.premierinn.domain.bathroomselection.entity.RoomBathroomChoices
import com.whitbread.premierinn.domain.common.ACCESSIBLE_ROOM_CODE
import com.whitbread.premierinn.domain.common.LettingType
import com.whitbread.premierinn.domain.common.RoomType
import com.whitbread.premierinn.domain.common.RoomTypeCode
import com.whitbread.premierinn.domain.common.STANDARD_ROOM_CLASS_OPERA
import com.whitbread.premierinn.domain.common.roomTypeCode
import com.whitbread.premierinn.hoteldetails.BathroomSelectionInput
import com.whitbread.premierinn.hoteldetails.ParcelableRoomOpera
import com.whitbread.premierinn.common.RoomBooking
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.subjects.BehaviorSubject

/**
 * Manages the availability of bathrooms, which changes as the user changes their bathroom selection
 */
class BathroomAvailabilityManager(val bathroomSelectionInput: BathroomSelectionInput?) {

    @Suppress("PrivatePropertyName")
    private val DEFAULT_BATHROOM_TYPE: BathroomType = BathroomType.LOWERED_BATH
    @Suppress("PrivatePropertyName")
    private val DEFAULT_ROOM_SIZE: AccessibleRoomSizeOption = AccessibleRoomSizeOption.DOUBLE
    private var listOfOriginalStdRoomBookings: List<RoomBooking> = mutableListOf()
    private var listOfOriginalTwinRoomBookings: List<RoomBooking> = mutableListOf()
    private var listOfOriginalAccessibleRoomBookings: List<RoomBooking> = mutableListOf()
    private var listOfSelectedAccRoomBookings: List<RoomBooking> = mutableListOf()

    private var accessibleBookingRooms: List<ParcelableRoomOpera> = mutableListOf()
    private var roomBookings: List<RoomBooking> = mutableListOf()
    private var accessibleRoomBookings: List<RoomBooking> = mutableListOf()
    private var twinRoomBookings: List<RoomBooking> = mutableListOf()

    val bathroomAvailabilityObservable: Flowable<BathroomSelectionInfo>
        get() = bathroomAvailabilitySubject.toFlowable(BackpressureStrategy.LATEST)

    private val bathroomAvailabilitySubject = BehaviorSubject.create<BathroomSelectionInfo>()

    private val roomTypeAvailabilityCount = HashMap<RoomTypeCode, Int>()
    private val roomSelections = ArrayList<RoomBathroomChoices>()

    init {
        accessibleBookingRooms = bathroomSelectionInput!!.parcelableAccessibleRoom
        roomBookings = bathroomSelectionInput.provisionalSummaryInput.roomBookings() ?: emptyList()
        accessibleRoomBookings = bathroomSelectionInput.provisionalSummaryInput.accessibleRoomBookings()!!
        twinRoomBookings = bathroomSelectionInput.provisionalSummaryInput.twinRoomBookings()!!

        initialiseSelectionData()
        notifyChanges()

        listOfOriginalStdRoomBookings = roomBookings

        listOfOriginalAccessibleRoomBookings = accessibleRoomBookings

        listOfOriginalTwinRoomBookings = twinRoomBookings
    }

    @Synchronized
    fun selectRoomSize(roomId: Int, roomSize: AccessibleRoomSizeOption, notifyChanges: Boolean = true) {
        val existingRoomSelection = roomSelections.find { it.roomId == roomId }!!

        makeRoomSelection(roomId = roomId,
                onlyAllow = { it.accessibleRoomSize() == roomSize },
                givePreferenceTo = { it.bathroomType == existingRoomSelection.selectedLettingType.bathroomType },
                notifyChanges = notifyChanges)
    }

    @Synchronized
    fun selectBathroomType(roomId: Int, bathroomType: BathroomType, notifyChanges: Boolean = true) {
        val existingRoomSelection = roomSelections.find { it.roomId == roomId }!!

        makeRoomSelection(roomId = roomId,
                onlyAllow = {
                    it.bathroomType == bathroomType
                            },
                givePreferenceTo = {
                    it.accessibleRoomSize() == existingRoomSelection.selectedLettingType.accessibleRoomSize()
                                   },
                notifyChanges = notifyChanges)
    }

    private fun makeRoomSelection(roomId: Int,
                                  onlyAllow: (list: LettingType) -> Boolean,
                                  givePreferenceTo: (list: LettingType) -> Boolean,
                                  notifyChanges: Boolean = false) {

        val existingRoomSelection = roomSelections.find { it.roomId == roomId }!!
        val existingSelectionMeetsRequirements = onlyAllow(existingRoomSelection.selectedLettingType)
                && givePreferenceTo(existingRoomSelection.selectedLettingType)


        if (existingSelectionMeetsRequirements) {
            return
        }

        val availableMatchingLettingTypes = existingRoomSelection.compatibleBathrooms
                .filter {
                    onlyAllow(it) }

        if (availableMatchingLettingTypes.isNotEmpty()) {
            val matchingRoomWithPreference = availableMatchingLettingTypes
                    .find { givePreferenceTo(it) }

            matchingRoomWithPreference?.let {
                selectLettingType(roomId, it)
            } ?: run {
                selectLettingType(roomId, availableMatchingLettingTypes.first())
            }
            if (notifyChanges) notifyChanges()
        }
    }

    private fun initialiseSelectionData() {
        val totalAvailabilityMap = HashMap<RoomTypeCode, Int>()

        addSelectedTypesToTotalAvailability(totalAvailabilityMap)

        for (roomTypeCode in totalAvailabilityMap.keys) {
            totalAvailabilityMap[roomTypeCode]?.let { availabilityCount ->
                changeUnselectedAvailability(roomTypeCode) { 1 }
            }
        }

        // Apply default room - Iterating by index to avoid concurrentmodificationexception by editing list while iterating
        for (indx in 0 until roomSelections.size) {
            val roomChoices = roomSelections[indx]
            // This will just do nothing if the selection is not possible for this room
            makeRoomSelection(roomId = roomChoices.roomId,
                onlyAllow = {
                    it.bathroomType == DEFAULT_BATHROOM_TYPE
                            },
                givePreferenceTo = { it.accessibleRoomSize() == DEFAULT_ROOM_SIZE })
        }
    }

    private fun addSelectedTypesToTotalAvailability(totalAvailabilityMap: MutableMap<RoomTypeCode, Int>) {
        var accessibleRoomsByRoomNumber: Map<Int, List<ParcelableRoomOpera>> = emptyMap()
        val listOfRoomBathroomChoices = mutableListOf<RoomBathroomChoices>()
        bathroomSelectionInput?.provisionalSummaryInput?.isAlternativeRoom?.let { isAlternateRoom ->
            val filteredAccessibleRooms = if (isAlternateRoom) {
                accessibleBookingRooms.filter { it.roomClass != STANDARD_ROOM_CLASS_OPERA }
            } else {
                accessibleBookingRooms
            }
            accessibleRoomsByRoomNumber = filteredAccessibleRooms.groupBy { it.number }
        }

        accessibleRoomsByRoomNumber.entries.forEach { eachAccRoomOption ->
            val compatibleBathrooms = ArrayList<LettingType>()
            var number = -1

            eachAccRoomOption.value.forEach { accessibleRoomOption ->
                    val compatibleRoom = LettingType(accessibleRoomOption.lettingType)
                    number = accessibleRoomOption.number
                    compatibleBathrooms.add(compatibleRoom)
            }
            listOfRoomBathroomChoices.add(RoomBathroomChoices(number, compatibleBathrooms[0], compatibleBathrooms))
        }
        listOfRoomBathroomChoices.forEach { bathroomchoic ->
            val lettingType = bathroomchoic.selectedLettingType
            val existingTotalAvailability = totalAvailabilityMap[lettingType.code.roomTypeCode()]

            existingTotalAvailability?.let {
                totalAvailabilityMap[lettingType.code.roomTypeCode()] = it + 1
            } ?: run {
                totalAvailabilityMap[lettingType.code.roomTypeCode()] = 1
            }

            roomSelections.add(bathroomchoic)
        }
    }

    private fun notifyChanges() {
        val bathroomSelectionTracking = BathroomSelectionInfo(roomTypeAvailabilityCount, roomSelections)
        println(bathroomSelectionTracking)
        bathroomAvailabilitySubject.onNext(bathroomSelectionTracking)
    }

    fun listOfAccessibleSelectedRoom(): List<RoomBooking> {
        val updatedAccRoomSelectionsList = ArrayList<RoomBooking>()

        val groupAccessibleRoomBookingByRoomNumber = listOfOriginalAccessibleRoomBookings.groupBy { it.roomNumber }
        groupAccessibleRoomBookingByRoomNumber.entries.forEach { eachRoomNumber ->
            eachRoomNumber.value.forEach { accRoomBooking ->
                if (accRoomBooking.type == ACCESSIBLE_ROOM_CODE) {
                    val lettingTypeToMatch = LettingType(accRoomBooking.lettingCode)
                    val findMatchingRoomSelection = roomSelections.find { it.roomId == accRoomBooking.roomNumber }
                    if (findMatchingRoomSelection!!.selectedLettingType == lettingTypeToMatch) {
                        updatedAccRoomSelectionsList.add(accRoomBooking)
                    }
                }
            }
        }

        listOfSelectedAccRoomBookings = updatedAccRoomSelectionsList
        return listOfSelectedAccRoomBookings

    }

    fun listOfTwinSelectedRoom(): List<RoomBooking> {
        val updatedTwinRoomSelectionsList = ArrayList<RoomBooking>()

        val groupBy = listOfOriginalTwinRoomBookings.groupBy { it.roomNumber }

        groupBy.forEach { (_, room) ->
            val higherPrice = room.maxByOrNull { it.dailyRates[0].price.amount }
            higherPrice?.let {
                updatedTwinRoomSelectionsList.add(it)
            }
        }

        listOfSelectedAccRoomBookings = updatedTwinRoomSelectionsList
        return listOfSelectedAccRoomBookings
    }

    fun listOfStdSelectedRoom(): List<RoomBooking> {
        return bathroomSelectionInput?.provisionalSummaryInput?.isAlternativeRoom?.let {
            return listOfOriginalStdRoomBookings.filter{ it.type != RoomType.ACCESSIBLE.code }
        } ?: listOfOriginalStdRoomBookings
    }

    @Synchronized
    private fun selectLettingType(roomId: Int, lettingType: LettingType) {
        // This retrieval must happen before we update the room selections list
        val existingSelectedRoomType = roomSelections.find { it.roomId == roomId }!!.selectedLettingType
        updateRoomSelectionsList(roomId, lettingType)

        updateBathroomAvailabilityList(lettingType, existingSelectedRoomType)
    }

    private fun updateRoomSelectionsList(roomId: Int, lettingType: LettingType) {
        val existingSelection = roomSelections.find { it.roomId == roomId }
        val updatedSelection = existingSelection!!.copy(selectedLettingType = lettingType)
        val selectionIndex = roomSelections.indexOf(existingSelection)
        roomSelections.removeAt(selectionIndex)
        roomSelections.add(selectionIndex, updatedSelection)
    }

    private fun updateBathroomAvailabilityList(selectedLettingType: LettingType, deselectedLettingType: LettingType?) {
        changeUnselectedAvailability(selectedLettingType.code.roomTypeCode()) { 1 }
        deselectedLettingType?.let {
            changeUnselectedAvailability(deselectedLettingType.code.roomTypeCode()) { 1}
        }
    }

    private fun changeUnselectedAvailability(roomTypeCode: RoomTypeCode, calculation: (initialAvailability: Int) -> Int) {
        val originalAvailability = roomTypeAvailabilityCount[roomTypeCode] ?: 0
        roomTypeAvailabilityCount[roomTypeCode] = calculation(originalAvailability)
    }
}
