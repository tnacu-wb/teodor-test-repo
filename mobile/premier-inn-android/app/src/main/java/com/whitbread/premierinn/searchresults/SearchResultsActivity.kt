package com.whitbread.premierinn.searchresults

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.collection.ArrayMap
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.doOnNextLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_DRAGGING
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HIDDEN
import com.google.android.material.bottomsheet.BottomSheetBehavior.from
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxrelay2.PublishRelay
import com.whitbread.premierinn.R
import com.whitbread.premierinn.api.response.availability.Coordinates
import com.whitbread.premierinn.api.response.search.SearchItemInput
import com.whitbread.premierinn.common.AutoCompositeDisposable
import com.whitbread.premierinn.common.ReactiveView
import com.whitbread.premierinn.common.activity.BaseActivity
import com.whitbread.premierinn.common.addTo
import com.whitbread.premierinn.common.service.LogService
import com.whitbread.premierinn.common.utils.BottomSheetSimpleCallback
import com.whitbread.premierinn.common.utils.IntentUtils
import com.whitbread.premierinn.common.utils.MapUtils
import com.whitbread.premierinn.common.utils.StringUtils
import com.whitbread.premierinn.common.utils.argument
import com.whitbread.premierinn.common.utils.getSnapPosition
import com.whitbread.premierinn.common.utils.hideOnTop
import com.whitbread.premierinn.common.utils.openIncentiveTermsAndConditions
import com.whitbread.premierinn.common.utils.showFromTop
import com.whitbread.premierinn.common.view.ToggleButtonView
import com.whitbread.premierinn.databinding.ActivitySearchResultsMapListBinding
import com.whitbread.premierinn.domain.availability.AvailabilitySortBy.DISTANCE
import com.whitbread.premierinn.domain.availability.AvailabilitySortBy.PRICE
import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import com.whitbread.premierinn.domain.common.OperaFallbackPopupInfoDomain
import com.whitbread.premierinn.domain.common.PromoContentDomain
import com.whitbread.premierinn.domain.graphql.requestBodyModels.HotelAvailabilitiesRequestBody
import com.whitbread.premierinn.domain.search.entity.Location
import com.whitbread.premierinn.hoteldetails.HotelDetailsActivity
import com.whitbread.premierinn.hoteldetails.HotelDetailsInput
import com.whitbread.premierinn.landing.LandingActivityIntent
import com.whitbread.premierinn.landing.model.LandingInputModel
import com.whitbread.premierinn.searchresults.SearchResultsViewEvent.ChangeMapCentreEvent
import com.whitbread.premierinn.searchresults.SearchResultsViewEvent.ChangeModeStateEvent
import com.whitbread.premierinn.searchresults.SearchResultsViewEvent.DismissCoronavirusEvent
import com.whitbread.premierinn.searchresults.SearchResultsViewEvent.DismissInformationBannerEvent
import com.whitbread.premierinn.searchresults.SearchResultsViewEvent.HotelSelectedIdleEvent
import com.whitbread.premierinn.searchresults.SearchResultsViewEvent.OpenHotelInWebViewEvent
import com.whitbread.premierinn.searchresults.SearchResultsViewEvent.RequestAvailabilityEvent
import com.whitbread.premierinn.searchresults.SearchResultsViewEvent.RequestBannerGoBackEvent
import com.whitbread.premierinn.searchresults.SearchResultsViewEvent.RequestCloseSelfEvent
import com.whitbread.premierinn.searchresults.SearchResultsViewEvent.RequestNewAvailabilityEvent
import com.whitbread.premierinn.searchresults.SearchResultsViewEvent.ResetCurrentStateRenderEvent
import com.whitbread.premierinn.searchresults.SearchResultsViewEvent.ResetFallbackEvent
import com.whitbread.premierinn.searchresults.SearchResultsViewEvent.ResetOpenHDPActivityEvent
import com.whitbread.premierinn.searchresults.SearchResultsViewEvent.ScreenFirstLaunchEvent
import com.whitbread.premierinn.searchresults.SearchResultsViewEvent.ScreenResumedEvent
import com.whitbread.premierinn.searchresults.SearchResultsViewEvent.SearchAreaEvent
import com.whitbread.premierinn.searchresults.SearchResultsViewEvent.ShowFallbackOrHDPEvent
import com.whitbread.premierinn.searchresults.SearchResultsViewState.Mode.FULL_MAP
import com.whitbread.premierinn.searchresults.SearchResultsViewState.Mode.HOTEL_SELECTED
import com.whitbread.premierinn.searchresults.SearchResultsViewState.Mode.LIST_AND_MAP
import com.whitbread.premierinn.searchresults.SearchResultsViewState.Mode.LIST_EXPANDED
import com.whitbread.premierinn.searchresults.adapter.ITEM_SELECTED
import com.whitbread.premierinn.searchresults.adapter.ITEM_UNSELECTED
import com.whitbread.premierinn.searchresults.adapter.SearchResultsListAdapter
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.Locale

const val EXTRA_SRP_INPUT = "EXTRA_SRP_INPUT"
private const val HOTEL_CODE = "HOTEL_CODE"
private const val LAUNCHED_BY_LANDING_ACTIVITY = "LAUNCHED_BY_LANDING_ACTIVITY"
const val PLACE_ID = "PLACE_ID"
private const val COUNTRY = "COUNTRY"
private const val LANGUAGE = "LANGUAGE"


@JvmOverloads
fun Context.createSearchResultIntent(input: SearchResultsInput,
                                     flag: Int? = null,
                                     hotelCode: String? = null,
                                     launchedByLandingActivity: Boolean = false,
                                     placeId: String? = EMPTY_STRING_DOMAIN,
                                     country: String,
                                     language: String): Intent {
    return Intent(this, SearchResultsActivity::class.java).apply {
        input.apply { putExtra(EXTRA_SRP_INPUT, input) }
        flag?.apply { addFlags(this) }
        putExtra(HOTEL_CODE, hotelCode)
        putExtra(LAUNCHED_BY_LANDING_ACTIVITY, launchedByLandingActivity)
        putExtra(PLACE_ID, placeId)
        putExtra(COUNTRY, country)
        putExtra(LANGUAGE, language)
    }
}

@AndroidEntryPoint
class SearchResultsActivity : BaseActivity<ActivitySearchResultsMapListBinding>(), ReactiveView<SearchResultsViewState> {

    private val viewModel: SearchResultsViewModel by viewModels()
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    private val activityContext by lazy { this@SearchResultsActivity }
    private val appContext by lazy { activityContext.applicationContext }
    private val searchResultsInput by argument<SearchResultsInput>(EXTRA_SRP_INPUT)
    private val hotelCode by lazy { intent.extras?.getString(HOTEL_CODE) }
    private val launchedByLandingActivity by argument<Boolean>(LAUNCHED_BY_LANDING_ACTIVITY)
    private val placeId by lazy { intent.getStringExtra(PLACE_ID) ?: StringUtils.EMPTY_STRING }
    private val country by lazy { intent.getStringExtra(COUNTRY) ?: StringUtils.EMPTY_STRING }
    private val language by lazy { intent.getStringExtra(LANGUAGE) ?: StringUtils.EMPTY_STRING }

    private val uiEvents = PublishRelay.create<SearchResultsViewEvent>()
    private var googleMap: GoogleMap? = null
    private val autoCompositeDisposable by lazy { AutoCompositeDisposable(lifecycle) }
    private lateinit var hotelAvailabilityRequest: HotelAvailabilitiesRequestBody

    private val verticalLayoutManager = LinearLayoutManager(this)
    private val pagerSnapHelper = PagerSnapHelper()
    private val spaceVertical by lazy { DividerItemDecoration(this, LinearLayout.VERTICAL) }
    private val spaceHorizontal by lazy { DividerItemDecoration(this, LinearLayout.HORIZONTAL) }
    private val crashlyticsLogger = LogService()

    override fun inflateBinding(inflater: LayoutInflater): ActivitySearchResultsMapListBinding {
        return ActivitySearchResultsMapListBinding.inflate(inflater)
    }

    override fun getToolbar(): Toolbar? {
        return binding.toolbar
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        hotelAvailabilityRequest = searchResultsInput.toHotelAvailabilitiesRequest(
            DISTANCE,
            placeId,
            country,
            language,
            viewModel.getListOfRatePlanCodes(),
            viewModel.getOperaCompanyId()
        )
        initUiComponents()
        mapReadyObservable(mapFragment)
            .doOnNext {
                viewModel.viewStates()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::render) { error ->
                        crashlyticsLogger.logException(error, "SearchResultsViewState rendering failed")
                    }
                    .addTo(autoCompositeDisposable)
            }
            .map {
                ScreenFirstLaunchEvent(params = searchResultsInput
                    .toHotelAvailabilitiesRequest(
                        DISTANCE,
                        placeId,
                        country,
                        language,
                        viewModel.getListOfRatePlanCodes(),
                        viewModel.getOperaCompanyId()),
                    searchResultsInput, hotelCode)
            }
            .subscribe { uiEvents.accept(it) }
            .addTo(autoCompositeDisposable)


        viewModel.bind(uiEvents)
    }

    override fun onResume() {
        super.onResume()
        uiEvents.accept(ScreenResumedEvent)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!isFinishing) {
            uiEvents.accept(ResetCurrentStateRenderEvent)
        }
        googleMap = null
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        uiEvents.accept(RequestCloseSelfEvent(System.currentTimeMillis()))
        super.onBackPressed()
    }

    override fun render(state: SearchResultsViewState) {
        if (state.viewCloses) {
            super.onBackPressed()
            return
        }

        showFirstTimeIncentiveBanner(state.showPromoFooterBanner, state.promoContent, state.promoCode)

        if (state.applyToolbarChanges) {
            setToolbar(state.toolbarTitle!!, true, state.toolbarSubTitle!!)
        }

        if (state.applyChangesOnRecyclerView) {
            setRecyclerViewLayoutManager(state.isLayoutManagerVertical, state.withHideAnimationInHorizontalList)
        }

        if (state.applyMapBoundsChange) {
            val bottom = state.mode.toMapPadding(appContext.resources, binding.rootLayout.height.seventyPercent())
            googleMap?.setPadding(0, binding.toolbar.height, 0, bottom)
        }

        if (state.applyNewMapCenter) {
            val location = state.searchedLocation ?: state.mapCenter
            googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), 10.0f), 300, null)
        }

        if (state.applyMapPinsChange) {
            googleMap?.clear()
            arrayMarkerMap.clear()

            val mapBounds = LatLngBounds.Builder()

            state.mapViewItems.forEach {
                val markerOnMap = googleMap!!.addMarker(MapUtils.createHotelPinMarkerOptionsWithText(applicationContext, it))
                markerOnMap?.tag = it.id()
                arrayMarkerMap[it.id()] = markerOnMap
                mapBounds.include(LatLng(it.location.latitude, it.location.longitude))
            }

            if (state.mapViewItems.isEmpty())
                mapBounds.include(LatLng(state.initialSearchedLocation!!.latitude, state.initialSearchedLocation!!.longitude))

            try {
                googleMap?.animateCamera(CameraUpdateFactory.newLatLngBounds(mapBounds.build(), 0))
            } catch (e: Exception) {
                crashlyticsLogger.logException(e.fillInStackTrace())
            }

            state.searchedLocation?.let {
                if (state.showSearchedLocationPin) {
                    googleMap?.addMarker(MapUtils.createPinMarkerOptionsWithText(applicationContext, LatLng(it.latitude, it.longitude),
                        state.searchedLocationName!!))
                }
            }
        }

        if (state.applyVerticalListItemChange) {
            hotelListAdapter.submitList(state.verticalListViewItems)
        }

        if (state.scrollToSelectedAfterRenderingHorizontalItems) {
            binding.hotelsRecyclerView.doOnNextLayout {
                binding.hotelsRecyclerView.scrollToPosition(hotelListAdapter.getAdapterPositionById(state.selectedItem!!.id()))
            }
            hotelListAdapter.submitList(state.mapViewItems)
            setMapPinSelectedState(selected = true, item = state.selectedItem!!)
        }

        if (state.scrollToSelectedHotel) {
            setMapPinSelectedState(selected = true, item = state.selectedItem!!)
            binding.hotelsRecyclerView.scrollToPosition(hotelListAdapter.getAdapterPositionById(state.selectedItem.id()))
        }

        if (state.scrollToSelectedHotelAnimated) {
            setMapPinSelectedState(selected = true, item = state.selectedItem!!)

            val adapterPosition = hotelListAdapter.getAdapterPositionById(state.selectedItem.id())
            smoothScroller.targetPosition = adapterPosition
            horizontalLayoutManager.startSmoothScroll(smoothScroller)
        }

        if (state.clearPreviousMapPin) {
            setMapPinSelectedState(selected = false, item = state.previouslySelectedItem!!)
        }

        if (state.listButtonShown) {
            binding.listBtnContainer.resetBtn.isVisible = !state.isMapCentered() || state.showInfoBanner
            binding.listBtnContainer.searchBtn.isVisible = !state.isMapCentered()
        }

        if (state.applyNewUiMode) {
            if (!state.listButtonShown && !state.sortingFiltersShown) {
                binding.listBtnContainer.root.hideOnTop()
                binding.mapBtnContainer.root.hideOnTop()
            } else {
                if (state.sortingFiltersShown) {
                    binding.listBtnContainer.root.hideOnTop()
                    binding.mapBtnContainer.root.showFromTop()
                } else {
                    binding.mapBtnContainer.root.hideOnTop()
                    binding.listBtnContainer.root.showFromTop()
                }
            }
            when (state.mode) {
                FULL_MAP -> bottomSheetBehavior.state = STATE_HIDDEN
                LIST_AND_MAP -> if (!state.withHideAnimationInHorizontalList) bottomSheetBehavior.state = STATE_COLLAPSED
                HOTEL_SELECTED ->
                    if (state.isMapLoading || state.isLoading)
                        bottomSheetBehavior.state = STATE_HIDDEN
                    else
                        bottomSheetBehavior.state = STATE_COLLAPSED
                else -> { //void
                }
            }
        }

        if (state.applyListItemSelectionChange) {
            val pos = pagerSnapHelper.getSnapPosition(binding.hotelsRecyclerView)
            hotelListAdapter.notifyItemChanged(pos, ITEM_SELECTED)
            state.previouslySelectedItem?.run {
                hotelListAdapter.notifyItemChanged(hotelListAdapter.getAdapterPositionById(id()), ITEM_UNSELECTED)
            }
            setMapPinSelectedState(selected = true, item = state.selectedItem!!)

            googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(state.selectedItem.location.latitude,
                state.selectedItem.location.longitude), googleMap?.cameraPosition?.zoom
                ?: 10f), 300, null)
        }

        if (state.showCoronavirusMessage) {
            binding.coronavirusSearchResultLayout.coronavirusTextDescription.text = state.coronaVirusMessage
            binding.coronavirusSearchResultLayout.root.visibility = View.VISIBLE
        } else {
            binding.coronavirusSearchResultLayout.root.visibility = View.GONE
        }

        binding.loadingContainer.root.isVisible = state.isMapLoading

        if (state.searchArea) {
            state.mapCenter.let { location ->
                uiEvents.accept(RequestNewAvailabilityEvent(hotelAvailabilityRequest,
                    searchResultsInput, Location(location.latitude, location.longitude))
                )
            }
        }

        if (state.showInfoBanner || (state.showMapNoHotelError && state.mode == FULL_MAP)) {
            showInfoBanner(state.showInfoBanner, state.showMapNoHotelError, state)
        } else {
            binding.infoBannerContainer.root.isVisible = false
        }

        if (state.openHDPActivity) {
            val isInMapView = (bottomSheetBehavior.state == STATE_COLLAPSED)
            uiEvents.accept(ResetOpenHDPActivityEvent(false))
            startActivity(HotelDetailsActivity.createIntent(this@SearchResultsActivity,
                HotelDetailsInput.fromSearchResults(state.selectedItem, searchResultsInput, isInMapView).build()))
        }

        if (state.showFallbackPopup) {
            uiEvents.accept(ResetFallbackEvent(false))
            showFallbackPopup(state.fallbackPopupInfo!!)
        }

        if (state.openHotelInWebViewEvent) {
            //TODO: Opera Logically this seems like event is triggered twice i.e. on line 407 as well as here
            // Think of ways to improve this
            uiEvents.accept(OpenHotelInWebViewEvent)
            openHotelInWebView()
        }
    }

    private fun showFallbackPopup(fallbackPopupInfo: OperaFallbackPopupInfoDomain) {
        AlertDialog.Builder(this, R.style.PurpleDialog)
            .setTitle((fallbackPopupInfo.operaFallbackAlertTitleDomain))
            .setMessage(fallbackPopupInfo.operaFallbackAlertMessageDomain)
            .setNegativeButton(fallbackPopupInfo.operaFallbackAlertCloseDomain)
            { _, _ -> uiEvents.accept(ResetFallbackEvent(false))}
            .setPositiveButton(fallbackPopupInfo.operaFallbackAlertContinueDomain)
            { _, _ -> uiEvents.accept(OpenHotelInWebViewEvent) }
            .create()
            .show()
    }

    private fun openHotelInWebView() {
        startActivity(IntentUtils.createWebLinkIntent(getString(R.string.fallback_hotel_web_url)))
    }

    private fun showFirstTimeIncentiveBanner(
        isVisible: Boolean,
        promoContentDomain: PromoContentDomain?,
        promoCode: String
    ) {
        if (isVisible) {
            binding.firstTimeIncentiveBanner.visibility = View.VISIBLE
            binding.firstTimeOfferTitle.text = promoContentDomain?.srpBanner?.title
            binding.firstTimeOfferSubtitle.text = setSubtitleText(promoContentDomain, promoCode)
            binding.firstTimeOfferSubtitle.movementMethod = LinkMovementMethod.getInstance()
        } else {
            binding.firstTimeIncentiveBanner.visibility = View.GONE
        }
    }

    private fun setSubtitleText(promoContentDomain: PromoContentDomain?, promoCode: String): SpannableString {
        val subTitle = promoContentDomain?.srpBanner?.subTitle ?: EMPTY_STRING_DOMAIN
        val date = promoContentDomain?.srpBanner?.date ?: EMPTY_STRING_DOMAIN
        val termsWord = promoContentDomain?.srpBanner?.terms?.text ?: EMPTY_STRING_DOMAIN

        val fullText = try {
            // Check if subTitle contains any format specifiers
            if (subTitle.contains("%")) {
                String.format(Locale.UK, subTitle, date, termsWord)
            } else {
                subTitle
            }
        } catch (e: Exception) {
            subTitle
        }
        val spannable = SpannableString(fullText)

        val startBold = fullText.indexOf(date)
        if (startBold >= 0 && date.isNotEmpty()) {
            spannable.setSpan(
                StyleSpan(Typeface.BOLD),
                startBold,
                startBold + date.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        // Make the whole text clickable instead of just termsWord
        spannable.setSpan(
            object : ClickableSpan() {
                override fun onClick(widget: View) {
                    openIncentiveTermsAndConditions(
                        this@SearchResultsActivity,
                        promoContentDomain?.srpBanner?.terms?.url ?: EMPTY_STRING_DOMAIN,
                        promoCode
                    )
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = false
                    ds.color = Color.WHITE
                }
            },
            0,
            fullText.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return spannable
    }

    private fun showInfoBanner(showBanner: Boolean, noInitialHotelsAvailable: Boolean, state: SearchResultsViewState) {
        binding.infoBannerContainer.root.isVisible = true

        when {
            showBanner -> {
                binding.infoBannerContainer.informationTitle.text = getString(R.string.information_banner_no_hotels_found_title)
                binding.infoBannerContainer.informationText.text =  getString(R.string.information_banner_no_hotels_found_area_txt)
                binding.infoBannerContainer.informationBannerActionButton.isVisible = !state.previousSuccessfulListViewItems.isNullOrEmpty()
                binding.infoBannerContainer.informationText.isVisible = true
                binding.infoBannerContainer.informationBannerActionButton.text = getString(R.string.information_banner_go_back)
                binding.infoBannerContainer.informationBannerActionButton.setOnClickListener {
                    uiEvents.accept(RequestBannerGoBackEvent(null))
                }

                state.previousSuccessfulSearchedLocation?.let { location ->
                }
            }
            noInitialHotelsAvailable -> {
                binding.infoBannerContainer.informationTitle.setTextAppearance(this.appContext, R.style.Body)
                binding.infoBannerContainer.informationTitle.setTextColor(resources.getColor(R.color.white))
                binding.infoBannerContainer.informationTitle.text = getString(R.string.search_results_no_hotel_available)
                binding.infoBannerContainer.informationText.isVisible = false
            }
        }

        binding.infoBannerContainer.notificationCloseButton.setOnClickListener {
            uiEvents.accept(DismissInformationBannerEvent(null))
        }

    }

    private fun setMapPinSelectedState(item: HotelListItem, selected: Boolean) {
        val pinDrawable = if (selected) R.drawable.ic_hotel_map_pin_selected else item.toMapDrawableRes()
        arrayMarkerMap[item.id()]?.setIcon(MapUtils.createHotelBitmapDescriptorWithText(appContext, pinDrawable, item.toMapPinText()))
    }

    private fun initUiComponents() {
        binding.appBar.bringToFront()

        mapFragment = supportFragmentManager.findFragmentById(R.id.search_results_map) as SupportMapFragment

        bottomSheetBehavior = from(binding.hotelsRecyclerViewParent)
        bottomSheetBehavior.isHideable = true
        binding.hotelsRecyclerView.adapter = hotelListAdapter

        binding.listBtnContainer.resetBtn.clicks()
            .map {
                hotelAvailabilityRequest =
                    searchResultsInput.toHotelAvailabilitiesRequest(
                        DISTANCE,
                        placeId,
                        country,
                        language,
                        viewModel.getListOfRatePlanCodes(),
                        viewModel.getOperaCompanyId()
                    )
            }.map {
                ScreenFirstLaunchEvent(
                    searchResultsInput.toHotelAvailabilitiesRequest(
                        DISTANCE,
                        placeId,
                        country,
                        language,
                        viewModel.getListOfRatePlanCodes(),
                        viewModel.getOperaCompanyId()
                    ), searchResultsInput, hotelCode
                )
            }.subscribe(uiEvents::accept).addTo(autoCompositeDisposable)
        binding.listBtnContainer.searchBtn.clicks().map { SearchAreaEvent }.subscribe(uiEvents::accept).addTo(autoCompositeDisposable)
        binding.listBtnContainer.listBtn.clicks().map { ChangeModeStateEvent(LIST_AND_MAP) }.subscribe(uiEvents::accept).addTo(autoCompositeDisposable)
        binding.mapBtnContainer.mapBtn.clicks().map { ChangeModeStateEvent(FULL_MAP) }.subscribe(uiEvents::accept).addTo(autoCompositeDisposable)
        binding.coronavirusSearchResultLayout.coronavirusDismissButton.clicks().map { DismissCoronavirusEvent }.subscribe(uiEvents::accept).addTo(autoCompositeDisposable)
        binding.mapBtnContainer.availabilitySortToggle.click.map {
            hotelAvailabilityRequest = if (it == ToggleButtonView.State.LEFT) {
                hotelAvailabilityRequest.copy(sort = DISTANCE.name)
            } else {
                hotelAvailabilityRequest.copy(sort = PRICE.name)
            }
            RequestAvailabilityEvent(hotelAvailabilityRequest, searchResultsInput)
        }.subscribe(uiEvents::accept).addTo(autoCompositeDisposable)
    }

    private fun mapReadyObservable(fragment: SupportMapFragment): Observable<Boolean> = Observable.create<Boolean> {
        fragment.getMapAsync { gMap ->
            googleMap = gMap
            setUpMap()
            it.onNext(true)
        }
    }

    private val simpleScrollListItem = object : OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            if (dx == 0 && dy == 0 && pagerSnapHelper.findSnapView(recyclerView.layoutManager) is ConstraintLayout) {
                val pos = pagerSnapHelper.getSnapPosition(recyclerView)
                if (pos != RecyclerView.NO_POSITION) {
                    val hotelListItem = hotelListAdapter.getListItem(pos) as HotelListItem
                    uiEvents.accept(HotelSelectedIdleEvent(hotelListItem))
                }
            }
            super.onScrolled(recyclerView, dx, dy)
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (SCROLL_STATE_IDLE == newState) {
                val pos = pagerSnapHelper.getSnapPosition(recyclerView)
                if (pos != RecyclerView.NO_POSITION) {
                    val hotelListItem = hotelListAdapter.getListItem(pos) as HotelListItem
                    uiEvents.accept(HotelSelectedIdleEvent(hotelListItem))
                }
            }
        }
    }
    private val horizontalLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    private val listBottomSheetCallback = object : BottomSheetSimpleCallback() {
        override fun onStateChanged(view: View, newState: Int) {
            if (newState == STATE_EXPANDED) {
                uiEvents.accept(ChangeModeStateEvent(LIST_EXPANDED))
            }
            if (newState == STATE_HIDDEN) {
                uiEvents.accept(ChangeModeStateEvent(FULL_MAP))
            }
            if (newState == STATE_COLLAPSED) { // half visible
                uiEvents.accept(ChangeModeStateEvent(LIST_AND_MAP))
            }
        }
    }
    private val arrayMarkerMap = ArrayMap<String, Marker>()

    private val smoothScroller: RecyclerView.SmoothScroller by lazy {
        object : LinearSmoothScroller(this) {
            override fun getVerticalSnapPreference(): Int {
                return LinearSmoothScroller.SNAP_TO_START
            }
        }
    }

    private val onClickListener: ((Int) -> Unit) = {
        val listItem = hotelListAdapter.getListItem(it) as HotelListItem
        uiEvents.accept(ShowFallbackOrHDPEvent(listItem))
    }

    private val onEditClickListener: (() -> Unit) = {
        if (launchedByLandingActivity) {
            super.onBackPressed()
        } else {
            val searchItem = SearchItemInput.builder()
                .searchText(searchResultsInput.placeName())
                .location(
                    Coordinates.create(
                        searchResultsInput.latitude(),
                        searchResultsInput.longitude()
                    )
                ).build()
            startActivity(
                LandingActivityIntent.create(
                    context = this,
                    bundle = LandingInputModel(
                        searchItem = searchItem,
                        searchResultsInput = searchResultsInput
                    )
                )
            )
        }
    }

    private val hotelListAdapter by lazy {
        SearchResultsListAdapter(listener = onClickListener, editListener = onEditClickListener)
    }

    private fun setRecyclerViewLayoutManager(isVertical: Boolean, hideHorizontalListAnimated: Boolean) {
        val layoutManager = if (isVertical) verticalLayoutManager else horizontalLayoutManager
        val horizontalOrientation = layoutManager.orientation == LinearLayoutManager.HORIZONTAL
        binding.hotelsRecyclerView.layoutManager = layoutManager
        if (horizontalOrientation) {

            binding.hotelsRecyclerView.background = ContextCompat.getDrawable(appContext, android.R.color.transparent)
            binding.hotelsRecyclerView.minimumHeight = resources.getDimensionPixelSize(R.dimen.search_results_horizontal_item_height) + (resources.getDimensionPixelSize(R.dimen.default_medium_margin) * 2)

            spaceHorizontal.setDrawable(ContextCompat.getDrawable(this, R.drawable.shape_divider_transparent)!!)
            if (binding.hotelsRecyclerView.itemDecorationCount > 0) {
                binding.hotelsRecyclerView.removeItemDecorationAt(0)
            }
            binding.hotelsRecyclerView.addItemDecoration(spaceHorizontal)

            pagerSnapHelper.attachToRecyclerView(binding.hotelsRecyclerView)

            binding.hotelsRecyclerView.addOnScrollListener(simpleScrollListItem)

            bottomSheetBehavior.peekHeight = resources.getDimensionPixelSize(R.dimen.search_results_horizontal_item_height) + (resources.getDimensionPixelSize(R.dimen.default_medium_margin) * 2)
            bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetSimpleCallback() {
                override fun onStateChanged(view: View, newState: Int) {
                    if (newState == STATE_DRAGGING) {
                        bottomSheetBehavior.state = STATE_COLLAPSED
                    }
                }
            })
        } else {
            pagerSnapHelper.attachToRecyclerView(null)
            binding.hotelsRecyclerView.removeOnScrollListener(simpleScrollListItem)

            fun setUpVerticalRecyclerView() {
                binding.hotelsRecyclerView.minimumHeight = binding.rootLayout.height.seventyPercent()
                binding.hotelsRecyclerView.background = ContextCompat.getDrawable(appContext, R.color.white)

                spaceVertical.setDrawable(ContextCompat.getDrawable(this, R.drawable.shape_divider_transparent)!!)
                if (binding.hotelsRecyclerView.itemDecorationCount > 0) {
                    binding.hotelsRecyclerView.removeItemDecorationAt(0)
                }
                binding.hotelsRecyclerView.addItemDecoration(spaceVertical)

                bottomSheetBehavior.peekHeight = binding.rootLayout.height.seventyPercent()
            }

            if (hideHorizontalListAnimated) {
                bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetSimpleCallback() {
                    override fun onStateChanged(view: View, newState: Int) {
                        if (newState == STATE_HIDDEN) {
                            bottomSheetBehavior.setBottomSheetCallback(listBottomSheetCallback)
                            setUpVerticalRecyclerView()
                            bottomSheetBehavior.state = STATE_COLLAPSED
                        }
                    }
                })
                bottomSheetBehavior.state = STATE_HIDDEN
            } else {
                bottomSheetBehavior.setBottomSheetCallback(listBottomSheetCallback)
                setUpVerticalRecyclerView()
            }
        }
    }

    private fun setUpMap() {
        googleMap?.let {
            it.uiSettings?.isCompassEnabled = false
            it.uiSettings?.isMapToolbarEnabled = false
            it.uiSettings?.isIndoorLevelPickerEnabled = false
            it.uiSettings?.isRotateGesturesEnabled = false
            it.uiSettings?.isTiltGesturesEnabled = false
            it.isBuildingsEnabled = false
            it.isTrafficEnabled = false
            it.isIndoorEnabled = false

            it.setOnMarkerClickListener(GoogleMap.OnMarkerClickListener { marker ->
                marker.tag?.run { uiEvents.accept(ChangeModeStateEvent(HOTEL_SELECTED, marker.tag as String)) }
                return@OnMarkerClickListener true
            })
            it.setOnCameraIdleListener {
                uiEvents.accept(ChangeMapCentreEvent(Location(it.cameraPosition.target.latitude, it.cameraPosition.target.longitude)))
            }
            it.setOnMapClickListener { uiEvents.accept(ChangeModeStateEvent(FULL_MAP)) }
        }
    }

    private fun Int.seventyPercent(): Int = (this * 0.68).toInt()
}
