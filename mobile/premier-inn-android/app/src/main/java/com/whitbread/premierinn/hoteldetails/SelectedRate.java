package com.whitbread.premierinn.hoteldetails;

import static com.whitbread.premierinn.data.common.Constants.EMPTY_STRING;
import static com.whitbread.premierinn.domain.common.Constants.EMPLOYEE_RATE_PLAN_CODE;
import static com.whitbread.premierinn.domain.common.Constants.EMPLOYEE_CODE;

import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.whitbread.premierinn.api.response.availability.BookingRule;
import com.whitbread.premierinn.domain.common.RatePlanOpera;
import com.whitbread.premierinn.domain.graphql.hdp.entity.RoomRateDomain;

import java.util.List;


@AutoValue
public abstract class SelectedRate implements Parcelable {
    public abstract String code();

    public abstract String classification();

    public abstract String rateType();

    public abstract String rateName();

    public abstract String description();

    @Nullable
    public abstract List<BookingRule> bookingRules();

    public abstract boolean prepaymentRequired();

    public abstract boolean guaranteeRequired();

    public abstract boolean cardFeeApplies();

    public static SelectedRate.Builder builder() {
        return new AutoValue_SelectedRate.Builder();
    }

    public static SelectedRate createFrom(RatePlanOpera bookingRatePlan, String rateName, String rateDescription, String rateType) {
        String updatedRateType;
        if (bookingRatePlan.getCellCode().equals(EMPLOYEE_CODE)) {
            updatedRateType = EMPLOYEE_RATE_PLAN_CODE;
        } else {
            updatedRateType = rateType;
        }
        return SelectedRate.builder()
                .rateType(updatedRateType)
                .classification(rateType)
                .rateName(rateName)
                .code(bookingRatePlan.getCode())
                .description(rateDescription)
                .guaranteeRequired(false)
                .prepaymentRequired(false)
                .cardFeeApplies(false)
                .build();
    }

    public static SelectedRate createFrom(RoomRateDomain bookingRatePlan, String rateName, String rateDescription, String rateType) {
        String updatedRateType;
        if (bookingRatePlan.getCellCode().equals(EMPLOYEE_CODE)) {
            updatedRateType = EMPLOYEE_RATE_PLAN_CODE;
        } else {
            updatedRateType = rateType;
        }
        return SelectedRate.builder()
                .rateType(updatedRateType)
                .classification(rateType)
                .rateName(rateName)
                .code(bookingRatePlan.getRatePlanCode())
                .description(rateDescription)
                .guaranteeRequired(false)
                .prepaymentRequired(false)
                .cardFeeApplies(false)
                .build();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder code(String code);

        public abstract Builder rateType(String rateType);

        public abstract Builder classification(String classification);

        public abstract Builder rateName(String rateName);

        public abstract Builder description(String description);

        public abstract Builder bookingRules(List<BookingRule> bookingRules);

        public abstract Builder prepaymentRequired(boolean prepaymentRequired);

        public abstract Builder guaranteeRequired(boolean guaranteeRequired);

        public abstract Builder cardFeeApplies(boolean cardFeeApplies);

        public abstract SelectedRate build();
    }

    public BookingRule getCancellationRule() {
        for (BookingRule bookingRule : bookingRules()) {
            if (bookingRule.isCancellationPolicy()) {
                return bookingRule;
            }
        }
        return null;
    }

    public BookingRule getAmendmentRule() {
        for (BookingRule bookingRule : bookingRules()) {
            if (bookingRule.isAmendmentPolicy()) {
                return bookingRule;
            }
        }
        return null;
    }

    public String rateBookingRules() {
        for (BookingRule bookingRule : bookingRules()) {
            return bookingRule.rateBookingRules();
        }

        return EMPTY_STRING;
    }
}
