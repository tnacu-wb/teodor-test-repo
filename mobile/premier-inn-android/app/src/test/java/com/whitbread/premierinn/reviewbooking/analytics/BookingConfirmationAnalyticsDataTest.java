package com.whitbread.premierinn.reviewbooking.analytics;

import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.ALL_DISCOUNT_CODE_APPLIED;
import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.ALL_PROMO_BOOKING_COMPLETE;
import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.ALL_RATE_TAGS;
import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.CURRENCY;
import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.PROMO_CODE;
import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.PROMO_NAME;
import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.SCREEN_TYPE;
import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.Type.BOOKING_FLOW;
import static com.whitbread.premierinn.reviewbooking.analytics.BookingConfirmationAnalyticsData.KEY_ADDED_EXTRAS;
import static com.whitbread.premierinn.reviewbooking.analytics.BookingConfirmationAnalyticsData.KEY_ADDED_EXTRAS_SELECTED;
import static com.whitbread.premierinn.reviewbooking.analytics.BookingConfirmationAnalyticsData.KEY_ALL_BOOKINGS;
import static com.whitbread.premierinn.reviewbooking.analytics.BookingConfirmationAnalyticsData.KEY_BAC_ALCOHOL_BOUGHT;
import static com.whitbread.premierinn.reviewbooking.analytics.BookingConfirmationAnalyticsData.KEY_BAC_DINNER_BOUGHT;
import static com.whitbread.premierinn.reviewbooking.analytics.BookingConfirmationAnalyticsData.KEY_BAC_DINNER_BUDGET;
import static com.whitbread.premierinn.reviewbooking.analytics.BookingConfirmationAnalyticsData.KEY_BAC_PARKING;
import static com.whitbread.premierinn.reviewbooking.analytics.BookingConfirmationAnalyticsData.KEY_BAC_ULTIMATE_WIFI;
import static com.whitbread.premierinn.reviewbooking.analytics.BookingConfirmationAnalyticsData.KEY_BOOKING_FLOW_ACCOUNT_CREATED;
import static com.whitbread.premierinn.reviewbooking.analytics.BookingConfirmationAnalyticsData.KEY_BOOKING_FLOW_COMPLETE_TIME;
import static com.whitbread.premierinn.reviewbooking.analytics.BookingConfirmationAnalyticsData.KEY_BOOKING_ID;
import static com.whitbread.premierinn.reviewbooking.analytics.BookingConfirmationAnalyticsData.KEY_BOOKING_PROMO_CODE;
import static com.whitbread.premierinn.reviewbooking.analytics.BookingConfirmationAnalyticsData.KEY_BOOKING_ROOM_DESCRIPTION;
import static com.whitbread.premierinn.reviewbooking.analytics.BookingConfirmationAnalyticsData.KEY_PROMO_BOOKING_COMPLETE;
import static com.whitbread.premierinn.reviewbooking.analytics.BookingConfirmationAnalyticsData.KEY_CARD;
import static com.whitbread.premierinn.reviewbooking.analytics.BookingConfirmationAnalyticsData.KEY_CHECK_IN_DATE;
import static com.whitbread.premierinn.reviewbooking.analytics.BookingConfirmationAnalyticsData.KEY_CHECK_OUT_DATE;
import static com.whitbread.premierinn.reviewbooking.analytics.BookingConfirmationAnalyticsData.KEY_CONF_CUSTOMER_TYPE;
import static com.whitbread.premierinn.reviewbooking.analytics.BookingConfirmationAnalyticsData.KEY_CONF_RATE_CODE;
import static com.whitbread.premierinn.reviewbooking.analytics.BookingConfirmationAnalyticsData.KEY_DONATION;
import static com.whitbread.premierinn.reviewbooking.analytics.BookingConfirmationAnalyticsData.KEY_EARLY_CHECK_IN;
import static com.whitbread.premierinn.reviewbooking.analytics.BookingConfirmationAnalyticsData.KEY_FOOD_REVENUE;
import static com.whitbread.premierinn.reviewbooking.analytics.BookingConfirmationAnalyticsData.KEY_GOSH_DONATION;
import static com.whitbread.premierinn.reviewbooking.analytics.BookingConfirmationAnalyticsData.KEY_LATE_CHECK_OUT;
import static com.whitbread.premierinn.reviewbooking.analytics.BookingConfirmationAnalyticsData.KEY_LEAD_DAYS;
import static com.whitbread.premierinn.reviewbooking.analytics.BookingConfirmationAnalyticsData.KEY_CONF_LETTING_TYPES;
import static com.whitbread.premierinn.reviewbooking.analytics.BookingConfirmationAnalyticsData.KEY_NUM_ROOMS_BOOKED;
import static com.whitbread.premierinn.reviewbooking.analytics.BookingConfirmationAnalyticsData.KEY_PAYMENT_METHOD;
import static com.whitbread.premierinn.reviewbooking.analytics.BookingConfirmationAnalyticsData.KEY_PAYMENT_OUTAGE;
import static com.whitbread.premierinn.reviewbooking.analytics.BookingConfirmationAnalyticsData.KEY_PAYMENT_TIMING;
import static com.whitbread.premierinn.reviewbooking.analytics.BookingConfirmationAnalyticsData.KEY_PRE_PAY;
import static com.whitbread.premierinn.reviewbooking.analytics.BookingConfirmationAnalyticsData.KEY_PURCHASE;
import static com.whitbread.premierinn.reviewbooking.analytics.BookingConfirmationAnalyticsData.KEY_RATE_INFO;
import static com.whitbread.premierinn.reviewbooking.analytics.BookingConfirmationAnalyticsData.KEY_REVENUE;
import static com.whitbread.premierinn.reviewbooking.analytics.BookingConfirmationAnalyticsData.KEY_ROOM_TYPE_CODES;
import static com.whitbread.premierinn.reviewbooking.analytics.BookingConfirmationAnalyticsData.KEY_UPSELL_REVENUE;
import static junit.framework.TestCase.assertEquals;

import com.contentsquare.android.api.model.CustomVar;
import com.whitbread.premierinn.api.response.InstanceFactory;
import com.whitbread.premierinn.api.response.availability.UpsellItem;
import com.whitbread.premierinn.api.response.booking.BookingAvailability;
import com.whitbread.premierinn.api.response.booking.BookingPrice;
import com.whitbread.premierinn.api.response.booking.BookingRatePlan;
import com.whitbread.premierinn.api.response.booking.BookingRoom;
import com.whitbread.premierinn.common.RoomBooking;
import com.whitbread.premierinn.common.mapper.CommonMappersKt;
import com.whitbread.premierinn.common.utils.DateUtils;
import com.whitbread.premierinn.domain.common.PriceDomain;
import com.whitbread.premierinn.hoteldetails.MappersKt;
import com.whitbread.premierinn.summary.models.ParcelableExtrasItem;

import org.junit.Test;
import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kotlin.Pair;

public class BookingConfirmationAnalyticsDataTest {

    private static final String CARD = "VT";
    private static final String CONFIRMATION_NUMBER = "ABC12345";
    private static final String HOTEL_CODE = "ABC123";
    private static final long TIMESTAMP_15_NOV_2016_1300 = 1479214800000L;
    private static final LocalDate ARRIVAL_DATE = LocalDate.of(2017, 1, 1);
    private static final Date BOOKING_DATE = new Date(TIMESTAMP_15_NOV_2016_1300);
    private static final int NIGHTS = 3;
    private static final PriceDomain TOTAL_PRICE = PriceDomain.Companion.createWithGBPCurrency(432f);
    private static final String KEY_PRODUCTS = "&&products";
    private static final String KEY_EVENTS = "&&events";
    private static final String PAYMENT_METHOD = "CARD";
    private static final boolean PAYMENT_OUTAGE = true;

    private static final Pair<Boolean, String> CARD_TYPE_PAIR = new Pair<>(false, "MC");

    @Test
    public void testProducesValidString() {

        BookingAvailability bookingAvailability = InstanceFactory.create(BookingAvailability.class,
                "apiTest/booking-availabilities-request_both_rates.json");

        BookingRatePlan bookingRatePlan = bookingAvailability.ratePlans().get(0);

        List<UpsellItem> selectedUpSellItems = new ArrayList<>();

        List<ParcelableExtrasItem> extrasItemDomains = new ArrayList<>();


        UpsellItem upsellItem = UpsellItem.builder()
                .code("BFADBF")
                .legend("Premier Inn Breakfast")
                .price(new BookingPrice(10.50f, "GBP")) // Adjust constructor as needed
                .foodUpsell(true)
                .availableForChildren(true)
                .freeBreakfastOption(false)
                .freeBreakfastTrigger(false)
                .freeBreakfastCode("BFCHDF")
                .description("Some new description")
                .shortDescription("Short description")
                .files(null) // or provide a List<File> if needed
                .build();

        selectedUpSellItems.add(upsellItem);

        extrasItemDomains.add(0, new ParcelableExtrasItem(
                "HSCKIN",
                "Early check-in",
                10.0,
                "/content/dam/global/extras/early-check-in.png",
                "Check in any time from 11am (normal check-in time is 3pm).",
                "GBP",
                1,
                10
        ));

        extrasItemDomains.add(1, new ParcelableExtrasItem(
                "HSCOU2",
                "Late check-out",
                10.0,
                "/content/dam/global/extras/late-checkout.png",
                "Check out any time until 2pm (normal check-out time is 12pm).",
                "GBP",
                1,
                10
        ));

        List<RoomBooking> roomBookings = convertRoomRatesToRoomBooking(bookingAvailability,
                bookingAvailability.ratePlans().get(0).getClassification());

        BookingConfirmationAnalyticsData bookingConfirmationAnalyticsData =
                BookingConfirmationAnalyticsData.builder()
                        .prepaid(true)
                        .isLoggedIn(true)
                        .card(CARD)
                        .cardType(CARD_TYPE_PAIR)
                        .confirmationNumber(CONFIRMATION_NUMBER)
                        .arrivalDate(LocalDate.of(2017, 1, 1))
                        .bookingCompleteTime(BOOKING_DATE)
                        .numNights(NIGHTS)
                        .business(false)
                        .donationRevenue(10f)
                        .paymentMethod(PAYMENT_METHOD)
                        .isPaymentOutage(PAYMENT_OUTAGE)
                        .hotelCode(HOTEL_CODE)
                        .numNights(NIGHTS)
                        .totalCostExcludingCC(234f)
                        .totalUpsellCost(10f)
                        .addedExtras(new Pair<>(selectedUpSellItems, extrasItemDomains))
                        .totalPrice(TOTAL_PRICE)
                        .businessDinnerAllowanceSelected(false)
                        .businessAlcoholSelected(false)
                        .dinnerBudget(PriceDomain.Companion.createDefault())
                        .businessParkingSelected(false)
                        .businessUltimateWifi(false)
                        .roomsBooked(roomBookings)
                        .rateCode(bookingRatePlan.getRateCode())
                        .numGuests(1)
                        .goshDonation(3f)
                        .lettingType("LETTINGTYPE")
                        .roomPrice(PriceDomain.Companion.createWithGBPCurrency(10f))
                        .accountCreated(false)
                        .promoCode("AAAA")
                        .promoName("promo_name")
                        .rateTag("rate_tag")
                        .paymentTiming("PAY_NOW")
                        .promoBookingComplete(true)
                        .build();

        Map<String, String> expectedContextData = expectedContextData(productString(), eventString());
        Map<String, String> actualContextData = bookingConfirmationAnalyticsData.contextData();
        assertMapsEqual(expectedContextData, actualContextData);

        List<CustomVar> expectedCustomCSQVars = expectedCustomCSQVarsData();
        List<CustomVar> actualCustomCSQVars = bookingConfirmationAnalyticsData.customCSQVars();
        assertEquals(expectedCustomCSQVars, actualCustomCSQVars);

    }

    private void assertMapsEqual(Map<String, String> expected, Map<String, String> actual) {
        for (String key : actual.keySet()) {
            assertEquals(key, expected.get(key), actual.get(key));
        }
        assertEquals(expected.size(), actual.size());
    }

    private static List<RoomBooking> convertRoomRatesToRoomBooking(BookingAvailability bookingAvailability,
                                                                    String chosenRateClassification) {
        List<RoomBooking> roomBookings = new ArrayList<>();
        for (BookingRatePlan bookingRatePlan : bookingAvailability.ratePlans()) {
            if (bookingRatePlan.getClassification() == chosenRateClassification) {
                for (BookingRoom room : bookingRatePlan.getRooms()) {
                    RoomBooking roomBooking = new RoomBooking(room.getAdults(),
                            room.getChildren(),
                            0,
                            room.getCot(),
                            room.getType(),
                            room.getLettingType(),
                            MappersKt.toDailyRatesInputApp(room.getDailyRates()),
                            room.getCityTax() != null ? CommonMappersKt.toParcelablePrice(room.getCityTax()) : null,
                            room.getRoomNumber(),
                            null);

                    roomBookings.add(roomBooking);
                }
            }
        }
        return roomBookings;
    }

    private Map<String, String> expectedContextData(String productString, String eventString) {

        Map<String, String> contextData = new HashMap<>();

        contextData.put(SCREEN_TYPE, "And:" + BOOKING_FLOW);
        contextData.put(KEY_PRE_PAY, "Prepay");
        contextData.put(KEY_PAYMENT_METHOD, "CARD");
        contextData.put(KEY_LEAD_DAYS, String.valueOf(DateUtils.getLeadDays(
                ARRIVAL_DATE, LocalDate.of(2016, 11, 15))));
        contextData.put(KEY_BOOKING_ID, CONFIRMATION_NUMBER);
        contextData.put(KEY_BOOKING_FLOW_COMPLETE_TIME, "13:00");
        contextData.put(KEY_CHECK_IN_DATE, "01/01/2017");
        contextData.put(KEY_CHECK_OUT_DATE, "04/01/2017");
        contextData.put(KEY_CONF_LETTING_TYPES, "DBS");
        contextData.put(KEY_ROOM_TYPE_CODES, "DB");
        contextData.put(KEY_CONF_RATE_CODE, "RT");
        contextData.put(KEY_RATE_INFO, "RT-LETTINGTYPE-10.00");
        contextData.put(KEY_NUM_ROOMS_BOOKED, "1");
        contextData.put(KEY_PAYMENT_OUTAGE, "true");
        contextData.put(KEY_ALL_BOOKINGS, "1");
        contextData.put(KEY_PURCHASE, "1");
        contextData.put(KEY_CONF_CUSTOMER_TYPE, "Leisure");
        contextData.put(KEY_CARD, "MC:NO CCHF");
        contextData.put(KEY_ADDED_EXTRAS, NIGHTS + " x " + "Premier Inn Breakfast 1 x Early check-in 1 x Late check-out ");
        contextData.put(KEY_FOOD_REVENUE, "1");
        contextData.put(KEY_ADDED_EXTRAS_SELECTED, "1");
        contextData.put(KEY_DONATION, "1");
        contextData.put(KEY_BAC_ALCOHOL_BOUGHT, String.valueOf(false));
        contextData.put(KEY_BAC_DINNER_BUDGET, "0.0");
        contextData.put(KEY_BAC_PARKING, String.valueOf(false));
        contextData.put(KEY_PRODUCTS, productString);
        contextData.put(KEY_EVENTS, eventString);
        contextData.put(KEY_UPSELL_REVENUE, "10.50");
        contextData.put(KEY_EARLY_CHECK_IN, "10.0");
        contextData.put(KEY_LATE_CHECK_OUT, "10.0");
        contextData.put(KEY_GOSH_DONATION, "3.0");
        contextData.put(KEY_BAC_DINNER_BOUGHT, String.valueOf(false));
        contextData.put(KEY_BAC_ULTIMATE_WIFI, String.valueOf(false));
        contextData.put(KEY_BOOKING_FLOW_ACCOUNT_CREATED, String.valueOf(false));
        contextData.put(KEY_BOOKING_PROMO_CODE, "AAAA");
        contextData.put(KEY_PROMO_BOOKING_COMPLETE, String.valueOf(true));
        contextData.put(KEY_PAYMENT_TIMING, "true");
        contextData.put(KEY_BOOKING_ROOM_DESCRIPTION, "DOUBLE room");

        contextData.put(PROMO_CODE, "AAAA");
        contextData.put(PROMO_NAME, "promo_name");
        contextData.put(ALL_RATE_TAGS, "rate_tag");
        contextData.put(ALL_PROMO_BOOKING_COMPLETE, "true");
        contextData.put(ALL_DISCOUNT_CODE_APPLIED, "true");
        return contextData;
    }

    private List<CustomVar> expectedCustomCSQVarsData() {
        return Arrays.asList(
                new CustomVar(8, KEY_CONF_RATE_CODE, "RT"),
                new CustomVar(18, KEY_CONF_LETTING_TYPES, "DBS"),
                new CustomVar(10, KEY_CONF_CUSTOMER_TYPE, "Leisure"),
                new CustomVar(17, CURRENCY, TOTAL_PRICE.getCurrency()),
                new CustomVar(16, KEY_REVENUE, String.valueOf(TOTAL_PRICE.getAmount())),
                new CustomVar(15, KEY_BOOKING_ID, CONFIRMATION_NUMBER)
        );
    }

    private static String productString() {
        return
                ";"
                        + HOTEL_CODE
                        + ";"
                        + NIGHTS
                        + ";"
                        + TOTAL_PRICE.getAmount()
                        + ";"
                        + "event37=" + 10f
                        + "|"
                        + "event20=" + 224.0f
                        + "|"
                        + "event54=" + 10.0f
                        + "|"
                        + "event31=" + 1;
    }

    private static String eventString() {
        return "event82=3,event37=10.0,event20=224.0,event31=1,event54=10.0";
    }
}