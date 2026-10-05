//package com.whitbread.premierinn.summarybreakdown.analytics;
//
//import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key;
//import static com.whitbread.premierinn.summary.analytics.SummaryAnalyticsData.KEY_ADULTS;
//import static com.whitbread.premierinn.summary.analytics.SummaryAnalyticsData.KEY_CHECK_IN_DATE;
//import static com.whitbread.premierinn.summary.analytics.SummaryAnalyticsData.KEY_CHECK_IN_DAY;
//import static com.whitbread.premierinn.summary.analytics.SummaryAnalyticsData.KEY_CHECK_IN_OUT_DAY;
//import static com.whitbread.premierinn.summary.analytics.SummaryAnalyticsData.KEY_CHECK_OUT_DATE;
//import static com.whitbread.premierinn.summary.analytics.SummaryAnalyticsData.KEY_CHECK_OUT_DAY;
//import static com.whitbread.premierinn.summary.analytics.SummaryAnalyticsData.KEY_CHILDREN;
//import static com.whitbread.premierinn.summary.analytics.SummaryAnalyticsData.KEY_NIGHTS;
//import static com.whitbread.premierinn.summary.analytics.SummaryAnalyticsData.KEY_RATE_DESCRIPTION;
//import static com.whitbread.premierinn.summary.analytics.SummaryAnalyticsData.KEY_RATE_NAME;
//import static com.whitbread.premierinn.summary.analytics.SummaryAnalyticsData.KEY_ROOMS;
//import static com.whitbread.premierinn.summarybreakdown.analytics.SummaryBreakdownAnalyticsData.EXTRAS_SELECTED_DESC;
//import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.Type;
//import static org.hamcrest.CoreMatchers.is;
//import static org.hamcrest.MatcherAssert.assertThat;
//import static org.mockito.Mockito.when;
//import android.util.Pair;
//import org.junit.Test;
//import org.threeten.bp.LocalDate;
//import org.threeten.bp.Month;
//import java.util.ArrayList;
//
//import com.whitbread.premierinn.api.response.availability.UpsellItem;
//import com.whitbread.premierinn.summary.models.ParcelableExtrasItem;
//
//import org.junit.runner.RunWith;
//import org.mockito.Mock;
//import org.mockito.junit.MockitoJUnitRunner;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@RunWith(MockitoJUnitRunner.class)
//public class SummaryBreakdownAnalyticsDataTest {
//
//    @Mock
//    List<UpsellItem> listOfSelectedUpsells;
//
//    @Mock
//    UpsellItem selectedUpsell1;
//
//    @Mock
//    List<ParcelableExtrasItem> listOfSelectedExtras;
//
//    @Mock
//    Pair<List<UpsellItem>, List<ParcelableExtrasItem>> pairOfSelectedExtras;
//
//    @Mock
//    ParcelableExtrasItem selectedExtra1;
//
//    @Test
//    public void assertContextData() {
//        listOfSelectedUpsells = new ArrayList<>();
//        listOfSelectedUpsells.add(selectedUpsell1);
//
//        listOfSelectedExtras = new ArrayList<>();
//        listOfSelectedExtras.add(selectedExtra1);
//
//        when(pairOfSelectedExtras.first).thenReturn(listOfSelectedUpsells);
//        when(pairOfSelectedExtras.second).thenReturn(listOfSelectedExtras);
//        when(selectedUpsell1.legend()).thenReturn("PI Breakfast");
//        when(selectedExtra1.getName()).thenReturn("Late check out");
//
//        SummaryBreakdownAnalyticsData summaryBreakdownAnalyticsData = SummaryBreakdownAnalyticsData.builder()
//                .screenType(Type.BOOKING_FLOW)
//                .selectedExtras(pairOfSelectedExtras)
//                .hotelCode("hotelCode")
//                .rateCode("Flex")
//                .rateDescription("Description")
//                .rateName("FLEXRATE")
//                .arrivalDate(LocalDate.of(2010, Month.APRIL, 7))
//                .departureDate(LocalDate.of(2010, Month.APRIL, 12))
//                .nights(5)
//                .rooms(3)
//                .adults(3)
//                .children(2)
//                .build();
//
//        assertThat(getExpectedContextData(), is(summaryBreakdownAnalyticsData.contextData()));
//    }
//
//    private Map<String, Object> getExpectedContextData() {
//        Map<String, Object> contextData = new HashMap<>();
//        contextData.put(KEY_RATE_CODE, "Flex");
//        contextData.put(KEY_RATE_DESCRIPTION, "Description");
//        contextData.put(KEY_RATE_NAME, "FLEXRATE");
//        contextData.put(KEY_CHECK_IN_DATE, "07/04/2010");
//        contextData.put(KEY_CHECK_OUT_DATE, "12/04/2010");
//        contextData.put(EXTRAS_SELECTED_DESC, "PI Breakfast, Late check out");
//        contextData.put(KEY_NIGHTS, "5");
//        contextData.put(KEY_ROOMS, "3");
//        contextData.put(KEY_ADULTS, "3");
//        contextData.put(KEY_CHILDREN, "2");
//        contextData.put(KEY_CHECK_IN_DAY, "Wed");
//        contextData.put(KEY_CHECK_OUT_DAY, "Mon");
//        contextData.put(KEY_CHECK_IN_OUT_DAY, "Wed-Mon");
//        contextData.put(Key.SCREEN_TYPE, "And:Booking flow");
//        contextData.put(Key.PRODUCTS, ";hotelCode");
//        return contextData;
//    }
//}
