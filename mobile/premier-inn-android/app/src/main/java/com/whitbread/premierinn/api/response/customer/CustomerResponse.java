package com.whitbread.premierinn.api.response.customer;

import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

@AutoValue
public abstract class CustomerResponse {

    @Nullable
    @SerializedName("customerId")
    public abstract String customerId();

    @SerializedName("success")
    public abstract boolean success();

    public static TypeAdapter<CustomerResponse> typeAdapter(Gson gson) {
        return new AutoValue_CustomerResponse.GsonTypeAdapter(gson);
    }
}