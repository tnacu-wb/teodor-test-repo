package com.whitbread.premierinn.hoteldetails.hotelfulldescription;

import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class HotelFullDescription implements Parcelable {

    public abstract String name();
    public abstract String description();
    public abstract String directions();
    @Nullable
    public abstract String parkingDescription();

    public static HotelFullDescription create(@NonNull String name, @NonNull String description, @NonNull String directions,
                                              @Nullable String parkingDescription) {
        return new AutoValue_HotelFullDescription(name, description, directions, parkingDescription);
    }

}
