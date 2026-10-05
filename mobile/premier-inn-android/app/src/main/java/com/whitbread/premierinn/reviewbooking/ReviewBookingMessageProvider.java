package com.whitbread.premierinn.reviewbooking;


import android.content.Context;

import com.whitbread.premierinn.R;
import com.whitbread.premierinn.api.ErrorCodesKt;
import com.whitbread.premierinn.common.analytics.AnalyticsConstants;
import com.whitbread.premierinn.common.utils.StringUtils;
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository;
import com.whitbread.premierinn.domain.resource.usecase.GetStringResource;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;

public class ReviewBookingMessageProvider {

    private final Context context;

    public ReviewBookingMessageProvider(@NonNull Context context) {
        this.context = context;
    }

    String getErrorDinnerAllowanceEmptyField() {
        return context.getString(R.string.review_booking_dinner_allowance_error_empty_field);
    }

    String getErrorDinnerAllowanceMaxValue() {
        return context.getString(R.string.review_booking_dinner_allowance_error_max_value);
    }

    String getErrorDinnerAllowanceMinValue() {
        return context.getString(R.string.review_booking_dinner_allowance_error_min_value);
    }

    @NotNull
    public String policyAmendAndCancelAllowStandard() {
        return context.getString(R.string.review_booking_amend_cancel_allow_standard);
    }

    @NotNull
    public String policyAmendAndCancelDontAllow() {
        return context.getString(R.string.review_booking_amend_cancel_dont_allow);
    }

    @NotNull
    public String policyAmendAndCancelOnlyAllowWithInXHoursOfBooking(int hours) {
        return context.getString(R.string.review_booking_amend_cancel_only_allow_x_hours_booking, hours);
    }

    @NotNull
    public String policyAmendAndCancelAllowWithInXDaysOfArrival(int days) {
        return context.getString(R.string.review_booking_amend_cancel_allow_x_days_arrival, days);
    }

    @NotNull
    public String policyAmendAllowedStandard() {
        return context.getString(R.string.review_booking_amend_allowed_standard);
    }

    @NotNull
    public String policyCancelAllowedStandard() {
        return context.getString(R.string.review_booking_cancel_allowed_standard);
    }

    @NotNull
    public String policyAmendNotAllowed() {
        return context.getString(R.string.review_booking_amend_not_allowed);
    }

    @NotNull
    public String policyCancelNotAllowed() {
        return context.getString(R.string.review_booking_cancel_not_allowed);
    }

    @NotNull
    public String policyCancelAllowedXDaysBeforeArrival(int days) {
        return context.getString(R.string.review_booking_cancel_allow_x_days_arrival, days);
    }

    @NotNull
    public String policyAmendAllowedXDaysBeforeArrival(int days) {
        return context.getString(R.string.review_booking_amend_allow_x_days_arrival, days);
    }

    @NotNull
    public String policyCancelOnlyAllowWithInXHoursOfBooking(int hours) {
        return context.getString(R.string.review_booking_cancel_only_allow_x_hours_booking, hours);
    }

    @NotNull
    public String policyAmendOnlyAllowWithInXOfBooking(int hours) {
        return context.getString(R.string.review_booking_amend_only_allow_x_hours_booking, hours);
    }

    @NotNull
    public String getBookingError(String errorCode, GetStringResource getStringResource) {
        switch (errorCode) {
            case ErrorCodesKt.BOOKING_PAYMENT_DECLINED:
                return getStringResource.invoke(ContentManagedResourceRepository.Key.BOOKING_PAYMENT_DECLINED);
            case ErrorCodesKt.BOOKING_PAYMENT_NOT_FOUND:
                return getStringResource.invoke(ContentManagedResourceRepository.Key.BOOKING_PAYMENT_NOT_FOUND);
            case ErrorCodesKt.BOOKING_PAYMENT_GENERAL:
                return getStringResource.invoke(ContentManagedResourceRepository.Key.GENERAL_PAYMENT_ERROR);
            case ErrorCodesKt.BOOKING_PAYMENT_AMOUNT_CONFLICT:
                return getStringResource.invoke(ContentManagedResourceRepository.Key.PAYMENT_AMOUNT_CONFLICT);
            case ErrorCodesKt.BOOKING_PAYMENT_TRANSACTION_REFUND:
                return getStringResource.invoke(ContentManagedResourceRepository.Key.TRANSACTIONAL_REFUND_ERROR);
            case ErrorCodesKt.BOOKING_PAYMENT_FRAUD_CHECK:
                return getStringResource.invoke(ContentManagedResourceRepository.Key.PAYMENT_FRAUD_CHECK);
            default:
                return context.getString(R.string.generic_error_message_with_try_again_later);
        }
    }

    @NotNull
    public String getBookingTracking(String errorCode) {
        switch (errorCode) {
            case ErrorCodesKt.BOOKING_PAYMENT_DECLINED:
                return context.getString(R.string.booking_payment_declined);
            case ErrorCodesKt.BOOKING_PAYMENT_NOT_FOUND:
                return context.getString(R.string.booking_payment_not_found);
            case ErrorCodesKt.BOOKING_PAYMENT_GENERAL:
                return context.getString(R.string.booking_payment_Unknown);
            case ErrorCodesKt.BOOKING_BART_SESSION_TIME_OUT:
            return context.getString(R.string.booking_payment_session_time_out);
            case ErrorCodesKt.BOOKING_PAYMENT_AMOUNT_CONFLICT:
                return context.getString(R.string.booking_payment_amount_booking_amount_not_equal);
            case ErrorCodesKt.BOOKING_PAYMENT_TRANSACTION_REFUND:
                return context.getString(R.string.booking_transaction_refund_failed);
            case ErrorCodesKt.BOOKING_PAYMENT_FRAUD_CHECK:
                return context.getString(R.string.booking_transaction_fraud_check_failed);
            default:
                return StringUtils.EMPTY_STRING;
        }
    }

    @NonNull
    public String getPaymentError(int errorCode) {
        switch (errorCode) {
            case ErrorCodesKt.PAYMENT_UNKOWN_ERROR:
            case ErrorCodesKt.PAYMENT_VALIDATION_ERROR:
            case ErrorCodesKt.PAYMENT_MS_PROVIDER_ACCOUNT_NOT_FOUND:
            case ErrorCodesKt.PAYMENT_TEMPLATE_ERROR:
            case ErrorCodesKt.PAYMENT_UNABLE_TO_PARSE_PROVIDER_RESPONSE:
            case ErrorCodesKt.PAYMENT_ERROR_HANDLING_REQUEST:
            case ErrorCodesKt.PAYMENT_UNAUTHORISED:
            case ErrorCodesKt.PAYMENT_PAYMENT_NOT_FOUND:
                 return context.getString(R.string.payment_generic_error);
            case ErrorCodesKt.PAYMENT_PROVIDER_ERROR:
                return context.getString(R.string.payment_provider_error);
            case ErrorCodesKt.PAYMENT_UNABLE_TO_REFUND:
                return context.getString(R.string.payment_refund_error);
            case ErrorCodesKt.PAYMENT_WEB_2_PAY_TIME_OUT:
                return context.getString(R.string.payment_web_2_pay_timeout_error);
            default:
                return StringUtils.EMPTY_STRING;
        }
    }

    @NonNull
    public String getPaymentTracking(int errorCode) {
        switch (errorCode) {
            case ErrorCodesKt.PAYMENT_UNKOWN_ERROR:
                return AnalyticsConstants.ScreenState.PAYMENT_UNKNOWN;
            case ErrorCodesKt.PAYMENT_VALIDATION_ERROR:
                return AnalyticsConstants.ScreenState.PAYMENT_VALIDATION;
            case ErrorCodesKt.PAYMENT_PROVIDER_ERROR:
                return AnalyticsConstants.ScreenState.PAYMENT_PROVIDER_ISSUE;
            case ErrorCodesKt.PAYMENT_TEMPLATE_ERROR:
                return AnalyticsConstants.ScreenState.PAYMENT_IPAGE_TEMPLATE;
            case ErrorCodesKt.PAYMENT_UNABLE_TO_PARSE_PROVIDER_RESPONSE:
                return AnalyticsConstants.ScreenState.PAYMENT_PARSING;
            case ErrorCodesKt.PAYMENT_UNAUTHORISED:
                return AnalyticsConstants.ScreenState.PAYMENT_AUTHORISED;
            case ErrorCodesKt.PAYMENT_PAYMENT_NOT_FOUND:
                return AnalyticsConstants.ScreenState.PAYMENT_NOT_FOUND;
            case ErrorCodesKt.PAYMENT_UNABLE_TO_REFUND:
                return AnalyticsConstants.ScreenState.PAYMENT_UNABLE_TO_REFUND;
            case ErrorCodesKt.PAYMENT_WEB_2_PAY_TIME_OUT:
                return AnalyticsConstants.ScreenState.PAYMENT_PROVIDER_SESSION_TIME_OUT;
            default:
                return StringUtils.EMPTY_STRING;
        }
    }

}
