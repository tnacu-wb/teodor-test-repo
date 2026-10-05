package com.whitbread.premierinn.api.response.booking;

import android.os.Parcelable;
import androidx.annotation.NonNull;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

import org.threeten.bp.LocalDate;

@AutoValue
public abstract class DailyRate implements Parcelable {

    @NonNull
    public static TypeAdapter<DailyRate> typeAdapter(Gson gson) {
        return new AutoValue_DailyRate.GsonTypeAdapter(gson);
    }

    @NonNull
    public static DailyRate create(LocalDate date, BookingPrice price) {
        return new AutoValue_DailyRate(date, price);
    }

    @SerializedName("date")
    public abstract LocalDate date();

    @SerializedName("price")
    public abstract BookingPrice price();
}
