package com.whitbread.premierinn.api.response.customer;

import android.os.Parcelable;
import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

@AutoValue
public abstract class Passport implements Parcelable {
    @Nullable
    @SerializedName("number")
    public abstract String number();

    @Nullable
    @SerializedName("countryOfIssue")
    public abstract String countryOfIssue();

    public static TypeAdapter<Passport> typeAdapter(Gson gson) {
        return new AutoValue_Passport.GsonTypeAdapter(gson);
    }

    public static Passport.Builder builder() {
        // Setting empty string as default so that it's consistent with what we receive if there's no passport information.
        // This helps with comparison check for analytics, where we see if we've changed anything.
        return new AutoValue_Passport.Builder().number("").countryOfIssue("");
    }

    public abstract Passport.Builder toBuilder();

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Passport.Builder number(String passportNumber);

        public abstract Passport.Builder countryOfIssue(String countryOfIssue);

        public abstract Passport build();
    }
}