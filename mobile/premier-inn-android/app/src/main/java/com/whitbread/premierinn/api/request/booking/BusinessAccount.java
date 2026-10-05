package com.whitbread.premierinn.api.request.booking;

import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;
import com.whitbread.premierinn.api.response.booking.BookingPrice;
import com.whitbread.premierinn.reviewbooking.BusinessBookingOptions;
import com.whitbread.premierinn.reviewbooking.ReviewBookingInput;

@AutoValue
public abstract class BusinessAccount implements Parcelable {

    @SerializedName("alcoholAllowed")
    public abstract boolean alcoholAllowed();

    @Nullable
    @SerializedName("atosPassword")
    public abstract String atosPassword();

    @Nullable
    @SerializedName("breakfastCode")
    public abstract String breakfastCode();

    @SerializedName("carParkingAllowed")
    public abstract boolean carParkingAllowed();

    @SerializedName("cardNotPresentAuth")
    public abstract boolean cardNotPresentAuth();

    @Nullable
    @SerializedName("customerReference")
    public abstract String customerReference();

    @Nullable
    @SerializedName("dinnerAllowance")
    public abstract BookingPrice dinnerAllowance();

    @SerializedName("otherChargesAllowed")
    public abstract boolean otherChargesAllowed();

    @Nullable
    @SerializedName("purchaseOrder")
    public abstract String purchaseOrder();

    @SerializedName("wifiAccessAllowed")
    public abstract boolean wifiAccessAllowed();

    @NonNull
    public static TypeAdapter<BusinessAccount> typeAdapter(Gson gson) {
        return new AutoValue_BusinessAccount.GsonTypeAdapter(gson);
    }

    public static BusinessAccount create(@NonNull ReviewBookingInput reviewBookingInput) {

        BusinessBookingOptions businessBookingOptions = reviewBookingInput.businessBookingOptions();

        if (businessBookingOptions == null) {
            return null;
        }

        return new AutoValue_BusinessAccount(businessBookingOptions.alcoholAllowed(),
                businessBookingOptions.atosPassword(),
                businessBookingOptions.breakfastCode(),
                businessBookingOptions.carParkingAllowed(),
                businessBookingOptions.cardNotPresentAuth(),
                businessBookingOptions.customerReference(),
                businessBookingOptions.dinnerAllowance(),
                false,
                businessBookingOptions.purchaseOrder(),
                businessBookingOptions.wifiAccessAllowed());

    }
}