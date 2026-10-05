package com.whitbread.premierinn.landing

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.tbruyelle.rxpermissions2.RxPermissions
import com.whitbread.premierinn.R
import com.whitbread.premierinn.account.AccountActivity
import com.whitbread.premierinn.api.response.search.SearchItemInput
import com.whitbread.premierinn.calendar.maincalendar.HomeCalendarActivity
import com.whitbread.premierinn.common.AutoCompositeDisposable
import com.whitbread.premierinn.common.FREQUENT_BOOKINGS
import com.whitbread.premierinn.common.PAST_SEARCHES
import com.whitbread.premierinn.common.RECENT_SEARCHES
import com.whitbread.premierinn.common.UPCOMING_BOOKING
import com.whitbread.premierinn.common.addTo
import com.whitbread.premierinn.common.bottomnavigation.BottomNavigationActivity
import com.whitbread.premierinn.common.mapper.toParcelablePromoContent
import com.whitbread.premierinn.common.utils.IntentUtils
import com.whitbread.premierinn.databinding.ActivityLandingBinding
import com.whitbread.premierinn.domain.common.OperaFallbackPopupInfoDomain
import com.whitbread.premierinn.domain.common.PromoContentDomain
import com.whitbread.premierinn.domain.common.RoomCriteria
import com.whitbread.premierinn.domain.dashboard.entity.DashboardItem
import com.whitbread.premierinn.domain.dashboard.entity.FrequentBooking
import com.whitbread.premierinn.domain.recentsearch.entity.RecentSearch
import com.whitbread.premierinn.firsttimedownload.FirstTimeOfferBottomSheet
import com.whitbread.premierinn.hoteldetails.HotelDetailsActivity
import com.whitbread.premierinn.hoteldetails.HotelDetailsInput
import com.whitbread.premierinn.landing.model.BottomSheetState
import com.whitbread.premierinn.landing.model.CardNavigationModel
import com.whitbread.premierinn.landing.ui_compose.HomePageBottomSheet
import com.whitbread.premierinn.roomcriteria.ParcelableRooms
import com.whitbread.premierinn.roomcriteria.RoomCriteriaActivity
import com.whitbread.premierinn.roomcriteria.createAlertDialog
import com.whitbread.premierinn.roomcriteria.toRoomCriteriaList
import com.whitbread.premierinn.search.SearchActivity
import com.whitbread.premierinn.searchresults.SearchResultsInput
import com.whitbread.premierinn.searchresults.createSearchResultIntent
import com.whitbread.premierinn.utils.openUrlWithFallback
import org.threeten.bp.LocalDate

const val ANIMATION_DURATION = 1000L

abstract class BaseLandingActivity : BottomNavigationActivity<ActivityLandingBinding>() {

    private val disposables = AutoCompositeDisposable(lifecycle)
    val viewModel: LandingViewModel by viewModels()
    private val locationView by lazy { binding.itemLandingWhereToLocation }
    private val locationText by lazy { binding.itemLandingWhereToLocation.landingLocationText }
    private val dateView by lazy { binding.itemLandingDate }
    private val dateText by lazy { binding.itemLandingDate.landingDateText }
    private val roomsView by lazy { binding.itemLandingRoom }
    private val roomsText by lazy { binding.itemLandingRoom.landingRoomsText }
    private val submitButton by lazy { binding.landingSearchButton }
    private val covidDismissButton by lazy { binding.viewCoronavirusFrameLayout.coronavirusDismissButton }
    private val dashboardContainer by lazy { binding.dashboardContainer }
    private val companyView by lazy { binding.companyView }
    private val companyName by lazy { binding.companyView.companyName }
    private val piLogo by lazy { binding.piLogo }
    private val composeBottomSheetContainerView by lazy { binding.bottomSheet}

    private lateinit var covidBannerView: View
    private lateinit var covidMessageView: TextView

    private val rxPermissions by lazy { RxPermissions(this) }
    private var bottomSheetState = mutableStateOf(BottomSheetState())

    override fun inflateBinding(inflater: LayoutInflater): ActivityLandingBinding {
        return ActivityLandingBinding.inflate(inflater)
    }

    override fun getToolbar(): Toolbar? {
        return null;
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.exitTransition = null

        covidBannerView = findViewById(R.id.view_coronavirus_frame_layout)
        covidMessageView = covidBannerView.findViewById(R.id.coronavirus_text_description)

        viewModel.isInnBusinessUserLiveData.observe(this) { isInnBusinessUser ->
            if (isInnBusinessUser) {
                piLogo.visibility = View.GONE
                companyView.root.visibility = View.VISIBLE
            } else {
                piLogo.visibility = View.VISIBLE
                companyView.root.visibility = View.GONE
            }
        }

        viewModel.events()
                .subscribe {
                    when (it) {
                        is OpenSearch -> openSearch()
                        is OpenCalendar -> openCalendar(it.dates, it.maxNights, it.maxArrivalDate)
                        is OpenRoomCriteria -> openRoomSelection(it.roomCriteria, it.isBusinessUser)
                        is OpenSearchResults -> openSearchResults(
                            it.searchResultsInput,
                            it.placeId,
                            it.country,
                            it.language)
                        is OpenHotelDetails -> openHotelDetails(it.searchPayload, it.hotelCode, it.brand)
                        is OpenHotelAlternatives -> openHotelAlternatives(
                                it.searchPayload,
                                it.hotelCode,
                                it.placeId,
                                it.country,
                                it.language
                        )
                        is RequestLocationPermission -> requestLocationPermission()
                        is BusinessBookerRecentSearchError -> showBusinessBookerRecentSearchErrorDialog()
                        is OpenFallbackPopup -> showFallbackPopup(it.fallbackPopupInfo)
                        is OpenHotelInWebView -> openHotelInWebView()
                        is MaxRoomsError -> showMaxRoomErrorDialog(it)
                        is MaxNightsError -> showMaxNightsErrorDialog(it.nights)
                        is OpenCardDestination -> openLink(it.link, it.shouldOpenInApp)
                        is OpenNotificationLinkDestination -> openLink(it.link, it.shouldOpenInApp)
                        is BusinessLoginCustomerOrCompanyError -> redirectOnBusinessCustomerOrCompanyLoginError()
                        is OpenIncentiveScreen -> showFirstTimeOfferSheet(it.promoContent, it.promoCode)
                    }
                }.addTo(disposables)

        viewModel.uiStates()
                .subscribe { updateUi(it) }
                .addTo(disposables)

        locationView.root.setOnClickListener { viewModel.onLocationBoxClick() }
        dateView.root.setOnClickListener { viewModel.onDateBoxClick() }
        roomsView.root.setOnClickListener { viewModel.onRoomsBoxClick() }
        submitButton.setOnClickListener { viewModel.onSubmitClick() }
        covidDismissButton.setOnClickListener { viewModel.onCovidDismissButtonClicked() }

        requestNotificationPermission()
        setupBottomSheet()
        viewModel.runPromoCheck()

        viewModel.updateRecentSearches(true)
    }

    private fun openLink(link: String, shouldOpenInApp: Boolean) {
        if (shouldOpenInApp) {
            openUrlWithFallback(this, link)
        } else {
            startActivity(IntentUtils.createWebLinkIntent(link))
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onActivityResumed()
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            rxPermissions.request(Manifest.permission.POST_NOTIFICATIONS)
                .subscribe(viewModel::onNotificationPermissionResult)
                .addTo(disposables)
        }
    }

    private fun openSearch() {
        startActivityForResult(SearchActivity.createIntent(this), SEARCH_REQUEST_CODE)
    }

    private fun openCalendar(dates: Pair<LocalDate, LocalDate>?, maxNights: Int, maxArrivalDate: Int) {
        dates?.let {
            startActivityForResult(
                    HomeCalendarActivity.createIntent(this, it.first, it.second, maxNights, maxArrivalDate),
                    CALENDAR_REQUEST_CODE
            )
        } ?: kotlin.run {
            startActivityForResult(HomeCalendarActivity.createIntent(this, maxNights, maxArrivalDate), CALENDAR_REQUEST_CODE)
        }
    }

    private fun showFallbackPopup(fallbackPopupInfo: OperaFallbackPopupInfoDomain) {
        AlertDialog.Builder(this, R.style.PurpleDialog)
            .setTitle((fallbackPopupInfo.operaFallbackAlertTitleDomain))
            .setMessage(fallbackPopupInfo.operaFallbackAlertMessageDomain)
            .setNegativeButton(fallbackPopupInfo.operaFallbackAlertCloseDomain)
            { _, _ -> viewModel.onFallbackCancelButtonClicked()}
            .setPositiveButton(fallbackPopupInfo.operaFallbackAlertContinueDomain)
            { _, _ -> viewModel.onFallbackContinueButtonClicked() }
            .create()
            .show()
    }

    private fun openHotelInWebView() {
        startActivity(IntentUtils.createWebLinkIntent(getString(R.string.fallback_hotel_web_url)))
    }

    private fun openRoomSelection(roomCriteria: List<RoomCriteria>, isInnBusinessUser: Boolean) {
        val roomCriteriaUpdated: List<RoomCriteria> = if (isInnBusinessUser) {
            roomCriteria.take(1)
        } else {
            roomCriteria
        }
        startActivityForResult(
                RoomCriteriaActivity.createIntent(this, roomCriteriaUpdated),
                ROOMS_REQUEST_CODE
        )
    }

    private fun openSearchResults(
        searchResultsInput: SearchResultsInput,
        placeId: String?,
        country: String,
        language: String
    ) {
        startActivity(createSearchResultIntent(
                input = searchResultsInput,
                launchedByLandingActivity = true,
                placeId = placeId,
                country = country,
                language = language))
    }

    private fun openHotelAlternatives(
        payload: SearchPayload,
        hotelCode: String,
        placeId: String?,
        country: String,
        language: String
    ) {
        val input = payload.toSearchResultsInput()
        startActivity(
                createSearchResultIntent(
                        input = input,
                        hotelCode = hotelCode,
                        launchedByLandingActivity = true,
                        placeId = placeId,
                        country = country,
                        language = language
                )
        )
    }

    private fun openHotelDetails(payload: SearchPayload, hotelCode: String, brand: String) {
        val input = HotelDetailsInput.builder()
                .searchResultsInput(payload.toSearchResultsInput())
                .hotelName(payload.placeName)
                .distanceFromSearchedLocation(0f)
                .cameFromMapView(false)
                .hotelCode(hotelCode)
                .brand(brand)
                .build()
        startActivity(HotelDetailsActivity.createIntent(this, input))
    }

    private fun requestLocationPermission() {
        rxPermissions.request(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        )
                .subscribe { viewModel.onLocationPermissionResult(it) }
                .addTo(disposables)
    }

    private fun updateUi(state: UIModel) {
        showOverlay(state.isLoading)

        if (state.companyName.isNotEmpty()) {
            companyView.root.visibility = View.VISIBLE
            companyName.text = state.companyName
        } else companyView.root.visibility = View.GONE
        locationText.text = state.location
        dateText.text = state.dates
        roomsText.text = state.rooms
        submitButton.setLoadingState(state.showSubmitSpinner)
        if (state.showCovidBanner) {
            showCovidBanner(state.covidBannerMessage)
        } else {
            hideCovidBanner()
        }

        // hide old dashboard until content is migrated to new bottom sheet
        //createDashboard(state.dashboardItem, state.leadGuestDetails, state.recentSearches, state.recentSearchesCleared, state.language)

        bottomSheetState.value = BottomSheetState(
            contentDomain = state.bottomSheetContent,
            recentSearches = state.recentSearches)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                SEARCH_REQUEST_CODE -> onSearchActivityResult(requireNotNull(intent))
                CALENDAR_REQUEST_CODE -> onCalendarActivityResult(requireNotNull(intent))
                ROOMS_REQUEST_CODE -> onRoomsActivityResult(requireNotNull(intent))
            }
        }
    }

    private fun onSearchActivityResult(intent: Intent) {
        val searchItemInput =
                requireNotNull(intent.getParcelableExtra<SearchItemInput>(SearchActivity.SELECTED_SEARCH_ITEM))
        viewModel.onLocationSet(searchItemInput)
    }

    private fun onCalendarActivityResult(intent: Intent) {
        val arrival =
                intent.getSerializableExtra(HomeCalendarActivity.CALENDAR_SELECTED_ARRIVAL) as LocalDate
        val departure =
                intent.getSerializableExtra(HomeCalendarActivity.CALENDAR_SELECTED_DEPARTURE) as LocalDate
        viewModel.onDatesSet(arrival, departure)
    }

    private fun onRoomsActivityResult(intent: Intent) {
        val parcelableRooms =
            intent.getParcelableExtra(RoomCriteriaActivity.ROOM_CRITERIA_SELECTION) as ParcelableRooms?
        val roomsCriteria = parcelableRooms?.toRoomCriteriaList()
        roomsCriteria?.let { _ ->
            viewModel.onRoomCriteriaSet(roomsCriteria)
        }
    }

    private fun showCovidBanner(message: String?) {
        covidMessageView.text = message
        covidBannerView.visibility = View.VISIBLE
    }

    private fun hideCovidBanner() {
        covidBannerView.visibility = View.GONE
    }

    private fun createDashboard(dashboardItems: List<DashboardItem?>, leadGuestDetails: LeadGuestDetails,
                                recentSearches: List<RecentSearch>, recentSearchesCleared: Boolean,
                                language: String) {
        val upcomingBooking = dashboardItems.firstOrNull { dashboard -> dashboard?.type == UPCOMING_BOOKING }
        val frequentlyBooked = dashboardItems.firstOrNull { dashboard -> dashboard?.type == FREQUENT_BOOKINGS }?.content?.frequentBookings
        val showRecentSearch = dashboardItems.firstOrNull { dashboard -> dashboard?.type == RECENT_SEARCHES || dashboard?.type == PAST_SEARCHES }

        dashboardContainer.visibility = View.VISIBLE
        when {
//           Dashboard is not migrated, so we will comment this out

//            upcomingBooking != null -> {
//                //showUpcomingBooking(upcomingBooking, leadGuestDetails, language)
//            }
//            showRecentSearch != null && recentSearches.isNotEmpty() -> {
//                showRecentSearches(recentSearches)
//            }
//            frequentlyBooked != null && frequentlyBooked.isNotEmpty() -> {
//                //Dashboard is not migrated, so we will comment this out
//                //showFrequentBookings(frequentlyBooked)
//            }
            recentSearchesCleared -> {
                viewModel.recentSearchCleared()
                val animation = AnimationUtils.loadAnimation(this, R.anim.slide_down)
                animation.duration = ANIMATION_DURATION
                dashboardContainer.startAnimation(animation)
            }
            else -> {
                if (recentSearches.isNotEmpty()) {
                    showRecentSearches(recentSearches)
                } else {
                    dashboardContainer.visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun showUpcomingBooking(dashboardItem: DashboardItem, leadGuestDetails: LeadGuestDetails, language: String) {
        dashboardItem.content?.let { content ->
            if (content.arrivalDate != null && content.departureDate != null) {
                val hotelItem = UpcomingBookingView(this, leadGuestDetails, content, language)
                dashboardContainer.removeAllViews()
                dashboardContainer.addView(hotelItem.upcomingBookingBinding.root)
            }
        }
    }

    private fun showRecentSearches(recentSearches: List<RecentSearch>) {
        val recentSearchesView = RecentSearchesView(
                this, recentSearches, ::onRecentSearchesClick, ::onClearRecentSearchesClick)
        dashboardContainer.removeAllViews()
        dashboardContainer.addView(recentSearchesView.view)
    }

    private fun showFrequentBookings(frequentlyBooked: List<FrequentBooking>) {
        val frequentBooking = FrequentlyBookedView(this, frequentlyBooked)
        dashboardContainer.removeAllViews()
        dashboardContainer.addView(frequentBooking.frequentlyBookedBinding.root)
    }

    private fun onNotificationLinkClick(link: String, shouldOpenInApp: Boolean) {
        viewModel.onNotificationLinkClick(link, shouldOpenInApp)
    }

    private fun onRecentSearchesClick(selectedSearchItem: RecentSearch) {
        viewModel.onRecentSearchClicked(selectedSearchItem)
    }

    private fun onClearRecentSearchesClick() {
        viewModel.deleteRecentSearches()
    }

    private fun onHomeCardsClick(card: CardNavigationModel) {
        viewModel.onHomeCardsClick(card)
    }

    private fun showBusinessBookerRecentSearchErrorDialog() {
        AlertDialog.Builder(this, R.style.PurpleDialog)
                .setTitle(getString(R.string.dialog_business_booker_recent_search_error_title))
                .setMessage(getString(R.string.dialog_business_booker_recent_search_error_messagee))
                .setPositiveButton(getString(R.string.button_text_continue))
                { _, _ ->  }
                .create()
                .show()
    }

    private fun showMaxRoomErrorDialog(event: MaxRoomsError) {
        createAlertDialog(event.message, event.phoneNumber, event.isGroupFormRequired).show()
    }

    private fun showMaxNightsErrorDialog(maxNights: Int) {
        val phoneNumber = resources.getString(R.string.call_view_default_customer_service_number)
        AlertDialog.Builder(this, R.style.PurpleDialog)
            .setTitle(
                resources.getString(
                    R.string.calendar_too_many_nights_dialog_title,
                    maxNights.toString()
                )
            )
            .setMessage(resources.getString(R.string.calendar_too_many_nights_dialog_description))
            .setNegativeButton(R.string.cancel_dialog) { dialog, _ -> dialog.dismiss() }
            .setPositiveButton(R.string.call_us) { _, _ ->
                val callIntent = IntentUtils.createTelephoneIntent(phoneNumber)
                if (IntentUtils.checkIntentResolvedActivity(this, callIntent)) {
                    startActivity(callIntent)
                } else {
                    Toast.makeText(
                        this,
                        R.string.phone_call_action_not_supported,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }.create().show()
    }

    private fun showOverlay(visibility: Boolean) {
        binding.progressBarContainer.isVisible = visibility
        binding.loadingIndicator.isVisible = BottomSheetBehavior.from(composeBottomSheetContainerView).state == BottomSheetBehavior.STATE_EXPANDED
    }

    private fun redirectOnBusinessCustomerOrCompanyLoginError() {
        AccountActivity.start(this)
    }

    private fun setupBottomSheet() {
        BottomSheetBehavior.from(composeBottomSheetContainerView).apply {
            isDraggable = true
            isHideable = false
            peekHeight = resources.displayMetrics.heightPixels / 4
        }
        composeBottomSheetContainerView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                // In Compose world
                MaterialTheme {
                    HomePageBottomSheet(
                        state = bottomSheetState.value,
                        onClearRecentSearchClick = ::onClearRecentSearchesClick,
                        onCardClick = ::onHomeCardsClick,
                        onRecentSearchClick = ::onRecentSearchesClick,
                        onNotificationLinkClick = ::onNotificationLinkClick
                    )
                }
            }
        }
    }

    private fun showFirstTimeOfferSheet(promoContent: PromoContentDomain, promoCode: String) {
        FirstTimeOfferBottomSheet(
            promoContent.toParcelablePromoContent(),
            promoCode
        ).show(this.supportFragmentManager)
    }

    override fun getSelectedMenuItem(): Int {
        return R.id.bottom_navigation_search
    }

    companion object {

        private const val SEARCH_REQUEST_CODE = 1
        private const val CALENDAR_REQUEST_CODE = 2
        private const val ROOMS_REQUEST_CODE = 3
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.saveSelectedHomeScreenCriteria()
    }
}