package com.whitbread.premierinn.bathroomselection

import androidx.lifecycle.SavedStateHandle
import com.whitbread.premierinn.accessiblebathroomselection.AccessibleRoomSizeOption
import com.whitbread.premierinn.bathroomselection.BathroomSelectionActivity.Companion.BATHROOM_SELECTION_INPUT
import com.whitbread.premierinn.bathroomselection.BathroomSelectionEvent.SubmitEvent
import com.whitbread.premierinn.businessbooker.data.common.persistence.BusinessPersistenceManager
import com.whitbread.premierinn.common.BookingFlowInput
import com.whitbread.premierinn.common.ParcelablePrice
import com.whitbread.premierinn.common.Reducer
import com.whitbread.premierinn.common.RxViewModelStore
import com.whitbread.premierinn.common.analytics.AnalyticsConstants
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.ALL_DISCOUNT_CODE_APPLIED
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.ALL_RATE_TAGS
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.PROMO_CODE
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.PROMO_NAME
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.SCREEN_TYPE
import com.whitbread.premierinn.common.analytics.TrackingAnalytics
import com.whitbread.premierinn.common.analytics.analyticsDataOf
import com.whitbread.premierinn.common.format.PriceFormat
import com.whitbread.premierinn.common.mapper.toAncillariesCloseout
import com.whitbread.premierinn.common.mapper.toParcelablePrice
import com.whitbread.premierinn.common.utils.constructBookingFlowInput
import com.whitbread.premierinn.common.utils.constructReviewBookingInputWhenSkippingUpsellForBBUser
import com.whitbread.premierinn.common.utils.getAdditionalInfoQuestions
import com.whitbread.premierinn.common.utils.getUpsellItemsAllowed
import com.whitbread.premierinn.common.utils.updateSummaryInput
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManager
import com.whitbread.premierinn.data.graphql.BookingInformationException
import com.whitbread.premierinn.data.graphql.CreateReservationException
import com.whitbread.premierinn.domain.bathroomselection.entity.BathroomSelectionInfo
import com.whitbread.premierinn.domain.bathroomselection.entity.BathroomType
import com.whitbread.premierinn.domain.bathroomselection.entity.RoomBathroomChoices
import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import com.whitbread.premierinn.domain.common.LOWB
import com.whitbread.premierinn.domain.common.RoomType
import com.whitbread.premierinn.domain.common.RoomTypeCode
import com.whitbread.premierinn.domain.common.STANDARD_ROOM_CLASS_OPERA
import com.whitbread.premierinn.domain.common.SUB_CHANNEL
import com.whitbread.premierinn.domain.common.WETR
import com.whitbread.premierinn.domain.common.roomTypeCode
import com.whitbread.premierinn.domain.common.toRoomStringGQL
import com.whitbread.premierinn.domain.graphql.common.usecase.GraphQLHoldBookingUseCase
import com.whitbread.premierinn.domain.graphql.requestBodyModels.BookingChannelDetails
import com.whitbread.premierinn.domain.graphql.requestBodyModels.Channel
import com.whitbread.premierinn.domain.graphql.requestBodyModels.HoldBookingRequestBody
import com.whitbread.premierinn.hoteldetails.BathroomSelectionInput
import com.whitbread.premierinn.hoteldetails.ParcelableRoomOpera
import com.whitbread.premierinn.hoteldetails.hdpOperaExtensions.constructCreateReservation
import com.whitbread.premierinn.hoteldetails.hdpOperaExtensions.createPackagesRequestBody
import com.whitbread.premierinn.hoteldetails.hdpOperaExtensions.isAccessibleRoom
import com.whitbread.premierinn.summary.SummaryInput
import com.whitbread.premierinn.summary.addThreeRoomBookingListAndOrderIt
import com.whitbread.premierinn.summarybreakdown.SummaryBreakdownInput
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BathroomSelectionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val stringProvider: BathroomSelectionStringProvider,
    private val deviceLocaleProvider: DeviceLocaleProvider,
    private val simplePersistenceManager: SimplePersistenceManager,
    private val businessPersistenceManager: BusinessPersistenceManager,
    private val graphQLHoldBookingUseCase: GraphQLHoldBookingUseCase,
    private val trackingAnalytics: TrackingAnalytics
) : RxViewModelStore<BathroomSelectionState, BathroomSelectionEvent>(BathroomSelectionState(emptyList(),
    emptyList(), deviceLocaleProvider = deviceLocaleProvider)) {
    private val bathroomSelectionInput: BathroomSelectionInput? = savedStateHandle.get<BathroomSelectionInput>(BATHROOM_SELECTION_INPUT)
    private val bathroomAvailabilityManager: BathroomAvailabilityManager = BathroomAvailabilityManager(bathroomSelectionInput)

    init {
        bathroomAvailabilityManager.bathroomAvailabilityObservable
            .subscribe { bathroomAvailTracking ->
                val accessibleRooms = accessibleRoomOptions(bathroomAvailTracking)
                val nonAccessibleRooms = nonAccessibleRoomOption()
                val totalCost = totalCost(accessibleRooms, nonAccessibleRooms)
                val state = BathroomSelectionState(
                    accessibleRooms,
                    nonAccessibleRooms,
                    totalCost,
                    deviceLocaleProvider
                )
                applyState(Reducer { state })
            }.addDisposable()
        bathroomSelectionInput?.provisionalSummaryInput?.promotionCode()?.let { promoCode ->
            trackingAnalytics.track(
                AnalyticsConstants.ScreenState.BATHROOM_SELECTION,
                analyticsDataOf(
                    SCREEN_TYPE to AnalyticsConstants.Type.BOOKING_FLOW,
                    PROMO_CODE to (promoCode),
                    PROMO_NAME to (bathroomSelectionInput.provisionalSummaryInput.promotionTag()
                        ?: EMPTY_STRING_DOMAIN),
                    ALL_RATE_TAGS to (bathroomSelectionInput.provisionalSummaryInput.rateTag()
                        ?: EMPTY_STRING_DOMAIN),
                    ALL_DISCOUNT_CODE_APPLIED to "true"
                )
            )
        }
    }

    private fun accessibleRoomOptions(bathroomAvailTracking: BathroomSelectionInfo): List<AccessibleRoomChoices> {
        val accessibleRoomChoices = mutableListOf<AccessibleRoomChoices>()

        var firstRoomCost = 0f
        var secondRoomCost = 0f
        var roomBookingInfo: ParcelableRoomOpera
        var currency: String
        var occupantsSubheading: String
        var listOfBathroomTypes: List<BathroomOption>
        var roomSizeDescription: String
        //  Unaware if we should be supporting this in opera. Didnt want to add something which we are not sure about
        // setting to false so that the change option doesnt appear
        val sizeChangeOption = false


        val alternativeRoom =
            bathroomSelectionInput?.provisionalSummaryInput?.isAlternativeRoom ?: false

        if (alternativeRoom) {
            // its alternate room like prem plus with accessible flow or maybe some random bigger room acc
            val listOfAccRoomBookingsAlt = bathroomSelectionInput?.parcelableAccessibleRoom
                ?.filter { it.roomClass != STANDARD_ROOM_CLASS_OPERA }?.filter { it.type == RoomType.ACCESSIBLE }
            val groupByRoomNumber = listOfAccRoomBookingsAlt?.groupBy { it.number }
            groupByRoomNumber?.let { accRoomByNumber ->
                accRoomByNumber.entries.forEach { allRoomOptionInEachRoom ->
                    when(allRoomOptionInEachRoom.value.size) {
                        1 -> {
                            firstRoomCost = allRoomOptionInEachRoom.value.first().cost.amount
                        }
                        2 -> {
                            allRoomOptionInEachRoom.value.forEach { roomDetails ->
                                if (roomDetails.specialRequests!!.contains(WETR)) {
                                    firstRoomCost = roomDetails.cost.amount
                                } else if (roomDetails.specialRequests.contains(LOWB)) {
                                    secondRoomCost = roomDetails.cost.amount
                                }
                            }
                        }
                    }
                    val findRoomWithSameRoomNumber = bathroomAvailTracking.roomSelections.find { it.roomId ==  allRoomOptionInEachRoom.key}
                        roomBookingInfo = allRoomOptionInEachRoom.value.first()
                        currency = allRoomOptionInEachRoom.value.first().cost.currency
                        occupantsSubheading = occupantsSubheadingAccessibleRoom(roomBookingInfo)
                        listOfBathroomTypes = createBathroomListForRoom(findRoomWithSameRoomNumber!!, bathroomAvailTracking)
                        roomSizeDescription = stringProvider.roomSizeDescription(findRoomWithSameRoomNumber.selectedLettingType.accessibleRoomSize())

                        val accessibleRoomChoice = AccessibleRoomChoices(findRoomWithSameRoomNumber.roomId,
                            occupantsSubheading,
                            roomSizeDescription,
                            sizeChangeOption,
                            null,
                            listOfBathroomTypes[0],
                            if (listOfBathroomTypes.size > 1) listOfBathroomTypes[1] else null,
                            listOfBathroomTypes[0].isBathroomSelected,
                            if (listOfBathroomTypes.size > 1) listOfBathroomTypes[1].isBathroomSelected else false,
                            ParcelablePrice(firstRoomCost, currency),
                            ParcelablePrice(secondRoomCost, currency))

                        accessibleRoomChoices.add(accessibleRoomChoice)
                    }
                }
        } else {
            for (room in bathroomAvailTracking.roomSelections) {
                roomBookingInfo = bathroomSelectionInput!!.parcelableAccessibleRoom.find { it.number == room.roomId }!!

                currency = bathroomSelectionInput.parcelableAccessibleRoom.first().cost.currency
                val getAccessibleRoomByRoomNumber = bathroomSelectionInput.parcelableAccessibleRoom.filter { it.number == room.roomId }
                if (getAccessibleRoomByRoomNumber.size > 1) {
                    firstRoomCost = getAccessibleRoomByRoomNumber.find { it.lettingType.roomTypeCode().bathroomType == BathroomType.WET_ROOM }!!.cost.amount
                    secondRoomCost = getAccessibleRoomByRoomNumber.find { it.lettingType.roomTypeCode().bathroomType == BathroomType.LOWERED_BATH }!!.cost.amount
                } else {
                    firstRoomCost = getAccessibleRoomByRoomNumber[0].cost.amount
                }

                occupantsSubheading = occupantsSubheadingAccessibleRoom(roomBookingInfo)
                listOfBathroomTypes = createBathroomListForRoom(room, bathroomAvailTracking)

                roomSizeDescription = stringProvider.roomSizeDescription(room.selectedLettingType.accessibleRoomSize())

                val accessibleRoomChoice = AccessibleRoomChoices(room.roomId,
                    occupantsSubheading,
                    roomSizeDescription,
                    sizeChangeOption,
                    null,
                    listOfBathroomTypes[0],
                    if (listOfBathroomTypes.size > 1) listOfBathroomTypes[1] else null,
                    listOfBathroomTypes[0].isBathroomSelected,
                    if (listOfBathroomTypes.size > 1) listOfBathroomTypes[1].isBathroomSelected else false,
                    ParcelablePrice(firstRoomCost, currency),
                    ParcelablePrice(secondRoomCost, currency))

                accessibleRoomChoices.add(accessibleRoomChoice)
            }
        }

        return accessibleRoomChoices
    }

    private fun allowSizeChange(room: RoomBathroomChoices, bathroomAvailability: Map<RoomTypeCode, Int>): Pair<Boolean, String?> {
        // Check that there are different sizes available for room
        val roomSizeToChangeTo = room.compatibleBathrooms
                .filter { it.accessibleRoomSize() != room.selectedLettingType.accessibleRoomSize() }
                .firstOrNull { filteredLettingType -> bathroomAvailability[filteredLettingType.roomTypeCode] ?: 0 > 0 }

        roomSizeToChangeTo?.let {
            val toolTipText =  stringProvider.toolTipText(it.accessibleRoomSize())
            return Pair(true, toolTipText)
        } ?: run {
            return Pair(false, null)
        }
    }

    private fun nonAccessibleRoomOption(): List<NonAccessibleRoom> {

        val nonAccessibleRoomChoices = mutableListOf<NonAccessibleRoom>()

            for (room in bathroomSelectionInput!!.parcelableRoom) {
                val occupantsInfo = occupantsSubheadingNonAccessible(room)
                val roomDescription = stringProvider.getRoomDescription(room.type.toRoomStringGQL())
                val totalCost = room.cost
                val roomItem =
                    NonAccessibleRoom(room.number, occupantsInfo, roomDescription, totalCost)
                nonAccessibleRoomChoices.add(roomItem)
            }

            for (room in listOfSelectedParcelableTwinRooms()) {
                val occupantsInfo = occupantsSubheadingNonAccessible(room)
                val roomDescription = stringProvider.getRoomDescription(room.type.toRoomStringGQL())
                val totalCost = room.cost
                val roomItem =
                    NonAccessibleRoom(room.number, occupantsInfo, roomDescription, totalCost)
                nonAccessibleRoomChoices.add(roomItem)
            }


        return nonAccessibleRoomChoices.sortedBy { it.roomId }
    }

    private fun listOfSelectedParcelableTwinRooms(): List<ParcelableRoomOpera> {
        val updatedParcelableTwinRooms = ArrayList<ParcelableRoomOpera>()

        val groupBy = bathroomSelectionInput!!.parcelableTwinRoom.groupBy { it.number }

        groupBy.forEach { (_, room) ->
            val higherPrice = room.maxByOrNull { it.cost.amount }
            higherPrice?.let {
                updatedParcelableTwinRooms.add(it)
            }
        }
        return updatedParcelableTwinRooms
    }

    private fun occupantsSubheadingAccessibleRoom(roomBookingInfo: ParcelableRoomOpera): String {
        val adultsString = stringProvider.getAdults(bathroomSelectionInput!!.provisionalSummaryInput.totalAdultsAccessible(roomBookingInfo.number))
        val sb = StringBuilder(adultsString)
        if (bathroomSelectionInput.provisionalSummaryInput.totalChildrenAccessible(roomBookingInfo.number) > 0) {
            sb.append(", ").append(stringProvider.getChildren(bathroomSelectionInput.provisionalSummaryInput.totalChildrenAccessible(roomBookingInfo.number)))
        }
        return sb.toString()
    }

    private fun occupantsSubheadingNonAccessible(roomBookingInfo: ParcelableRoomOpera): String {
        val adultsString = stringProvider.getAdults(roomBookingInfo.adults)
        val sb = StringBuilder(adultsString)
        if (roomBookingInfo.children > 0) {
            sb.append(", ").append(stringProvider.getChildren(roomBookingInfo.children))
        }
        return sb.toString()
    }


    fun onBathroomSelected(bathroomClickEvent: AccessibleRoomOptionsView.BathroomClickEvent) {
        bathroomAvailabilityManager.selectBathroomType(bathroomClickEvent.roomId, bathroomClickEvent.bathroomType)
    }

    fun onRoomSizeSelected(roomId: Int, roomSize: AccessibleRoomSizeOption) {
        bathroomAvailabilityManager.selectRoomSize(roomId, roomSize)
    }

    fun onRoomSizeChangeClicked(roomId: Int) {
        val alreadySelectedRoomSize = bathroomAvailabilityManager.bathroomAvailabilityObservable
                .blockingFirst()
                .roomSelections.find { it.roomId == roomId }!!
                .selectedLettingType.accessibleRoomSize()

        publish(BathroomSelectionEvent.ShowSizeSelectionEvent(roomId, alreadySelectedRoomSize))
    }

    fun onContinueClicked() {
        val summaryInput = getSummaryInputForRooms()

        val createReservationRequestBody = summaryInput.constructCreateReservation(deviceLocaleProvider.getDeviceLanguage().lowercase())
        val packagesReqBody = summaryInput.createPackagesRequestBody(deviceLocaleProvider.getDeviceLanguage().lowercase(),
            deviceLocaleProvider.getCountryIfRegion(deviceLocaleProvider.getDeviceLocale()).lowercase())
        val channel = if (summaryInput.isBusinessUser()) Channel.BB.name else Channel.PI.name

        val holdBookingRequestBody = HoldBookingRequestBody(createReservationRequestBody, packagesReqBody,
            deviceLocaleProvider.getCountryIfRegion(deviceLocaleProvider.getDeviceLocale()).lowercase(),
            deviceLocaleProvider.getDeviceLanguage().lowercase(),
            BookingChannelDetails(channel, SUB_CHANNEL, deviceLocaleProvider.getDeviceLanguage().lowercase())
        )

        graphQLHoldBookingUseCase.holdBooking(holdBookingRequestBody)
            .subscribe({ pairOfPackagesAndBasketRef ->
                val updatedSummaryInput = summaryInput.updateSummaryInput(
                    pairOfPackagesAndBasketRef.first,
                    summaryInput.ancillaryCloseOutItems().toAncillariesCloseout(),
                    businessPersistenceManager.getUpsellItemsAllowed(),
                    pairOfPackagesAndBasketRef.second
                )
                applyState(Reducer {
                    it.copy(bookingFlowInput = updatedSummaryInput.constructBookingFlowInput(pairOfPackagesAndBasketRef.second))
                })
                publish(SubmitEvent(updatedSummaryInput))
            }, { error ->
                when (error) {
                    is CreateReservationException -> {
                        publish(BathroomSelectionEvent.CreateReservationFailureEvent)
                    }

                    is BookingInformationException -> {
                        val summaryInput = getSummaryInputForRooms()
                        val constructedBookingFlowInput =
                            summaryInput.constructBookingFlowInput(error.basketReference)
                        applyState(Reducer {
                            it.copy(bookingFlowInput = constructedBookingFlowInput)
                        })
                        publish(BathroomSelectionEvent.BookingInformationErrorEvent(error.basketReference, summaryInput))
                    }

                    else -> {
                        publish(BathroomSelectionEvent.GenericFailureEvent)
                    }
                }
            }).addDisposable()
    }

    fun launchNextScreen(basketReference: String, updatedSummaryInput: SummaryInput) {
        bathroomSelectionInput?.let {
            if (updatedSummaryInput.isBusinessUser()) {
                openAdditionalInfOrOrReviewAndBook(updatedSummaryInput, basketReference)
            } else {
                if (updatedSummaryInput.isLoggedIn) {
                    proceedToGuestDetails()
                } else {
                    proceedToLogin(basketReference)
                }
            }
        }
    }

    fun openAdditionalInfOrOrReviewAndBook(summaryInput: SummaryInput, basketReference: String) {
        val reviewBookInput = summaryInput.constructReviewBookingInputWhenSkippingUpsellForBBUser(
            simplePersistenceManager, basketReference,
            deviceLocaleProvider.getDeviceLanguage().lowercase())

        val listOfQuestions = businessPersistenceManager.getAdditionalInfoQuestions()

        val updatedInput = listOfQuestions?.let {
            reviewBookInput.toBuilder().listOfEmployeeQuestionsModel(it).build()
        } ?: reviewBookInput

        if (listOfQuestions != null && listOfQuestions.isNotEmpty()) {
            publish(BathroomSelectionEvent.OpenAdditionalInfoActivity(updatedInput))
        } else {
            publish(BathroomSelectionEvent.OpenReviewBookActivity(reviewBookInput))
        }
    }

    fun proceedToGuestDetails() {
        publish(BathroomSelectionEvent.OpenGuestDetailsActivity)
    }

    fun proceedToLogin(basketReference: String) {
        publish(BathroomSelectionEvent.OpenLoginDetails(basketReference))
    }

    fun onPriceBreakdownClicked(){
        val summaryInput = getSummaryInputForRooms()

        val summaryBreakdownInput = SummaryBreakdownInput.create(
            summaryInput, 0.0f, null, null,
            summaryInput.totalStayPrice().toParcelablePrice(), false)

        publish(BathroomSelectionEvent.ShowPriceBreakdownEvent(summaryBreakdownInput))
    }

    fun getBookingFlowInput(): BookingFlowInput? {
        return currentState().bookingFlowInput
    }

    private fun getSummaryInputForRooms(): SummaryInput {
        val listOfSpecialRequests = mutableListOf<List<String>>()
        val packageCode = mutableListOf<String?>()
        val packageAmount = mutableListOf<Double?>()

        val mergedList = bathroomAvailabilityManager.listOfStdSelectedRoom()
            .addThreeRoomBookingListAndOrderIt(
                bathroomAvailabilityManager.listOfAccessibleSelectedRoom(),
                bathroomAvailabilityManager.listOfTwinSelectedRoom()
            )

        for ((index, eachRoom) in mergedList.withIndex()) {
            val selectedRoom = bathroomSelectionInput!!.roomTypesDomainList[index]
            if (selectedRoom.roomType.isAccessibleRoom()) {
                val lettingCodeSelected = eachRoom.lettingCode
                val selectedRoomOption =
                    selectedRoom.roomsDomainList.filter { it.pmsRoomType == lettingCodeSelected }
                listOfSpecialRequests.add(selectedRoomOption.first().specialRequests ?: emptyList())
                packageCode.add(selectedRoomOption.first().packageCode ?: EMPTY_STRING_DOMAIN)
                packageAmount.add(selectedRoomOption.first().packageAmount)
            } else {
                listOfSpecialRequests.add(
                    selectedRoom.roomsDomainList.first().specialRequests ?: emptyList()
                )
                packageCode.add(
                    selectedRoom.roomsDomainList.first().packageCode ?: EMPTY_STRING_DOMAIN
                )
                packageAmount.add(selectedRoom.roomsDomainList.first().packageAmount)
            }
        }

        return bathroomSelectionInput!!.provisionalSummaryInput.toBuilder()
            .packageCode(packageCode)
            .packageAmount(packageAmount)
            .specialRequests(listOfSpecialRequests)
            .roomBookings(bathroomAvailabilityManager.listOfStdSelectedRoom())
            .accessibleRoomBookings(bathroomAvailabilityManager.listOfAccessibleSelectedRoom())
            .build()
    }

    private fun createBathroomListForRoom(room: RoomBathroomChoices,
                                          bathroomSelectionTracking: BathroomSelectionInfo): List<BathroomOption> {
        val bathroomsUiModel = mutableListOf<BathroomOption>()
        val canShowWetRoomOption = shouldShowBathroomOption(BathroomType.WET_ROOM, room, bathroomSelectionTracking)
        val canShowLoweredBathOption = shouldShowBathroomOption(BathroomType.LOWERED_BATH, room, bathroomSelectionTracking)

        if (canShowWetRoomOption) {
            val uiModel = uiModelForBathroomOption(room, BathroomType.WET_ROOM, canShowLoweredBathOption, bathroomSelectionTracking)
            bathroomsUiModel.add(uiModel)
        }
        if (canShowLoweredBathOption) {
            val uiModel = uiModelForBathroomOption(room, BathroomType.LOWERED_BATH, canShowWetRoomOption, bathroomSelectionTracking)
            bathroomsUiModel.add(uiModel)
        }

        return bathroomsUiModel
    }

    private fun uiModelForBathroomOption(room: RoomBathroomChoices,
                                         bathroomType: BathroomType,
                                         canShowOtherBathroomOption: Boolean,
                                         bathroomSelectionInfo: BathroomSelectionInfo): BathroomOption {
        val warningMessage = if (!canShowOtherBathroomOption) {
            warningMessage(bathroomType, room, bathroomSelectionInfo)
        } else null
        return bathroomType
                .toBathroomItem(room.selectedLettingType.bathroomType == bathroomType, warningMessage = warningMessage)
    }

    private fun warningMessage(bathroomType: BathroomType, room: RoomBathroomChoices, bathroomSelectionInfo: BathroomSelectionInfo): String? {
        // Whether or not we show a message under one bathroom type mainly depends on the state of the OTHER bathroom type
        // and if the room can select it
        val otherBathroomType = if (bathroomType == BathroomType.WET_ROOM) BathroomType.LOWERED_BATH else BathroomType.WET_ROOM
        if (!room.hasBathroomSelected(otherBathroomType)) {
            if (!hotelOffersBathroomType(otherBathroomType)) {
                return stringProvider.hotelDoesNotOfferBathroomMessage(otherBathroomType)
            } else if (!bathroomSelectionInfo.canSelectBathroomTypeFromRemainingAvailability(otherBathroomType, room)) {
                return stringProvider.noMoreAvailableMessage(otherBathroomType,
                        room.selectedLettingType.accessibleRoomSize(),
                        room.selectedLettingType)
            }
        }
        return null
    }

    private fun shouldShowBathroomOption(bathroomType: BathroomType,
                                         room: RoomBathroomChoices,
                                         bathroomSelectionInfo: BathroomSelectionInfo): Boolean {

        return room.hasBathroomSelected(bathroomType)
                || bathroomSelectionInfo.canSelectBathroomTypeFromRemainingAvailability(bathroomType, room)
    }

    private fun RoomBathroomChoices.hasBathroomSelected(bathroomType: BathroomType): Boolean {
        return compatibleBathrooms.find { it.bathroomType == bathroomType } != null
    }

    private fun hotelOffersBathroomType(bathroomType: BathroomType): Boolean {
        return bathroomSelectionInput!!.provisionalSummaryInput.hotel()
            .accessibilityFacilities()?.offersBathroomType(bathroomType) == true
    }

    private fun BathroomSelectionInfo.canSelectBathroomTypeFromRemainingAvailability(bathroomType: BathroomType,
                                                                                     room: RoomBathroomChoices): Boolean {
        val availableRoomTypes = getRoomTypesForAvailableBathroomType(bathroomType)
        return room.unselectedCompatibleBathrooms
            .filter {
                it.bathroomType == bathroomType && it.accessibleRoomSize() == room.selectedLettingType.accessibleRoomSize()
            }
            .any { availableRoomTypes.contains(it.roomTypeCode) }
    }

    private fun BathroomType.toBathroomItem(isSelected: Boolean, warningMessage: String? = null): BathroomOption {
        return BathroomOption(stringProvider.bathroomTitle(this),
                stringProvider.bathroomDescription(this),
                isSelected, warningMessage,
                this)
    }

    private fun totalCost(accessibleRooms: List<AccessibleRoomChoices>, nonAccessibleRooms: List<NonAccessibleRoom>): String {
        //Accessible letting type price doesn't change so we can assume that the total cost will always remain the same
        val currency = bathroomSelectionInput!!.provisionalSummaryInput.totalStayPrice().currency
        var totalCost = 0f
        accessibleRooms.forEach { room ->
            totalCost += if (room.firstBathroomSelected) {
                room.totalCostFirst.amount
            } else {
                room.totalCostSecond.amount
            }
        }
        nonAccessibleRooms.forEach { room ->
            totalCost += room.totalCost.amount
        }
        return PriceFormat.format(totalCost, currency, deviceLocaleProvider)
    }
}
