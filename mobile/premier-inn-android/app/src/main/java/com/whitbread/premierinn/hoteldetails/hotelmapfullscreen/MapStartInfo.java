package com.whitbread.premierinn.hoteldetails.hotelmapfullscreen;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;
import com.whitbread.premierinn.api.response.availability.Coordinates;
import com.whitbread.premierinn.domain.hotel.entity.Hotel;

import androidx.annotation.Nullable;

@AutoValue
public abstract class MapStartInfo implements Parcelable {

    public abstract Coordinates hotelCoordinate();

    @Nullable
    public abstract Coordinates searchCoordinate();

    public abstract String hotelName();

    public abstract Hotel.Brand hotelBrand();

    @Nullable
    public abstract String searchTerm();

    public abstract float distance();

    public static Builder builder() {
        return new AutoValue_MapStartInfo.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder hotelCoordinate(Coordinates hotelCoordinate);

        public abstract Builder searchCoordinate(Coordinates searchCoordinate);

        public abstract Builder hotelName(String hotelName);

        public abstract Builder searchTerm(String searchTerm);

        public abstract Builder distance(float distance);

        public abstract Builder hotelBrand(Hotel.Brand hotelBrand);

        public abstract MapStartInfo build();
    }
}
