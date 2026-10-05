package com.whitbread.premierinn.ciol.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whitbread.premierinn.ciol.fragments.PRE_STAY_EDIT_FEATURE_TAG
import com.whitbread.premierinn.ciol.viewmodel.state.utils.FieldType
import com.whitbread.premierinn.ciol.viewmodel.state.utils.InputState
import com.whitbread.premierinn.ciol.viewmodel.state.utils.buildUpdateRequestBody
import com.whitbread.premierinn.common.utils.StringUtils
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.domain.booking.entity.GuestsRoom
import com.whitbread.premierinn.domain.booking.entity.PreStayModel
import com.whitbread.premierinn.domain.ciol.usecase.IsPassportRequiredForCountryUseCase
import com.whitbread.premierinn.domain.common.AppDispatchers
import com.whitbread.premierinn.domain.common.translateFullNameToEnglishOrGerman
import com.whitbread.premierinn.domain.common.translateTitleToGermanIfApplicable
import com.whitbread.premierinn.domain.graphql.ciol.usecase.UpdatePreStayInfoUseCase
import com.whitbread.premierinn.domain.result.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.Serializable
import javax.inject.Inject

@HiltViewModel
class PreStaySharedViewModel @Inject constructor(
    val deviceLocaleProvider: DeviceLocaleProvider,
    private val updatePreStayInfoUseCase: UpdatePreStayInfoUseCase,
    private val isPassportRequiredForCountryUseCase: IsPassportRequiredForCountryUseCase,
    private val dispatchers: AppDispatchers
) : ViewModel() {

    lateinit var preStayModel: PreStayModel

    fun initParams(preStayModel: PreStayModel) {
        this.preStayModel = preStayModel
    }

    companion object {
        const val HYPHEN = " - "
    }

    private val _shouldNavigate = MutableStateFlow(false)
    val shouldNavigate: StateFlow<Boolean> = _shouldNavigate

    private val _title = MutableStateFlow(StringUtils.EMPTY_STRING)
    val title: StateFlow<String> = _title

    private val _state = MutableStateFlow(ConfirmDetailsScreenState())
    val state: StateFlow<ConfirmDetailsScreenState> = _state

    var isStateInitialized: Boolean = false

    sealed class Error {
        data class GenericError(val isPibaCnp: Boolean = false) : Error()
    }

    data class ConfirmDetailsScreenState(
        val booker: HashMap<Serializable, String> = hashMapOf(Pair(FieldType.LEAD_BOOKER, StringUtils.EMPTY_STRING)),
        val email: HashMap<Serializable, String> = hashMapOf(Pair(FieldType.EMAIL, StringUtils.EMPTY_STRING)),
        val phone: HashMap<Serializable, String> = hashMapOf(Pair(FieldType.PHONE, StringUtils.EMPTY_STRING)),
        val address: HashMap<Serializable, String> = hashMapOf(Pair(FieldType.ADDRESS, StringUtils.EMPTY_STRING)),
        val rooms: List<GuestsRoom> = emptyList(),
        val specialOccasion: HashMap<String, Boolean> = hashMapOf(Pair(StringUtils.EMPTY_STRING, false)),
        val dataValidity: InputState? = null,
        val error: Error? = null
    )

    fun onScreenOpened() {
        viewModelScope.launch(dispatchers.io) {
            _state.update {
                it.copy(
                    booker = hashMapOf(
                        Pair(
                            FieldType.LEAD_BOOKER,
                            getFullBookerName()
                        )
                    ),
                    email = hashMapOf(
                        Pair(
                            FieldType.EMAIL,
                            preStayModel.preStayDetails.bookerDetails.leadBookerEmail
                        )
                    ),
                    phone = hashMapOf(
                        Pair(
                            FieldType.PHONE,
                            preStayModel.preStayDetails.bookerDetails.leadBookerPhone
                        )
                    ),
                    address = hashMapOf(
                        Pair(
                            FieldType.ADDRESS,
                            getFullAddress()
                        )
                    ),
                    rooms = preStayModel.preStayDetails.guestRooms
                )
            }
            isStateInitialized = true
        }
    }

    fun updateReservationInfo(shouldShowGuestInfo: Boolean) {
        if (!shouldShowGuestInfo) {
            shouldNavigate(true)
            return
        }

        viewModelScope.launch {
            updatePreStayInfoUseCase(
                buildUpdateRequestBody(
                    preStayModel = preStayModel,
                    bookerName = _state.value.booker.values.first(),
                    bookerEmail = _state.value.email.values.first(),
                    bookerPhone = _state.value.phone.values.first(),
                    rooms = _state.value.rooms,
                    leadBookerName = getFullBookerName(),
                    deviceLocaleProvider = deviceLocaleProvider
                )
            ).collect { result ->
                when (result) {
                    is Result.Success -> {
                        preStayModel = preStayModel.copy(
                            preStayDetails = preStayModel.preStayDetails.copy(
                                guestRooms = _state.value.rooms
                            ),
                        )
                        shouldNavigate(true)
                    }

                    is Result.Error -> {
                        _state.update { it.copy(error = Error.GenericError()) }

                        Log.e(
                            PRE_STAY_EDIT_FEATURE_TAG,
                            result.error.message ?: "Error while retrieving message"
                        )
                    }
                }
            }
        }
    }

    fun shouldNavigate(shouldNavigate: Boolean) {
        _shouldNavigate.update { shouldNavigate }
    }

    fun updatePhoneNumber(newPhoneNumber: String) {
        preStayModel = preStayModel.copy(
            preStayDetails = preStayModel.preStayDetails.copy(
                bookerDetails = preStayModel.preStayDetails.bookerDetails.copy(
                    leadBookerPhone = newPhoneNumber
                )
            )
        )

        _state.update {
            it.copy(
                phone = hashMapOf(
                    Pair(
                        FieldType.PHONE,
                        preStayModel.preStayDetails.bookerDetails.leadBookerPhone
                    )
                )
            )
        }
    }

    fun updateEmail(newEmail: String) {
        preStayModel = preStayModel.copy(
            preStayDetails = preStayModel.preStayDetails.copy(
                bookerDetails = preStayModel.preStayDetails.bookerDetails.copy(
                    leadBookerEmail = newEmail
                )
            )
        )

        _state.update {
            it.copy(
                email = hashMapOf(
                    Pair(
                        FieldType.EMAIL,
                        preStayModel.preStayDetails.bookerDetails.leadBookerEmail
                    )
                )
            )
        }
    }

    fun updateAddress(addressLine1: String, addressLine2: String, addressLine3: String, postalCode: String, country: String) {
        preStayModel = preStayModel.copy(
            preStayDetails = preStayModel.preStayDetails.copy(
                bookerDetails = preStayModel.preStayDetails.bookerDetails.copy(
                    address = preStayModel.preStayDetails.bookerDetails.address.copy(
                        addressLine1 = addressLine1,
                        addressLine2 = addressLine2,
                        addressLine3 = addressLine3,
                        postalCode = postalCode,
                        country = country
                    )
                )
            )
        )

        _state.update {
            it.copy(
                address = hashMapOf(
                    Pair(
                        FieldType.ADDRESS,
                        getFullAddress()
                    )
                )
            )
        }
    }

    private fun updateCountriesPassportNumberInfo(updatedRoom: GuestsRoom, fieldType: Serializable) {
        val isPassportRequiredForCountry = if (fieldType == FieldType.LEAD_GUEST) {
            updatedRoom.leadGuestNationality.let {
                isPassportRequiredForCountryUseCase.invoke(it, preStayModel.preStayHeaderInfo.hotelBrand)
            }
        } else {
            updatedRoom.accompanyingGuestNationality.let {
                isPassportRequiredForCountryUseCase.invoke(it, preStayModel.preStayHeaderInfo.hotelBrand)
            }
        }

        val updatedRooms = _state.value.rooms.map { it.copy() }
        updatedRooms.first { it.roomId == updatedRoom.roomId }.let { roomToUpdate ->
            when (fieldType) {
                FieldType.LEAD_GUEST -> roomToUpdate.isLeadGuestPassportNumberRequired = isPassportRequiredForCountry
                else -> roomToUpdate.isAccompanyingGuestPassportNumberRequired = isPassportRequiredForCountry
            }
        }

        _state.update {
            it.copy(rooms = updatedRooms)
        }
    }

    fun getBookerName() = _state.value.booker.values.firstOrNull()?.translateFullNameToEnglishOrGerman(deviceLocaleProvider.getDeviceLanguage())

    fun getLanguage() = deviceLocaleProvider.getDeviceLanguage()

    fun updateState(
        updatedRoom: GuestsRoom,
        fieldType: Serializable
    ) {
        val roomsToUpdate = _state.value.rooms.map { it.copy() }.toMutableList().apply {
            replaceAll { currentRoom ->
                if (currentRoom.roomId == updatedRoom.roomId) updatedRoom else currentRoom
            }
        }

        _state.update {
            it.copy(rooms = roomsToUpdate)
        }
        updateCountriesPassportNumberInfo(updatedRoom, fieldType)
    }

    fun updateTitle(newTitle: String) {
        _title.update { newTitle }
    }

    private fun getFullAddress(): String {
        if (preStayModel.isThirdPartyBooking) {
            with(preStayModel.preStayDetails.bookerDetails.address) {
                if (country.isBlank() || postalCode.isBlank() || addressLine1.isBlank()) {
                    return EMPTY_STRING
                }
            }
        }

        preStayModel.preStayDetails.bookerDetails.address.let {
            return listOf(
                it.addressLine1,
                it.addressLine2,
                it.addressLine3,
                it.postalCode
            ).filter { element -> element.isNotEmpty() }.joinToString()
        }
    }

    private fun getFullBookerName(): String {
        var list: List<String>
        preStayModel.preStayDetails.bookerDetails.let {
            list = listOf(it.leadBookerTitle.translateTitleToGermanIfApplicable(deviceLocaleProvider.getDeviceLanguage()), it.leadBookerFirstName, it.leadBookerLastName)
        }
        return list.filter { it.isNotEmpty() }.joinToString(" ")
    }

    fun onErrorHandled() {
        _state.update { it.copy(error = null)}
    }
}
