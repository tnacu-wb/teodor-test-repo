package com.whitbread.premierinn.data.booking.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.whitbread.premierinn.data.common.EMPTY_STRING
import org.threeten.bp.LocalDate

@Entity(tableName = "booking")
data class BookingEntity(@PrimaryKey
                         @ColumnInfo(name = "booking_reference") val bookingReference: String,
                         @ColumnInfo(name = "lead_guest_surname") val leadGuestSurname: String,
                         @ColumnInfo(name = "arrival_date") val arrivalDate: LocalDate,
                         @ColumnInfo(name = "departure_date") val departureDate: LocalDate,
                         @ColumnInfo(name = "hotel_code") val hotelCode: String,
                         @ColumnInfo(name = "hotel_name") val hotelName: String,
                         @ColumnInfo(name = "num_of_rooms") val numberOfRooms: Int,
                         @ColumnInfo(name = "lead_guest_full_name") val leadGuestFullName: String,
                         @ColumnInfo(name = "rate_class") val rateClass: String,
                         @Embedded(prefix = "total_") val totalCost: PriceEntity? = null,
                         @Embedded(prefix = "prepaid_") val prePaidAmount: PriceEntity? = null,
                         @ColumnInfo(name = "check_in_online_opened") val checkInOnlineOpened: Boolean = false,
                         @ColumnInfo(name = "checked_in") val checkedIn: Boolean = false,
                         @ColumnInfo(name = "canceled") val isCanceled: Boolean = false,
                         @ColumnInfo(name = "amendable") val amendable: Boolean = false,
                         @ColumnInfo(name = "cancellable") val cancellable: Boolean = false,
                         @ColumnInfo(name = "linked_to_account") val isLinkedToAccount: Boolean = false,
                         @ColumnInfo(name = "last_modified") val lastModified: Long = 0,
                         @Embedded(prefix = "amend_restriction_") val amendRestrictions : AmendRestrictionsEntity,
                         @ColumnInfo(name = "business_booker") val businessBooking: Boolean = false,
                         @ColumnInfo(name = "booking_status") val bookingStatus: String,
                         @ColumnInfo(name = "is_check_in_online_available") val isCheckInOnlineAvailable: Boolean,
                         @ColumnInfo(name = "is_check_out_online_available") val isCheckOutOnlineAvailable: Boolean,
                         @ColumnInfo(name = "basket_status") val basketStatus: String? = null,
                         @ColumnInfo(name = "hotel_country") val hotelCountry: String? = EMPTY_STRING,
                         @ColumnInfo(name = "employee_booking") val employeeBooking: Boolean = false,
                         @ColumnInfo(name = "is_third_party_booking") val isThirdPartyBooking: Boolean = false)


data class PriceEntity(@ColumnInfo(name = "cost_amount") val amount: Float, @ColumnInfo(name = "cost_currency") val currency: String)

data class AmendRestrictionsEntity(@ColumnInfo(name = "nights") val nights: Boolean,
                                   @ColumnInfo(name = "rooms") val rooms: Boolean,
                                   @ColumnInfo(name = "guest_names") val guestNames: Boolean,
                                   @ColumnInfo(name = "upsell") val upsell: Boolean,
                                   @ColumnInfo(name = "restricted") val restricted: Boolean)