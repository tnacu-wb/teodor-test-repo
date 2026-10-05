package com.whitbread.premierinn.api.request.booking;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

import org.threeten.bp.LocalDate;

import java.util.List;

import androidx.annotation.NonNull;

@AutoValue
public abstract class HoldBody {

    @SerializedName("arrival")
    public abstract LocalDate arrival();
    @SerializedName("departure")
    public abstract LocalDate departure();
    @SerializedName("hotelCode")
    public abstract String hotelCode();
    @SerializedName("rateCode")
    public abstract String rateCode();
    @SerializedName("rooms")
    public abstract List<HoldBodyRoom> rooms();

    public static HoldBody create(@NonNull LocalDate arrival, @NonNull LocalDate departure, @NonNull String hotelCode,
                                  @NonNull String rateCode, @NonNull List<HoldBodyRoom> holdBodyRooms) {
        return new AutoValue_HoldBody(arrival, departure, hotelCode, rateCode, holdBodyRooms);
    }

    @NonNull
    public static TypeAdapter<HoldBody> typeAdapter(Gson gson) {
        return new AutoValue_HoldBody.GsonTypeAdapter(gson);
    }
}
