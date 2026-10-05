package com.whitbread.premierinn.api.response.availability;

import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@AutoValue
public abstract class HotelImage implements Parcelable {

    @NonNull
    public static TypeAdapter<HotelImage> typeAdapter(Gson gson) {
        return new AutoValue_HotelImage.GsonTypeAdapter(gson);
    }

    @SerializedName("fileReference")
    @Nullable
    public abstract String fileReference();

    @SerializedName("tags")
    @Nullable
    public abstract List<String> tags();
}
