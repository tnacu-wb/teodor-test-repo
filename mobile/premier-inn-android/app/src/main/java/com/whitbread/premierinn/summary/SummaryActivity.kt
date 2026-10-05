package com.whitbread.premierinn.summary

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.whitbread.premierinn.R
import com.whitbread.premierinn.additionalinformation.AdditionalInformationActivity
import com.whitbread.premierinn.api.Urls
import com.whitbread.premierinn.common.BookingFlowInput
import com.whitbread.premierinn.common.RESULT_BUSINESS_LOGIN_SUCCESS
import com.whitbread.premierinn.common.activity.BaseActivity
import com.whitbread.premierinn.common.analytics.AnalyticsConstants
import com.whitbread.premierinn.common.mapper.toAncillariesCloseout
import com.whitbread.premierinn.common.summary.adapter.SummaryRoomsExtrasAdapter
import com.whitbread.premierinn.common.summary.adapter.SummaryRoomsMealsAdapter
import com.whitbread.premierinn.common.summary.model.SummaryRoomItem
import com.whitbread.premierinn.common.utils.filterAncillaryCloseOutItems
import com.whitbread.premierinn.data.common.toLocalDate
import com.whitbread.premierinn.databinding.ActivitySummaryBinding
import com.whitbread.premierinn.guestdetails.GuestDetailsActivity
import com.whitbread.premierinn.landing.LandingActivityIntent.create
import com.whitbread.premierinn.login.LoginActivity
import com.whitbread.premierinn.login.Screen
import com.whitbread.premierinn.login.ScreenType
import com.whitbread.premierinn.reviewbooking.ReviewBookActivity
import com.whitbread.premierinn.reviewbooking.ReviewBookingInput
import com.whitbread.premierinn.summary.SummaryDialogAction.Continue
import com.whitbread.premierinn.summary.SummaryDialogAction.Positive
import com.whitbread.premierinn.summary.SummaryDialogAction.TryAgain
import com.whitbread.premierinn.summary.SummaryError.CreateReservationError
import com.whitbread.premierinn.summary.SummaryError.GenericError
import com.whitbread.premierinn.summary.SummaryError.SaveReservationWithAncillariesError
import com.whitbread.premierinn.summary.fragments.MenuAndAllergyInfoBottomSheet
import com.whitbread.premierinn.summarybreakdown.SummaryBreakdownActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

const val SUMMARY_INPUT_V2 = "summary_input_v2"
const val EXTRA_BASKET_REFERENCE_V2 = "EXTRA_BASKET_REFERENCE_V2"

@AndroidEntryPoint
class SummaryActivity : BaseActivity<ActivitySummaryBinding>() {
    private val input by lazy { requireNotNull(intent.getParcelableExtra<SummaryInput>(SUMMARY_INPUT_V2)) }
    private val viewModel: SummaryViewModel by viewModels()
    private val summaryRoomsMealsAdapter: SummaryRoomsMealsAdapter by lazy { SummaryRoomsMealsAdapter(
        input.totalNights(),
        onIncrement = { roomIndex, mealIndex -> viewModel.mealIncrementCounter(roomIndex, mealIndex) },
        onDecrement = { roomIndex, mealIndex -> viewModel.mealDecrementCounter(roomIndex, mealIndex) }
    ) }
    private val summaryRoomsExtrasAdapter: SummaryRoomsExtrasAdapter by lazy { SummaryRoomsExtrasAdapter(
        onToggleChanged = {roomIndex, extraIndex, isSelected -> viewModel.updateExtrasToggleState(roomIndex, extraIndex, isSelected)  }
    ) }

    override fun inflateBinding(inflater: LayoutInflater): ActivitySummaryBinding {
        return ActivitySummaryBinding.inflate(inflater)
    }

    override fun getToolbar(): Toolbar? {
        return binding.toolbar
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setToolbar(getString(R.string.summary_toolbar_title), true)

        viewModel.init()

        setupUiElements()
        observeViewState()
        observeNavigation()
    }

    private fun observeViewState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collectLatest { onStateChanged(it) }
            }
        }
    }

    private fun observeNavigation() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.navigationEvent.collect { onNavigationEvent(it)
                }
            }
        }
    }

    private fun onStateChanged(state: SummaryState) = with(state) {
        showLoadingSpinner(isLoading)
        updateRoomsRV(state, roomList)
        updateTotalPrice(state)
        error?.let(::handleError)
    }

    private fun setupUiElements() {
        initGallery()
        setUpHotelInfo()
        setUpRecyclerViews()
        setUpMenuAndAllergyInfo()
        onCLickContinueButton()
        onCLickPriceBreakDown()
    }

    private fun initGallery() {
        val images = input.upsellsImages()?.map { Urls.CONTENT_BASE_URL + it }.orEmpty()
        if (images.isNotEmpty()) {
            binding.ivpSummaryRestaurantImages.apply {
                visibility = View.VISIBLE
                images(images)
            }
        }
    }

    private fun setUpMenuAndAllergyInfo() {
        binding.tvViewMenus.setOnClickListener {
            MenuAndAllergyInfoBottomSheet(viewModel.getMenuAndAllergyInfoList(false)).show(supportFragmentManager)
        }
        binding.tvAllergyInfo.setOnClickListener {
            MenuAndAllergyInfoBottomSheet(viewModel.getMenuAndAllergyInfoList(true)).show(supportFragmentManager)
        }
    }

    private fun setUpHotelInfo() {
        binding.ivSummaryBannerImage.load(
            Urls.CONTENT_BASE_URL + input.hotel().imageReference(), R.drawable.image_no_hotel
        )
        binding.tvSummaryBannerHotelTitle.text = input.hotel().name()
        binding.tvSummaryBannerDateAndNight.text = getString(
            R.string.arrival_departure_dates,
            input.arrivalDateGQ().toShortDateFormat(),
            input.departureDateGQ().toShortDateFormat()
        )
        binding.tvSummaryBannerGuestAndRoom.text = getString(
            R.string.guests_and_rooms,
            resources.getQuantityString(R.plurals.guests, input.totalGuests(), input.totalGuests()),
            resources.getQuantityString(R.plurals.rooms, input.totalRooms(), input.totalRooms())
        )
    }

    private fun setUpDinnerAllowancesInfo() = with(binding.tvApprovedMeals) {
        visibility = View.VISIBLE
        val textRes = if (input.dinnerAllowance() > 0)
            R.string.summary_approved_meals_dinner_allowance
        else
            R.string.summary_approved_meals
        setText(textRes)
    }

    private fun updateRoomsRV(state: SummaryState, roomList: List<SummaryRoomItem>) {
        if (!state.isLoading) {
            val hasMeals = roomList.any { it.meals.isNotEmpty() }
            val hasExtras = roomList.any { it.extras.isNotEmpty() }

            if (hasMeals) {
                displayMeals(roomList)
                if (viewModel.isInnBusinessUser) setUpDinnerAllowancesInfo()
            } else {
                val hasValidCloseOut = filterAncillaryCloseOutItems(
                    input.ancillaryCloseOutItems()?.toAncillariesCloseout().orEmpty(),
                    input.arrivalDate(),
                    input.departureDateGQ().toLocalDate()
                ).isNotEmpty()
                displayNoMeals(hasValidCloseOut)
            }

            if (hasExtras) {
                displayExtras(roomList)
            } else {
                hideExtras()
            }
        }

    }

    private fun displayMeals(roomList: List<SummaryRoomItem>) {
        binding.rvSummaryRoomsMeals.visibility = View.VISIBLE
        summaryRoomsMealsAdapter.submitList(roomList)
    }

    private fun displayNoMeals(isRestaurantUnavailable: Boolean) {
        with(binding) {
            restaurantUnavailableInfoBox.apply {
                visibility = View.VISIBLE
                setText(
                    if (isRestaurantUnavailable)
                        getString(R.string.summary_restaurant_unavailable_description)
                    else
                        getString(R.string.summary_no_meals_approved_description)
                )
            }
            rvSummaryRoomsMeals.visibility = View.GONE
            tvViewMenus.visibility = View.GONE
            tvAllergyInfo.visibility = View.GONE
            rlSummaryBreakfast.background = ContextCompat.getDrawable(applicationContext, R.color.white)
            tvApprovedMeals.visibility = View.GONE
        }
    }

    private fun displayExtras(roomList: List<SummaryRoomItem>) {
        binding.rvSummaryRoomsExtras.visibility = View.VISIBLE
        summaryRoomsExtrasAdapter.submitList(roomList)
    }

    private fun hideExtras() {
        binding.rvSummaryRoomsExtras.visibility = View.GONE
        binding.tvSummarySelectExtrasTitle.visibility = View.GONE
    }

    private fun updateTotalPrice(state: SummaryState) {
        binding.continueLayout.summaryExpensesTotalPriceContainer.text = state.totalStayPrice
    }

    private fun showLoadingSpinner(visibility: Boolean) {
        binding.summariesProgressBarContainer.isVisible = visibility
    }

    private fun setUpRecyclerViews() {
        binding.rvSummaryRoomsMeals.apply {
            layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
            adapter = summaryRoomsMealsAdapter
            isNestedScrollingEnabled = false
        }

        binding.rvSummaryRoomsExtras.apply {
            layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
            adapter = summaryRoomsExtrasAdapter
            isNestedScrollingEnabled = false
        }
    }

    private fun onCLickPriceBreakDown() {
        binding.continueLayout.summaryLink.setOnClickListener {
            startSummaryBreakdownActivity()
        }
    }

    private fun onCLickContinueButton() {
        binding.continueLayout.cabSummaryContinueToLogin.setOnClickListener {
            if (!viewModel.state.value.isLoading) {
                viewModel.saveReservationWithAncillaries()
            }
        }
    }

    private fun startSummaryBreakdownActivity() {
        SummaryBreakdownActivity.start(this, viewModel.constructSummaryBreakDownInput())
    }

    private fun onNavigationEvent(navEvent: SummaryNavigation) {
        when (navEvent) {
            is SummaryNavigation.OpenGuestDetailsActivity -> {
                guestDetailsActivityIntent(navEvent.bookingFlowInput)
            }
            is SummaryNavigation.OpenReviewBookActivity -> {
                reviewBookActivityIntent(navEvent.reviewBookingInput)
            }
            is SummaryNavigation.OpenAdditionalInfoActivity -> {
                additionalInformationIntent(navEvent.reviewBookingInput)
            }
            SummaryNavigation.OpenLoginActivity -> {
                LoginActivity.createIntent(this@SummaryActivity,
                    Screen(ScreenType.BOOKING_FLOW_LOGIN.name, AnalyticsConstants.ScreenState.LOG_IN)
                ).also {
                    startActivityForResult(it, LoginActivity.ACTIVITY_RESULT_REQUEST_CODE)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == LoginActivity.ACTIVITY_RESULT_REQUEST_CODE) {
            val currentRoomList = viewModel.state.value.roomList
            when (resultCode) {
                RESULT_OK -> {
                    guestDetailsActivityIntent(viewModel.constructBookingFlowInput(currentRoomList, true))
                }
                RESULT_BUSINESS_LOGIN_SUCCESS -> {
                    startActivity(create(this))
                    finish()
                }
                RESULT_FIRST_USER -> {
                    guestDetailsActivityIntent(viewModel.constructBookingFlowInput(currentRoomList, false))
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val intent = intent.putExtra(EXTRA_BASKET_REFERENCE_V2, viewModel.state.value.basketReference)
        setResult(RESULT_OK, intent)

        super.onBackPressed()
    }

    private fun guestDetailsActivityIntent(bookingFlowInput: BookingFlowInput) {
        GuestDetailsActivity.createIntent(
            this@SummaryActivity, bookingFlowInput).also { startActivity(it) }
    }

    private fun reviewBookActivityIntent(reviewBookingInput: ReviewBookingInput) {
        ReviewBookActivity.createIntent(
            this@SummaryActivity, reviewBookingInput).also { startActivity(it) }
    }

    private fun additionalInformationIntent(reviewBookingInput: ReviewBookingInput) {
        AdditionalInformationActivity.createIntent(
            this@SummaryActivity, reviewBookingInput).also { startActivity(it) }
    }

    private fun launchNextActivity(isLoggedIn: Boolean) {
        lifecycleScope.launch {
            viewModel.goToNextScreen(isLoggedIn)
        }
    }

    private fun handleError(error: SummaryError) {
        // Early return for the NO_SELECTION case: no dialog is shown.
        if (error is SaveReservationWithAncillariesError && error.mealError == MealSelectionStatus.NO_SELECTION) return

        val (title, message, actions) = when (error) {
            GenericError, CreateReservationError, SummaryError.PaymentMethodsAndBookingConfirmationError,
            SummaryError.BookingConfirmationError->
                Triple(
                    getString(R.string.summary_breakfast_save_failed_title),
                    getString(R.string.generic_error_description),
                    listOf(Positive { finish() })
                )

            SummaryError.CreateReservationGuestError ->
                Triple(
                    getString(R.string.summary_breakfast_save_failed_title),
                    getString(R.string.generic_error_description),
                    listOf(Positive { finish() })
                )

            is SaveReservationWithAncillariesError -> when (error.mealError) {
                MealSelectionStatus.NEW_MEAL_ERROR ->
                    Triple(
                        getString(R.string.summary_breakfast_save_failed_title),
                        getString(R.string.summary_breakfast_save_failed_message),
                        listOf(
                            Continue { launchNextActivity(error.isLoggedIn) },
                            TryAgain {}
                        )
                    )
                MealSelectionStatus.PREVIOUS_MEAL_ERROR ->
                    Triple(
                        getString(R.string.summary_breakfast_save_failed_title),
                        getString(R.string.summary_breakfast_save_failed_second_attempt_message),
                        listOf(
                            Continue { launchNextActivity(error.isLoggedIn) },
                            TryAgain {}
                        )
                    )
                null ->
                    Triple(
                        getString(R.string.summary_breakfast_save_failed_title),
                        getString(R.string.generic_error_description),
                        listOf(Positive { finish() })
                    )
                else -> throw IllegalStateException("Unhandled error state")
            }
        }
        displayErrorAlertDialog(title, message, actions)
    }

    private fun displayErrorAlertDialog(
        title: String,
        message: String,
        actions: List<SummaryDialogAction> = emptyList()
    ) {
        AlertDialog.Builder(this, R.style.PurpleDialog)
            .setTitle(title)
            .setMessage(message)
            .apply {
                actions.forEach { dialogAction ->
                    when (dialogAction) {
                        is Positive ->
                            setPositiveButton(android.R.string.ok) { dialog, _ ->
                                viewModel.onErrorHandled()
                                dialogAction.action()
                                dialog.dismiss()
                            }
                        is Continue ->
                            setPositiveButton(R.string.button_text_continue) { dialog, _ ->
                                viewModel.onErrorHandled()
                                dialogAction.action()
                                dialog.dismiss()
                            }
                        is TryAgain ->
                            setNegativeButton(R.string.button_text_try_again) { dialog, _ ->
                                viewModel.onErrorHandled()
                                dialogAction.action()
                                dialog.dismiss()
                            }
                    }
                }
            }
            .create()
            .show()
    }

    companion object {
        @JvmStatic
        fun createIntent(
            context: Context,
            input: SummaryInput
        ): Intent {
            return Intent(context, SummaryActivity::class.java).apply {
                putExtra(SUMMARY_INPUT_V2, input)

            }
        }
    }
}