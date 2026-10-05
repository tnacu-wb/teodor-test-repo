package com.whitbread.premierinn.ciol.fragments

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.whitbread.premierinn.R
import com.whitbread.premierinn.ciol.CheckInOnlineActivity
import com.whitbread.premierinn.ciol.adapter.RoomSelectionAdapter
import com.whitbread.premierinn.ciol.entity.PreStayUiModel
import com.whitbread.premierinn.ciol.entity.upsells.UpsellEntry
import com.whitbread.premierinn.ciol.entity.upsells.UpsellItem
import com.whitbread.premierinn.ciol.entity.upsells.details.BookingConfirmationUiModel
import com.whitbread.premierinn.ciol.entity.upsells.details.RoomSelection
import com.whitbread.premierinn.ciol.entity.upsells.details.UpsellDetailsModel
import com.whitbread.premierinn.ciol.entity.upsells.details.UpsellType
import com.whitbread.premierinn.ciol.entity.upsells.details.convertToBookingConfirmationDomain
import com.whitbread.premierinn.ciol.entity.upsells.details.convertToBookingConfirmationUiModel
import com.whitbread.premierinn.ciol.entity.upsells.details.getNumberOfNights
import com.whitbread.premierinn.ciol.fragments.PreStayFragment.Companion.MODEL_KEY
import com.whitbread.premierinn.ciol.uimodel.PriceBreakdownModel
import com.whitbread.premierinn.ciol.utils.getId
import com.whitbread.premierinn.ciol.utils.getUpsellType
import com.whitbread.premierinn.ciol.utils.isKidsMeal
import com.whitbread.premierinn.ciol.viewmodel.RoomSelectionViewModel
import com.whitbread.premierinn.ciol.viewmodel.RoomSelectionViewModel.RoomSelectionState
import com.whitbread.premierinn.ciol.views.footer.PriceBreakdownView
import com.whitbread.premierinn.common.fragment.BaseFragment
import com.whitbread.premierinn.common.mapper.toParcelable
import com.whitbread.premierinn.databinding.FragmentRoomSelectionBinding
import com.whitbread.premierinn.domain.common.PriceDomain
import com.whitbread.premierinn.utils.parcelable
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

const val PRESELECTED_ROOM_SELECTION_KEY = "preselectedRoomSelectionKey"
const val ROOM_SELECTION_KEY = "roomSelectionKey"
const val BOOKING_CONFIRMATION_KEY = "bookingConfirmationKey"
const val UPSELL_ITEMS_KEY = "upsellItemsKey"
const val UPSELL_TYPE_KEY = "upsellTypeKey"

@AndroidEntryPoint
class RoomSelectionFragment : BaseFragment() {

    private val roomSelectionViewModel: RoomSelectionViewModel by viewModels()
    private var breakDownModel: PriceBreakdownModel =
        PriceBreakdownModel.createDefault()
    private var bookingConfirmation: BookingConfirmationUiModel? = null

    private lateinit var binding: FragmentRoomSelectionBinding
    private lateinit var roomSelectionItemsAdapter: RoomSelectionAdapter
    private lateinit var priceBreakdownView: PriceBreakdownView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRoomSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        val upsellItems = arguments?.getParcelableArrayList<UpsellItem>(UPSELL_ITEMS_KEY)
        val upsellEntry = upsellItems
            ?.filterIsInstance<UpsellEntry>()
            ?.filterNot { it.getId().isKidsMeal()}
            ?.find { it.getId().getUpsellType().name == arguments?.getString(UPSELL_TYPE_KEY) }

        bookingConfirmation =
            arguments?.parcelable<BookingConfirmationUiModel>(BOOKING_CONFIRMATION_KEY)
        roomSelectionViewModel.onScreenOpened(
            bookingConfirmation?.convertToBookingConfirmationDomain(),
            arguments?.getParcelableArrayList(PRESELECTED_ROOM_SELECTION_KEY),
            arguments?.getParcelableArrayList(ROOM_SELECTION_KEY),
            upsellItems,
            upsellEntry,
            arguments?.parcelable<PreStayUiModel>(MODEL_KEY)
        )

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                roomSelectionViewModel.state.collectLatest { state ->
                    onStateChanged(state)
                }
            }
        }
        setupPriceBreakdownView()
        setupRoomSelectionAdapter()
        setupRoomClickListener()
    }

    private fun setupToolbar() {
        (requireActivity() as CheckInOnlineActivity).setupToolbarTitle(
            requireContext().getString(R.string.ciol_upsells_toolbar_title)
        )
    }

    private fun setupRoomSelectionAdapter() {
        binding.roomSelectionRecyclerView.apply {
            roomSelectionItemsAdapter = RoomSelectionAdapter()
            this.adapter = roomSelectionItemsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun onStateChanged(state: RoomSelectionState) {
        var outstandingBalance = PriceDomain.createDefault()
        var numberOfNights = 0
        if (state.roomSelections.isNotEmpty()) {
            roomSelectionItemsAdapter.run {
                bookingConfirmation = state.bookingConfirmation?.convertToBookingConfirmationUiModel()
                preselectedRoomSelections = state.preselectedRoomSelections
                roomSelections = state.roomSelections
                upsellItems = state.upsellItems
                upsellEntry = state.upsellEntry
                notifyDataSetChanged()
            }
        }
        bookingConfirmation?.let {
            outstandingBalance = PriceDomain(it.balanceOutstanding ?: 0f, it.currencyCode)
            numberOfNights = it.reservationByIdList.first().roomStay.getNumberOfNights()
        }

        breakDownModel = PriceBreakdownModel(
            outstandingBalance.toParcelable(),
            state.roomSelections,
            numberOfNights
        )

        priceBreakdownView.displayPriceBreakdown(breakDownModel, null)

        setupContinueButton()
        state.upsellDetails?.let(::showUpsellDetailsBottomSheet)
    }

    private fun setupRoomClickListener() {
        roomSelectionItemsAdapter.onRoomClicked = { upsellEntry, roomSelection ->
            roomSelectionViewModel.onRoomClicked(upsellEntry, roomSelection.reservationId,
                String.format(requireContext().getString(R.string.room_number), roomSelectionItemsAdapter.roomSelections.indexOf(roomSelection) + 1))
        }
    }

    private fun setupPriceBreakdownView() {
        priceBreakdownView = PriceBreakdownView(requireContext()).apply {
            binding.footerContainer.addView(this)
            setupProgressBar(UPSELLS_TOOLBAR_PROGRESS)
        }
    }

    private fun setupContinueButton() {
        priceBreakdownView.getContinueButton().apply {
            this.setLoadingState(false)
        }
        priceBreakdownView.setupContinueButtonClickListener {
            roomSelectionViewModel.state.value.bookingConfirmation?.let {
                parentFragmentManager.setFragmentResult(
                    ROOM_SELECTION_REQUEST_KEY,
                    bundleOf(
                        ROOM_SELECTION_KEY to ArrayList(roomSelectionViewModel.state.value.roomSelections)
                    )
                )
                parentFragmentManager.popBackStack()
            }
        }
    }

    private fun showUpsellDetailsBottomSheet(upsellDetails: UpsellDetailsModel) {
        UpsellDetailsBottomSheet().apply {
            arguments = Bundle().apply {
                putParcelable(UPSELL_DETAILS_MODEL_KEY, upsellDetails)
            }
            show(this@RoomSelectionFragment.requireActivity().supportFragmentManager) { roomSelections ->
                roomSelectionViewModel.onUpsellDetailsClosed(roomSelections)
            }
        }
        roomSelectionViewModel.onActionPerformed()
    }

    companion object {
        fun newInstance(
            bookingConfirmation: BookingConfirmationUiModel,
            preselectedRoomSelections: ArrayList<RoomSelection>,
            roomSelections: ArrayList<RoomSelection>,
            upsellItems: ArrayList<UpsellItem>,
            upsellType: UpsellType,
            preStayUiModel: Parcelable?) =
            Bundle().apply {
                putParcelable(BOOKING_CONFIRMATION_KEY, bookingConfirmation)
                putParcelableArrayList(PRESELECTED_ROOM_SELECTION_KEY, preselectedRoomSelections)
                putParcelableArrayList(ROOM_SELECTION_KEY, roomSelections)
                putParcelableArrayList(UPSELL_ITEMS_KEY, upsellItems)
                putString(UPSELL_TYPE_KEY, upsellType.name)
                putParcelable(MODEL_KEY, preStayUiModel)
            }.let { bundle ->
                RoomSelectionFragment().apply {
                    arguments = bundle
                }
            }
    }
}
