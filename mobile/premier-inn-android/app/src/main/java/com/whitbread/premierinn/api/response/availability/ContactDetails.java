package com.whitbread.premierinn.api.response.availability;

import android.os.Parcelable;
import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

@AutoValue
public abstract class ContactDetails implements Parcelable {

    @SerializedName("phone")
    @Nullable
    public abstract String phone(); // NOT free of charge use it before booking

    @SerializedName("hotelNationalPhone")
    @Nullable
    public abstract String hotelNationalPhone(); // free of charge - use it after booking

    public static TypeAdapter<ContactDetails> typeAdapter(Gson gson) {
        return new AutoValue_ContactDetails.GsonTypeAdapter(gson);
    }

}
