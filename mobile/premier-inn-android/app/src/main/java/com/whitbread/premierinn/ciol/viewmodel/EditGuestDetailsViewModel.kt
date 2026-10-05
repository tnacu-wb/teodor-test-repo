package com.whitbread.premierinn.ciol.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whitbread.premierinn.ciol.analytics.CiolAnalyticsData.Companion.ADDITIONAL_GUEST_DETAILS
import com.whitbread.premierinn.ciol.analytics.CiolAnalyticsData.Companion.BUTTON_CLICK
import com.whitbread.premierinn.ciol.analytics.CiolAnalyticsData.Companion.GUEST_EDIT_DOB_KEY
import com.whitbread.premierinn.ciol.analytics.CiolAnalyticsData.Companion.GUEST_EDIT_NATIONALITY_KEY
import com.whitbread.premierinn.ciol.analytics.CiolAnalyticsData.Companion.GUEST_EDIT_SAVE_KEY
import com.whitbread.premierinn.ciol.analytics.CiolAnalyticsData.Companion.GUEST_FIRST_LAST_NAME_EDIT_KEY
import com.whitbread.premierinn.ciol.analytics.CiolAnalyticsData.Companion.LEAD_GUEST_DETAILS
import com.whitbread.premierinn.ciol.analytics.RegCardAnalyticsData
import com.whitbread.premierinn.ciol.entity.RegCardGuest
import com.whitbread.premierinn.ciol.entity.mapToStayingGuestDetailsRegCard
import com.whitbread.premierinn.ciol.utils.getDateOfBirth
import com.whitbread.premierinn.ciol.utils.getFirstName
import com.whitbread.premierinn.ciol.utils.getLastName
import com.whitbread.premierinn.ciol.utils.getNationality
import com.whitbread.premierinn.ciol.viewmodel.state.FormValidation
import com.whitbread.premierinn.ciol.viewmodel.state.toFormValidationError
import com.whitbread.premierinn.common.analytics.TrackingAnalytics
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.domain.ciol.usecase.GERMANY_ISO_CODE
import com.whitbread.premierinn.domain.ciol.usecase.GetCountryNameFromIsoCodeUseCase
import com.whitbread.premierinn.domain.ciol.usecase.GetIsoCodeFromCountryNameUseCase
import com.whitbread.premierinn.domain.ciol.usecase.GetNationalityFromIsoCodeUseCase
import com.whitbread.premierinn.domain.ciol.usecase.ValidateRegCardGuestUseCase
import com.whitbread.premierinn.domain.result.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditGuestDetailsViewModel @Inject constructor(
    private val getIsoCodeFromCountryNameUseCase: GetIsoCodeFromCountryNameUseCase,
    private val validateRegCardGuestUseCase: ValidateRegCardGuestUseCase,
    private val getCountryNameFromIsoCodeUseCase: GetCountryNameFromIsoCodeUseCase,
    private val getNationalityFromIsoCodeUseCase: GetNationalityFromIsoCodeUseCase,
    private val analytics: TrackingAnalytics
) : ViewModel() {

    private val _selectedCountry = MutableStateFlow(EMPTY_STRING)
    val selectedCountry: StateFlow<String> = _selectedCountry

    private val _selectedNationality = MutableStateFlow(EMPTY_STRING)
    val selectedNationality: StateFlow<String> = _selectedNationality

    private val _isPassportRequired = MutableStateFlow(false)
    val isPassportRequired: StateFlow<Boolean> = _isPassportRequired

    private val _formValidation = MutableStateFlow(emptyList<FormValidation>())
    val formValidation: StateFlow<List<FormValidation>> = _formValidation

    private val _receivedGuest = MutableStateFlow<RegCardGuest?>(null)
    val receivedGuest : StateFlow<RegCardGuest?> = _receivedGuest

    private val _isLeadGuest = MutableStateFlow<Boolean?>(null)
    val isLeadGuest : StateFlow<Boolean?> = _isLeadGuest

    fun onScreenOpened(isLeadGuest: Boolean?) {
        _isLeadGuest.update { isLeadGuest }
    }

    fun onGuestReceived(guest: RegCardGuest) {
        _receivedGuest.update { guest }
    }

    fun onCountrySelected(countryName: String) = viewModelScope.launch {
        _selectedCountry.update { countryName }
    }

    fun onNationalitySelected(nationality: String) = viewModelScope.launch {
        _selectedNationality.update { nationality }
        _isPassportRequired.update {
            getIsoCodeFromCountryNameUseCase(nationality) != GERMANY_ISO_CODE
        }
    }

    fun getUserSelectedNationality(nationalityIsoCode: String) = getNationalityFromIsoCodeUseCase(nationalityIsoCode).ifBlank {
        nationalityIsoCode
    }

    fun onSaveButtonPressed(guest: RegCardGuest, isLeadGuest: Boolean) {
        validateForm(guest, isLeadGuest, true)
    }

    fun getCountryNameFromIsoCode(countryCode: String): String {
        return getCountryNameFromIsoCodeUseCase(countryCode)
    }

    fun getIsoCodeFromCountryName(countryName: String): String? {
        return getIsoCodeFromCountryNameUseCase(countryName)
    }

    fun validateForm(guest: RegCardGuest, isLeadGuest: Boolean, isSaveAction: Boolean = false) {
        viewModelScope.launch {
            validateRegCardGuestUseCase(
                stayingGuest = guest.mapToStayingGuestDetailsRegCard(),
                isLeadGuest = isLeadGuest
            ).collect { response ->
                when(response) {
                    is Result.Error ->
                        _formValidation.update { response.error.invalidFields.map { it.toFormValidationError() } }
                    is Result.Success ->
                        _formValidation.update {
                            if (isSaveAction) {
                                listOf(FormValidation.FormValid(guest, isLeadGuest))
                            } else {
                                emptyList()
                            }
                        }
                }
            }
        }
    }

    fun trackSaveButton(regCardGuest: RegCardGuest) {
        val screenName = if (isLeadGuest.value == true) LEAD_GUEST_DETAILS else ADDITIONAL_GUEST_DETAILS
        val data = mutableMapOf(
            GUEST_FIRST_LAST_NAME_EDIT_KEY to "${regCardGuest.getFirstName()}, ${regCardGuest.getLastName()}",
            GUEST_EDIT_SAVE_KEY to BUTTON_CLICK,
            GUEST_EDIT_DOB_KEY to regCardGuest.getDateOfBirth(),
            GUEST_EDIT_NATIONALITY_KEY to regCardGuest.getNationality()
        )
        analytics.track(screenName, RegCardAnalyticsData(data))
    }
}
