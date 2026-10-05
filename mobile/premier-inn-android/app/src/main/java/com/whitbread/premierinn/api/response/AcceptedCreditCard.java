package com.whitbread.premierinn.api.response;

import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

@AutoValue
public abstract class AcceptedCreditCard implements Parcelable {

    @SerializedName("code")
    @Nullable
    public abstract String creditCardCode();

    @SerializedName("feeAmount")
    @Nullable
    public abstract String feeAmount();

    @SerializedName("feeCurrency")
    @Nullable
    public abstract String feeCurrency();

    @SerializedName("listOrder")
    @Nullable
    public abstract Integer listOrder();

    @SerializedName("paymentOnly")
    @Nullable
    public abstract Boolean paymentOnly();

    @SerializedName("name")
    @Nullable
    public abstract String name();

    @SerializedName("schemeLogo")
    @Nullable
    public abstract String schemeLogo();

    @NonNull
    public static TypeAdapter<AcceptedCreditCard> typeAdapter(Gson gson) {
        return new AutoValue_AcceptedCreditCard.GsonTypeAdapter(gson);
    }

    public static Builder builder() {
        return new AutoValue_AcceptedCreditCard.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder creditCardCode(String creditCardCode);

        public abstract Builder feeAmount(String feeAmount);

        public abstract Builder feeCurrency(String feeCurrency);

        public abstract Builder listOrder(Integer listOrder);

        public abstract Builder paymentOnly(Boolean paymentOnly);

        public abstract Builder name(String name);

        public abstract Builder schemeLogo(String schemeLogo);

        public abstract AcceptedCreditCard build();
    }
}
