package com.whitbread.premierinn.reviewbooking;

import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.whitbread.premierinn.api.request.booking.BookingAddress;
import com.whitbread.premierinn.api.response.AcceptedCreditCard;
import com.whitbread.premierinn.api.response.CardInfo;
import com.whitbread.premierinn.common.ParcelablePrice;
import com.whitbread.premierinn.common.PaymentTimingChoice;
import com.whitbread.premierinn.additionalinformation.entity.EmployeeQuestionsModel;
import com.whitbread.premierinn.paymentdetails.PaymentDetailsInput;

import java.util.List;


@AutoValue
public abstract class ReviewBookingInput implements Parcelable {

    private static final String BUSINESS_CARD = "AT";

    @Nullable
    public abstract PaymentDetailsInput paymentDetailsInput();

    @Nullable
    public abstract String cardUrl();

    @Nullable
    public abstract CardInfo cardInfo();

    @Nullable
    public abstract String cardNumber();

    @Nullable
    public abstract String cvv();

    @Nullable
    public abstract String nameOnCard();

    @Nullable
    public abstract String expiryDate();

    public abstract BookingAddress cardHolderAddress();

    @Nullable
    public abstract PaymentTimingChoice userSelectedPaymentChoice();

    @Nullable
    public abstract ParcelablePrice donation();

    @Nullable
    public abstract ParcelableDonationsDomain operaDonations();

    @Nullable
    public abstract String paypalClientToken();

    @Nullable
    public abstract String selectedCharityPackageCode();

    @Nullable
    public abstract String bookingReference();

    @Nullable
    public abstract String uuidBasketReference();

    @Nullable
    public abstract String startDate();

    @Nullable
    public abstract String issueNumber();

    @Nullable
    public abstract String guestHistoryNumber();

    @Nullable
    public abstract List<AcceptedCreditCard> acceptedCreditCards();

    @Nullable
    public abstract List<AdditionalInformation> additionalInformation();

    @Nullable
    public abstract String cnpPurchaseOrderNumber();

    @Nullable
    public abstract String cnpCustomerReferenceNumber();

    @Nullable
    public abstract String cardType();

    @Nullable
    public abstract String paymentAuthenticationResponse();

    @Nullable
    public abstract BusinessBookingOptions businessBookingOptions();

    @Nullable
    public abstract List<EmployeeQuestionsModel> listOfEmployeeQuestionsModel();

    public boolean isBusinessCard() {
        if (cardType() == null || cardType().isEmpty()) {
            return false;
        } else {
            return cardType().equalsIgnoreCase(BUSINESS_CARD);
        }
    }

    @Nullable
    public abstract Boolean marketingOptIn();

    @Nullable
    public abstract Boolean isBusinessUser();

    @Nullable
    public abstract Boolean isWifiAvailable();

    @Nullable
    public abstract String promoCode();

    @Nullable
    public abstract String promoName();

    @Nullable
    public abstract String rateTag();

    public abstract Builder toBuilder();

    public static Builder builder() {
        return new AutoValue_ReviewBookingInput.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder paymentDetailsInput(PaymentDetailsInput paymentDetailsInput);

        public abstract Builder cardUrl(String cardUrl);

        public abstract Builder cardInfo(CardInfo cardInfo);

        public abstract Builder cardNumber(String cardNumber);

        public abstract Builder cvv(String cvv);

        public abstract Builder nameOnCard(String nameOnCard);

        public abstract Builder expiryDate(String expiryDate);

        public abstract Builder cnpPurchaseOrderNumber(String cnpPurchaseOrderNumber);

        public abstract Builder cnpCustomerReferenceNumber(String cnpCustomerReferenceNumber);

        public abstract Builder cardType(String cardType);

        public abstract Builder cardHolderAddress(BookingAddress cardHolderAddress);

        public abstract Builder userSelectedPaymentChoice(PaymentTimingChoice paymentTimingChoice);

        public abstract Builder startDate(String startDate);

        public abstract Builder issueNumber(String issueNumber);

        public abstract Builder guestHistoryNumber(String guestHistoryNumber);

        public abstract Builder acceptedCreditCards(List<AcceptedCreditCard> acceptedCreditCards);

        public abstract Builder additionalInformation(List<AdditionalInformation> additionalInformation);

        public abstract Builder marketingOptIn(Boolean hasCustomerOptInToMarketing);

        public abstract Builder paymentAuthenticationResponse(String paymentAuthenticationResponse);

        public abstract Builder businessBookingOptions(BusinessBookingOptions businessBookingOptions);

        public abstract Builder listOfEmployeeQuestionsModel(List<EmployeeQuestionsModel> listOfEmployeeQuestionsModel);

        public abstract Builder donation(ParcelablePrice price);

        public abstract Builder operaDonations(ParcelableDonationsDomain parcelableDonationsDomain);

        public abstract Builder paypalClientToken(String paypalClientToken);

        public abstract Builder selectedCharityPackageCode(String operaCharityPackageCode);

        public abstract Builder bookingReference(String bookingReference);

        public abstract Builder uuidBasketReference(String uuidBasketReference);

        public abstract Builder isBusinessUser(Boolean isBusinessUser);

        public abstract Builder isWifiAvailable(Boolean isWifiAvailable);

        public abstract ReviewBookingInput build();

        public abstract Builder promoCode(String pushToken);

        public abstract Builder promoName(String pushToken);

        public abstract Builder rateTag(String pushToken);
    }
}
