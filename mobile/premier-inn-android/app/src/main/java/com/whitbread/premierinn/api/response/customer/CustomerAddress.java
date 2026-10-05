package com.whitbread.premierinn.api.response.customer;

import android.os.Parcelable;
import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

@AutoValue
public abstract class CustomerAddress implements Parcelable {

    @Nullable
    @SerializedName("line5")
    public abstract String line5();

    @Nullable
    @SerializedName("line4")
    public abstract String line4();

    @SerializedName("countryCode")
    public abstract String countryCode();

    @Nullable
    @SerializedName("companyName")
    public abstract String companyName();

    @SerializedName("postCode")
    public abstract String postCode();

    @Nullable
    @SerializedName("line3")
    public abstract String line3();

    @SerializedName("type")
    public abstract String type();

    @Nullable
    @SerializedName("line2")
    public abstract String line2();

    @SerializedName("line1")
    public abstract String line1();

    public boolean isCompanyAddress() {
        return !companyName().isEmpty();
    }

    public static CustomerAddress.Builder builder() {
        return new AutoValue_CustomerAddress.Builder();
    }

    public abstract CustomerAddress.Builder toBuilder();

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract CustomerAddress.Builder line1(String line1);
        public abstract CustomerAddress.Builder line2(String line2);
        public abstract CustomerAddress.Builder line3(String line3);
        public abstract CustomerAddress.Builder line4(String line4);
        public abstract CustomerAddress.Builder line5(String line5);
        public abstract CustomerAddress.Builder postCode(String postcode);
        public abstract CustomerAddress.Builder countryCode(String countryCode);
        public abstract CustomerAddress.Builder companyName(String companyName);
        public abstract CustomerAddress.Builder type(String type);
        public abstract CustomerAddress build();
    }

    public static TypeAdapter<CustomerAddress> typeAdapter(Gson gson) {
        return new AutoValue_CustomerAddress.GsonTypeAdapter(gson);
    }
}