package com.whitbread.premierinn.api.request.booking;

import androidx.annotation.NonNull;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;
import com.whitbread.premierinn.common.RoomBooking;

@AutoValue
public abstract class HoldBodyRoom {

    @SerializedName("adults")
    public abstract int adults();

    @SerializedName("children")
    public abstract int children();

    @SerializedName("cot")
    public abstract boolean cot();

    @SerializedName("type")
    public abstract String type();

    @SerializedName("lettingType")
    public abstract String lettingType();

    @SerializedName("roomNumber")
    public abstract int roomNumber();

    public static HoldBodyRoom create(RoomBooking room) {
        return new AutoValue_HoldBodyRoom(room.getAdults(), room.getChildren(), room.getCot(), room.getType(),
                room.getLettingType().getCode(), room.getRoomNumber());
    }

    @NonNull
    public static TypeAdapter<HoldBodyRoom> typeAdapter(Gson gson) {
        return new AutoValue_HoldBodyRoom.GsonTypeAdapter(gson);
    }
}
