package com.whitbread.premierinn.summary.analytics;


import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key;
import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.KEY_LETTING_TYPE;
import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.KEY_RATE_CODE;
import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.PUSH_TOKEN;
import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.Type;
import static com.whitbread.premierinn.common.format.DateFormat.SLASHED_DAY_MONTH_YEAR;
import static com.whitbread.premierinn.common.format.DateFormat.WEEKDAY_DAY_MONTH;
import static com.whitbread.premierinn.common.utils.StringUtils.toColonSeparatedString;
import static com.whitbread.premierinn.common.utils.TrackingAnalyticsUtils.prepayLabel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;

import com.contentsquare.android.api.model.CustomVar;
import com.google.auto.value.AutoValue;
import com.whitbread.premierinn.api.response.availability.UpsellItem;
import com.whitbread.premierinn.common.RoomBooking;
import com.whitbread.premierinn.common.analytics.AnalyticsConstants;
import com.whitbread.premierinn.common.analytics.AnalyticsData;
import com.whitbread.premierinn.common.format.FormatExtensionsKt;
import com.whitbread.premierinn.common.utils.StringUtils;
import com.whitbread.premierinn.common.utils.TrackingAnalyticsUtils;
import com.whitbread.premierinn.domain.common.PriceDomain;
import com.whitbread.premierinn.summary.models.ParcelableExtrasItem;

import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class encapsulates all the analytics data that needs to be sent to Adobe for Add Extras (SummaryView) in Booking Flow
 * https://whitbreadis.atlassian.net/wiki/spaces/~Tom.Pinney/pages/126484544/Android+Add+Extras+-+Review
 */
@AutoValue
public abstract class SummaryAnalyticsData implements AnalyticsData {

    public static final String KEY_PREPAY = "analyticsData.bf.prepay";
    public static final String KEY_RATE_DESCRIPTION = "analyticsData.bf.rateDescription";
    public static final String KEY_RATE_NAME = "analyticsData.bf.rateName";
    public static final String KEY_EXTRAS_SHOWN_DESCRIPTION = "analyticsData.bf.extrasShownDescription";
    public static final String KEY_EXTRAS_SHOWN_CODE = "analyticsData.bf.extrasShownCode";
    public static final String KEY_CHECK_IN_DATE = "analyticsData.bf.checkInDate";
    public static final String KEY_CHECK_OUT_DATE = "analyticsData.bf.checkOutDate";
    public static final String KEY_NIGHTS = "analyticsData.bf.nights";
    public static final String KEY_ROOMS = "analyticsData.bf.rooms";
    public static final String KEY_ADULTS = "analyticsData.bf.adults";
    public static final String KEY_CHILDREN = "analyticsData.bf.children";
    public static final String KEY_CHECK_IN_DAY = "analyticsData.bf.checkInDay";
    public static final String KEY_CHECK_OUT_DAY = "analyticsData.bf.checkOutDay";
    public static final String KEY_CHECK_IN_OUT_DAY = "analyticsData.bf.checkInOutDay";

    public abstract String hotelCode();

    public abstract boolean prepaid();

    @Nullable
    public abstract String userId();

    public abstract String rateCode();
    @Nullable
    public abstract String lettingType();
    @Nullable
    public abstract List<String> lettingTypes();

    public abstract PriceDomain roomPrice();

    public abstract List<RoomBooking> selectedRooms();

    public abstract String rateDescription();

    public abstract String rateName();

    public abstract Pair<List<UpsellItem>, List<ParcelableExtrasItem>> extrasShown();

    public abstract LocalDate checkInDate();

    public abstract int nights();

    public abstract int rooms();

    public abstract int adults();

    public abstract int children();

    public abstract String pushToken();

    @Nullable
    public abstract String promoCode();

    @Nullable
    public abstract String promoName();

    @Nullable
    public abstract String rateTag();

    public static SummaryAnalyticsData.Builder builder() {
        return new com.whitbread.premierinn.summary.analytics.AutoValue_SummaryAnalyticsData.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {

        public abstract Builder hotelCode(String hotelCode);

        public abstract Builder prepaid(boolean prepaid);

        public abstract Builder userId(String userId);

        public abstract Builder rateCode(String rateCode);

        public abstract Builder lettingType(String lettingType);

        public abstract Builder lettingTypes(List<String> lettingTypes);

        public abstract Builder roomPrice(PriceDomain roomPrice);

        public abstract Builder selectedRooms(List<RoomBooking> rooms);

        public abstract Builder rateDescription(String rateDescription);

        public abstract Builder rateName(String rateName);

        public abstract Builder extrasShown(Pair<List<UpsellItem>, List<ParcelableExtrasItem>> extrasList);

        public abstract Builder checkInDate(LocalDate checkInDate);

        public abstract Builder nights(int nights);

        public abstract Builder rooms(int rooms);

        public abstract Builder adults(int adults);

        public abstract Builder children(int children);

        public abstract SummaryAnalyticsData build();

        public abstract Builder pushToken(String pushToken);

        public abstract Builder promoCode(String pushToken);

        public abstract Builder promoName(String pushToken);

        public abstract Builder rateTag(String pushToken);

    }

    @Override
    public Map<String, String> contextData() {
        final Map<String, String> contextData = new HashMap<>();

        LocalDate checkOutDate = checkInDate().plusDays(nights());

        contextData.put(KEY_PREPAY, prepayLabel(prepaid()));
        contextData.put(KEY_RATE_CODE, TrackingAnalyticsUtils.formatRateCode(rateCode()));
        contextData.put(KEY_RATE_DESCRIPTION, rateDescription());
        contextData.put(KEY_RATE_NAME, rateName());
        contextData.put(KEY_EXTRAS_SHOWN_DESCRIPTION, formattedExtrasShownLegend(extrasShown())); // Legend is passed inside this key
        contextData.put(KEY_EXTRAS_SHOWN_CODE, formattedExtrasShownCode(extrasShown()));
        contextData.put(KEY_CHECK_IN_DATE, formattedArrivalDate(checkInDate()));
        contextData.put(KEY_CHECK_OUT_DATE, formattedDepartureDate(checkOutDate));
        contextData.put(KEY_CHECK_IN_DAY, formattedCheckInDay(checkInDate()));
        contextData.put(KEY_CHECK_OUT_DAY, formattedCheckOutDay(checkOutDate));
        contextData.put(KEY_CHECK_IN_OUT_DAY, formattedCheckInOutDay(checkInDate(), checkOutDate));
        contextData.put(KEY_NIGHTS, Integer.toString(nights()));
        contextData.put(KEY_ROOMS, Integer.toString(rooms()));
        contextData.put(KEY_ADULTS, Integer.toString(adults()));
        contextData.put(KEY_CHILDREN, Integer.toString(children()));
        contextData.put(Key.SCREEN_TYPE, Type.BOOKING_FLOW);
        contextData.put(Key.PRODUCTS, ";" + hotelCode());
        contextData.put(Key.EVENTS, "scOpen");
        contextData.put(PUSH_TOKEN, pushToken());

        if (promoCode() != null) {
            contextData.put(AnalyticsConstants.Key.PROMO_CODE, promoCode());
            contextData.put(AnalyticsConstants.Key.PROMO_NAME, promoName());
            contextData.put(AnalyticsConstants.Key.ALL_RATE_TAGS, rateTag());
            contextData.put(AnalyticsConstants.Key.ALL_DISCOUNT_CODE_APPLIED, "true");
        }

        if (extrasShown().first.isEmpty()) {
            contextData.put(Key.LABEL_KEY, AnalyticsConstants.Value.RESTAURANT_CLOSED);
        }
        return contextData;
    }

    @Override
    public List<CustomVar> customCSQVars() {

        CustomVar customVar1 = new CustomVar(7, KEY_RATE_CODE, TrackingAnalyticsUtils.formatRateCode(rateCode()));
        CustomVar customVar2 = new CustomVar(19, KEY_LETTING_TYPE, toColonSeparatedString(bookedLettingTypes()));

        return Arrays.asList(customVar1, customVar2);
    }

    @Nullable
    public static String formattedCheckInDay(@NonNull LocalDate arrivalDate) {
        return formattedCheckInDate(arrivalDate).substring(0, 3);
    }

    @Nullable
    public static String formattedCheckOutDay(@NonNull LocalDate checkOutDate) {
        return formattedCheckOutDate(checkOutDate).substring(0, 3);
    }

    @Nullable
    public static String formattedArrivalDate(@NonNull LocalDate arrivalDate) {
        return FormatExtensionsKt.format(arrivalDate, SLASHED_DAY_MONTH_YEAR);
    }

    @Nullable
    public static String formattedDepartureDate(@NonNull LocalDate departureDate) {
        return FormatExtensionsKt.format(departureDate, SLASHED_DAY_MONTH_YEAR);
    }

    public static String formattedCheckInDate(@NonNull LocalDate arrivalDate) {
        return FormatExtensionsKt.format(arrivalDate, WEEKDAY_DAY_MONTH);
    }

    public static String formattedCheckOutDate(@NonNull LocalDate checkOutDate) {
        return FormatExtensionsKt.format(checkOutDate, WEEKDAY_DAY_MONTH);
    }

    public static String formattedCheckInOutDay(@NonNull LocalDate checkInDate, @NonNull LocalDate checkOutDate) {
        final String formattedCheckInDate = formattedCheckInDay(checkInDate);
        final String formattedCheckOutDate = formattedCheckOutDay(checkOutDate);

        if (formattedCheckInDate == null || formattedCheckOutDate == null) {
            return StringUtils.EMPTY_STRING;
        }
        return String.format("%s-%s", formattedCheckInDate, formattedCheckOutDate);
    }

    public static String formattedExtrasShownLegend(@NonNull Pair<List<UpsellItem>, List<ParcelableExtrasItem>> extrasPair) {
        ArrayList<String> listOfLegends = new ArrayList<>();
        if (extrasPair.first != null) {
            for (int index = 0; index < extrasPair.first.size(); index++) {
                if (extrasPair.first.get(index).legend() != null) {
                    listOfLegends.add(extrasPair.first.get(index).legend());
                }
            }
        }

        for (int index = 0; index < extrasPair.second.size(); index++) {
            listOfLegends.add(extrasPair.second.get(index).getName());
        }
        return StringUtils.toCommaSeparatedString(listOfLegends);
    }

    public static String formattedExtrasShownCode(@NonNull Pair<List<UpsellItem>, List<ParcelableExtrasItem>> extrasPair) {
        ArrayList<String> listOfExtras = new ArrayList<>();
        if (extrasPair.first != null) {
            for (int index = 0; index < extrasPair.first.size(); index++) {
                if (extrasPair.first.get(index).legend() != null) {
                    listOfExtras.add(extrasPair.first.get(index).code());
                }
            }
        }
        for (int index = 0; index < extrasPair.second.size(); index++) {
            listOfExtras.add(extrasPair.second.get(index).getId());
        }
        return StringUtils.toCommaSeparatedString(listOfExtras);
    }

    private List<String> bookedLettingTypes() {
        List<String> roomLettingTypes = new ArrayList<>();
        for (RoomBooking bookingRoom : selectedRooms()) {
            roomLettingTypes.add(bookingRoom.getLettingCode());
        }
        return roomLettingTypes;
    }
}
