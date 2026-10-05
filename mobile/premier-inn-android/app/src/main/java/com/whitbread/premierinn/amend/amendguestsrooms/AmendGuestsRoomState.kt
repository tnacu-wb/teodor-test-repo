package com.whitbread.premierinn.amend.amendguestsrooms

import com.whitbread.premierinn.amend.AmendStringProvider
import com.whitbread.premierinn.common.AsyncResult
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManagerImpl
import com.whitbread.premierinn.domain.booking.entity.Booking
import com.whitbread.premierinn.domain.common.Guest
import com.whitbread.premierinn.domain.common.PriceDomain
import com.whitbread.premierinn.domain.common.RatePlanOpera
import com.whitbread.premierinn.domain.common.RoomCriteria
import com.whitbread.premierinn.domain.reservation.entity.Reservation
import com.whitbread.premierinn.domain.reservation.usecase.AddRoomUseCase
import com.whitbread.premierinn.domain.reservation.usecase.StoreUpdatedAmendedReservation

data class AmendGuestsRoomState(private val guestResult: AsyncResult<Guest>,
                                private val roomCriteriaUpdateResult: AsyncResult<RoomCriteria?>,
                                private val updateState: StoreUpdatedAmendedReservation.UpdateState? = null,
                                private val addRoomState: AddRoomUseCase.AddARoomState? = null,
                                private val titleOptions: List<String>,
                                private val stringResourceProvider: AmendStringProvider,
                                private val booking: AsyncResult<Booking> ?= null,
                                private val errorMessageAddNewRoom: String? = null,
                                private val errorMessageEditRoom: String? = null,
                                val storage: SimplePersistenceManagerImpl,
                                val buttonText: String,
                                val deviceLocaleProvider: DeviceLocaleProvider) {

    fun getLeadGuest(): GuestUiModel? =
            if (guestResult is AsyncResult.Success && guestResult.data != null) {
                GuestUiModel(
                        titleOptions = titleOptions,
                        name = NameModel(
                            guestResult.data.title,
                            guestResult.data.firstName,
                            guestResult.data.lastName,
                            guestResult.data.emailAddress
                        )
                )
            } else null

    val getErrorMessageAddNewRoom = errorMessageAddNewRoom

    val getErrorMessageEditRoom = errorMessageEditRoom

    fun getAddedRoom(): RoomCriteria? =
        if (addRoomState is AddRoomUseCase.AddARoomState.AvailabilityUpdated && addRoomState.updatedReservation != null) {
            storage.storeAmendedReservation(addRoomState.updatedReservation)
            addRoomState.updatedReservation?.roomsCriteria?.last()
        } else if (addRoomState is AddRoomUseCase.AddARoomState.AvailabilityUpdated && addRoomState.updatedReservation != null) {
            storage.storeAmendedReservation(addRoomState.updatedReservation)
            addRoomState.updatedReservation?.roomsCriteria?.last()
        } else null

    fun getAddedRoomCost() : PriceDomain? =
        if (addRoomState is AddRoomUseCase.AddARoomState.AvailabilityUpdated &&
            addRoomState.updatedReservation != null
        ) {
            if (addRoomState.ratePlanOpera.roomList.isNotEmpty()) {
                addRoomState.ratePlanOpera.roomList.first().cost
            } else if (!addRoomState.ratePlanOpera.accessibleRoomList.isNullOrEmpty()) {
                addRoomState.ratePlanOpera.accessibleRoomList!!.first().cost
            } else if (!addRoomState.ratePlanOpera.twinRoomList.isNullOrEmpty()) {
                addRoomState.ratePlanOpera.twinRoomList!!.first().cost
            } else null
        } else null

    fun getRatePlan(): RatePlanOpera? =
        when {
            updateState is StoreUpdatedAmendedReservation.UpdateState.AvailabilityUpdated -> {
                updateState.ratePlanOpera
            }
            addRoomState is AddRoomUseCase.AddARoomState.AvailabilityUpdated -> {
                addRoomState.ratePlanOpera
            }
            else -> null
        }

    fun getUpdatedReservation(): Reservation? =
        when (updateState) {
            is StoreUpdatedAmendedReservation.UpdateState.AvailabilityUpdated -> {
                updateState.updatedReservation
            }
            else -> null
        }

    val isRoomsRestricted : Boolean?
        get() = if( booking is AsyncResult.Success && booking.data != null) {
            booking.data.amendRestrictions.restricted && booking.data.amendRestrictions.rooms
        } else null

    val isGuestNamesRestricted : Boolean?
        get() = if( booking is AsyncResult.Success && booking.data != null) {
            booking.data.amendRestrictions.restricted && booking.data.amendRestrictions.guestNames
        } else null

    val fullScreenLoading = guestResult is AsyncResult.Loading
    val buttonLoading = updateState is StoreUpdatedAmendedReservation.UpdateState.Loading
    val updateCompleted = updateState is StoreUpdatedAmendedReservation.UpdateState.Updated
    val updateWithAvailabilityCompleted = updateState is StoreUpdatedAmendedReservation.UpdateState.AvailabilityUpdated

    val addRoomButtonLoading = addRoomState is AddRoomUseCase.AddARoomState.Loading

    val addRoomAvailabilityUpdated = addRoomState is AddRoomUseCase.AddARoomState.AvailabilityUpdated

    val errorMessage = when (updateState) {
        is StoreUpdatedAmendedReservation.UpdateState.NoAvailability -> stringResourceProvider.getNoMoreAvailabilityText(updateState.roomType)
        is StoreUpdatedAmendedReservation.UpdateState.Error -> stringResourceProvider.updateErrorMessage
        else -> null
    }

    val addRoomErrorMessage = when (addRoomState) {
        is AddRoomUseCase.AddARoomState.NoAvailability -> stringResourceProvider.noAvailabilityRoomsErrorMessage
        is AddRoomUseCase.AddARoomState.Error -> stringResourceProvider.availabilityRoomErrorMessage
        else -> null
    }
}

sealed class AmendGuestsRoomStateEvent {
    data class InitialCriteriaLoaded(val criteria: RoomCriteria) : AmendGuestsRoomStateEvent()
    data class InitialAddRoomCriteriaLoaded(val criteria: RoomCriteria) : AmendGuestsRoomStateEvent()
    data class GenericErrorEvent(val error: Throwable) : AmendGuestsRoomStateEvent()
    data class ShowMealsScreen(val show: Boolean) : AmendGuestsRoomStateEvent()
    data class AddNewRoomSuccessEvent(val guest:Guest) : AmendGuestsRoomStateEvent()
    data class AddNewRoomFailEvent(val message: String) : AmendGuestsRoomStateEvent()
    data class EditNewRoomSuccessEvent(val showUpsell: Boolean): AmendGuestsRoomStateEvent()
    data class EditNewRoomFailEvent(val message: String) : AmendGuestsRoomStateEvent()
    data class SaveGuestAmendDetailsFailed(val message: String) : AmendGuestsRoomStateEvent()
    object SaveUpdatedBookingConfirmationForAddRoomFailed : AmendGuestsRoomStateEvent()
    data class SavingBookingConfirmAfterAddRoomSuccessEvent(val guest: Guest):AmendGuestsRoomStateEvent()

}

data class GuestUiModel(val titleOptions: List<String>, val name: NameModel)
data class NameModel(val title: String, val firstName: String, val lastName: String, val emailAddress: String? = null)
