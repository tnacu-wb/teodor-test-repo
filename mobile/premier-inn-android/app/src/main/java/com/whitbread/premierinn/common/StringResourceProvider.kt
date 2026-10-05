package com.whitbread.premierinn.common

import android.content.Context
import android.content.pm.PackageManager
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import com.whitbread.premierinn.R
import com.whitbread.premierinn.bookingdetails.UpsellItemSummary.UpsellItemType
import com.whitbread.premierinn.common.format.DateFormat
import com.whitbread.premierinn.common.format.format
import com.whitbread.premierinn.common.utils.FileUtils
import com.whitbread.premierinn.criteria.roomselector.Room
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.domain.booking.entity.Booking
import com.whitbread.premierinn.domain.common.RoomType
import com.whitbread.premierinn.domain.common.toGermanIfApplicable
import com.whitbread.premierinn.searchresults.SearchResultsInput
import org.threeten.bp.LocalDate
import java.io.IOException
import java.util.*
import javax.inject.Inject

/**
 * Class that provides android string resources to unitTestable classes like Presenters, Services etc.
 */
open class StringResourceProvider @Inject constructor(private val context: Context,
                                                      private val deviceLocaleProvider: DeviceLocaleProvider) {
    fun getString(@StringRes resId: Int): String {
        return context.getString(resId)
    }

    @Throws(IOException::class)
    fun getStringAsset(fileName: String): String {
        return FileUtils.readFile(context.assets.open(fileName))
    }

    fun getString(@StringRes resId: Int, vararg formatArgs: Any): String {
        return context.getString(resId, *formatArgs)
    }

    fun getQuantityString(@PluralsRes resId: Int, quantity: Int, vararg args: Any): String {
        return context.resources.getQuantityString(resId, quantity, *args)
    }

    @get:Throws(PackageManager.NameNotFoundException::class)
    val versionLabel: String
        get() {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            return String.format(context.resources.getString(R.string.landing_screen_version_name),
                    packageInfo.versionName, packageInfo.versionCode)
        }

    @get:Throws(PackageManager.NameNotFoundException::class)
    val versionName: String
        get() {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            return VERSION_PREFIX + packageInfo.versionName
        }

    fun callUsNumber(): String {
        return context.getString(R.string.call_view_default_customer_service_number)
    }

    fun callUsNumberCost(): String {
        return context.getString(R.string.call_view_cost)
    }

    fun getDateRange(arrivalDate: LocalDate, departureDate: LocalDate): String {
        val arrivalDateFormatted = arrivalDate.format(DateFormat.WEEKDAY_DAY_MONTH)
        val departureDateFormatted = departureDate.format(DateFormat.WEEKDAY_DAY_MONTH)
        return "$arrivalDateFormatted - $departureDateFormatted"
    }

    fun getNights(nights: Int): String {
        return context.resources.getQuantityString(R.plurals.nights, nights, nights)
    }

    fun getRooms(rooms: Int, areAllRoomsAccessible: Boolean): String {
        return context.resources.getQuantityString(
                if (areAllRoomsAccessible) R.plurals.accessible_rooms else R.plurals.rooms, rooms, rooms)
    }

    fun getRooms(rooms: Int): String {
        return context.resources.getQuantityString(R.plurals.rooms, rooms, rooms)
    }

    fun getRoomsWithType(rooms: Int, type: String): String {
        return context.resources.getQuantityString(R.plurals.type_rooms, rooms, rooms,
            type.toGermanIfApplicable(deviceLocaleProvider.getDeviceLanguage()))
    }

    fun getGuests(guests: Int): String {
        return context.resources.getQuantityString(R.plurals.guests, guests, guests)
    }

    fun getAdults(adults: Int): String {
        return context.resources.getQuantityString(R.plurals.number_of_adults, adults, adults)
    }

    fun getChildren(children: Int): String {
        return context.resources.getQuantityString(R.plurals.number_of_children, children, children)
    }

    fun getCot(prefix: String?): String {
        return context.resources.getString(R.string.with_cot, prefix)
    }

    fun getInfants(infants: Int): String {
        return context.resources.getQuantityString(R.plurals.number_of_infants, infants, infants)
    }

    val titlesList: List<String>
        get() = context.resources.getStringArray(R.array.titles).toList()

    // Returns a formatted String like Tue 17 Oct - Wed 18 Oct (1 night) • 1 guest 1, 1 room
    fun formattedInputSearchCriteria(input: SearchResultsInput): String {
        val nights = getNights(input.nights())
        val rooms = getRooms(input.numRooms())
        val guests = getGuests(input.totalNumOfGuests())
        val subtitleBuilder = StringBuilder()
        subtitleBuilder.append(getDateRange(input.arrivalDate(),
                input.departureDate()))
                .append(" (").append(nights).append(")")
                .append(" • ")
                .append(guests).append(", ").append(rooms)
        return subtitleBuilder.toString()
    }

    fun formattedSearchCriteria(input: SearchResultsInput?): Pair<String, String> {
        return if (input != null) {
            val editDateLabel = "${input.arrivalDate().format(DateFormat.DAY_MONTH_FORMAT)} " +
                    "${context.getString(R.string.calendar_label_to)} ${input.departureDate().format(DateFormat.DAY_MONTH_FORMAT)}"
            val editGuestLabel = getGuests(input.totalNumOfGuests()).plus(", ")
                .plus(
                    if (input.roomTypeCodes().toHashSet().size <= 1)
                        getRoomsWithType(input.numRooms(), Room.typeLookUp(input.roomTypeCodes().first()).name.lowercase(Locale.getDefault())
                        ) else getRooms(input.numRooms())
                )
            editDateLabel to editGuestLabel
        } else EMPTY_STRING to EMPTY_STRING
    }

    fun formattedCheckInCriteria(booking: Booking): String {
        val rooms = getRooms(booking.numberOfRooms)
        val guests = getGuests(booking.numberOfGuests!!)
        val subtitleBuilder = StringBuilder()
        subtitleBuilder.append(getDateRange(booking.arrivalDate,
                booking.departureDate))
                .append(", ")
                .append(guests).append(", ").append(rooms)
        return subtitleBuilder.toString()
    }

    fun getUpsellDescriptionFromType(upsellItemType: UpsellItemType): String {
        return when (upsellItemType) {
            UpsellItemType.PI_BREAKFAST -> context.getString(R.string.pi_breakfast_label)
            UpsellItemType.OPERA_PI_BREAKFAST -> context.getString(R.string.pi_breakfast_label)
            UpsellItemType.CONTINENTAL_BREAKFAST -> context.getString(R.string.continental_breakfast_label)
            UpsellItemType.OPERA_CONTINENTAL_BREAKFAST -> context.getString(R.string.continental_breakfast_label)
            UpsellItemType.MEAL_DEAL -> context.getString(R.string.payment_breakdown_meal_deal_description)
            UpsellItemType.OPERA_MEAL_DEAL -> context.getString(R.string.payment_breakdown_meal_deal_description)
            UpsellItemType.OPERA_BREAKFAST_BOX -> context.getString(R.string.payment_breakdown_breakfast_box_description)
            UpsellItemType.CARD_PROCESSING_FEE -> context.getString(R.string.review_booking_card_fee)
            UpsellItemType.HUB_BREAKFAST -> context.getString(R.string.payment_breakdown_hub_breakfast_description)
            UpsellItemType.BREAKFAST_BOX -> context.getString(R.string.payment_breakdown_breakfast_box_description)
            else -> ""
        }
    }

    fun getRoomDescription(roomType: String) : String {
        return when (Room.typeLookUp(roomType)) {
            RoomType.ACCESSIBLE -> getRoomTypeString(context.getString(R.string.criteria_room_type_accessible))
            RoomType.DOUBLE -> getRoomTypeString(context.getString(R.string.criteria_room_type_double))
            RoomType.TWIN -> getRoomTypeString(context.getString(R.string.criteria_room_type_twin))
            RoomType.SINGLE -> getRoomTypeString(context.getString(R.string.criteria_room_type_single))
            RoomType.FAMILY -> getRoomTypeString(context.getString(R.string.criteria_room_type_family))
            RoomType.UNKNOWN -> throw IllegalStateException("Unknown room type in criteria")
        }
    }

    fun getRoomTypeString(roomType: String) : String {
        return context.resources.getString(R.string.room_suffix, roomType)
    }

    val unexpectedError = context.getString(R.string.three_cp_unexpected_error)
    fun emptyError(parameter: String) = context.getString(R.string.three_cp_empty_error, parameter)
    fun nullError(parameter: String) = context.getString(R.string.three_cp_null_error, parameter)
    companion object {
        private const val VERSION_PREFIX = "v. "
    }
}