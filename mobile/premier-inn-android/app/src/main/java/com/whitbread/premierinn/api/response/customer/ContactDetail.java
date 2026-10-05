package com.whitbread.premierinn.api.response.customer;

import android.os.Parcelable;
import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

@AutoValue
public abstract class ContactDetail implements Parcelable {

    public abstract String firstName();

    public abstract String lastName();

    public abstract CustomerAddress address();

    @Nullable
    public abstract String telephone();

    @Nullable
    public abstract String mobile();

    public abstract String title();

    public abstract String email();

    @Nullable
    @SerializedName("nationality")
    public abstract String nationality();

    @Nullable
    @SerializedName("passport")
    public abstract Passport passport();

    @Nullable
    @SerializedName("carRegistration")
    public abstract String carRegistration();

    public static TypeAdapter<ContactDetail> typeAdapter(Gson gson) {
        return new AutoValue_ContactDetail.GsonTypeAdapter(gson);
    }

    public static Builder builder() {
        return new AutoValue_ContactDetail.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder firstName(String firstName);

        public abstract Builder lastName(String lastName);

        public abstract Builder address(CustomerAddress address);

        public abstract Builder telephone(String telephone);

        public abstract Builder mobile(String mobile);

        public abstract Builder title(String title);

        public abstract Builder email(String email);

        public abstract Builder carRegistration(String carRegistration);

        public abstract Builder nationality(String nationality);

        public abstract Builder passport(Passport passport);

        public abstract ContactDetail build();
    }
}