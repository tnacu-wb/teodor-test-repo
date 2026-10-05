package com.whitbread.premierinn.amend.amendreview

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.SavedStateHandle
import com.google.gson.Gson
import com.whitbread.premierinn.R
import com.whitbread.premierinn.amend.AmendStringProvider
import com.whitbread.premierinn.amend.amendreview.ReviewAmendsActivity.Companion.AMEND_SUMMARY_DOMAIN
import com.whitbread.premierinn.amend.amendreview.ReviewAmendsActivity.Companion.EXTRA_AMEND_INPUT
import com.whitbread.premierinn.amend.amendreview.uimodel.AmendedTotal
import com.whitbread.premierinn.amend.amendreview.uimodel.BookingSummary
import com.whitbread.premierinn.amend.amendreview.uimodel.ReviewAmendTextItem
import com.whitbread.premierinn.amend.amendreview.uimodel.ReviewAmendTitleItem
import com.whitbread.premierinn.amend.analytics.AmendConfirmationData
import com.whitbread.premierinn.amend.analytics.AmendReviewData
import com.whitbread.premierinn.common.AppConfiguration
import com.whitbread.premierinn.common.AsyncResult
import com.whitbread.premierinn.common.Reducer
import com.whitbread.premierinn.common.RxViewModelStore
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.ScreenState.AMEND_CONFIRMATION
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.ScreenState.AMEND_REVIEW
import com.whitbread.premierinn.common.analytics.TrackingAnalytics
import com.whitbread.premierinn.common.format.PriceFormat
import com.whitbread.premierinn.common.managebooking.ManageBookingInput
import com.whitbread.premierinn.common.mapToAsyncResult
import com.whitbread.premierinn.common.service.LogService
import com.whitbread.premierinn.common.utils.StringUtils.LINE_BREAK
import com.whitbread.premierinn.common.utils.fullName
import com.whitbread.premierinn.common.utils.sumByFloat
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManager
import com.whitbread.premierinn.data.remote.graphql.ConfirmationSpinnerMessages
import com.whitbread.premierinn.domain.common.GBP
import com.whitbread.premierinn.domain.common.HUB_BREAKFAST_CODE
import com.whitbread.premierinn.domain.common.NO_AMOUNT
import com.whitbread.premierinn.domain.common.POA
import com.whitbread.premierinn.domain.common.RoomCriteria
import com.whitbread.premierinn.domain.common.SUB_CHANNEL
import com.whitbread.premierinn.domain.common.Upsell
import com.whitbread.premierinn.domain.common.nightsCount
import com.whitbread.premierinn.domain.graphql.amend.entity.AmendSummaryDomain
import com.whitbread.premierinn.domain.graphql.amend.entity.ConfirmAmendLogicDomain
import com.whitbread.premierinn.domain.graphql.amend.usecase.GraphQLAmendUseCase
import com.whitbread.premierinn.domain.graphql.findBooking.usecase.GraphQLFindBookingUseCase
import com.whitbread.premierinn.domain.graphql.requestBodyModels.BookingChannelDetails
import com.whitbread.premierinn.domain.graphql.requestBodyModels.Channel
import com.whitbread.premierinn.domain.graphql.requestBodyModels.ConfirmAmendLogicRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.FindBookingRequestBody
import com.whitbread.premierinn.domain.graphql.reviewBooking.entity.BasketStatusRevised
import com.whitbread.premierinn.domain.reservation.entity.Reservation
import com.whitbread.premierinn.domain.reservation.usecase.CompletePendingAmendUseCase
import com.whitbread.premierinn.domain.reservation.usecase.ObserveAmendedReservationUseCase
import com.whitbread.premierinn.domain.reservation.usecase.ObserveBookingUseCase
import com.whitbread.premierinn.domain.reservation.usecase.OriginalReservationUseCase
import com.whitbread.premierinn.domain.reservation.usecase.SubmitAmendedReservationUseCase
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository
import com.whitbread.premierinn.domain.resource.usecase.GetLongResource
import com.whitbread.premierinn.domain.resource.usecase.GetStringResource
import com.whitbread.premierinn.summary.models.ParcelableExtrasItem
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.absoluteValue
import kotlin.math.sign
import kotlin.properties.Delegates

@HiltViewModel
class ReviewAmendViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    observeBookingUseCase: ObserveBookingUseCase,
    observeAmendedReservation: ObserveAmendedReservationUseCase,
    private val originalReservation: OriginalReservationUseCase,
    private val amendStringResourceProvider: AmendStringProvider,
    private val localStorage: SimplePersistenceManager,
    private val submitAmendedReservation: SubmitAmendedReservationUseCase,
    private val completePendingAmendUseCase: CompletePendingAmendUseCase,
    private val crashlyticsLogger: LogService = LogService(),
    private val trackingAnalytics: TrackingAnalytics,
    private val storage: SimplePersistenceManager,
    private val deviceLocaleProvider: DeviceLocaleProvider,
    val getStringResource: GetStringResource,
    private val getLongResource: GetLongResource,
    val graphQLAmendUseCase: GraphQLAmendUseCase,
    val appConfiguration: AppConfiguration,
    private val findBookingUseCase: GraphQLFindBookingUseCase) : RxViewModelStore<ReviewAmendState, ReviewAmendViewModel.ReviewAmendEvent>(ReviewAmendState
(stringResourceProvider = amendStringResourceProvider, deviceLocaleProvider = deviceLocaleProvider)) {

    private val input: ManageBookingInput by lazy {
        requireNotNull(savedStateHandle.get<ManageBookingInput>(EXTRA_AMEND_INPUT))
    }
    private val amendSummaryDomain: AmendSummaryDomain by lazy {
        requireNotNull(savedStateHandle.get<AmendSummaryDomain>(AMEND_SUMMARY_DOMAIN))
    }

    private var roomTypeChanged: Boolean = false
    private var nightsChanged: String = "0"
    private var roomChanged: String = "0"
    private var foodRevenueChange: String = "0"
    private var roomRevenueChange: String = "0"
    private var extrasRevenueChange: String = "0"
    private var totalRevenueChange: String = "0"
    private var amendChangesDescription: String = EMPTY_STRING
    private var roomRevenueNeg: Float = NO_AMOUNT
    private var roomRevenuePos: Float = NO_AMOUNT
    private var totalRevenue: String = "0"
    private var payNow: Boolean = false
    private lateinit var updatedReservation: Reservation
    private lateinit var amendedTotal: AmendedTotal
    private var donationPledge: String
    private var token: String = EMPTY_STRING
    lateinit var currency: String
    private var channel = if (input.isBusinessBooking) Channel.BB.name else Channel.PI.name

    init {
        observeAmendedReservation(input.bookingReference())
                .mapToAsyncResult()
                .subscribeOn(Schedulers.io())
                .subscribe { result -> applyState(Reducer { it.copy(amendedReservation = result) }) }
                .addDisposable()

        observeBookingUseCase.invoke(input.bookingReference())
            .mapToAsyncResult()
            .subscribeOn(Schedulers.io())
            .subscribe { result ->
                if (result is AsyncResult.Success) {
                    currency = result.data?.totalCost?.currency ?: GBP
                    applyState(Reducer { it.copy(originalBooking = result) })
                }
            }
            .addDisposable()

        localStorage.getBillingAddress().let { result ->
            applyState(Reducer { it.copy(billingAddress = result) })
        }

        donationPledge = getStringResource.invoke(ContentManagedResourceRepository.Key.DONATION_PLEDGE)
    }

    fun getOriginalReservation() {
        applyState(Reducer { it.copy(originalReservation = originalReservation.invoke()) })
    }

    fun getBalanceAmount(): String = PriceFormat.format(amendSummaryDomain.payOnArrival,
        currency, deviceLocaleProvider)

    fun getRefundAmount(): String = PriceFormat.format(amendSummaryDomain.refund,
        currency, deviceLocaleProvider)

    fun getBalanceOutstandingTitle(): String = amendStringResourceProvider.balanceOutstandingTitle

    fun getBalanceOutstandingTextPoa(): String = amendStringResourceProvider.balanceOutstandingTextPayOnArrival

    fun getToBeRefundedText(): String = amendStringResourceProvider.balanceDescriptionRefund

    fun confirmAmendLogicCheckPoa(tempBookingRef: String, originalBookingRef: String, token: String, amendedTotal: AmendedTotal) {
        val environment = appConfiguration.graphQLUrl.replace("api", "www")
        this.amendedTotal = amendedTotal
        graphQLAmendUseCase.confirmAmendLogic(
            ConfirmAmendLogicRequestBody(
            bookingChannel = BookingChannelDetails(
                channel,
                SUB_CHANNEL,
                deviceLocaleProvider.getDeviceLanguage().lowercase()),
            tempBookingRef = tempBookingRef,
            originalBookingRef = originalBookingRef,
            token = token,
            paymentOptionSelected = POA,
            environment = environment)
        )
            .mapToAsyncResult()
            .subscribeOn(Schedulers.io())
            .subscribe { result ->
                if (result is AsyncResult.Success && result.data != null) {
                    if (result.data.error.isEmpty() && result.data.status.isNotEmpty()) {
                        publish(ReviewAmendEvent.ConfirmAmendLogicSuccessfulEvent(result.data))
                    } else {
                        publish(ReviewAmendEvent.ShowPopupWithRetryOption(amendStringResourceProvider.amendUnsuccessfulTitle,
                            amendStringResourceProvider.confirmAmendCallFailureMessage))
                    }
                }
                if (result is AsyncResult.Error) {
                    crashlyticsLogger.logException(result.error, "confirmAmendLogicCheckPoa() result Error")
                    publish(ReviewAmendEvent.ShowPopupWithRetryOption(amendStringResourceProvider.amendUnsuccessfulTitle,
                        amendStringResourceProvider.confirmAmendCallFailureMessage))
                }
            }.addDisposable()
    }

    fun basketStatusCall(basketReference: String, amendedReservation: Reservation) {
        val initialPollStartTime = getLongResource.invoke(ContentManagedResourceRepository.Key.CONFIRMATION_POLLING_DELAY)
        val pollInterval = getLongResource.invoke(ContentManagedResourceRepository.Key.CONFIRMATION_POLLING_INTERVAL)
        val confirmationSpinnerMessages: ConfirmationSpinnerMessages = Gson()
            .fromJson(
                getStringResource.invoke(ContentManagedResourceRepository.Key.CONFIRMATION_POLLING_MESSAGES_CONFIG),
                ConfirmationSpinnerMessages::class.java
            )
        val maxAttempts = confirmationSpinnerMessages.getMaxAttempts()
        var attemptsMade = 0
        var timeStart = 0L

        graphQLAmendUseCase.getBasketStatusAmend(basketReference)
                           .delaySubscription(initialPollStartTime, TimeUnit.SECONDS)
            .repeatWhen { it.delay (pollInterval, TimeUnit.SECONDS) }
            .takeUntil { status ->
                if (attemptsMade >= maxAttempts) {
                    return@takeUntil true
                } else {
                    when(status.basketStatus) {
                        BasketStatusRevised.PAY_PENDING,
                        BasketStatusRevised.PROCESSING ,
                        BasketStatusRevised.AMENDING -> {
                            return@takeUntil false
                        }

                        BasketStatusRevised.COMPLETED,
                        BasketStatusRevised.PRE_CHECKED_IN,
                        BasketStatusRevised.AMENDED,
                        BasketStatusRevised.AMEND_FAILED,
                        BasketStatusRevised.FAILED,
                        BasketStatusRevised.OPEN -> {
                            return@takeUntil true
                        }
                    }
                }
            }
            .doOnNext {
                attemptsMade++
                if (attemptsMade == 1) {
                    timeStart = System.currentTimeMillis()
                    publish(ReviewAmendEvent.HideCtaLoadingAndShowTranslucentSpinnerEvent)
                    applyState(Reducer {it.copy(confirmPollingMessage = confirmationSpinnerMessages.messages[0].message)})
                } else {
                    val elapsedTime = System.currentTimeMillis() - timeStart
                    if (elapsedTime <= confirmationSpinnerMessages.messages[1].seconds * 1000) {
                        applyState(Reducer {it.copy(confirmPollingMessage = confirmationSpinnerMessages.messages[1].message)})
                    } else {
                        applyState(Reducer {it.copy(confirmPollingMessage = confirmationSpinnerMessages.messages[2].message)})
                    }
                }
            }
            .retry(maxAttempts)
            .lastElement().toObservable()
            .subscribeOn(Schedulers.io())
            .subscribe({ result ->
                when(result.basketStatus) {
                    BasketStatusRevised.PAY_PENDING,
                    BasketStatusRevised.PROCESSING,
                    BasketStatusRevised.AMENDING-> {
                            findBookingForRetrievingToken(true, true)
                    }

                    BasketStatusRevised.COMPLETED,
                    BasketStatusRevised.PRE_CHECKED_IN,
                    BasketStatusRevised.AMENDED -> {
                        updateBooking(amendedReservation, amendedTotal)
                    }

                    BasketStatusRevised.FAILED,
                    BasketStatusRevised.AMEND_FAILED -> {
                        findBookingForRetrievingToken(true)
                    }

                    BasketStatusRevised.OPEN -> {
                        publish(ReviewAmendEvent.ShowPopupWithRetryOption(amendStringResourceProvider.amendErrorTitleGeneric,
                            amendStringResourceProvider.basketStatusOpenErrorMessage))
                    }
                }
            }, { failure ->
                findBookingForRetrievingToken(true)
                crashlyticsLogger.logException(Throwable(), "basketStatusCall() result Error ${failure.message}")
             })
            .addDisposable()
    }

    fun findBookingForRetrievingToken(isFailure: Boolean, isAmending: Boolean = false,
                                      arrivalDate: String = EMPTY_STRING, lastName: String = EMPTY_STRING) {
        var retryCount = 0
        val maxRetry = 3
        val intervalDelay = 1L
        findBookingUseCase.findBooking(
            FindBookingRequestBody(
                input.bookingReference(),
                lastName.takeIf { it.isNotEmpty() } ?: input.surname(),
                arrivalDate.takeIf { it.isNotEmpty() } ?: input.arrivalDate().toString(),
                deviceLocaleProvider.getDeviceLanguage().lowercase(),
                deviceLocaleProvider.getCountryIfRegion(deviceLocaleProvider.getDeviceLocale())
                    .lowercase(),
                BookingChannelDetails(
                    channel,
                    SUB_CHANNEL,
                    deviceLocaleProvider.getDeviceLanguage().lowercase())
            )
        ).mapToAsyncResult()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .repeatWhen { it.delay(intervalDelay, TimeUnit.SECONDS) }
            .takeUntil { result ->
                retryCount >= maxRetry || (result is AsyncResult.Success && result.data?.token?.isNotEmpty() == true)
            }
            .subscribe({ result ->
                when (result) {
                    is AsyncResult.Loading -> {
                    }

                    is AsyncResult.Success -> {
                        token = result.data?.token ?: EMPTY_STRING
                        if (isFailure) {
                            val errorMessage = if (isAmending) {
                                amendStringResourceProvider.basketStatusAmendingErrorMessage
                            } else {
                                amendStringResourceProvider.basketStatusFailedErrorMessage
                            }
                            publish(ReviewAmendEvent.ShowFailedBasketErrorPopup(errorMessage, token))
                        } else if (token.isEmpty()) {
                            retryCount++
                            if (retryCount >= maxRetry) {
                                publish(ReviewAmendEvent.FindBookingSuccessEvent(token, amendedTotal, localStorage.getCustomerEmail()))
                            }
                        } else {
                            publish(ReviewAmendEvent.FindBookingSuccessEvent(token, amendedTotal, localStorage.getCustomerEmail()))
                        }
                    }

                    is AsyncResult.Error -> {
                        crashlyticsLogger.logException(result.error, "findBookingForRetrievingToken() result Error")
                    }
                }
            }, { failure ->
                publish(ReviewAmendEvent.ShowFailedBasketErrorPopup(amendStringResourceProvider.basketStatusFailedErrorMessage, token))
                crashlyticsLogger.logException(Throwable(), "findBookingForRetrievingToken() result Error ${failure.message}")

            }).addDisposable()
    }

    @VisibleForTesting
    internal fun updateBooking(updatedReservation: Reservation, amendedTotal: AmendedTotal) {
        submitAmendedReservation.updateBooking(updatedReservation.bookingReference,
            updatedReservation.arrival, updatedReservation.departure, updatedReservation.roomsLeadGuest[0].lastName)
            .mapToAsyncResult()
            .subscribeOn(Schedulers.io())
            .subscribe { result ->
                if (result is AsyncResult.Success) {
                    logAmendConfirmationAnalytics()
                    publish(ReviewAmendEvent.SubmitAmendedReservation
                        (amendedTotal, localStorage.getCustomerEmail(),
                        updatedReservation.arrival.toString(), updatedReservation.roomsLeadGuest.first().lastName))
                }
                if (result is AsyncResult.Error) {
                    crashlyticsLogger.logException(result.error, "updateBooking() result Error")
                    publish(ReviewAmendEvent.AmendErrorEvent)
                }
            }
            .addDisposable()
    }

    fun findDatesChanged(amendedRoom: Reservation, originalReservation: Reservation) {
        if (amendedRoom.arrival != originalReservation.arrival || amendedRoom.departure != originalReservation.departure) {
            val dates = amendedRoom.arrival to amendedRoom.departure
            val originalDates = originalReservation.arrival to originalReservation.departure
            nightsChanged = dates.nightsCount().minus(originalDates.nightsCount()).toString()
            val reviewAmendTextItem = ReviewAmendTextItem().mapToReviewAmendTextItem(
                    title = amendStringResourceProvider.dateChangedTitle,
                    description = amendStringResourceProvider.dateChangeDescription(dates))
            applyState(Reducer { it.copy(dateChanged = ReviewAmendState.DateChanged(reviewAmendTextItem)) })
        }
    }

    fun findMealsChanged(amendedReservation: Reservation, originalReservation: Reservation) {
        val originalMeals = originalReservation.upsells.filter { it.category == Upsell.Category.BREAKFAST || it.code == HUB_BREAKFAST_CODE }
        val amendedMeals = amendedReservation.upsells.filter { it.category == Upsell.Category.BREAKFAST || it.code == HUB_BREAKFAST_CODE }
        val currency = amendedReservation.roomsBreakdown.last().totalRoomCost.currency

        if (originalMeals.isNotEmpty() || amendedMeals.isNotEmpty()) {
            val reservation = if (amendedMeals.isNotEmpty()) {
                amendedReservation
            } else {
                originalReservation
            }
            val numberOfNights = (reservation.arrival to reservation.departure).nightsCount()
            lateinit var meals: List<Upsell>
            var priceChange by Delegates.notNull<Float>()
            var priceChangeResId by Delegates.notNull<Int>()
            val originalResNumberOfNights = (originalReservation.arrival to originalReservation.departure).nightsCount()
            when {
                amendedMeals.isEmpty() && originalMeals.isNotEmpty() -> {
                    meals = originalMeals
                    priceChange = meals.map { it.unitCost.amount.toBigDecimal().multiply(it.quantity.toBigDecimal()).toFloat() }.sum() * originalResNumberOfNights
                    priceChangeResId = R.string.amend_negative_price_difference
                }
                amendedMeals.isNotEmpty() && originalMeals.isEmpty() -> {
                    meals = amendedMeals
                    priceChange = meals.map { it.unitCost.amount.toBigDecimal().multiply(it.quantity.toBigDecimal()).toFloat() }.sum() * numberOfNights
                    priceChangeResId = R.string.amend_positive_price_difference
                }
                amendedMeals.isNotEmpty() -> {
                    val amendedUpsellPrice = amendedMeals.map { it.unitCost.amount.toBigDecimal().multiply(it.quantity.toBigDecimal()).toFloat() }.sum() * numberOfNights
                    val originalUpsellPrice = originalMeals.map { it.unitCost.amount.toBigDecimal().multiply(it.quantity.toBigDecimal()).toFloat() }.sum() * originalResNumberOfNights
                    val priceDifference = amendedUpsellPrice.minus(originalUpsellPrice)

                    meals = amendedMeals
                    priceChange = priceDifference.absoluteValue
                    priceChangeResId = if (priceDifference.sign > 0) R.string.amend_positive_price_difference else R.string.amend_negative_price_difference
                }
            }
            if (priceChange != NO_AMOUNT) {
                val mealList = meals.distinctBy { it.roomId to it.code }.map { it.legend to it.quantity }.groupBy { it.first }
                        .mapValues { it.value.sumBy { pair -> pair.second } }
                val reviewAmendTextItem = ReviewAmendTextItem().mapToReviewAmendTextItem(
                        title = amendStringResourceProvider.mealChangedTitle,
                        description = mealList.map {
                            amendStringResourceProvider.mealChangeDescription(it.key, it.value, numberOfNights)
                        }.toList().joinToString(separator = LINE_BREAK),
                        priceChange = amendStringResourceProvider.priceChangeText(
                                PriceFormat.format(priceChange,
                                    meals[0].unitCost.currency, deviceLocaleProvider), priceChangeResId)
                )
                applyState(Reducer { it.copy(mealsChanged = ReviewAmendState.MealsChanged(reviewAmendTextItem)) })
            }

            foodRevenueChange = amendStringResourceProvider.priceChangeText(PriceFormat.format(
                    priceChange, currency, deviceLocaleProvider),
                    priceChangeResId)
        }
    }

    fun findRoomChanged(amendedRoom: Reservation, originalReservation: Reservation) {
        val oldRoomsById = originalReservation.roomsCriteria.associateBy { it.roomId }
        val newRoomsById = amendedRoom.roomsCriteria.associateBy { it.roomId }

        val oldGuestsById = originalReservation.roomsLeadGuest.associateBy { it.roomId }
        val newGuestById = amendedRoom.roomsLeadGuest.associateBy { it.roomId }
        roomChanged = amendedRoom.roomsCriteria.size.minus(originalReservation.roomsCriteria.size).toString()

        val changes = mutableListOf<ReviewAmendTextItem>()

        newRoomsById.forEach { (key, newRoom) ->

            var hasTitleBeenCreated = false

            newGuestById[key]?.let { guest ->
                oldGuestsById[key]?.let { oldGuest ->
                    if (guest != oldGuest) {

                        if (!hasTitleBeenCreated) {
                            hasTitleBeenCreated = createTitle(changes, guest.fullName())
                        }

                        changes.add(
                                ReviewAmendTextItem().mapToReviewAmendTextItem(
                                        description = amendStringResourceProvider.guestChangeText(guest.fullName())
                                )
                        )
                    }
                }
            }

            oldRoomsById[key]?.let { oldRoom ->
                // room ID is the same, but at least one of the other values have changed
                if (oldRoom != newRoom) {
                    if (!hasTitleBeenCreated) {
                        newGuestById[key]?.let { guest ->
                            hasTitleBeenCreated = createTitle(changes, guest.fullName())
                        }
                    }

                    findChangesInNumberOfAdults(oldRoom, newRoom, changes)

                    findChangesInNumberOfChildren(oldRoom, newRoom, changes)

                    findChangesInNumberOfInfants(oldRoom, newRoom, changes)

                    findChangesInRoomType(oldRoom, newRoom, changes)

                    findChangesInCot(oldRoom, newRoom, changes)
                }
            }
        }
        applyState(Reducer { it.copy(roomChanged = ReviewAmendState.RoomChanged(changes)) })
    }

    fun findRoomAdded(amendedRoom: Reservation, originalReservation: Reservation) {
        val oldRoomsById = originalReservation.roomsCriteria.associateBy { it.roomId }
        val newRoomsById = amendedRoom.roomsCriteria.associateBy { it.roomId }
        val roomsAdded = newRoomsById.minus(oldRoomsById.keys).values

        val addedRoomList = mutableListOf<ReviewAmendTextItem>()
        var totalRoomCost = NO_AMOUNT

        roomsAdded.map { roomAdded ->
            amendedRoom.roomsLeadGuest.map { guests ->
                amendedRoom.roomsBreakdown.map { roomBreakdown ->
                    if (roomAdded.roomId == guests.roomId && roomAdded.roomId == roomBreakdown.roomId) {
                        val newRoom = amendStringResourceProvider.roomAddedText(guests.fullName())
                        addedRoomList.add(ReviewAmendTextItem().mapToReviewAmendTextItem(
                                title = newRoom,
                                priceChange = amendStringResourceProvider.priceChangeText(PriceFormat.format(
                                        roomBreakdown.totalRoomCost.amount,
                                    roomBreakdown.totalRoomCost.currency, deviceLocaleProvider),
                                        R.string.amend_positive_price_difference))
                        )
                        totalRoomCost += roomBreakdown.totalRoomCost.amount
                    }
                }
            }
        }
        roomRevenuePos = totalRoomCost
        applyState(Reducer { it.copy(roomAdded = ReviewAmendState.RoomAdded(addedRoomList)) })
    }

    fun findRoomRemoved(amendedRoom: Reservation, originalReservation: Reservation) {
        val oldRoomsById = originalReservation.roomsCriteria.associateBy { it.roomId }
        val newRoomsById = amendedRoom.roomsCriteria.associateBy { it.roomId }
        val roomsRemoved = newRoomsById.keys.let { oldRoomsById.minus(it).values }

        val removedRoomList = mutableListOf<ReviewAmendTextItem>()
        var totalRoomCost = NO_AMOUNT

        roomsRemoved.map { roomCriteria ->
            originalReservation.roomsLeadGuest.map { guest ->
                originalReservation.roomsBreakdown.map { roomBreakdown ->
                    if (guest.roomId == roomCriteria.roomId && roomBreakdown.roomId == roomCriteria.roomId) {
                        val removedRoom = amendStringResourceProvider.roomRemovedText(guest.fullName())
                        removedRoomList.add(ReviewAmendTextItem().mapToReviewAmendTextItem(
                                title = removedRoom,
                                priceChange = amendStringResourceProvider.priceChangeText(PriceFormat.format(roomBreakdown.totalRoomCost.amount,
                                    roomBreakdown.totalRoomCost.currency, deviceLocaleProvider), R.string.amend_negative_price_difference))
                        )
                        totalRoomCost += roomBreakdown.totalRoomCost.amount
                    }
                }
            }
        }
        roomRevenueNeg = totalRoomCost
        applyState(Reducer { it.copy(roomRemoved = ReviewAmendState.RoomRemoved(removedRoomList)) })
    }


    fun findExtrasChanged(
        listOfEciLcoExtras: ArrayList<ParcelableExtrasItem>?,
        amendedReservation: Reservation,
        originalReservation: Reservation
    ) {
        val originalNumberOfNights = (originalReservation.arrival to originalReservation.departure).nightsCount()
        val amendedNumberOfNights = (amendedReservation.arrival to amendedReservation.departure).nightsCount()
        val originalExtras = originalReservation.upsells.filter { it.category == Upsell.Category.OTHER }
        val amendedExtras = amendedReservation.upsells.filter { it.category == Upsell.Category.OTHER }


        if (listOfEciLcoExtras != null ||
            (originalNumberOfNights != amendedNumberOfNights && originalExtras.isNotEmpty() && amendedExtras.isNotEmpty())
        ) {
            val extrasChangedList = mutableListOf<ReviewAmendTextItem>()
            var totalBalance = 0f
            var descriptionItems = mutableListOf<String>()

            listOfEciLcoExtras?.distinct()?.let { listOfExtras ->
                descriptionItems.add(listOfExtras.joinToString(separator = LINE_BREAK) {
                    amendStringResourceProvider.extrasRemovedDescriptionText(it.description ?: EMPTY_STRING)
                })

                totalBalance -= (listOfEciLcoExtras.sumByFloat { it.price?.toFloat() ?: 0f })
            }

            val nightDifference = amendedNumberOfNights - originalNumberOfNights

            if (nightDifference != 0 && amendedExtras.isNotEmpty()) {
                totalBalance += amendedExtras.sumByFloat { it.unitCost.amount } * nightDifference

                if (nightDifference > 0) {
                    descriptionItems.add(amendedExtras.distinctBy { it.legend }.joinToString(separator = LINE_BREAK) {
                        amendStringResourceProvider.extrasAddedDescriptionText(it.legend)
                    })
                } else {
                    descriptionItems.add(amendedExtras.distinctBy { it.legend }.joinToString(separator = LINE_BREAK) {
                        amendStringResourceProvider.extrasRemovedDescriptionText(it.legend)
                    })
                }
            }

            extrasChangedList.add(
                ReviewAmendTextItem().mapToReviewAmendTextItem(
                    title = amendStringResourceProvider.extrasChangesTitleText(),
                    description = descriptionItems.joinToString(separator = LINE_BREAK),
                    priceChange = amendStringResourceProvider.priceChangeText(
                        PriceFormat.format(
                            totalBalance.absoluteValue,
                            currency, deviceLocaleProvider
                        ), if (totalBalance > 0) R.string.amend_positive_price_difference else R.string.amend_negative_price_difference
                    )
                )
            )
            applyState(Reducer { it.copy(extrasChanged = ReviewAmendState.ExtrasChanged(extrasChangedList)) })
        }
    }

    private fun findChangesInCot(oldRoom: RoomCriteria, newRoom: RoomCriteria, changes: MutableList<ReviewAmendTextItem>) {
        if (oldRoom.includeCot && !newRoom.includeCot) {
            changes.add(
                    ReviewAmendTextItem().mapToReviewAmendTextItem(
                            description = amendStringResourceProvider.cotRemoved
                    ))
        } else if (!oldRoom.includeCot && newRoom.includeCot) {
            changes.add(
                    ReviewAmendTextItem().mapToReviewAmendTextItem(
                            description = amendStringResourceProvider.cotAdded
                    ))
        }
    }

    private fun findChangesInRoomType(oldRoom: RoomCriteria, newRoom: RoomCriteria, changes: MutableList<ReviewAmendTextItem>) {
        if (oldRoom.roomType != newRoom.roomType) {
            roomTypeChanged = true
            changes.add(
                    ReviewAmendTextItem().mapToReviewAmendTextItem(
                            description = amendStringResourceProvider.roomTypeChangedText(newRoom.roomType)
                    )
            )
        }
    }

    private fun findChangesInNumberOfInfants(oldRoom: RoomCriteria, newRoom: RoomCriteria, changes: MutableList<ReviewAmendTextItem>) {
        if (oldRoom.numberOfInfants > newRoom.numberOfInfants) {
            val numberOfInfantsRemoved = oldRoom.numberOfInfants - newRoom.numberOfInfants

            changes.add(
                    ReviewAmendTextItem().mapToReviewAmendTextItem(
                            description = amendStringResourceProvider.infantRemovedText(numberOfInfantsRemoved)
                    )
            )
        } else if (oldRoom.numberOfInfants < newRoom.numberOfInfants) {
            val numberOfInfantsAdded = newRoom.numberOfInfants - oldRoom.numberOfInfants

            changes.add(
                    ReviewAmendTextItem().mapToReviewAmendTextItem(
                            description = amendStringResourceProvider.infantAddedText(numberOfInfantsAdded)
                    )
            )
        }
    }

    private fun findChangesInNumberOfChildren(oldRoom: RoomCriteria, newRoom: RoomCriteria, changes: MutableList<ReviewAmendTextItem>) {
        if (oldRoom.numberOfChildren > newRoom.numberOfChildren) {
            val numberOfChildrenRemoved = oldRoom.numberOfChildren - newRoom.numberOfChildren

            changes.add(
                    ReviewAmendTextItem().mapToReviewAmendTextItem(
                            description = amendStringResourceProvider.childrenRemovedText(numberOfChildrenRemoved)
                    )
            )
        } else if (oldRoom.numberOfChildren < newRoom.numberOfChildren) {
            val numberOfChildrenAdded = newRoom.numberOfChildren - oldRoom.numberOfChildren

            changes.add(
                    ReviewAmendTextItem().mapToReviewAmendTextItem(
                            description = amendStringResourceProvider.childrenAddedText(numberOfChildrenAdded)
                    )
            )
        }
    }

    private fun findChangesInNumberOfAdults(oldRoom: RoomCriteria, newRoom: RoomCriteria, changes: MutableList<ReviewAmendTextItem>) {
        if (oldRoom.numberOfAdults > newRoom.numberOfAdults) {
            val numberOfAdultsRemoved = oldRoom.numberOfAdults - newRoom.numberOfAdults

            changes.add(
                    ReviewAmendTextItem().mapToReviewAmendTextItem(
                            description = amendStringResourceProvider.adultRemovedText(numberOfAdultsRemoved)
                    )
            )
        } else if (oldRoom.numberOfAdults < newRoom.numberOfAdults) {
            val numberOfAdultsAdded = newRoom.numberOfAdults - oldRoom.numberOfAdults
            changes.add(
                    ReviewAmendTextItem().mapToReviewAmendTextItem(
                            description = amendStringResourceProvider.adultAddedText(numberOfAdultsAdded)
                    )
            )
        }
    }

    private fun createTitle(changes: MutableList<ReviewAmendTextItem>, guest: String): Boolean {
        changes.add(
                ReviewAmendTextItem().mapToReviewAmendTextItem(
                        title = amendStringResourceProvider.roomChangedTitleText(guest)
                )
        )
        return true
    }

    fun createBookingSummary(bookingSummary: BookingSummary) {
        val numberOfAdults = bookingSummary.roomCriteria.sumBy { it.numberOfAdults }
        val numberOfChildren = bookingSummary.roomCriteria.sumBy { it.numberOfChildren }
        val numberOfRooms = bookingSummary.roomCriteria.size
        val numberOfGuests = numberOfAdults + numberOfChildren
        val newSummaryTotal = bookingSummary.originalTotalCost.amount + bookingSummary.amendedTotal.amount
        val newRoomChangeTotal = roomRevenuePos.minus(roomRevenueNeg)
        val revenueChangeTotal = (newSummaryTotal).minus(bookingSummary.originalTotalCost.amount)
        val mealsAndQuantity = bookingSummary.upsells.filter { it.category == Upsell.Category.BREAKFAST }.distinctBy { it.roomId to it.code }
                .map { it.legend to it.quantity }.groupBy { it.first }.mapValues { it.value.sumBy { pair -> pair.second } }
        val extras = bookingSummary.upsells.filter { it.category == Upsell.Category.OTHER }.map { it.legend.covertToCharitablePledgeIfApplicable() }.distinct()

        val header = ReviewAmendTitleItem().mapToReviewAmendTitle(
                title = amendStringResourceProvider.bookingSummaryTitle,
                previousTotal = PriceFormat.format(newSummaryTotal,
                    bookingSummary.amendedTotal.currency, deviceLocaleProvider))

        val dates = ReviewAmendTextItem().mapToReviewAmendTextItem(
                description = amendStringResourceProvider.dateChangeDescription(bookingSummary.amendedDates)
        )

        val guestAndRooms = ReviewAmendTextItem().mapToReviewAmendTextItem(
                description = amendStringResourceProvider.guestAndRoomChangeDescription(numberOfGuests, numberOfRooms)
        )
        val mealsSummary = if (mealsAndQuantity.isNotEmpty()) {
            ReviewAmendTextItem().mapToReviewAmendTextItem(
                    description = mealsAndQuantity.map {
                        amendStringResourceProvider.mealChangeDescription(it.key, it.value, bookingSummary.amendedDates.nightsCount())
                    }.toList().joinToString(separator = LINE_BREAK)
            )
        } else null
        val extrasSummary = if (extras.isNotEmpty()) {
            ReviewAmendTextItem().mapToReviewAmendTextItem(description = extras.joinToString(separator = LINE_BREAK))
        } else null
        applyState(Reducer {
            it.copy(bookingSummaryCreated = ReviewAmendState.BookingSummaryCreated(
                    header, dates, guestAndRooms, mealsSummary, extrasSummary))
        })

        val newRoomChangeTotalResID = if (newRoomChangeTotal.sign > 0) R.string.amend_positive_price_difference else R.string.amend_difference
        val newRevenueChangeTotalResID = if (revenueChangeTotal.sign > 0) R.string.amend_positive_price_difference else R.string.amend_difference

        roomRevenueChange = amendStringResourceProvider.priceChangeText(PriceFormat.format(
                newRoomChangeTotal, bookingSummary.amendedTotal.currency, deviceLocaleProvider),
                newRoomChangeTotalResID)

        extrasRevenueChange = extrasSummary?.priceChange ?: "0"
        totalRevenueChange = amendStringResourceProvider.priceChangeText(PriceFormat.format(
                revenueChangeTotal,
            bookingSummary.amendedTotal.currency, deviceLocaleProvider),
                newRevenueChangeTotalResID)

        totalRevenue = PriceFormat.format(
                newSummaryTotal,
                bookingSummary.amendedTotal.currency, deviceLocaleProvider)

        amendChangesDescription = "${dates.description};${guestAndRooms.description};${mealsSummary?.description};${extrasSummary?.description}"

        logAmendReviewAnalytics()
    }

    private fun String.covertToCharitablePledgeIfApplicable() : String {
        return if (this.contains("pledge", ignoreCase = true)) {
            donationPledge
        } else this
    }

    private fun logAmendConfirmationAnalytics() { // Move this to the Amend Confirmation page, or call it just before
        trackingAnalytics.track(AMEND_CONFIRMATION,
            AmendConfirmationData(bookingRef = input.bookingReference(),
                nightsChanged = nightsChanged,
                roomsChanged = roomChanged,
                roomTypeChanged = roomTypeChanged,
                amendChangesDescription = amendChangesDescription,
                foodRevenueChange = foodRevenueChange,
                roomRevenueChange = roomRevenueChange,
                extrasRevenueChange = extrasRevenueChange,
                totalRevenueChange = totalRevenueChange,
                totalRevenue = totalRevenue))

    }

    private fun logAmendReviewAnalytics() {
        trackingAnalytics.track(AMEND_REVIEW,
            AmendReviewData(bookingRef = input.bookingReference(),
                nightsChanged = nightsChanged,
                roomsChanged = roomChanged,
                roomTypeChanged = roomTypeChanged,
                amendChangesDescription = amendChangesDescription,
                payNow = payNow)
        )

    }

    fun onCardVerificationSuccess(pares: String) {
        completePendingAmendUseCase.invoke(pares)
                .mapToAsyncResult()
                .subscribeOn(Schedulers.io())
                .subscribe { result ->
                    if (result is AsyncResult.Success) {
                        updateBooking(updatedReservation, amendedTotal)
                    }
                    if (result is AsyncResult.Error) {
                        crashlyticsLogger.logException(result.error, "onCardVerificationSuccess() result Error")
                        publish(ReviewAmendEvent.AmendErrorEvent)
                    }
                }
                .addDisposable()
    }

    fun refreshDashboard() {
        storage.setRefreshDashboard(true)
    }

    sealed class ReviewAmendEvent {
        object GenericErrorEvent : ReviewAmendEvent()
        object AmendErrorEvent : ReviewAmendEvent()
        object HideCtaLoadingAndShowTranslucentSpinnerEvent : ReviewAmendEvent()
        data class ConfirmAmendLogicSuccessfulEvent(val confirmAmendLogicResp: ConfirmAmendLogicDomain): ReviewAmendEvent()
        data class SubmitAmendedReservation(val amendedTotal: AmendedTotal, val customerEmail: String,
                                            val arrivalDate: String, val lastName: String) : ReviewAmendEvent()
        data class FindBookingSuccessEvent(val findBookingToken: String, val amendedTotal: AmendedTotal, val customerEmail: String): ReviewAmendEvent()
        data class ShowPopupWithRetryOption(val title: String, val errorDescription: String): ReviewAmendEvent()
        data class ShowFailedBasketErrorPopup(val errorDescription: String, val findBookingToken: String): ReviewAmendEvent()
    }

    sealed class ReviewAmendState {
        data class DateChanged(val dates: ReviewAmendTextItem) : ReviewAmendState()
        data class MealsChanged(val meals: ReviewAmendTextItem) : ReviewAmendState()
        data class RoomChanged(val roomsChangedList: List<ReviewAmendTextItem>) : ReviewAmendState()
        data class RoomAdded(val roomsAddedList: List<ReviewAmendTextItem>) : ReviewAmendState()
        data class RoomRemoved(val roomsRemovedList: MutableList<ReviewAmendTextItem>) : ReviewAmendState()
        data class ExtrasChanged(val extrasChangedList: MutableList<ReviewAmendTextItem>) : ReviewAmendState()
        data class BookingSummaryCreated(val header: ReviewAmendTitleItem, val dates: ReviewAmendTextItem, val guestAndRooms: ReviewAmendTextItem,
                                         val meals: ReviewAmendTextItem?, val extras: ReviewAmendTextItem?) : ReviewAmendState()
    }
}
