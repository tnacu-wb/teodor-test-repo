package com.whitbread.premierinn.summarybreakdown.analytics;

import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.KEY_RATE_CODE;
import static com.whitbread.premierinn.common.format.DateFormat.SLASHED_DAY_MONTH_YEAR;
import static com.whitbread.premierinn.common.utils.TrackingAnalyticsUtils.format;
import static com.whitbread.premierinn.summary.analytics.SummaryAnalyticsData.KEY_ADULTS;
import static com.whitbread.premierinn.summary.analytics.SummaryAnalyticsData.KEY_CHECK_IN_DATE;
import static com.whitbread.premierinn.summary.analytics.SummaryAnalyticsData.KEY_CHECK_IN_DAY;
import static com.whitbread.premierinn.summary.analytics.SummaryAnalyticsData.KEY_CHECK_IN_OUT_DAY;
import static com.whitbread.premierinn.summary.analytics.SummaryAnalyticsData.KEY_CHECK_OUT_DATE;
import static com.whitbread.premierinn.summary.analytics.SummaryAnalyticsData.KEY_CHECK_OUT_DAY;
import static com.whitbread.premierinn.summary.analytics.SummaryAnalyticsData.KEY_CHILDREN;
import static com.whitbread.premierinn.summary.analytics.SummaryAnalyticsData.KEY_NIGHTS;
import static com.whitbread.premierinn.summary.analytics.SummaryAnalyticsData.KEY_RATE_DESCRIPTION;
import static com.whitbread.premierinn.summary.analytics.SummaryAnalyticsData.KEY_RATE_NAME;
import static com.whitbread.premierinn.summary.analytics.SummaryAnalyticsData.KEY_ROOMS;

import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.whitbread.premierinn.api.response.availability.UpsellItem;
import com.whitbread.premierinn.common.analytics.AnalyticsConstants;
import com.whitbread.premierinn.common.analytics.AnalyticsData;
import com.whitbread.premierinn.common.analytics.ProductValues;
import com.whitbread.premierinn.common.format.FormatExtensionsKt;
import com.whitbread.premierinn.common.utils.AppExtensions;
import com.whitbread.premierinn.common.utils.DateUtils;
import com.whitbread.premierinn.common.utils.TrackingAnalyticsUtils;
import com.whitbread.premierinn.summary.models.ParcelableExtrasItem;

import org.threeten.bp.LocalDate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kotlin.Pair;

@AutoValue
public abstract class SummaryBreakdownAnalyticsData implements AnalyticsData {

    static final String EXTRAS_SELECTED_DESC = "analyticsData.bf.extrasSelectedDescription";
    //Ex. Meal Deal , Premier Inn Breakfast, Early check-in

    public abstract String hotelCode();

    @Nullable
    public abstract String userId();

    public abstract String rateCode();

    public abstract String rateDescription();

    public abstract String rateName();

    @Nullable
    public abstract Pair<List<UpsellItem>, List<ParcelableExtrasItem>>  selectedExtras();

    public abstract LocalDate arrivalDate();

    public abstract LocalDate departureDate();

    public abstract int nights();

    public abstract int rooms();

    public abstract int adults();

    public abstract int children();

    public abstract String screenType();

    @Override
    public Map<String, String> contextData() {
        String productString = ProductValues.builder()
                .category("")
                .product(hotelCode())
                .quantity(ProductValues.UNAVAILABLE)
                .totalPrice(ProductValues.UNAVAILABLE).build().toString();

        Map<String, String> contextData = new HashMap<>();
        contextData.put(KEY_RATE_CODE, TrackingAnalyticsUtils.formatRateCode(rateCode()));
        contextData.put(KEY_RATE_DESCRIPTION, rateDescription());
        contextData.put(KEY_RATE_NAME, rateName());
        contextData.put(KEY_CHECK_IN_DATE, FormatExtensionsKt.format(arrivalDate(), SLASHED_DAY_MONTH_YEAR));
        contextData.put(KEY_CHECK_OUT_DATE, FormatExtensionsKt.format(departureDate(), SLASHED_DAY_MONTH_YEAR));
        if (selectedExtras() != null) {
            contextData.put(EXTRAS_SELECTED_DESC, AppExtensions.toAnalyticsDataString(selectedExtras()));
        }
        contextData.put(KEY_NIGHTS, Integer.toString(nights()));
        contextData.put(KEY_ROOMS, Integer.toString(rooms()));
        contextData.put(KEY_ADULTS, Integer.toString(adults()));
        contextData.put(KEY_CHILDREN, Integer.toString(children()));
        contextData.put(KEY_CHECK_IN_DAY, DateUtils.getWeekDay(arrivalDate()));
        contextData.put(KEY_CHECK_OUT_DAY, DateUtils.getWeekDay(departureDate()));
        contextData.put(KEY_CHECK_IN_OUT_DAY, DateUtils.getWeekDay(arrivalDate()) + "-" + DateUtils.getWeekDay(departureDate()));
        contextData.put(AnalyticsConstants.Key.SCREEN_TYPE, format(screenType()));
        contextData.put(AnalyticsConstants.Key.PRODUCTS, productString);
        return contextData;
    }

    public static Builder builder() {
        return new AutoValue_SummaryBreakdownAnalyticsData.Builder();
    }


    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder hotelCode(String hotelCode);

        public abstract Builder userId(String userId);

        public abstract Builder rateCode(String rateCode);

        public abstract Builder rateDescription(String rateDescription);

        public abstract Builder rateName(String rateName);

        public abstract Builder selectedExtras(Pair<List<UpsellItem>, List<ParcelableExtrasItem>> addedExtras);

        public abstract Builder arrivalDate(LocalDate arrivalDate);

        public abstract Builder departureDate(LocalDate departureDate);

        public abstract Builder nights(int nights);

        public abstract Builder rooms(int rooms);

        public abstract Builder adults(int adults);

        public abstract Builder children(int children);

        public abstract Builder screenType(String screenType);

        public abstract SummaryBreakdownAnalyticsData build();
    }
}
