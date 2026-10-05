package com.whitbread.premierinn.ciol.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.whitbread.premierinn.R
import com.whitbread.premierinn.ciol.CheckInOnlineActivity
import com.whitbread.premierinn.ciol.adapter.UpsellItemsAdapter
import com.whitbread.premierinn.ciol.entity.AuthorizeCardWebViewResponse
import com.whitbread.premierinn.ciol.entity.RegCardPdfModel
import com.whitbread.premierinn.ciol.entity.upsells.MealUiModel
import com.whitbread.premierinn.ciol.entity.upsells.details.RoomSelection
import com.whitbread.premierinn.ciol.mapper.convertToHotelPackagesRequestBody
import com.whitbread.premierinn.ciol.mapper.convertToUiModel
import com.whitbread.premierinn.ciol.uimodel.InfoBottomSheetData
import com.whitbread.premierinn.ciol.utils.isKidsMeal
import com.whitbread.premierinn.ciol.utils.showCheckInInformationBottomSheet
import com.whitbread.premierinn.ciol.utils.showErrorAlertDialog
import com.whitbread.premierinn.ciol.viewmodel.PreStaySharedViewModel
import com.whitbread.premierinn.ciol.viewmodel.UpsellsViewModel
import com.whitbread.premierinn.ciol.viewmodel.UpsellsViewModel.NavigationDestination
import com.whitbread.premierinn.ciol.viewmodel.UpsellsViewModel.UpsellAction
import com.whitbread.premierinn.ciol.viewmodel.UpsellsViewModel.UpsellAction.GoToSelectRoom
import com.whitbread.premierinn.ciol.viewmodel.UpsellsViewModel.UpsellAction.ShowUpsellDetailsBottomSheet
import com.whitbread.premierinn.ciol.viewmodel.UpsellsViewModel.UpsellsState
import com.whitbread.premierinn.ciol.views.footer.PriceBreakdownView
import com.whitbread.premierinn.common.fragment.BaseFragment
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.databinding.FragmentUpsellsBinding
import com.whitbread.premierinn.domain.common.entity.isPibaCNPBooking
import com.whitbread.premierinn.threeCp.ThreeCpActivity
import com.whitbread.premierinn.utils.parcelable
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

const val UPSELLS_TOOLBAR_PROGRESS = 50
const val UPSELLS_FEATURE_TAG = "Upsells"
const val SPECIAL_OCCASION = "special_occasion_label"
const val REG_CARD_PDF_MODEL = "reg_card_pdf_model"
const val ANALYTICS_MODEL = "ciol_completion_analytics_model"
const val UPDATE_RESERVATION_PACKAGES_MODEL_KEY = "UPDATE_RESERVATION_PACKAGES_MODEL"
const val ROOM_SELECTION_REQUEST_KEY = "requestKey"
const val PAY_AND_CHECK_IN_FRAGMENT_REQUEST_KEY = "PAY_AND_CHECK_IN_FRAGMENT"
private const val AUTHORIZE_CARD_REQUEST_CODE = 1
private const val AUTHORIZE_CARD_SUCCESS = "SUCCESS"

@AndroidEntryPoint
class UpsellsFragment : BaseFragment() {
    private val sharedViewModel: PreStaySharedViewModel by activityViewModels()

    private val upsellsViewModel: UpsellsViewModel by viewModels()

    private lateinit var binding: FragmentUpsellsBinding
    private lateinit var upsellItemsAdapter: UpsellItemsAdapter
    private lateinit var priceBreakdownView: PriceBreakdownView

    private var specialOccasion: String = EMPTY_STRING

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUpsellsBinding.inflate(inflater, container, false)
        upsellsViewModel.initParams(sharedViewModel.preStayModel.convertToHotelPackagesRequestBody(sharedViewModel.deviceLocaleProvider))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        val regCardPdfModel = arguments?.parcelable<RegCardPdfModel>(REG_CARD_PDF_MODEL)
        specialOccasion = arguments?.getString(SPECIAL_OCCASION, EMPTY_STRING) ?: EMPTY_STRING
        upsellsViewModel.onScreenOpened(sharedViewModel.preStayModel, specialOccasion, regCardPdfModel)

        parentFragmentManager.setFragmentResultListener(ROOM_SELECTION_REQUEST_KEY, this) { _, bundle ->
            bundle.getParcelableArrayList<RoomSelection>(ROOM_SELECTION_KEY)?.let { ArrayList(it) }?.let { roomSelections ->
                upsellsViewModel.onUpsellDetailsClosed(roomSelections)
            } ?: run {
                Log.w(UPSELLS_FEATURE_TAG, "Room Selection Screen returned a null roomSelections")
            }
        }

        parentFragmentManager.setFragmentResultListener(PAY_AND_CHECK_IN_FRAGMENT_REQUEST_KEY, this) { _, bundle ->
            bundle.getBoolean(UPSELLS_ADDED_REMOTELY_KEY).let { wereUpsellsAddedRemotely ->
                if (wereUpsellsAddedRemotely) {
                    upsellsViewModel.refreshScreenComponents(sharedViewModel.preStayModel.preStayDetails.numberOfAdults)
                }
            }
        }

        setupUpsellItemsAdapter()
        setupUpsellClickListener()
        setupPriceBreakdownView()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                upsellsViewModel.state.collectLatest { state ->
                    onStateChanged(state)
                }
            }
        }
    }

    private fun setupToolbar() {
        (requireActivity() as CheckInOnlineActivity).setupToolbarTitle(
            requireContext().getString(R.string.ciol_upsells_toolbar_title)
        )
    }

    private fun setupUpsellItemsAdapter() {
        binding.upsellsRecyclerView.apply {
            upsellItemsAdapter = UpsellItemsAdapter()
            this.adapter = upsellItemsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupUpsellClickListener() {
        upsellItemsAdapter.onUpsellClicked = { upsellEntry ->
            upsellsViewModel.onUpsellClicked(upsellEntry, sharedViewModel.preStayModel.bookingReference)
        }
    }

    private fun setupPriceBreakdownView() {
        priceBreakdownView = PriceBreakdownView(requireContext()).apply {
            binding.footerContainer.addView(this)
            setupProgressBar(UPSELLS_TOOLBAR_PROGRESS)
        }
    }

    private fun setupContinueButton(isLoadingDisplayed: Boolean) {
        val isPibaCNP = sharedViewModel.preStayModel.paymentOption.isPibaCNPBooking()
        val isPibaCNPFlow = isPibaCNP && upsellsViewModel.state.value.preselectedRoomSelections.any { it.selectedUpsells.isNotEmpty() }
        priceBreakdownView.getContinueButton().apply {
            this.setLoadingState(isLoadingDisplayed)
        }
        priceBreakdownView.setupContinueButtonClickListener {
            if (isPibaCNPFlow) {
                val infoBottomSheetData = InfoBottomSheetData.showPibaMessageData(resources)
                activity?.showCheckInInformationBottomSheet(infoBottomSheetData) { upsellsViewModel.onContinueButtonClicked(sharedViewModel.preStayModel) }
            } else {
                upsellsViewModel.onContinueButtonClicked(sharedViewModel.preStayModel)
            }
        }
    }

    private fun onStateChanged(state: UpsellsState) {
        val shouldHideAddUpsellsSection = !sharedViewModel.preStayModel.upsellsAddonsEnabled
        binding.addonsDescription.isVisible = shouldHideAddUpsellsSection && !state.isLoading

        binding.upsellsProgress.apply {
            if (state.isLoading) show() else hide()
        }
        setupContinueButton(isLoadingDisplayed = state.isLoading)
        state.priceBreakdownModel?.let {
            priceBreakdownView.displayPriceBreakdown(
                priceBreakdownModel = it,
                action = ::trackExpandButton,
                paymentOption = sharedViewModel.preStayModel.paymentOption,
                isPibaCpEnabled = upsellsViewModel.state.value.isPibaCpEnabled
            )
        }
        if (state.upsellItems.isNotEmpty()) {
            // Remove kids meal as it cannot be selected independently
            val items = state.upsellItems.toMutableList()
            items.removeIf { it is MealUiModel && it.id.isKidsMeal() }
            upsellItemsAdapter.run {
                isLoadingDisplayed = state.isLoading
                upsellItems = items
                isMultiRoomBooking = state.isMultiRoomBooking
                preselectedRoomSelections = state.preselectedRoomSelections.map { it.copy() }
                roomSelections = state.roomSelections.map { it.copy() }
                totalRoomsAdults = state.totalRoomsAdults
                deviceLocale = state.deviceLocale
                this.shouldHideAddUpsellsSection = shouldHideAddUpsellsSection
                notifyDataSetChanged()
            }
        }

        state.navigation?.let(::navigateTo)

        state.error?.let(::handleError)

        state.action?.let(::handleUpsellAction)
    }

    private fun handleError(error: UpsellsViewModel.Error) {
        if (error is UpsellsViewModel.Error.GenericError) {
            if (error.isPibaCnp) {
                showErrorAlertDialog(
                    getString(R.string.piba_error_dialog_title),
                    getString(R.string.piba_error_dialog_message)
                )
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.generic_error_message_with_try_again),
                    LENGTH_LONG
                ).show()
            }
            upsellsViewModel.onErrorDisplayed()
        }
    }

    private fun trackExpandButton(priceAmount: Double) {
        upsellsViewModel.trackExpandButton(priceAmount)
    }

    private fun handleUpsellAction(action: UpsellAction) {
        when (action) {
            is GoToSelectRoom -> {
                (activity as CheckInOnlineActivity).supportFragmentManager.beginTransaction()
                    .replace(
                        R.id.container,
                        RoomSelectionFragment.newInstance(
                            action.bookingConfirmation,
                            ArrayList(action.preselectedRoomSelections),
                            ArrayList(action.roomSelections),
                            ArrayList(action.upsellItems),
                            action.upsellType,
                            sharedViewModel.preStayModel.convertToUiModel()
                        )
                    )
                    .addToBackStack(null)
                    .commit()
            }
            is ShowUpsellDetailsBottomSheet -> {
                UpsellDetailsBottomSheet().apply {
                    arguments = Bundle().apply {
                        putParcelable(UPSELL_DETAILS_MODEL_KEY, action.upsellDetails)
                    }
                    show(this@UpsellsFragment.requireActivity().supportFragmentManager) { roomSelections ->
                        upsellsViewModel.onUpsellDetailsClosed(roomSelections)
                    }
                }
            }
        }
        upsellsViewModel.onActionPerformed()
    }

    private fun navigateTo(destination: NavigationDestination) {
        when (destination) {
            is NavigationDestination.CompletionScreen -> {
                (requireActivity() as CheckInOnlineActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.container, CheckInCompletionFragment.newInstance(
                        destination.completionModel))
                    .addToBackStack(null)
                    .commit()
            }

            is NavigationDestination.PayAndCheckInFragment ->
                (activity as CheckInOnlineActivity).supportFragmentManager.beginTransaction()
                    .replace(
                        R.id.container,
                        PayAndCheckInFragment.newInstance(
                            destination.preStayModel?.convertToUiModel(),
                            destination.analyticsModel,
                            specialOccasion,
                            destination.regCardPdfModel,
                            destination.updateReservationPackagesUiModel
                        )
                    )
                    .addToBackStack(null)
                    .commit()

            is NavigationDestination.AuthorizeCardScreen -> {
                startActivityForResult(
                    ThreeCpActivity.createThreeCpIntentAuthorizeCard(requireContext(), destination.data),
                    AUTHORIZE_CARD_REQUEST_CODE
                )
            }
        }
        upsellsViewModel.onUserNavigated()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            AUTHORIZE_CARD_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val response = data?.extras?.parcelable<AuthorizeCardWebViewResponse>(ThreeCpActivity.AUTHORIZE_CARD_RESPONSE_EXTRA)
                    if (AUTHORIZE_CARD_SUCCESS.equals(response?.authorizationStatus, true)) {
                        upsellsViewModel.authorizeCardCompletedSuccessfully(
                            response?.transactionId ?: EMPTY_STRING,
                            sharedViewModel.preStayModel
                        )
                    } else {
                        Toast.makeText(requireContext(), getString(R.string.start_ciol_generic_error), LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(requireContext(), getString(R.string.start_ciol_generic_error), LENGTH_LONG).show()
                }
            }
        }
    }

    companion object {
        fun newInstance(specialOccasion: String? = null, regCardPdfModel: RegCardPdfModel? = null) =
            UpsellsFragment().apply {
                arguments = Bundle().apply {
                    specialOccasion?.let {
                        putString(SPECIAL_OCCASION, it)
                    }

                    regCardPdfModel?.let {
                        putParcelable(REG_CARD_PDF_MODEL, it)
                    }
                }
            }
    }
}
