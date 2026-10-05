package com.whitbread.premierinn.guestdetails.analytics;

import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.KEY_LETTING_TYPE;
import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.KEY_RATE_CODE;
import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.Type.BOOKING_FLOW;
import static com.whitbread.premierinn.guestdetails.analytics.GuestDetailsAnalyticsData.KEY_CUSTOMER_TYPE;
import static com.whitbread.premierinn.guestdetails.analytics.GuestDetailsAnalyticsData.KEY_RATE_NAME;
import static com.whitbread.premierinn.summary.analytics.SummaryAnalyticsData.KEY_RATE_DESCRIPTION;
import static junit.framework.Assert.assertEquals;

import com.contentsquare.android.api.model.CustomVar;
import com.whitbread.premierinn.api.response.InstanceFactory;
import com.whitbread.premierinn.api.response.booking.BookingAvailability;
import com.whitbread.premierinn.api.response.booking.BookingRatePlan;
import com.whitbread.premierinn.common.RoomBooking;
import com.whitbread.premierinn.common.analytics.AnalyticsConstants;
import com.whitbread.premierinn.common.utils.TrackingAnalyticsUtils;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GuestDetailsAnalyticsDataTest {

    private BookingAvailability bookingAvailability;
    private BookingRatePlan chosenRate;
    private String formattedRateCode;
    private String rateName;
    private String rateDescription;
    private final List<RoomBooking> selectedRooms = new ArrayList<>();



    @Before
    public void setUp() throws Exception {
        bookingAvailability = InstanceFactory.create(BookingAvailability.class, "apiTest/booking-availabilities-request_both_rates.json");
        chosenRate = bookingAvailability.ratePlans().get(0);
        formattedRateCode = chosenRate.getRateCode().replaceAll("[0-9]", "");
        rateName = "Flex";
        rateDescription = formattedRateCode + "-" + "LETTINGTYPE" + "-" + "10.00";
    }

    @Test
    public void validContextData() {
        GuestDetailsAnalyticsData data = GuestDetailsAnalyticsData.builder()
                .hotelCode(bookingAvailability.hotelCode())
                .rateCode(chosenRate.getRateCode())
                .rateDescription(rateDescription)
                .rateName(rateName)
                .marketingOptIn(true)
                .selectedRooms(selectedRooms)
                .businessUser(false)
                .promoCode("promo_code")
                .promoName("promo_name")
                .rateTag("rate_tag")
                .build();

        assertEquals(expectedContextData(), data.contextData());
    }

    @Test
    public void testCustomCSQVarsData() {
        GuestDetailsAnalyticsData data = GuestDetailsAnalyticsData.builder()
                .hotelCode(bookingAvailability.hotelCode())
                .rateCode(chosenRate.getRateCode())
                .rateDescription(rateDescription)
                .rateName(rateName)
                .marketingOptIn(true)
                .selectedRooms(selectedRooms)
                .businessUser(false)
                .build();

        assertEquals(expectedCustomCSQVarsData(), data.customCSQVars());
    }

    private List<CustomVar> expectedCustomCSQVarsData() {
        return Arrays.asList(
                new CustomVar(7, KEY_RATE_CODE, "RT"),
                new CustomVar(19, KEY_LETTING_TYPE, ""),
                new CustomVar(14, KEY_CUSTOMER_TYPE, "Leisure")
        );
    }

    private Map<String, String> expectedContextData() {
        Map<String, String> contextData = new HashMap<>();

        contextData.put(AnalyticsConstants.Key.SCREEN_TYPE, BOOKING_FLOW);
        contextData.put(AnalyticsConstants.Key.PRODUCTS, ";" + bookingAvailability.hotelCode());
        contextData.put(KEY_RATE_CODE, TrackingAnalyticsUtils.formatRateCode(chosenRate.getRateCode()));
        contextData.put(KEY_RATE_DESCRIPTION, rateDescription);
        contextData.put(KEY_RATE_NAME, rateName);
        contextData.put(AnalyticsConstants.Key.MARKETING_OPTIN, "true");
        contextData.put(AnalyticsConstants.Key.EVENTS, "scAdd");
        contextData.put(AnalyticsConstants.Key.PROMO_CODE, "promo_code");
        contextData.put(AnalyticsConstants.Key.PROMO_NAME, "promo_name");
        contextData.put(AnalyticsConstants.Key.ALL_RATE_TAGS, "rate_tag");
        contextData.put(AnalyticsConstants.Key.ALL_DISCOUNT_CODE_APPLIED, "true");

        return contextData;
    }
}