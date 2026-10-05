package com.whitbread.premierinn.alternativeroomselection

import androidx.lifecycle.SavedStateHandle
import com.whitbread.premierinn.BuildConfig
import com.whitbread.premierinn.alternativeroomselection.AlternativeRoomSelectionActivity.Companion.ROOM_SELECTION_INPUT
import com.whitbread.premierinn.alternativeroomselection.analytics.AlternativeRoomTrackingData
import com.whitbread.premierinn.alternativeroomselection.analytics.toAlternativeRoomTracking
import com.whitbread.premierinn.businessbooker.data.common.persistence.BusinessPersistenceManager
import com.whitbread.premierinn.common.AsyncResult
import com.whitbread.premierinn.common.BookingFlowInput
import com.whitbread.premierinn.common.Reducer
import com.whitbread.premierinn.common.RxViewModelStore
import com.whitbread.premierinn.common.StringResourceProvider
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.ScreenState
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Value.EMPLOYEE_RATE_CODE
import com.whitbread.premierinn.common.analytics.TrackingAnalytics
import com.whitbread.premierinn.common.format.PriceFormat
import com.whitbread.premierinn.common.mapToAsyncResult
import com.whitbread.premierinn.common.mapper.toAncillariesCloseout
import com.whitbread.premierinn.common.mapper.toParcelablePrice
import com.whitbread.premierinn.common.mapper.toPriceDomain
import com.whitbread.premierinn.common.utils.constructBookingFlowInput
import com.whitbread.premierinn.common.utils.constructReviewBookingInputWhenSkippingUpsellForBBUser
import com.whitbread.premierinn.common.utils.getAdditionalInfoQuestions
import com.whitbread.premierinn.common.utils.getUpsellItemsAllowed
import com.whitbread.premierinn.common.utils.sumByFloat
import com.whitbread.premierinn.common.utils.updateSummaryInput
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManager
import com.whitbread.premierinn.data.graphql.BookingInformationException
import com.whitbread.premierinn.data.graphql.CreateReservationException
import com.whitbread.premierinn.domain.alternativeroom.entity.RoomInfo
import com.whitbread.premierinn.domain.alternativeroom.entity.TwinRoomChoices
import com.whitbread.premierinn.domain.alternativeroom.entity.TwinRoomInfoItem
import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import com.whitbread.premierinn.domain.common.LettingType
import com.whitbread.premierinn.domain.common.PriceDomain
import com.whitbread.premierinn.domain.common.SUB_CHANNEL
import com.whitbread.premierinn.domain.graphql.common.usecase.GraphQLHoldBookingUseCase
import com.whitbread.premierinn.domain.graphql.requestBodyModels.BookingChannelDetails
import com.whitbread.premierinn.domain.graphql.requestBodyModels.Channel
import com.whitbread.premierinn.domain.graphql.requestBodyModels.HoldBookingRequestBody
import com.whitbread.premierinn.hoteldetails.BathroomSelectionInput
import com.whitbread.premierinn.hoteldetails.BookingRoomOpera
import com.whitbread.premierinn.hoteldetails.analytics.getWeekDay
import com.whitbread.premierinn.hoteldetails.hdpOperaExtensions.constructCreateReservation
import com.whitbread.premierinn.hoteldetails.hdpOperaExtensions.createPackagesRequestBody
import com.whitbread.premierinn.hoteldetails.hdpOperaExtensions.isTwinRoom
import com.whitbread.premierinn.reviewbooking.ReviewBookingInput
import com.whitbread.premierinn.summary.SummaryInput
import com.whitbread.premierinn.summary.addTwoRoomBookingListAndOrderIt
import com.whitbread.premierinn.summarybreakdown.SummaryBreakdownInput
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.threeten.bp.LocalDate
import javax.inject.Inject

@HiltViewModel
class AlternativeRoomSelectionViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getTwinRoomInfoUseCase: GetTwinRoomInfoUseCase,
    private val graphQLHoldBookingUseCase: GraphQLHoldBookingUseCase,
    private val stringProvider: StringResourceProvider,
    private val deviceLocaleProvider: DeviceLocaleProvider,
    private val trackingAnalytics: TrackingAnalytics,
    private val simplePersistenceManager: SimplePersistenceManager,
    private val businessPersistenceManager: BusinessPersistenceManager,
    ) : RxViewModelStore<AlternativeRoomSelectionState, AlternativeRoomSelectionViewModel.AlternativeRoomSelectionEvent>(
    AlternativeRoomSelectionState(emptyList(), deviceLocaleProvider = deviceLocaleProvider)
) {

    private val bathroomSelectionInput: BathroomSelectionInput? by lazy {
        savedStateHandle.get<BathroomSelectionInput>(ROOM_SELECTION_INPUT)
    }
    private val twinRoomAvailabilityManager: TwinRoomAvailabilityManager by lazy {
        TwinRoomAvailabilityManager(bathroomSelectionInput)
    }

    init {

        applyState(Reducer {
            it.copy(
                fullAvailability = twinRoomAvailabilityManager.fullAvailability()
            )
        })

            getListOfRoomTypes().mapToAsyncResult()
                .subscribeOn(Schedulers.io())
                .subscribe { result ->
                    applyState(Reducer {
                        it.copy(twinRoomInfo = result)
                    })
                    if (result is AsyncResult.Error) {
                        publish(AlternativeRoomSelectionEvent.GenericErrorEvent(result.error))
                    }
                }.addDisposable()

            applyState(Reducer {
                it.copy(
                    listOfOriginalRooms = toTwinRoomChoices(
                        twinRoomAvailabilityManager.listOfOriginalRooms()
                    )
                )
            })

            twinRoomAvailabilityManager.listOfSelectedRooms()
                .subscribeOn(Schedulers.io())
                .subscribe { result ->
                    applyState(Reducer { it.copy(listOfSelectedRooms = result) })
                    applyState(Reducer { it.copy(totalPrice = getTotalPrice()) })
                }.addDisposable()

        trackAnalytics()
    }

    fun getListOfRoomTypes(): Single<List<TwinRoomInfoItem>> {
        return getTwinRoomInfoUseCase.invoke()
    }

    fun getBookingFlowInput(): BookingFlowInput? {
        return currentState().bookingFlowInput
    }

    private fun toTwinRoomChoices(selectedRooms: List<BookingRoomOpera>): MutableList<RoomTypeChoices> {
        val twinRoomChoices = mutableListOf<TwinRoomChoices>()

        selectedRooms.forEach { room ->

            val alternativeRoomInfo = mutableListOf<RoomInfo>()

            room.alternativeRooms?.forEach {
                    alternativeRoomInfo.add(RoomInfo(it.lettingType, it.totalCost.toPriceDomain()))
            }
            val roomInfo = RoomInfo(room.lettingType, room.totalCost.toPriceDomain())

            val roomChoice = TwinRoomChoices(
                    room.roomNumber, room.type, room.adults, room.children, room.lettingType, roomInfo,
                    alternativeRoomInfo,
                    if (room.lettingType.isNotEmpty()) LettingType(room.lettingType).twinRoomType != null
                    else false,
                    room.totalCost.toPriceDomain()
            )

            twinRoomChoices.add(roomChoice)
        }
        return twinRoomChoices.toRoomTypeChoices()
    }


    private fun MutableList<TwinRoomChoices>.toRoomTypeChoices(): MutableList<RoomTypeChoices> {
        val twinRoomChoices = this
        val roomTypeOptions = mutableListOf<RoomTypeChoices>()
        for (room in twinRoomChoices) {
            val listOfRoomTypes = createListOfRoomType(room)
            val roomTypeOption = RoomTypeChoices(
                room.roomId,
                getRoomHeading(room.type),
                getOccupantsSubheading(room.numberOfAdults, room.numberOfChildren),
                listOfRoomTypes[0],
                if (listOfRoomTypes.size > 1) listOfRoomTypes[1] else null
            )
            roomTypeOptions.add(roomTypeOption)
        }

        return roomTypeOptions
    }

    private fun getOccupantsSubheading(numberOfAdults: Int, numberOfChildren: Int): String {
        val adultsString = stringProvider.getAdults(numberOfAdults)
        val sb = StringBuilder(adultsString)
        if (numberOfChildren > 0) {
            sb.append(", ").append(stringProvider.getChildren(numberOfChildren))
        }
        return sb.toString()
    }

    private fun getRoomHeading(type: String): String {
        return stringProvider.getRoomDescription(type)
    }

    private fun createListOfRoomType(room: TwinRoomChoices): List<TwinRoomOption> {
        val roomTypesUiModel = mutableListOf<TwinRoomOption>()

        val trueTwinModel = uiModelForTwinRoomOption(
            room.lettingType,
            room.totalCost,
            room.isTwinRoomOption
        )
        roomTypesUiModel.add(trueTwinModel)

        if (room.isTwinRoomOption && !room.alternativeLettingType.isNullOrEmpty()) {
            var premierInnTwinModel: TwinRoomOption
            room.alternativeLettingType!![0].let { roomInfo ->
                premierInnTwinModel = uiModelForTwinRoomOption(
                    roomInfo.lettingType,
                    roomInfo.price,
                    room.isTwinRoomOption
                )
                roomTypesUiModel.add(premierInnTwinModel)
            }
        }

        return roomTypesUiModel
    }

    private fun uiModelForTwinRoomOption(
        lettingType: String,
        price: PriceDomain,
        isTwinRoomOption: Boolean
    ): TwinRoomOption {
        return createTwinRoomItem(
                lettingType,
                price,
                isTwinRoomOption
            )
    }

    private fun createTwinRoomItem(
        lettingType: String,
        price: PriceDomain,
        isTwinRoomOption: Boolean
    ): TwinRoomOption {
        return TwinRoomOption(lettingType, price, isTwinRoomOption)
    }

    fun onRoomSelected(event: AlternativeRoomTypeComponentView.SelectedRoomClickEvent) {
            twinRoomAvailabilityManager.updateRoom(event.id, event.twinRoomOption)
    }

    private fun getTotalPrice(): String {
        val summaryInput = getSummaryInputForCurrentSelectedRooms()
        val totalStayPrice = summaryInput.totalStayPrice()

        return PriceFormat.format(totalStayPrice.amount, totalStayPrice.currency, deviceLocaleProvider)
    }

    fun onContinueClicked() {
        val summaryInput = getSummaryInputForCurrentSelectedRooms()

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
                publish(AlternativeRoomSelectionEvent.SubmitEvent(updatedSummaryInput))
            }, { error ->
                when (error) {
                    is CreateReservationException -> {
                        publish(AlternativeRoomSelectionEvent.CreateReservationFailureEvent)
                    }

                    is BookingInformationException -> {

                        val summaryInput = getSummaryInputForCurrentSelectedRooms()
                        val constructedBookingFlowInput =
                            summaryInput.constructBookingFlowInput(error.basketReference)
                        applyState(Reducer {
                            it.copy(bookingFlowInput = constructedBookingFlowInput)
                        })
                        publish(AlternativeRoomSelectionEvent.BookingInformationErrorEvent(error.basketReference, summaryInput))
                    }

                    else -> {
                        publish(AlternativeRoomSelectionEvent.GenericFailureEvent)
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
            publish(AlternativeRoomSelectionEvent.OpenAdditionalInfoActivity(updatedInput))
        } else {
            publish(AlternativeRoomSelectionEvent.OpenReviewBookActivity(reviewBookInput))
        }
    }

    fun proceedToGuestDetails() {
        publish(AlternativeRoomSelectionEvent.OpenGuestDetailsActivity)
    }

    fun proceedToLogin(basketReference: String) {
        publish(AlternativeRoomSelectionEvent.OpenLoginDetails(basketReference))
    }

    fun onPriceBreakdownClicked() {
        val summaryInput = getSummaryInputForCurrentSelectedRooms()

        val summaryBreakdownInput = SummaryBreakdownInput.create(
            summaryInput, 0.0f, null, null,
            summaryInput.totalStayPrice().toParcelablePrice(), false)
        publish(AlternativeRoomSelectionEvent.ShowPriceBreakdown(summaryBreakdownInput))
    }

    private fun getSummaryInputForCurrentSelectedRooms(): SummaryInput {
        val listOfSpecialRequests = mutableListOf<List<String>>()
        val packageCode = mutableListOf<String?>()
        val packageAmount = mutableListOf<Double?>()

        val mergedList = twinRoomAvailabilityManager.listOfOriginalRoomBookings()
            .addTwoRoomBookingListAndOrderIt(twinRoomAvailabilityManager.listOfSelectedTwinRoomBookings())

        for ((index, eachRoom) in mergedList.withIndex()) {
            val selectedRoom = bathroomSelectionInput!!.roomTypesDomainList[index]
            if (selectedRoom.roomType.isTwinRoom()) {
                val lettingCodeSelected = eachRoom.lettingCode
                val selectedRoomOption = selectedRoom.roomsDomainList.filter { it.pmsRoomType == lettingCodeSelected }
                listOfSpecialRequests.add(selectedRoomOption.first().specialRequests ?: emptyList())
                packageCode.add(selectedRoomOption.first().packageCode ?: EMPTY_STRING_DOMAIN)
                packageAmount.add(selectedRoomOption.first().packageAmount)
            } else {
                listOfSpecialRequests.add(selectedRoom.roomsDomainList.first().specialRequests ?: emptyList())
                packageCode.add(selectedRoom.roomsDomainList.first().packageCode ?: EMPTY_STRING_DOMAIN)
                packageAmount.add( selectedRoom.roomsDomainList.first().packageAmount)
            }
        }

        return bathroomSelectionInput!!.provisionalSummaryInput.toBuilder()
            // send add package code and package amount for the twin rooms
            .packageCode(packageCode)
            .packageAmount(packageAmount)
            .specialRequests(listOfSpecialRequests)
            .roomBookings(twinRoomAvailabilityManager.listOfOriginalRoomBookings())
            .twinRoomBookings(twinRoomAvailabilityManager.listOfSelectedTwinRoomBookings())
            .build()
    }

    private fun trackAnalytics() {
        val listOfRooms = twinRoomAvailabilityManager.listOfOriginalRooms()
        val summaryInput = bathroomSelectionInput!!.provisionalSummaryInput
        val checkInDay = getWeekDay(LocalDate.parse(summaryInput.arrivalDateGQ()))
        val checkOutDay = getWeekDay(LocalDate.parse(summaryInput.departureDateGQ()))
        val rateCode = if (summaryInput.isEmployeeRateSelected()) EMPLOYEE_RATE_CODE else summaryInput.rate().code()
        trackingAnalytics.track(ScreenState.CHOOSE_TWIN_ROOM, AlternativeRoomTrackingData(
            toAlternativeRoomTracking(summaryInput, listOfRooms),
            roomType = listOfRooms.joinToString(separator = ":") { it.type },
            checkInDate = summaryInput.arrivalDateGQ(),
            checkOutDate = summaryInput.departureDateGQ(),
            nights = summaryInput.totalNights().toString(),
            rooms = listOfRooms.size.toString(),
            adults = listOfRooms.sumOf { it.adults }.toString(),
            children = listOfRooms.sumOf { it.children }.toString(),
            checkInDay = checkInDay,
            checkOutDay = checkOutDay,
            checkInOutDay = "$checkInDay-$checkOutDay",
            rateCode = rateCode,
            rateDescription = summaryInput.rate().description(),
            rateName = summaryInput.rate().rateName(),
            environment = BuildConfig.FLAVOR
        ))
    }

    sealed class AlternativeRoomSelectionEvent {
        data class GenericErrorEvent(val error: Throwable) : AlternativeRoomSelectionEvent()
        data object GenericFailureEvent : AlternativeRoomSelectionEvent()
        data object CreateReservationFailureEvent : AlternativeRoomSelectionEvent()
        data class BookingInformationErrorEvent(val basketReference: String, val updatedSummaryInput: SummaryInput) : AlternativeRoomSelectionEvent()
        data class OpenLoginDetails(val basketReference: String) : AlternativeRoomSelectionEvent()
        data class OpenReviewBookActivity(val reviewBookingInput: ReviewBookingInput) : AlternativeRoomSelectionEvent()
        data object OpenGuestDetailsActivity : AlternativeRoomSelectionEvent()
        data class OpenAdditionalInfoActivity(val reviewBookingInput: ReviewBookingInput) : AlternativeRoomSelectionEvent()
        data class SubmitEvent(val summaryInput: SummaryInput) : AlternativeRoomSelectionEvent()
        data class ShowPriceBreakdown(val summaryBreakdownInput: SummaryBreakdownInput) : AlternativeRoomSelectionEvent()
    }
}
