package com.whitbread.premierinn.api.response.customer;

import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;
import com.whitbread.premierinn.criteria.model.RoomConfiguration;
import com.whitbread.premierinn.criteria.view.CotState;
import com.whitbread.premierinn.domain.common.RoomType;

@AutoValue
public abstract class RoomRequirements implements Parcelable {

    public static final String PREMIER_INN = "PI";

    @SerializedName("adults")
    public abstract int adults();

    @SerializedName("children")
    public abstract int children();

    @SerializedName("cotRequired")
    public abstract boolean cotRequired();

    @SerializedName("smoking")
    public abstract boolean smoking();

    @SerializedName("roomNumber")
    public abstract int roomNumber();

    @Nullable
    @SerializedName("hotelBrand")
    public abstract String hotelBrand();

    @Nullable
    @SerializedName("type")
    public abstract String type();

    @Nullable
    @SerializedName("lettingType")
    public abstract String lettingType();

    public boolean isPIPreference() {
        return PREMIER_INN.equals(hotelBrand());
    }

    public static TypeAdapter<RoomRequirements> typeAdapter(Gson gson) {
        return new AutoValue_RoomRequirements.GsonTypeAdapter(gson);
    }

    public static Builder builder() {
        return new AutoValue_RoomRequirements.Builder()
                .hotelBrand(PREMIER_INN)
                .smoking(false);
    }

    public abstract Builder toBuilder();

    public static RoomRequirements defaults() {
        return builder()
                .adults(1)
                .children(0)
                .roomNumber(0)
                .cotRequired(false)
                .type(RoomType.DOUBLE.getCode())
                .build();
    }

    public static RoomRequirements buildFromRoomConfiguration(@NonNull RoomConfiguration roomConfiguration) {
        return builder()
                .roomNumber(0)
                .type(roomConfiguration.roomType().name())
                .cotRequired(roomConfiguration.cot() == CotState.SELECTED)
                .adults(roomConfiguration.adults().value())
                .children(roomConfiguration.children().value()).build();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder adults(int adults);

        public abstract Builder children(int children);

        public abstract Builder cotRequired(boolean cotRequired);

        public abstract Builder smoking(boolean smoking);

        public abstract Builder roomNumber(int roomNumber);

        public abstract Builder hotelBrand(String hotelBrand);

        public abstract Builder type(String type);

        public abstract Builder lettingType(String lettingType);

        public abstract RoomRequirements build();
    }
}