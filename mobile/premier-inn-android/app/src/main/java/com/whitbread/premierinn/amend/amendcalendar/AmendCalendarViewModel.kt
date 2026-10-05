package com.whitbread.premierinn.amend.amendcalendar

import androidx.lifecycle.SavedStateHandle
import com.whitbread.premierinn.amend.AmendStringProvider
import com.whitbread.premierinn.amend.amendguestsrooms.EXTRA_AMEND_INPUT
import com.whitbread.premierinn.amend.analytics.AmendBookingData
import com.whitbread.premierinn.common.AsyncResult
import com.whitbread.premierinn.common.Reducer
import com.whitbread.premierinn.common.RxViewModelStore
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.ScreenState.AMEND_DATE
import com.whitbread.premierinn.common.analytics.TrackingAnalytics
import com.whitbread.premierinn.common.managebooking.ManageBookingInput
import com.whitbread.premierinn.common.mapToAsyncResult
import com.whitbread.premierinn.common.service.LogService
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.domain.common.PriceDomain
import com.whitbread.premierinn.domain.common.SUB_CHANNEL
import com.whitbread.premierinn.domain.graphql.amend.entity.AmendSummaryDomain
import com.whitbread.premierinn.domain.graphql.amend.usecase.GraphQLAmendUseCase
import com.whitbread.premierinn.domain.graphql.requestBodyModels.AmendSummaryRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.BookingChannelDetails
import com.whitbread.premierinn.domain.graphql.requestBodyModels.ChangeBookingDatesRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.Channel
import com.whitbread.premierinn.domain.reservation.usecase.ObserveAmendedReservationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.threeten.bp.LocalDate
import org.threeten.bp.Period
import javax.inject.Inject

@HiltViewModel
class AmendCalendarViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    observeAmendedReservation: ObserveAmendedReservationUseCase,
    private val stringResourceProvider: AmendStringProvider,
    private val graphQLAmendUseCase: GraphQLAmendUseCase,
    private val deviceLocaleProvider: DeviceLocaleProvider,
    private val crashlyticsLogger: LogService,
    private val trackingAnalytics: TrackingAnalytics,
) : RxViewModelStore<AmendCalendarState, AmendCalendarViewModel.AmendCalendarEvent>(AmendCalendarState(stringResourceProvider = stringResourceProvider)) {
    private val input: ManageBookingInput by lazy {
        requireNotNull(savedStateHandle.get<ManageBookingInput>(EXTRA_AMEND_INPUT))
    }
    init {
        observeAmendedReservation(input.bookingReference())
                .mapToAsyncResult()
                .subscribeOn(Schedulers.io())
                .subscribe { result ->
                    applyState(Reducer { it.copy(amendedReservation = result) })
                    trackingAnalytics.track(AMEND_DATE, AmendBookingData(input.bookingReference()))
                }
                .addDisposable()
    }

    fun makePromoInfoCallAndChangeDates(
        token: String?,
        tempBasketRef: String?,
        updatedDates: Pair<LocalDate, LocalDate>,
        isBusinessBooking: Boolean,
        isPromoBooking: Boolean,
        originalBasketReference: String,
        brand: String
    ) {
        if (isPromoBooking) {
            graphQLAmendUseCase.getPromotionInformation(
                country = deviceLocaleProvider.getDeviceLocale().country.lowercase(),
                language = deviceLocaleProvider.getDeviceLanguage().lowercase(),
                channel = Channel.BB.name.takeIf { isBusinessBooking }
                    ?: Channel.PI.name,
                brand = brand,
                stayStartDate = updatedDates.first.toString(),
                stayEndDate = updatedDates.second.toString(),
                basketReference = originalBasketReference
            )
                .subscribeOn(Schedulers.io())
                .subscribe({ promoInfoResponse ->
                    if (promoInfoResponse.isWithinPromoWindow == true) {
                        makeChangeDates(token, tempBasketRef, updatedDates, isBusinessBooking)
                    } else {
                        val errorMessage = promoInfoResponse.appPromoAmendMessage ?: stringResourceProvider.availabilityChangeDatesErrorMessage
                        publish(StayDatesErrorEvent(Throwable(errorMessage)))
                    }
                }, { error ->
                    crashlyticsLogger.logException(error, "getPromotionInformation() Error")
                    publish(StayDatesErrorEvent(Throwable(stringResourceProvider.availabilityChangeDatesErrorMessage)))
                })
                .addDisposable()
        } else {
            makeChangeDates(token, tempBasketRef, updatedDates, isBusinessBooking)
        }
    }

    private fun makeChangeDates(
        token: String?,
        tempBasketRef: String?,
        updatedDates: Pair<LocalDate, LocalDate>,
        isBusinessBooking: Boolean
    ) {
        graphQLAmendUseCase.changeBookingDates(ChangeBookingDatesRequestBody(
            tempBookingRef = tempBasketRef!!,
            newStartDate = updatedDates.first.toString(),
            newEndDate = updatedDates.second.toString(),
            token = token!!,
            bookingChannel = BookingChannelDetails(
                Channel.BB.name.takeIf { isBusinessBooking } ?: Channel.PI.name,
                SUB_CHANNEL,
                deviceLocaleProvider.getDeviceLanguage())
        ))
            .subscribeOn(Schedulers.io())
            .subscribe({ state ->
                if (!state.tempBasket.isNullOrEmpty()) {
                    publish(StayDatesSuccessEvent)
                } else {
                    publish(StayDatesErrorEvent(Throwable(stringResourceProvider.availabilityChangeDatesErrorMessage)))
                    crashlyticsLogger.logException(Throwable(), "changeBookingDates() Error: tempBasket returned as null")
                    applyState(Reducer { it.copy(errorMessageStays = stringResourceProvider.availabilityChangeDatesErrorMessage) })
                }
            }, {
                publish(StayDatesErrorEvent(Throwable(stringResourceProvider.availabilityChangeDatesErrorMessage)))
                crashlyticsLogger.logException(it, "changeBookingDates() Error")
                applyState(Reducer { it.copy(errorMessageStays = stringResourceProvider.availabilityChangeDatesErrorMessage) })
            })
            .addDisposable()
    }
    fun amendSummaryCall(
        tempBasketRef: String,
        uuidBasketReference: String,
        token: String,
    ) {
        graphQLAmendUseCase.amendSummary(
            AmendSummaryRequestBody(
                tempBasketReference = tempBasketRef,
                originalBasketReference = uuidBasketReference,
                token = token,
                bookingChannel = BookingChannelDetails(
                    if (input.isBusinessBooking) Channel.BB.name else Channel.PI.name,
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
                            publish(AmendSummarySuccessEvent(response))
                        } else {
                            crashlyticsLogger.logException(
                                Throwable(stringResourceProvider.availabilityErrorMessage),
                                "amendSummaryCall() paymentOptions returned as null")
                            publish(GenericErrorEvent(Throwable(stringResourceProvider.availabilityErrorMessage)))
                        }
                    } ?: run {
                        crashlyticsLogger.logException(Throwable(stringResourceProvider.availabilityErrorMessage), "amendSummaryCall() response returned as null")
                        publish(GenericErrorEvent(Throwable(stringResourceProvider.availabilityErrorMessage)))
                    }
                }
            }, { error ->
                crashlyticsLogger.logException(error, "amendSummaryCall() Error")
                publish(GenericErrorEvent(error))
            })
            .addDisposable()
    }

    fun createTotalCost(totalCost: PriceDomain): String {
        return stringResourceProvider.getTotalPriceText(totalCost)
    }

    fun getRestrictedNights(arrivalDate: LocalDate?, departureDate: LocalDate?, amendRestrictions: Boolean): Long {
        return if (arrivalDate != null && departureDate != null && amendRestrictions) {
            Period.between(arrivalDate, departureDate).days.toLong()
        } else 0L
    }

    sealed class AmendCalendarEvent
    data object StayDatesSuccessEvent : AmendCalendarEvent()
    data class AmendSummarySuccessEvent(val amendSummaryDomain: AmendSummaryDomain) : AmendCalendarEvent()
    data object AmendedDatesAndUpsellsSavedEvent : AmendCalendarEvent()
    data class GenericErrorEvent(val error: Throwable) : AmendCalendarEvent()
    data class StayDatesErrorEvent(val error: Throwable) : AmendCalendarEvent()
}
