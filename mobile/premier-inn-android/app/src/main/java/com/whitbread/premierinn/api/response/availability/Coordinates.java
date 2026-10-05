package com.whitbread.premierinn.api.response.availability;

import android.os.Parcelable;
import androidx.annotation.NonNull;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

@AutoValue
public abstract class Coordinates implements Parcelable {

    @NonNull
    public static TypeAdapter<Coordinates> typeAdapter(Gson gson) {
        return new AutoValue_Coordinates.GsonTypeAdapter(gson);
    }

    public static Coordinates create(float latitude, float longitude) {
        return new AutoValue_Coordinates(latitude, longitude);
    }

    @SerializedName("latitude")
    public abstract float latitude();

    @SerializedName("longitude")
    public abstract float longitude();
}
