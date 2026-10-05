package com.whitbread.premierinn.ciol.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.whitbread.premierinn.R
import com.whitbread.premierinn.api.Urls
import com.whitbread.premierinn.ciol.entity.upsells.ExtrasItemUiModel
import com.whitbread.premierinn.ciol.entity.upsells.MealUiModel
import com.whitbread.premierinn.ciol.entity.upsells.details.RoomSelection
import com.whitbread.premierinn.ciol.entity.upsells.details.UpsellDetailsModel
import com.whitbread.premierinn.ciol.entity.upsells.details.UpsellType.BREAKFAST
import com.whitbread.premierinn.ciol.entity.upsells.details.UpsellType.ECI
import com.whitbread.premierinn.ciol.entity.upsells.details.UpsellType.LCO
import com.whitbread.premierinn.ciol.entity.upsells.details.UpsellType.MEAL_DEAL
import com.whitbread.premierinn.ciol.entity.upsells.details.UpsellType.WIFI
import com.whitbread.premierinn.ciol.entity.upsells.details.getAnalyticsUpsellName
import com.whitbread.premierinn.ciol.utils.getButtonText
import com.whitbread.premierinn.ciol.utils.getImageUri
import com.whitbread.premierinn.ciol.utils.getMultiRoomDescription
import com.whitbread.premierinn.ciol.utils.getNumberOfSelections
import com.whitbread.premierinn.ciol.utils.getSingleRoomDescription
import com.whitbread.premierinn.ciol.utils.getTitle
import com.whitbread.premierinn.ciol.utils.getUpsellType
import com.whitbread.premierinn.ciol.utils.showPdfViaImplicitIntent
import com.whitbread.premierinn.ciol.viewmodel.UpsellDetailsBottomSheetViewModel
import com.whitbread.premierinn.ciol.viewmodel.UpsellDetailsBottomSheetViewModel.UpsellDetailsAction
import com.whitbread.premierinn.ciol.viewmodel.UpsellDetailsBottomSheetViewModel.UpsellDetailsAction.ShowAllergensGuide
import com.whitbread.premierinn.ciol.viewmodel.UpsellDetailsBottomSheetViewModel.UpsellDetailsAction.ShowMenu
import com.whitbread.premierinn.ciol.viewmodel.UpsellDetailsBottomSheetViewModel.UpsellDetailsAction.ShowMenuSelectionBottomSheet
import com.whitbread.premierinn.ciol.views.upsells.BreakfastUpsellDetailedEntryView
import com.whitbread.premierinn.ciol.views.upsells.ExtrasItemUpsellDetailedEntryView
import com.whitbread.premierinn.ciol.views.upsells.MealDealUpsellDetailedEntryView
import com.whitbread.premierinn.databinding.ViewUpsellDetailsBottomSheetBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

const val UPSELL_DETAILS_MODEL_KEY = "UPSELL_DETAILS_MODEL"

@AndroidEntryPoint
class UpsellDetailsBottomSheet : BottomSheetDialogFragment() {
    private lateinit var binding: ViewUpsellDetailsBottomSheetBinding

    private val upsellDetailsModel: UpsellDetailsModel by lazy { arguments?.getParcelable(UPSELL_DETAILS_MODEL_KEY)!! }
    private val upsellDetailsBottomSheetViewModel: UpsellDetailsBottomSheetViewModel by viewModels()

    private var onCompletionListener: ((List<RoomSelection>) -> Unit)? = null

    override fun getTheme() = R.style.AppBottomSheetDialogTheme

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ViewUpsellDetailsBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        upsellDetailsBottomSheetViewModel.onScreenOpened(upsellDetailsModel.getAnalyticsUpsellName(requireContext()))

        setupMenuAndAllergenRows()
        expandBottomSheet()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                upsellDetailsBottomSheetViewModel.state.collectLatest(::onStateUpdated)
            }
        }
    }

    private fun expandBottomSheet() {
        binding.root.apply {
            post {
                dialog?.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)?.let { dialogView ->
                    val behaviour = BottomSheetBehavior.from(dialogView)
                    behaviour.state = BottomSheetBehavior.STATE_EXPANDED
                }
            }
        }
    }

    private fun onStateUpdated(state: UpsellDetailsBottomSheetViewModel.UpsellDetailsState) {
        state.apply {
            setupHeader(upsellDetailsModel)
            setupBody(upsellDetailsModel)
            setupFooter(this)
            saveAndClose(this)
            action?.let(::handleAction)
        }
    }

    private fun setupHeader(state: UpsellDetailsModel) {
        binding.header.apply {
            upsellImageView.load(Urls.CONTENT_BASE_URL.plus(state.getImageUri()))
            upsellTitleTextView.text = state.getTitle(requireContext())
            upsellPriceInfoTextView.text = state.run {
                roomName?.let {
                    getMultiRoomDescription(requireContext(), upsellDetailsBottomSheetViewModel.getDeviceLocale())
                } ?: getSingleRoomDescription(requireContext(), upsellDetailsBottomSheetViewModel.getDeviceLocale())
            }
            upsellCloseImageView.setOnClickListener { this@UpsellDetailsBottomSheet.dismiss() }
        }
    }

    private fun setupFooter(state: UpsellDetailsBottomSheetViewModel.UpsellDetailsState) {
        setupContinueButton(state)
        setupRemoveButton(state)
    }

    private fun setupContinueButton(state: UpsellDetailsBottomSheetViewModel.UpsellDetailsState) {
        binding.footer.continueButton.apply {
            state.shouldDisplayContinueButton.let { displayContinueButton ->
                isVisible = displayContinueButton
                if (!displayContinueButton) return@apply
            }

            setText(
                state.upsellDetailsModel.getButtonText(
                    requireContext(),
                    upsellDetailsBottomSheetViewModel.getDeviceLocale()
                )
            )

            if (arrayOf(BREAKFAST, MEAL_DEAL).contains(state.upsellDetailsModel.upsellType)) {
                isEnabled = state.upsellDetailsModel.roomSelections
                    .flatMap { it.selectedUpsells }
                    .filter { it is MealUiModel && it.id.getUpsellType() == state.upsellDetailsModel.upsellType }
                    .sumOf { it.getNumberOfSelections() } != 0

                if (isEnabled) {
                    setButtonBackgroundColor(ContextCompat.getColor(context, R.color.teal_dark))
                    setButtonTextColor(ContextCompat.getColor(context, R.color.white))
                } else {
                    setButtonBackgroundColor(ContextCompat.getColor(context, R.color.grey_light_x))
                    setButtonTextColor(ContextCompat.getColor(context, R.color.light_grey_1))
                }
            }

            setOnClickListener { _ ->
                upsellDetailsBottomSheetViewModel.onAddButtonClicked()
            }
        }
    }

    private fun setupRemoveButton(state: UpsellDetailsBottomSheetViewModel.UpsellDetailsState) {
        binding.footer.removeButton.apply {
            isVisible = state.shouldDisplayRemoveButton

            if (state.shouldDisplayRemoveButton) {
                setText(
                    when (state.upsellDetailsModel.upsellType) {
                        BREAKFAST, MEAL_DEAL -> getString(R.string.upsells_remove_all_button_text)
                        else -> getString(R.string.criteria_remove_room)
                    }
                )
            }

            setOnClickListener { _ ->
                upsellDetailsBottomSheetViewModel.onRemoveButtonClicked()
            }
        }
    }

    private fun saveAndClose(state: UpsellDetailsBottomSheetViewModel.UpsellDetailsState) {
        if (state.closeBottomSheet) {
            state.upsellDetailsModel.apply {
                onCompletionListener?.invoke(roomSelections)
            }
            dismiss()
            upsellDetailsBottomSheetViewModel.onDialogDismissed()
        }
    }

    private fun setupBody(state: UpsellDetailsModel) {
        val views = state.availableUpsells.mapIndexed { index, upsellItem ->
            when (state.upsellType) {
                BREAKFAST ->
                    BreakfastUpsellDetailedEntryView(requireContext())
                        .initialize(
                            upsell = upsellItem as MealUiModel,
                            mealSelectionRules = upsellDetailsBottomSheetViewModel.retrieveMealSelectionRules(
                                upsellItem
                            ),
                            locale = upsellDetailsBottomSheetViewModel.getDeviceLocale(),
                            currentItemPosition = index,
                            totalItems = state.availableUpsells.size,
                            bookingContainsChildren = upsellDetailsModel.roomStay.any { it.childrenNumber.toInt() != 0 }
                        )
                        .onIncrement { upsellDetailsBottomSheetViewModel.onIncrementClicked(it) }
                        .onDecrement { upsellDetailsBottomSheetViewModel.onDecrementClicked(it) }

                MEAL_DEAL ->
                    MealDealUpsellDetailedEntryView(requireContext())
                        .initialize(
                            upsell = upsellItem as MealUiModel,
                            mealSelectionRules = upsellDetailsBottomSheetViewModel.retrieveMealSelectionRules(
                                upsellItem
                            ),
                            locale = upsellDetailsBottomSheetViewModel.getDeviceLocale(),
                            currentItemPosition = index,
                            totalItems = state.availableUpsells.size,
                            bookingContainsChildren = upsellDetailsModel.roomStay.any { it.childrenNumber.toInt() != 0 }
                        ).onIncrement { upsellDetailsBottomSheetViewModel.onIncrementClicked(it) }
                        .onDecrement { upsellDetailsBottomSheetViewModel.onDecrementClicked(it) }

                WIFI ->
                    ExtrasItemUpsellDetailedEntryView(requireContext())
                        .initialize(
                            upsellItem as ExtrasItemUiModel,
                            upsellDetailsBottomSheetViewModel.getDeviceLocale(),
                            state.adultsNames.isNotEmpty()
                        )

                ECI, LCO ->
                    ExtrasItemUpsellDetailedEntryView(requireContext())
                        .initialize(
                            upsellItem as ExtrasItemUiModel,
                            upsellDetailsBottomSheetViewModel.getDeviceLocale(),
                            false
                        )

            }
        }
        binding.upsellsContainer.removeAllViews()
        views.forEach {
            binding.upsellsContainer.addView(it)
        }
    }

    private fun setupMenuAndAllergenRows() {
        if (!arrayOf(BREAKFAST, MEAL_DEAL).contains(upsellDetailsModel.upsellType)) {
            binding.menuDetailsSeparator.isVisible = false
            binding.menuContainer.container.isVisible = false
            binding.allergenGuideContainer.container.isVisible = false
            return
        }

        binding.menuContainer.apply {
            entryNameTextView.text = getString(R.string.upsells_view_menu_text)
            entryImageView.setOnClickListener {
                upsellDetailsBottomSheetViewModel.onMenuButtonClicked()
            }
        }

        binding.allergenGuideContainer.apply {
            entryNameTextView.text = getString(R.string.upsells_view_allergen_guide_text)
            entryImageView.setOnClickListener {
                upsellDetailsBottomSheetViewModel.onAllergensButtonClicked()
            }
        }
    }

    private fun handleAction(action: UpsellDetailsAction) {
        when (action) {
            is ShowAllergensGuide ->
                showPdf(Urls.CONTENT_BASE_URL.plus(action.allergensUrl)) { errorOnOpening ->
                    if (errorOnOpening) {
                        showPdfFailedToOpenError()
                    } else {
                        upsellDetailsBottomSheetViewModel.onAllergensOpened()
                    }
                }

            is ShowMenu ->
                showPdf(Urls.CONTENT_BASE_URL.plus(action.menuUrl)) { errorOnOpening ->
                    if (errorOnOpening) {
                        showPdfFailedToOpenError()
                    } else {
                        upsellDetailsBottomSheetViewModel.onMenuOpened()
                    }
                }

            is ShowMenuSelectionBottomSheet -> {
                Bundle().apply {
                    putParcelableArrayList(MENU_SELECTION_EXTRA_MENUS_KEY, ArrayList(action.menus))
                }.let {
                    MenuSelectionBottomSheet().apply {
                        arguments = it
                        show(this@UpsellDetailsBottomSheet.requireActivity().supportFragmentManager) {errorWhenOpening: Boolean ->
                            if (errorWhenOpening) {
                                showPdfFailedToOpenError()
                            } else {
                                upsellDetailsBottomSheetViewModel.onMenuOpened()
                            }
                        }
                    }
                }
            }
        }
        upsellDetailsBottomSheetViewModel.onActionPerformed()
    }

    private fun showPdf(url: String, onPdfOpened: (errorOnOpening: Boolean) -> Unit) {
        showPdfViaImplicitIntent(pdfUrl = url, activity = requireActivity()) { errorOnOpening ->
            onPdfOpened.invoke(errorOnOpening)
        }
    }

    private fun showPdfFailedToOpenError() {
        Toast.makeText(
            requireContext(),
            requireContext().getString(R.string.review_booking_error_title),
            Toast.LENGTH_LONG
        ).show()
    }

    fun show(manager: FragmentManager, onCompletionListener: (List<RoomSelection>) -> Unit) {
        manager.commit(allowStateLoss = true) {
            add(this@UpsellDetailsBottomSheet, UpsellDetailsBottomSheet::class.java.simpleName)
            this@UpsellDetailsBottomSheet.onCompletionListener = onCompletionListener
        }
    }
}