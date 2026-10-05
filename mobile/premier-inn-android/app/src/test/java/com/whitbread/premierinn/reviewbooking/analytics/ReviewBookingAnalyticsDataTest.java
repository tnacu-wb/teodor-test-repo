package com.whitbread.premierinn.reviewbooking.analytics;

import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.KEY_LETTING_TYPE;
import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.KEY_RATE_CODE;
import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.SCREEN_TYPE;
import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.Type.BOOKING_FLOW;
import static com.whitbread.premierinn.common.utils.TrackingAnalyticsUtils.format;
import static com.whitbread.premierinn.reviewbooking.analytics.ReviewBookingAnalyticsData.KEY_ADDED_EXTRAS_DESCRIPTION;
import static com.whitbread.premierinn.reviewbooking.analytics.ReviewBookingAnalyticsData.KEY_CUSTOMER_TYPE;
import static com.whitbread.premierinn.reviewbooking.analytics.ReviewBookingAnalyticsData.KEY_RATE_NAME;
import static com.whitbread.premierinn.reviewbooking.analytics.ReviewBookingAnalyticsData.formattedArrivalDate;
import static com.whitbread.premierinn.reviewbooking.analytics.ReviewBookingAnalyticsData.formattedCheckInDay;
import static com.whitbread.premierinn.reviewbooking.analytics.ReviewBookingAnalyticsData.formattedCheckInOutDay;
import static com.whitbread.premierinn.reviewbooking.analytics.ReviewBookingAnalyticsData.formattedCheckOutDay;
import static com.whitbread.premierinn.reviewbooking.analytics.ReviewBookingAnalyticsData.formattedDepartureDate;
import static com.whitbread.premierinn.summary.analytics.SummaryAnalyticsData.KEY_ADULTS;
import static com.whitbread.premierinn.summary.analytics.SummaryAnalyticsData.KEY_CHECK_IN_DATE;
import static com.whitbread.premierinn.summary.analytics.SummaryAnalyticsData.KEY_CHECK_IN_DAY;
import static com.whitbread.premierinn.summary.analytics.SummaryAnalyticsData.KEY_CHECK_IN_OUT_DAY;
import static com.whitbread.premierinn.summary.analytics.SummaryAnalyticsData.KEY_CHECK_OUT_DATE;
import static com.whitbread.premierinn.summary.analytics.SummaryAnalyticsData.KEY_CHECK_OUT_DAY;
import static com.whitbread.premierinn.summary.analytics.SummaryAnalyticsData.KEY_CHILDREN;
import static com.whitbread.premierinn.summary.analytics.SummaryAnalyticsData.KEY_NIGHTS;
import static com.whitbread.premierinn.summary.analytics.SummaryAnalyticsData.KEY_RATE_DESCRIPTION;
import static com.whitbread.premierinn.summary.analytics.SummaryAnalyticsData.KEY_ROOMS;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;

import com.contentsquare.android.api.model.CustomVar;
import com.whitbread.premierinn.common.RoomBooking;
import com.whitbread.premierinn.common.analytics.AnalyticsConstants;
import com.whitbread.premierinn.common.utils.AppExtensions;
import com.whitbread.premierinn.common.utils.TrackingAnalyticsUtils;
import com.whitbread.premierinn.domain.common.PriceDomain;
import com.whitbread.premierinn.hoteldetails.SelectedRate;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kotlin.Pair;

@RunWith(MockitoJUnitRunner.class)
public class ReviewBookingAnalyticsDataTest {

    private final List<RoomBooking> selectedRooms = new ArrayList<>();
    private final LocalDate arrivalDate = LocalDate.of(2017, 4, 5);
    @Mock
    SelectedRate selectedRate;


    @Before
    public void setUp() throws Exception {
        when(selectedRate.code()).thenReturn("FLEX RATE");
    }

    @Test
    public void testContextData() {
        ReviewBookingAnalyticsData data = ReviewBookingAnalyticsData.builder()
                .hotelCode("ABCDE")
                .selectedRate(selectedRate)
                .nights(1)
                .rooms(2)
                .adults(2)
                .children(2)
                .checkInDate(arrivalDate)
                .selectedExtras(
                        new Pair<>(
                                Collections.emptyList(),
                                Collections.emptyList()
                        )
                )
                .screenType(BOOKING_FLOW)
                .lettingType("hello")
                .roomPrice(PriceDomain.Companion.createWithGBPCurrency(10f))
                .selectedRooms(selectedRooms)
                .businessUser(false)
                .isEmployeeRateSelected(false)
                .promoCode("promo_code")
                .promoName("promo_name")
                .rateTag("rate_tag")
                .build();

        assertEquals(expectedContextData(), data.contextData());
    }

    @Test
    public void testCustomCSQVarsData() {
        ReviewBookingAnalyticsData data = ReviewBookingAnalyticsData.builder()
                .hotelCode("ABCDE")
                .selectedRate(selectedRate)
                .nights(1)
                .rooms(2)
                .adults(2)
                .children(2)
                .checkInDate(arrivalDate)
                .selectedExtras(
                        new Pair<>(
                                Collections.emptyList(),
                                Collections.emptyList()
                        )
                )
                .screenType(BOOKING_FLOW)
                .lettingType("hello")
                .roomPrice(PriceDomain.Companion.createWithGBPCurrency(10f))
                .selectedRooms(selectedRooms)
                .businessUser(false)
                .isEmployeeRateSelected(false)
                .build();

        assertEquals(expectedCustomCSQVarsData(), data.customCSQVars());
    }

    private List<CustomVar> expectedCustomCSQVarsData() {
        return Arrays.asList(
                new CustomVar(7, KEY_RATE_CODE, "FLEX RATE"),
                new CustomVar(19, KEY_LETTING_TYPE, ""),
                new CustomVar(14, KEY_CUSTOMER_TYPE, "Leisure")
        );
    }

    private Map<String, String> expectedContextData() {
        final Map<String, String> contextData = new HashMap<>();

        contextData.put(KEY_NIGHTS, "1");
        contextData.put(KEY_ROOMS, "2");
        contextData.put(KEY_ADULTS, "2");
        contextData.put(KEY_CHILDREN, "2");
        contextData.put(KEY_CHECK_IN_DATE, formattedArrivalDate(arrivalDate));
        contextData.put(KEY_CHECK_OUT_DATE, formattedDepartureDate(arrivalDate, 1));
        contextData.put(KEY_CHECK_IN_DAY, formattedCheckInDay(arrivalDate));
        contextData.put(KEY_CHECK_OUT_DAY, formattedCheckOutDay(arrivalDate, 1));
        contextData.put(KEY_CHECK_IN_OUT_DAY, formattedCheckInOutDay(arrivalDate, 1));
        contextData.put(KEY_RATE_DESCRIPTION, selectedRate.description());
        contextData.put(KEY_RATE_NAME, selectedRate.rateName());
        contextData.put(AnalyticsConstants.Key.PRODUCTS, ";" + "ABCDE");
        contextData.put(AnalyticsConstants.Key.EVENTS, "scCheckout");
        contextData.put(KEY_ADDED_EXTRAS_DESCRIPTION, AppExtensions.toAnalyticsDataString(new Pair<>(
                Collections.emptyList(),
                Collections.emptyList()
        )));


        contextData.put(KEY_RATE_CODE, TrackingAnalyticsUtils.formatRateCode(selectedRate.code()));

        contextData.put(SCREEN_TYPE, format(BOOKING_FLOW));

        contextData.put(AnalyticsConstants.Key.PROMO_CODE, "promo_code");
        contextData.put(AnalyticsConstants.Key.PROMO_NAME, "promo_name");
        contextData.put(AnalyticsConstants.Key.ALL_RATE_TAGS, "rate_tag");
        contextData.put(AnalyticsConstants.Key.ALL_DISCOUNT_CODE_APPLIED, "true");

        return contextData;
    }
}