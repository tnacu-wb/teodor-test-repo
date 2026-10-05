package com.whitbread.premierinn.api.request.booking;

import static com.whitbread.premierinn.domain.common.PriceDomainKt.GBP_LABEL;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;
import com.whitbread.premierinn.common.mapper.CommonMappersKt;
import com.whitbread.premierinn.data.remote.ApiCommon;
import com.whitbread.premierinn.reviewbooking.ReviewBookingInput;

import java.util.List;

@AutoValue
public abstract class BookingBody {

    @SerializedName("booker")
    public abstract Booker booker();

    @SerializedName("guests")
    public abstract List<Guest> guests();

    @SerializedName("paymentCard")
    public abstract PaymentCard paymentCard();

    @SerializedName("breakfasts")
    public abstract List<Breakfast> breakfasts();

    @SerializedName("donation")
    public abstract ApiCommon.Price donation();

    @SerializedName("business")
    public abstract boolean business();

    @Nullable
    @SerializedName("paymentId")
    public abstract String paymentId();

    @NonNull
    public static TypeAdapter<BookingBody> typeAdapter(Gson gson) {
        return new AutoValue_BookingBody.GsonTypeAdapter(gson);
    }

    public static BookingBody create(@NonNull ReviewBookingInput reviewBookingInput, boolean prepaymentRequired, String paymentId) {
        Booker booker = Booker.create(reviewBookingInput);
        List<Guest> guests = Guest.createGuests(reviewBookingInput.paymentDetailsInput().guestDetailsList());
        PaymentCard paymentCard = PaymentCard.create(reviewBookingInput, prepaymentRequired);
        List<Breakfast> breakfasts = reviewBookingInput.paymentDetailsInput().bookingFlowInput().breakfasts();
        ApiCommon.Price donation = reviewBookingInput.donation() != null
                ? CommonMappersKt.toApiPrice(reviewBookingInput.donation())
                : new ApiCommon.Price(0f, GBP_LABEL);

        return new AutoValue_BookingBody(booker, guests, paymentCard, breakfasts, donation,
                reviewBookingInput.paymentDetailsInput().isBusinessTrip(), paymentId);
    }
}
