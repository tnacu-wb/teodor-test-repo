package com.whitbread.premierinn.bookingdetails

import androidx.annotation.VisibleForTesting
import com.whitbread.premierinn.R
import com.whitbread.premierinn.account.AppFeedbackMessageProvider
import com.whitbread.premierinn.amend.ParcelablePromotionsInformationDomain
import com.whitbread.premierinn.amend.amendguestsrooms.ParcelableAmendTotal
import com.whitbread.premierinn.amend.toListOfParcelablePromotionsInformationDomain
import com.whitbread.premierinn.amend.toListOfParcelableRooms
import com.whitbread.premierinn.api.Urls
import com.whitbread.premierinn.bookingdetails.BookingDetailsState.StateType.Error
import com.whitbread.premierinn.bookingdetails.BookingDetailsState.StateType.Idle
import com.whitbread.premierinn.bookingdetails.BookingDetailsState.StateType.InFlight
import com.whitbread.premierinn.bookingdetails.BookingDetailsState.StateType.Success
import com.whitbread.premierinn.bookingdetails.UpsellItemSummary.UpsellItemType
import com.whitbread.premierinn.bookingdetails.analytics.BookingDetailsAnalyticsData
import com.whitbread.premierinn.businessbooker.data.common.persistence.BusinessPersistenceManagerImpl
import com.whitbread.premierinn.ciol.analytics.logCiolAnalytics
import com.whitbread.premierinn.ciol.entity.PreStayDetailsUiModel
import com.whitbread.premierinn.ciol.entity.PreStayUiModel
import com.whitbread.premierinn.ciol.entity.upsells.MealUiModel
import com.whitbread.premierinn.ciol.entity.upsells.UpsellEntry
import com.whitbread.premierinn.ciol.entity.upsells.UpsellItem
import com.whitbread.premierinn.ciol.entity.upsells.convertToUpsellItemList
import com.whitbread.premierinn.ciol.entity.upsells.details.toRoomSelection
import com.whitbread.premierinn.ciol.mapper.convertToUiModel
import com.whitbread.premierinn.ciol.uimodel.RoomKeyInstructionsModel
import com.whitbread.premierinn.ciol.utils.addUpsellsSelectionsFromAllRooms
import com.whitbread.premierinn.ciol.utils.isKidsMeal
import com.whitbread.premierinn.ciol.utils.mapNationality
import com.whitbread.premierinn.ciol.utils.value
import com.whitbread.premierinn.common.AsyncResult
import com.whitbread.premierinn.common.CheckInCheckOutStringProvider
import com.whitbread.premierinn.common.StringResourceProvider
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Action.CIOL_START_ACTION
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Action.LEAVE_EASY_SELECTED_ACTION
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.ScreenState.BOOKING_DETAILS
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.ScreenState.LEAVE_EASY_SELECTED
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.ScreenState.START_CIOL
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Type.LEAVE_EASY_FLOW
import com.whitbread.premierinn.common.analytics.FirebaseLogger
import com.whitbread.premierinn.common.analytics.FirebaseParams
import com.whitbread.premierinn.common.analytics.TrackingAnalytics
import com.whitbread.premierinn.common.format.DateFormat
import com.whitbread.premierinn.common.forms.result.Result
import com.whitbread.premierinn.common.managebooking.ManageBookingInput
import com.whitbread.premierinn.common.mapToAsyncResult
import com.whitbread.premierinn.common.mapper.toParcelableExtrasItemDomain
import com.whitbread.premierinn.common.mvp.Presenter
import com.whitbread.premierinn.common.mvp.PresenterView
import com.whitbread.premierinn.common.pushNotification.utils.PushType
import com.whitbread.premierinn.common.service.LogService
import com.whitbread.premierinn.common.utils.getCheckInCheckoutTimes
import com.whitbread.premierinn.common.utils.getDescriptionLabel
import com.whitbread.premierinn.common.utils.retrieveCheckInCheckoutInfoFromFirebase
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManagerImpl
import com.whitbread.premierinn.domain.apprating.usecase.AppRatingPromptIfNeeded
import com.whitbread.premierinn.domain.apprating.usecase.SaveAppAsRated
import com.whitbread.premierinn.domain.booking.entity.Booking
import com.whitbread.premierinn.domain.booking.entity.PreStayHeaderInfo
import com.whitbread.premierinn.domain.booking.entity.PreStayModel
import com.whitbread.premierinn.domain.booking.usecase.GetBookingUpdates
import com.whitbread.premierinn.domain.ciol.entity.isMealWithChildBreakfast
import com.whitbread.premierinn.domain.common.AllCheckInCheckoutTimesInfoDomain
import com.whitbread.premierinn.domain.common.PriceDomain
import com.whitbread.premierinn.domain.common.SUB_CHANNEL
import com.whitbread.premierinn.domain.common.Upsell
import com.whitbread.premierinn.domain.common.hoteldetails.entity.HotelInformationDomain
import com.whitbread.premierinn.domain.common.mapFromRatePlanCodeToRatePlan
import com.whitbread.premierinn.domain.common.nightsCount
import com.whitbread.premierinn.domain.common.usecase.IsFeatureOn
import com.whitbread.premierinn.domain.graphql.bookingDetails.usecase.GraphQLBookingDetailsUseCase
import com.whitbread.premierinn.domain.graphql.findBooking.usecase.GraphQLFindBookingUseCase
import com.whitbread.premierinn.domain.graphql.hdp.entity.DataPackagesDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.ExtrasItemDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.MealDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.UpsellDomainItem
import com.whitbread.premierinn.domain.graphql.requestBodyModels.BookingChannelDetails
import com.whitbread.premierinn.domain.graphql.requestBodyModels.CancelInformationRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.CategoryLabelsRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.Channel
import com.whitbread.premierinn.domain.graphql.requestBodyModels.FindBookingRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.HotelPackagesRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.ResendInvoiceRequestBody
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository.Key
import com.whitbread.premierinn.domain.resource.usecase.GetStringResource
import com.whitbread.premierinn.hoteldetails.hdpOperaExtensions.getDinnerAllowance
import com.whitbread.premierinn.hoteldetails.hdpOperaExtensions.getPriceCapLocations
import com.whitbread.premierinn.summary.models.ParcelableExtrasItem
import dagger.hilt.android.scopes.ActivityRetainedScoped
import io.reactivex.Flowable
import io.reactivex.FlowableTransformer
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.net.URLEncoder
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.properties.Delegates

@ActivityRetainedScoped
class BookingDetailsPresenter @Inject constructor(
    private val subscriptions: CompositeDisposable,
    private val getBookingUpdates: GetBookingUpdates,
    private val bookingUiModelMapper: BookingUiModelMapper,
    private val operaBookingDetailsUiModelMapper: OperaBookingDetailsUiModelMapper,
    private val appRatingPromptIfNeeded: AppRatingPromptIfNeeded,
    private val setAppAsRated: SaveAppAsRated,
    private val logService: LogService,
    private val appFeedbackMessageProvider: AppFeedbackMessageProvider,
    private val firebaseLogger: FirebaseLogger,
    private val isFeatureOn: IsFeatureOn,
    private val storage: SimplePersistenceManagerImpl,
    private val businessStorage: BusinessPersistenceManagerImpl,
    private val contentRepository: ContentManagedResourceRepository,
    private val checkInCheckOutStringProvider: CheckInCheckOutStringProvider,
    private val getDeviceLocaleProvider: DeviceLocaleProvider,
    private val stringResourceProvider: StringResourceProvider,
    private val getStringResource: GetStringResource,
    private val graphQLBookingDetailsUseCase: GraphQLBookingDetailsUseCase,
    private val graphQLFindBookingUseCase: GraphQLFindBookingUseCase,
    private val trackingAnalytics: TrackingAnalytics,
) : Presenter<BookingDetailsPresenter.View>() {
    private lateinit var bookingReference: String
    private lateinit var uuidBasketReference: String
    private lateinit var token: String
    private lateinit var lastName: String
    private lateinit var arrivalDate: String
    private var isPushTrigger: Boolean = false
    private lateinit var pushType: String
    private var trackingCode: String? = null
    private var justBookedEmail: String? = null
    private var accountResponse: String? = null
    private var amendTotal: ParcelableAmendTotal? = null

    fun initParams(
        bookingReference: String?,
        uuidBasketReference: String?,
        token: String?,
        lastName: String?,
        arrivalDate: String?,
        isPushTrigger: Boolean,
        pushType: String?,
        justBookedEmail: String?,
        accountResponse: String?,
        amendTotal: ParcelableAmendTotal?,
        trackingCode: String?
    ) {
        this.bookingReference = bookingReference.value()
        this.uuidBasketReference = uuidBasketReference.value()
        this.token = token.value()
        this.lastName = lastName.value()
        this.arrivalDate = arrivalDate.value()
        this.isPushTrigger = isPushTrigger
        this.pushType = pushType.value()
        this.justBookedEmail = justBookedEmail
        this.accountResponse = accountResponse
        this.amendTotal = amendTotal
        bookingUiModelMapper.initParams(justBookedEmail, accountResponse)
        this.trackingCode = trackingCode
    }

    companion object {
        private val EMPTY_BOOKING_SOURCE: Observable<Booking> = Observable.empty()
        private val EMPTY_HOTEL_INFO_SOURCE: Observable<HotelInformationDomain> = Observable.empty()
    }

    private var eciLcoInfoMessage: String = EMPTY_STRING
    private var roomCriteriaSize: Int = -1
    private var nightsCount: Int = -1
    val listOfEciLco = arrayListOf<ExtrasItemDomain>()
    private var balanceOutstanding: PriceDomain = PriceDomain.createDefault()
    private var mainScope = MainScope()
    var hotelBrand: String by Delegates.observable("") { _, _, _ ->
        mainScope.launch {
            subscriptions.add(view.onHotelNameClick().map { it.hotelCode }
                .subscribe { view.startHotelDetailsActivity(it, hotelBrand) })
        }
    }

    private var allCheckInCheckoutTimesInfo: AllCheckInCheckoutTimesInfoDomain? = null
    private var checkOutInfo: String = EMPTY_STRING
    private var checkInInfo: String = EMPTY_STRING
    private val allowIfBookingStateIsNotIdle = { state: BookingDetailsState -> state.type != Idle }
    private var hotelName: String = EMPTY_STRING
    private var shouldShowBookingResultLoading = true
    private var shouldShowDetailsResultLoading = true
    private var extraList: MutableList<UpsellEntry> = mutableListOf()
    private var bookingUpsells: List<Upsell> = emptyList()

    private val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern(DateFormat.DATE_TIME_WITH_OFFSET)
    private val language = getDeviceLocaleProvider.getDeviceLocale().language.lowercase()
    private val country = getDeviceLocaleProvider.getDeviceLocale().country.lowercase()

    internal var preStayUiModel: PreStayUiModel? = null

    @VisibleForTesting lateinit var view : View
    lateinit var listOfBookingRooms: List<Booking.Room>
    private var isLoggedInAsBusinessBooker: Boolean = false
    private var isBusinessBooking: Boolean = false
    private var isLoggedInAsBusinessBookerAndIsBusinessBooking: Boolean = false
    private var hotelId: String = EMPTY_STRING
    private var dinnerAllowance : Float = 0f
    private var manageBookingInput: ManageBookingInput? = null
    private var emailOfBooker: String = EMPTY_STRING
    private var bookingConfirmationSource: Observable<Booking> = EMPTY_BOOKING_SOURCE
    private var hotelInfoObservable: Observable<HotelInformationDomain> = EMPTY_HOTEL_INFO_SOURCE

    public override fun onAttachView(view: View) {
        this.view = view
        view.shouldShowLoadingSpinner(true)
        allCheckInCheckoutTimesInfo = retrieveCheckInCheckoutInfoFromFirebase(subscriptions, logService, contentRepository)
        if (isPushTrigger) {
            setUiFromPushNotification()
        } else {
            attachPresenter(view)
        }
    }

    private fun attachPresenter(view: View) {
        val bookingActiveUpdates by lazy { getBookingUpdates.execute(bookingReference) }
        val bookingResult = bookingActiveUpdates.map(bookingUiModelMapper)
            .doOnError { logService.logException(it) }
            .compose(BookingResultTransformer())

        isLoggedInAsBusinessBooker = businessStorage.getBusinessCustomerEmail().isNotEmpty()


        val bookingDetailsResult = bookingActiveUpdates.take(1).toObservable()
            .flatMap { bookingFromStorage ->
                hotelId = bookingFromStorage.hotelCode
                isBusinessBooking = bookingFromStorage.isBusinessBooking
                isLoggedInAsBusinessBookerAndIsBusinessBooking = isLoggedInAsBusinessBooker && isBusinessBooking

                handleShowingAmendBanner(view)

                if (bookingConfirmationSource === EMPTY_BOOKING_SOURCE) {
                    bookingConfirmationSource =
                        graphQLBookingDetailsUseCase.bookingConfirmationAndManageBooking(
                            uuidBasketReference,
                            getDeviceLocaleProvider.getDeviceLocale().country.lowercase(),
                            getDeviceLocaleProvider.getDeviceLocale().language.lowercase(),
                            Channel.BB.name.takeIf { isLoggedInAsBusinessBookerAndIsBusinessBooking }
                                ?: Channel.PI.name,
                            EMPTY_STRING,
                            CancelInformationRequestBody(
                                uuidBasketReference,
                                bookingFromStorage.hotelCode,
                                OffsetDateTime.now().format(dateTimeFormatter),
                                URLEncoder.encode(token, "UTF-8"),
                                BookingChannelDetails(
                                    Channel.BB.name.takeIf { isLoggedInAsBusinessBookerAndIsBusinessBooking }
                                        ?: Channel.PI.name,
                                    SUB_CHANNEL,
                                    getDeviceLocaleProvider.getDeviceLocale().language.lowercase()
                                )
                            )
                        )
                            .cache()

                    hotelInfoObservable =
                        graphQLBookingDetailsUseCase.getHotelInfo(
                            getDeviceLocaleProvider.getDeviceLocale().country.lowercase(),
                            getDeviceLocaleProvider.getDeviceLanguage().lowercase(),
                            bookingFromStorage.hotelCode
                        ).cache()
                }

                subscriptions.add(
                    bookingConfirmationSource
                        .subscribeOn(Schedulers.io())
                        .subscribe({ booking ->
                            listOfBookingRooms = booking.rooms ?: emptyList()
                            hotelName = bookingFromStorage.hotelName
                            emailOfBooker = booking.preStayDetails?.bookerDetails?.leadBookerEmail ?: EMPTY_STRING
                            updateEciLcoMessage(booking)
                            updateBooking(booking, bookingFromStorage)
                            bookingUpsells = booking.upsells
                              manageBookingInput = ManageBookingInput.builder()
                                .hotelCode(booking.hotelCode)
                                .arrivalDate(booking.arrivalDate)
                                .departureDate(booking.departureDate)
                                .surname(booking.leadGuestSurname)
                                .bookingReference(booking.bookingReference)
                                .amendable(booking.amendable)
                                .cancellable(booking.cancellable)
                                .isEmployeeBooking(booking.isEmployeeBooking)
                                .isBusinessBooking(booking.isBusinessBooking)
                                .selectedRatePlan(
                                    if (booking.isEmployeeBooking)
                                        booking.rateCode.mapFromRatePlanCodeToRatePlan()
                                    else booking.rateCode
                                )
                                .listOfRooms(listOfBookingRooms.toListOfParcelableRooms())
                                .dinnerAllowance(dinnerAllowance)
                                .build()
                        }, { error ->
                            logService.logException(
                                error,
                                "Error while retrieving booking conf:${error.localizedMessage}"
                            )
                        })
                )

                Observable.zip(
                    bookingConfirmationSource,
                    hotelInfoObservable
                ) { booking: Booking, hotelInfo: HotelInformationDomain ->
                    if (booking.isBusinessBooking) {
                        businessStorage.getPriceCapLocations()?.let { priceCapLocations ->
                            dinnerAllowance = getDinnerAllowance(
                                priceCapLocations = priceCapLocations,
                                county = hotelInfo.county,
                                country = getDeviceLocaleProvider.getDeviceLocale().country
                            )
                        }
                    }

                    booking.preStayDetails?.let { preStayBooking ->
                        hotelInfo.preStayHeaderInfo?.let { preStayHeader ->
                            preStayUiModel = PreStayModel(
                                preStayDetails = preStayBooking,
                                preStayHeaderInfo = preStayHeader,
                                outstandingBalance = booking.balanceOutstanding,
                                basketReference = uuidBasketReference,
                                isOpera = true,
                                bookingReference = booking.bookingReference,
                                rateCode = booking.rateCode,
                                rateName = booking.rateType,
                                rateDescription = booking.rateDescription,
                                isBusinessBooking = isBusinessBooking,
                                isThirdPartyBooking = booking.isThirdPartyBooking,
                                paymentOption =  booking.paymentOption,
                                upsellsAddonsEnabled = booking.upsellsAddonsEnabled
                            ).convertToUiModel()
                        }
                    }
                    Pair(booking, hotelInfo)
                }
            }
            .map(operaBookingDetailsUiModelMapper)
            .flatMap { bookingDetailsUiModel ->
                getPackages(bookingDetailsUiModel.preStayBooking)
                    .toObservable()
                    .doOnNext { dataPackagesDomain ->
                        setUpsellsList(dataPackagesDomain)
                    }
                    .map { bookingDetailsUiModel }
                    .doOnError { error ->
                        logService.logException(error, "Error while retrieving packages: ${error.localizedMessage}")
                    }
                    .onErrorReturn { bookingDetailsUiModel }
            }
            .compose(BookingDetailsResultTransformer())

        //TODO: Need to move the mappers out of the view container
        val stateUpdates = Observable.merge(bookingResult.toObservable(), bookingDetailsResult)
            .scan(BookingDetailsState.idle()) { previousState, result ->
                when (result) {
                    is BookingResult.Success -> {
                        shouldShowBookingResultLoading = false
                        BookingDetailsState.success(bookingBasics = result.data)
                    }
                    is BookingDetailsResult.InFlight -> BookingDetailsState.inFlight(previousState.bookingUiModel)
                    is BookingDetailsResult.Success -> {
                        shouldShowDetailsResultLoading = false
                        hotelBrand = result.data.hotelBrand.name
                        setCheckInCheckoutTimes()

                        BookingDetailsState.success(
                            bookingDetails = result.data,
                            bookingBasics = previousState.bookingUiModel
                        )
                    }
                    is BookingResult.Error -> {
                        shouldShowBookingResultLoading = false
                        BookingDetailsState.idle()
                    }
                    is BookingDetailsResult.Error -> {
                        shouldShowDetailsResultLoading = false
                        BookingDetailsState.error()
                    }
                    else -> throw IllegalArgumentException("$result is not Expected oldState ->  $previousState")
                }
            }

        subscriptions.add(
            stateUpdates.distinctUntilChanged()
                .filter(allowIfBookingStateIsNotIdle)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    when (it.type) {
                        Success, InFlight, Error -> {
                            manageLoading()
                            view.bindBookingState(
                                it,
                                storage,
                                businessStorage,
                                amendTotal?.let { amendTotal }, checkInInfo, checkOutInfo,
                                balanceOutstanding, getDeviceLocaleProvider, eciLcoInfoMessage,
                                extraList, isBusinessBooking
                            )

                            if (it.type == Success && it.bookingUiModel != null && it.bookingDetails != null) {
                                logAnalytics(it.bookingUiModel.booking)
                            }
                        }
                        else -> throw IllegalArgumentException("$it is not Expected")
                    }
                })

        setClickSubscriptions()
    }

    private fun setUiFromPushNotification() {
        subscriptions.add(
            graphQLFindBookingUseCase.findBooking(
                FindBookingRequestBody(
                    resNo = bookingReference,
                    lastName = lastName,
                    arrivalDate = arrivalDate,
                    language = language,
                    country = country,
                    BookingChannelDetails(
                        Channel.BB.name.takeIf { isLoggedInAsBusinessBookerAndIsBusinessBooking } ?: Channel.PI.name,
                        SUB_CHANNEL,
                        getDeviceLocaleProvider.getDeviceLocale().language.lowercase()
                    )
                )
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ findBooking ->
                    token = findBooking.token ?: EMPTY_STRING
                    uuidBasketReference = findBooking.uuidBasketReference
                    hotelId = findBooking.hotelId

                    showPushBookingDetails(findBooking.hotelId)
                }, { error ->
                    view.shouldShowLoadingSpinner(false)
                    logService.logException(
                        error,
                        "Error while retrieving findBooking from push:${error.localizedMessage}"
                    )
                })
        )
    }

    private fun showPushBookingDetails(hotelId: String){
        val isCiolPushType = PushType.START_CIOL.type == pushType
        var leadBookerFirstName: String? = null

        val dbBooking = try {
            getBookingUpdates.execute(bookingReference)
                .subscribeOn(Schedulers.io()).blockingFirst()
        } catch (e: Exception) {
            null
        }

        handleShowingAmendBanner(view)

        subscriptions.add(
            graphQLBookingDetailsUseCase.bookingConfirmationAndManageBookingWithHotelInfoPair(
                uuidBasketReference,
                country,
                language,
                Channel.BB.name.takeIf { isLoggedInAsBusinessBookerAndIsBusinessBooking } ?: Channel.PI.name,
                EMPTY_STRING,
                CancelInformationRequestBody(
                    uuidBasketReference,
                    hotelId,
                    OffsetDateTime.now().format(dateTimeFormatter),
                    URLEncoder.encode(token, "UTF-8"),
                    BookingChannelDetails(
                        Channel.BB.name.takeIf { isLoggedInAsBusinessBookerAndIsBusinessBooking } ?: Channel.PI.name,
                        SUB_CHANNEL,
                        language
                    )
                )
            ).flatMap { (booking, hotelInfo) ->
                val bookingBasicsUiModel = bookingUiModelMapper.apply(booking)
                val bookingDetailsUiModel = operaBookingDetailsUiModelMapper.apply(
                    Pair(
                        bookingBasicsUiModel.booking,
                        hotelInfo
                    )
                )
                hotelName = hotelInfo.name

                if (isCiolPushType) {
                    listOfBookingRooms = booking.rooms ?: emptyList()
                    updateEciLcoMessage(booking)
                    updateBooking(booking, dbBooking)
                }
                setPreStayModel(booking, hotelInfo.preStayHeaderInfo)

                getPackages(bookingDetailsUiModel.preStayBooking).toObservable()
                    .map { item ->
                        Triple(
                            bookingBasicsUiModel.copy(
                                booking = bookingBasicsUiModel.booking.copy(
                                    hotelName = hotelInfo.name
                                )
                            ), bookingDetailsUiModel, item
                        )
                    }
                    .doOnError { error ->
                        logService.logException(error, "Error while retrieving packages: ${error.localizedMessage}")
                    }
                    .onErrorReturn {
                        Triple(
                            bookingBasicsUiModel.copy(
                                booking = bookingBasicsUiModel.booking.copy(
                                    hotelName = hotelInfo.name
                                )
                            ), bookingDetailsUiModel, DataPackagesDomain.createDefault()
                        )
                    }
            }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ (bookingBasicsUiModel, bookingDetailsUiModel, dataPackagesDomain) ->
                    leadBookerFirstName =
                        bookingBasicsUiModel.booking.preStayDetails?.bookerDetails?.leadBookerFirstName

                    view.shouldShowLoadingSpinner(false)
                    hotelBrand = bookingDetailsUiModel.hotelBrand.name
                    setCheckInCheckoutTimes()
                    setUpsellsList(dataPackagesDomain)
                    bookingBasicsUiModel.booking.balanceOutstanding?.let {
                        balanceOutstanding = it
                    }
                    val eciLcoMessage = if (isCiolPushType) eciLcoInfoMessage else EMPTY_STRING

                    bookingUpsells = bookingBasicsUiModel.booking.upsells

                    logAnalytics(bookingBasicsUiModel.booking)

                    view.bindBookingState(
                        BookingDetailsState.success(
                            bookingBasics = bookingBasicsUiModel,
                            bookingDetails = bookingDetailsUiModel
                        ),
                        storage,
                        businessStorage,
                        amendTotal, checkInInfo, checkOutInfo,
                        balanceOutstanding, getDeviceLocaleProvider, eciLcoMessage,
                        extraList, isBusinessBooking
                    )
                }, { error ->
                    view.shouldShowLoadingSpinner(false)
                    BookingDetailsState.error()
                    logService.logException(
                        error,
                        "Error while retrieving booking:${error.localizedMessage}"
                    )
                })
        )

        if (isCiolPushType) {
            setClickSubscriptions()
        } else {
            setClickListenersForBasicView()
            subscriptions.add(view.onReadyToLeaveButtonClick().subscribe {
                view.showReadyToLeaveBottomSheet(
                    leadBookerFirstName,
                    uuidBasketReference,
                    preStayUiModel
                )
            })
        }
    }

    private fun setPreStayModel(
        booking: Booking,
        preStayHeaderInfo: PreStayHeaderInfo?
    ) {
        booking.preStayDetails?.let { preStayBooking ->
            preStayHeaderInfo?.let { preStayHeader ->
                preStayUiModel = PreStayModel(
                    preStayDetails = preStayBooking,
                    preStayHeaderInfo = preStayHeader,
                    outstandingBalance = booking.balanceOutstanding,
                    basketReference = uuidBasketReference,
                    isOpera = true,
                    bookingReference = booking.bookingReference,
                    rateCode = booking.rateCode,
                    rateName = booking.rateType,
                    rateDescription = booking.rateDescription,
                    isBusinessBooking = isBusinessBooking,
                    isThirdPartyBooking = booking.isThirdPartyBooking,
                    paymentOption = booking.paymentOption,
                    upsellsAddonsEnabled = booking.upsellsAddonsEnabled
                ).convertToUiModel()
            }
        }
    }

    private fun updateEciLcoMessage(booking: Booking){
        if (isFeatureOn(Key.FEATURE_ALLOW_ECI_LCO)) {
            val isEciOrLcoPresent =
                booking.upsells.any { it.code == UpsellItemType.EARLY_CHECK_IN.code() || it.code == UpsellItemType.LATE_CHECK_OUT.code() }
            if (isEciOrLcoPresent) {
                // need to clear list to not duplicate elements from when they were previously added
                listOfEciLco.clear()

                val listOfEciLcoUpsell =
                    booking.upsells.filter { it.code == UpsellItemType.EARLY_CHECK_IN.code() || it.code == UpsellItemType.LATE_CHECK_OUT.code() }
                listOfEciLcoUpsell.forEach { eachEciLcoUpsell ->
                    listOfEciLco.add(
                        ExtrasItemDomain(
                            id = eachEciLcoUpsell.code,
                            name = eachEciLcoUpsell.legend,
                            price = eachEciLcoUpsell.unitCost.amount.toDouble(),
                            currency = eachEciLcoUpsell.unitCost.currency,
                            description = eachEciLcoUpsell.legend
                        )
                    )
                }
                eciLcoInfoMessage =
                    getStringResource.invoke(Key.ECI_LCO_BANNER_MESSAGE)
            }
        }
    }

    private fun handleShowingAmendBanner(view: View) {
        val (shouldShowBanner, messageKey) = if (isLoggedInAsBusinessBookerAndIsBusinessBooking) {
            isFeatureOn(Key.FEATURE_SHOULD_SHOW_AMEND_BANNER_BUSINESS) to Key.AMEND_OPERA_BOOKING_INFO_BUSINESS
        } else {
            isFeatureOn(Key.FEATURE_SHOULD_SHOW_AMEND_BANNER) to Key.AMEND_OPERA_BOOKING_INFO
        }

        if (shouldShowBanner) {
            view.showAmendBookingInfoBanner(
                getStringResource.invoke(messageKey),
                isLoggedInAsBusinessBooker
            )
        }
    }

    private fun updateBooking(booking: Booking, dbBooking: Booking?) {
        if (booking.bookingReference != EMPTY_STRING) {
            if (dbBooking == null) {
                graphQLBookingDetailsUseCase.saveUpdatedBookingInDb(booking)
            } else {
                graphQLBookingDetailsUseCase.saveUpdatedBookingInDb(
                    booking.copy(
                        cancellable = booking.cancellable,
                        hotelName = dbBooking.hotelName,
                        bookingStatus = dbBooking.bookingStatus,
                        hotelCountry = dbBooking.hotelCountry
                    )
                )
            }

            nightsCount =
                (booking.arrivalDate to booking.departureDate).nightsCount()
            roomCriteriaSize = booking.rooms?.size ?: -1
            if (booking.balanceOutstanding != null) {
                balanceOutstanding = booking.balanceOutstanding!!
            }
        }
    }

    private fun setClickSubscriptions() {
        subscriptions.add(view.onManageBookingClicked().subscribe {
            manageBookingInput?.let { input ->
                when {
                    input.amendable() -> {
                        if (!isFeatureOn(Key.FEATURE_SHOULD_SHOW_AMEND_BANNER)) {
                            view.shouldShowLoadingSpinner(true)
                            subscriptions.add(
                                graphQLBookingDetailsUseCase.findBookingAndPromoInfoCall(
                                    FindBookingRequestBody(
                                        resNo = bookingReference,
                                        lastName = input.surname(),
                                        arrivalDate = input.arrivalDate().toString(),
                                        language = getDeviceLocaleProvider.getDeviceLocale().language.lowercase(),
                                        country = getDeviceLocaleProvider.getDeviceLocale().country.lowercase(),
                                        BookingChannelDetails(
                                            Channel.BB.name.takeIf { isLoggedInAsBusinessBookerAndIsBusinessBooking }
                                                ?: Channel.PI.name,
                                            SUB_CHANNEL,
                                            getDeviceLocaleProvider.getDeviceLocale().language.lowercase()
                                        )
                                    ),
                                    country,
                                    language,
                                    Channel.BB.name.takeIf { isLoggedInAsBusinessBookerAndIsBusinessBooking }
                                        ?: Channel.PI.name,
                                    hotelBrand,
                                    input.arrivalDate().toString(),
                                    input.departureDate().toString(),
                                    uuidBasketReference)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe { findBookingAndPromo ->
                                        view.shouldShowLoadingSpinner(false)
                                        if (!findBookingAndPromo.first.token.isNullOrEmpty()) {
                                            view.startAmendBookingActivity(
                                                input,
                                                uuidBasketReference,
                                                findBookingAndPromo.first.token!!,
                                                hotelBrand,
                                                hotelName,
                                                eciLcoInfoMessage.isNotEmpty(),
                                                listOfEciLco.toList()
                                                    .toParcelableExtrasItemDomain(null),
                                                isBusinessBooking,
                                                findBookingAndPromo.second.toListOfParcelablePromotionsInformationDomain(),
                                                preStayUiModel?.preStayDetails?.bookingFlowId ?: EMPTY_STRING
                                            )
                                        } else {
                                            view.showAmendJourneyError()
                                        }
                                    })
                        } else {
                            if (input.cancellable() == true) {
                                view.startNonAmendableBookingActivity(
                                    input,
                                    uuidBasketReference,
                                    token,
                                    nightsCount,
                                    roomCriteriaSize
                                )
                            } else {
                                // when input is amendable but amend banner is on and cancellable is false
                                // this would mean we should not amend or cancel(unfortunately this is not handled well)
                            }
                        }
                    }

                    input.cancellable() == true -> {
                        view.startNonAmendableBookingActivity(
                            input,
                            uuidBasketReference,
                            token,
                            nightsCount,
                            roomCriteriaSize
                        )
                    }

                    else -> {
                        // when amendable is false and cancellable is false
                        // One more scenario that is not handled well here
                    }
                }
            }


        })


        setClickListenersForBasicView()

        subscriptions.add(view.onReadyToLeaveButtonClick().subscribe {
            logCiolAnalytics(
                analytics = trackingAnalytics,
                preStayModel = preStayUiModel,
                screenName = LEAVE_EASY_SELECTED,
                action = LEAVE_EASY_SELECTED_ACTION,
                screenType = LEAVE_EASY_FLOW
            )
            view.showReadyToLeaveBottomSheet(
                preStayUiModel?.preStayDetails?.bookerDetails?.leadBookerFirstName,
                preStayUiModel?.basketReference,
                preStayUiModel
            )
        })

        subscriptions.add(
            view.onRoomKeyInstructionsButtonClicked().subscribe {
                subscriptions.add(graphQLBookingDetailsUseCase.getRoomKeyInstructions(
                    CategoryLabelsRequestBody(
                        getDeviceLocaleProvider.getCountryIfRegion(getDeviceLocaleProvider.getDeviceLocale())
                            .lowercase(),
                        getDeviceLocaleProvider.getDeviceLanguage().lowercase(),
                        CategoryLabelsRequestBody.CATEGORY,
                        listOf(
                            CategoryLabelsRequestBody.LABEL_TITLE,
                            hotelBrand.getDescriptionLabel()
                        )
                    )
                )
                    .mapToAsyncResult()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { result ->
                        when (result) {
                            is AsyncResult.Loading -> {
                                view.shouldShowLoadingSpinner(true)
                            }

                            is AsyncResult.Success -> {
                                view.shouldShowLoadingSpinner(false)

                                val roomKeyInstructionsUiModel = result.data?.convertToUiModel(
                                    preStayUiModel?.bookingReference,
                                    preStayUiModel?.preStayHeaderInfo?.hotelImage,
                                    preStayUiModel?.preStayDetails?.hotelId
                                )
                                roomKeyInstructionsUiModel.let {
                                    view.showRoomKeyInstructionsBottomSheet(
                                        roomKeyInstructionsUiModel
                                    )
                                }
                            }

                            is AsyncResult.Error -> {
                                view.shouldShowLoadingSpinner(false)
                            }

                            else -> {}
                        }
                    }
                )
            }
        )

        subscriptions.add(view.onCheckInOnlineButtonClick()
            .subscribe {
                val pushToken = storage.getFirebaseToken()
                preStayUiModel?.let { model ->
                    logCiolAnalytics(
                        analytics = trackingAnalytics,
                        preStayModel = preStayUiModel,
                        screenName = START_CIOL,
                        action = CIOL_START_ACTION,
                        pushToken = pushToken)
                    view.showCheckInInfoBottomSheet(
                        preStayModel = model.mapNationality(getDeviceLocaleProvider.getNationalityBasedOnDeviceLanguage())
                    )
                } ?: run {
                    view.showStartCheckInOnlineError()
                }
            }
        )

        subscriptions.add(appRatingPromptIfNeeded.execute(!justBookedEmail.isNullOrEmpty())
            .filter { it }
            .delay(3, TimeUnit.SECONDS, Schedulers.io())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe { view.showAppRatingPrompt() })

        subscriptions.add(
            view.onAppRatingPromptOkClicked()
                .doOnNext {
                    setAppAsRated.execute()
                    firebaseLogger.logEvent(
                        FirebaseLogger.Event.APP_RATING,
                        FirebaseParams().apply {
                            putString(FirebaseParams.ParamName.RATING_TYPE, "positive")
                        })
                }.subscribe { view.navigateToPlaystore(Urls.PLAYSTORE) })

        subscriptions.add(view.onAppRatingPromptCancelClicked()
            .doOnNext { firebaseLogger.logEvent(FirebaseLogger.Event.APP_RATING_CANCELLATION) }
            .subscribe())

        subscriptions.add(
            view.onAppRatingFeedbackClicked()
                .doOnNext {
                    setAppAsRated.execute()
                    firebaseLogger.logEvent(
                        FirebaseLogger.Event.APP_RATING,
                        FirebaseParams().apply {
                            putString(FirebaseParams.ParamName.RATING_TYPE, "negative")
                        })
                }.subscribe {
                    view.showFeedbackPrompt(
                        appFeedbackMessageProvider.emailAddress,
                        appFeedbackMessageProvider.emailFeedbackSubject,
                        appFeedbackMessageProvider.emailFeedbackBody
                    )
                })

        val channel = if (isLoggedInAsBusinessBookerAndIsBusinessBooking) Channel.BB.name else Channel.PI.name
        subscriptions.add(
            view.onSendInvoiceDialogClicked()
                .flatMapSingle {
                    graphQLBookingDetailsUseCase.resendInvoice(
                        ResendInvoiceRequestBody(
                            hotelId = hotelId,
                            email = EMPTY_STRING,
                            // IMP: Do not assign to booking reference as the BE is expecting basketReference here
                            bookingReference = uuidBasketReference,
                            bookingChannel = BookingChannelDetails(
                                channel,
                                SUB_CHANNEL,
                                getDeviceLocaleProvider.getDeviceLanguage().lowercase()
                            )
                        )
                    )
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .materialize()
                }
                .subscribe { notification ->
                    when {
                        notification.isOnNext -> view.showSendInvoiceMessage(true, emailOfBooker)
                        notification.isOnError -> view.showSendInvoiceMessage(false, EMPTY_STRING)
                    }
                }
        )
    }

    fun scrollToBanner() {
        view.scrollToBanner()
    }

    private fun setClickListenersForBasicView() {
        subscriptions.add(
            view.onFaqButtonClicked().subscribe { view.startUriActivity(stringResourceProvider.getString(R.string.faq_web_url))})

        subscriptions.add(view.onParkingButtonClicked().subscribe { parkingAction ->
            view.showParkingDetailsBottomSheet(parkingAction.parkingDescription)
        })

        subscriptions.add(view.onDirectionClicked().map { it.input }
            .subscribe { view.startUriActivity(it) })

        subscriptions.add(view.onAddToCalendarClick().subscribe { view.startCalendarApp(it) })
        subscriptions.add(view.onSendInvoiceClicked().subscribe { view.sendInvoice() })
    }

    private fun setUpsellsList(item: DataPackagesDomain) {
        val availableUpsellItems =
            mutableListOf<UpsellDomainItem>().apply {
                addAll(item.packages.meals)
                addAll(item.packages.mealsKids)
                item.packages.extrasItems?.let { addAll(it) }
            }

        val selectedIdList = item.packages.roomSelection
            ?.flatMap { it.packagesSelection }
            ?.map { it.id }

        availableUpsellItems.removeUnselectedExtras(selectedIdList)

        extraList = availableUpsellItems.convertToUpsellItemList()
        val roomSelection = item.packages.roomSelection?.toRoomSelection()
        roomSelection?.let {
            extraList.addUpsellsSelectionsFromAllRooms(roomSelection)
            extraList.removeKidsUpsell()
        }
    }

    private fun getPackages(preStayDetailsUiModel: PreStayDetailsUiModel?): Single<DataPackagesDomain> {
        preStayDetailsUiModel?.let {
            return graphQLBookingDetailsUseCase.getPackages(
                HotelPackagesRequestBody(
                    hotelId = it.hotelId,
                    startDate = it.startDate.toString(),
                    endDate = it.endDate.toString(),
                    adultsNumber = it.numberOfAdults,
                    childrenNumber = it.numberOfChildren,
                    nightsNumber = it.numberOfNights,
                    language = getDeviceLocaleProvider.getDeviceLanguage().lowercase(),
                    country = getDeviceLocaleProvider.getCountryIfRegion(getDeviceLocaleProvider.getDeviceLocale())
                        .lowercase(),
                    bookingFlowId = it.bookingFlowId,
                    showMealInclusiveRate = true,
                    basketReferenceId = uuidBasketReference,
                    channel = if (isLoggedInAsBusinessBookerAndIsBusinessBooking) Channel.BB else Channel.PI,
                )
            )
        }
        return Single.error(Exception("preStayDetails is null"))
    }

    private fun setCheckInCheckoutTimes() {
        val checkInOutTime = getCheckInCheckoutTimes(hotelBrand, allCheckInCheckoutTimesInfo, checkInCheckOutStringProvider)
        checkInInfo = checkInOutTime.first
        checkOutInfo = checkInOutTime.second
    }

    private fun manageLoading() {
        if (!shouldShowBookingResultLoading && !shouldShowDetailsResultLoading) {
            view.shouldShowLoadingSpinner(showLoading = false)
            shouldShowBookingResultLoading = true
            shouldShowDetailsResultLoading = true
        }
    }

    sealed class BookingResult : Result {
        class Success(val data: BookingBasicsUiModel) : BookingResult()
        class Error(val error: Throwable) : BookingResult()
    }

    sealed class BookingDetailsResult : Result {
        object InFlight : BookingDetailsResult()
        class Success(val data: BookingDetailsUiModel) : BookingDetailsResult()
        class Error(val error: Throwable) : BookingDetailsResult()
    }

    class BookingDetailsResultTransformer : ObservableTransformer<BookingDetailsUiModel, Result> {
        override fun apply(upstream: Observable<BookingDetailsUiModel>): ObservableSource<Result> {
            return upstream.map<Result> {
                BookingDetailsResult.Success(it)
            }.onErrorReturn {
                BookingDetailsResult.Error(it)
            }.startWith(BookingDetailsResult.InFlight)
        }
    }

    class BookingResultTransformer : FlowableTransformer<BookingBasicsUiModel, Result> {
        override fun apply(upstream: Flowable<BookingBasicsUiModel>): Flowable<Result> {
            return upstream.map<Result> { BookingResult.Success(it) }
                .onErrorReturn { BookingResult.Error(it) }
        }
    }

    private fun MutableList<UpsellDomainItem>.removeUnselectedExtras(upsellId: List<String?>?) {
        if (upsellId != null) {
            if (upsellId.isEmpty()) {
                clear()
            } else {
                removeAll { item ->
                    when (item) {
                        is ExtrasItemDomain ->  item.id !in upsellId
                        is MealDomain -> item.id !in upsellId
                    }
                }
            }
        }
    }

    private fun MutableList<UpsellEntry>.removeKidsUpsell() {
        val childUpsell = this.firstOrNull { it is MealUiModel && it.id.isKidsMeal() }
        val firstMealDeal = this.firstOrNull { it is MealUiModel && it.id.isMealWithChildBreakfast() }
        childUpsell?.let { childItem ->
            firstMealDeal?.let { mealItem ->
                mealItem as MealUiModel
                mealItem.freeBreakfastSelections = (childItem as MealUiModel).noOfSelections
            }
            this.remove(childItem)
        }
    }

    fun refreshBookingData() {
        bookingConfirmationSource = EMPTY_BOOKING_SOURCE
        hotelInfoObservable = EMPTY_HOTEL_INFO_SOURCE
    }

    private fun logAnalytics(booking: Booking) {
        val analyticsData = BookingDetailsAnalyticsData(
            isThirdPartyBooking = booking.isThirdPartyBooking,
            thirdPartyBookingId = if (booking.isThirdPartyBooking) booking.bookingReference else null,
            upsells = bookingUpsells.isNotEmpty(),
            trackingCode = trackingCode
        )
        trackingAnalytics.track(BOOKING_DETAILS, analyticsData)
    }

    override fun onDetachView() {
        super.onDetachView()
        subscriptions.clear()
    }

    public override fun onDestroy() {
        subscriptions.clear()
        mainScope.cancel()
    }

    interface View : PresenterView {

        fun bindBookingState(
            state: BookingDetailsState,
            storage: SimplePersistenceManagerImpl,
            businessPersistenceManagerImpl: BusinessPersistenceManagerImpl,
            amendTotal: ParcelableAmendTotal?,
            checkInTime: String,
            checkOutTime: String,
            balanceOutstandingPrice: PriceDomain,
            getDeviceLocaleProvider: DeviceLocaleProvider,
            eciLcoMessage: String,
            upsellItems: List<UpsellItem>,
            isBusinessBooking: Boolean
        )

        fun shouldShowLoadingSpinner(showLoading: Boolean)

        fun showAmendJourneyError()

        fun showPromotionAmendmentNotAllowedDialog(promotionalAmendMessage: String)

        fun startUriActivity(uri: String)

        fun startCalendarApp(action: AddToCalendarAction)

        fun sendInvoice()

        fun startAmendBookingActivity(
            manageBookingInput: ManageBookingInput,
            uuidBasketReference: String,
            token: String,
            hotelBrand: String,
            hotelName: String,
            isEciLcoBooking: Boolean,
            listOfEciLco: List<ParcelableExtrasItem>,
            isBusinessBooker: Boolean,
            promotionsInfoDomain: ParcelablePromotionsInformationDomain,
            bookingFlowId: String
        )

        fun startNonAmendableBookingActivity(
            manageBookingInput: ManageBookingInput,
            uuidBasketReference: String,
            cancelBookingToken: String, nightsCount: Int, roomCriteriaSize: Int
        )

        fun startHotelDetailsActivity(hotelCode: String, hotelBrand: String)

        fun showParkingDetailsBottomSheet(parkingDescription: String)

        fun showCheckInInfoBottomSheet(preStayModel: PreStayUiModel)

        fun onDirectionClicked(): Observable<MapDirectionsAction>

        fun onAddToCalendarClick(): Observable<AddToCalendarAction>

        fun onFaqButtonClicked(): Observable<Unit>

        fun onRoomKeyInstructionsButtonClicked(): Observable<RoomKeyInstructionsAction>

        fun onParkingButtonClicked(): Observable<ParkingAction>

        fun showRoomKeyInstructionsBottomSheet(roomKeyInstructionsModel: RoomKeyInstructionsModel?)

        fun onManageBookingClicked(): Observable<Unit>

        fun onPriceBreakdownButtonClicked(): Observable<Unit>

        fun onHotelNameClick(): Observable<HotelDetailsAction>

        fun onCheckInOnlineButtonClick(): Observable<CheckInOnlineAction>

        fun onReadyToLeaveButtonClick(): Observable<ReadyToLeaveAction>

        fun showAppRatingPrompt()

        fun navigateToPlaystore(fallbackUrl: String)

        fun onAppRatingPromptOkClicked(): Observable<Any>

        fun onAppRatingPromptCancelClicked(): Observable<Any>

        fun onAppRatingFeedbackClicked(): Observable<Any>

        fun showFeedbackPrompt(receiverAddress: String, subject: String, body: String)

        fun showAmendBookingInfoBanner(message: String, isBusinessBooker: Boolean)

        fun showStartCheckInOnlineError()

        fun showReadyToLeaveBottomSheet(guestName: String?, basketReference: String?, preStayModel: PreStayUiModel?)

        fun onSendInvoiceClicked(): Observable<SendInvoiceAction>

        fun onSendInvoiceDialogClicked(): Observable<Any>

        fun showSendInvoiceMessage(success: Boolean, email: String)
        fun scrollToBanner()
    }
}
