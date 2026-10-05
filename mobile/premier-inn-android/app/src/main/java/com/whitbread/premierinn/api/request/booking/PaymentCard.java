package com.whitbread.premierinn.api.request.booking;

import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;
import com.whitbread.premierinn.reviewbooking.ReviewBookingInput;

@AutoValue
public abstract class PaymentCard implements Parcelable {

    @Nullable
    @SerializedName("cardNumber")
    public abstract String cardNumber();

    @Nullable
    @SerializedName("cardSecurityCode")
    public abstract String cardSecurityCode();

    @Nullable
    @SerializedName("cardType")
    public abstract String cardType();

    @Nullable
    @SerializedName("cardholderName")
    public abstract String cardholderName();

    @Nullable
    @SerializedName("expiryDate")
    public abstract String expiryDate();

    @Nullable
    @SerializedName("startDate")
    public abstract String startDate();

    @Nullable
    @SerializedName("issueNumber")
    public abstract String issueNumber();

    @SerializedName("prepaymentRequired")
    public abstract boolean prepaymentRequired();

    @Nullable
    @SerializedName("billingAddress")
    public abstract BookingAddress billingAddress();

    @Nullable
    @SerializedName("businessAccount")
    public abstract BusinessAccount businessAccount();

    @SerializedName("useExistingCard")
    public abstract boolean useExistingCard();

    @Nullable
    @SerializedName("paymentAuthenticationResponse")
    public abstract String paymentAuthenticationResponse();

    @NonNull
    public static TypeAdapter<PaymentCard> typeAdapter(Gson gson) {
        return new AutoValue_PaymentCard.GsonTypeAdapter(gson);
    }

    public static PaymentCard create(@NonNull ReviewBookingInput reviewBookingInput, boolean prepaymentRequired) {

        BusinessAccount businessAccount = BusinessAccount.create(reviewBookingInput);

        // cardInfo should be null when we're processing a logged user with a stored card
        if (reviewBookingInput.cardInfo() == null) {
            return new AutoValue_PaymentCard(null,
                    reviewBookingInput.cvv(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    prepaymentRequired,
                    reviewBookingInput.cardHolderAddress(),
                    businessAccount,
                    true,
                    reviewBookingInput.paymentAuthenticationResponse());

        } else {
            String startDate = reviewBookingInput.startDate() == null
                    || reviewBookingInput.startDate().isEmpty() ? null
                    : reviewBookingInput.startDate().substring(0, 2) + "/" + reviewBookingInput.startDate().substring(2, 4);

            return new AutoValue_PaymentCard(reviewBookingInput.cardNumber(),
                    reviewBookingInput.cvv(),
                    reviewBookingInput.cardInfo().cardType(),
                    reviewBookingInput.nameOnCard(),
                    reviewBookingInput.expiryDate().substring(0, 2) + "/" + reviewBookingInput.expiryDate().substring(2, 4),
                    startDate,
                    reviewBookingInput.issueNumber(),
                    prepaymentRequired,
                    reviewBookingInput.cardHolderAddress(),
                    businessAccount,
                    false, // TODO remove hardcoded value when implement that feature
                    reviewBookingInput.paymentAuthenticationResponse());
        }
    }
}