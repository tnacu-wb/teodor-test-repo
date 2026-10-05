package com.whitbread.premierinn.amend.amendreview

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import com.whitbread.premierinn.R
import com.whitbread.premierinn.amend.amendAndPay.AmendAndPayActivity
import com.whitbread.premierinn.amend.amendreview.adapter.Diffable
import com.whitbread.premierinn.amend.amendreview.adapter.DiffableCallback
import com.whitbread.premierinn.amend.amendreview.adapter.ReviewAmendAdapter
import com.whitbread.premierinn.amend.amendreview.uimodel.AmendedTotal
import com.whitbread.premierinn.amend.amendreview.uimodel.ReviewAmendBalanceItem
import com.whitbread.premierinn.amend.amendreview.uimodel.ReviewAmendConfirmItem
import com.whitbread.premierinn.amend.amendreview.uimodel.ReviewAmendEciLcoInfoBoxItem
import com.whitbread.premierinn.amend.amendreview.uimodel.ReviewAmendTitleItem
import com.whitbread.premierinn.amend.amendreview.uimodel.ReviewChangesTitleItem
import com.whitbread.premierinn.amend.amendreview.uimodel.ReviewSeparatorItem
import com.whitbread.premierinn.amend.toParcelable
import com.whitbread.premierinn.bookingdetails.BookingDetailsActivity
import com.whitbread.premierinn.common.AutoCompositeDisposable
import com.whitbread.premierinn.common.PaymentTimingChoice
import com.whitbread.premierinn.common.activity.BaseActivity
import com.whitbread.premierinn.common.addTo
import com.whitbread.premierinn.common.managebooking.ManageBookingInput
import com.whitbread.premierinn.common.utils.HtmlUtils
import com.whitbread.premierinn.common.view.CallToActionButtonLayout
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.databinding.ActivityReviewAmendsBinding
import com.whitbread.premierinn.databinding.ViewReviewAmendConfirmBinding
import com.whitbread.premierinn.domain.common.EUR
import com.whitbread.premierinn.domain.common.GBP
import com.whitbread.premierinn.domain.common.PaymentDetails
import com.whitbread.premierinn.domain.graphql.amend.entity.AmendSummaryDomain
import com.whitbread.premierinn.domain.reservation.entity.Reservation
import com.whitbread.premierinn.summary.models.ParcelableExtrasItem
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.absoluteValue

@AndroidEntryPoint
class ReviewAmendsActivity : BaseActivity<ActivityReviewAmendsBinding>() {

    private var billingAddress: String = EMPTY_STRING
    private val disposable: AutoCompositeDisposable by lazy { AutoCompositeDisposable(lifecycle) }

    private val input by lazy {
        requireNotNull(
            intent.getParcelableExtra<ManageBookingInput>(
                EXTRA_AMEND_INPUT
            )
        )
    }
    private val tempBasketRef by lazy { requireNotNull(intent.getStringExtra(TEMP_BASKET_REF)) }
    private val uuidBasketReference by lazy { requireNotNull(intent.getStringExtra(UUID_BASKET_REFERENCE)) }
    private val token by lazy { requireNotNull(intent.getStringExtra(TOKEN)) }
    private val amendSummaryDomain by lazy {
        intent.getSerializableExtra(AMEND_SUMMARY_DOMAIN) as AmendSummaryDomain
    }
    private val listOfEciLcoExtras by lazy { intent.getParcelableArrayListExtra<ParcelableExtrasItem>(
        UPSELLS_EXTRAS_LIST
    ) }

    private val isEciLcoBooking by lazy {
        requireNotNull(
            intent.getBooleanExtra(IS_ECI_LCO_BOOKING, false)
        )
    }

    private lateinit var currency: String
    private val payNowFlow by lazy { intent.getBooleanExtra(PAY_NOW_FLOW, false) }

    private val viewModel: ReviewAmendViewModel by viewModels()

    private lateinit var reviewAmendConfirmBinding: ViewReviewAmendConfirmBinding

//    private val isFromDashboard by lazy {
//        requireNotNull(
//            intent.getBooleanExtra(
//                DASHBOARD_AMEND_MEALS,
//                false
//            )
//        )
//    }

    private lateinit var adapter: ReviewAmendAdapter
    private lateinit var amendedReservation: Reservation
    private lateinit var pollingMessage: String

    override fun inflateBinding(inflater: LayoutInflater): ActivityReviewAmendsBinding {
        return ActivityReviewAmendsBinding.inflate(inflater)
    }

    override fun getToolbar(): Toolbar? {
        return binding.toolbar
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        reviewAmendConfirmBinding = ViewReviewAmendConfirmBinding.inflate(layoutInflater)

        setToolbar(resources.getString(R.string.review_amends_title), true)

        adapter =  ReviewAmendAdapter(
        onCancelChangesClicked = { showCancelChangesDialog() },
        selectedPaymentTimingChoice = { paymentTimingChoice -> setTextForBalance(paymentTimingChoice) },
        onConfirmChangesClicked = { amendTotal, amendedReservation, selectedPaymentTimingChoice ->
            completeAmend(
                amendTotal,
                amendedReservation,
                selectedPaymentTimingChoice
            )
        },
        isPayNowFlow = payNowFlow,
        disposable = disposable,
        isEciLcoBooking = isEciLcoBooking)
        setRecyclerView()

        binding.reviewAmendRecyclerview
        binding.progress.indeterminateDrawable.colorFilter =
            BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                R.color.white,
                BlendModeCompat.SRC_IN
            )

        viewModel.events()
            .subscribe {
                when (it) {
                    is ReviewAmendViewModel.ReviewAmendEvent.GenericErrorEvent -> {
                    }
                    is ReviewAmendViewModel.ReviewAmendEvent.SubmitAmendedReservation -> {
                        setTransparentLoadingSpinner(true)
                        binding.translucentLoadingAmend.loadingSpinnerInfoText.text = pollingMessage
                        viewModel.findBookingForRetrievingToken(isFailure = false, arrivalDate = it.arrivalDate, lastName = it.lastName)
                    }
                    is ReviewAmendViewModel.ReviewAmendEvent.FindBookingSuccessEvent -> {
                        startMyBookingsActivity(it.amendedTotal, it.customerEmail, it.findBookingToken)
                    }
                    is ReviewAmendViewModel.ReviewAmendEvent.AmendErrorEvent -> {
                        updateErrorBanner()
                    }
                    is ReviewAmendViewModel.ReviewAmendEvent.ConfirmAmendLogicSuccessfulEvent -> {
                        if (it.confirmAmendLogicResp.status == "NOT_REQUIRED") {
                            reviewAmendConfirmBinding.reviewAmendConfirmButton.setLoadingState(true)
                            viewModel.basketStatusCall(tempBasketRef, amendedReservation)
                        } else {
                            reviewAmendConfirmBinding.reviewAmendConfirmButton.setLoadingState(false)
                            showAmendErrorPopupWithRetry(getString(R.string.review_amends_confirm_amend_failure_title),
                                getString(R.string.review_amends_confirm_amend_failure))
                        }
                    }
                    is ReviewAmendViewModel.ReviewAmendEvent.HideCtaLoadingAndShowTranslucentSpinnerEvent -> {
                        reviewAmendConfirmBinding.reviewAmendConfirmButton.setLoadingState(false)
                        setTransparentLoadingSpinner(true)
                    }

                    is ReviewAmendViewModel.ReviewAmendEvent.ShowPopupWithRetryOption -> {
                        reviewAmendConfirmBinding.reviewAmendConfirmButton.setLoadingState(false)
                        setTransparentLoadingSpinner(false)
                        showAmendErrorPopupWithRetry(it.title, it.errorDescription)
                    }

                    is ReviewAmendViewModel.ReviewAmendEvent.ShowFailedBasketErrorPopup -> {
                        reviewAmendConfirmBinding.reviewAmendConfirmButton.setLoadingState(false)
                        showAmendFailedErrorPopup(it.errorDescription, it.findBookingToken)
                    }
                }

            }.addTo(disposable)

        viewModel.states()
            .distinctUntilChanged()
            .subscribe(::render)
            .addTo(disposable)

        viewModel.getOriginalReservation()
    }

    private fun updateErrorBanner() {
        reviewAmendConfirmBinding.reviewAmendConfirmButton.setLoadingState(false)
        binding.amendReviewErrorBanner.visibility = View.VISIBLE
        binding.amendReviewErrorMessage.text = getString(R.string.review_amends_error_amending)
    }

    private fun showCancelChangesDialog() {
        val dialog = AlertDialog.Builder(this, R.style.PurpleDialog)
            .setTitle(getString(R.string.dialog_discard_changes_title))
            .setMessage(getString(R.string.dialog_discard_changes_message))
            .setNegativeButton(getString(R.string.dialog_cancel_negative_button))
            { _, _ -> }
            .setPositiveButton(getString(R.string.dialog_discard_button))
            { _, _ ->
                setResult(Activity.RESULT_OK)
                finish()
            }
            .create()

        dialog.show()
    }


    private fun showAmendErrorPopupWithRetry(title: String, errorDescription: String) {
        androidx.appcompat.app.AlertDialog.Builder(this, R.style.PurpleDialog)
            .setTitle(title)
            .setMessage(errorDescription)
            .setPositiveButton(getText(android.R.string.ok))
            { _, _ ->  }
            .create()
            .show()
    }


    private fun showAmendFailedErrorPopup(errorDescription: String, findBookingToken: String) {
        androidx.appcompat.app.AlertDialog.Builder(this, R.style.PurpleDialog)
            .setTitle(getString(R.string.amend_error_title))
            .setMessage(errorDescription)
            .setPositiveButton(getText(android.R.string.ok))
            { _, _ ->
                launchBookingDetailsScreen(findBookingToken)
            }
            .setCancelable(false)
            .create()
            .show()
    }


    private fun render(state: ReviewAmendState) {

        val reviewSummary = ArrayList<Diffable>()

        binding.progress.isVisible = state.isLoading

        if (isEciLcoBooking) {
            reviewSummary.add(
                ReviewAmendEciLcoInfoBoxItem().mapToReviewAmendEciLcoInfoMessage(
                    applicationContext.getString(R.string.review_amends_eci_lco_removed)
                )
            )
        }

        state.bookingPaymentInfo?.let {
            reviewSummary.add(ReviewAmendTitleItem().mapToReviewAmendTitle(it.title, it.cost))
        }

        state.getAmendedReservation?.let { amendedReservation ->
            state.getOriginalReservation?.let { originalReservation ->
                reviewSummary.add(ReviewSeparatorItem)
                reviewSummary.add(ReviewChangesTitleItem().mapToReviewChangesHeader(getString(R.string.review_amends_your_changes_title)))

                viewModel.findDatesChanged(amendedReservation, originalReservation)
                viewModel.findMealsChanged(amendedReservation, originalReservation)
                viewModel.findRoomChanged(amendedReservation, originalReservation)
                viewModel.findRoomAdded(amendedReservation, originalReservation)
                viewModel.findRoomRemoved(amendedReservation, originalReservation)
                viewModel.findExtrasChanged(
                    if (isEciLcoBooking) listOfEciLcoExtras else null,
                    amendedReservation,
                    originalReservation
                )
            }
        }

        billingAddress = state.billingAddress ?: EMPTY_STRING

        state.onDateChanged?.let {
            reviewSummary.add(it.dates)
        }

        state.onMealsChanged?.let {
            reviewSummary.add(it.meals)
        }

        currency = state.getAmendedTotal?.currency ?: GBP
        state.onRoomChanged?.let {
            it.roomsChangedList.forEach { roomChanged ->
                reviewSummary.add(roomChanged)
            }
        }

        state.onRoomAdded?.let {
            it.roomsAddedList.forEach { roomAdded ->
                reviewSummary.add(roomAdded)
            }
        }

        state.onRoomRemoved?.let {
            it.roomsRemovedList.forEach { roomRemoved ->
                reviewSummary.add(roomRemoved)
            }
        }

        state.onExtrasChanged?.let {
            it.extrasChangedList.forEach { extrasChanged ->
                reviewSummary.add(extrasChanged)
            }
        }

        state.submitBookingSummary?.let { bookingSummary ->
            viewModel.createBookingSummary(bookingSummary)
        }

        state.confirmPollingMessage?.let {
            pollingMessage = it
            binding.translucentLoadingAmend.loadingSpinnerInfoText.text = it
        }

        state.onBookingSummaryCreated?.let {
            reviewSummary.add(ReviewSeparatorItem)
            reviewSummary.add(it.header)
            reviewSummary.add(it.dates)
            reviewSummary.add(it.guestAndRooms)
            it.meals?.let { meals -> reviewSummary.add(meals) }
            it.extras?.let { extras -> reviewSummary.add(extras) }
            reviewSummary.add(ReviewSeparatorItem)
        }

        if (amendSummaryDomain.refund < 0.0f) {
            reviewSummary.add(
                ReviewAmendBalanceItem().mapToReviewAmendBalanceItem(
                    viewModel.getBalanceOutstandingTitle(),
                    viewModel.getToBeRefundedText(),
                    viewModel.getRefundAmount()
                )
            )
        } else {
            reviewSummary.add(
                ReviewAmendBalanceItem().mapToReviewAmendBalanceItem(
                    viewModel.getBalanceOutstandingTitle(),
                    viewModel.getBalanceOutstandingTextPoa(),
                    viewModel.getBalanceAmount()
                )
            )
        }


        state.submitList?.let {
            val diffResult = DiffUtil.calculateDiff(DiffableCallback(adapter.items, reviewSummary))
            adapter.items = reviewSummary
            diffResult.dispatchUpdatesTo(adapter)
        }

        createConfirmContainer(reviewSummary, state)
    }

    private fun setTransparentLoadingSpinner(show: Boolean) {
        binding.translucentLoadingAmend.translucentConstraintLayout.isVisible = show
        binding.translucentLoadingAmend.translucentConstraintLayout.isClickable = show

        binding.translucentLoadingAmend.translucentConstraintLayout.visibility = if (show) View.VISIBLE else View.GONE
        binding.translucentLoadingAmend.loadingSpinnerInfoText.visibility =
            if (show) View.VISIBLE else View.GONE

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

    private fun setRecyclerView() {
        binding.reviewAmendRecyclerview.adapter = adapter
    }

    private fun setTextForBalance(paymentTimingChoice: PaymentTimingChoice) {
        val balanceTextDescription: TextView =
            findViewById(R.id.amend_balance_description)
        if (paymentTimingChoice == PaymentTimingChoice.PAY_NOW) {
            balanceTextDescription.text = getString(R.string.review_amends_balance_description_pay_now)
        } else {
            balanceTextDescription.text = getString(R.string.review_amends_balance_description_poa)
        }
    }

    private fun completeAmend(
        amendedTotal: AmendedTotal,
        amendedReservation: Reservation,
        selectedPaymentTimingChoice: PaymentTimingChoice
    ) {
        val confirmButton: CallToActionButtonLayout = findViewById(R.id.review_amend_confirm_button)
        // if POA selected
        if (selectedPaymentTimingChoice == PaymentTimingChoice.PAY_LATER) {
            // call confirmAmendLogic
            confirmButton.setLoadingState(true)
            this.amendedReservation = amendedReservation
            viewModel.confirmAmendLogicCheckPoa(
                tempBasketRef,
                uuidBasketReference,
                token,
                amendedTotal
            )
        } else {
            // launch the Pay now flow by launching new activity
            confirmButton.setLoadingState(false)
            AmendAndPayActivity.createIntent(
                this,
                input,
                amendSummaryDomain,
                billingAddress,
                currency,
                tempBasketRef,
                uuidBasketReference,
                token
            ).also {
                startActivity(it)
            }
        }
    }

    private fun createConfirmContainer(
        reviewSummary: ArrayList<Diffable>,
        state: ReviewAmendState
    ) {
        val spannableTermsAndConditions = HtmlUtils.parseTags(
            applicationContext.getString(
                R.string.review_amends_terms_and_conditions,
                getString(R.string.terms_conditions_web_url)
            )
        )
        val confirmContainer = ReviewAmendConfirmItem().mapToConfirmItem(
            spannableTermsAndConditions,
            state.getBalance,
            state.getAmendedReservation
        )
        reviewSummary.add(confirmContainer)
    }

    private fun launchBookingDetailsScreen(token: String?) {
        BookingDetailsActivity.createIntent(
            this,
            input.bookingReference(),
            uuidBasketReference,
            null,
            null,
            null,
            token,
            EMPTY_STRING,
            EMPTY_STRING,
            false,
            EMPTY_STRING,
            EMPTY_STRING
        ).also { intent ->
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            startActivity(intent)
            finish()
        }
    }

    private fun startMyBookingsActivity(amendedTotal: AmendedTotal, customerEmail: String, findBookingToken: String) {
        reviewAmendConfirmBinding.reviewAmendConfirmButton.setLoadingState(false)
        viewModel.refreshDashboard()
        val currencySymbol = when (currency) {
            GBP -> {
                "£"
            }
            EUR -> {
                "€"
            }
            else -> "£"
        }
        val amendedPoa =
            if (amendSummaryDomain.refund >= 0.0f) {
                amendedTotal.copy(
                    amount = currencySymbol + amendSummaryDomain.payOnArrival.toBigDecimal()
                        .toString()
                )
            } else {
                amendedTotal.copy(
                    amount = currencySymbol + amendSummaryDomain.refund.absoluteValue.toBigDecimal().toString(),
                    description = getString(R.string.review_amends_balance_description_refund)
                )
            }

        BookingDetailsActivity.createIntent(
            this,
            input.bookingReference(),
            uuidBasketReference,
            customerEmail,
            amendedPoa.toParcelable(),
            null,
            findBookingToken,
            EMPTY_STRING,
            EMPTY_STRING,
            false,
            EMPTY_STRING,
            EMPTY_STRING
        ).also { intent ->
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)

//            if (isFromDashboard) {
//                startActivityForResult(intent, DASHBOARD_REVIEW_AMENDS_REQUEST_RESULT)
//                intent.putExtra(DASHBOARD_AMEND_MEALS, isFromDashboard)
//                setResult(Activity.RESULT_OK, intent)
//            } else
                startActivity(intent)
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (resultCode == RESULT_OK && intent != null) {
            //TODO: Complete when we implement pay now for amend.
        } else {
            updateErrorBanner()
        }
    }

    companion object {
        const val EXTRA_AMEND_INPUT = "amend_reservation_input"
        private const val IS_ECI_LCO_BOOKING = "is_eci_lco_booking"
        private const val UPSELLS_EXTRAS_LIST = "upsells_extras_list"
        private const val TEMP_BASKET_REF = "temp_basket_ref"
        private const val UUID_BASKET_REFERENCE = "uuid_basket_ref"
        private const val TOKEN = "token"
        const val AMEND_SUMMARY_DOMAIN = "amend_summary_domain"
        private const val PAY_NOW_FLOW = "pay_now_flow"

        @JvmStatic
        fun createIntent(
            context: Context,
            input: ManageBookingInput,
            isEciLcoBooking: Boolean,
            amendSummaryDomain: AmendSummaryDomain? = null,
            tempBasketRef: String? = null,
            uuidBasketReference: String? = null,
            token: String? = null,
            listOfEciLcoExtras: java.util.ArrayList<ParcelableExtrasItem> ? = java.util.ArrayList()
        ): Intent {
            return Intent(context, ReviewAmendsActivity::class.java).apply {
                putExtra(EXTRA_AMEND_INPUT, input)
                putExtra(IS_ECI_LCO_BOOKING, isEciLcoBooking)
                    putExtra(TEMP_BASKET_REF, tempBasketRef)
                    putExtra(UUID_BASKET_REFERENCE, uuidBasketReference)
                    putExtra(TOKEN, token)
                    putExtra(AMEND_SUMMARY_DOMAIN, amendSummaryDomain)
                    amendSummaryDomain?.let {
                        it.paymentOptions?.let { paymentOptions ->
                            if (paymentOptions.payNow == true) {
                                putExtra(PAY_NOW_FLOW, true)
                            }
                        }
                    }
                    listOfEciLcoExtras?.let {
                        if (it.isNotEmpty()) {
                            putExtra(IS_ECI_LCO_BOOKING, true)
                            putParcelableArrayListExtra(UPSELLS_EXTRAS_LIST, listOfEciLcoExtras)
                        }
                    }
            }
        }
    }
}