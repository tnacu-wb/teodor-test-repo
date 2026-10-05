package com.whitbread.premierinn.ciol.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.domain.ciol.usecase.GetCountriesUseCase
import com.whitbread.premierinn.domain.countries.entity.CountryDomain
import com.whitbread.premierinn.domain.result.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectNationalityViewModel @Inject constructor(
    private val getCountriesUseCase: GetCountriesUseCase
): ViewModel() {

    private val _state = MutableStateFlow(SelectNationalityState())
    val state: StateFlow<SelectNationalityState> = _state

    data class SelectNationalityState(
        var isLoading: Boolean = true,
        var isError: Boolean = false,
        var countries: List<CountryDomain>? = null,
        var filteredCountries: List<CountryDomain>? = null,
        var selectedPosition: Int? = null
    )

    fun onScreenOpened(leadGuestNationality: String?) = viewModelScope.launch {
        getCountriesUseCase(leadGuestNationality).collect { result ->
            when (result) {
                is Result.Success -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            countries = result.data.first,
                            selectedPosition = result.data.second
                        )
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

    fun onSearchTextChanged(text: String, showCountryName: Boolean) {
        _state.value.countries?.filter {
            val searchCountryOrNationality = if (showCountryName) it.countryName.lowercase() else it.nationality?.lowercase() ?: EMPTY_STRING
            searchCountryOrNationality.contains(text.lowercase())
        }
            ?.let { filteredCountries ->
                _state.update { it.copy(filteredCountries = filteredCountries) }
            }
    }
}
