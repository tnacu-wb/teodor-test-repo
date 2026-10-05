package com.whitbread.premierinn.ciol.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whitbread.premierinn.ciol.mapper.convertToUiModel
import com.whitbread.premierinn.ciol.uimodel.RoomKeyInstructionsModel
import com.whitbread.premierinn.common.utils.getDescriptionLabel
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.domain.common.COUNTRY_CODE_UK
import com.whitbread.premierinn.domain.graphql.ciol.usecase.GetRoomKeyInstructionsUseCase
import com.whitbread.premierinn.domain.graphql.requestBodyModels.CategoryLabelsRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.CategoryLabelsRequestBody.Companion.LABEL_TITLE
import com.whitbread.premierinn.domain.result.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class CheckInCompletionViewModel @Inject constructor(
    private val deviceLocaleProvider: DeviceLocaleProvider,
    private val roomKeyInstructionsUseCase: GetRoomKeyInstructionsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CheckInCompletionState())
    val state: StateFlow<CheckInCompletionState> = _state

    data class CheckInCompletionState(
        val roomKeyInstructionsModel: RoomKeyInstructionsModel? = null,
        val hotelImage: String? = null,
        val bookingReferenceId: String? = null,
        val openBottomSheet: Boolean = false,
        val isCountryCodeUK: Boolean = false,
        val isLoading: Boolean = false,
        val isError: Boolean = false,
        val hotelId: String? = null
    )

    fun onScreenOpened(hotelImage: String?, bookingReferenceId: String?, hotelId: String?) {
        _state.update {
            it.copy(
                hotelId = hotelId,
                hotelImage = hotelImage,
                bookingReferenceId = bookingReferenceId,
                isCountryCodeUK = deviceLocaleProvider.getCountryIfRegion(
                    deviceLocaleProvider.getDeviceLocale()
                ).lowercase(Locale.getDefault()) == COUNTRY_CODE_UK
            )
        }
    }

    fun onBottomSheetClosed() {
        _state.update { it.copy(openBottomSheet = false) }
    }

    fun onRoomKeyInstructionsClicked(hotelBrand: String) = viewModelScope.launch {
        _state.value.roomKeyInstructionsModel?.let {
            _state.update { it.copy(openBottomSheet = true) }
        } ?: run {
            onGetRoomKeyInstructions(hotelBrand)
        }
    }

    private fun onGetRoomKeyInstructions(hotelBrand: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            roomKeyInstructionsUseCase(CategoryLabelsRequestBody(
                deviceLocaleProvider.getCountryIfRegion(deviceLocaleProvider.getDeviceLocale()).lowercase(),
                deviceLocaleProvider.getDeviceLanguage().lowercase(), CategoryLabelsRequestBody.CATEGORY,
                listOf(LABEL_TITLE, hotelBrand.getDescriptionLabel())
            )).collect { result ->
                when(result) {
                    is Result.Success -> {
                        _state.update { it.copy(
                            isLoading = false,
                            roomKeyInstructionsModel = result.data.convertToUiModel(
                                _state.value.bookingReferenceId,
                                _state.value.hotelImage,
                                _state.value.hotelId
                            ),
                            openBottomSheet = true)
                        }
                    }

                    is Result.Error -> {
                        _state.update {
                            it.copy(isLoading = false, isError = true)
                        }
                    }
                }
            }
        }
    }
}
