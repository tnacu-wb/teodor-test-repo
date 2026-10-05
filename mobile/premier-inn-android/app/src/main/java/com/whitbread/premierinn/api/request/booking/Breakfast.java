package com.whitbread.premierinn.api.request.booking;

import static com.whitbread.premierinn.domain.common.Constants.OPERA_FREE_CHILD_BREAKFAST;

import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;
import com.whitbread.premierinn.api.response.availability.UpsellItem;
import com.whitbread.premierinn.common.summary.model.SummaryRoomItem;
import com.whitbread.premierinn.common.RoomBooking;

import java.util.ArrayList;
import java.util.List;

@AutoValue
public abstract class Breakfast implements Parcelable {

    @SerializedName("adults")
    public abstract int adults();

    @SerializedName("children")
    public abstract int children();

    @SerializedName("code")
    public abstract String code();

    @SerializedName("roomNumber")
    public abstract int roomNumber();

    @NonNull
    public static TypeAdapter<Breakfast> typeAdapter(Gson gson) {
        return new AutoValue_Breakfast.GsonTypeAdapter(gson);
    }

    public static List<Breakfast> createBreakfasts(@Nullable UpsellItem upsellItem, @NonNull List<RoomBooking> rooms) {
        List<Breakfast> breakfastList = new ArrayList<>();
        if (upsellItem != null) {
            for (RoomBooking room : rooms) {
                int payingChildren = 0;
                if (upsellItem.availableForChildren() && !upsellItem.freeBreakfastOption()) {
                    payingChildren = room.getChildren();
                } else {
                    if (room.getChildren() != 0) {
                        breakfastList.add(new AutoValue_Breakfast(0, room.getChildren(),
                                upsellItem.freeBreakfastCode(), room.getRoomNumber()));
                    }
                }
                Breakfast breakfast = new AutoValue_Breakfast(room.getAdults(), payingChildren, upsellItem.code(), room.getRoomNumber());
                breakfastList.add(breakfast);
            }
        }
        return breakfastList;
    }

    public static List<Breakfast> createBreakfasts(@NonNull List<SummaryRoomItem> rooms) {
        List<Breakfast> breakfastList = new ArrayList<>();
        for (SummaryRoomItem room : rooms) {
            int roomNumber = Integer.parseInt(room.getRoomNumber());

            // Child breakfast (only once)
            if (room.getChildren() > 0
                    && room.getMeals().stream().anyMatch(mealItem -> mealItem.getKidsEatFree() && mealItem.getCounter() > 0)) {

                breakfastList.add(new AutoValue_Breakfast(
                        0,
                        room.getChildren(),
                        OPERA_FREE_CHILD_BREAKFAST,
                        roomNumber
                ));
            }

            // Adult breakfasts
            room.getMeals().stream()
                    .filter(m -> m.getCounter() > 0 && !m.getKidsEatFree())
                    .forEach(m -> breakfastList.add(new AutoValue_Breakfast(
                            m.getCounter(),
                            0,
                            m.getId(),
                            roomNumber
                    )));
        }
        return breakfastList;
    }
}