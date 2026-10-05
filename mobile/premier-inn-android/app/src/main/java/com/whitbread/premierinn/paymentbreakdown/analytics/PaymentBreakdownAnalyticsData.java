package com.whitbread.premierinn.paymentbreakdown.analytics;

import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.KEY_RATE_CODE;
import static com.whitbread.premierinn.common.utils.TrackingAnalyticsUtils.format;

import com.google.auto.value.AutoValue;
import com.whitbread.premierinn.common.analytics.AnalyticsConstants;
import com.whitbread.premierinn.common.analytics.AnalyticsData;
import com.whitbread.premierinn.common.utils.TrackingAnalyticsUtils;

import java.util.HashMap;
import java.util.Map;

@AutoValue
public abstract class PaymentBreakdownAnalyticsData implements AnalyticsData {

    public static final String KEY_RATE_DESCRIPTION = "analyticsData.bf.rateDescription";
    public static final String KEY_RATE_NAME = "analyticsData.bf.rateName";

    public abstract String rateCode();

    public abstract String rateDescription();

    public abstract String rateName();

    public abstract String screenType();

    @Override
    public Map<String, String> contextData() {
        Map<String, String> contextData = new HashMap<>();
        contextData.put(KEY_RATE_CODE, TrackingAnalyticsUtils.formatRateCode(rateCode()));
        contextData.put(KEY_RATE_DESCRIPTION, rateDescription());
        contextData.put(KEY_RATE_NAME, rateName());
        contextData.put(AnalyticsConstants.Key.SCREEN_TYPE, format(screenType()));
        contextData.put(AnalyticsConstants.Key.EVENTS, "scCheckout");
        return contextData;
    }

    public static Builder builder() {
        return new AutoValue_PaymentBreakdownAnalyticsData.Builder();
    }


    @AutoValue.Builder
    public abstract static class Builder {

        public abstract Builder rateCode(String rateCode);

        public abstract Builder rateDescription(String rateDescription);

        public abstract Builder rateName(String rateName);


        public abstract Builder screenType(String screenType);

        public abstract PaymentBreakdownAnalyticsData build();
    }
}
