package com.whitbread.premierinn.api.response.booking;

import androidx.annotation.NonNull;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

@AutoValue
public abstract class BookingReleaseResponse {
    @SerializedName("releaseSuccessful")
    public abstract boolean releaseSuccessful();

    @NonNull
    public static TypeAdapter<BookingReleaseResponse> typeAdapter(Gson gson) {
        return new AutoValue_BookingReleaseResponse.GsonTypeAdapter(gson);
    }

}
