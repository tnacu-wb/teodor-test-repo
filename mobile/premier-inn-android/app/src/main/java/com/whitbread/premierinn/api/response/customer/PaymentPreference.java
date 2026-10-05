package com.whitbread.premierinn.api.response.customer;

import android.os.Parcelable;
import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

@AutoValue
public abstract class PaymentPreference implements Parcelable {

    @Nullable
    @SerializedName("paymentCard")
    public abstract PaymentCard paymentCard();

    public static TypeAdapter<PaymentPreference> typeAdapter(Gson gson) {
        return new AutoValue_PaymentPreference.GsonTypeAdapter(gson);
    }

    public static Builder builder() {
        return new AutoValue_PaymentPreference.Builder();
    }

    public abstract Builder toBuilder();

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder paymentCard(PaymentCard paymentCard);

        public abstract PaymentPreference build();
    }
}
