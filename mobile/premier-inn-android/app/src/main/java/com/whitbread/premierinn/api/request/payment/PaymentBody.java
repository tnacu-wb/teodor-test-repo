package com.whitbread.premierinn.api.request.payment;

import static com.whitbread.premierinn.domain.common.PriceDomainKt.GBP_LABEL;

import androidx.annotation.NonNull;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;
import com.whitbread.premierinn.api.request.booking.Booker;
import com.whitbread.premierinn.api.request.booking.Breakfast;
import com.whitbread.premierinn.api.request.booking.Guest;
import com.whitbread.premierinn.api.request.booking.PaymentCard;
import com.whitbread.premierinn.common.mapper.CommonMappersKt;
import com.whitbread.premierinn.data.remote.ApiCommon;
import com.whitbread.premierinn.reviewbooking.ReviewBookingInput;

import java.util.List;

@AutoValue
public abstract class PaymentBody {

    @SerializedName("booker")
    public abstract Booker booker();

    @SerializedName("breakfasts")
    public abstract List<Breakfast> breakfasts();

    @SerializedName("donation")
    public abstract ApiCommon.Price donation();

    @SerializedName("guests")
    public abstract List<Guest> guests();

    @SerializedName("paymentCard")
    public abstract PaymentCard paymentCard();

    @SerializedName("business")
    public abstract boolean business();

    @NonNull
    public static TypeAdapter<PaymentBody> typeAdapter(Gson gson) {
        return new AutoValue_PaymentBody.GsonTypeAdapter(gson);
    }

    public static PaymentBody create(@NonNull ReviewBookingInput reviewBookingInput,
                                     boolean prepaymentRequired) {
        Booker booker = Booker.create(reviewBookingInput);
        List<Guest> guests = Guest.createGuests(reviewBookingInput.paymentDetailsInput().guestDetailsList());
        PaymentCard paymentCard = PaymentCard.create(reviewBookingInput, prepaymentRequired);
        List<Breakfast> breakfasts = reviewBookingInput.paymentDetailsInput().bookingFlowInput().breakfasts();

        ApiCommon.Price donation = reviewBookingInput.donation() == null
                ? new ApiCommon.Price(0f, GBP_LABEL)
                : CommonMappersKt.toApiPrice(reviewBookingInput.donation());

        return new AutoValue_PaymentBody(booker, breakfasts, donation, guests, paymentCard,
                reviewBookingInput.paymentDetailsInput().isBusinessTrip());
    }
}
