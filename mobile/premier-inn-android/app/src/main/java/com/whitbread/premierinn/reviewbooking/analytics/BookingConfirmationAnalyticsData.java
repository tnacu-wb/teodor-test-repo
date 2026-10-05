package com.whitbread.premierinn.reviewbooking.analytics;

import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.ALL_DISCOUNT_CODE_APPLIED;
import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.ALL_PROMO_BOOKING_COMPLETE;
import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.ALL_RATE_TAGS;
import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.CURRENCY;
import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.EVENTS;
import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.PRODUCTS;
import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.PROMO_CODE;
import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.PROMO_NAME;
import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.SCREEN_TYPE;
import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.Type.BOOKING_FLOW;
import static com.whitbread.premierinn.common.format.DateFormat.HOUR_AND_MINUTES;
import static com.whitbread.premierinn.common.format.DateFormat.SLASHED_DAY_MONTH_YEAR;
import static com.whitbread.premierinn.common.utils.AppExtensions.calculateTotalPrices;
import static com.whitbread.premierinn.common.utils.DateUtils.getLeadDays;
import static com.whitbread.premierinn.common.utils.StringUtils.toColonSeparatedString;
import static com.whitbread.premierinn.common.utils.StringUtils.toCommaSeparatedString;
import static com.whitbread.premierinn.common.utils.TrackingAnalyticsUtils.buildEvent;
import static com.whitbread.premierinn.common.utils.TrackingAnalyticsUtils.format;
import static com.whitbread.premierinn.common.utils.TrackingAnalyticsUtils.prepayLabel;
import static com.whitbread.premierinn.common.utils.TrackingAnalyticsUtils.tripTypeLabel;
import static com.whitbread.premierinn.domain.common.OperaCommonExtensionsKt.EXTRAS_LIST;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.contentsquare.android.api.model.CustomVar;
import com.google.auto.value.AutoValue;
import com.whitbread.premierinn.api.response.availability.UpsellItem;
import com.whitbread.premierinn.common.PaymentTimingChoice;
import com.whitbread.premierinn.common.PaymentTimingRule;
import com.whitbread.premierinn.common.RoomBooking;
import com.whitbread.premierinn.common.analytics.AnalyticsData;
import com.whitbread.premierinn.common.analytics.ProductValues;
import com.whitbread.premierinn.common.format.FormatExtensionsKt;
import com.whitbread.premierinn.common.utils.TrackingAnalyticsUtils;
import com.whitbread.premierinn.domain.common.OperaRoomTypesKt;
import com.whitbread.premierinn.domain.common.PriceDomain;
import com.whitbread.premierinn.summary.models.ParcelableExtrasItem;

import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

import kotlin.Pair;

/**
 * This class encapsulates all analytics data that needs to be part of the Booking Confirmation tracking.
 * https://whitbreadis.atlassian.net/wiki/spaces/~Tom.Pinney/pages/113057641/Android+Confirmation+Analytics
 */
@AutoValue
public abstract class BookingConfirmationAnalyticsData implements AnalyticsData {

    //Context data keys
    static final String KEY_PRE_PAY = "analyticsData.conf.prepay";
    static final String KEY_CARD = "analyticsData.conf.card";
    static final String KEY_LEAD_DAYS = "analyticsData.conf.leadDays";
    static final String KEY_BOOKING_ID = "analyticsData.conf.bookingID";
    static final String KEY_BOOKING_FLOW_COMPLETE_TIME = "analyticsData.conf.bookingFlowCompleteTime";
    static final String KEY_CHECK_IN_DATE = "analyticsData.conf.checkin";
    static final String KEY_CHECK_OUT_DATE = "analyticsData.conf.checkout";
    static final String KEY_LETTING_TYPES = "analyticsData.conf.lettingTypes";
    static final String KEY_CONF_LETTING_TYPES = "analyticsData.conf.lettingTypes";
    static final String KEY_ROOM_TYPE_CODES = "analyticsData.conf.roomTypeCodes";
    static final String KEY_CONF_RATE_CODE = "analyticsData.conf.rateCode";
    static final String KEY_RATE_INFO = "analyticsData.conf.rateName";
    static final String KEY_CONF_CUSTOMER_TYPE = "analyticsData.conf.customerType";
    static final String KEY_BAC_DINNER_BOUGHT = "analyticsData.conf.BAC.dinner";
    static final String KEY_BAC_ALCOHOL_BOUGHT = "analyticsData.conf.BAC.alcohol";
    static final String KEY_BAC_DINNER_BUDGET = "analyticsData.conf.BAC.dinnerBudget";
    static final String KEY_BAC_PARKING = "analyticsData.conf.BAC.parking";
    static final String KEY_BAC_ULTIMATE_WIFI = "analyticsData.conf.BAC.ultimateWifi";
    static final String KEY_ADDED_EXTRAS = "analyticsData.conf.addedExtras";
    static final String KEY_GOSH_DONATION = "analyticsData.conf.goshAmount";
    static final String KEY_PAYMENT_METHOD = "analyticsData.bf.paymentMethod";
    static final String KEY_PAYMENT_OUTAGE = "analyticsData.conf.paymentOutage";
    static final String KEY_PURCHASE = "analyticsData.conf.events.purchase";
    static final String KEY_FOOD_REVENUE = "analyticsData.conf.events.event37";
    static final String KEY_ADDED_EXTRAS_SELECTED = "analyticsData.conf.events.event30";
    static final String KEY_ALL_BOOKINGS = "analyticsData.conf.events.event20";
    static final String KEY_DONATION = "analyticsData.conf.events.event54";
    static final String KEY_NUM_ROOMS_BOOKED = "analyticsData.conf.events.event31";
    static final String KEY_BOOKING_FLOW_ACCOUNT_CREATED = "analyticsData.conf.events.BFAccountCreated";
    static final String KEY_PROMO_BOOKING_COMPLETE = "analyticsData.conf.promoBookingComplete";

    static final String KEY_UPSELL_REVENUE = "analyticsData.conf.upsellrevenue";

    static final String KEY_EARLY_CHECK_IN = "analyticsData.conf.earlyCheckIn";

    static final String KEY_LATE_CHECK_OUT = "analyticsData.conf.lateCheckOut";
    static final String KEY_BOOKING_PROMO_CODE = "analyticsData.conf.promoCode";
    static final String KEY_BOOKING_ROOM_DESCRIPTION = "analyticsData.conf.bookingRoomDescription";
    static final String KEY_REVENUE = "revenue";
    static final String KEY_PAYMENT_TIMING = "analyticsData.conf.paymentTakenNow";

    //Event data keys
    private static final String EVENT_FOOD_REVENUE = "event37=%s";
    private static final String EVENT_ROOM_REVENUE = "event20=%s";
    private static final String EVENT_DONATION_REVENUE = "event54=%s";
    private static final String EVENT_NUM_ROOMS = "event31=%s";
    private static final String EVENT_ROOM_NIGHTS = "event82=%s"; // (Number of rooms) * (number of nights of booking)

    @Nullable
    public abstract Pair<List<UpsellItem>, List<ParcelableExtrasItem>> addedExtras();

    public abstract float totalUpsellCost();

    public abstract float totalCostExcludingCC();

    public abstract float donationRevenue();

    public abstract String hotelCode();

    public abstract int numNights();

    public abstract PriceDomain totalPrice();

    public abstract LocalDate arrivalDate();

    public abstract boolean business();

    public abstract String confirmationNumber();

    public abstract boolean prepaid();

    public abstract String paymentMethod();

    public abstract boolean isPaymentOutage();

    public abstract int numGuests();

    public abstract String rateCode();

    public abstract String lettingType();

    @Nullable
    public abstract String card();

    public abstract Date bookingCompleteTime();

    public abstract List<RoomBooking> roomsBooked();

    public abstract boolean isLoggedIn();

    public abstract boolean businessDinnerAllowanceSelected();

    public abstract boolean businessAlcoholSelected();

    public abstract PriceDomain dinnerBudget();

    public abstract boolean businessParkingSelected();

    public abstract boolean businessUltimateWifi();

    public abstract float goshDonation();

    public abstract PriceDomain roomPrice();

    public abstract boolean accountCreated();

    public abstract Pair<Boolean, String> cardType();

    @Nullable
    public abstract String promoCode();

    @Nullable
    public abstract String promoName();

    @Nullable
    public abstract String rateTag();

    public abstract String paymentTiming();

    public abstract boolean promoBookingComplete();

    public static Builder builder() {
        return new AutoValue_BookingConfirmationAnalyticsData.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder addedExtras(Pair<List<UpsellItem>, List<ParcelableExtrasItem>> addedExtras);

        public abstract Builder totalUpsellCost(float upsellCost);

        public abstract Builder totalCostExcludingCC(float roomRevenue);

        public abstract Builder donationRevenue(float donationRevenue);

        public abstract Builder hotelCode(String hotelCode);

        public abstract Builder numNights(int nights);

        public abstract Builder totalPrice(PriceDomain totalPrice);

        public abstract Builder arrivalDate(LocalDate arrivalDate);

        public abstract Builder business(boolean business);

        public abstract Builder confirmationNumber(String confirmationNumber);

        public abstract Builder prepaid(boolean prepaid);

        public abstract Builder paymentMethod(String paymentMethod);

        public abstract Builder isPaymentOutage(boolean isPaymentOutage);

        public abstract Builder numGuests(int numGuests);

        public abstract Builder rateCode(String rateCode);

        public abstract Builder lettingType(String lettingType);

        public abstract Builder card(String cardType);

        public abstract Builder cardType(Pair<Boolean, String> pair);

        public abstract Builder bookingCompleteTime(Date time);

        public abstract Builder roomsBooked(List<RoomBooking> rooms);

        public abstract Builder isLoggedIn(boolean isLoggedIn);

        public abstract Builder businessDinnerAllowanceSelected(boolean dinner);

        public abstract Builder businessAlcoholSelected(boolean alcohol);

        public abstract Builder dinnerBudget(PriceDomain dinnerBudget);

        public abstract Builder businessParkingSelected(boolean parking);

        public abstract Builder businessUltimateWifi(boolean ultimateWifi);

        public abstract Builder goshDonation(float goshDonation);

        public abstract Builder roomPrice(PriceDomain roomPrice);

        public abstract Builder accountCreated(boolean accountCreated);

        public abstract Builder promoCode(@Nullable String promoCode);

        public abstract Builder promoName(@Nullable String promoName);

        public abstract Builder rateTag(@Nullable String rateTag);

        public abstract Builder paymentTiming(String paymentTiming);

        public abstract Builder promoBookingComplete(boolean promoBookingComplete);

        public abstract BookingConfirmationAnalyticsData build();
    }

    private LocalDate departureDate() {
        return arrivalDate().plusDays(numNights());
    }

    private String formattedArrivalDate() {
        return FormatExtensionsKt.format(arrivalDate(), SLASHED_DAY_MONTH_YEAR);
    }

    private String formattedDepartureDate() {
        return FormatExtensionsKt.format(departureDate(), SLASHED_DAY_MONTH_YEAR);
    }

    private String formattedBookingCompleteTime() {
        return HOUR_AND_MINUTES.format(bookingCompleteTime());
    }

    public int numRoomsBooked() {
        return roomsBooked().size();
    }

    private int leadDays() {
        Calendar bookingDateInCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        bookingDateInCalendar.setTime(bookingCompleteTime());

        LocalDate bookingLocalDate = LocalDate.of(bookingDateInCalendar.get(Calendar.YEAR),
                bookingDateInCalendar.get(Calendar.MONTH) + 1,
                bookingDateInCalendar.get(Calendar.DAY_OF_MONTH));
        return getLeadDays(arrivalDate(), bookingLocalDate);
    }

    private List<String> bookedLettingTypes() {
        List<String> roomLettingTypes = new ArrayList<>();
        for (RoomBooking bookingRoom : roomsBooked()) {
            roomLettingTypes.add(bookingRoom.getLettingCode());
        }
        return roomLettingTypes;
    }

    private List<String> bookedRoomDescriptions() {
        List<String> roomDescriptions = new ArrayList<>();
        for (RoomBooking bookingRoom : roomsBooked()) {
            roomDescriptions.add(String.format("%s room", OperaRoomTypesKt.toRoomTypeGQL(bookingRoom.getType())));
        }
        return roomDescriptions;
    }

    private List<String> bookedRoomTypeCodes() {
        List<String> roomTypeCodes = new ArrayList<>();
        for (RoomBooking room : roomsBooked()) {
            roomTypeCodes.add(room.getType());
        }
        return roomTypeCodes;
    }

    private float totalRoomRevenue() {
        return totalCostExcludingCC() - totalUpsellCost();
    }

    @Override
    public Map<String, String> contextData() {
        Map<String, String> contextData = new HashMap<>();

        contextData.put(SCREEN_TYPE, format(BOOKING_FLOW));
        if (paymentMethod().equals(PaymentTimingRule.RESERVE_WITHOUT_CARD.name())) {
            contextData.put(KEY_PRE_PAY, prepayLabel(false));
        } else {
            contextData.put(KEY_PRE_PAY, prepayLabel(prepaid()));
        }
        if (isPaymentOutage()) {
            contextData.put(KEY_PAYMENT_OUTAGE, String.valueOf(isPaymentOutage()));
        }
        contextData.put(KEY_PAYMENT_METHOD, paymentMethod());
        contextData.put(KEY_CARD, formattedCardTypeAndFee(cardType()));
        contextData.put(KEY_LEAD_DAYS, String.valueOf(leadDays()));
        contextData.put(KEY_BOOKING_ID, confirmationNumber());
        contextData.put(KEY_BOOKING_FLOW_COMPLETE_TIME, formattedBookingCompleteTime());
        contextData.put(KEY_CHECK_IN_DATE, formattedArrivalDate());
        contextData.put(KEY_CHECK_OUT_DATE, formattedDepartureDate());
        contextData.put(KEY_CONF_LETTING_TYPES, toCommaSeparatedString(bookedLettingTypes()));
        contextData.put(KEY_BOOKING_ROOM_DESCRIPTION, toCommaSeparatedString(bookedRoomDescriptions()));
        contextData.put(KEY_ROOM_TYPE_CODES, toColonSeparatedString(bookedRoomTypeCodes()));
        contextData.put(KEY_CONF_RATE_CODE, TrackingAnalyticsUtils.formatRateCode(rateCode()));
        contextData.put(KEY_RATE_INFO, TrackingAnalyticsUtils.formatRateInfo(rateCode(), lettingType(), roomPrice()));
        contextData.put(KEY_NUM_ROOMS_BOOKED, String.valueOf(numRoomsBooked()));
        contextData.put(KEY_ALL_BOOKINGS, String.valueOf(1));
        contextData.put(KEY_PURCHASE, String.valueOf(1));
        contextData.put(KEY_CONF_CUSTOMER_TYPE, tripTypeLabel(business()));
        contextData.put(KEY_BAC_DINNER_BOUGHT, String.valueOf(businessDinnerAllowanceSelected()));
        contextData.put(KEY_BAC_ALCOHOL_BOUGHT, String.valueOf(businessAlcoholSelected()));
        contextData.put(KEY_BAC_PARKING, String.valueOf(businessParkingSelected()));
        contextData.put(KEY_BAC_ULTIMATE_WIFI, String.valueOf(businessUltimateWifi()));
        contextData.put(KEY_PAYMENT_TIMING, formatedPaymentTiming());

        if (promoCode() != null && !promoCode().isEmpty()) {
            contextData.put(KEY_BOOKING_PROMO_CODE, promoCode());
            contextData.put(PROMO_CODE, promoCode());
            contextData.put(PROMO_NAME, promoName());
            contextData.put(ALL_RATE_TAGS, rateTag());
            contextData.put(ALL_PROMO_BOOKING_COMPLETE, "true");
            contextData.put(ALL_DISCOUNT_CODE_APPLIED, "true");
        }

        if (goshDonation() > 0f) {
            contextData.put(KEY_GOSH_DONATION, String.valueOf(goshDonation()));
        }

        if (dinnerBudget() != null) {
            contextData.put(KEY_BAC_DINNER_BUDGET, Float.toString(dinnerBudget().getAmount()));
        }

        if (addedExtras() != null) {
            contextData.put(KEY_ADDED_EXTRAS, formattedAddedExtras(addedExtras()));
            contextData.put(KEY_ADDED_EXTRAS_SELECTED, "1");

            if (addedExtras().getFirst() != null) {
                String formattedTotalMealsPrice = String.format(Locale.UK, "%.2f", formattedAddedExtrasMealPrice(addedExtras()));

                contextData.put(KEY_UPSELL_REVENUE, formattedTotalMealsPrice);
            }

            if (addedExtras().getSecond() != null) {
                Map<String, Double> extrasTotalPriceMap = calculateTotalPrices(addedExtras().getSecond(), numRoomsBooked());
                EXTRAS_LIST.forEach(id -> {
                    if (extrasTotalPriceMap.get(id) != null) {
                        if ("HSCKIN".equals(id)) {
                            contextData.put(KEY_EARLY_CHECK_IN, String.valueOf(extrasTotalPriceMap.get(id)));
                        }
                        if ("HSCOU2".equals(id)) {
                            contextData.put(KEY_LATE_CHECK_OUT, String.valueOf(extrasTotalPriceMap.get(id)));
                        }
                    }
                });
            }
        }

        if (addedExtras() != null && !addedExtras().getFirst().isEmpty()) {
            String mealItems = String.valueOf(addedExtras().getFirst().size());
            contextData.put(KEY_FOOD_REVENUE, mealItems);
        }

        if (donationRevenue() > 0f) {
            contextData.put(KEY_DONATION, "1");
        }

        contextData.put(KEY_BOOKING_FLOW_ACCOUNT_CREATED, String.valueOf(accountCreated()));
        contextData.put(KEY_PROMO_BOOKING_COMPLETE, String.valueOf(promoBookingComplete()));

        contextData.put(PRODUCTS, buildProductString());
        contextData.put(EVENTS, buildProductEventsString());

        return contextData;
    }

    @Override
    public List<CustomVar> customCSQVars() {
        return Arrays.asList(
                new CustomVar(8, KEY_CONF_RATE_CODE, TrackingAnalyticsUtils.formatRateCode(rateCode())),
                new CustomVar(18, KEY_CONF_LETTING_TYPES, toColonSeparatedString(bookedLettingTypes())),
                new CustomVar(10, KEY_CONF_CUSTOMER_TYPE, tripTypeLabel(business())),
                new CustomVar(17, CURRENCY, totalPrice().getCurrency()),
                new CustomVar(16, KEY_REVENUE, String.valueOf(totalPrice().getAmount())),
                new CustomVar(15, KEY_BOOKING_ID, confirmationNumber())
        );
    }

    public String formatedPaymentTiming() {
        return String.valueOf(Objects.equals(paymentTiming(), PaymentTimingChoice.PAY_NOW.name()));
    }

    public Float formattedAddedExtrasMealPrice(@NonNull Pair<List<UpsellItem>, List<ParcelableExtrasItem>> addedExtras) {
        float totalMealsPrice = 0f;
        if (!addedExtras.getFirst().isEmpty()) {
            for (UpsellItem upsellItem : addedExtras.getFirst()) {
                totalMealsPrice += upsellItem.price().getAmount();
            }
        }
        return totalMealsPrice;
    }

    public String formattedAddedExtras(@NonNull Pair<List<UpsellItem>, List<ParcelableExtrasItem>> addedExtras) {
        StringBuilder formattedExtras = new StringBuilder();
        if (addedExtras.getFirst() != null && !addedExtras.getFirst().isEmpty()) {
            for (UpsellItem upsellItem : addedExtras.getFirst()) {
                formattedExtras.append(String.format("%s x %s ", numNights(), upsellItem.legend()));
            }
        }
        if (addedExtras.getSecond() != null && !addedExtras.getSecond().isEmpty()) {
            for (ParcelableExtrasItem extrasItemDomain : addedExtras.getSecond()) {
                formattedExtras.append(String.format("%s x %s ", roomsBooked().size(), extrasItemDomain.getName()));
            }
        }
        return formattedExtras.toString();
    }

    public String formattedCardTypeAndFee(@NonNull Pair<Boolean, String> pair) {
        final Boolean hasCardFee = pair.getFirst();
        final String cardType = pair.getSecond();

        if (hasCardFee) {
            return String.format("%s:%s", cardType, "CCHF"); // credit card is charged
        } else {
            return String.format("%s:%s", cardType, "NO CCHF"); // credit card is not charged
        }
    }


    private String buildProductString() {
        List<String> events = new ArrayList<>();

        events.add(buildEvent(EVENT_FOOD_REVENUE, Float.toString(totalUpsellCost())));
        events.add(buildEvent(EVENT_ROOM_REVENUE, Float.toString(totalRoomRevenue())));
        events.add(buildEvent(EVENT_DONATION_REVENUE, Float.toString(donationRevenue())));
        events.add(buildEvent(EVENT_NUM_ROOMS, String.valueOf(numRoomsBooked())));

        return ProductValues.builder()
                .category("")
                .product(hotelCode())
                .quantity(numNights())
                .totalPrice(totalPrice().getAmount())
                .events(events)
                .build().toString();
    }


    private String buildProductEventsString() {
        List<String> events = new ArrayList<>();

        events.add(buildEvent(EVENT_ROOM_NIGHTS, String.valueOf((roomsBooked().size() * numNights()))));

        if (addedExtras() != null && !addedExtras().getFirst().isEmpty()) {
            events.add(buildEvent(EVENT_FOOD_REVENUE, String.valueOf(totalUpsellCost())));
        }

        events.add(buildEvent(EVENT_ROOM_REVENUE, String.valueOf(totalRoomRevenue())));
        events.add(buildEvent(EVENT_NUM_ROOMS, String.valueOf(numRoomsBooked())));

        if (donationRevenue() > 0f) {
            events.add(buildEvent(EVENT_DONATION_REVENUE, String.valueOf(donationRevenue())));
        }

        return toCommaSeparatedString(events);
    }
}
