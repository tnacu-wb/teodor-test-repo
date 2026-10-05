package com.whitbread.premierinn.ciol.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.whitbread.premierinn.ciol.analytics.CiolAnalyticsData
import com.whitbread.premierinn.ciol.analytics.CiolAnalyticsModel
import com.whitbread.premierinn.ciol.entity.upsells.BreakfastUiModel
import com.whitbread.premierinn.ciol.entity.upsells.ExtrasItemUiModel
import com.whitbread.premierinn.ciol.entity.upsells.MealUiModel
import com.whitbread.premierinn.ciol.entity.upsells.MenuUiModel
import com.whitbread.premierinn.ciol.entity.upsells.copy
import com.whitbread.premierinn.ciol.entity.upsells.details.UpsellDetailsModel
import com.whitbread.premierinn.ciol.entity.upsells.details.UpsellType.BREAKFAST
import com.whitbread.premierinn.ciol.entity.upsells.details.UpsellType.ECI
import com.whitbread.premierinn.ciol.entity.upsells.details.UpsellType.LCO
import com.whitbread.premierinn.ciol.entity.upsells.details.UpsellType.MEAL_DEAL
import com.whitbread.premierinn.ciol.entity.upsells.details.UpsellType.WIFI
import com.whitbread.premierinn.ciol.fragments.UPSELLS_FEATURE_TAG
import com.whitbread.premierinn.ciol.fragments.UPSELL_DETAILS_MODEL_KEY
import com.whitbread.premierinn.ciol.uilogic.ComputeMealSelectionRules
import com.whitbread.premierinn.ciol.utils.getId
import com.whitbread.premierinn.ciol.utils.getNumberOfSelections
import com.whitbread.premierinn.ciol.utils.getUpsellType
import com.whitbread.premierinn.ciol.utils.isKidsMeal
import com.whitbread.premierinn.ciol.utils.updateNumberOfSelections
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.ScreenState.ALLERGENS_SELECTED
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.ScreenState.MENU_SELECTED
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.ScreenState.UPSELL_DETAILS
import com.whitbread.premierinn.common.analytics.TrackingAnalytics
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class UpsellDetailsBottomSheetViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val deviceLocaleProvider: DeviceLocaleProvider,
    private val computeMealSelectionRules: ComputeMealSelectionRules,
    private val trackingAnalytics: TrackingAnalytics
) : ViewModel() {
    private val initialState: UpsellDetailsModel by lazy {
        requireNotNull(savedStateHandle.get<UpsellDetailsModel>(UPSELL_DETAILS_MODEL_KEY))
    }
    private val _state = MutableStateFlow(
        UpsellDetailsState(
            upsellDetailsModel = initialState,
            shouldDisplayContinueButton = getInitialNumberOfSelectedUpsells(initialState) == 0,
            shouldDisplayRemoveButton = getInitialNumberOfSelectedUpsells(initialState) > 0
        )
    )
    val state: StateFlow<UpsellDetailsState> = _state

    data class UpsellDetailsState(
        val upsellDetailsModel: UpsellDetailsModel,
        val shouldDisplayContinueButton: Boolean,
        val shouldDisplayRemoveButton: Boolean,
        val closeBottomSheet: Boolean = false,
        val action: UpsellDetailsAction? = null,
    )

    sealed class UpsellDetailsAction {
        data class ShowAllergensGuide(val allergensUrl: String) : UpsellDetailsAction()
        data class ShowMenu(val menuUrl: String) : UpsellDetailsAction()
        data class ShowMenuSelectionBottomSheet(val menus: List<MenuUiModel>) :
            UpsellDetailsAction()
    }

    fun onScreenOpened(upsellName: String) {
        trackingAnalytics.track(
            UPSELL_DETAILS,
            CiolAnalyticsData(
                CiolAnalyticsModel(
                    bookingId = _state.value.upsellDetailsModel.bookingId,
                    extraDescription = upsellName
                )
            )
        )
    }

    fun onMenuOpened() {
        trackingAnalytics.track(
            MENU_SELECTED,
            CiolAnalyticsData(CiolAnalyticsModel(bookingId = _state.value.upsellDetailsModel.bookingId))
        )
    }

    fun onAllergensOpened() {
        trackingAnalytics.track(
            ALLERGENS_SELECTED,
            CiolAnalyticsData(CiolAnalyticsModel(bookingId = _state.value.upsellDetailsModel.bookingId))
        )
    }

    fun getDeviceLocale() = deviceLocaleProvider.getDeviceLocale()

    fun retrieveMealSelectionRules(currentUpsell: MealUiModel) =
        _state.value.upsellDetailsModel.run {
            computeMealSelectionRules.invoke(
                currentUpsell = currentUpsell,
                availableUpsells = availableUpsells,
                roomSelection = roomSelections.first(),
                roomAdults = roomStay.first().adultsNumber.toInt(),
                roomChildren = roomStay.first().childrenNumber.toInt()
            )
        }

    fun onAddButtonClicked() {
        when (_state.value.upsellDetailsModel.upsellType) {
            BREAKFAST, MEAL_DEAL -> {
                _state.update { it.copy(closeBottomSheet = true) }
            }

            ECI, LCO, WIFI -> {
                // For ECI/LCO roomSelections will contain all rooms, for WIFI only 1 room
                _state.value.upsellDetailsModel.availableUpsells.first().let { upsell ->
                    upsell.updateNumberOfSelections(upsell.getNumberOfSelections() + 1)
                    val updatedRoomSelections =
                        _state.value.upsellDetailsModel.roomSelections.map { it.copy() }
                    updatedRoomSelections.forEach { roomSelection ->
                        roomSelection.selectedUpsells.add(upsell)
                    }

                    _state.update {
                        it.copy(
                            upsellDetailsModel = it.upsellDetailsModel.copy(
                                roomSelections = updatedRoomSelections
                            ),
                            closeBottomSheet = true
                        )
                    }
                }
            }
        }
    }

    fun onRemoveButtonClicked() {
        val updatedRoomSelections = _state.value.upsellDetailsModel.roomSelections.map { it.copy() }
        // Upon removal, always set the numberOfSelections to 0 instead of removing the upsell,
        // in order for the upsells to properly update in Upsell screen
        when (val upsellType = _state.value.upsellDetailsModel.upsellType) {
            BREAKFAST, MEAL_DEAL, WIFI -> {
                // For MealDeal, Breakfast and WiFi we always have 1 room selection in the list
                updatedRoomSelections.first().selectedUpsells
                    .filter { it.getId().getUpsellType() == upsellType }
                    .forEach { upsell ->
                        upsell.updateNumberOfSelections(0)
                        if (upsell is MealUiModel) {
                            upsell.freeBreakfastSelections = 0
                            updatedRoomSelections.first().selectedUpsells
                                .firstOrNull { selectedUpsell ->
                                    selectedUpsell.getId().isKidsMeal()
                                }?.let { breakfastUpsellFromMeal ->
                                    (breakfastUpsellFromMeal as MealUiModel).noOfSelections = 0
                                }
                        }
                    }
            }

            ECI, LCO -> {
                // As ECI and LCO are applicable for all rooms, room selections will have multiple entries
                updatedRoomSelections
                    .flatMap { it.selectedUpsells }
                    .filter { upsell -> upsell.getId().getUpsellType() == upsellType }
                    .forEach { upsell -> upsell.updateNumberOfSelections(0) }
            }
        }

        _state.update {
            it.copy(
                upsellDetailsModel = it.upsellDetailsModel.copy(
                    roomSelections = updatedRoomSelections
                ),
                closeBottomSheet = true
            )
        }
    }

    fun onDialogDismissed() {
        _state.update {
            it.copy(
                closeBottomSheet = false
            )
        }
    }

    fun onMenuButtonClicked() {
        _state.value.upsellDetailsModel.availableUpsells
            .filterIsInstance<MealUiModel>()
            .mapNotNull { it.menu }
            .distinct()
            .let { menus ->
                if (menus.isEmpty()) {
                    Log.w(
                        UPSELLS_FEATURE_TAG,
                        "No menus found for upsell type: ${state.value.upsellDetailsModel.upsellType}"
                    )
                    return@let
                }

                when (menus.size) {
                    1 -> UpsellDetailsAction.ShowMenu(menus.first().menuSrc)
                    else -> UpsellDetailsAction.ShowMenuSelectionBottomSheet(menus)
                }.let { action ->
                    _state.update {
                        it.copy(action = action)
                    }
                }
            }
    }

    fun onAllergensButtonClicked() = _state.value.upsellDetailsModel.availableUpsells
        .filterIsInstance<MealUiModel>()
        .map { it.allergyInfoSrc }
        .firstOrNull { it != null }
        ?.let { allergyInfoSrc ->
            _state.update {
                it.copy(
                    action = UpsellDetailsAction.ShowAllergensGuide(allergyInfoSrc)
                )
            }
        } ?: run {
        Log.w(
            UPSELLS_FEATURE_TAG,
            "No valid allergyInfoSrc was found for upsell type: ${state.value.upsellDetailsModel.upsellType}"
        )
    }

    fun onActionPerformed() {
        _state.update { it.copy(action = null) }
    }

    fun onIncrementClicked(upsell: MealUiModel) {
        updateStateWithNumberOfSelections(upsell.copy(noOfSelections = upsell.noOfSelections + 1))
    }

    fun onDecrementClicked(upsell: MealUiModel) {
        updateStateWithNumberOfSelections(upsell.copy(noOfSelections = upsell.noOfSelections - 1))
    }

    /*
    This method can only be called for a MealUiModel. The other type of possible outsell:
    ExtraItemUiModel is added to the booking only when pressing on Add button
     */
    private fun updateStateWithNumberOfSelections(updatedUpsell: MealUiModel) {
        val availableUpsells = _state.value.upsellDetailsModel.availableUpsells.copy()

        availableUpsells.forEach { existingUpsell ->
            if (existingUpsell is MealUiModel && existingUpsell.id == updatedUpsell.id)
                existingUpsell.noOfSelections = updatedUpsell.noOfSelections
        }

        if (updatedUpsell.id.isKidsMeal()) {
            availableUpsells.firstOrNull { it is MealUiModel && it.freeBreakfastOption == true }
                ?.let { upsellWithFreeBf ->
                    (upsellWithFreeBf as MealUiModel).freeBreakfastSelections =
                        updatedUpsell.noOfSelections
                }
        }

        val updatedRoomSelections = _state.value.upsellDetailsModel.roomSelections.map { it.copy() }
        updatedRoomSelections.map { currentRoomSelection ->
            val currentUpsell = currentRoomSelection.selectedUpsells.find {
                when (it) {
                    is MealUiModel -> it.id == (updatedUpsell as? MealUiModel)?.id
                    is ExtrasItemUiModel, is BreakfastUiModel -> false
                }
            }

            if (currentUpsell != null) {
                // Upsell was already present, just update the number of selections
                (currentUpsell as MealUiModel).noOfSelections = updatedUpsell.noOfSelections
            } else {
                // Upsell isn't present because it was just selected, so add it to the list
                currentRoomSelection.selectedUpsells.add(updatedUpsell)
            }

            // If Kids meal selection changed, also update freeBreakfastSelections in the meal which
            // has the freeBreakfastOption true
            if (updatedUpsell.id.isKidsMeal()) {
                val upsellDetailsType = _state.value.upsellDetailsModel.upsellType
                currentRoomSelection.selectedUpsells
                    .firstOrNull {
                        it is MealUiModel && it.id.getUpsellType() == upsellDetailsType && it.freeBreakfastOption == true
                    }?.let { upsellWithFreeBreakfast ->
                        (upsellWithFreeBreakfast as MealUiModel).freeBreakfastSelections =
                            updatedUpsell.noOfSelections
                    }
            }

            return@map currentRoomSelection
        }

        _state.update {
            it.copy(
                upsellDetailsModel = it.upsellDetailsModel.copy(
                    roomSelections = updatedRoomSelections,
                    availableUpsells = availableUpsells
                ),
                // After interacting with +/- buttons, the Continue button should become visible
                shouldDisplayContinueButton = true
            )
        }

        checkIfChildrenSelectionIsCorrect()
    }

    private fun checkIfChildrenSelectionIsCorrect() {
        _state.value.upsellDetailsModel.apply {
            val adultMealSelections = this.availableUpsells
                .filter { it is MealUiModel && it.freeBreakfastOption == true }
                .sumOf { (it as MealUiModel).noOfSelections }

            (this.availableUpsells.firstOrNull { it is MealUiModel && it.id.isKidsMeal() } as? MealUiModel)
                ?.let { kidsMealUpsell ->
                    if (kidsMealUpsell.noOfSelections > 0 && adultMealSelections == 0) {
                        onDecrementClicked(kidsMealUpsell)
                    }
                }
        }
    }

    private fun getInitialNumberOfSelectedUpsells(initialState: UpsellDetailsModel): Int =
        initialState.availableUpsells.sumOf { it.getNumberOfSelections() }
}