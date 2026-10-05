package com.whitbread.premierinn.api.request.booking;

import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

@AutoValue
public abstract class BookingAddress implements Parcelable {

    @SerializedName("countryCode")
    public abstract String countryCode();

    @SerializedName("legacyCountryCode")
    public abstract String legacyCountryCode();
    @SerializedName("line1")
    public abstract String line1();
    @Nullable
    @SerializedName("line2")
    public abstract String line2();
    @SerializedName("line4")
    public abstract String city();
    @SerializedName("postcode")
    public abstract String postcode();

    @NonNull
    public static TypeAdapter<BookingAddress> typeAdapter(Gson gson) {
        return new AutoValue_BookingAddress.GsonTypeAdapter(gson);
    }

    public static BookingAddress create(@NonNull String countryCode, @NonNull String legacyCountryCode, @NonNull String line1,
                                        @Nullable String line2, @NonNull String city, @NonNull String postcode) {
        String notEmptyLine2 = (line2 == null || line2.isEmpty()) ? null : line2;
        return new AutoValue_BookingAddress(countryCode, legacyCountryCode, line1, notEmptyLine2, city, postcode);
    }
}
