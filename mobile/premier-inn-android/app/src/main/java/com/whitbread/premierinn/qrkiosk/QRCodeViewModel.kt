package com.whitbread.premierinn.qrkiosk

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whitbread.premierinn.common.service.LogService
import com.whitbread.premierinn.common.utils.qrBitmap.QRCodeCreateBitmap
import com.whitbread.premierinn.domain.common.AppDispatchers
import com.whitbread.premierinn.domain.qrcode.GenerateQrCodeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class QRCodeViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val generateQrCodeUseCase: GenerateQrCodeUseCase,
    private val crashlyticsLogger: LogService,
    private val qrCodeCreateBitmap: QRCodeCreateBitmap,
    private val dispatchers: AppDispatchers
) : ViewModel() {

    private val bookingReference: String by lazy {
        savedStateHandle.get<String>(KIOSK_VIEW_INPUT) ?: ""
    }
    private val _state = MutableStateFlow(QRCodeState())
    val state = _state.asStateFlow()

    private var hasInitialised = false

    fun init() {
        if (hasInitialised) return
        hasInitialised = true
        initialiseState()
    }

    private fun initialiseState() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val bitmap = withContext(dispatchers.default) {
                    val result = generateQrCodeUseCase.invoke(bookingReference, 2000, 2000)
                    qrCodeCreateBitmap.convertToBitmap(result)
                }
                _state.update {
                    it.copy(isLoading = false, qrCode = bitmap, error = null)
                }
            } catch (e: Exception) {
                crashlyticsLogger.logException(e)
                _state.update { it.copy(isLoading = false, error = "Unexpected error: ${e.message}") }
            }
        }
    }
}