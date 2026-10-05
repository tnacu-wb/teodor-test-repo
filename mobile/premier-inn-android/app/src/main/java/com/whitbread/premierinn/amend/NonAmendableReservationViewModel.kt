package com.whitbread.premierinn.amend

import androidx.lifecycle.SavedStateHandle
import com.whitbread.premierinn.amend.NonAmendableReservationActivity.Companion.EXTRA_HOTEL_NAME
import com.whitbread.premierinn.amend.NonAmendableReservationActivity.Companion.EXTRA_NON_AMENDABLE_INPUT
import com.whitbread.premierinn.amend.NonAmendableReservationActivity.Companion.NIGHTS_COUNT
import com.whitbread.premierinn.amend.NonAmendableReservationActivity.Companion.ROOM_CRITERIA_SIZE
import com.whitbread.premierinn.amend.NonAmendableReservationViewModel.NonAmendableReservationEvent
import com.whitbread.premierinn.amend.NonAmendableReservationViewModel.NonAmendableReservationEvent.CancelBookingEvent
import com.whitbread.premierinn.amend.NonAmendableReservationViewModel.NonAmendableReservationEvent.GenericErrorEvent
import com.whitbread.premierinn.amend.NonAmendableReservationViewModel.NonAmendableReservationEvent.ShowCancelBookingDialog
import com.whitbread.premierinn.amend.analytics.CancelBookingAnalyticsData
import com.whitbread.premierinn.common.AsyncResult
import com.whitbread.premierinn.common.Reducer
import com.whitbread.premierinn.common.RxViewModelStore
import com.whitbread.premierinn.common.analytics.AnalyticsConstants
import com.whitbread.premierinn.common.analytics.TrackingAnalytics
import com.whitbread.premierinn.common.format.DateFormat
import com.whitbread.premierinn.common.managebooking.ManageBookingInput
import com.whitbread.premierinn.common.mapToAsyncResult
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManager
import com.whitbread.premierinn.domain.common.SUB_CHANNEL
import com.whitbread.premierinn.domain.graphql.bookingDetails.usecase.GraphQLBookingDetailsUseCase
import com.whitbread.premierinn.domain.graphql.requestBodyModels.BookingChannelDetails
import com.whitbread.premierinn.domain.graphql.requestBodyModels.CancelInformationRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.CancelReservationRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.Channel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.schedulers.Schedulers
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.net.URLEncoder
import javax.inject.Inject

@HiltViewModel
class NonAmendableReservationViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val graphQLBookingDetailsUseCase: GraphQLBookingDetailsUseCase,
    getDeviceLocaleProvider: DeviceLocaleProvider,
    private val storage: SimplePersistenceManager,
    private val trackingAnalytics: TrackingAnalytics,
) : RxViewModelStore<NonAmendableReservationState, NonAmendableReservationEvent>(NonAmendableReservationState()) {

    private val input: ManageBookingInput by lazy {
        requireNotNull(savedStateHandle.get<ManageBookingInput>(EXTRA_NON_AMENDABLE_INPUT))
    }
    private val nightsCount: Int by lazy {
        savedStateHandle.get<Int>(NIGHTS_COUNT) ?: -1
    }
    private val roomCriteriaSize: Int by lazy {
        savedStateHandle.get<Int>(ROOM_CRITERIA_SIZE)  ?: -1
    }
    private val uuidBasketReference: String by lazy {
        savedStateHandle.get<String>(UUID_BASKET_REFERENCE) ?: ""
    }
    private val token: String  by lazy {
        savedStateHandle.get<String>(TOKEN) ?: ""
    }
    private val hotelName: String  by lazy {
        requireNotNull(savedStateHandle.get<String>(EXTRA_HOTEL_NAME))
    }

    init {
        val dateTimeFormatter = DateTimeFormatter.ofPattern(DateFormat.DATE_TIME_WITH_OFFSET)
        graphQLBookingDetailsUseCase.bookingConfirmationAndManageBooking(
            basketReference = uuidBasketReference,
            country = getDeviceLocaleProvider.getDeviceLocale().country.lowercase(),
            language = getDeviceLocaleProvider.getDeviceLanguage().lowercase(),
            bookingChannel = Channel.BB.name.takeIf { input.isBusinessBooking } ?: Channel.PI.name,
            hotelName = hotelName,
            cancelInformationRequestBody = CancelInformationRequestBody(
                uuidBasketReference,
                input.hotelCode(),
                OffsetDateTime.now().format(dateTimeFormatter),
                URLEncoder.encode(token, "UTF-8"),
                BookingChannelDetails(
                    Channel.BB.name.takeIf { input.isBusinessBooking } ?: Channel.PI.name,
                    SUB_CHANNEL,
                    getDeviceLocaleProvider.getDeviceLanguage().lowercase()
                )
            )
        )
            .mapToAsyncResult()
            .subscribeOn(Schedulers.io())
            .subscribe { result ->
                applyState(Reducer { it.copy(reservation = result) })
                if (result is AsyncResult.Error) {
                    publish(GenericErrorEvent(result.error))
                }
            }
            .addDisposable()
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
            }, { e ->
                publish(GenericErrorEvent(e))
            })
            .addDisposable()
    }

    private fun logCancelAnalytics() {
        trackingAnalytics.trackAction(
            AnalyticsConstants.Action.CANCEL_BOOKING,
            CancelBookingAnalyticsData(
                isCancelled = true,
                bookingRef = input.bookingReference(),
                cancelNights = nightsCount.toString(),
                cancelRooms = roomCriteriaSize.toString()
            )
        )
    }

    fun refreshDashboard() {
        storage.setRefreshDashboard(true)
    }

    sealed class NonAmendableReservationEvent {
        data class GenericErrorEvent(val error: Throwable) : NonAmendableReservationEvent()
        data object ShowCancelBookingDialog : NonAmendableReservationEvent()
        data object CancelBookingEvent : NonAmendableReservationEvent()
    }
}