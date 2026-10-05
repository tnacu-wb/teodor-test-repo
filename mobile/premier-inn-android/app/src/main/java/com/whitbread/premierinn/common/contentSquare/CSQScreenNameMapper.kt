package com.whitbread.premierinn.common.contentSquare

import com.whitbread.premierinn.ciol.analytics.CiolAnalyticsData.Companion.LEAD_GUEST_DETAILS
import com.whitbread.premierinn.ciol.analytics.CiolAnalyticsData.Companion.REG_CARD_GUEST_DETAILS
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.*
import javax.inject.Inject

class CSQScreenNameMapper @Inject constructor() {

    private val screenMap = mapOf(

        // Amendment Screens
        "AmendReservationActivity" to ScreenState.AMEND_BOOKING,
        "AmendCalendarActivity" to ScreenState.AMEND_DATE,
        "AmendAddRoomActivity" to ScreenState.AMEND_ADD_ROOM,
        "AmendGuestsRoomsActivity" to ScreenState.AMEND_EDIT_ROOM,
        "AmendUpsellsActivity" to ScreenState.AMEND_MEALS,
        "ReviewAmendsActivity" to ScreenState.AMEND_REVIEW,
        "NonAmendableReservationActivity" to ScreenState.AMEND_BOOKING,
        "AmendAndPayActivity" to ScreenState.AMEND_AND_PAY,

        // Alternative Room Selection Screens
        "BathroomSelectionActivity" to ScreenState.BATHROOM_SELECTION,
        "AlternativeRoomSelectionActivity" to ScreenState.CHOOSE_TWIN_ROOM,

        //Calendar Screens
        "FrequentBookingCalendarActivity" to ScreenState.CALENDAR,
        "HomeCalendarActivity" to ScreenState.CALENDAR,
        "CalendarDialogActivity" to ScreenState.CALENDAR,

        // Check-in Online Screens (Old)
        "CheckInOnlineActivity" to ScreenState.CHECK_IN,
        "CheckOutConfirmationActivity" to ScreenState.CHECK_OUT_CONFIRMATION,

        // Misc Screens
        "BartDowntimeActivity" to ScreenState.MAINTENANCE,
        "ForceUpdateActivity" to ScreenState.FORCE_UPDATE,
        "NewsletterPreferencesActivity" to ScreenState.NEWSLETTER,
        "NotificationsActivity" to ScreenState.NOTIFICATIONS,
        "GdprComposeActivity" to ScreenState.INTERSTITIAL,
        "GdprActivity" to ScreenState.INTERSTITIAL,
        "GdprDataUseActivity" to ScreenState.HOW_DATA_USED,
        "FirstTimeDownloadComposeActivity" to ScreenState.FIRST_TIME_DOWNLOAD,

        // QR Code Screens
        "QRCodeActivity" to ScreenState.QR_CODE,
        "QRCodeInfoActivity" to ScreenState.QR_CODE_INFO,

        // Landing and Loading Screens
        "LoadingActivity" to ScreenState.LOADING,
        "LandingActivity" to ScreenState.PREMIER_INN + " - " + ScreenState.LANDING,
        "GdprComposeActivity" to ScreenState.INTERSTITIAL,
        "FirstTimeDownloadComposeActivity" to ScreenState.FIRST_TIME_DOWNLOAD_OVERLAY,

        // Booking Flow Screens
        "SearchActivity" to ScreenState.SEARCH,
        "RoomTypeSelectorActivity" to ScreenState.ROOM_DIALOG,
        "RoomCriteriaActivity" to ScreenState.CRITERIA,
        "RoomPreferencesActivity" to ScreenState.ROOM_PREFERENCES,
        "SearchResultsActivity" to ScreenState.SEARCH_RESULTS,
        "SummaryActivity" to ScreenState.SUMMARY,
        "HotelDetailsActivity" to ScreenState.HOTEL_DETAILS,
        "FullScreenGalleryActivity" to ScreenState.FULL_SCREEN_GALLERY,
        "HotelMapFullScreenActivity" to ScreenState.FULL_SCREEN_HOTEL_MAP,
        "AdditionalInformationActivity" to ScreenState.ADDITIONAL_INFO,
        "SummaryBreakdownActivity" to ScreenState.SUMMARY_EXTRAS_FULL_SUMMARY,
        "PostcodeFinderActivity" to ScreenState.POSTCODE_FINDER,
        "ReviewBookActivity" to ScreenState.REVIEW_BOOKING,
        "GuestDetailsActivity" to ScreenState.GUEST_DETAILS,
        "EditGuestActivity" to ScreenState.GUEST_DETAILS,
        "PaymentBreakdownActivity" to ScreenState.PAYMENT_DETAILS_SUMMARY,
        "ImportantHotelInfoActivity" to ScreenState.IMPORTANT_HOTEL_INFO,
        "ShortcutLocationTrampolineActivity" to ScreenState.SHORTCUT_LOCATION,

        // Payment Screens
        "ThreeCpActivity" to ScreenState.PAYMENT_3CP_IPAGE,
        "ThreeCpCustomTabActivity" to ScreenState.PAYMENT_3CP_IPAGE,
        "BusinessBookerPaymentMethodsActivity" to ScreenState.PAYMENT_METHODS,

        // My Booking Screens
        "MyBookingsActivity" to ScreenState.MY_BOOKINGS,
        "BookingDetailsActivity" to ScreenState.LEAVE_EASY_SELECTED,
        "FindBookingActivity" to ScreenState.FIND_BOOKING,
        "PlanTripActivity" to ScreenState.PLAN_YOUR_TRIP,

        // Account Section Screens
        "AccountActivity" to ScreenState.MY_ACCOUNT,
        "AboutActivity" to ScreenState.ABOUT,
        "CreateAccountActivity" to ScreenState.CREATE_ACCOUNT,
        "PersonalDetailsActivity" to ScreenState.MY_DETAILS,
        "ChangePasswordActivity" to ScreenState.CHANGE_PASSWORD,
        "ResetPasswordActivity" to ScreenState.RESET_PASSWORD,
        "MealPreferencesActivity" to ScreenState.MEAL_PREFERENCES,
        "EditPaymentMethodsActivity" to ScreenState.PAYMENT_METHODS,
        "LoginActivity" to ScreenState.LOG_IN,
        "BookingPreferencesActivity" to ScreenState.BOOKING_PREFERENCES,

        // CIOL Fragments
        "CheckInCompletionFragment" to ScreenState.CIOL_COMPLETION,
        "EditGuestDetailsFragment" to LEAD_GUEST_DETAILS,
        "PayAndCheckInFragment" to ScreenState.CIOL_PAYMENTS,
        "PreStayEditFragment" to ScreenState.EDIT_GUEST_DETAILS,
        "PreStayFragment" to ScreenState.PRE_STAY,
        "RegCardGuestDetailsFragment" to REG_CARD_GUEST_DETAILS,
        "RoomSelectionFragment" to ScreenState.CIOL_SELECT_ROOM,
        "UpsellsFragment" to ScreenState.UPSELLS,
        "RoomCriteriaFragment" to ScreenState.CIOL_ROOM_CRITERIA,

    )

    fun map(className: String?): String {
        if (className.isNullOrBlank()) return "Unknown"
        return screenMap[className] ?: formatClassName(className)
    }

    private fun formatClassName(className: String): String {
        // Remove package name and suffixes
        val simpleName = className.replace("Activity", "").replace("Fragment", "")

        // Split on capital letters and join with spaces
        return simpleName.split(Regex("(?=[A-Z])")).joinToString(" ").trim()
    }

}