package com.whitbread.premierinn.hoteldetails.discountcode.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.whitbread.premierinn.base.view.bottomsheet.FullScreenBottomSheetDialogFragment
import com.whitbread.premierinn.base.view.bottomsheet.NoMidDismissBackNavigationCallback
import com.whitbread.premierinn.domain.hotel.entity.HotelBookingAvailabilityState
import com.whitbread.premierinn.hoteldetails.discountcode.DiscountCodeBottomSheetEvent
import com.whitbread.premierinn.hoteldetails.discountcode.DiscountCodeConstants.BundleKeys
import com.whitbread.premierinn.hoteldetails.discountcode.DiscountCodeInput
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DiscountCodeBottomSheetFragment : FullScreenBottomSheetDialogFragment() {

    private val viewModel: DiscountCodeBottomSheetViewModel by viewModels()
    private var discountAppliedCallback: ((String, HotelBookingAvailabilityState, String?, String?) -> Unit)? = null
    private var discountErrorCallback: ((String, String?, String?) -> Unit)? = null

    fun setDiscountAppliedCallback(callback: (String, HotelBookingAvailabilityState, String?, String?) -> Unit) {
        this.discountAppliedCallback = callback
    }

    fun setDiscountErrorCallback(callback: (String, String?, String?) -> Unit) {
        this.discountErrorCallback = callback
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val state by viewModel.state.collectAsStateWithLifecycle()
                // Handle one-off events via state
                state.event?.let { event ->
                    when (event) {
                        is DiscountCodeBottomSheetEvent.UpdateHotelDetailsRates -> {
                            discountAppliedCallback?.invoke(
                                event.promoCode,
                                event.availabilityState,
                                event.successMessage,
                                event.promoKind
                            )
                        }
                        is DiscountCodeBottomSheetEvent.DiscountCodeError -> {
                            discountErrorCallback?.invoke(
                                event.promoCode,
                                event.errorMessage,
                                event.promoKind
                            )
                        }

                        is DiscountCodeBottomSheetEvent.DiscountCodeBottomSheetClosed -> {
                            closeDialog()
                        }
                    }
                    // Clear the event after handling
                    viewModel.onEventConsumed()
                }

                DiscountCodeBottomSheetContent(
                    state = state,
                    onAction = { action ->
                        viewModel.processAction(action)
                    }
                )
            }
        }
    }

    override fun getSheetCallback(bottomSheetDialog: BottomSheetDialog) =
        NoMidDismissBackNavigationCallback(bottomSheetDialog, this)

    companion object {
        const val TAG = BundleKeys.FRAGMENT_TAG

        fun newInstance(input: DiscountCodeInput): DiscountCodeBottomSheetFragment {
            return DiscountCodeBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(BundleKeys.INPUT_KEY, input)
                }
            }
        }
    }
}
