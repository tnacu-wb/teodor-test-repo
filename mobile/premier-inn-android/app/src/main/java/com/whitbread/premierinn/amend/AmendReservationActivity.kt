package com.whitbread.premierinn.amend

import android.animation.LayoutTransition
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import com.whitbread.premierinn.R
import com.whitbread.premierinn.amend.AmendReservationViewModel.AmendReservationEvent.AmendSummarySuccessEvent
import com.whitbread.premierinn.amend.AmendReservationViewModel.AmendReservationEvent.CancelBookingEvent
import com.whitbread.premierinn.amend.AmendReservationViewModel.AmendReservationEvent.GenericErrorEvent
import com.whitbread.premierinn.amend.AmendReservationViewModel.AmendReservationEvent.LaunchAmendError
import com.whitbread.premierinn.amend.AmendReservationViewModel.AmendReservationEvent.RemoveRoomAmendError
import com.whitbread.premierinn.amend.AmendReservationViewModel.AmendReservationEvent.RemoveRoomSuccessEvent
import com.whitbread.premierinn.amend.AmendReservationViewModel.AmendReservationEvent.ShowCancelBookingDialog
import com.whitbread.premierinn.amend.AmendReservationViewModel.AmendReservationEvent.ShowRemoveBookingDialog
import com.whitbread.premierinn.amend.AmendReservationViewModel.AmendReservationEvent.StartAmendSummaryCall
import com.whitbread.premierinn.amend.amendcalendar.AmendCalendarActivity
import com.whitbread.premierinn.amend.amendcalendar.AmendCalendarActivity.Companion.CALENDAR_SELECTED_ARRIVAL
import com.whitbread.premierinn.amend.amendguestsrooms.AmendAddRoomActivity
import com.whitbread.premierinn.amend.amendguestsrooms.AmendGuestsRoomsActivity
import com.whitbread.premierinn.amend.amendreview.ReviewAmendsActivity
import com.whitbread.premierinn.amend.amendupsells.AmendUpsellsActivity
import com.whitbread.premierinn.amend.amendupsells.AmendUpsellsInput
import com.whitbread.premierinn.common.AutoCompositeDisposable
import com.whitbread.premierinn.common.activity.BaseActivity
import com.whitbread.premierinn.common.addTo
import com.whitbread.premierinn.common.managebooking.ManageBookingInput
import com.whitbread.premierinn.common.service.LogService
import com.whitbread.premierinn.common.utils.ColorFilterUtil
import com.whitbread.premierinn.common.utils.StringUtils
import com.whitbread.premierinn.common.utils.formattedNumberOfNights
import com.whitbread.premierinn.databinding.ActivityAmendReservationBinding
import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import com.whitbread.premierinn.domain.common.NO_AMOUNT
import com.whitbread.premierinn.domain.common.PriceDomain
import com.whitbread.premierinn.domain.common.UpsellAvailable
import com.whitbread.premierinn.domain.common.nightsCount
import com.whitbread.premierinn.domain.graphql.amend.entity.AmendSummaryDomain
import com.whitbread.premierinn.summary.models.ParcelableExtrasItem
import dagger.hilt.android.AndroidEntryPoint
import org.threeten.bp.LocalDate


const val EXTRA_AMEND_INPUT = "amend_reservation_input"
const val UPSELLS_EXTRAS_LIST = "upsells_extras_list"
const val EXTRA_AMEND_ERROR = "amend_error"
const val REVIEW_AMENDS_REQUEST_RESULT = 4
const val AMEND_DATES_REQUEST_RESULT = 5
const val AMEND_UPSELLS_REQUEST_RESULT = 6
const val AMEND_GUEST_RESULT = 7
const val AMEND_UPSELLS_REQUEST_RESTAURANT_CLOSED_RESULT = 8
const val TOKEN = "token"
const val HOTEL_BRAND = "hotel_brand"
const val HOTEL_NAME = "hotel_name"
const val IS_ECI_LCO_BOOKING = "is_eci_lco_booking"
const val UUID_BASKET_REFERENCE = "uuid_basket_reference"
const val IS_BUSINESS_BOOKING = "is_business_booking"
const val PROMOTIONS_INFORMATION = "promotions_information"
const val BOOKING_FLOW_ID = "booking_flow_id"

@AndroidEntryPoint
class AmendReservationActivity : BaseActivity<ActivityAmendReservationBinding>() {

    private val disposable: AutoCompositeDisposable by lazy { AutoCompositeDisposable(lifecycle) }
    private val crashlyticsLogger = LogService()
    private val input by lazy { requireNotNull(intent.getParcelableExtra<ManageBookingInput>(EXTRA_AMEND_INPUT)) }
    private val token by lazy { requireNotNull(intent.getStringExtra(TOKEN)) }
    private val listOfEciLcoExtras by lazy { intent.getParcelableArrayListExtra<ParcelableExtrasItem>(UPSELLS_EXTRAS_LIST) }
    private val promoInformation by lazy { intent.getParcelableExtra<ParcelablePromotionsInformationDomain>(PROMOTIONS_INFORMATION) }
    private val viewModel: AmendReservationViewModel by viewModels()
    private var originalBookingTotalCost: PriceDomain? = null
    private var originalRoomTotalCost: Float? = null
    private var totalUpsellPrice: Float = NO_AMOUNT
    private var amendedTotalCost: Float = NO_AMOUNT
    private var isDateRestricted = false
    private var isUpsellsRestricted = false
    lateinit var errorMessage: String
    private var upsellsAvailable: List<UpsellAvailable>? = null
    private var isEciLcoBooking = false
    private var isPromoBooking = false
    private var uuidBasketReference = EMPTY_STRING_DOMAIN
    private var hotelBrand = EMPTY_STRING_DOMAIN
    private var hotelName = EMPTY_STRING_DOMAIN
    private var isBusinessBooking = false
    private var bookingFlowId = EMPTY_STRING_DOMAIN
    private var bookingHasUpsell = false

    override fun inflateBinding(inflater: LayoutInflater): ActivityAmendReservationBinding {
        return ActivityAmendReservationBinding.inflate(inflater)
    }

    override fun getToolbar(): Toolbar? {
        return binding.toolbar
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setToolbar(resources.getString(R.string.amend_reservation_title), true)
        ColorFilterUtil.setProgressColour(this, binding.progress, R.color.white)
        errorMessage = intent.getStringExtra(EXTRA_AMEND_ERROR) ?: StringUtils.EMPTY_STRING
        if (errorMessage.isNotBlank()) {
            updateErrorBanner(errorMessage)
        }

        isEciLcoBooking = intent.getBooleanExtra(IS_ECI_LCO_BOOKING, false)
        isBusinessBooking = intent.getBooleanExtra(IS_BUSINESS_BOOKING, false)
        uuidBasketReference = intent.getStringExtra(UUID_BASKET_REFERENCE)!!
        hotelBrand = intent.getStringExtra(HOTEL_BRAND)!!
        hotelName = intent.getStringExtra(HOTEL_NAME)!!
        bookingFlowId = intent.getStringExtra(BOOKING_FLOW_ID) ?: EMPTY_STRING_DOMAIN
        isPromoBooking = !promoInformation?.promoBookingInfo?.promotionCode.isNullOrEmpty()
        viewModel.setPromotionInformation(promoInformation)

        val listAdapter = AmendedRoomListAdapter(
                editBooking = ::editBookingOnClick,
                removeBooking = ::removeBookingOnClick)
        setRecyclerView(listAdapter)

        binding.cancelBtn.setOnClickListener { viewModel.onCancelClicked() }

        viewModel.events()
                .subscribe {
                    when (it) {
                        is GenericErrorEvent -> {
                            setTransparentLoadingSpinner(false)
                            it.error?.let { throwable -> crashlyticsLogger.logException(throwable.fillInStackTrace()) }
                            showToast(getString(R.string.generic_error_description))
                        }
                        is LaunchAmendError -> showLaunchAmendError()
                        is ShowCancelBookingDialog -> showCancelBookingDialog()
                        is CancelBookingEvent -> {
                            viewModel.refreshDashboard()
                            setResult(Activity.RESULT_OK)
                            finish()
                        }
                        is StartAmendSummaryCall -> {
                            viewModel.amendSummaryCall()
                        }
                        is AmendSummarySuccessEvent -> {
                                setTransparentLoadingSpinner(false)
                                displayReviewAmendsScreen(it.amendSummaryDomain)
                        }
                        is ShowRemoveBookingDialog -> showRemoveRoomDialog(it.roomId, it.position, it.button)
                        is RemoveRoomAmendError -> {
                            setTransparentLoadingSpinner(false)
                            showGenericErrorDialog(listAdapter, it.roomId)
                        }
                        is RemoveRoomSuccessEvent -> {
                            viewModel.makeBookingConfirmationCallAfterRemoveRoom(it.roomId)
                        }
                        is AmendReservationViewModel.AmendReservationEvent.HideCtaLoadingAndShowTranslucentSpinnerEvent -> {
                            setTransparentLoadingSpinner(true)
                        }
                        is AmendReservationViewModel.AmendReservationEvent.AmendSummaryAfterChangeDates -> {
                            setTransparentLoadingSpinner(true)
                            val priceDifference = it.amendSummaryDomain.totalCost - it.amendSummaryDomain.previousTotal
                            binding.priceDifferenceLayout.priceView.layoutTransition.enableTransitionType(LayoutTransition.APPEARING)
                            binding.priceDifferenceLayout.amendPriceDifferenceText.text = viewModel.createPriceDifference(
                                PriceDomain(priceDifference, originalBookingTotalCost!!.currency))
                            setTransparentLoadingSpinner(false)
                        }
                        is AmendReservationViewModel.AmendReservationEvent.SavingBookingConfirmAfterRemoveRoomSuccessEvent -> {
                            setTransparentLoadingSpinner(true)
                            viewModel.removeRoomOperaFromStorage(it.roomId, input.bookingReference())
                        }
                        is AmendReservationViewModel.AmendReservationEvent.SavingBookingConfirmAfterRemoveRoomFailureEvent -> {
                            setTransparentLoadingSpinner(false)
                            showToast(getString(R.string.generic_error_description))
                        }
                    }
                }.addTo(disposable)

        viewModel.states()
                .distinctUntilChanged()
                .subscribe { state ->
                    if (state.isLoading) {
                        setTransparentLoadingSpinner(true)
                    } else {
                        state.amendSummaryInProgress.let {
                            if (it) {
                                setTransparentLoadingSpinner(true)
                            } else {
                                setTransparentLoadingSpinner(false)
                            }
                        }
                    }
                    if (state.isPromotionalBooking) {
                        promoInformation?.appPromoAmendMessage?.let {
                            binding.promoAmendInfoMessage.isVisible = true
                            binding.promoAmendInfoMessage.setText(it)
                        }
                    }

                    binding.amendHotelName.text = state.hotelName
                    totalUpsellPrice = state.getUpsellPrice()
                    upsellsAvailable = state.getAvailableUpsells

                    state.reservationDates?.let { dates ->
                        viewModel.setArrivalAndDepartureDate(dates.first, dates.second)
                        val tempBasketRef = viewModel.getTempBasketRef()

                        binding.amendDatesLayout.amendDatesText.text = dates.formattedNumberOfNights(this)

                        listAdapter.numberOfNights(dates.nightsCount())

                        binding.amendDatesLayout.editDates.setOnClickListener {
                            originalRoomTotalCost?.let { totalCost ->
                                AmendCalendarActivity.createIntent(
                                    this,
                                    input,
                                    token,
                                    tempBasketRef,
                                    totalCost,
                                    originalBookingTotalCost!!.currency,
                                    dates.first,
                                    dates.second,
                                    isDateRestricted,
                                    isUpsellsRestricted,
                                    state.maxNights(),
                                    state.maxArrivalDate(),
                                    uuidBasketReference,
                                    hotelBrand,
                                    state.isPromotionalBooking,
                                ).also {
                                    startActivityForResult(it, AMEND_DATES_REQUEST_RESULT)
                                }
                            }
                        }
                    }

                    state.amendedRoomWithDetails.let {
                        binding.addRoomBtn.isVisible = state.addRoomAllowed() && !isBusinessBooking
                        bookingHasUpsell = it.any { room -> room.roomUpsells.isNotEmpty() }
                        state.addRoomEnabled()
                        listAdapter.numberOfNights(state.numberOfNights)
                        listAdapter.submitList(it)
                        displayMaxRoomWarningMessage(!binding.addRoomBtn.isVisible)
                    }

                    state.getOriginalReservation?.let {
                        when {
                            it -> {
                                viewModel.getOriginalReservation()
                            }
                        }
                    }

                    upsellsAvailable?.let {
                        binding.amendUpsellsLayout.upsellsChangedWarningMessage.isVisible = state.shouldShowWifiAndMealsWarning()
                        binding.amendUpsellsLayout.changeMealsAndExtras.setOnClickListener {
                            startActivityForResult(
                                AmendUpsellsActivity.newInstance(this,
                                    AmendUpsellsInput(
                                        manageBookingInput = input,
                                        temporaryBasketReference = viewModel.getTempBasketRef(),
                                        totalAdults = state.numberOfAdults,
                                        totalChildren = state.numberOfChildren,
                                        hotelBrand = hotelBrand,
                                        bookingFlowId = bookingFlowId,
                                        arrivalDeparturePair = viewModel.getArrivalDepartureDate(),
                                        hotelName = hotelName,
                                        galleryImages = state.galleryImages
                                    )
                                ),
                                AMEND_UPSELLS_REQUEST_RESULT)
                        }
                    }

                    binding.addRoomBtn.setOnClickListener {
                        AmendAddRoomActivity.createIntent(
                            context = this@AmendReservationActivity,
                            hotelBrand = hotelBrand,
                            selectedRatePlan = input.selectedRatePlan()!!,
                            tempBasketRef = viewModel.getTempBasketRef(),
                            token = token,
                            uuidBasketRef = uuidBasketReference,
                            roomIndex = listAdapter.itemCount,
                            addedRoomIndex = listAdapter.itemCount + 1,
                            isUpsellsRestricted = isUpsellsRestricted,
                            input = input,
                            hotelName = hotelName,
                            viewModel.getArrivalDepartureDate()
                        ).also {
                            startActivity(it)
                        }
                    }

                    binding.cancelBtn.isVisible = input.cancellable() == true
                    state.cancelBookingInProgress?.let {
                        binding.cancelBtn.setLoadingState(it)
                    }

                    state.originalBookingTotalCost?.let {
                        originalBookingTotalCost = it
                    }

                    originalBookingTotalCost?.let {
                        originalRoomTotalCost = it.amount - state.getUpsellPrice()
                    }

                    state.hasRoomBeenRemoved.let { isRemoved ->
                        if (isRemoved) {
                            setTransparentLoadingSpinner(false)
                        }
                    }

                    state.getAmendRestrictions?.let {
                        isDateRestricted = it.restricted && it.nights
                        isUpsellsRestricted = it.restricted && it.upsell
                    }

                    binding.amendUpsellsLayout.amendMealsAndExtraCard.visibility = when (isUpsellsRestricted) {
                        true -> View.GONE
                        else -> View.VISIBLE
                    }

                    state.getAmendedTotalCost?.let { totalCost ->
                        amendedTotalCost = totalCost
                        state.isAmended?.let { isAmended ->
                            setPriceView(isAmended, amendedTotalCost)
                        }
                    }

                    binding.priceDifferenceLayout.reviewAmendsBtn.setOnClickListener {
                        setTransparentLoadingSpinner(true)

                        if (state.isAncillariesCloseoutApplicable && bookingHasUpsell) {
                            startActivityForResult(
                                AmendUpsellsActivity.newInstance(this,
                                    AmendUpsellsInput(
                                        manageBookingInput = input,
                                        temporaryBasketReference = viewModel.getTempBasketRef(),
                                        totalAdults = state.numberOfAdults,
                                        totalChildren = state.numberOfChildren,
                                        hotelBrand = hotelBrand,
                                        bookingFlowId = bookingFlowId,
                                        arrivalDeparturePair = viewModel.getArrivalDepartureDate(),
                                        hotelName = hotelName,
                                        galleryImages = state.galleryImages
                                    )
                                ),
                                AMEND_UPSELLS_REQUEST_RESTAURANT_CLOSED_RESULT)
                        } else {
                            viewModel.amendSummaryCall()
                        }
                    }
                }
                .addTo(disposable)
    }

    private fun updateErrorBanner(message: String?) {
        if (message == null) {
            binding.amendEditErrorBanner.isVisible = false
        } else if (!binding.amendEditErrorBanner.isVisible || binding.amendErrorMessage.text != message) {
            binding.amendErrorMessage.text = message
            binding.amendEditErrorBanner.isVisible = true
        }
    }

    private fun editBookingOnClick(amendedRoom: AmendReservationState.AmendedRoom) {
        AmendGuestsRoomsActivity.createIntent(context = this@AmendReservationActivity,
                roomIndex = amendedRoom.position,
                roomId = amendedRoom.roomId,
                input = input,
                hotelBrand = hotelBrand,
                selectedRatePlan = input.selectedRatePlan()!!,
                isUpsellsRestricted = isUpsellsRestricted,
                tempBasketRef = viewModel.getTempBasketRef(),
                token = token,
                uuidBasketReference = uuidBasketReference,
                isPromoBooking = isPromoBooking).also {
            startActivityForResult(it, AMEND_GUEST_RESULT)
        }
    }

    private fun removeBookingOnClick(item: AmendReservationState.AmendedRoom, position: Int,
                                     removeButton: TextView) {
        viewModel.onRemoveClicked(item.roomId, position, removeButton)
    }

    private fun setRecyclerView(listAdapter: AmendedRoomListAdapter) {
        binding.roomList.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL).apply {
            setDrawable(ContextCompat.getDrawable(this@AmendReservationActivity, R.drawable.shape_divider_transparent)!!)
        })
        binding.roomList.adapter = listAdapter
    }

    private fun showCancelBookingDialog() {
        AlertDialog.Builder(this, R.style.PurpleDialog)
            .setTitle(getString(R.string.dialog_cancel_booking_title))
            .setMessage(getString(R.string.dialog_cancel_booking_message))
            .setNegativeButton(getString(R.string.dialog_cancel_negative_button))
            { _, _ -> }
            .setPositiveButton(getText(R.string.dialog_cancel_positive_button))
            { _, _ -> viewModel.onCancelBookingConfirmed() }
            .create()
            .show()
    }

    private fun displayReviewAmendsScreen(amendSummaryDomain: AmendSummaryDomain) {
        ReviewAmendsActivity.createIntent(
            this,
            input,
            isEciLcoBooking,
            amendSummaryDomain,
            viewModel.getTempBasketRef(),
            uuidBasketReference,
            token,
            listOfEciLcoExtras)
        .also {
            startActivityForResult(it, REVIEW_AMENDS_REQUEST_RESULT)
        }
    }

    private fun showRemoveRoomDialog(roomId: String, position: Int, removeButton: TextView) {
        AlertDialog.Builder(this, R.style.PurpleDialog)
                .setTitle(getString(R.string.amend_remove_room_dialog_title, position))
                .setMessage(getString(R.string.amend_remove_room_dialog_message, position))
                .setNegativeButton(getString(R.string.amend_dialog_remove_negative_button))
                { _, _ -> }
                .setPositiveButton(getString(R.string.amend_dialog_remove_positive_button))
                { _, _ ->
                    removeButton.setOnClickListener {
                        !removeButton.isEnabled
                        !removeButton.isClickable
                        !removeButton.isFocusable
                    }
                    viewModel.onRemoveRoomConfirmed(roomId)
                    setTransparentLoadingSpinner(true)
                }
                .create().show()
    }

    private fun showLaunchAmendError() {
        AlertDialog.Builder(this, R.style.PurpleDialog)
            .setTitle(getString(R.string.amend_error_title))
            .setMessage(getString(R.string.amend_error_message))
            .setPositiveButton(getText(android.R.string.ok))
            { _, _ ->
                finish()
            }
            .setCancelable(false)
            .create()
            .show()
    }

    private fun showGenericErrorDialog(listAdapter: AmendedRoomListAdapter, roomId: String) {
        AlertDialog.Builder(this, R.style.PurpleDialog)
            .setTitle(getString(R.string.amend_error_title))
            .setMessage(getString(R.string.amend_error_message))
            .setPositiveButton(getText(android.R.string.ok))
            { _, _ ->
                val currentlySelectedRemoveButton = listAdapter.getCurrentlySelectedRemoveTextView()
                val currentlySelectedRemovePosition = listAdapter.getCurrentlySelectedRemoveTextViewPosition()

                currentlySelectedRemoveButton.setOnClickListener {
                    currentlySelectedRemoveButton.isEnabled = true
                    currentlySelectedRemoveButton.isClickable = true
                    currentlySelectedRemoveButton.isFocusable = true
                    showRemoveRoomDialog(roomId, currentlySelectedRemovePosition, currentlySelectedRemoveButton)
                }
            }
            .setCancelable(false)
            .create()
            .show()
    }

    private fun setPriceView(isAmended: Boolean, amendedTotalCost: Float) {
        if (originalBookingTotalCost == null) {
            return
        }

        binding.priceDifferenceLayout.priceView.visibility = if (isAmended) View.VISIBLE else View.GONE
        binding.amendReservationSpaceView.visibility = if (isAmended) View.VISIBLE else View.GONE

        when (isAmended) {
            true -> {
                val totalCost = priceDifference(amendedTotalCost, originalBookingTotalCost!!)
                binding.priceDifferenceLayout.priceView.layoutTransition.enableTransitionType(LayoutTransition.APPEARING)
                binding.priceDifferenceLayout.amendPriceDifferenceText.text = viewModel.createPriceDifference(totalCost)
            }
            false -> {
                binding.priceDifferenceLayout.priceView.layoutTransition.enableTransitionType(LayoutTransition.DISAPPEARING)
            }
        }
    }

    private fun setTransparentLoadingSpinner(show: Boolean) {
        binding.translucentLoadingAmend.translucentConstraintLayout.isVisible = show
        binding.translucentLoadingAmend.translucentConstraintLayout.isClickable = show

        binding.translucentLoadingAmend.translucentConstraintLayout.visibility = if (show) View.VISIBLE else View.GONE
        binding.translucentLoadingAmend.loadingSpinnerInfoText.visibility = if (show) View.VISIBLE else View.GONE
        binding.translucentLoadingAmend.loadingSpinnerInfoText.text = getString(R.string.loading_spinner_loading_text)

        binding.translucentLoadingAmend.translucentConstraintLayout.setBackgroundColor(
            this.getResources().getColor(R.color.base_black_70, null)
        )
        binding.translucentLoadingAmend.spinnerBackground.background =
            null
        binding.translucentLoadingAmend.loadingSpinner.setPadding(
            0,
            0,
            0,
            0
        )
        binding.translucentLoadingAmend.loadingSpinner.indeterminateDrawable.mutate()
            .setColorFilter(
                this.getResources().getColor(R.color.white, null),
                PorterDuff.Mode.MULTIPLY
            )
    }

    private fun displayMaxRoomWarningMessage(shouldDisplayMessage: Boolean) {
       binding.maxRoomWarningMessage.visibility = if (shouldDisplayMessage && !isPromoBooking) View.VISIBLE else View.GONE
    }

    companion object {
        @JvmStatic
        fun createIntent(
            context: Context,
            input: ManageBookingInput? = null,
            errorMessage: String? = null,
            uuidBasketReference: String,
            token: String,
            hotelBrand: String,
            hotelName: String,
            isEciLcoBooking: Boolean,
            listOfExtras: ArrayList<ParcelableExtrasItem>,
            isBusinessBooking: Boolean,
            promotionInformation: ParcelablePromotionsInformationDomain,
            bookingFlowId: String
        ): Intent {
            return Intent(context, AmendReservationActivity::class.java).apply {
                putExtra(EXTRA_AMEND_INPUT, input)
                putParcelableArrayListExtra(UPSELLS_EXTRAS_LIST, listOfExtras)
                putExtra(EXTRA_AMEND_ERROR, errorMessage)
                putExtra(IS_ECI_LCO_BOOKING, isEciLcoBooking)
                putExtra(UUID_BASKET_REFERENCE, uuidBasketReference)
                putExtra(TOKEN, token)
                putExtra(HOTEL_BRAND, hotelBrand)
                putExtra(HOTEL_NAME, hotelName)
                putExtra(IS_BUSINESS_BOOKING, isBusinessBooking)
                putExtra(PROMOTIONS_INFORMATION, promotionInformation)
                putExtra(BOOKING_FLOW_ID, bookingFlowId)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        when (requestCode) {
            REVIEW_AMENDS_REQUEST_RESULT -> {
                if (resultCode == RESULT_OK) {
                    finish()
                }
            }
            AMEND_DATES_REQUEST_RESULT -> {
                if (resultCode == Activity.RESULT_OK) {
                    setTransparentLoadingSpinner(true)
                    intent?.let {
                        val arrival =
                            intent.getSerializableExtra(CALENDAR_SELECTED_ARRIVAL) as LocalDate
                        val departure =
                            intent.getSerializableExtra(CALENDAR_SELECTED_ARRIVAL) as LocalDate
                        viewModel.bookingConfPackagesAndAmendSummary(arrival, departure)
                    }
                }
            }

            AMEND_UPSELLS_REQUEST_RESULT -> {
                if (resultCode == RESULT_OK) {
                    setTransparentLoadingSpinner(true)
                        val arrival = viewModel.getArrivalDepartureDate().first
                        val departure = viewModel.getArrivalDepartureDate().second
                        viewModel.bookingConfPackagesAndAmendSummary(arrival, departure)
                }
            }

            AMEND_UPSELLS_REQUEST_RESTAURANT_CLOSED_RESULT -> {
                if (resultCode == RESULT_OK) {
                    setTransparentLoadingSpinner(true)
                    viewModel.amendSummaryCall()
                }
            }

            AMEND_GUEST_RESULT -> {
                if (resultCode == RESULT_OK) {
                    setTransparentLoadingSpinner(true)
                    val arrival = viewModel.getArrivalDepartureDate().first
                    val departure = viewModel.getArrivalDepartureDate().second
                    viewModel.bookingConfPackagesAndAmendSummary(arrival, departure)

                }
            }
        }
    }

    // clears the reservation and room selections stored in shared preferences
    override fun onDestroy() {
        viewModel.clearReservation()
        viewModel.deleteLinkedAmend()
        viewModel.clearSavedRoomSelections()
        super.onDestroy()
    }
}
