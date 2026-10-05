package com.whitbread.premierinn.alternativeroomselection

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout.LayoutParams
import android.widget.LinearLayout.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isEmpty
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.jakewharton.rxbinding3.view.clicks
import com.whitbread.premierinn.R
import com.whitbread.premierinn.additionalinformation.AdditionalInformationActivity
import com.whitbread.premierinn.common.AutoCompositeDisposable
import com.whitbread.premierinn.common.BookingFlowInput
import com.whitbread.premierinn.common.RESULT_BUSINESS_LOGIN_SUCCESS
import com.whitbread.premierinn.common.activity.BaseActivity
import com.whitbread.premierinn.common.addTo
import com.whitbread.premierinn.common.analytics.AnalyticsConstants
import com.whitbread.premierinn.databinding.ActivityAlternativeRoomSelectionBinding
import com.whitbread.premierinn.databinding.ViewSummaryContinueBinding
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
class AlternativeRoomSelectionActivity : BaseActivity<ActivityAlternativeRoomSelectionBinding>() {

    private var density: Float = 0.0f
    private lateinit var viewSummaryContinueBinding: ViewSummaryContinueBinding
    private val disposable: AutoCompositeDisposable by lazy { AutoCompositeDisposable(lifecycle) }
    private lateinit var recyclerView : RecyclerView
    private val input by lazy { intent.getParcelableExtra<BathroomSelectionInput>(ROOM_SELECTION_INPUT) }
    private val viewModel: AlternativeRoomSelectionViewModel by viewModels()

    override fun inflateBinding(inflater: LayoutInflater): ActivityAlternativeRoomSelectionBinding {
        return ActivityAlternativeRoomSelectionBinding.inflate(inflater)
    }

    override fun getToolbar(): Toolbar? {
        return binding.toolbar
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewSummaryContinueBinding = ViewSummaryContinueBinding.bind(binding.root)
        setToolbar()

        recyclerView = binding.alternativeRoomImageRecyclerView

        viewModel.events()
            .subscribe {
                when (it) {
                    is AlternativeRoomSelectionViewModel.AlternativeRoomSelectionEvent.GenericErrorEvent -> {
                        Toast.makeText(this, it.error.message, Toast.LENGTH_SHORT).show()
                    }
                    is AlternativeRoomSelectionViewModel.AlternativeRoomSelectionEvent.SubmitEvent -> {
                        val intent = SummaryActivity.createIntent(this, it.summaryInput)
                        startActivity(intent)
                        finish()
                    }
                    is AlternativeRoomSelectionViewModel.AlternativeRoomSelectionEvent.ShowPriceBreakdown -> {
                        SummaryBreakdownActivity.start(this, it.summaryBreakdownInput)
                    }

                    is AlternativeRoomSelectionViewModel.AlternativeRoomSelectionEvent.CreateReservationFailureEvent -> {
                        binding.alternateRoomProgressBarContainer.isVisible = false
                        showCreateReservationError()
                    }

                    is AlternativeRoomSelectionViewModel.AlternativeRoomSelectionEvent.BookingInformationErrorEvent -> {
                        binding.alternateRoomProgressBarContainer.isVisible = false
                        input?.let { _ ->
                            viewModel.launchNextScreen(it.basketReference, it.updatedSummaryInput)
                        } ?: showGenericError()
                    }

                    is AlternativeRoomSelectionViewModel.AlternativeRoomSelectionEvent.OpenLoginDetails -> {
                        LoginActivity.createIntent(this@AlternativeRoomSelectionActivity,
                            Screen(ScreenType.BOOKING_FLOW_LOGIN.name, AnalyticsConstants.ScreenState.LOG_IN)
                        ).also { intent ->
                            startActivityForResult(intent, LoginActivity.ACTIVITY_RESULT_REQUEST_CODE)
                        }
                    }
                    is AlternativeRoomSelectionViewModel.AlternativeRoomSelectionEvent.OpenGuestDetailsActivity -> {
                        proceedToGuestDetails()
                    }

                    is AlternativeRoomSelectionViewModel.AlternativeRoomSelectionEvent.OpenAdditionalInfoActivity -> {
                        additionalInformationIntent(it.reviewBookingInput)
                    }

                    is AlternativeRoomSelectionViewModel.AlternativeRoomSelectionEvent.OpenReviewBookActivity -> {
                        reviewBookActivityIntent(it.reviewBookingInput)
                    }

                    is AlternativeRoomSelectionViewModel.AlternativeRoomSelectionEvent.GenericFailureEvent -> {
                        binding.alternateRoomProgressBarContainer.isVisible = false
                        showGenericError()
                    }
                }
            }.addTo(disposable)

        viewModel.states()
            .distinctUntilChanged()
            .subscribe(::render)
            .addTo(disposable)
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
            this@AlternativeRoomSelectionActivity, bookingFlowInput).also { startActivity(it) }
    }

    private fun reviewBookActivityIntent(reviewBookingInput: ReviewBookingInput) {
        ReviewBookActivity.createIntent(
            this@AlternativeRoomSelectionActivity, reviewBookingInput).also { startActivity(it) }
    }

    private fun additionalInformationIntent(reviewBookingInput: ReviewBookingInput) {
        AdditionalInformationActivity.createIntent(
            this@AlternativeRoomSelectionActivity, reviewBookingInput).also { startActivity(it) }
    }

    private fun setToolbar() {
        setToolbar(resources.getString(R.string.choose_your_room_type_label), true)
    }

    private fun render(state: AlternativeRoomSelectionState) {
        binding.alternateRoomProgressBarContainer.isVisible = state.isLoading //might be redundant tbh
        createAlternativeRoomImageView(state)
        createAlternativeRoomTypeView(state)
        populatePriceBreakdown(state)
        listenToPriceBreakdownClicks()
        listenToContinueButtonClicks()
    }

    private fun createAlternativeRoomImageView(state: AlternativeRoomSelectionState) {
        state.getTwinRoomInfo().let { list ->
            if (recyclerView.isEmpty()) {
                val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                val snapHelper: SnapHelper = PagerSnapHelper()

                recyclerView.layoutManager = layoutManager
                recyclerView.onFlingListener = null

                snapHelper.attachToRecyclerView(recyclerView)

                recyclerView.adapter = AlternativeRoomImageAdapter(list)
            }
        }
    }

    private fun createAlternativeRoomTypeView(state: AlternativeRoomSelectionState) {
        density = resources.displayMetrics.density

        if (binding.alternativeRoomTypeContainer.isEmpty()) {
            for (twinRoomChoices in state.getListOfOriginalRooms) {
                val headerView = AlternativeRoomTypeHeader(
                    twinRoomChoices.roomId,
                    twinRoomChoices.roomHeading,
                    twinRoomChoices.occupantsSubheading
                )
                val roomTypeView = AlternativeRoomTypeComponentView(
                    this,
                    headerView,
                    viewModel.getListOfRoomTypes(),
                    twinRoomChoices,
                    disposable,
                    state.deviceLocaleProvider
                )
                listenToSelections(roomTypeView, twinRoomChoices.roomId)
                val params = LayoutParams(
                    MATCH_PARENT,
                    WRAP_CONTENT
                )
                params.setMargins(0, (16 * density).toInt(), 0, 0)
                binding.alternativeRoomTypeContainer.addView(roomTypeView, params)
            }
        }
    }

    private fun listenToSelections(roomView: AlternativeRoomTypeComponentView, id: Int) {
        disposable.add(roomView
            .onSelectionClicked()
            .map {
                AlternativeRoomTypeComponentView.SelectedRoomClickEvent(
                    id,
                    it.twinRoomOption
                )
            }
            .subscribe { viewModel.onRoomSelected(it) })
    }

    private fun populatePriceBreakdown(state: AlternativeRoomSelectionState) {
        state.totalPrice?.let {
            viewSummaryContinueBinding.summaryExpensesTotalPriceContainer.text = it
        }
    }

    private fun listenToContinueButtonClicks() {
        disposable.add(binding.alternativeRoomContinueLayout.cabSummaryContinueToLogin.clicks()
            .subscribe {
                binding.alternateRoomProgressBarContainer.isVisible = true
                viewModel.onContinueClicked()
            })
    }


    private fun listenToPriceBreakdownClicks() {
        disposable.add(viewSummaryContinueBinding.summaryLink.clicks()
            .subscribe { viewModel.onPriceBreakdownClicked() })
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
                this@AlternativeRoomSelectionActivity, bookingFlowInput).also { startActivity(it) }
        }
    }

    private fun showGenericError() {
        Toast.makeText(
            this,
            R.string.generic_error_description,
            Toast.LENGTH_LONG
        ).show()
    }

    companion object {
        const val ROOM_SELECTION_INPUT = "room_selection_input_key"

        fun createIntent(context: Context, bathroomSelectionInput: BathroomSelectionInput?): Intent {
            return Intent(context, AlternativeRoomSelectionActivity::class.java).apply {
                putExtra(ROOM_SELECTION_INPUT, bathroomSelectionInput)
            }
        }
    }
}
