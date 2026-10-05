package com.whitbread.premierinn.mybookings

import com.whitbread.premierinn.businessbooker.data.common.persistence.BusinessPersistenceManager
import com.whitbread.premierinn.ciol.analytics.logCiolAnalytics
import com.whitbread.premierinn.ciol.entity.PreStayUiModel
import com.whitbread.premierinn.ciol.mapper.convertToUiModel
import com.whitbread.premierinn.ciol.uimodel.RoomKeyInstructionsModel
import com.whitbread.premierinn.ciol.utils.mapNationality
import com.whitbread.premierinn.common.AsyncResult
import com.whitbread.premierinn.common.StringResourceProvider
import com.whitbread.premierinn.common.analytics.AnalyticsConstants
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Action.CIOL_START_ACTION
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.ScreenState
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.ScreenState.START_CIOL
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Type
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Value.QR_CODE_KIOSK_CHECK_IN
import com.whitbread.premierinn.common.analytics.TrackingAnalytics
import com.whitbread.premierinn.common.format.DateFormat
import com.whitbread.premierinn.common.mapToAsyncResult
import com.whitbread.premierinn.common.mvp.Presenter
import com.whitbread.premierinn.common.mvp.PresenterView
import com.whitbread.premierinn.common.service.LogService
import com.whitbread.premierinn.common.usecase.GetQrKioskHotelUseCase
import com.whitbread.premierinn.common.utils.getDescriptionLabel
import com.whitbread.premierinn.common.utils.logUnExpectedErrorAndRun
import com.whitbread.premierinn.common.view.ListItem
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManager
import com.whitbread.premierinn.data.remote.ApiThrowable
import com.whitbread.premierinn.domain.authentication.UnAuthorizedCustomerError
import com.whitbread.premierinn.domain.authentication.usecase.IsCustomerLoggedIn
import com.whitbread.premierinn.domain.booking.NoLinkedAccountBookingsFound
import com.whitbread.premierinn.domain.booking.entity.Booking
import com.whitbread.premierinn.domain.booking.entity.PreStayModel
import com.whitbread.premierinn.domain.booking.usecase.ListenToBookingsUpdates
import com.whitbread.premierinn.domain.booking.usecase.SyncCustomerFutureBookings
import com.whitbread.premierinn.domain.booking.usecase.UpdateStoredBookingData
import com.whitbread.premierinn.domain.ciol.usecase.IsCheckInOnlineEnabledUseCase
import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import com.whitbread.premierinn.domain.common.SUB_CHANNEL
import com.whitbread.premierinn.domain.customer.usecase.GetCustomer
import com.whitbread.premierinn.domain.graphql.bookingDetails.usecase.GraphQLBookingDetailsUseCase
import com.whitbread.premierinn.domain.graphql.findBooking.usecase.GraphQLFindBookingUseCase
import com.whitbread.premierinn.domain.graphql.requestBodyModels.BookingChannelDetails
import com.whitbread.premierinn.domain.graphql.requestBodyModels.BookingHistoryRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.CancelInformationRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.CategoryLabelsRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.CategoryLabelsRequestBody.Companion.LABEL_TITLE
import com.whitbread.premierinn.domain.graphql.requestBodyModels.Channel
import com.whitbread.premierinn.domain.graphql.requestBodyModels.FindBookingRequestBody
import dagger.hilt.android.scopes.ActivityRetainedScoped
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.jetbrains.annotations.VisibleForTesting
import org.threeten.bp.LocalDate
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.net.URLEncoder
import java.util.Locale
import javax.inject.Inject

@ActivityRetainedScoped
class MyBookingsPresenter @Inject constructor(
    private val subscriptions: CompositeDisposable,
    private val stringResourceProvider: StringResourceProvider,
    private val isCustomerLoggedIn: IsCustomerLoggedIn,
    private val getCustomer: GetCustomer,
    private val persistenceManager: SimplePersistenceManager,
    private val businessPersistenceManager: BusinessPersistenceManager,
    private val listenToBookingsUpdates: ListenToBookingsUpdates,
    private val syncCustomerFutureBookings: SyncCustomerFutureBookings,
    private val updatedStoredBookingData: UpdateStoredBookingData,
    private val findBookingUseCase: GraphQLFindBookingUseCase,
    private val bookingDetailsUseCase: GraphQLBookingDetailsUseCase,
    private val analytics: TrackingAnalytics,
    private val logService: LogService,
    private val deviceLocaleProvider: DeviceLocaleProvider,
    private val isCheckInOnlineEnabledUseCase: IsCheckInOnlineEnabledUseCase,
    private val getQrKioskHotelUseCase: GetQrKioskHotelUseCase
) : Presenter<MyBookingsPresenter.View>() {
    private var isBusinessUser: Boolean = false
    @VisibleForTesting
    var uiModels: MutableList<ListItem> = mutableListOf()
    private var analyticsAlreadyCalled = false
    private var preStayUiModel: PreStayUiModel? = null

    override fun onAttachView(view: View) {
        subscriptions.add(
            updatedStoredBookingData.execute(deviceLocaleProvider.getDeviceLanguage().lowercase(),
                deviceLocaleProvider.getCountryIfRegion(deviceLocaleProvider.getDeviceLocale()).lowercase(Locale.getDefault()),
                OffsetDateTime.now().format(DateTimeFormatter.ofPattern(DateFormat.DATE_TIME_WITH_OFFSET)))
                    .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ }, { error ->
                            // We don't want to show error for network issues because user should
                            // already know that they have no internet connection and therefore shouldn't
                            // expect their details to be synced
                            if (error !is ApiThrowable.Network) {
                                logService.logException(error)
                                view.showUpdateError()
                            }
                        })
        )

        listenToBookingsUpdates()

        isBusinessUser = businessPersistenceManager.getBusinessCustomerEmail().isNotEmpty()
        val channel = if (isBusinessUser) Channel.BB.name else Channel.PI.name
        val bookingHistoryRequest = BookingHistoryRequestBody(business = isBusinessUser,
            includeCheckInBookings = true,
            sortOrder = "DEFAULT",
            continuationToken = null,
            pageSize = 40,
            pageIndex = 1,
            bookingChannel = BookingChannelDetails(
                channel, SUB_CHANNEL,
                deviceLocaleProvider.getDeviceLocale().language.lowercase())
        )

        subscriptions.add(
                Observable.merge(onImmediateSubscription(), view.onLoginSuccessful())
                        .flatMap { isCustomerLoggedIn().toObservable() }
                        .filter {
                             if (!it) {
                                 trackAnalytics(ScreenState.NO_BOOKINGS, Type.MY_PREMIER_INN)
                            }
                            it }
                        .flatMap {
                            getCustomerAndSaveGuestHistoryNumber()
                            syncCustomerFutureBookings.execute(bookingHistoryRequest)
                                .andThen(Observable.just("success")).mapToAsyncResult() }
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { state: AsyncResult<*> ->
                            view.showLoadingInToolbar(state is AsyncResult.Loading)
                            view.showLoading(state is AsyncResult.Loading)
                            if (state is AsyncResult.Error) {
                                if (state.error is NoLinkedAccountBookingsFound && uiModels.isEmpty()) {
                                    view.showNoBookingsScreen()
                                } else if (state.error !is NoLinkedAccountBookingsFound && state.error !is UnAuthorizedCustomerError) {
                                    state.logUnExpectedErrorAndRun(logService) { view.showLoadingError() }
                                }
                            }
                        }
        )

        subscriptions.add(
                view.onImportedBooking().subscribe { view.showSuccessImportedBookingMessage() }
        )

        subscriptions.add(view.onLogInClick()
                .subscribe { view.startLogInActivity() })

            subscriptions.add(view.onFindBookingClick()
                .subscribe { view.startFindBookingActivity() })

        subscriptions.add(view.onSearchClick()
                .subscribe { view.showSearchScreen() })

        subscriptions.add(view.onBookingListClicks()
                .subscribe { clickAction ->
                    if (clickAction is OnClickItemAction.BookingItem) {
                        subscriptions.add(findBookingUseCase.findBooking(
                                FindBookingRequestBody(clickAction.bookingId, clickAction.surname,
                                        clickAction.arrivalDate.toString(),
                                        deviceLocaleProvider.getDeviceLanguage().lowercase(),
                                        deviceLocaleProvider.getCountryIfRegion(deviceLocaleProvider.getDeviceLocale()).lowercase(),
                            BookingChannelDetails(
                                setChannelAsBBOrPI(clickAction),
                                SUB_CHANNEL,
                                deviceLocaleProvider.getDeviceLanguage().lowercase())
                            )
                        )
                                .mapToAsyncResult()
                                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                                .subscribe { result ->
                                    when(result) {
                                        is AsyncResult.Loading -> {
                                            view.showLoading(true)
                                        }
                                        is AsyncResult.Success -> {
                                            view.showLoading(false)
                                            view.startBookingDetailsActivity(
                                                    clickAction.bookingId,
                                                    result.data?.uuidBasketReference ?: EMPTY_STRING_DOMAIN,
                                                    result.data?.token ?: EMPTY_STRING_DOMAIN)

                                        }
                                        is AsyncResult.Error -> {
                                            view.showLoading(false)
                                            view.startBookingDetailsActivity(clickAction.bookingId, EMPTY_STRING_DOMAIN, EMPTY_STRING_DOMAIN)
                                        }
                                        else -> {
                                            view.startBookingDetailsActivity(clickAction.bookingId, EMPTY_STRING_DOMAIN, EMPTY_STRING_DOMAIN)
                                        }
                                    }
                                })
                    }

                    if (clickAction is OnClickItemAction.PlanYourTrip) {
                        subscriptions.add(findBookingUseCase.findBooking(
                                FindBookingRequestBody(clickAction.bookingId, clickAction.surname,
                                        clickAction.arrivalDate.toString(),
                                        deviceLocaleProvider.getDeviceLanguage().lowercase(),
                                        deviceLocaleProvider.getCountryIfRegion(deviceLocaleProvider.getDeviceLocale()).lowercase(),
                                    BookingChannelDetails(
                                        setChannelAsBBOrPI(clickAction),
                                        SUB_CHANNEL,
                                        deviceLocaleProvider.getDeviceLanguage().lowercase())
                                ))
                                .mapToAsyncResult()
                                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                                .subscribe { result ->
                                    when(result) {
                                        is AsyncResult.Loading -> {
                                            view.showLoading(true)
                                        }
                                        is AsyncResult.Success -> {
                                            view.showLoading(false)
                                            view.startPlanTripActivity(
                                                    clickAction.hotelId)
                                        }
                                        is AsyncResult.Error -> {
                                            view.showLoading(false)
                                        }
                                        else -> {
                                            view.startPlanTripActivity(clickAction.hotelId)
                                        }
                                    }
                                })
                    }

                    if (clickAction is OnClickItemAction.CheckIn) {
                        val language = deviceLocaleProvider.getDeviceLanguage().lowercase()
                        val country =
                            deviceLocaleProvider.getCountryIfRegion(deviceLocaleProvider.getDeviceLocale())
                                .lowercase()
                        val userDateTime = OffsetDateTime.now()
                            .format(DateTimeFormatter.ofPattern(DateFormat.DATE_TIME_WITH_OFFSET))
                        view.showLoading(true)
                        subscriptions.add(
                            findBookingUseCase.findBooking(
                                FindBookingRequestBody(
                                    clickAction.bookingReference,
                                    clickAction.surname,
                                    clickAction.arrivalDate.toString(),
                                    language,
                                    country,
                                    BookingChannelDetails(
                                        setChannelAsBBOrPI(clickAction),
                                        SUB_CHANNEL,
                                        deviceLocaleProvider.getDeviceLanguage().lowercase())
                                )
                            )
                                .flatMapObservable { findBooking ->
                                    bookingDetailsUseCase.bookingConfirmationAndManageBookingWithHotelInfoPair(
                                        uuidBasketReference = findBooking.uuidBasketReference,
                                        country =  country,
                                        language = language,
                                        // This we need to reconsider if we need to pass PI for leisure as well
                                        // as currently only for BB we send the bookingchannel and maybe we need to
                                        // send it always?
                                        bookingChannel = Channel.BB.name.takeIf { isBusinessUser } ?: Channel.PI.name,
                                        cancelInformationRequestBody =  CancelInformationRequestBody(
                                            findBooking.uuidBasketReference,
                                            clickAction.hotelCode,
                                            userDateTime,
                                            URLEncoder.encode(findBooking.token, "UTF-8"),
                                            BookingChannelDetails(
                                                setChannelAsBBOrPI(clickAction),
                                                SUB_CHANNEL,
                                                language.lowercase()
                                            )
                                        )
                                    ).map { bookingHotelInfoPromoInfoPair ->
                                        Triple(
                                            bookingHotelInfoPromoInfoPair.first,
                                            bookingHotelInfoPromoInfoPair.second,
                                            findBooking
                                        )
                                    }
                                }
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                    { pair ->
                                        val bookingInfo = pair.first
                                        val hotelInfo = pair.second
                                        val findBooking = pair.third
                                        val pushToken = persistenceManager.getFirebaseToken()

                                        bookingInfo.preStayDetails?.let { preStayBooking ->
                                            hotelInfo.preStayHeaderInfo?.let { preStayHeader ->
                                                preStayUiModel = PreStayModel(
                                                    preStayDetails = preStayBooking,
                                                    preStayHeaderInfo = preStayHeader,
                                                    outstandingBalance = bookingInfo.balanceOutstanding,
                                                    basketReference = findBooking.uuidBasketReference,
                                                    isOpera = true,
                                                    bookingReference = bookingInfo.bookingReference,
                                                    rateCode = bookingInfo.rateCode,
                                                    rateName = bookingInfo.rateType,
                                                    rateDescription = bookingInfo.rateDescription,
                                                    isBusinessBooking = bookingInfo.isBusinessBooking,
                                                    isThirdPartyBooking = bookingInfo.isThirdPartyBooking,
                                                    paymentOption = bookingInfo.paymentOption,
                                                    upsellsAddonsEnabled = bookingInfo.upsellsAddonsEnabled
                                                ).convertToUiModel()
                                                view.showLoading(false)
                                                view.showCheckInInfoBottomSheet(
                                                    preStayUiModel = preStayUiModel?.mapNationality(deviceLocaleProvider.getNationalityBasedOnDeviceLanguage())
                                                )
                                                logCiolAnalytics(analytics = analytics,
                                                    preStayModel = preStayUiModel,
                                                    screenName = START_CIOL,
                                                    action = CIOL_START_ACTION,
                                                    pushToken = pushToken)
                                            }
                                        }
                                    },
                                    { error ->
                                        logService.logException(
                                            error,
                                            "Error retrieving data:${error.localizedMessage}"
                                        )
                                        view.showLoading(false)
                                        view.showStartCheckInOnlineError()
                                    }
                                )
                        )
                    }

                    if(clickAction is OnClickItemAction.RoomKeyInstructions) {
                        subscriptions.add(bookingDetailsUseCase.getRoomKeyInstructions(
                            CategoryLabelsRequestBody(
                                deviceLocaleProvider.getCountryIfRegion(deviceLocaleProvider.getDeviceLocale()).lowercase(),
                                deviceLocaleProvider.getDeviceLanguage().lowercase(), CategoryLabelsRequestBody.CATEGORY,
                                listOf(LABEL_TITLE, clickAction.hotelCountry.getDescriptionLabel())
                            )
                        )
                            .mapToAsyncResult()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe { result ->
                                when(result) {
                                    is AsyncResult.Loading -> {
                                        view.showLoading(true)
                                    }
                                    is AsyncResult.Success -> {
                                        view.showLoading(false)
                                        val roomKeyInstructionsUiModel = result.data?.convertToUiModel(clickAction.bookingId, clickAction.hotelImage, clickAction.hotelId)
                                        roomKeyInstructionsUiModel?.let { view.showGetRoomKeyInstructionsBottomSheet(roomKeyInstructionsUiModel) }
                                    }
                                    is AsyncResult.Error -> {
                                        view.showLoading(false)
                                    }
                                    else -> {}
                                }
                            }
                        )
                    }

                    if (clickAction is OnClickItemAction.QRCode) {
                        trackAction()
                        view.startQRCodeActivity(clickAction.bookingId)
                    }
                })
    }

    private fun setChannelAsBBOrPI(clickItemAction: OnClickItemAction) : String {
        return Channel.BB.name.takeIf { clickItemAction. isBusinessUserAndIsBusinessBooking() }
            ?: Channel.PI.name
    }

    private fun OnClickItemAction.isBusinessUserAndIsBusinessBooking(): Boolean {
        return when(this) {
            is OnClickItemAction.BookingItem -> isBusinessUser && this.isBusinessBooking
            is OnClickItemAction.PlanYourTrip -> isBusinessUser && this.isBusinessBooking
            is OnClickItemAction.CheckIn  -> isBusinessUser && this.isBusinessBooking

            is OnClickItemAction.RoomKeyInstructions -> false
            is OnClickItemAction.QRCode -> false
        }
    }
    private fun listenToBookingsUpdates() {
        subscriptions.add(
            listenToBookingsUpdates.execute()
                .flatMap { updatedBookingList ->
                    val sortedBookingStatus = listOf(BOOKING_STATUS_FUTURE, BOOKING_STATUS_PAST, BOOKING_STATUS_CANCELLED)
                    val defaultRank = Int.MAX_VALUE
                    val sortedListOfBookingByBookingStatus = updatedBookingList.sortedWith(compareBy<Booking> { booking ->
                        val rank = if (booking.isCancelled)  {
                            sortedBookingStatus.indexOf(BOOKING_STATUS_CANCELLED)
                        } else if (booking.bookingStatus.isEmpty()) {
                                if (booking.arrivalDate.isAfter(LocalDate.now()) || booking.arrivalDate.isEqual(LocalDate.now())) {
                                        sortedBookingStatus.indexOf(BOOKING_STATUS_FUTURE)

                                } else if (booking.departureDate.isBefore(LocalDate.now())) {
                                    sortedBookingStatus.indexOf(BOOKING_STATUS_PAST)
                                } else {
                                    defaultRank
                            }
                        } else {
                            sortedBookingStatus.indexOf(booking.bookingStatus)
                        }
                        if (rank == -1) defaultRank else rank
                    }.thenBy { it.arrivalDate })
                    Observable.just(sortedListOfBookingByBookingStatus)
                        .flatMapIterable { list -> list }
                        .reduce(
                            ArrayList(),
                            MyBookingUiItemsReducer(
                                stringResourceProvider,
                                deviceLocaleProvider,
                                isCheckInOnlineEnabledUseCase(),
                                persistenceManager.getBookingStatus(),
                                persistenceManager.getBookingHotelCountry(),
                                getQrKioskHotelUseCase()
                            )
                        )
                        .toObservable()
                        .flatMap(
                            { isCustomerLoggedIn().toObservable() },
                            { listItems: MutableList<ListItem>, isLoggedIn: Boolean ->
                                listItems to isLoggedIn
                            })
                }
                .compose { up -> up.mapToAsyncResult() }
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe { state: AsyncResult<Pair<MutableList<ListItem>, Boolean>> ->
                    when (state) {
                        is AsyncResult.Success<Pair<MutableList<ListItem>, Boolean>> -> {
                            val (listUiModels, isCustomerLoggedIn) = state.data!!
                            uiModels = listUiModels
                            if (listUiModels.isNotEmpty()) {
                                listUiModels.add(GDPRUiModel())
                                view.showBookings(listUiModels)
                                if (persistenceManager.getCustomer().guestHistoryNumber.isNotBlank() && !analyticsAlreadyCalled) {
                                    analyticsAlreadyCalled = true
                                    trackAnalytics(ScreenState.MY_BOOKINGS, Type.MY_PREMIER_INN)
                                }
                            } else {
                                if (isCustomerLoggedIn) {
                                    trackAnalytics(ScreenState.NO_BOOKINGS, Type.MY_PREMIER_INN)
                                    view.showNoBookingsScreen()
                                } else {
                                    view.showNoBookingsScreenWithLoginPrompt()
                                }
                            }
                        }
                        is AsyncResult.Error -> {
                            state.logUnExpectedErrorAndRun(logService) { view.showLoadingError() }
                        }

                        else -> {}
                    }
                }
        )
    }

    private fun getCustomerAndSaveGuestHistoryNumber() {
        if (persistenceManager.getCustomer().guestHistoryNumber.isBlank()) {
            subscriptions.add(
                getCustomer()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ customer ->
                        persistenceManager.saveCustomer(customer)
                        listenToBookingsUpdates()

                    }, { error ->
                        logService.logException(error)
                    })
            )
        }
    }

    private fun onImmediateSubscription(): ObservableSource<out Any> {
        return Observable.just("")
    }

    @VisibleForTesting
    fun trackAnalytics(screen: String, type: String) {
        analytics.track(screen, type)

        if (isBusinessUser) {
            val listOfBookings = uiModels.filterIsInstance<BookingUiModel>()
            analytics.track(screen, MyBookingsAnalyticsData(
                totalBookings = listOfBookings.count { it.isBusinessBooking }.toString(),
                cancelledBookings = listOfBookings.count { it.isBusinessBooking && it.isCancelled }.toString(),
                futureBookings = listOfBookings.count { it.isBusinessBooking && it.bookingStatus == "FUTURE" }.toString(),
                stayedBookings = EMPTY_STRING_DOMAIN,
                checkedInBookings = EMPTY_STRING_DOMAIN
            ))
        }
    }

    private fun trackAction() {
        analytics.trackAction(
            AnalyticsConstants.Action.QR_CODE_SHOWN,
            MyBookingsQrCodeCheckInAnalyticsData(
                cid = QR_CODE_KIOSK_CHECK_IN
            )
        )
    }

    override fun onDetachView() {
        subscriptions.clear()
    }

    interface View : PresenterView {

        fun showBookings(bookingUiModels: List<ListItem>)

        fun showSearchScreen()

        fun showLoadingInToolbar(show: Boolean)

        fun showLoading(show: Boolean)

        fun showNoBookingsScreenWithLoginPrompt()

        fun showNoBookingsScreen()

        fun showImportBookingError()

        fun showSuccessImportedBookingMessage()

        fun startPlanTripActivity(hotelCode: String)

        fun showGetRoomKeyInstructionsBottomSheet(roomKeyInstructionsModel: RoomKeyInstructionsModel)

        fun startBookingDetailsActivity(bookingReference: String, uuidBasketReference: String?, token: String)

        fun startQRCodeActivity(bookingReference: String)

        fun startLogInActivity()

        fun startFindBookingActivity()

        fun onLoginSuccessful(): Observable<Any>

        fun onImportedBooking(): Observable<Any>

        fun onSearchClick(): Observable<Unit>

        fun onLogInClick(): Observable<Unit>

        fun onBookingListClicks(): Observable<OnClickItemAction>

        fun onBookingCancelled(): Observable<Any>

        fun onFindBookingClick(): Observable<Unit>

        fun showLoadingError()

        fun showUpdateError()

        fun showCheckInInfoBottomSheet(preStayUiModel: PreStayUiModel?)

        fun showStartCheckInOnlineError()
    }
}