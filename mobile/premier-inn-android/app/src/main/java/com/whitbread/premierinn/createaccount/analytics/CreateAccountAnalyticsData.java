package com.whitbread.premierinn.createaccount.analytics;

import com.google.auto.value.AutoValue;
import com.whitbread.premierinn.common.analytics.AnalyticsData;

import java.util.HashMap;
import java.util.Map;

import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.CAR_DETAILS;

@AutoValue
public abstract class CreateAccountAnalyticsData implements AnalyticsData {

    public abstract boolean carDetails();

    public static CreateAccountAnalyticsData.Builder builder() {
        return new AutoValue_CreateAccountAnalyticsData.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract CreateAccountAnalyticsData.Builder carDetails(boolean carDetails);
        public abstract CreateAccountAnalyticsData build();
    }

    @Override
    public Map<String, String> contextData() {
        Map<String, String> contextData = new HashMap<>();
        contextData.put(CAR_DETAILS, String.valueOf(carDetails()));

        return contextData;
    }
}
