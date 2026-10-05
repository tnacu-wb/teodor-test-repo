package com.whitbread.premierinn.api.response.availability;

import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

@AutoValue
public abstract class TripAdvisor implements Parcelable {
    @NonNull
    public static TypeAdapter<TripAdvisor> typeAdapter(Gson gson) {
        return new AutoValue_TripAdvisor.GsonTypeAdapter(gson);
    }

    @Nullable
    @SerializedName("ratingImageUrl")
    public abstract String ratingImageUrl();

    @SerializedName("sampleSize")
    @Nullable
    public abstract Integer sampleSize();

    public boolean hasData() {
        return ((ratingImageUrl() != null && !ratingImageUrl().isEmpty()) || sampleSize() != null);
    }

}

