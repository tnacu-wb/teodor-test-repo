package com.whitbread.premierinn.ciol.entity.upsells.details

import android.content.Context
import android.os.Parcelable
import com.whitbread.premierinn.R
import com.whitbread.premierinn.ciol.entity.upsells.BreakfastUiModel
import com.whitbread.premierinn.ciol.entity.upsells.ExtrasItemUiModel
import com.whitbread.premierinn.ciol.entity.upsells.MealUiModel
import com.whitbread.premierinn.ciol.entity.upsells.UpsellEntry
import com.whitbread.premierinn.ciol.entity.upsells.UpsellItem
import com.whitbread.premierinn.ciol.entity.upsells.convertToAmendSelectedPackages
import com.whitbread.premierinn.ciol.entity.upsells.convertToUpsellItemList
import com.whitbread.premierinn.ciol.entity.upsells.copy
import com.whitbread.premierinn.ciol.utils.getId
import com.whitbread.premierinn.ciol.utils.isKidsMeal
import com.whitbread.premierinn.ciol.utils.updateNumberOfSelections
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.domain.booking.entity.BookingConfirmation
import com.whitbread.premierinn.domain.booking.entity.RoomStay
import com.whitbread.premierinn.domain.ciol.entity.isMealType
import com.whitbread.premierinn.domain.graphql.hdp.entity.PackagesSelectionDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.RoomSelectionDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.UpsellDomainItem
import com.whitbread.premierinn.domain.graphql.requestBodyModels.AmendRoomsSelections
import kotlinx.parcelize.Parcelize
import org.threeten.bp.LocalDate
import org.threeten.bp.temporal.ChronoUnit

@Parcelize
data class UpsellDetailsModel(
    // This has multiple items only when upsell type is ECI or LCO, otherwise we only send a single item in the list
    val roomSelections: List<RoomSelection>,
    val availableUpsells: List<UpsellEntry>, // Also contains kids meal if available
    // This is null for single room reservation and not null otherwise
    val roomName: String? = null,
    val roomStay: List<RoomStayUiModel>,
    // Empty for single room, non empty otherwise
    val adultsNames: List<String>,
    val upsellType: UpsellType,
    val bookingId: String
) : Parcelable

@Parcelize
data class RoomSelection(
    val reservationId: String,
    val selectedUpsells: MutableList<UpsellEntry>
) : Parcelable

@Parcelize
data class RoomStayUiModel(
    val adultsNumber: Long,
    val childrenNumber: Long,
    val cot: Boolean,
    val roomType: String,
    val ratePlanCode: String,
    val arrivalDate: String,
    val departureDate: String,
    val roomPrice: Float,
) : Parcelable

@Parcelize
data class MealSelectionRules(
    val isMealUnavailableForSelection: Boolean,
    val isKidsMenuSelectionEnabled: Boolean,
    val canIncrementForAdults: Boolean,
    val canDecrementForAdults: Boolean,
    val canIncrementForChildren: Boolean,
    val canDecrementForChildren: Boolean,
) : Parcelable

enum class UpsellType(val upsellName: String) {
    BREAKFAST("Breakfast"), MEAL_DEAL("Meal Deal"), ECI("Early Check-in"), LCO("Late Check-out"), WIFI("Ultimate Wi-Fi")
}

fun RoomStay.convertToRoomStayUiModel() = RoomStayUiModel(
    adultsNumber = adultsNumber,
    childrenNumber = childrenNumber,
    cot = cot,
    roomType = roomType,
    ratePlanCode = ratePlanCode,
    arrivalDate = arrivalDate,
    departureDate = departureDate,
    roomPrice = roomPrice
)

fun RoomStayUiModel.convertToRoomStay() = RoomStay(
    adultsNumber = adultsNumber,
    childrenNumber = childrenNumber,
    cot = cot,
    roomType = roomType,
    ratePlanCode = ratePlanCode,
    arrivalDate = arrivalDate,
    departureDate = departureDate,
    roomPrice = roomPrice
)

fun buildUpsellDetailsModel(
    roomId: String,
    roomSelections: List<RoomSelection>,
    availableUpsells: List<UpsellItem>,
    bookingConfirmation: BookingConfirmation,
    roomName: String? = null,
    upsellType: UpsellType,
    bookingId: String
) = UpsellDetailsModel(
    roomSelections = roomSelections,
    availableUpsells = availableUpsells.filterIsInstance<UpsellEntry>().sortedBy {
        when (it) {
            is BreakfastUiModel -> 0 // Case not possible
            is ExtrasItemUiModel -> it.order
            is MealUiModel -> it.order
        }
    },
    roomName = roomName,
    roomStay = getRoomStayList(roomId, bookingConfirmation, upsellType),
    adultsNames = getAdultNames(roomId, bookingConfirmation, upsellType),
    upsellType = upsellType,
    bookingId = bookingId
)

private fun getRoomStayList(
    roomId: String,
    bookingConfirmation: BookingConfirmation,
    upsellType: UpsellType
): List<RoomStayUiModel> = when (upsellType) {
    UpsellType.ECI,
    UpsellType.LCO -> bookingConfirmation.reservationByIdList.map { it.roomStay.convertToRoomStayUiModel() }

    UpsellType.BREAKFAST,
    UpsellType.MEAL_DEAL,
    UpsellType.WIFI -> listOf(
        bookingConfirmation.reservationByIdList.first {
            it.reservationId == roomId
        }.roomStay.convertToRoomStayUiModel()
    )
}

private fun getAdultNames(
    roomId: String,
    bookingConfirmation: BookingConfirmation,
    upsellType: UpsellType
): List<String> =
    if (bookingConfirmation.reservationByIdList.size == 1) {
        emptyList()
    } else when (upsellType) {
        UpsellType.ECI,
        UpsellType.LCO -> emptyList()

        UpsellType.BREAKFAST,
        UpsellType.MEAL_DEAL,
        UpsellType.WIFI -> bookingConfirmation.reservationByIdList
            .first { it.reservationId == roomId }.reservationGuestList
            .map { it.firstName }
    }

fun RoomStayUiModel.getNumberOfNights() = ChronoUnit.DAYS.between(LocalDate.parse(arrivalDate), LocalDate.parse(departureDate)).toInt()

fun List<RoomSelection>.convertToAmendRoomsSelectionsList(): List<AmendRoomsSelections> = this.map {
    it.toAmendRoomsSelections()
}

fun RoomSelection.toAmendRoomsSelections() = AmendRoomsSelections(
    reservationId = reservationId,
    packagesSelection = selectedUpsells.map { it.convertToAmendSelectedPackages() }.toMutableList()
)

fun List<RoomSelection>.getCurrentRoomSelections(
    currentRoomId: String, upsellType: UpsellType, shouldNavigateToRoomSelectionsFragment: Boolean = false
) = this.map {
    it.copy(
        selectedUpsells = it.selectedUpsells.copy().toMutableList()
    )
}.let { roomSelections ->
    if (arrayOf(UpsellType.ECI, UpsellType.LCO).contains(upsellType) || shouldNavigateToRoomSelectionsFragment
    ) {
        // We should add ECI and LCO to all rooms in the booking
        return@let roomSelections
    }
    // Otherwise just return a list containing the roomSelections for the current room
    return@let listOf(
        roomSelections.first { it.reservationId == currentRoomId }
    )
}

fun List<RoomSelection>.deepCopy() = this.map {
    it.copy(
        reservationId = it.reservationId,
        selectedUpsells = it.selectedUpsells.map { upsellEntry ->
            when(upsellEntry) {
                is BreakfastUiModel -> upsellEntry.copy()
                is ExtrasItemUiModel -> upsellEntry.copy()
                is MealUiModel -> upsellEntry.copy()
            }
        }.toMutableList()
    )
}

fun List<RoomSelectionDomain>.toRoomSelection(): List<RoomSelection> {
    val resultList = mutableListOf<RoomSelection>()
    this.forEach { item ->
        resultList.add(RoomSelection(
            reservationId = item.reservationId ?: EMPTY_STRING,
            selectedUpsells = item.packagesSelection.toUpsellEntry().toMutableList()
        ))
    }
    return resultList
}

fun List<PackagesSelectionDomain>.toUpsellEntry(): List<UpsellEntry> {
    val resultList = mutableListOf<UpsellEntry>()
    this.forEach { item ->
        val itemId = item.id ?: EMPTY_STRING
        if (itemId.isMealType()) {
            resultList.add(
                MealUiModel(
                    id = itemId,
                    noOfSelections = item.noOfSelections,
                    freeBreakfastSelections = if (itemId.isKidsMeal()) item.noOfSelections else 0,
                    displayAsEnabled = true,
                    preselectedNoOfSelections = 0
                )
            )
        } else {
            resultList.add(
                ExtrasItemUiModel(
                    id = itemId,
                    noOfSelections = item.noOfSelections,
                    preselectedNoOfSelections = 0
                )
            )
        }
    }
    return resultList
}

fun List<RoomSelectionDomain>?.convertToRoomSelectionList(availableUpsells: List<UpsellDomainItem>) =
    this?.map { roomSelectionDomain ->
        roomSelectionDomain.toRoomSelection(availableUpsells.convertToUpsellItemList())
    } ?: emptyList()

fun RoomSelectionDomain.toRoomSelection(availableUpsells: List<UpsellEntry>) = RoomSelection(
    reservationId = reservationId ?: EMPTY_STRING,
    selectedUpsells = this.packagesSelection.toUpsellEntryList(availableUpsells)
)

fun List<PackagesSelectionDomain>.toUpsellEntryList(availableUpsells: List<UpsellEntry>): MutableList<UpsellEntry> {
    val upsellEntryList = mutableListOf<UpsellEntry>()

    this.map { packageSelection ->
        availableUpsells.firstOrNull { it.getId() == packageSelection.id }?.let { upsell ->
            upsell.updateNumberOfSelections(packageSelection.noOfSelections)
            upsellEntryList.add(upsell)
        }
    }

    return upsellEntryList
}

fun UpsellDetailsModel.getAnalyticsUpsellName(context: Context): String{
    return when(this.upsellType){
        UpsellType.BREAKFAST -> context.getString(R.string.upsells_breakfast_options)
        UpsellType.MEAL_DEAL -> context.getString(R.string.upsells_details_meal_deal)
        UpsellType.ECI -> context.getString(R.string.upsells_early_check_in)
        UpsellType.LCO -> context.getString(R.string.upsells_late_check_out)
        UpsellType.WIFI -> context.getString(R.string.upsells_details_ultimate_wifi)
    }
}