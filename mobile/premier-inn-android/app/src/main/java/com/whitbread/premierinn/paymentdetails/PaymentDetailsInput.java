package com.whitbread.premierinn.paymentdetails;

import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.whitbread.premierinn.api.request.booking.BookingAddress;
import com.whitbread.premierinn.api.response.customer.ParcelableCustomer;
import com.whitbread.premierinn.common.BookingFlowInput;
import com.whitbread.premierinn.common.PaymentProvider;
import com.whitbread.premierinn.common.PaymentTimingChoice;
import com.whitbread.premierinn.guestdetails.view.GuestDetailsFormDataInput;
import com.whitbread.premierinn.reviewbooking.ParcelablePaymentMethodsDetailsInput;

import java.util.List;

@AutoValue
public abstract class PaymentDetailsInput implements Parcelable {

    @NonNull
    public abstract BookingFlowInput bookingFlowInput();

    @Nullable
    public abstract ParcelablePaymentMethodsDetailsInput paymentMethodsDetailInput();

    public abstract GuestDetailsFormDataInput bookerDetails();

    public abstract List<GuestDetailsFormDataInput> guestDetailsList();

    public abstract boolean isBookerStaying();

    public abstract BookingAddress address();

    @Nullable
    public abstract ParcelableCustomer customer();

    @Nullable
    public abstract PaymentTimingChoice paymentTimingChoice();

    public abstract boolean isBusinessTrip();

    public abstract boolean isTaxExempt();

    public abstract boolean marketingOptIn();

    @Nullable
    public abstract PaymentProvider paymentProvider();

    @Nullable
    public abstract String accountPassword();

    @Nullable
    public abstract Boolean savePaymentDetails();

    @Nullable
    public abstract Boolean shouldCreateAccount();

    public abstract Builder toBuilder();

    public static Builder builder() {
        return new AutoValue_PaymentDetailsInput.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder bookingFlowInput(BookingFlowInput bookingFlowInput);

        public abstract Builder paymentMethodsDetailInput(ParcelablePaymentMethodsDetailsInput paymentMethodsDetailInput);

        public abstract Builder bookerDetails(GuestDetailsFormDataInput bookerDetails);

        public abstract Builder guestDetailsList(List<GuestDetailsFormDataInput> guestDetailsList);

        public abstract Builder isBookerStaying(boolean isBookerStaying);

        public abstract Builder address(BookingAddress address);

        public abstract Builder customer(ParcelableCustomer customer);

        public abstract Builder paymentTimingChoice(PaymentTimingChoice paymentTimingChoice);

        public abstract Builder isBusinessTrip(boolean business);

        public abstract Builder isTaxExempt(boolean taxExempt);

        public abstract Builder marketingOptIn(boolean marketingOptIn);

        public abstract Builder paymentProvider(PaymentProvider paymentProvider);

        public abstract Builder accountPassword(String password);

        public abstract Builder shouldCreateAccount(Boolean createAccount);

        public abstract Builder savePaymentDetails(Boolean save);

        public abstract PaymentDetailsInput build();
    }
}
