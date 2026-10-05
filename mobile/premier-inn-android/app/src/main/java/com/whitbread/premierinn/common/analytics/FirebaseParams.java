package com.whitbread.premierinn.common.analytics;

import static com.whitbread.premierinn.common.format.DateFormat.DASHED_YEAR_MONTH_DAY;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.whitbread.premierinn.common.format.FormatExtensionsKt;
import com.whitbread.premierinn.common.utils.StringUtils;

import org.threeten.bp.LocalDate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseParams {

    private final Map<String, Object> pairs = new HashMap<>();

    public void putString(@NonNull ParamName key, @Nullable String value) {
        pairs.put(key.getParamName(), value);
    }

    public void putString(@NonNull String key, @Nullable String value) {
        pairs.put(key, value);
    }

    public void putDouble(@NonNull ParamName key, @Nullable Double value) {
        pairs.put(key.getParamName(), value);
    }

    public void putInteger(@NonNull ParamName key, @Nullable Integer value) {
        pairs.put(key.getParamName(), value);
    }

    public void putFormattedDate(@NonNull ParamName key, @NonNull LocalDate date) {
        pairs.put(key.getParamName(), FormatExtensionsKt.format(date, DASHED_YEAR_MONTH_DAY));
    }

    public void putCommaSeparatedList(@NonNull ParamName key, @NonNull List<String> values) {
        pairs.put(key.getParamName(), StringUtils.toCommaSeparatedString(values));
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        for (Map.Entry<String, Object> entry : pairs.entrySet()) {
            if (entry.getValue() instanceof String) {
                bundle.putString(entry.getKey(), (String) entry.getValue());
            } else if (entry.getValue() instanceof Integer) {
                bundle.putInt(entry.getKey(), (Integer) entry.getValue());
            } else if (entry.getValue() instanceof Double) {
                bundle.putDouble(entry.getKey(), (Double) entry.getValue());
            }
        }
        return bundle;
    }

    public enum ParamName {
        NUMBER_OF_BOOKINGS(FirebaseAnalytics.Param.QUANTITY),
        CURRENCY(FirebaseAnalytics.Param.CURRENCY),
        PRICE_AMOUNT(FirebaseAnalytics.Param.VALUE),
        HOTEL_CODE(FirebaseAnalytics.Param.LOCATION),
        ARRIVAL_DATE(FirebaseAnalytics.Param.START_DATE),
        DEPARTURE_DATE(FirebaseAnalytics.Param.END_DATE),
        NUMBER_OF_NIGHTS(FirebaseAnalytics.Param.NUMBER_OF_NIGHTS),
        NUMBER_OF_ROOMS(FirebaseAnalytics.Param.NUMBER_OF_ROOMS),
        NUMBER_OF_GUESTS(FirebaseAnalytics.Param.NUMBER_OF_PASSENGERS),
        SEARCH_TERM(FirebaseAnalytics.Param.SEARCH_TERM),
        RATE_TYPE(FirebaseAnalytics.Param.TRAVEL_CLASS),
        TRIP_TYPE("user_type"),
        PAYMENT_TYPE("payment_type"),
        RATING_TYPE("rating_type"),
        SRP_SUCCESS_STATUS("success");

        private final String paramName;

        ParamName(@NonNull String paramName) {
            this.paramName = paramName;
        }

        public String getParamName() {
            return paramName;
        }
    }

    // Overrode equals and toString to help with testing
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        FirebaseParams that = (FirebaseParams) o;

        return pairs.equals(that.pairs);
    }

    @Override
    public int hashCode() {
        return pairs.hashCode();
    }

    @Override
    public String toString() {
        return "FirebaseParams{pairs=" + pairs + '}';
    }
}