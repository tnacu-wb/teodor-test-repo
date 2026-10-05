package com.whitbread.premierinn.bathroomselection

import android.content.Context
import com.whitbread.premierinn.R
import com.whitbread.premierinn.accessiblebathroomselection.AccessibleRoomSizeOption
import com.whitbread.premierinn.common.StringResourceProvider
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.domain.bathroomselection.entity.BathroomType
import com.whitbread.premierinn.domain.common.LettingType
import javax.inject.Inject

class BathroomSelectionStringProvider @Inject constructor(val context: Context, deviceLocaleProvider: DeviceLocaleProvider): StringResourceProvider(context, deviceLocaleProvider) {

    private val hotelDoesNotOfferWetRooms: String
        get() = context.getString(R.string.bathroom_selection_hotel_no_wetrooms)

    private val hotelDoesNotOfferLoweredBaths: String
        get() = context.getString(R.string.bathroom_selection_hotel_no_lowered_baths)

    private val loweredBathTitle: String
        get() = context.getString(R.string.bathroom_selection_lowered_bath_title)

    private val wetRoomTitle: String
        get() = context.getString(R.string.bathroom_selection_wet_room_title)

    private val loweredBathDescription: String
        get() = context.getString(R.string.bathroom_selection_lowered_bath_description)

    private val wetRoomDescription: String
        get() = context.getString(R.string.bathroom_selection_wetroom_description)

    fun roomSizeDescription(roomSize: AccessibleRoomSizeOption): String {
        return when (roomSize) {
            AccessibleRoomSizeOption.DOUBLE -> context.getString(R.string.bathroom_selection_room_size_double)
            AccessibleRoomSizeOption.TWIN -> context.getString(R.string.bathroom_selection_room_size_twin)
        }
    }

    fun toolTipText(roomSizeToChangeTo: AccessibleRoomSizeOption): String {
        val roomSizeName = when (roomSizeToChangeTo) {
            AccessibleRoomSizeOption.DOUBLE -> context.getString(R.string.bathroom_size_double)
            AccessibleRoomSizeOption.TWIN -> context.getString(R.string.bathroom_size_twin)
        }
        return context.getString(R.string.bathroom_selection_tooltip_available, roomSizeName)
    }

    fun noMoreAvailableMessage(bathroomType: BathroomType, roomSize: AccessibleRoomSizeOption, selected: LettingType): String {
        val bathroomLabel = fun(bathroomType: BathroomType) = when (bathroomType) {
                BathroomType.WET_ROOM -> context.getString(R.string.bathroom_wet_rooms_label)
                BathroomType.LOWERED_BATH -> context.getString(R.string.bathroom_lowered_bath_rooms_label)
            }

        val roomSizeLabel = fun(roomSize: AccessibleRoomSizeOption) = when (roomSize) {
                AccessibleRoomSizeOption.DOUBLE -> context.getString(R.string.bathroom_size_double)
                AccessibleRoomSizeOption.TWIN -> context.getString(R.string.bathroom_size_twin)
            }

        val unavailableBathroomType = bathroomLabel(bathroomType)
        val unavailableRoomSize = roomSizeLabel(roomSize)
        val selectedBathroomType = bathroomLabel(selected.bathroomType!!)
        val selectedRoomSize = roomSizeLabel(selected.accessibleRoomSize())

        return context.getString(R.string.bathroom_selection_no_more_available,
                unavailableRoomSize,
                unavailableBathroomType,
                selectedRoomSize,
                selectedBathroomType)
    }

    fun hotelDoesNotOfferBathroomMessage(bathroomType: BathroomType): String {
        return when (bathroomType) {
            BathroomType.WET_ROOM -> hotelDoesNotOfferWetRooms
            BathroomType.LOWERED_BATH -> hotelDoesNotOfferLoweredBaths
        }
    }

    fun bathroomTitle(bathroomType: BathroomType): String {
        return when (bathroomType) {
            BathroomType.WET_ROOM -> wetRoomTitle
            BathroomType.LOWERED_BATH -> loweredBathTitle
        }
    }

    fun bathroomDescription(bathroomType: BathroomType): String {
        return when (bathroomType) {
            BathroomType.WET_ROOM -> wetRoomDescription
            BathroomType.LOWERED_BATH -> loweredBathDescription
        }
    }
}