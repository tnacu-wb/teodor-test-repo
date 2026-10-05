package com.whitbread.premierinn.api.response.customer;

import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

@AutoValue
public abstract class BookingPreference implements Parcelable {

    private static final String LEISURE_TRIP_TYPE = "LEISURE";
    private static final String BUSINESS_TRIP_TYPE = "BUSINESS";

    @Nullable
    @SerializedName("roomRequirements")
    public abstract RoomRequirements roomRequirements();

    @SerializedName("foodPreference")
    @Nullable
    public abstract Integer foodPreference();

    @Nullable
    @SerializedName("reason")
    public abstract String tripType();


    public boolean isLeisureTrip() {
        return LEISURE_TRIP_TYPE.equals(tripType());
    }

    public boolean isBusinessTrip() {
        return BUSINESS_TRIP_TYPE.equals(tripType());
    }

    public static TypeAdapter<BookingPreference> typeAdapter(Gson gson) {
        return new AutoValue_BookingPreference.GsonTypeAdapter(gson);
    }

    public static Builder builder() {
        return new AutoValue_BookingPreference.Builder();
    }

    public abstract BookingPreference.Builder toBuilder();

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder roomRequirements(RoomRequirements roomRequirements);

        public abstract Builder foodPreference(Integer foodPreference);

        public abstract Builder tripType(String tripType);

        public abstract BookingPreference build();

    }
}