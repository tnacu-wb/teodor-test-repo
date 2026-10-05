package com.whitbread.premierinn.ciol.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whitbread.premierinn.ciol.analytics.logCiolAnalytics
import com.whitbread.premierinn.ciol.entity.PreStayUiModel
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Action.LEAVE_EASY_CONFIRMATION_ACTION
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.ScreenState.LEAVE_EASY_CONFIRMATION
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Type.LEAVE_EASY_FLOW
import com.whitbread.premierinn.common.analytics.TrackingAnalytics
import com.whitbread.premierinn.domain.graphql.ciol.usecase.ConfirmPreCheckOutUseCase
import com.whitbread.premierinn.domain.result.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PreCheckOutConfirmationViewModel @Inject constructor(
    private val preCheckOutUseCase: ConfirmPreCheckOutUseCase,
    private val trackingAnalytics: TrackingAnalytics
): ViewModel() {

    private val _state = MutableStateFlow(PreCheckOutConfirmationState())
    val state: StateFlow<PreCheckOutConfirmationState> = _state.asStateFlow()

    data class PreCheckOutConfirmationState(
            val isConfirmed: Boolean = false,
            val isError: Boolean = false,
            val isLoading: Boolean = false
    )

    fun onConfirmCheckOut(basketReference: String, preStayUiModel: PreStayUiModel?) {
        viewModelScope.launch {
            preCheckOutUseCase.invoke(basketReference)
                    .onStart {
                        _state.update { it.copy(isConfirmed = false, isError = false, isLoading = true) }
                    }
                    .collect { result ->
                        logCiolAnalytics(
                            analytics = trackingAnalytics,
                            preStayModel = preStayUiModel,
                            screenName = LEAVE_EASY_CONFIRMATION,
                            action = LEAVE_EASY_CONFIRMATION_ACTION,
                            screenType = LEAVE_EASY_FLOW,
                            isError = result is Result.Error
                        )
                        when(result) {
                            is Result.Success -> {
                                _state.update { it.copy(isConfirmed = true, isLoading = false) }
                            }
                            is Result.Error -> {
                                _state.update { it.copy(isError = true, isLoading = false) }
                            }
                    }
            }
        }
    }
}
