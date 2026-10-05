package com.whitbread.premierinn.api.request.booking;

import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;
import com.whitbread.premierinn.guestdetails.view.GuestDetailsFormDataInput;
import com.whitbread.premierinn.reviewbooking.ReviewBookingInput;

@AutoValue
public abstract class Booker implements Parcelable {

    @SerializedName("address")
    public abstract BookingAddress address();

    @SerializedName("emailAddress")
    public abstract String emailAddress();

    @SerializedName("firstName")
    public abstract String firstName();

    @SerializedName("lastName")
    public abstract String lastName();

    @SerializedName("telephoneNumber")
    public abstract String telephoneNumber();

    @SerializedName("title")
    public abstract String title();

    @Nullable
    @SerializedName("guestHistoryNumber")
    public abstract String guestHistoryNumber();

    @NonNull
    public static TypeAdapter<Booker> typeAdapter(Gson gson) {
        return new AutoValue_Booker.GsonTypeAdapter(gson);
    }

    public static Booker create(@NonNull ReviewBookingInput reviewBookingInput) {
        GuestDetailsFormDataInput guestDetailsFormDataInput = reviewBookingInput.paymentDetailsInput().bookerDetails();
        return new AutoValue_Booker(reviewBookingInput.paymentDetailsInput().address(), guestDetailsFormDataInput.email(),
                guestDetailsFormDataInput.firstName(), guestDetailsFormDataInput.lastName(), guestDetailsFormDataInput.phoneNumber(),
                guestDetailsFormDataInput.title(),
                reviewBookingInput.guestHistoryNumber());
    }
}
