package com.whitbread.premierinn.reviewbooking;

import static com.whitbread.premierinn.common.format.DateFormat.MONTH_YEAR;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.whitbread.premierinn.common.mapper.CommonMappersKt;
import com.whitbread.premierinn.common.utils.DateUtils;
import com.whitbread.premierinn.domain.common.PriceDomain;

import java.text.ParseException;
import java.util.Date;

@AutoValue
abstract class CardSummaryModel {

    private static final int DISPLAYED_DIGIT_LENGTH = 4;
    private static final String EXPIRY_DATE_SEPARATOR = "/";
    private static final int EXPIRY_DATE_SEPARATOR_INDEX = 2;

    abstract String cardLegend();

    @Nullable
    abstract PriceDomain fee();

    abstract String cardUrl();

    abstract String lastFourDigits();

    abstract String bookerName();

    abstract String expiryDate();

    static CardSummaryModel create(@NonNull ReviewBookingInput reviewBookingInput) {
        String cardLegend = reviewBookingInput.cardInfo() != null ? reviewBookingInput.cardInfo().cardLegend() : "";
        PriceDomain fee = reviewBookingInput.cardInfo() != null && reviewBookingInput.cardInfo().cardFeeAmount() != null
                ? CommonMappersKt.toPriceDomain(reviewBookingInput.cardInfo().cardFeeAmount())
                : null;
        String cardUrl = reviewBookingInput.cardUrl();

        String lastFourDigits = reviewBookingInput.cardNumber().substring(
                reviewBookingInput.cardNumber().length() - DISPLAYED_DIGIT_LENGTH);
        String nameOnCard = reviewBookingInput.nameOnCard();
        String expiryDate = reviewBookingInput.expiryDate().substring(0, EXPIRY_DATE_SEPARATOR_INDEX) + EXPIRY_DATE_SEPARATOR
                + reviewBookingInput.expiryDate().substring(EXPIRY_DATE_SEPARATOR_INDEX);

        return new AutoValue_CardSummaryModel(cardLegend, fee, cardUrl, lastFourDigits, nameOnCard, expiryDate);
    }


    public boolean hasCardExpired(long currentTime) {
        Date currentDate = DateUtils.resetDayToFirstOfTheMonth(currentTime);

        Date expiryDate;
        try {
            expiryDate = MONTH_YEAR.parse(expiryDate()); // defaults to 01/MM/YYYY
        } catch (ParseException e) {
            throw new IllegalStateException("INVALID CARD EXPIRY DATE " + expiryDate());
        }

        return currentDate.after(expiryDate);
    }

}