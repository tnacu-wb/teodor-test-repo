package com.whitbread.premierinn.common.analytics;


import com.whitbread.premierinn.account.AboutActivity;
import com.whitbread.premierinn.bookingdetails.BookingDetailsActivity;
import com.whitbread.premierinn.calendar.dialogcalendar.CalendarDialogActivity;
import com.whitbread.premierinn.editguest.EditGuestActivity;
import com.whitbread.premierinn.findbooking.FindBookingActivity;
import com.whitbread.premierinn.guestdetails.GuestDetailsActivity;
import com.whitbread.premierinn.hoteldetails.HotelDetailsActivity;
import com.whitbread.premierinn.hoteldetails.hotelimagesfullscreen.FullScreenGalleryActivity;
import com.whitbread.premierinn.landing.LandingActivity;
import com.whitbread.premierinn.loading.LoadingActivity;
import com.whitbread.premierinn.login.LoginActivity;
import com.whitbread.premierinn.mybookings.MyBookingsActivity;
import com.whitbread.premierinn.paymentbreakdown.PaymentBreakdownActivity;
import com.whitbread.premierinn.plantrip.PlanTripActivity;
import com.whitbread.premierinn.resetpassword.ResetPasswordActivity;
import com.whitbread.premierinn.reviewbooking.ReviewBookActivity;
import com.whitbread.premierinn.search.SearchActivity;
import com.whitbread.premierinn.searchresults.SearchResultsActivity;
import com.whitbread.premierinn.summary.SummaryActivityKt;
import com.whitbread.premierinn.summarybreakdown.SummaryBreakdownActivity;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AnalyticsConstants {

    public static final class ScreenState {
        public static final String PREMIER_INN = "Premier Inn";

        public static final String LOADING = "Splash";
        public static final String LANDING = "Home";

        public static final String SEARCH = "Search Criteria – Location only";
        public static final String SEARCH_RESULTS = "Search Results";

        public static final String SEARCH_NO_RESULTS_FOUND = "Search Results: no results returned";

        public static final String CRITERIA = "Search Criteria";
        public static final String ROOM_DIALOG = "Search Criteria – room type";
        public static final String CALENDAR = "Search Criteria – Date Picker";

        public static final String SEARCH_RESULTS_LIST_NAME = "Search Results List";
        public static final String SEARCH_RESULTS_MAP_NAME = "Search Results Map";
        public static final String SEARCH_RESULTS_NO_RESULTS_FOUND = "Search Results List – no availability";
        public static final String SEARCH_RESULTS_MAP_VIEW_ACTION = "SRP Map View";

        public static final String HOTEL_DETAILS = "Hotel Details";
        public static final String HOTEL_DETAILS_NO_RESULTS = "Hotel Details – no availability";

        public static final String BATHROOM_SELECTION = "Bathroom Selection";

        public static final String HOTEL_IMAGES_FULL_SCREEN = "hotel Image Gallery";

        public static final String SEE_PRICES = "Choose rates";
        public static final String SUMMARY = "Add Extras";
        public static final String SUMMARY_EXTRAS_FULL_SUMMARY = "Add Extras - View Full Summary";

        public static final String LOG_IN = "Log In";
        public static final String AUTHENTICATION_ERROR_LOG_IN = "Authentication Error: " + LOG_IN;

        public static final String CHOOSE_TWIN_ROOM = "Premier Inn Choose Twin Room";
        public static final String GUEST_DETAILS = "Your Details";
        public static final String PAYMENT_DETAILS = "Card Details";
        public static final String PAYMENT_DETAILS_3CP = "Card Details: 3CP Payment Portal Validation";

        public static final String PAYMENT_3CP_IPAGE = "Card Details";
        public static final String PAYMENT_3CP_IPAGE_PAYMENT_DETAILS_SUBMITTED = "Card Details: 3CP Payment Details Submitted";
        public static final String PAYMENT_POLLING_FAILED_3CP = "Polling Status Failed Error";
        public static final String PAYMENT_POLLING_MAX_ATTEMPTS = "Polling Reached Max Attempts Error”";
        public static final String PAYMENT_DETAILS_SUMMARY = "Card Details - Stay Summary";
        public static final String PAYMENT_BACS_ADDITIONAL_INFO = "BAC - Additional Information";

        public static final String REVIEW_BOOKING = "Review and Book";
        public static final String BOOKING_CONFIRMATION = "Booking Confirmation";

        public static final String MY_ACCOUNT = "My Account";
        public static final String ACCOUNT_LOG_IN = MY_ACCOUNT + ": " + LOG_IN;
        public static final String CREATE_ACCOUNT = MY_ACCOUNT + ": Create Account";
        public static final String MANAGE_SAVED_CARDS = MY_ACCOUNT + ": Payment Methods: Manage Saved Cards";
        public static final String CHANGE_PASSWORD = MY_ACCOUNT + ": Manage Password";
        public static final String PAYMENT_METHODS = MY_ACCOUNT + ": Payment Methods";
        public static final String MY_DETAILS = MY_ACCOUNT + ": Manage My Details";
        public static final String BOOKING_PREFERENCES = MY_ACCOUNT + ": Manage Booking Preferences";
        public static final String ROOM_PREFERENCES = BOOKING_PREFERENCES + ": Room";
        public static final String MEAL_PREFERENCES = BOOKING_PREFERENCES + ": Meal";
        public static final String ACCOUNT_CREATED = "My Account Created";
        public static final String ACCOUNT_UPDATED = "My Account Amended";
        public static final String BOOKING_PREFERENCES_CHANGED = "Booking Preferences Changed";
        public static final String PASSWORD_CHANGED = MY_ACCOUNT + ": " + "Change Password";
        public static final String CARD_DELETED = "Card Removed";
        public static final String CARD_REPLACED = "Card Replaced";
        public static final String CARD_ADDED = "Card Added";
        public static final String NEWSLETTER_UPDATES = "Newsletter Updates";
        public static final String ADDITIONAL_INFO = "Additional Information";

        public static final String MY_BOOKINGS = "My Bookings";
        public static final String BOOKING_LOG_IN = MY_BOOKINGS + ": " + LOG_IN;
        public static final String NO_BOOKINGS = MY_BOOKINGS + ": No Bookings";
        public static final String BOOKING_DETAILS = "Booking Details";
        public static final String FIND_BOOKING = "Find a Booking";
        public static final String PLAN_YOUR_TRIP = "Plan Your Trip";
        public static final String RESET_PASSWORD = "Reset Password";
        public static final String ABOUT = "App Settings";

        public static final String CHECK_IN = "Check-in Online";
        public static final String CHECK_IN_ROOM_DETAILS = "Check-in Online: Room %d guest details";
        public static final String CHECK_IN_CONFIRMATION = "Check-in Online Confirmation";
        public static final String CHECK_IN_ALTERNATE_CARD = "Check-in Online: Alternative Card Details";
        public static final String CHECK_OUT_CONFIRMATION = "Check-out Confirmation";

        public static final String AMEND_BOOKING = "Amend a Booking";
        public static final String AMEND_DATE = "Amend a Booking: Date Picker";
        public static final String AMEND_EDIT_ROOM = "Amend a Booking: Edit Room";
        public static final String AMEND_ADD_ROOM = "Amend a Booking: Add Room";
        public static final String AMEND_MEALS = "Amend: Extras";
        public static final String AMEND_AVAILABILITY = "Amend: search criteria";
        public static final String AMEND_CONFIRMATION = "Amend Confirmation";
        public static final String AMEND_REVIEW = "Amend Review";
        public static final String AMEND_AND_PAY = "Amend and Pay";

        public static final String NO_INTERNET = "Error – No internet Connection";
        public static final String FIRST_TIME_OFFER = "First Time Offer Overlay";
        public static final String FREE_BREAKFAST = "Free Breakfast Offer active";
        public static final String FIRST_TIME_DOWNLOAD = "First Time Download Overlay";


        // GDPR
        public static final String INTERSTITIAL = "Privacy Policy Acceptance Offer";
        public static final String HOW_DATA_USED = "Data Policy";
        public static final String FIRST_TIME_DOWNLOAD_OVERLAY = "First Time Download Overlay";

        // Payment
        public static final String BOOKING = "Booking: ";
        public static final String PAYMENT = "Payment: ";
        public static final String PAYMENT_UNKNOWN = PAYMENT + "Unknown";
        public static final String PAYMENT_VALIDATION = PAYMENT + "Validation";
        public static final String PAYMENT_PROVIDER_ACCOUNT = PAYMENT + "Provider account not found";
        public static final String PAYMENT_IPAGE_TEMPLATE = PAYMENT + "iPage template";
        public static final String PAYMENT_PARSING = PAYMENT + "Parsing";
        public static final String PAYMENT_ERROR_HANDLING = PAYMENT + "Error handling request";
        public static final String PAYMENT_PROVIDER_ISSUE = PAYMENT + "Provider issue";
        public static final String PAYMENT_AUTHORISED = PAYMENT + "Unauthorised";
        public static final String PAYMENT_NOT_FOUND = PAYMENT + "Payment not found";
        public static final String PAYMENT_UNABLE_TO_REFUND = PAYMENT + "Unable to refund";
        public static final String PAYMENT_PROVIDER_SESSION_TIME_OUT = PAYMENT + "Provider session timed out";
        public static final String PAYMENT_BOOKING_SESSION_TIME_OUT = PAYMENT + "Booking session timed out";

        // CIOL
        public static final String CIOL = "Check-in online: ";
        public static final String LEAVE_EASY = "Leave easy: ";
        public static final String START_CIOL = CIOL + "Start Check In";
        public static final String PRE_STAY = CIOL + "Confirm Details";
        public static final String PRE_STAY_CONTINUE_CLICK = CIOL + "Confirm Details Button Click";
        public static final String UPSELLS = CIOL + "Upsells";
        public static final String EDIT_GUEST_DETAILS = CIOL + "Room Guest Details";
        public static final String MENU_SELECTED = CIOL + "Menu Selected";
        public static final String ALLERGENS_SELECTED = CIOL + "Allergens Selected";
        public static final String UPSELL_DETAILS = CIOL + "Upsell Details";
        public static final String CIOL_PAYMENTS = CIOL + "Payment Page";
        public static final String CIOL_COMPLETION = CIOL + "Confirmation";
        public static final String CIOL_DEEPLINKING = CIOL + "Deeplink";
        public static final String CIOL_SELECT_ROOM = CIOL + "Select a room";

        public static final String CIOL_ROOM_CRITERIA = CIOL + CRITERIA;
        public static final String LEAVE_EASY_SELECTED = LEAVE_EASY + "Booking details";
        public static final String LEAVE_EASY_CONFIRMATION = LEAVE_EASY + "Confirmation";

        public static final String QR_CODE = "Booking QR Code";
        public static final String QR_CODE_INFO = "Booking QR Code Information";

        public static final String MAINTENANCE = "Down for Maintenance";
        public static final String FORCE_UPDATE = "Force Update";
        public static final String NOTIFICATIONS = "Notifications";
        public static final String NEWSLETTER = "Newsletter Updates";

        public static final String  FULL_SCREEN_GALLERY = "Gallery";
        public static final String  FULL_SCREEN_HOTEL_MAP = "Full Screen Hotel Map";
        public static final String  POSTCODE_FINDER = "Postcode Finder";

        public static final String  IMPORTANT_HOTEL_INFO = "Important Hotel Information";
        public static final String  SHORTCUT_LOCATION = "Shortcut Location";
    }

    public static final class Type {
        public static final String HOME = "Home";
        public static final String LOOK_TO_BOOK = "Look to Book";
        public static final String BOOKING_FLOW = "Booking flow";
        public static final String MY_PREMIER_INN = "My Premier Inn";
        public static final String ACCOUNT_EDITING = "Account editing";
        public static final String ACCOUNT_CREATION = "Account creation";
        public static final String UNKNOWN = "UNKNOWN";
        public static final String CIOL_FLOW = "Check-in online flow";
        public static final String LEAVE_EASY_FLOW = "Leave easy flow";
    }

    public static final class Action {
        public static final String CANCEL_BOOKING = "Booking Cancelled";
        public static final String ERROR = "Errors";
        public static final String MARKETING_PERMISSION_UPDATE = "Marketing Permission Update";
        public static final String PUSHID_ACTION = "Push ID Set";
        public static final String CIOL_START_ACTION = "Android: CIOL selected";
        public static final String CIOL_DEEPLINK_ACTION = "Android: Check-in online: Deeplink";
        public static final String CIOL_CONFIRMATION_ACTION = "Android: Check-in Online Confirmation";
        public static final String QR_CODE_SHOWN = "QR Code Shown";
        public static final String CIOL_ADOBE_PUSH_ACTION = "Android: CIOL Push Notification Opened";
        public static final String LEAVE_EASY_ADOBE_PUSH_ACTION = "Android: Leave Easy Push Notification Opened";
        public static final String LEAVE_EASY_SELECTED_ACTION = "Android: Leave Easy selected";
        public static final String LEAVE_EASY_CONFIRMATION_ACTION = "Android: Leave Easy Confirmation";
        public static final String DISCOUNT_CODE_BOX = "Discount Code Box";
    }

    //Common context data keys
    public static final class Key {
        public static final String PRODUCTS = "&&products";
        public static final String EVENTS = "&&events";
        public static final String ENVIRONMENT = "analyticsData.all.environment";
        public static final String TIME = "analyticsData.all.time";
        public static final String TIME_ZONE = "analyticsData.all.timeZone";
        public static final String LANGUAGE = "analyticsData.all.language";
        public static final String LOGIN = "analyticsData.all.userLogin";
        public static final String PAGE_NAME = "s.pageName";
        public static final String LABEL_KEY = "analyticsData.all.label";
        public static final String ALL_RATE_TAGS = "analyticsData.all.rateTags";
        public static final String ALL_DISCOUNT_CODE_APPLIED = "analyticsData.all.discountCodeApplied";
        public static final String ALL_PROMO_BOOKING_COMPLETE = "analyticsData.all.promoBookingComplete";

        public static final String SCREEN_TYPE = "analyticsData.all.screenType";
        public static final String USER_ID = "analyticsData.all.userID";
        public static final String CURRENCY = "analyticsData.all.currencyCode";

        public static final String ERROR_MESSAGE = "analyticsData.error.message";
        public static final String ERROR_CODE = "analyticsData.error.code";
        public static final String CAR_DETAILS = "analyticsData.carDetails";
        public static final String MARKETING_OPTIN = "analyticsData.emailOptin";
        public static final String PUSH_ID = "analyticsData.pushID";
        public static final String GOOGLE_ID = "analyticsData.all.gclid";
        public static final String MICROSOFT_ID = "analyticsData.all.msclkid";
        public static final String DISCOUNT_CODE = "analyticsData.search.discountCode";
        public static final String DISCOUNT_CODE_APPLIED = "analyticsData.search.discountCodeApplied";
        public static final String SEARCH_PROMO_CODE = "analyticsData.search.promoCode";
        public static final String SEARCH_RATE_TAGS = "analyticsData.search.rateTags";
        public static final String SEARCH_PROMO_NAME = "analyticsData.search.promoName";
        public static final String PROMO_NAME = "analyticsData.all.promoName";

        // CIOL
        public static final String CIOL_BOOKING_ID = "analyticsData.ciol.bookingid";
        public static final String CIOL_FLOW_KEY = "analyticsData.ciol";
        public static final String CIOL_CARD_TYPE_KEY = "analyticsData.ciol.cardType";
        public static final String CIOL_PREPAY_KEY = "analyticsData.ciol.prepay";
        public static final String CIOL_REVENUE_KEY = "analyticsData.ciol.revenue";
        public static final String CIOL_NIGHTS_CHANGE_KEY = "analyticsData.ciol.nightsChange";
        public static final String CIOL_ROOMS_CHANGE_KEY = "analyticsData.ciol.roomsChange";
        public static final String CIOL_ROOMS_TYPE_CHANGE_KEY = "analyticsData.ciol.roomTypeChange";
        public static final String CIOL_FOOD_REVENUE_CHANGE_KEY = "analyticsData.ciol.foodRevenueChange";
        public static final String CIOL_ECI_REVENUE_CHANGE_KEY = "analyticsData.ciol.eciRevenueChange";
        public static final String CIOL_LCO_REVENUE_CHANGE_KEY = "analyticsData.ciol.lcoRevenueChange";
        public static final String CIOL_WIFI_REVENUE_CHANGE_KEY = "analyticsData.ciol.wifiRevenueChange";
        public static final String CIOL_ROOM_REVENUE_CHANGE_KEY = "analyticsData.ciol.roomRevenueChange";
        public static final String CIOL_TOTAL_REVENUE_CHANGE_KEY = "analyticsData.ciol.totalRevenueChange";
        public static final String CIOL_ACTION_KEY = "analyticsData.ciol.action";
        public static final String CIOL_EXTRAS_DESCRIPTION_KEY = "analyticsData.ciol.extrasShownDescription";
        public static final String CIOL_EXTRAS_CODE_KEY = "analyticsData.ciol.extrasShownCode";
        public static final String CIOL_CHECK_IN_DATE_KEY = "analyticsData.ciol.checkinDate";
        public static final String CIOL_CHECK_IN_DAY_KEY = "analyticsData.ciol.checkinDay";
        public static final String CIOL_CHECK_OUT_DATE_KEY = "analyticsData.ciol.checkoutDate";
        public static final String CIOL_CHECK_OUT_DAY_KEY = "analyticsData.ciol.checkOutDay";
        public static final String CIOL_CHECK_IN_OUT_DAY_KEY = "analyticsData.ciol.CheckinoutDay";
        public static final String CIOL_NIGHTS_KEY = "analyticsData.ciol.nights";
        public static final String CIOL_ROOMS_KEY = "analyticsData.ciol.rooms";
        public static final String CIOL_ADULTS_KEY = "analyticsData.ciol.adults";
        public static final String CIOL_CHILDREN_KEY = "analyticsData.ciol.children";
        public static final String CIOL_HOTEL_ID_KEY = "analyticsData.ciol.hotelId";
        public static final String CIOL_RATE_CODE_KEY = "analyticsData.ciol.rateCode";
        public static final String CIOL_RATE_DESCRIPTION_KEY = "analyticsData.ciol.rateDescription";
        public static final String CIOL_RATE_NAME_KEY = "analyticsData.ciol.rateName";
        public static final String CIOL_SPECIAL_OCCASION_KEY = "analyticsData.ciol.specialOccasion";
        public static final String CIOL_ADOBE_CAMPAIGN_KEY = "s.campaign";

        public static final String THIRD_PARTY_EXPORTED = "analyticsData.all.TPbookingExported:";

        public static final String QRCODE_CID = "analyticsData.qrCode.cid";

        // Push token
        public static final String PUSH_TOKEN = "analyticsData.pushToken ";

        // Business
        public static final String BUSINESS_COMPANY_ID_KEY = "analyticsData.all.business.companyid";
        public static final String BUSINESS_USER_LEVEL_ID = "analyticsData.all.business.userlevel";
        public static final String BUSINESS_TOTAL_BOOKINGS = "analyticsData.all.business.totalBookings";
        public static final String BUSINESS_CANCELLED_BOOKINGS = "analyticsData.all.business.cancelledBookings";
        public static final String BUSINESS_FUTURE_BOOKINGS = "analyticsData.all.business.futureBookings";
        public static final String BUSINESS_STAYED_BOOKINGS = "analtyicsData.all.business.stayedBookings";
        public static final String BUSINESS_CHECKED_IN_BOOKINGS = "analyticsData.all.business.checkedinBookings";

        // Generic Booking Flow
        public static final String KEY_RATE_CODE = "analyticsData.bf.rateCode";
        public static final String KEY_LETTING_TYPE = "analyticsData.bf.lettingType";

        // App Incentive - Free Breakfast / Sitewide / Unique - Generic Promo content

        public static final String PROMO_CODE = "analyticsData.all.promoCode";
        public static final String APP_INCENTIVE = "App Incentive";
        public static final String FREE_BREAKFAST = "Free Breakfast";
        public static final String SITEWIDE_PROMOTIONS = "Sitewide Promotions";

        // Booking Details
        public static final String THIRD_PARTY_BOOKING = "analyticsData.all.TPbooking";
        public static final String THIRD_PARTY_ID = "analyticsData.all.TPbookingid";
        public static final String ANCILLARIES_BOOKED = "analyticsData.all.ancillariesBooked";


    }

    public static final class Value {
        public static final String VARIANT_DEBUG = "debug";
        public static final String VARIANT_PRODUCTION = "production";
        public static final String LOGGED_OUT = "Logged Out";
        public static final String LOGGED_IN = "Logged In";
        public static final String USER_LEISURE_TYPE = "Leisure";
        public static final String USER_BUSINESS_TYPE = "Business";
        public static final String EMPLOYEE_RATE_CODE = "Employee";
        public static final String QR_CODE_KIOSK_CHECK_IN = "QRCode_KioskCheckIn";
        public static final String USER_LEVEL_BUSINESS_BOOKER = "Business - Booker";
        public static final String WRONG_PASSWORD_MATCH = "Please make sure the passwords you’ve entered match";
        public static final String PASSWORD_UPDATED = "Password Updated";
        public static final String RESTAURANT_CLOSED = "restaurant closed for hotel";
    }

    public static final Map<String, String> ACTIVITY_SECTION_MAP;

    static {
        Map<String, String> map = new HashMap<>();

        map.put(HotelDetailsActivity.class.getCanonicalName(), Type.LOOK_TO_BOOK);
        map.put(AboutActivity.class.getCanonicalName(), Type.MY_PREMIER_INN);
        map.put(BookingDetailsActivity.class.getCanonicalName(), Type.BOOKING_FLOW);
        map.put(CalendarDialogActivity.class.getCanonicalName(), Type.LOOK_TO_BOOK);
        map.put(EditGuestActivity.class.getCanonicalName(), Type.BOOKING_FLOW);
        map.put(FindBookingActivity.class.getCanonicalName(), Type.MY_PREMIER_INN);
        map.put(FullScreenGalleryActivity.class.getCanonicalName(), Type.LOOK_TO_BOOK);
        map.put(GuestDetailsActivity.class.getCanonicalName(), Type.BOOKING_FLOW);
        map.put(LandingActivity.class.getCanonicalName(), Type.HOME);
        map.put(LoadingActivity.class.getCanonicalName(), Type.HOME);
        map.put(LoginActivity.class.getCanonicalName(), Type.MY_PREMIER_INN);
        map.put(MyBookingsActivity.class.getCanonicalName(), Type.MY_PREMIER_INN);
        map.put(PaymentBreakdownActivity.class.getCanonicalName(), Type.BOOKING_FLOW);
        map.put(PlanTripActivity.class.getCanonicalName(), Type.MY_PREMIER_INN);
        map.put(ResetPasswordActivity.class.getCanonicalName(), Type.MY_PREMIER_INN);
        map.put(ReviewBookActivity.class.getCanonicalName(), Type.BOOKING_FLOW);
        map.put(SearchActivity.class.getCanonicalName(), Type.LOOK_TO_BOOK);
        map.put(SearchResultsActivity.class.getCanonicalName(), Type.LOOK_TO_BOOK);
        map.put(SummaryActivityKt.class.getCanonicalName(), Type.BOOKING_FLOW);
        map.put(SummaryBreakdownActivity.class.getCanonicalName(), Type.BOOKING_FLOW);

        ACTIVITY_SECTION_MAP = Collections.unmodifiableMap(map);
    }
}
