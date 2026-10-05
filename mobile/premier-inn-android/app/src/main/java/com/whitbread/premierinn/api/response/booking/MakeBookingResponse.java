package com.whitbread.premierinn.api.response.booking;

import androidx.annotation.NonNull;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

@AutoValue
public abstract class MakeBookingResponse {

    @NonNull
    public static TypeAdapter<MakeBookingResponse> typeAdapter(Gson gson) {
        return new AutoValue_MakeBookingResponse.GsonTypeAdapter(gson);
    }

    @SerializedName("confirmationNumber")
    public abstract String confirmationNumber();

    @SerializedName("prepaymentSuccess")
    public abstract boolean prepaymentSuccess();
}
