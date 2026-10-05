package com.whitbread.premierinn.amend.amendguestsrooms

import androidx.lifecycle.SavedStateHandle
import com.whitbread.premierinn.amend.AmendStringProvider
import com.whitbread.premierinn.amend.amendcalendar.IS_PROMO_BOOKING
import com.whitbread.premierinn.amend.amendguestsrooms.AmendAddRoomActivity.Companion.ADDED_ROOM_INDEX_KEY
import com.whitbread.premierinn.amend.amendguestsrooms.AmendAddRoomActivity.Companion.HOTEL_BRAND
import com.whitbread.premierinn.amend.amendguestsrooms.AmendAddRoomActivity.Companion.ROOM_INDEX_KEY
import com.whitbread.premierinn.amend.amendguestsrooms.AmendAddRoomActivity.Companion.SELECTED_RATE_PLAN
import com.whitbread.premierinn.amend.analytics.AmendBookingData
import com.whitbread.premierinn.amend.analytics.AmendCheckAvailabilityData
import com.whitbread.premierinn.amend.toListOfBookingRoom
import com.whitbread.premierinn.common.AsyncResult
import com.whitbread.premierinn.common.Reducer
import com.whitbread.premierinn.common.RxViewModelStore
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.ScreenState.AMEND_ADD_ROOM
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.ScreenState.AMEND_AVAILABILITY
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.ScreenState.AMEND_EDIT_ROOM
import com.whitbread.premierinn.common.analytics.TrackingAnalytics
import com.whitbread.premierinn.common.format.DateFormat
import com.whitbread.premierinn.common.managebooking.ManageBookingInput
import com.whitbread.premierinn.common.mapToAsyncResult
import com.whitbread.premierinn.common.service.LogService
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManagerImpl
import com.whitbread.premierinn.domain.common.Guest
import com.whitbread.premierinn.domain.common.RatePlanOpera
import com.whitbread.premierinn.domain.common.RoomCriteria
import com.whitbread.premierinn.domain.common.SUB_CHANNEL
import com.whitbread.premierinn.domain.graphql.amend.usecase.GraphQLAmendUseCase
import com.whitbread.premierinn.domain.graphql.requestBodyModels.AddNewRoomRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.AmendEditRoomRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.BookingChannelDetails
import com.whitbread.premierinn.domain.graphql.requestBodyModels.Channel
import com.whitbread.premierinn.domain.graphql.requestBodyModels.LeadGuestAmend
import com.whitbread.premierinn.domain.graphql.requestBodyModels.RoomOccupancyAmend
import com.whitbread.premierinn.domain.reservation.entity.Reservation
import com.whitbread.premierinn.domain.reservation.usecase.AddRoomAndUpsellToStorageUseCase
import com.whitbread.premierinn.domain.reservation.usecase.AddRoomUseCase
import com.whitbread.premierinn.domain.reservation.usecase.ObserveAmendedReservationUseCase
import com.whitbread.premierinn.domain.reservation.usecase.ObserveBookingUseCase
import com.whitbread.premierinn.domain.reservation.usecase.StoreUpdatedAmendedReservation
import com.whitbread.premierinn.domain.reservation.usecase.StoreUpdatedAmendedRoom
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.threeten.bp.LocalDate
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.net.URLEncoder
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class AmendGuestsRoomViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val stringResourceProvider: AmendStringProvider,
    observeAmendedReservationUseCase: ObserveAmendedReservationUseCase,
    observeBookingUseCase: ObserveBookingUseCase,
    private val crashlyticsLogger: LogService,
    private val updatedAmendedReservation: StoreUpdatedAmendedRoom,
    private val storeUpdatedAmendedReservation: StoreUpdatedAmendedReservation,
    private val addedRoomUpdatedReservation: AddRoomUseCase,
    private val storeAddedRoom: AddRoomAndUpsellToStorageUseCase,
    private val graphQLAmendUseCase: GraphQLAmendUseCase,
    private val storage: SimplePersistenceManagerImpl,
    private val deviceLocaleProvider: DeviceLocaleProvider,
    private val trackingAnalytics: TrackingAnalytics
) : RxViewModelStore<AmendGuestsRoomState, AmendGuestsRoomStateEvent>(
    AmendGuestsRoomState(
        stringResourceProvider = stringResourceProvider,
        guestResult = AsyncResult.Loading(),
        roomCriteriaUpdateResult = AsyncResult.Loading(),
        titleOptions = stringResourceProvider.titlesList,
        storage = storage,
        buttonText = stringResourceProvider.updateButtonText,
        deviceLocaleProvider = deviceLocaleProvider
    )
) {

    private val input: ManageBookingInput by lazy {
        requireNotNull(savedStateHandle.get<ManageBookingInput>(EXTRA_AMEND_INPUT))
    }
    private val reservationId: String by lazy {
        input.bookingReference()
    }

    private val hotelBrand: String by lazy {
        savedStateHandle.get<String>(HOTEL_BRAND) ?: ""
    }
    private val selectedRatePlan: String by lazy {
        savedStateHandle.get<String>(SELECTED_RATE_PLAN) ?: ""
    }
    private val roomIndex: Int by lazy {
        savedStateHandle.get<Int>(ROOM_INDEX_KEY) ?: 0
    }
    private val addRoomIndex: Int by lazy {
        savedStateHandle.get<Int>(ADDED_ROOM_INDEX_KEY) ?: -1
    }

    private val isPromoBooking: Boolean by lazy {
        savedStateHandle.get<Boolean>(IS_PROMO_BOOKING) ?: false
    }

    private lateinit var originalRoomCriteria: RoomCriteria
    private var onGuestUpdate = false

    //roomIndex is 1 so need -1 to read first element from array
    private val storedGuestDetails = observeAmendedReservationUseCase(reservationId).map { it.roomsLeadGuest }
            .map { it[roomIndex - 1] }
    private val storedCriteria = observeAmendedReservationUseCase(reservationId)
        .map { it.roomsCriteria }
        .map { it.getOrNull(roomIndex - 1) }

    init {
        storedCriteria
                .mapToAsyncResult()
                .subscribe { storedCriteria ->
                    if (storedCriteria is AsyncResult.Success && storedCriteria.data != null) {
                        originalRoomCriteria = storedCriteria.data
                        if (addRoomIndex != -1) {
                            applyState(Reducer { it.copy(roomCriteriaUpdateResult = storedCriteria) })
                            trackingAnalytics.track(AMEND_ADD_ROOM, AmendBookingData(input.bookingReference()))
                            publish(AmendGuestsRoomStateEvent.InitialAddRoomCriteriaLoaded
                            (RoomCriteria.createWithDefaults()))
                        } else {
                            trackingAnalytics.track(AMEND_EDIT_ROOM, AmendBookingData(input.bookingReference()))
                            publish(AmendGuestsRoomStateEvent.InitialCriteriaLoaded(originalRoomCriteria))
                        }
                    }
                }.addDisposable()

        storedGuestDetails
                .mapToAsyncResult()
                .subscribe { guestResult ->
                    applyState(Reducer { it.copy(guestResult = guestResult) })
                }
                .addDisposable()

        observeBookingUseCase.invoke(reservationId)
                .mapToAsyncResult()
                .subscribeOn(Schedulers.io())
                .subscribe { booking ->
                    applyState(Reducer { it.copy(booking = booking) })
                }.addDisposable()
    }

    fun onUpdateLeadGuest(updateLeadGuest: Boolean) {
        onGuestUpdate = updateLeadGuest
    }

    fun onCriteriaChanged(newCriteria: RoomCriteria) {
        if (isPromoBooking) {
            applyState(Reducer { it.copy(buttonText = stringResourceProvider.updateButtonText) })
        } else {
            if (newCriteria.numberOfAdults != originalRoomCriteria.numberOfAdults
                || newCriteria.numberOfChildren != originalRoomCriteria.numberOfChildren) {
                applyState(Reducer { it.copy(buttonText = stringResourceProvider.getCheckAvailabilityText) })
            } else {
                checkIfRoomTypeOrGuestChangeAndUpdateButtonText(newCriteria)
            }
        }
    }

    private fun checkIfRoomTypeOrGuestChangeAndUpdateButtonText(newCriteria: RoomCriteria) {
        if (newCriteria.roomType != originalRoomCriteria.roomType || (addRoomIndex != -1) && !onGuestUpdate) {
            applyState(Reducer { it.copy(buttonText = stringResourceProvider.getCheckAvailabilityText) })
        } else if (onGuestUpdate) {
            applyState(Reducer { it.copy(buttonText = stringResourceProvider.continueButtonText) })
        } else {
            applyState(Reducer { it.copy(buttonText = stringResourceProvider.updateButtonText) })
        }
    }

    fun onCheckAvailabilityButtonPressed(newCriteria: RoomCriteria) {
        storedCriteria.firstOrError().mapToAsyncResult()
            .flatMap { storedCriteria ->
                updateIfNewRoomAdded(
                    storedCriteria,
                    newCriteria,
                    reservationId,
                    hotelBrand,
                    input.isEmployeeBooking
                )
            }
            .subscribe { updateResult ->
                applyState(Reducer {
                    it.copy(addRoomState = updateResult, errorMessageAddNewRoom = null)
                })
                logCheckAvailabilityAnalytics()
            }
            .addDisposable()
    }

    fun onSubmitButtonPressedAddRoom(ratePlanOpera: RatePlanOpera?) {
        val amendedReservation = storage.getStoredAmendedReservation()
        if (addRoomIndex != -1) {
            if (amendedReservation != null) {
                storedCriteria.firstOrError().mapToAsyncResult().flatMap {
                    ratePlanOpera?.let {
                        storeAddedRoom.invoke(
                            reservationId
                        )
                    }
                }.subscribe { updateResult ->
                    applyState(Reducer {
                        it.copy(updateState = updateResult)
                    })
                }.addDisposable()
            }
        }
    }

    fun onSubmitButtonPressed(newCriteria: RoomCriteria, guestName: NameModel, roomId: String, tempBasketRef: String, token: String) {
        publish(AmendGuestsRoomStateEvent.ShowMealsScreen(originalRoomCriteria.numberOfAdults != newCriteria.numberOfAdults ||
                originalRoomCriteria.numberOfChildren != newCriteria.numberOfChildren))

        storedGuestDetails.firstOrError().mapToAsyncResult()
            .flatMap { storedGuestDetails ->
                updateIfGuestOrRoomCriteriaUpdated(
                    storedGuestDetails,
                    newCriteria,
                    guestName,
                    deviceLocaleProvider.getDeviceLocale(),
                    if (input.isBusinessBooking) Channel.BB.name else Channel.PI.name,
                    roomId,
                    tempBasketRef,
                    token,
                    input.isEmployeeBooking,
                    input.isBusinessBooking
                )
            }
            .subscribe({ updateResult ->
                applyState(Reducer { it.copy(updateState = updateResult) })
            }, {
                crashlyticsLogger.logException(it, "onSubmitButtonPressed() Error")
                publish(AmendGuestsRoomStateEvent.EditNewRoomFailEvent(stringResourceProvider.updateErrorMessage))
            })
            .addDisposable()
    }

    private fun updateIfNewRoomAdded(
        storedCriteria: AsyncResult<RoomCriteria?>,
        updatedCriteria: RoomCriteria,
        reservationId: String, hotelBrand: String,
        isEmployeeBooking: Boolean
    ): Observable<AddRoomUseCase.AddARoomState> {
        val tempGuestDetails = Guest(updatedCriteria.roomNumber, updatedCriteria.roomId, TEMP_TITLE, TEMP_FIRST_NAME, TEMP_LAST_NAME,
                null, null, null, null)

        if (storedCriteria is AsyncResult.Success && storedCriteria.data != null) {
            storedCriteria.data.updateWith(updatedCriteria).also {
                return addedRoomUpdatedReservation.invoke(
                    reservationId,
                    input.listOfRooms()?.toListOfBookingRoom() ?: emptyList(),
                    it,
                    tempGuestDetails,
                    it.roomNumber,
                    deviceLocaleProvider.getDeviceLocale(),
                    hotelBrand,
                    selectedRatePlan,
                    isEmployeeBooking
                )
            }
        } else if (storedCriteria is AsyncResult.Error) {
            crashlyticsLogger.logException(storedCriteria.error, "updateIfNewRoomAdded() result Error")
            publish(AmendGuestsRoomStateEvent.GenericErrorEvent(storedCriteria.error))
            return Observable.just(AddRoomUseCase.AddARoomState.Error(exception = storedCriteria.error))
        } else {
            return Observable.just(AddRoomUseCase.AddARoomState.Loading)
        }
    }

    private fun updateIfGuestOrRoomCriteriaUpdated(
        storedGuestDetails: AsyncResult<Guest>,
        updatedCriteria: RoomCriteria,
        updatedGuestName: NameModel,
        deviceLocale: Locale,
        bookingChannel: String,
        roomId: String,
        tempBasketRef: String,
        token: String,
        isEmployeeBooking: Boolean,
        isBusinessBooking: Boolean
    ): Observable<StoreUpdatedAmendedReservation.UpdateState> {
        if (storedGuestDetails is AsyncResult.Success && storedGuestDetails.data != null) {
            storedGuestDetails.data.updateWith(updatedGuestName).also {
                // This is work around for promo booking to use the original room criteria so that
                // availability call is not made due to silent substitution.
                // For bookings which has silent substitution for example 1 adult DB -> FMTRPL(family)
                // We show the room type as Family in amend reservation but when room criteria is initialized,
                // family room wont be available as no child selected
                val assignUpdatedCriteria =
                    if (isPromoBooking) originalRoomCriteria else updatedCriteria
                return updatedAmendedReservation(
                    reservationId,
                    assignUpdatedCriteria,
                    it,
                    roomIndex,
                    deviceLocale,
                    bookingChannel,
                    hotelBrand,
                    selectedRatePlan,
                    input.listOfRooms()?.toListOfBookingRoom() ?: emptyList(),
                    roomId,
                    tempBasketRef,
                    token,
                    isEmployeeBooking,
                    isBusinessBooking
                )
            }
        } else if (storedGuestDetails is AsyncResult.Error) {
            crashlyticsLogger.logException(storedGuestDetails.error, "updateIfGuestOrRoomCriteriaUpdated() result Error")
            publish(AmendGuestsRoomStateEvent.GenericErrorEvent(storedGuestDetails.error))
            return Observable.just(StoreUpdatedAmendedReservation.UpdateState.Error(exception = storedGuestDetails.error))
        } else {
            return Observable.just(StoreUpdatedAmendedReservation.UpdateState.Loading)
        }
    }

    fun addNewRoom(tempBasketRef: String, token: String, rateType: String, roomTypeCode: String,
                   adults: Int, children: Int, guestNameModel: Guest) {
        graphQLAmendUseCase.addNewRoom(
            AddNewRoomRequestBody(
                bookingChannel = BookingChannelDetails(
                    if (input.isBusinessBooking) Channel.BB.name else Channel.PI.name,
                    SUB_CHANNEL,
                    deviceLocaleProvider.getDeviceLanguage()
                ),
                tempBookingRef = tempBasketRef,
                roomOccupancy = RoomOccupancyAmend(adultsNumber = adults, childrenNumber = children, cotRequired = false),
                leadGuest = LeadGuestAmend(
                    title = guestNameModel.title,
                    firstName = guestNameModel.firstName,
                    lastName = guestNameModel.lastName,
                    emailAddress = guestNameModel.emailAddress
                ),
                roomType = roomTypeCode, token = token, ratePlanCode = rateType))
            .subscribeOn(Schedulers.io())
            .subscribe({ state ->
                if(state.tempBookingRef.isNotEmpty()) {
                    publish(AmendGuestsRoomStateEvent.AddNewRoomSuccessEvent(guestNameModel))
                } else {
                    crashlyticsLogger.logException(Throwable(), "addNewRoom() Error: tempBookingRef returned as null")
                    publish(AmendGuestsRoomStateEvent.AddNewRoomFailEvent(stringResourceProvider.genericErrorMessage))
                }
            }, {
                crashlyticsLogger.logException(it, "addNewRoom() Error")
                publish(AmendGuestsRoomStateEvent.AddNewRoomFailEvent(stringResourceProvider.genericErrorMessage))
            })
            .addDisposable()
    }

    fun editRoom(
        roomId: String, tempBasketRef: String, token: String, newCriteriaState: RoomCriteria,
        leadGuestInput: NameModel, showUpsells: Boolean
    ) {
        graphQLAmendUseCase.amendEditRoom(
            AmendEditRoomRequestBody(
                bookingChannel = BookingChannelDetails(
                    if (input.isBusinessBooking) Channel.BB.name else Channel.PI.name,
                    SUB_CHANNEL,
                    deviceLocaleProvider.getDeviceLanguage()
                ),
                tempBookingRef = tempBasketRef, reservationId = roomId,
                roomOccupancy = RoomOccupancyAmend(
                    adultsNumber = newCriteriaState.numberOfAdults,
                    childrenNumber = newCriteriaState.numberOfChildren, cotRequired = false
                ),
                leadGuest = LeadGuestAmend(
                    title = leadGuestInput.title,
                    firstName = leadGuestInput.firstName,
                    lastName = leadGuestInput.lastName,
                    emailAddress = leadGuestInput.emailAddress
                ),
                roomType = newCriteriaState.roomType.code, token = token
            ),
            input.isBusinessBooking
        )
            .subscribeOn(Schedulers.io())
            .subscribe({ state ->
                if (state.tempBookingRef.isNotEmpty()) {
                    publish(AmendGuestsRoomStateEvent.EditNewRoomSuccessEvent(showUpsells))
                } else {
                    crashlyticsLogger.logException(
                        Throwable(),
                        "amendEditRoom() Error: tempBookingRef returned as null"
                    )
                    applyState(Reducer { it.copy(updateState = null) })
                    publish(AmendGuestsRoomStateEvent.EditNewRoomFailEvent(stringResourceProvider.genericErrorMessage))
                }
            }, { throwable ->
                crashlyticsLogger.logException(throwable, "amendEditRoom() Error")
                applyState(Reducer { it.copy(updateState = null) })
                publish(AmendGuestsRoomStateEvent.EditNewRoomFailEvent(stringResourceProvider.genericErrorMessage))
            }).addDisposable()
    }

    fun updateReservationInDbAfterEditRoomSuccess(updatedReservation: Reservation?) {
        updatedReservation?.let {
            storeUpdatedAmendedReservation.performUpdate(updatedReservation.bookingReference, updatedReservation)
                .subscribeOn(Schedulers.io())
                .subscribe { state ->
                    if (state is StoreUpdatedAmendedReservation.UpdateState.Error) {
                        crashlyticsLogger.logException(state.exception ?: Throwable(), "updateReservationInDbAfterEditRoomSuccess() UpdateState Error")
                        publish(AmendGuestsRoomStateEvent.SaveGuestAmendDetailsFailed("Unable to amend now"))
                    }
                }
        }
    }

    fun makeBookingConfirmationCallAfterAddRoom(
        tempBasketRef: String,
        uuidBasketReference: String,
        token: String,
        hotelName: String,
        guest: Guest,
        arrivalDeparturePair: Pair<LocalDate, LocalDate>
    ) {
        val dateFormatterForArrAndDepDate = DateTimeFormatter.ofPattern(DateFormat.DASHED_YEAR_MONTH_DAY)
        val dateTimeFormatterForUserDateTime = DateTimeFormatter
            .ofPattern(DateFormat.DATE_TIME_WITH_OFFSET)
        graphQLAmendUseCase.bookingConfirmationCall(
            reservationId,
            arrivalDeparturePair.first.format(dateFormatterForArrAndDepDate),
            arrivalDeparturePair.second.format(dateFormatterForArrAndDepDate),
            uuidBasketReference,
            deviceLocaleProvider.getDeviceLocale().country.lowercase(),
            deviceLocaleProvider.getDeviceLanguage(), input.hotelCode(),
            hotelName,
            URLEncoder.encode(token, "UTF-8"), OffsetDateTime.now().format(dateTimeFormatterForUserDateTime),
            BookingChannelDetails(
                if (input.isBusinessBooking) Channel.BB.name else Channel.PI.name,
                SUB_CHANNEL,
                deviceLocaleProvider.getDeviceLanguage().lowercase()
            ),
            tempBasketRef,
            isAddRoom = true,
            isRemoveRoom = false,
            isBusinessBooking = input.isBusinessBooking
        )
            .mapToAsyncResult()
            .subscribeOn(Schedulers.io())
            .subscribe { state ->
                if (state is AsyncResult.Error) {
                    crashlyticsLogger.logException(state.error, "makeBookingConfirmationCallAfterAddRoom() result Error")
                    publish(AmendGuestsRoomStateEvent.SaveUpdatedBookingConfirmationForAddRoomFailed)
                } else if (state is AsyncResult.Success){
                    publish(AmendGuestsRoomStateEvent.SavingBookingConfirmAfterAddRoomSuccessEvent(guest))
                }
            }
            .addDisposable()
    }

    private fun RoomCriteria.updateWith(updatedCriteria: RoomCriteria): RoomCriteria {
        return copy(
                numberOfAdults = updatedCriteria.numberOfAdults,
                numberOfChildren = updatedCriteria.numberOfChildren,
                includeCot = updatedCriteria.includeCot,
                roomType = updatedCriteria.roomType,
                roomId = updatedCriteria.roomId,
                roomNumber = updatedCriteria.roomNumber
        )
    }

    private fun Guest.updateWith(updatedGuestName: NameModel): Guest {
        return copy(
                title = updatedGuestName.title,
                firstName = updatedGuestName.firstName,
                lastName = updatedGuestName.lastName,
                emailAddress = updatedGuestName.emailAddress
        )
    }

    private fun logCheckAvailabilityAnalytics() {
        trackingAnalytics.track(AMEND_AVAILABILITY,
            AmendCheckAvailabilityData(bookingRef = input.bookingReference(),
                nightsChanged = "0",
                roomsChanged = "1",
                roomTypeChanged = false,
                amendChangesDescription = ""))
    }

    companion object {
        const val TEMP_TITLE = "Mr"
        const val TEMP_FIRST_NAME = "Test"
        const val TEMP_LAST_NAME = "Test"
    }

}