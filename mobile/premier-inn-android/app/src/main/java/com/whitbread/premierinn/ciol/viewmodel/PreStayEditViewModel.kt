package com.whitbread.premierinn.ciol.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whitbread.premierinn.ciol.analytics.logCiolAnalytics
import com.whitbread.premierinn.ciol.entity.FullNameElement
import com.whitbread.premierinn.ciol.entity.IdentificationNumber
import com.whitbread.premierinn.ciol.entity.IdentificationType
import com.whitbread.premierinn.ciol.entity.NameElement
import com.whitbread.premierinn.ciol.entity.Nationality
import com.whitbread.premierinn.ciol.entity.PreStayUiModel
import com.whitbread.premierinn.ciol.fragments.PRE_STAY_EDIT_FEATURE_TAG
import com.whitbread.premierinn.ciol.usecase.ValidateContactNumberUseCase
import com.whitbread.premierinn.ciol.usecase.ValidateEmailUseCase
import com.whitbread.premierinn.ciol.usecase.ValidateFirstNameUseCase
import com.whitbread.premierinn.ciol.usecase.ValidateLastNameUseCase
import com.whitbread.premierinn.ciol.usecase.ValidatorUseCase
import com.whitbread.premierinn.ciol.utils.getFirstName
import com.whitbread.premierinn.ciol.utils.getLastName
import com.whitbread.premierinn.ciol.utils.getTitle
import com.whitbread.premierinn.ciol.viewmodel.state.utils.FieldType
import com.whitbread.premierinn.ciol.viewmodel.state.utils.InputState
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.ScreenState.EDIT_GUEST_DETAILS
import com.whitbread.premierinn.common.analytics.TrackingAnalytics
import com.whitbread.premierinn.common.utils.StringUtils
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.domain.booking.entity.GuestsRoom
import com.whitbread.premierinn.domain.ciol.usecase.GetUserSelectedCountryUseCase
import com.whitbread.premierinn.domain.ciol.usecase.IsPassportRequiredForCountryUseCase
import com.whitbread.premierinn.domain.countries.entity.CountryDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.Serializable
import javax.inject.Inject

@HiltViewModel
class PreStayEditViewModel @Inject constructor(
    private val phoneValidator: ValidateContactNumberUseCase,
    private val emailValidator: ValidateEmailUseCase,
    private val firstNameValidator: ValidateFirstNameUseCase,
    private val lastNameValidator: ValidateLastNameUseCase,
    private val getUserSelectedCountryUseCase: GetUserSelectedCountryUseCase,
    private val isPassportRequiredForCountryUseCase: IsPassportRequiredForCountryUseCase,
    private val trackingAnalytics: TrackingAnalytics
) : ViewModel() {

    private val _state = MutableStateFlow(ElementInfoState())
    val state: StateFlow<ElementInfoState> = _state

    data class ElementInfoState(
        val hotelBrand: String = EMPTY_STRING,
        val roomId: String? = null,
        val guest: Serializable? = null,
        val fullNameElement: FullNameElement? = null,
        val nationality: Nationality = Nationality(),
        val identificationType: IdentificationType = IdentificationType(),
        val identificationNumber: IdentificationNumber = IdentificationNumber(),
        val element: HashMap<Serializable, String> = hashMapOf(
            Pair(
                FieldType.UNKNOWN,
                StringUtils.EMPTY_STRING
            )
        ),
        val dataValidity: InputState? = null
    )

    fun updateHotelBrand(hotelBrand: String) {
        _state.update { it.copy(hotelBrand = hotelBrand) }
    }

    fun onScreenOpened(preStayUiModel: PreStayUiModel?) {
        logCiolAnalytics(trackingAnalytics, preStayUiModel, EDIT_GUEST_DETAILS)
    }

    fun getUserSelectedCountry(
        leadGuestNationality: String?,
        leadGuestPassportNumber: String?
    ) = viewModelScope.launch {
        getUserSelectedCountryUseCase(leadGuestNationality)?.let { userSelectedCountry ->
            if (!isPassportRequired(userSelectedCountry.countryName)) {
                // When passport info is not required, reset the idType and idNumber
                _state.update {
                    it.copy(
                        nationality = it.nationality.copy(selectedCountry = userSelectedCountry),
                        identificationNumber = it.identificationNumber.copy(idNumber = null, isValid = true),
                        identificationType = it.identificationType.copy(idType = null, isValid = true)
                    )
                }
            } else {
                _state.update {
                    if (leadGuestPassportNumber?.isNotEmpty() == true) {
                        it.copy(
                            nationality = it.nationality.copy(selectedCountry = userSelectedCountry),
                            identificationNumber = it.identificationNumber.copy(idNumber = leadGuestPassportNumber, isValid = true),
                            identificationType = it.identificationType.copy(idType = "Passport", isValid = true)
                        )
                    } else {
                        it.copy(
                            nationality = it.nationality.copy(selectedCountry = userSelectedCountry),
                        )
                    }
                }
            }
        } ?: run {
            Log.w(PRE_STAY_EDIT_FEATURE_TAG, "userSelectedCountry: null")
        }
    }

    fun onValidate() {
        viewModelScope.launch {
            if (isLeadBookerFlow() || isGuestFlow()) {
                performLeadBookerOrGuestsFlowValidations(
                    isValidFirstName(firstNameValidator)?:false,
                    isValidLastName(lastNameValidator)?:false,
                    isNationalityValid(),
                    isIdTypeValid(),
                    isIdNumberValid()
                )
            } else {
                when (_state.value.element.keys.first()) {
                    FieldType.PHONE -> {
                        validate(phoneValidator, FieldType.PHONE)
                    }
                    FieldType.EMAIL -> {
                        validate(emailValidator, FieldType.EMAIL)
                    }
                    FieldType.UNKNOWN -> {
                        //do nothing
                    }
                }
            }
        }
    }

    fun onCountrySelected(countryDomain: CountryDomain) {
        if (isPassportRequired(countryDomain.countryName)) {
            _state.update {
                it.copy(nationality = it.nationality.copy(selectedCountry = countryDomain))
            }
        } else {
            // When id type and number are hidden, just remove the existing data
            _state.update {
                it.copy(
                    nationality = it.nationality.copy(selectedCountry = countryDomain),
                    identificationType = IdentificationType(),
                    identificationNumber = IdentificationNumber()
                )
            }
        }
    }

    fun onIdentificationTypeSelected(idType: String) {
        _state.update {
            it.copy(identificationType = it.identificationType.copy(idType = idType))
        }
    }

    fun onIdentificationNumberAdded(idNumber: String) {
        _state.update {
            it.copy(identificationNumber = it.identificationNumber.copy(idNumber = idNumber))
        }
    }

    fun isPassportRequired(selectedCountryName: String): Boolean {
        return isPassportRequiredForCountryUseCase(selectedCountryName, _state.value.hotelBrand)
    }

    private fun performLeadBookerOrGuestsFlowValidations(
        isFirstNameValid: Boolean,
        isLastNameValid: Boolean,
        isNationalityValid: Boolean,
        isIdTypeValid: Boolean,
        isIdNumberValid: Boolean,
    ) {
        val currentTitle = getCurrentTitle() ?: StringUtils.EMPTY_STRING

        if (!isFirstNameValid && !isLastNameValid) {
            _state.update {
                it.copy(
                    fullNameElement = it.fullNameElement?.copy(
                        firstName = it.fullNameElement.firstName?.copy(
                            dataValidity = InputState.INVALID
                        ), lastName = it.fullNameElement.lastName?.copy(
                            dataValidity  = InputState.INVALID)
                    ),
                    dataValidity = InputState.INVALID
                )
            }
            return
        }

        if (!isFirstNameValid) {
            _state.update {
                it.copy(
                    fullNameElement = it.fullNameElement?.copy(
                        firstName = it.fullNameElement.firstName?.copy(
                            dataValidity = InputState.INVALID
                        ), lastName = it.fullNameElement.lastName?.copy(
                            dataValidity  = InputState.VALID)
                    ),
                    dataValidity = InputState.INVALID
                )
            }
        }

        if (!isLastNameValid) {
            _state.update {
                it.copy(
                    fullNameElement = it.fullNameElement?.copy(
                        firstName = it.fullNameElement.firstName?.copy(
                            dataValidity = InputState.VALID
                        ), lastName = it.fullNameElement.lastName?.copy(
                            dataValidity  = InputState.INVALID)
                    ),
                    dataValidity = InputState.INVALID
                )
            }
        }

        if (currentTitle.isEmpty()){
            _state.update {
                it.copy(
                    fullNameElement = it.fullNameElement?.copy(
                        firstName = it.fullNameElement.firstName?.copy(
                            dataValidity = InputState.VALID
                        ), lastName = it.fullNameElement.lastName?.copy(
                            dataValidity  = InputState.VALID)
                    ),
                    dataValidity = InputState.INVALID
                )
            }
            return
        }

        val selectedCountry = _state.value.nationality.selectedCountry
        if (selectedCountry != null && isPassportRequired(selectedCountry.countryName)) {
            _state.update {
                it.copy(
                    nationality = it.nationality.copy(isValid = isNationalityValid),
                    identificationType = it.identificationType.copy(isValid = isIdTypeValid),
                    identificationNumber = it.identificationNumber.copy(isValid = isIdNumberValid),
                    dataValidity = if (
                        isFirstNameValid && isLastNameValid && isNationalityValid && isIdTypeValid && isIdNumberValid
                    ) InputState.VALID else InputState.INVALID
                )
            }
        } else {
            _state.update {
                it.copy(
                    nationality = it.nationality.copy(isValid = isNationalityValid),
                    dataValidity = if (isFirstNameValid && isLastNameValid && isNationalityValid)
                        InputState.VALID else InputState.INVALID
                )
            }
        }

        // Handle nationality errors before continuing
        if (_state.value.dataValidity == InputState.INVALID) return

        if (isFirstNameValid && isLastNameValid) {
            _state.update {
                it.copy(
                    fullNameElement = it.fullNameElement?.copy(
                        firstName = it.fullNameElement.firstName?.copy(
                            dataValidity = InputState.VALID
                        ), lastName = it.fullNameElement.lastName?.copy(
                            dataValidity  = InputState.VALID)
                    ),
                    dataValidity = InputState.VALID
                )
            }
        }
    }

    private fun isValidFirstName(validator: ValidatorUseCase) = getCurrentFirstName()?.let { validator(it) }

    private fun isValidLastName(validator: ValidatorUseCase) = getCurrentLastName()?.let { validator(it) }

    private fun isNationalityValid() = _state.value.run { nationality.selectedCountry != null }

    private fun isIdTypeValid() = _state.value.run { !identificationType.idType.isNullOrBlank() }

    private fun isIdNumberValid() = _state.value.run { !identificationNumber.idNumber.isNullOrBlank() }

    private fun validate(validator: ValidatorUseCase, fieldType: FieldType) {
        if(validator(_state.value.element.values.first())){
                _state.update {
                    it.copy(
                        dataValidity = InputState.VALID,
                        element = hashMapOf(
                            Pair(
                                fieldType,
                                _state.value.element.values.first()
                            )
                        )
                    )
                }
            }else{
                _state.update {
                    it.copy(dataValidity = InputState.INVALID)
                }
            }
    }

    fun leadBookerOrGuestFlowStateUpdate(
        roomId: String,
        inputMap: HashMap<Serializable, String>
    ) {
        if (inputMap.keys.first() == FieldType.LEAD_GUEST || inputMap.keys.first() == FieldType.SECOND_GUEST) {
            _state.update {
                it.copy(
                    roomId = roomId,
                    guest = inputMap.keys.first(),
                    fullNameElement = createFullNameElement(
                        title = inputMap.values.first().getTitle(),
                        firstName = inputMap.values.first().getFirstName(),
                        lastName = inputMap.values.first().getLastName(),
                    )
                )
            }
        }
    }

    fun updateState(inputMap: HashMap<Serializable, String>) {
        if (inputMap.keys.first() == FieldType.FIRST_NAME) {
            _state.update {
                it.copy(
                    fullNameElement = createFullNameElement(
                        title = getCurrentTitle() ?: StringUtils.EMPTY_STRING,
                        firstName = inputMap.values.first(),
                        lastName = getCurrentLastName(),
                    )
                )
            }
        } else if (inputMap.keys.first() == FieldType.LAST_NAME) {
            _state.update {
                it.copy(
                    fullNameElement = createFullNameElement(
                        title = getCurrentTitle() ?: StringUtils.EMPTY_STRING,
                        firstName = getCurrentFirstName(),
                        lastName = inputMap.values.first(),
                    )
                )
            }
        } else {
            _state.update {
                it.copy(element = inputMap)
            }
        }
    }

    fun updateFullNameElementTitle(newTitle: String) {
        _state.update {
            it.copy(fullNameElement = it.fullNameElement?.copy(title = newTitle))
        }
    }

    private fun createFullNameElement(
        firstNameType: FieldType = FieldType.FIRST_NAME,
        lastNameType: FieldType = FieldType.LAST_NAME,
        title: String,
        firstName: String?,
        lastName: String?,
        firstNameValidity: InputState? = null,
        lastNameValidity: InputState? = null
    ) =
        FullNameElement(
            title = title,
            firstName = NameElement(
                hashMapOf(Pair(firstNameType, firstName ?: StringUtils.EMPTY_STRING)),
                dataValidity = firstNameValidity
            ),
            lastName = NameElement(
                hashMapOf(Pair(lastNameType, lastName ?: StringUtils.EMPTY_STRING)
                ), dataValidity = lastNameValidity
            )
        )

    fun isGuestFlow() = _state.value.roomId != null

    fun isLeadBookerFlow() = _state.value.roomId == null && _state.value.element.keys.first() == FieldType.UNKNOWN

    fun getCurrentTitle() = _state.value.fullNameElement?.title

    fun getCurrentFirstName() = _state.value.fullNameElement?.firstName?.map?.values?.first()

    fun getCurrentLastName() = _state.value.fullNameElement?.lastName?.map?.values?.first()

    fun getGuestNationality() = _state.value.nationality.selectedCountry?.countryName ?: StringUtils.EMPTY_STRING

    fun getGuestPassportNumber() = _state.value.identificationNumber.idNumber ?: EMPTY_STRING

    private fun getRoomForGuest(type: FieldType, room: GuestsRoom) =
        if (type == FieldType.LEAD_GUEST) {
            GuestsRoom(
                roomId = room.roomId,
                leadGuestTitle = getCurrentTitle() ?: StringUtils.EMPTY_STRING,
                leadGuestFirstName = getCurrentFirstName() ?: StringUtils.EMPTY_STRING,
                leadGuestLastName = getCurrentLastName() ?: StringUtils.EMPTY_STRING,
                leadGuestNationality = getGuestNationality(),
                leadGuestPassportNumber = getGuestPassportNumber(),
                isLeadGuestPassportNumberRequired = room.isLeadGuestPassportNumberRequired,
                accompanyingGuestTitle = room.accompanyingGuestTitle,
                accompanyingGuestFirstName = room.accompanyingGuestFirstName,
                accompanyingGuestLastName = room.accompanyingGuestLastName,
                accompanyingGuestNationality = room.accompanyingGuestNationality,
                accompanyingGuestPassportNumber = room.accompanyingGuestPassportNumber,
                isAccompanyingGuestPassportNumberRequired = false,
                numberOfAdults = room.numberOfAdults,
                numberOfChildren = room.numberOfChildren,
                purposeOfStay = room.purposeOfStay
            )
        } else {
            GuestsRoom(
                roomId = room.roomId,
                leadGuestTitle = room.leadGuestTitle,
                leadGuestFirstName = room.leadGuestFirstName,
                leadGuestLastName = room.leadGuestLastName,
                leadGuestNationality = room.leadGuestNationality,
                leadGuestPassportNumber = room.leadGuestPassportNumber,
                isLeadGuestPassportNumberRequired = false,
                accompanyingGuestTitle = getCurrentTitle() ?: StringUtils.EMPTY_STRING,
                accompanyingGuestFirstName = getCurrentFirstName() ?: StringUtils.EMPTY_STRING,
                accompanyingGuestLastName = getCurrentLastName() ?: StringUtils.EMPTY_STRING,
                accompanyingGuestNationality = getGuestNationality(),
                accompanyingGuestPassportNumber = getGuestPassportNumber(),
                isAccompanyingGuestPassportNumberRequired = false,
                numberOfAdults = room.numberOfAdults,
                numberOfChildren = room.numberOfChildren,
                purposeOfStay = room.purposeOfStay
            )
        }

    fun updateRoomAfterEdit(fieldType:Serializable, room: GuestsRoom): GuestsRoom {
        return if (fieldType == FieldType.LEAD_GUEST) {
            getRoomForGuest(FieldType.LEAD_GUEST, room)
        } else {
            getRoomForGuest(FieldType.SECOND_GUEST, room)
        }
    }

    fun onTextChangedUpdateState(guest: Serializable, name:Serializable, text:String){
        if (guest == FieldType.LEAD_BOOKER || guest == FieldType.LEAD_GUEST || guest == FieldType.SECOND_GUEST) {
            updateState(hashMapOf(Pair(name, text)))
        } else {
            updateState(hashMapOf(Pair(guest, text)))
        }
    }
}
