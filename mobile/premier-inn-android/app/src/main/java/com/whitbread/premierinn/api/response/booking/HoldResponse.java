package com.whitbread.premierinn.api.response.booking;

import androidx.annotation.NonNull;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

@AutoValue
public abstract class HoldResponse {

    @SerializedName("sessionId")
    public abstract String sessionId();

    @NonNull
    public static TypeAdapter<HoldResponse> typeAdapter(Gson gson) {
        return new AutoValue_HoldResponse.GsonTypeAdapter(gson);
    }
}
