package com.whitbread.premierinn.bathroomselection

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.SparseArray
import android.view.LayoutInflater
import androidx.activity.viewModels
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.util.isEmpty
import androidx.core.view.isVisible
import com.jakewharton.rxbinding3.view.clicks
import com.whitbread.premierinn.R
import com.whitbread.premierinn.accessiblebathroomselection.AccessibleRoomSizeOption
import com.whitbread.premierinn.accessiblebathroomselection.RoomOptionsFragment
import com.whitbread.premierinn.additionalinformation.AdditionalInformationActivity
import com.whitbread.premierinn.bathroomselection.BathroomSelectionEvent.ShowSizeSelectionEvent
import com.whitbread.premierinn.bathroomselection.BathroomSelectionEvent.SubmitEvent
import com.whitbread.premierinn.common.AutoCompositeDisposable
import com.whitbread.premierinn.common.BookingFlowInput
import com.whitbread.premierinn.common.RESULT_BUSINESS_LOGIN_SUCCESS
import com.whitbread.premierinn.common.activity.BaseActivity
import com.whitbread.premierinn.common.addTo
import com.whitbread.premierinn.common.analytics.AnalyticsConstants
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.databinding.ActivityBathroomSelectionBinding
import com.whitbread.premierinn.guestdetails.GuestDetailsActivity
import com.whitbread.premierinn.hoteldetails.BathroomSelectionInput
import com.whitbread.premierinn.landing.LandingActivityIntent.create
import com.whitbread.premierinn.login.LoginActivity
import com.whitbread.premierinn.login.Screen
import com.whitbread.premierinn.login.ScreenType
import com.whitbread.premierinn.reviewbooking.ReviewBookActivity
import com.whitbread.premierinn.reviewbooking.ReviewBookingInput
import com.whitbread.premierinn.summary.SummaryActivity
import com.whitbread.premierinn.summarybreakdown.SummaryBreakdownActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BathroomSelectionActivity: BaseActivity<ActivityBathroomSelectionBinding>() {

    private val disposable: AutoCompositeDisposable by lazy { AutoCompositeDisposable(lifecycle) }

    private val accessibleRoomViews = SparseArray<AccessibleRoomOptionsView>()
    private val input by lazy { intent.getParcelableExtra<BathroomSelectionInput>(BATHROOM_SELECTION_INPUT)}
    private val viewModel: BathroomSelectionViewModel by viewModels()

    override fun inflateBinding(inflater: LayoutInflater): ActivityBathroomSelectionBinding {
        return ActivityBathroomSelectionBinding.inflate(inflater)
    }

    override fun getToolbar(): Toolbar? {
        return binding.toolbar
    }

    companion object {
        const val BATHROOM_SELECTION_INPUT = "bathroom_selection_input_key"

        fun createIntent(context: Context, bathroomSelectionInput: BathroomSelectionInput?): Intent {
            return Intent(context, BathroomSelectionActivity::class.java).apply {
                putExtra(BATHROOM_SELECTION_INPUT, bathroomSelectionInput)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setToolbar(resources.getString(R.string.bathroom_selection_title), true)

        viewModel.states()
                .distinctUntilChanged()
                .subscribe { display(it) }
                .addTo(disposable)

        viewModel.events()
            .subscribe {
                when (it) {
                    is SubmitEvent -> {
                        binding.bathroomProgressBarContainer.isVisible = false
                        val intent = SummaryActivity.createIntent(this, it.summaryInput)
                        startActivity(intent)
                        finish()
                    }

                    is BathroomSelectionEvent.ShowPriceBreakdownEvent -> {
                        SummaryBreakdownActivity.start(this, it.summaryBreakdownInput)
                    }

                    is ShowSizeSelectionEvent -> {
                        showRoomSizeSelectionFragment(it.roomId, it.currentlySelectedSize)
                    }

                    is BathroomSelectionEvent.CreateReservationFailureEvent -> {
                        binding.bathroomProgressBarContainer.isVisible = false
                        showCreateReservationError()
                    }

                    is BathroomSelectionEvent.BookingInformationErrorEvent -> {
                        binding.bathroomProgressBarContainer.isVisible = false
                        input?.let { _ ->
                            viewModel.launchNextScreen(it.basketReference, it.updatedSummaryInput)
                        } ?: showGenericError()
                    }

                    is BathroomSelectionEvent.OpenLoginDetails -> {
                        LoginActivity.createIntent(this@BathroomSelectionActivity,
                            Screen(ScreenType.BOOKING_FLOW_LOGIN.name, AnalyticsConstants.ScreenState.LOG_IN)
                        ).also { intent ->
                            startActivityForResult(intent, LoginActivity.ACTIVITY_RESULT_REQUEST_CODE)
                        }
                    }

                    is BathroomSelectionEvent.OpenGuestDetailsActivity -> {
                        proceedToGuestDetails()
                    }

                    is BathroomSelectionEvent.OpenAdditionalInfoActivity -> {
                        additionalInformationIntent(it.reviewBookingInput)
                    }

                    is BathroomSelectionEvent.OpenReviewBookActivity -> {
                        reviewBookActivityIntent(it.reviewBookingInput)
                    }

                    is BathroomSelectionEvent.GenericFailureEvent -> {
                        binding.bathroomProgressBarContainer.isVisible = false
                        showGenericError()
                    }
                }
            }.addTo(disposable)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == LoginActivity.ACTIVITY_RESULT_REQUEST_CODE) {
            val bookingFlowInput = viewModel.getBookingFlowInput()
            when (resultCode) {
                RESULT_OK -> {
                    bookingFlowInput?.let {
                        guestDetailsActivityIntent(bookingFlowInput)
                    }
                }
                RESULT_BUSINESS_LOGIN_SUCCESS -> {
                    startActivity(create(this))
                    finish()
                }
                RESULT_FIRST_USER -> {
                    bookingFlowInput?.let {
                        guestDetailsActivityIntent(bookingFlowInput)
                    }
                }
            }
        }
    }

    private fun guestDetailsActivityIntent(bookingFlowInput: BookingFlowInput) {
        GuestDetailsActivity.createIntent(
            this@BathroomSelectionActivity, bookingFlowInput).also { startActivity(it) }
    }

    private fun reviewBookActivityIntent(reviewBookingInput: ReviewBookingInput) {
        ReviewBookActivity.createIntent(
            this@BathroomSelectionActivity, reviewBookingInput).also { startActivity(it) }
    }

    private fun additionalInformationIntent(reviewBookingInput: ReviewBookingInput) {
        AdditionalInformationActivity.createIntent(
            this@BathroomSelectionActivity, reviewBookingInput).also { startActivity(it) }
    }

        private fun showRoomSizeSelectionFragment(roomId: Int, currentlySelectedSize: AccessibleRoomSizeOption) {
        val roomOptionsFragment = RoomOptionsFragment.create(currentlySelectedSize, true)
        roomOptionsFragment.show(supportFragmentManager, "room_size_fragment")
        roomOptionsFragment.callback(object: RoomOptionsFragment.Callback {
            override fun onAccessibleRoomSelected(option: AccessibleRoomSizeOption) {
                viewModel.onRoomSizeSelected(roomId, option)
                roomOptionsFragment.dismiss()
            }

            override fun onCanceled() {
                roomOptionsFragment.dismiss()
            }
        })
    }

    private fun showCreateReservationError() {
        AlertDialog.Builder(this, R.style.PurpleDialog)
            .setTitle(getString(R.string.generic_error_title))
            .setMessage(getString(R.string.generic_error_message_with_try_again))
            .setPositiveButton(getText(android.R.string.ok))
            { _, _ ->
                finish()
            }
            .setCancelable(false)
            .create()
            .show()
    }

    private fun proceedToGuestDetails() {
        val bookingFlowInput = viewModel.getBookingFlowInput()
        bookingFlowInput?.let {
            GuestDetailsActivity.createIntent(
                this@BathroomSelectionActivity, bookingFlowInput).also { startActivity(it) }
        }
    }

    private fun showGenericError() {
        Toast.makeText(
            this,
            R.string.generic_error_description,
            Toast.LENGTH_LONG
        ).show()
    }

    private fun display(state: BathroomSelectionState) {
        if (accessibleRoomViews.isEmpty()) {
            initialiseRooms(state)
        } else {
            updateAccessibleData(state.accessibleRoomChoices, state.deviceLocaleProvider)
        }
        setTotalCostContainer(state)
    }

    private fun setTotalCostContainer(state: BathroomSelectionState) {
        state.totalCost.let { totalCost ->
            binding.viewSummaryContinue.summaryExpensesTotalPriceContainer.text = totalCost
        }
    }

    private fun initialiseRooms(state: BathroomSelectionState) {
        for (roomWithChoices in state.accessibleRoomChoices) {
            val roomView = createAccessibleRoomView(roomWithChoices.roomId)
            roomView.setData(roomWithChoices, state.deviceLocaleProvider)
        }
        for (nonAccessibleRoom in state.nonAccessibleRoomChoices) {
            createNonAccessibleRoomView(nonAccessibleRoom, state.deviceLocaleProvider)
        }
    }

    private fun updateAccessibleData(accessibleRoomChoices: List<AccessibleRoomChoices>,
                                     deviceLocaleProvider: DeviceLocaleProvider) {
        for (roomWithChoices in accessibleRoomChoices) {
            accessibleRoomViews[roomWithChoices.roomId].setData(
                roomWithChoices,
                deviceLocaleProvider
            )
        }
    }

    private fun createNonAccessibleRoomView(nonAccessibleRoom: NonAccessibleRoom,
                                            deviceLocaleProvider: DeviceLocaleProvider) {
        val nonAccessibleRoomView = NonAccessibleRoomOptionsView(this)
        binding.bathroomRoomsContainer.addView(nonAccessibleRoomView)
        nonAccessibleRoomView.setData(nonAccessibleRoom, deviceLocaleProvider)
    }

    private fun createAccessibleRoomView(roomId: Int): AccessibleRoomOptionsView {
        val roomView = AccessibleRoomOptionsView(this)
        binding.bathroomRoomsContainer.addView(roomView)
        accessibleRoomViews[roomId] = roomView

        listenToBathroomSelections(roomView, roomId)
        listenToChangeSizeClicks(roomView, roomId)
        listenToPriceBreakdownClicks()
        listenToContinueButtonClicks()

        return roomView
    }

    private fun listenToBathroomSelections(roomView: AccessibleRoomOptionsView, roomId: Int) {
        disposable.add(roomView
            .onBathroomClicked()
            .map { AccessibleRoomOptionsView.BathroomClickEvent(roomId, it) }
            .subscribe { viewModel.onBathroomSelected(it) })
    }

    private fun listenToChangeSizeClicks(roomView: AccessibleRoomOptionsView, roomId: Int) {
        disposable.add(roomView
                .onChangeClicked()
                .subscribe { viewModel.onRoomSizeChangeClicked(roomId) })
    }

    private fun listenToContinueButtonClicks() {
        disposable.add(binding.viewSummaryContinue.cabSummaryContinueToLogin.clicks()
                .subscribe {
                    binding.bathroomProgressBarContainer.isVisible = true
                    viewModel.onContinueClicked()
                })
    }

    private fun listenToPriceBreakdownClicks() {
        disposable.add(binding.viewSummaryContinue.summaryLink.clicks()
            .subscribe { viewModel.onPriceBreakdownClicked() })
    }
}
