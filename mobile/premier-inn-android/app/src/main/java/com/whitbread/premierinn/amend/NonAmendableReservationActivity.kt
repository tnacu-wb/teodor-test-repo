package com.whitbread.premierinn.amend

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.core.view.isVisible
import com.whitbread.premierinn.R
import com.whitbread.premierinn.amend.NonAmendableReservationViewModel.NonAmendableReservationEvent.CancelBookingEvent
import com.whitbread.premierinn.amend.NonAmendableReservationViewModel.NonAmendableReservationEvent.GenericErrorEvent
import com.whitbread.premierinn.amend.NonAmendableReservationViewModel.NonAmendableReservationEvent.ShowCancelBookingDialog
import com.whitbread.premierinn.common.AutoCompositeDisposable
import com.whitbread.premierinn.common.activity.BaseActivity
import com.whitbread.premierinn.common.addTo
import com.whitbread.premierinn.common.managebooking.ManageBookingInput
import com.whitbread.premierinn.common.service.LogService
import com.whitbread.premierinn.databinding.ActivityNonAmendableBookingBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NonAmendableReservationActivity : BaseActivity<ActivityNonAmendableBookingBinding>() {

    private val disposable: AutoCompositeDisposable by lazy { AutoCompositeDisposable(lifecycle) }
    private val crashlyticsLogger = LogService()

    private val viewModel: NonAmendableReservationViewModel by viewModels()

    override fun inflateBinding(inflater: LayoutInflater): ActivityNonAmendableBookingBinding {
        return ActivityNonAmendableBookingBinding.inflate(inflater)
    }

    override fun getToolbar(): Toolbar? {
       return binding.toolbar
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolbar(resources.getString(R.string.non_amendable_reservation_toolbar_title), true)

        binding.progress.indeterminateDrawable.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(R.color.white, BlendModeCompat.SRC_IN)
        binding.hotelName.text = intent.getStringExtra(EXTRA_HOTEL_NAME)!!

        binding.cancelBtn.setOnClickListener { viewModel.onCancelClicked() }

        viewModel.events()
                .subscribe {
                    when (it) {
                        is GenericErrorEvent -> {
                            crashlyticsLogger.logException(it.error)
                            showToast(getString(R.string.generic_error_description))
                        }
                        is ShowCancelBookingDialog -> showCancelBookingDialog()
                        is CancelBookingEvent -> {
                            viewModel.refreshDashboard()
                            setResult(Activity.RESULT_OK)
                            finish()
                        }
                    }
                }.addTo(disposable)

        viewModel.states()
                .distinctUntilChanged()
                .subscribe(::render)
                .addTo(disposable)
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

    private fun render(state: NonAmendableReservationState) {
        binding.progress.isVisible = state.isLoading

        state.cancelBookingInProgress.let {
            binding.cancelBtn.setLoadingState(it)
        }

        binding.cancelBtn.isEnabled = state.cancellable == true
    }

    companion object {
        const val EXTRA_NON_AMENDABLE_INPUT = "non_amendable_reservation_input"
        const val NIGHTS_COUNT = "nights_count"
        const val ROOM_CRITERIA_SIZE = "room_criteria_size"
        const val UUID_BASKET_REFERENCE = "uuid_basket_reference"
        const val EXTRA_HOTEL_NAME = "non_amendable_reservation_hotel_name"
        const val TOKEN = "token"

        @JvmStatic
        fun createIntent(
            context: Context,
            input: ManageBookingInput,
            uuidBasketReference: String,
            hotelName: String,
            token: String,
            nightsCount: Int,
            roomCriteriaSize: Int
        ): Intent {
            return Intent(context, NonAmendableReservationActivity::class.java).apply {
                putExtra(EXTRA_NON_AMENDABLE_INPUT, input)
                putExtra(NIGHTS_COUNT, nightsCount)
                putExtra(ROOM_CRITERIA_SIZE, roomCriteriaSize)
                putExtra(UUID_BASKET_REFERENCE, uuidBasketReference)
                putExtra(EXTRA_HOTEL_NAME, hotelName)
                putExtra(TOKEN, token)
            }
        }
    }
}
