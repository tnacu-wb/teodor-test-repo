package com.whitbread.premierinn.amend

import android.widget.TextView
import androidx.lifecycle.SavedStateHandle
import com.whitbread.premierinn.amend.AmendReservationViewModel.AmendReservationEvent.CancelBookingEvent
import com.whitbread.premierinn.amend.AmendReservationViewModel.AmendReservationEvent.GenericErrorEvent
import com.whitbread.premierinn.amend.AmendReservationViewModel.AmendReservationEvent.LaunchAmendError
import com.whitbread.premierinn.amend.AmendReservationViewModel.AmendReservationEvent.RemoveRoomAmendError
import com.whitbread.premierinn.amend.AmendReservationViewModel.AmendReservationEvent.RemoveRoomSuccessEvent
import com.whitbread.premierinn.amend.AmendReservationViewModel.AmendReservationEvent.ShowCancelBookingDialog
import com.whitbread.premierinn.amend.AmendReservationViewModel.AmendReservationEvent.ShowRemoveBookingDialog
import com.whitbread.premierinn.amend.analytics.AmendBookingData
import com.whitbread.premierinn.amend.analytics.CancelBookingAnalyticsData
import com.whitbread.premierinn.businessbooker.data.common.persistence.BusinessPersistenceManager
import com.whitbread.premierinn.common.AsyncResult
import com.whitbread.premierinn.common.Reducer
import com.whitbread.premierinn.common.RxViewModelStore
import com.whitbread.premierinn.common.analytics.AnalyticsConstants
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.ScreenState.AMEND_BOOKING
import com.whitbread.premierinn.common.analytics.TrackingAnalytics
import com.whitbread.premierinn.common.format.DateFormat
import com.whitbread.premierinn.common.managebooking.ManageBookingInput
import com.whitbread.premierinn.common.mapToAsyncResult
import com.whitbread.premierinn.common.service.LogService
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManager
import com.whitbread.premierinn.domain.common.PriceDomain
import com.whitbread.premierinn.domain.common.SUB_CHANNEL
import com.whitbread.premierinn.domain.common.hoteldetails.usecase.GraphQLHotelDetailsUseCase
import com.whitbread.premierinn.domain.common.nightsCount
import com.whitbread.premierinn.domain.graphql.amend.entity.AmendSummaryDomain
import com.whitbread.premierinn.domain.graphql.amend.usecase.GraphQLAmendUseCase
import com.whitbread.premierinn.domain.graphql.bookingDetails.usecase.GraphQLBookingDetailsUseCase
import com.whitbread.premierinn.domain.graphql.requestBodyModels.AmendSummaryRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.BookingChannelDetails
import com.whitbread.premierinn.domain.graphql.requestBodyModels.CancelReservationRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.Channel
import com.whitbread.premierinn.domain.graphql.requestBodyModels.CopyBookingRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.RemoveRoomRequestBody
import com.whitbread.premierinn.domain.reservation.usecase.ClearReservationUseCase
import com.whitbread.premierinn.domain.reservation.usecase.ObserveAmendedReservationUseCase
import com.whitbread.premierinn.domain.reservation.usecase.ObserveBookingUseCase
import com.whitbread.premierinn.domain.reservation.usecase.OriginalReservationUseCase
import com.whitbread.premierinn.domain.reservation.usecase.RemoveRoomUseCase
import com.whitbread.premierinn.domain.reservation.usecase.RemoveStoredRoomUseCase
import com.whitbread.premierinn.domain.reservation.usecase.UpsellsAvailableUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.threeten.bp.LocalDate
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.net.URLEncoder
import javax.inject.Inject

@HiltViewModel
class AmendReservationViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    getBookingDetails: ObserveBookingUseCase,
    observeAmendedReservation: ObserveAmendedReservationUseCase,
    private val originalReservation: OriginalReservationUseCase,
    private val clearReservation: ClearReservationUseCase,
    private val removeRoomUseCase: RemoveRoomUseCase,
    private val removeStoredRoomUseCase: RemoveStoredRoomUseCase,
    private val stringResourceProvider: AmendStringProvider,
    upsellsAvailableUseCase: UpsellsAvailableUseCase,
    private val trackingAnalytics: TrackingAnalytics,
    private val storage: SimplePersistenceManager,
    businessStorage: BusinessPersistenceManager,
    private val deviceLocaleProvider: DeviceLocaleProvider,
    graphQLHotelDetailsUseCase: GraphQLHotelDetailsUseCase,
    private val graphQLAmendUseCase: GraphQLAmendUseCase,
    private val graphQLBookingDetailsUseCase: GraphQLBookingDetailsUseCase,
    private val crashlyticsLogger: LogService
) : RxViewModelStore<AmendReservationState, AmendReservationViewModel.AmendReservationEvent>(AmendReservationState(
    deviceLocaleProvider.getDeviceLanguage(), storage, businessStorage
)) {

    fun setPromotionInformation(info: ParcelablePromotionsInformationDomain?) {
        val isPromoBooking = !info?.promoBookingInfo?.promotionCode.isNullOrEmpty()
        applyState(Reducer {
            it.copy(isPromotionalBooking = isPromoBooking)
        })
    }

    private val input: ManageBookingInput by lazy {
        requireNotNull(savedStateHandle.get<ManageBookingInput>(EXTRA_AMEND_INPUT))
    }
    private val isEciLcoBooking: Boolean by lazy {
        savedStateHandle.get<Boolean>(IS_ECI_LCO_BOOKING) ?: false
    }
    private val isBusinessBooking: Boolean by lazy {
        savedStateHandle.get<Boolean>(IS_BUSINESS_BOOKING) ?: false
    }
    private val uuidBasketReference: String by lazy {
        savedStateHandle.get<String>(UUID_BASKET_REFERENCE) ?: ""
    }
    private val token: String by lazy {
        savedStateHandle.get<String>(TOKEN) ?: ""
    }

    private lateinit var arrivalDeparturePair: Pair<LocalDate, LocalDate>
    private lateinit var hotelName: String
    private var channel: String = if (isBusinessBooking) Channel.BB.name else Channel.PI.name

    init {
        graphQLHotelDetailsUseCase.fetchHotelInfoFromGQL(
            deviceLocaleProvider.getDeviceLocale().country.lowercase(),
            input.hotelCode(),
            deviceLocaleProvider.getDeviceLanguage().lowercase()
        )
            .map { Pair(it.name, it.galleryImages) }
            .mapToAsyncResult()
            .subscribeOn(Schedulers.io())
            .subscribe { result ->
                when (result) {
                    is AsyncResult.Success -> {
                        hotelName = result.data?.first ?: EMPTY_STRING
                        applyState(Reducer {
                            it.copy(
                                hotelName = result.data?.first ?: EMPTY_STRING,
                                galleryImages = result.data?.second?.toImageSrcList()
                            )
                        })
                    }
                    is AsyncResult.Error -> {
                        applyState(Reducer { it.copy(hotelName = EMPTY_STRING) })
                        crashlyticsLogger.logException(
                            result.error,
                            "fetchHotelInfoFromGQL() result Error"
                        )
                        publish(LaunchAmendError)
                    }
                    is AsyncResult.Loading -> {
                        // Initial state is already null/loading, no action needed
                    }
                }
            }
            .addDisposable()

        val dateTimeFormatterForUserDateTime = DateTimeFormatter
            .ofPattern(DateFormat.DATE_TIME_WITH_OFFSET)
        val dateFormatterForArrAndDepDate =
            DateTimeFormatter.ofPattern(DateFormat.DASHED_YEAR_MONTH_DAY)
        graphQLAmendUseCase.copyBookingAndBookingConfirmationWithSavingToDb(
            input.bookingReference(),
            input.arrivalDate().format(dateFormatterForArrAndDepDate),
            input.departureDate().format(dateFormatterForArrAndDepDate),
            CopyBookingRequestBody(
                uuidBasketReference,
                token,
                BookingChannelDetails(
                    channel,
                    SUB_CHANNEL,
                    deviceLocaleProvider.getDeviceLanguage()
                )
            ),
            uuidBasketReference,
            deviceLocaleProvider.getDeviceLocale().country.lowercase(),
            deviceLocaleProvider.getDeviceLanguage(),
            input.hotelCode(),
            URLEncoder.encode(token, "UTF-8"),
            OffsetDateTime.now().format(dateTimeFormatterForUserDateTime),
            BookingChannelDetails(
                channel,
                SUB_CHANNEL,
                deviceLocaleProvider.getDeviceLanguage().lowercase()
            ),
            isBusinessBooking
        )
            .mapToAsyncResult()
            .subscribeOn(Schedulers.io())
            .subscribe { result ->
                when (result) {
                    is AsyncResult.Success -> {
                        applyState(Reducer {
                            it.copy(
                                startAmendResult = AsyncResult.Success(null),
                                pairOfMealsAndAncillaryCloseoutItems = Pair(
                                    result.data?.packages?.packages?.meals ?: emptyList(),
                                    result.data?.ancillaryCloseOutItems ?: emptyList()
                            ))
                        })
                    }
                    is AsyncResult.Loading -> {
                        applyState(Reducer {
                            it.copy(
                                startAmendResult = AsyncResult.Loading()
                            )
                        })
                    }
                    is AsyncResult.Error -> {
                        applyState(Reducer {
                            it.copy(
                                startAmendResult = AsyncResult.Error(result.error)
                            )
                        })
                        crashlyticsLogger.logException(
                            result.error,
                            "copyBookingAndBookingConfirmationWithSavingToDb() result Error"
                        )
                        publish(LaunchAmendError)
                    }
                }
                trackingAnalytics.track(AMEND_BOOKING, AmendBookingData(input.bookingReference()))
            }
            .addDisposable()

        observeAmendedReservation(input.bookingReference())
            .mapToAsyncResult()
            .subscribeOn(Schedulers.io())
            .subscribe { result -> applyState(Reducer { it.copy(amendedReservation = result) }) }
            .addDisposable()

        getBookingDetails.invoke(input.bookingReference())
            .mapToAsyncResult()
            .subscribeOn(Schedulers.io())
            .subscribe { result ->
                applyState(Reducer {
                    it.copy(
                        originalBooking = result,
                        isEciLcoBooking = isEciLcoBooking,
                        isEmployeeBooking = input.isEmployeeBooking,
                        isBusinessBooking = isBusinessBooking
                    )
                })
            }
            .addDisposable()

        upsellsAvailableUseCase.invoke()
            .mapToAsyncResult()
            .subscribeOn(Schedulers.io())
            .subscribe { result -> applyState(Reducer { it.copy(upsellsAvailable = result) }) }
            .addDisposable()
    }

    fun getOriginalReservation() {
        applyState(Reducer { it.copy(originalReservation = originalReservation.invoke()) })
    }

    fun onCancelClicked() {
        publish(ShowCancelBookingDialog)
    }

    fun onCancelBookingConfirmed() {
        graphQLBookingDetailsUseCase.cancelReservation(
            CancelReservationRequestBody(
                uuidBasketReference,
                input.hotelCode(),
                token
            ),
            input.bookingReference()
        )
            .doOnSubscribe { applyState(Reducer { it.copy(cancelBookingInProgress = true) }) }
            .doAfterTerminate { applyState(Reducer { it.copy(cancelBookingInProgress = false) }) }
            .subscribeOn(Schedulers.io())
            .subscribe({
                publish(CancelBookingEvent)
                logCancelAnalytics()
            }, {
                publish(GenericErrorEvent(null))
            })
            .addDisposable()
    }

    fun onRemoveClicked(roomId: String, position: Int, removeButton: TextView) {
        publish(ShowRemoveBookingDialog(roomId, position, removeButton))
    }

    fun onRemoveRoomConfirmed(roomId: String) {
        removeRoomUseCase.invoke(
            RemoveRoomRequestBody(
                tempBookingRef = getTempBasketRef(),
                reservationId = roomId,
                token = URLEncoder.encode(token, "UTF-8"),
                BookingChannelDetails(
                    channel,
                    SUB_CHANNEL,
                    deviceLocaleProvider.getDeviceLanguage().lowercase()
                )
            )
        )
            .subscribeOn(Schedulers.io())
            .subscribe({ success ->
                if (success.tempBookingRef.isNotEmpty()) {
                    publish(RemoveRoomSuccessEvent(roomId))
                } else {
                    crashlyticsLogger.logException(
                        Throwable(),
                        "removeRoom() Error: tempBookingRef returned as null"
                    )
                    publish(RemoveRoomAmendError(roomId))
                }
            }, {
                crashlyticsLogger.logException(it, "removeRoom() Error")
                publish(RemoveRoomAmendError(roomId))
            }).addDisposable()
    }

    fun removeRoomOperaFromStorage(reservationId: String, bookingRef: String) {
        removeStoredRoomUseCase.invoke(reservationId, bookingRef)
            .subscribeOn(Schedulers.io())
            .subscribe { state ->
                applyState(Reducer { it.copy(removeRoomFromStorageState = state) })
                if (state is RemoveStoredRoomUseCase.RemoveRoomStorageState.Error ) {
                    crashlyticsLogger.logException(state.exception ?: Throwable(), "removeRoomOperaFromStorage() RemoveRoomStorageStateOpera Error")
                    publish(GenericErrorEvent(state.exception))
                }
            }
            .addDisposable()
    }

    fun amendSummaryCall() {
        graphQLAmendUseCase.amendSummary(
            AmendSummaryRequestBody(
                tempBasketReference = getTempBasketRef(),
                originalBasketReference = uuidBasketReference,
                token = token,
                bookingChannel = BookingChannelDetails(
                    channel,
                    SUB_CHANNEL,
                    deviceLocaleProvider.getDeviceLanguage().lowercase()
                ),
                country = deviceLocaleProvider.getDeviceLocale().country.lowercase()
            )
        )
            .mapToAsyncResult()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ result ->
                if (result is AsyncResult.Success) {
                    result.data?.let { response ->
                        if (response.paymentOptions != null) {
                            publish(AmendReservationEvent.AmendSummarySuccessEvent(response))
                        } else {
                            crashlyticsLogger.logException(Throwable(), "amendSummaryCall() Error: paymentOptions returned as null")
                            publish(GenericErrorEvent(Throwable("Error Occurred")))
                        }
                    } ?: run {
                        crashlyticsLogger.logException(Throwable(), "amendSummaryCall() Error: response returned as null")
                        publish(GenericErrorEvent(Throwable("Error Occurred")))
                    }
                }
            }, { error ->
                crashlyticsLogger.logException(error, "amendSummaryCall() Error")
                publish(GenericErrorEvent(error))
            })
            .addDisposable()
    }

    fun bookingConfPackagesAndAmendSummary(arrivalDate: LocalDate, departureDate: LocalDate) {
        applyState(Reducer { it.copy(amendSummaryInProgress = true) })

        val dateTimeFormatterForUserDateTime = DateTimeFormatter
            .ofPattern(DateFormat.DATE_TIME_WITH_OFFSET)
        val dateFormatterForArrAndDepDate = DateTimeFormatter.ofPattern(DateFormat.DASHED_YEAR_MONTH_DAY)

        graphQLAmendUseCase.bookingConfirmationCallAndAmendSummaryCall(
            bookingRef = input.bookingReference(),
            arrivalDate = arrivalDate.format(dateFormatterForArrAndDepDate),
            departureDate = departureDate.format(dateFormatterForArrAndDepDate),
            originalBasketReference = uuidBasketReference,
            country = deviceLocaleProvider.getDeviceLocale().country.lowercase(),
            deviceLanguage = deviceLocaleProvider.getDeviceLanguage(),
            hotelCode = input.hotelCode(),
            hotelName = this.hotelName,
            token = token,
            dateTimeFormatString = OffsetDateTime.now().format(dateTimeFormatterForUserDateTime),
            bookingChannelDetails = BookingChannelDetails(
                channel,
                SUB_CHANNEL,
                deviceLocaleProvider.getDeviceLanguage().lowercase()
            ),
            tempBasketReference = getTempBasketRef(),
            isBusinessBooking = isBusinessBooking
        ).mapToAsyncResult()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe { result ->
                if (result is AsyncResult.Success) {
                    val (amendSummary, packagesAndAncillaries) = result.data ?: (null to null)
                    if (amendSummary != null && amendSummary != AmendSummaryDomain.createWithDefaults()) {
                        publish(AmendReservationEvent.AmendSummaryAfterChangeDates(amendSummary))
                        packagesAndAncillaries?.let { closeoutData ->
                            applyState(Reducer {
                                it.copy(
                                    amendSummaryInProgress = false,
                                    pairOfMealsAndAncillaryCloseoutItems = Pair(
                                        closeoutData.packages.packages.meals,
                                        closeoutData.ancillaryCloseOutItems
                                ))
                            })
                        } ?: run {
                            applyState(Reducer { it.copy(amendSummaryInProgress = false) })
                        }
                    } else {
                        crashlyticsLogger.logException(Throwable(), "bookingConfPackagesAndAmendSummary() Error: response returned as null")
                        publish(GenericErrorEvent(Throwable("Error Occurred")))
                    }
                }

                if (result is AsyncResult.Error) {
                    crashlyticsLogger.logException(result.error, "bookingConfPackagesAndAmendSummary() result Error")
                    publish(GenericErrorEvent(result.error))
                }
            }
            .addDisposable()
    }

    fun makeBookingConfirmationCallAfterRemoveRoom(roomId: String) {
        val dateFormatterForArrAndDepDate =
            DateTimeFormatter.ofPattern(DateFormat.DASHED_YEAR_MONTH_DAY)
        val dateTimeFormatterForUserDateTime = DateTimeFormatter
            .ofPattern(DateFormat.DATE_TIME_WITH_OFFSET)
        graphQLAmendUseCase.bookingConfirmationCall(
            input.bookingReference(),
            getArrivalDepartureDate().first.format(dateFormatterForArrAndDepDate),
            getArrivalDepartureDate().second.format(dateFormatterForArrAndDepDate),
            uuidBasketReference,
            deviceLocaleProvider.getDeviceLocale().country.lowercase(),
            deviceLocaleProvider.getDeviceLanguage(),
            input.hotelCode(),
            hotelName,
            URLEncoder.encode(token, "UTF-8"),
            OffsetDateTime.now().format(dateTimeFormatterForUserDateTime),
            BookingChannelDetails(
                channel,
                SUB_CHANNEL,
                deviceLocaleProvider.getDeviceLanguage().lowercase()
            ),
            getTempBasketRef(),
            isAddRoom = false,
            isRemoveRoom = true,
            isBusinessBooking = isBusinessBooking
        )
            .mapToAsyncResult()
            .subscribeOn(Schedulers.io())
            .subscribe { state ->
                if (state is AsyncResult.Error) {
                    crashlyticsLogger.logException(state.error, "makeBookingConfirmationCallAfterRemoveRoom() result Error")
                    publish(AmendReservationEvent.SavingBookingConfirmAfterRemoveRoomFailureEvent)
                } else if (state is AsyncResult.Success){
                    publish(AmendReservationEvent.SavingBookingConfirmAfterRemoveRoomSuccessEvent(roomId))
                }
            }
            .addDisposable()
    }

    fun createPriceDifference(totalCost: PriceDomain): String {
        return stringResourceProvider.getTotalPriceText(totalCost)
    }

    fun clearReservation() {
        clearReservation.invoke()
    }

    fun deleteLinkedAmend() {
        graphQLAmendUseCase.removeReservationIdLinkedAmendDetailsInDao(input.bookingReference())
            .subscribeOn(Schedulers.io())
            .mapToAsyncResult()
            .subscribe { result ->
                if (result is AsyncResult.Error) {
                    crashlyticsLogger.logException(result.error, "deleteLinkedAmend() result Error")
                    publish(GenericErrorEvent(result.error))
                }
            }.addDisposable()
    }

    fun clearSavedRoomSelections() {
        storage.clearSavedOriginalRoomSelection()
        storage.clearSavedAmendedRoomSelection()
        storage.clearNewlyAddedRoomSelection()
        storage.clearRemovedRoomSelection()
        storage.clearBillingAddress()
    }

    fun refreshDashboard() {
        storage.setRefreshDashboard(true)
    }

    fun getTempBasketRef(): String {
        return storage.getTempBookingRef()
    }

    fun setArrivalAndDepartureDate(arrivalDate: LocalDate, departureDate: LocalDate) {
        this.arrivalDeparturePair = Pair(arrivalDate, departureDate)
    }

    fun getArrivalDepartureDate(): Pair<LocalDate, LocalDate> {
        return arrivalDeparturePair
    }
    private fun logCancelAnalytics() {
        val dates = storage.getReservation()!!.arrival to storage.getReservation()!!.departure
        storage.getReservation()!!.roomsCriteria.size.toString()
        trackingAnalytics.trackAction(
            AnalyticsConstants.Action.CANCEL_BOOKING,
            CancelBookingAnalyticsData(
                isCancelled = true,
                bookingRef = input.bookingReference(),
                cancelNights = dates.nightsCount().toString(),
                cancelRooms = storage.getReservation()!!.roomsCriteria.size.toString()
            )
        )
    }

    sealed class AmendReservationEvent {
        data class GenericErrorEvent(val error: Throwable?) : AmendReservationEvent()
        data object LaunchAmendError : AmendReservationEvent()
        data class RemoveRoomAmendError(val roomId:String) : AmendReservationEvent()
        data object ShowCancelBookingDialog : AmendReservationEvent()
        data object CancelBookingEvent : AmendReservationEvent()
        data class AmendSummarySuccessEvent(val amendSummaryDomain: AmendSummaryDomain) : AmendReservationEvent()
        data class AmendSummaryAfterChangeDates(val amendSummaryDomain: AmendSummaryDomain) : AmendReservationEvent()
        data object StartAmendSummaryCall : AmendReservationEvent()
        data class SavingBookingConfirmAfterRemoveRoomSuccessEvent(val roomId: String) : AmendReservationEvent()
        data object SavingBookingConfirmAfterRemoveRoomFailureEvent : AmendReservationEvent()
        data class ShowRemoveBookingDialog(val roomId: String, val position: Int, val button: TextView) : AmendReservationEvent()
        data class RemoveRoomSuccessEvent(val roomId: String) : AmendReservationEvent()
        data object HideCtaLoadingAndShowTranslucentSpinnerEvent : AmendReservationEvent()
    }
}
