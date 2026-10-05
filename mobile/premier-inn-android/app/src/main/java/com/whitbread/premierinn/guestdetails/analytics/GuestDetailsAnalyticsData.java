package com.whitbread.premierinn.guestdetails.analytics;

import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.KEY_LETTING_TYPE;
import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.KEY_RATE_CODE;
import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.Type.BOOKING_FLOW;
import static com.whitbread.premierinn.common.utils.StringUtils.toColonSeparatedString;
import static com.whitbread.premierinn.common.utils.TrackingAnalyticsUtils.tripTypeLabel;

import androidx.annotation.Nullable;

import com.contentsquare.android.api.model.CustomVar;
import com.google.auto.value.AutoValue;
import com.whitbread.premierinn.common.RoomBooking;
import com.whitbread.premierinn.common.analytics.AnalyticsConstants;
import com.whitbread.premierinn.common.analytics.AnalyticsData;
import com.whitbread.premierinn.common.utils.TrackingAnalyticsUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AutoValue
public abstract class GuestDetailsAnalyticsData implements AnalyticsData {

    public static final String KEY_RATE_DESCRIPTION = "analyticsData.bf.rateDescription";

    public static final String KEY_RATE_NAME = "analyticsData.bf.rateName";

    static final String KEY_CUSTOMER_TYPE = "analyticsData.bf.customerType";

    public abstract String hotelCode();

    public abstract String rateCode();

    public abstract String rateDescription();

    public abstract String rateName();

    public abstract Boolean marketingOptIn();

    public abstract List<RoomBooking> selectedRooms();

    public abstract boolean businessUser();

    @Nullable
    public abstract String promoCode();

    @Nullable
    public abstract String promoName();

    @Nullable
    public abstract String rateTag();

    @AutoValue.Builder
    public abstract static class Builder {

        public abstract Builder hotelCode(String hotelCode);

        public abstract Builder rateCode(String rateCode);

        public abstract Builder rateDescription(String rateDescription);

        public abstract Builder rateName(String rateName);

        public abstract Builder marketingOptIn(Boolean marketingOptIn);

        public abstract Builder selectedRooms(List<RoomBooking> rooms);

        public abstract Builder businessUser(boolean businessUser);

        public abstract GuestDetailsAnalyticsData build();

        public abstract Builder promoCode(String pushToken);

        public abstract Builder promoName(String pushToken);

        public abstract Builder rateTag(String pushToken);
    }

    public static Builder builder() {
        return new AutoValue_GuestDetailsAnalyticsData.Builder();
    }

    @Override
    public Map<String, String> contextData() {
        Map<String, String> contextData = new HashMap<>();

        contextData.put(AnalyticsConstants.Key.SCREEN_TYPE, BOOKING_FLOW);
        contextData.put(AnalyticsConstants.Key.PRODUCTS, ";" + hotelCode());
        contextData.put(KEY_RATE_CODE, TrackingAnalyticsUtils.formatRateCode(rateCode()));
        contextData.put(KEY_RATE_DESCRIPTION, rateDescription());
        contextData.put(KEY_RATE_NAME, rateName());
        contextData.put(AnalyticsConstants.Key.MARKETING_OPTIN, marketingOptIn().toString());
        contextData.put(AnalyticsConstants.Key.EVENTS, "scAdd");

        if (promoCode() != null) {
            contextData.put(AnalyticsConstants.Key.PROMO_CODE, promoCode());
            contextData.put(AnalyticsConstants.Key.PROMO_NAME, promoName());
            contextData.put(AnalyticsConstants.Key.ALL_RATE_TAGS, rateTag());
            contextData.put(AnalyticsConstants.Key.ALL_DISCOUNT_CODE_APPLIED, "true");
        }

        return contextData;
    }

    @Override
    public List<CustomVar> customCSQVars() {
        return Arrays.asList(
                new CustomVar(7, KEY_RATE_CODE, TrackingAnalyticsUtils.formatRateCode(rateCode())),
                new CustomVar(19, KEY_LETTING_TYPE, toColonSeparatedString(bookedLettingTypes())),
                new CustomVar(14, KEY_CUSTOMER_TYPE, tripTypeLabel(businessUser()))
        );
    }
    private List<String> bookedLettingTypes() {
        List<String> roomLettingTypes = new ArrayList<>();
        for (RoomBooking bookingRoom : selectedRooms()) {
            roomLettingTypes.add(bookingRoom.getLettingCode());
        }
        return roomLettingTypes;
    }
}