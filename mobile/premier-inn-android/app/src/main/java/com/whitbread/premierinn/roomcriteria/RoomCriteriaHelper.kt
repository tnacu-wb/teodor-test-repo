package com.whitbread.premierinn.roomcriteria

import android.content.Context
import com.whitbread.premierinn.R
import com.whitbread.premierinn.businessbooker.data.common.persistence.BusinessPersistenceManager
import com.whitbread.premierinn.common.AppConfiguration
import com.whitbread.premierinn.data.common.MAX_ROOMS_ERROR_THRESHOLD
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManager
import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import com.whitbread.premierinn.domain.common.RoomType
import com.whitbread.premierinn.domain.common.usecase.IsFeatureOn
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository.Key
import javax.inject.Inject

class RoomCriteriaHelper @Inject constructor(
    private val context: Context,
    private val persistenceManager: SimplePersistenceManager,
    private val businessPersistenceManager: BusinessPersistenceManager,
    private val appConfiguration: AppConfiguration,
    private val isFeatureOn: IsFeatureOn
) {
    private val innBusinessUser = businessPersistenceManager.getBusinessCustomerEmail() != EMPTY_STRING_DOMAIN
    private val isEmployeeOfferEnabled: Boolean
        get() = appConfiguration.isEmployeeOfferEnabled && isFeatureOn.invoke(Key.FEATURE_ALLOW_EMPLOYEE_OFFER)

    fun getRoomTitle(roomNumber: Int): String = context.getString(R.string.room_number, roomNumber)
    val accessibleCotTitle: String = context.getString(R.string.criteria_accessible_cot_title)
    fun getAccessibleCotMessage(phoneCostInfo: String): String = context.getString(R.string.criteria_accessible_cot_message, phoneCostInfo)
    val maxInfantsErrorTitle: String = context.getString(R.string.extra_cot_title)
    fun getMaxInfantsErrorMessage(phoneCostInfo: String): String = context.getString(R.string.extra_cot_message, phoneCostInfo)
    fun getRoomNameForType(roomType: RoomType): String {
        return roomType.getStringResourceName(context)
    }

    fun getMaxRoomsErrorMessage(phoneNumber: String = EMPTY_STRING_DOMAIN): String {
        val maxRooms = getMaxRooms()

        return if (maxRooms < MAX_ROOMS_ERROR_THRESHOLD) {
            context.getString(R.string.max_rooms_message_up_to_9_rooms, maxRooms, phoneNumber)
        } else {
            context.getString(R.string.max_rooms_message_over_9_rooms)
        }
    }

    fun isMaxRoomsGroupFormRequired(): Boolean {
        return getMaxRooms() == MAX_ROOMS_ERROR_THRESHOLD
    }

    fun getButtonSubtext(adultCount: Int, childrenCount: Int, infantCount: Int): String {
        return getTotalGroupedGuestsMessage(context, adultCount, childrenCount, infantCount)
    }

    private fun getMaxRooms(): Int {
        return when {
            innBusinessUser -> businessPersistenceManager.getMaxRoomsInnBusiness()
            isEmployeeOfferEnabled -> persistenceManager.getMaxRoomsForEmployee()
            else -> persistenceManager.getMaxRoomsLeisure()
        }
    }
}

fun RoomType.getStringResourceName(context: Context): String {
    return when (this) {
        RoomType.ACCESSIBLE -> context.getString(R.string.criteria_room_type_accessible)
        RoomType.DOUBLE -> context.getString(R.string.criteria_room_type_double)
        RoomType.TWIN -> context.getString(R.string.criteria_room_type_twin)
        RoomType.SINGLE -> context.getString(R.string.criteria_room_type_single)
        RoomType.FAMILY -> context.getString(R.string.criteria_room_type_family)
        RoomType.UNKNOWN -> throw IllegalStateException("Unknown room type in criteria")
    }
}

fun getTotalGroupedGuestsMessage(context: Context, adultCount: Int, childrenCount: Int, infantCount: Int): String {
    val adultString = context.resources.getQuantityString(R.plurals.number_of_adults, adultCount, adultCount)
    val childrenString = if (childrenCount > 0) {
        context.resources.getQuantityString(R.plurals.number_of_children, childrenCount, childrenCount)
    } else null
    val infantString = if (infantCount > 0) {
        context.resources.getQuantityString(R.plurals.number_of_infants, infantCount, infantCount)
    } else null

    return StringBuilder().apply {
        append("(")
        append(adultString)
        childrenString?.let {
            append(", $it")
        }
        infantString?.let {
            append(", $it")
        }
        append(")")
    }.toString()
}

//tofix
fun com.whitbread.premierinn.roomcriteria.viewmodel.RoomCriteriaState.formatTotalGuests(context: Context): String {
    return getTotalGroupedGuestsMessage(context, getAdultsTotal(), getChildrenTotal(), getInfantsTotal())
}