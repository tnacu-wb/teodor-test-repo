package com.whitbread.premierinn.summarybreakdown;

import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.google.auto.value.AutoValue;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.common.format.DateFormat;
import com.whitbread.premierinn.common.format.FormatExtensionsKt;
import com.whitbread.premierinn.common.format.PriceFormat;
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider;
import com.whitbread.premierinn.domain.common.RoomType;
import com.whitbread.premierinn.hoteldetails.DailyRateInput;
import com.whitbread.premierinn.common.RoomBooking;
import com.whitbread.premierinn.summary.SummaryExtensionsKt;

import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@AutoValue
public abstract class SummaryBreakdownRoom implements Parcelable {

    private static final Map<String, Integer> ROOM_STRING_ID_FOR_TYPE = new HashMap<>();

    static {
        ROOM_STRING_ID_FOR_TYPE.put(RoomType.DOUBLE.getCode(), R.string.criteria_room_type_double);
        ROOM_STRING_ID_FOR_TYPE.put(RoomType.SINGLE.getCode(), R.string.criteria_room_type_single);
        ROOM_STRING_ID_FOR_TYPE.put(RoomType.TWIN.getCode(), R.string.criteria_room_type_twin);
        ROOM_STRING_ID_FOR_TYPE.put(RoomType.FAMILY.getCode(), R.string.criteria_room_type_family);
        ROOM_STRING_ID_FOR_TYPE.put(RoomType.ACCESSIBLE.getCode(), R.string.criteria_room_type_accessible);
    }

    public static SummaryBreakdownRoom create(String formattedArrivalDate, String formattedDepartureDate,
                                              String roomType, boolean cot, int adults, int children,
                                              String currencyAndPrice, List<DailyRateInput> dailyRates) {
        return new AutoValue_SummaryBreakdownRoom(formattedArrivalDate, formattedDepartureDate, roomType, cot, adults, children,
                currencyAndPrice, dailyRates);
    }

    public static List<SummaryBreakdownRoom> createSummaryBreakdownRooms(@Nullable List<RoomBooking> roomBookings,
                                                                         @Nullable List<RoomBooking> accessibleRoomBookings,
                                                                         @Nullable List<RoomBooking> twinRoomBookings,
                                                                         @NonNull LocalDate arrivalDate,
                                                                         boolean isTaxExempt,
                                                                         @NonNull DeviceLocaleProvider deviceLocaleProvider) {
        List<SummaryBreakdownRoom> summaryRooms = new ArrayList<>();
        List<RoomBooking> allRoomBooking = roomBookings;
         // Fix this must be a more concise way to do this similar to in R&B activity
            if (roomBookings == null) {
                if (accessibleRoomBookings != null && twinRoomBookings != null) {
                    allRoomBooking = SummaryExtensionsKt.addTwoRoomBookingListAndOrderIt(
                            accessibleRoomBookings, twinRoomBookings);
                } else if (accessibleRoomBookings != null) {
                    allRoomBooking = accessibleRoomBookings;
                } else if (twinRoomBookings != null) {
                    allRoomBooking = twinRoomBookings;
                } else {
                    return summaryRooms;
                }
            } else {
                if (accessibleRoomBookings != null && twinRoomBookings != null) {
                    allRoomBooking = SummaryExtensionsKt.addThreeRoomBookingListAndOrderIt(
                            roomBookings, accessibleRoomBookings, twinRoomBookings);
                } else if (accessibleRoomBookings != null) {
                    allRoomBooking = SummaryExtensionsKt.addTwoRoomBookingListAndOrderIt(roomBookings, accessibleRoomBookings);
                } else if (twinRoomBookings != null) {
                    allRoomBooking = SummaryExtensionsKt.addTwoRoomBookingListAndOrderIt(roomBookings, twinRoomBookings);
                }
            }

        for (RoomBooking room : allRoomBooking) {
            String formattedArrivalDate = FormatExtensionsKt.format(arrivalDate, DateFormat.SHORT_DATE_MONTH);
            LocalDate lastNightOfStay = room.getDailyRates().get(room.getDailyRates().size() - 1).getDate();
            LocalDate departureDate = lastNightOfStay.plusDays(1);
            String formattedDepartureDate = FormatExtensionsKt.format(departureDate, DateFormat.SHORT_DATE_MONTH);

            summaryRooms.add(SummaryBreakdownRoom.create(formattedArrivalDate, formattedDepartureDate, room.getType(),
                    room.getCot(), room.getAdults(), room.getChildren(),
                    PriceFormat.format(room.totalRoomPrice(isTaxExempt), deviceLocaleProvider), room.getDailyRates()));
        }

        return summaryRooms;
    }

    public abstract String formattedArrivalDate();

    public abstract String formattedDepartureDate();

    public abstract String roomType();

    public abstract boolean cot();

    public abstract int adults();

    public abstract int children();

    public abstract String currencyAndPrice();

    @Nullable
    public abstract List<DailyRateInput> dailyRates();

    @StringRes
    int getRoomDescriptionStringId() {
        return Objects.requireNonNullElse(ROOM_STRING_ID_FOR_TYPE.get(roomType()), R.string.criteria_room_type_unknown);
    }
}
