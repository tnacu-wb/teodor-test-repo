package com.whitbread.premierinn.ciol.viewmodel

import androidx.lifecycle.ViewModel
import com.whitbread.premierinn.ciol.entity.LeadBookerDetailsUiModel
import com.whitbread.premierinn.ciol.fragments.PreStayEditItemFragment
import com.whitbread.premierinn.ciol.uimodel.PreStayEditItemUIModel
import com.whitbread.premierinn.ciol.utils.getPreStayEditItemValueMap
import com.whitbread.premierinn.ciol.viewmodel.state.utils.PreStayEditItemFieldType
import com.whitbread.premierinn.common.Validator
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.domain.ciol.usecase.GetCountryNameFromIsoCodeUseCase
import com.whitbread.premierinn.domain.ciol.usecase.GetIsoCodeFromCountryNameUseCase
import com.whitbread.premierinn.domain.countries.entity.CountryDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class PreStayEditItemViewModel @Inject constructor(
    private val getIsoCodeFromCountryNameUseCase: GetIsoCodeFromCountryNameUseCase,
    private val getCountryNameFromIsoCodeUseCase: GetCountryNameFromIsoCodeUseCase
) : ViewModel() {
    private lateinit var screenItemType: String
    private val _state = MutableStateFlow<PreStayEditItemState>(PreStayEditItemState())
    val state: StateFlow<PreStayEditItemState> = _state.asStateFlow()


    data class PreStayEditItemState(
        val itemTypeValueMap: MutableMap<PreStayEditItemFieldType, PreStayEditItemUIModel> = HashMap(),
        val event: PreStayEditItemEvent? = null
    )

    sealed class PreStayEditItemAction {
        data class ItemValueChanged(val newValue: String, val itemFieldType: PreStayEditItemFieldType) : PreStayEditItemAction()
        object Validate : PreStayEditItemAction()
        object CountrySelectorClicked : PreStayEditItemAction()
        object FindAddressClicked : PreStayEditItemAction()
        data class CountryValueSelected(val country: String) : PreStayEditItemAction()
        data class UpdateAddress(
            val addressLine1: String,
            val addressLine2: String,
            val addressLine3: String,
            val postalCode: String,
        ) : PreStayEditItemAction()
    }

    sealed class PreStayEditItemEvent {
        data class ItemsValidated(val itemTypeValueMap: MutableMap<PreStayEditItemFieldType, PreStayEditItemUIModel>) : PreStayEditItemEvent()
        object OpenCountrySelector : PreStayEditItemEvent()
        data class OpenAddressFinder(val postcode: String) : PreStayEditItemEvent()
    }


    fun onScreenOpened(itemType: String, leadBookerDetails: LeadBookerDetailsUiModel?) {
        this.screenItemType = itemType
        leadBookerDetails?.let {
            _state.value = PreStayEditItemState(
                itemTypeValueMap = it.getPreStayEditItemValueMap(itemType).apply {
                    val countryCode = this[PreStayEditItemFieldType.COUNTRY]?.value ?: CountryDomain.UK_CODE

                    this[PreStayEditItemFieldType.COUNTRY] = PreStayEditItemUIModel(
                        value = getCountryNameFromIsoCodeUseCase.invoke(
                            countryIsoCode = countryCode
                        )
                    )

                    handleCountrySelection(countryCode, this)
                }
            )
        }
    }

    fun onEventConsumed() {
        _state.update {
            it.copy(event = null)
        }
    }

    fun processAction(action: PreStayEditItemAction) {
        when (action) {
            is PreStayEditItemAction.ItemValueChanged -> {
                if (_state.value.itemTypeValueMap.containsKey(action.itemFieldType)) {
                    _state.update {
                        it.copy(
                            itemTypeValueMap = it.itemTypeValueMap.toMutableMap().apply {
                                this[action.itemFieldType] = PreStayEditItemUIModel(value = action.newValue)
                            }
                        )
                    }
                }
            }

            is PreStayEditItemAction.Validate -> {
                when (screenItemType) {
                    PreStayEditItemFragment.ITEM_TYPE_EMAIL_ADDRESS -> {
                        val emailAddress = _state.value.itemTypeValueMap[PreStayEditItemFieldType.EMAIL_ADDRESS]?.value ?: EMPTY_STRING
                        val isEmailAddressValid = Validator.isEmailValid(emailAddress)

                        _state.update {
                            it.copy(
                                itemTypeValueMap = it.itemTypeValueMap.toMutableMap().apply {
                                    this[PreStayEditItemFieldType.EMAIL_ADDRESS] = PreStayEditItemUIModel(
                                        value = this[PreStayEditItemFieldType.EMAIL_ADDRESS]?.value ?: EMPTY_STRING,
                                        isInvalid = !isEmailAddressValid
                                    )
                                },
                                event = if (isEmailAddressValid) PreStayEditItemEvent.ItemsValidated(it.itemTypeValueMap) else null
                            )
                        }
                    }

                    PreStayEditItemFragment.ITEM_TYPE_PHONE_NUMBER -> {
                        val phoneNumber = _state.value.itemTypeValueMap[PreStayEditItemFieldType.PHONE_NUMBER]?.value ?: EMPTY_STRING
                        val isPhoneNumberValid = Validator.isPhoneNumberValid(phoneNumber)

                        _state.update {
                            it.copy(
                                itemTypeValueMap = it.itemTypeValueMap.toMutableMap().apply {
                                    this[PreStayEditItemFieldType.PHONE_NUMBER] = PreStayEditItemUIModel(
                                        value = this[PreStayEditItemFieldType.PHONE_NUMBER]?.value ?: EMPTY_STRING,
                                        isInvalid = !isPhoneNumberValid
                                    )
                                },
                                event = if (isPhoneNumberValid) PreStayEditItemEvent.ItemsValidated(it.itemTypeValueMap) else null
                            )
                        }
                    }

                    PreStayEditItemFragment.ITEM_TYPE_ADDRESS -> {
                        val countryValue = _state.value.itemTypeValueMap[PreStayEditItemFieldType.COUNTRY]?.value ?: EMPTY_STRING
                        val postCode = _state.value.itemTypeValueMap[PreStayEditItemFieldType.POST_CODE]
                        val addressLine1Value = _state.value.itemTypeValueMap[PreStayEditItemFieldType.ADDRESS_LINE_1]?.value ?: EMPTY_STRING
                        val isPostCodeValid = postCode?.isVisible == false ||
                                isPostCodeValueValid(
                                    postCode?.value ?: EMPTY_STRING,
                                    getIsoCodeFromCountryNameUseCase.invoke(countryValue) ?: EMPTY_STRING
                                )
                        val isAddressValid = countryValue.isNotBlank() && isPostCodeValid && addressLine1Value.isNotBlank()

                        _state.update {
                            it.copy(
                                itemTypeValueMap = it.itemTypeValueMap.toMutableMap().apply {
                                    this[PreStayEditItemFieldType.COUNTRY] = PreStayEditItemUIModel(
                                        value = this[PreStayEditItemFieldType.COUNTRY]?.value ?: EMPTY_STRING,
                                        isInvalid = countryValue.isBlank()
                                    )

                                    this[PreStayEditItemFieldType.POST_CODE] = PreStayEditItemUIModel(
                                        value = this[PreStayEditItemFieldType.POST_CODE]?.value ?: EMPTY_STRING,
                                        isInvalid = !isPostCodeValid
                                    )

                                    this[PreStayEditItemFieldType.ADDRESS_LINE_1] = PreStayEditItemUIModel(
                                        value = this[PreStayEditItemFieldType.ADDRESS_LINE_1]?.value ?: EMPTY_STRING,
                                        isInvalid = addressLine1Value.isBlank()
                                    )
                                },
                                event = if (isAddressValid) PreStayEditItemEvent.ItemsValidated(
                                    itemTypeValueMap = it.itemTypeValueMap.toMutableMap().apply {
                                        this[PreStayEditItemFieldType.COUNTRY] = PreStayEditItemUIModel(
                                            value = getIsoCodeFromCountryNameUseCase.invoke(
                                                countryName = this[PreStayEditItemFieldType.COUNTRY]?.value
                                            ) ?: EMPTY_STRING
                                        )
                                    }
                                ) else null
                            )
                        }
                    }
                }
            }

            is PreStayEditItemAction.CountrySelectorClicked -> {
                _state.update {
                    it.copy(event = PreStayEditItemEvent.OpenCountrySelector)
                }
            }

            is PreStayEditItemAction.FindAddressClicked -> {
                _state.update {
                    it.copy(
                        event = PreStayEditItemEvent.OpenAddressFinder(
                            postcode = _state.value.itemTypeValueMap[PreStayEditItemFieldType.POST_CODE]?.value ?: EMPTY_STRING
                        )
                    )
                }
            }

            is PreStayEditItemAction.CountryValueSelected -> {
                _state.update {
                    it.copy(
                        itemTypeValueMap = it.itemTypeValueMap.toMutableMap().apply {
                            this[PreStayEditItemFieldType.COUNTRY] = PreStayEditItemUIModel(value = action.country)

                            val countryCode = getIsoCodeFromCountryNameUseCase.invoke(action.country) ?: EMPTY_STRING
                            handleCountrySelection(countryCode, this)
                        }
                    )
                }
            }

            is PreStayEditItemAction.UpdateAddress -> {
                _state.update {
                    it.copy(
                        itemTypeValueMap = it.itemTypeValueMap.toMutableMap().apply {
                            this[PreStayEditItemFieldType.ADDRESS_LINE_1] = PreStayEditItemUIModel(value = action.addressLine1)
                            this[PreStayEditItemFieldType.ADDRESS_LINE_2] = PreStayEditItemUIModel(value = action.addressLine2)
                            this[PreStayEditItemFieldType.ADDRESS_LINE_3] = PreStayEditItemUIModel(value = action.addressLine3)
                            this[PreStayEditItemFieldType.POST_CODE] = PreStayEditItemUIModel(value = action.postalCode)
                        }
                    )
                }
            }
        }
    }

    private fun handleCountrySelection(
        selectedCountryCode: String,
        itemTypeValueMap: MutableMap<PreStayEditItemFieldType, PreStayEditItemUIModel>
    ) {
        itemTypeValueMap.apply {
            val isPostCodeVisible = CountryDomain.isCountryUk(selectedCountryCode)
                    || CountryDomain.isCountryGermanyUsingIsoCode(selectedCountryCode)
            this[PreStayEditItemFieldType.POST_CODE] = this[PreStayEditItemFieldType.POST_CODE]?.copy(
                isVisible = isPostCodeVisible,
                value = if (!isPostCodeVisible) EMPTY_STRING else this[PreStayEditItemFieldType.POST_CODE]?.value ?: EMPTY_STRING
            ) ?: PreStayEditItemUIModel(
                isVisible = isPostCodeVisible
            )

            this[PreStayEditItemFieldType.FIND_ADDRESS] = this[PreStayEditItemFieldType.FIND_ADDRESS]?.copy(
                isVisible = CountryDomain.isCountryUk(selectedCountryCode)
            ) ?: PreStayEditItemUIModel(isVisible = CountryDomain.isCountryUk(selectedCountryCode))
        }
    }

    private fun isPostCodeValueValid(postCode: String, countryCode: String): Boolean {
        return when(countryCode) {
            CountryDomain.UK_CODE -> Validator.isUkPostcodeValid(postCode)
            CountryDomain.GERMANY_ISO_CODE -> Validator.isGermanPostcodeValid(postCode)
            else -> postCode.isNotBlank()
        }
    }
}
