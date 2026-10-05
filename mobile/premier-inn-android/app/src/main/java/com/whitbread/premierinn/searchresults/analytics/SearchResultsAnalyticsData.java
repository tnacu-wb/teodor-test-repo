package com.whitbread.premierinn.searchresults.analytics;


import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key;
import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.CIOL_ADOBE_CAMPAIGN_KEY;
import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.GOOGLE_ID;
import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.PUSH_TOKEN;
import static com.whitbread.premierinn.common.analytics.ProductValues.EMPTY;
import static com.whitbread.premierinn.common.format.DateFormat.SLASHED_DAY_MONTH_YEAR;
import static com.whitbread.premierinn.common.utils.DateUtils.getLeadDays;
import static com.whitbread.premierinn.common.utils.DateUtils.getWeekDay;
import static com.whitbread.premierinn.common.utils.TrackingAnalyticsUtils.buildEvar;
import static com.whitbread.premierinn.common.utils.TrackingAnalyticsUtils.format;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.contentsquare.android.api.model.CustomVar;
import com.google.auto.value.AutoValue;
import com.whitbread.premierinn.common.analytics.AnalyticsData;
import com.whitbread.premierinn.common.analytics.CampaignDataModel;
import com.whitbread.premierinn.common.analytics.ProductValues;
import com.whitbread.premierinn.common.format.FormatExtensionsKt;
import com.whitbread.premierinn.common.utils.StringUtils;
import com.whitbread.premierinn.common.utils.TrackingAnalyticsUtils;
import com.whitbread.premierinn.domain.common.HotelAvailability;
import com.whitbread.premierinn.domain.common.PriceDomain;
import com.whitbread.premierinn.domain.common.RatePlanSRP;
import com.whitbread.premierinn.domain.common.RoomSRP;
import com.whitbread.premierinn.domain.graphql.srp.entity.LowestRoomRateDomain;
import com.whitbread.premierinn.domain.graphql.srp.entity.SingleHotelAvailabilityDomain;

import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class encapsulates all the analytics data that needs to be sent to Adobe for Search Results List Screen
 * https://whitbreadis.atlassian.net/wiki/spaces/~Tom.Pinney/pages/113058052/Android+Search+Results
 */
@AutoValue
public abstract class SearchResultsAnalyticsData implements AnalyticsData {

    // Context Data keys
    public static final String KEY_SEARCH_LOCATION = "analyticsData.search.searchLocation";
    public static final String KEY_NIGHTS = "analyticsData.search.nights";
    public static final String KEY_ROOMS = "analyticsData.search.rooms";
    // 'location' or 'hotel specific' or 'near me' or 'deeplink'
    public static final String KEY_SEARCH_TYPE = "analyticsData.search.searchType";
    public static final String KEY_CHECK_IN = "analyticsData.search.check-in";
    public static final String KEY_CHECK_OUT = "analyticsData.search.check-out";
    public static final String KEY_ADULTS = "analyticsData.search.adults";
    public static final String KEY_CHILDREN = "analyticsData.search.children";
    public static final String KEY_GUESTS = "analyticsData.search.guests";
    public static final String KEY_LEAD_DAYS = "analyticsData.search.leadDays";
    public static final String KEY_ROOM_TYPE = "analyticsData.search.roomType"; //when multiples concatenated as "Double:Double:Family"
    public static final String KEY_NUM_RESULTS = "analyticsData.search.numResults";
    public static final String KEY_START_END_DAY = "analyticsData.search.startEndDay";
    public static final String KEY_START_DAY = "analyticsData.search.startDay";
    public static final String KEY_END_DAY = "analyticsData.search.endDay";
    public static final String KEY_EVENT = "analyticsData.search.event.event1";

    // Merchandising Evars Keys
    public static final String EVAR_DISTANCE_MILES = "eVar50=%s";
    public static final String EVAR_RESULT_POSITION = "eVar55=%s";
    public static final String EVAR_HOTEL_LABEL = "eVar51=%s";

    // Merchandising Event Keys
    public static final String EVENT_HOTEL_NOT_AVAILABLE = "event84";
    public static final String EVENT_HOTEL_AVAILABLE = "event90";

    public static final String BETWEEN_PRODUCT_DELIMITER = ",";

    //Constant Values
    public static final String SEARCH_TYPE_MAP = "Map";
    public static final String SEARCH_TYPE_NEAR_ME = "near me";
    public static final String SEARCH_TYPE_HOTEL_SPECIFIC = "hotel specific";
    public static final String SEARCH_TYPE_LOCATION = "location";
    public static final String RATE_NOT_AVAILABLE_VALUE = "Rate not available";
    public static final String FULLY_BOOKED_VALUE = "Fully booked";

    private static final String EVAR_RATES = "eVar62=%s";

    @Nullable
    public abstract List<HotelAvailability> searchResults();

    @Nullable
    public abstract List<SingleHotelAvailabilityDomain> searchResultsOpera();
    public abstract String screenType();

    public abstract String placeName();

    public abstract int nights();

    public abstract int numRooms();

    public abstract LocalDate arrivalDate();

    public abstract LocalDate departureDate();

    public abstract int numAdults();

    public abstract int numChildren();

    public abstract int totalNumOfGuests();

    public abstract List<String> roomTypes();

    public abstract boolean inMapView();

    @Nullable
    public abstract String searchedHotelCode();

    @Nullable
    public abstract String pushToken();

    @Nullable
    public abstract CampaignDataModel campaignModel();

    @Nullable
    public abstract String trackingCode();

    public static Builder builder() {
        return new AutoValue_SearchResultsAnalyticsData.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder screenType(String state);

        public abstract Builder searchResults(@NonNull List<HotelAvailability> searchResultsAnalyticsBody);

        public abstract Builder searchResultsOpera(List<SingleHotelAvailabilityDomain> searchResultsOperaAnalyticsBody);

        public abstract Builder placeName(@NonNull String placeName);

        public abstract Builder nights(int nights);

        public abstract Builder numRooms(int numRooms);

        public abstract Builder arrivalDate(@NonNull LocalDate arrivalDate);

        public abstract Builder departureDate(@NonNull LocalDate departureDate);

        public abstract Builder numAdults(int numAdults);

        public abstract Builder numChildren(int numChildren);

        public abstract Builder totalNumOfGuests(int totalNumOfGuests);

        public abstract Builder roomTypes(@NonNull List<String> roomTypes);

        public abstract Builder inMapView(boolean b);

        public abstract Builder pushToken(@NonNull String pushToken);

        @Nullable
        public abstract Builder searchedHotelCode(@NonNull String searchedHotelCode);

        public abstract Builder campaignModel(CampaignDataModel campaignModel);

        public abstract Builder trackingCode(String trackingCode);

        public abstract SearchResultsAnalyticsData build();

    }

    @Override
    public Map<String, String> contextData() {
        Map<String, String> contextData = new HashMap<>();
        contextData.put(Key.SCREEN_TYPE, format(screenType()));
        contextData.put(Key.PRODUCTS, buildProductString());
        // Speak to Tom, iOS is not sending this, u can only get it when u get the customer object
//         contextData.put(KEY_USER_ID, "");
        contextData.put(KEY_SEARCH_LOCATION, placeName());
        contextData.put(KEY_NIGHTS, String.valueOf(nights()));
        contextData.put(KEY_ROOMS, String.valueOf(numRooms()));
        contextData.put(KEY_SEARCH_TYPE, searchType());
        contextData.put(KEY_CHECK_IN, FormatExtensionsKt.format(arrivalDate(), SLASHED_DAY_MONTH_YEAR));
        contextData.put(KEY_CHECK_OUT, FormatExtensionsKt.format(departureDate(), SLASHED_DAY_MONTH_YEAR));
        contextData.put(KEY_ADULTS, String.valueOf(numAdults()));
        contextData.put(KEY_CHILDREN, String.valueOf(numChildren()));
        contextData.put(KEY_GUESTS, String.valueOf(totalNumOfGuests()));
        contextData.put(KEY_LEAD_DAYS, String.valueOf(getLeadDays(arrivalDate(), LocalDate.now())));
        contextData.put(KEY_ROOM_TYPE, StringUtils.toColonSeparatedString(roomTypes()));
        if (searchResultsOpera() != null) {
            contextData.put(KEY_NUM_RESULTS, String.valueOf(searchResultsOpera().size()));
        } else if (searchResults() != null) {
            contextData.put(KEY_NUM_RESULTS, String.valueOf(searchResults().size()));
        }
        contextData.put(KEY_START_END_DAY, getWeekDay(arrivalDate()) + "-" + getWeekDay(departureDate()));
        contextData.put(KEY_START_DAY, getWeekDay(arrivalDate()));
        contextData.put(KEY_END_DAY, getWeekDay(departureDate()));
        contextData.put(KEY_EVENT, String.valueOf(1));

        contextData.put(PUSH_TOKEN, pushToken());

        if (campaignModel() != null) {
            if (!campaignModel().getCampaignId().isEmpty()) {
                contextData.put(CIOL_ADOBE_CAMPAIGN_KEY, campaignModel().getCampaignId());
            }
            if (!campaignModel().getGoogleId().isEmpty()) {
                contextData.put(GOOGLE_ID, campaignModel().getGoogleId());
            }
            if (!campaignModel().getMicrosoftId().isEmpty()) {
                contextData.put(Key.MICROSOFT_ID, campaignModel().getMicrosoftId());
            }
        }

        if (trackingCode() != null && !trackingCode().isEmpty()) {
            contextData.put(CIOL_ADOBE_CAMPAIGN_KEY, trackingCode());
        }

        return contextData;
    }

    @Override
    public List<CustomVar> customCSQVars() {
        return Arrays.asList(
                new CustomVar(5, KEY_START_END_DAY, getWeekDay(arrivalDate()) + "-" + getWeekDay(departureDate())),
                new CustomVar(3, KEY_NIGHTS, String.valueOf(nights())),
                new CustomVar(4, KEY_GUESTS, String.valueOf(totalNumOfGuests()))
        );
    }

    private String buildProductString() {
        StringBuilder productsStringBuilder = new StringBuilder();
        if (searchResults() != null) {
            for (int index = 0; index < searchResults().size(); index++) {
                if (productsStringBuilder.length() > 0) {
                    productsStringBuilder.append(BETWEEN_PRODUCT_DELIMITER);
                }
                productsStringBuilder.append(createHotelProductStringInfo(searchResults().get(index), index));
            }
        } else if (searchResultsOpera() != null) {
            for (int index = 0; index < searchResultsOpera().size(); index++) {
                if (productsStringBuilder.length() > 0) {
                    productsStringBuilder.append(BETWEEN_PRODUCT_DELIMITER);
                }
                productsStringBuilder.append(createHotelProductStringInfoOpera(searchResultsOpera().get(index), index));
            }
        }

        return productsStringBuilder.toString();
    }

    private ProductValues createHotelProductStringInfo(@NonNull HotelAvailability hotelAvailability, int resultIndex) {
        List<String> hotelMerchandisingEvars = new ArrayList<>();
        List<String> hotelMerchandisingEvents = new ArrayList<>();

        addAvailabilityEvent(hotelAvailability.isFullyBooked(), hotelMerchandisingEvents);
        if (!hotelAvailability.isFullyBooked()) {
            addRateMerchandisingValues(hotelAvailability.getCheapestPlan(), hotelMerchandisingEvars);
        }
        hotelMerchandisingEvars.add(buildEvar(EVAR_DISTANCE_MILES, String.valueOf(hotelAvailability.getDistance().getValue())));
        if (!inMapView()) {
            hotelMerchandisingEvars.add(buildEvar(EVAR_RESULT_POSITION, String.valueOf(resultIndex + 1)));
        }

        if (hotelAvailability.getHotel().getFlag() != null) {
            hotelMerchandisingEvars.add(buildEvar(EVAR_HOTEL_LABEL, hotelAvailability.getHotel().getFlag().getLabel()));
        }

        // Product string for one product
        return ProductValues.builder()
                .category("")
                .product(hotelAvailability.getHotel().getCode())
                .quantity(EMPTY)
                .totalPrice(EMPTY)
                .evars(hotelMerchandisingEvars)
                .events(hotelMerchandisingEvents)
                .build();
    }

    private ProductValues createHotelProductStringInfoOpera(@NonNull SingleHotelAvailabilityDomain hotelAvailability, int resultIndex) {
        List<String> hotelMerchandisingEvars = new ArrayList<>();
        List<String> hotelMerchandisingEvents = new ArrayList<>();

        addAvailabilityEvent(hotelAvailability.getHotelAvailability().isFullyBooked(), hotelMerchandisingEvents);
        if (!hotelAvailability.getHotelAvailability().isFullyBooked()) {
            addRateMerchandisingValuesOpera(hotelAvailability.getHotelAvailability().getLowestRoomRate(), hotelMerchandisingEvars);
        }
        hotelMerchandisingEvars.add(buildEvar(EVAR_DISTANCE_MILES, String.valueOf(hotelAvailability.getHotelAvailability().getDistance())));
        if (!inMapView()) {
            hotelMerchandisingEvars.add(buildEvar(EVAR_RESULT_POSITION, String.valueOf(resultIndex + 1)));
        }

        if (!hotelAvailability.getHotelInformation().getMessagingFlag().getText().equals("")) {
            hotelMerchandisingEvars.add(buildEvar(EVAR_HOTEL_LABEL, hotelAvailability.getHotelInformation().getMessagingFlag().getText()));
        }

        // Product string for one product
        return ProductValues.builder()
                .category("")
                .product(hotelAvailability.getHotelId())
                .quantity(EMPTY)
                .totalPrice(EMPTY)
                .evars(hotelMerchandisingEvars)
                .events(hotelMerchandisingEvents)
                .build();
    }

    private void addAvailabilityEvent(boolean fullyBooked, List<String> events) {
        events.add(fullyBooked ? EVENT_HOTEL_NOT_AVAILABLE : EVENT_HOTEL_AVAILABLE);
    }

    private void addRateMerchandisingValues(RatePlanSRP cheapestPlan, List<String> evars) {
        // Assumes no alternative rooms can be lower priced, which is the
        // logic used for displaying to the user
        if (cheapestPlan != null) {
            RoomSRP room = cheapestPlan.getRoomList().get(0);
            String merchandisingRateValue = TrackingAnalyticsUtils.formatPriceInfo(room.getCost());
            evars.add(buildEvar(EVAR_RATES, merchandisingRateValue));
        }
    }

    private void addRateMerchandisingValuesOpera(LowestRoomRateDomain lowestRoomRateDomain, List<String> evars) {
        // Assumes no alternative rooms can be lower priced, which is the
        // logic used for displaying to the user

        String merchandisingRateValue;
        if (lowestRoomRateDomain != null) {
            merchandisingRateValue = TrackingAnalyticsUtils.formatPriceInfo(
                    new PriceDomain(lowestRoomRateDomain.getNetTotal(), lowestRoomRateDomain.getCurrencyCode()));
        } else {
            merchandisingRateValue = TrackingAnalyticsUtils.formatPriceInfo(
                    PriceDomain.Companion.createDefault());
        }
        evars.add(buildEvar(EVAR_RATES, merchandisingRateValue));
    }

    String searchType() {
        if (placeName().equals("My Location")) {
            return SEARCH_TYPE_NEAR_ME;
        }
        if (!StringUtils.isBlank(searchedHotelCode())) {
            return SEARCH_TYPE_HOTEL_SPECIFIC;
        } else {
            return SEARCH_TYPE_LOCATION;
        }
    }
}