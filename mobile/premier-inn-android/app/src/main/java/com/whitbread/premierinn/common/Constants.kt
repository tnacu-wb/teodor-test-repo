@file:JvmName("Constants")

package com.whitbread.premierinn.common

import okhttp3.internal.immutableListOf

// Landing Activity
const val UPCOMING_BOOKING = "UPCOMING_BOOKING"
const val FREQUENT_BOOKINGS = "FREQUENT_BOOKINGS"
const val RECENT_SEARCHES = "RECENT_SEARCHES"
const val PAST_SEARCHES = "PAST_SEARCHES"

const val EXTRA_THREE_CP_INPUT = "EXTRA_THREE_CP_INPUT"
const val EXTRA_THREE_CP_INPUT_AMEND = "EXTRA_THREE_CP_INPUT_AMEND"
const val EXTRA_THREE_CP_INPUT_SAVE_CARD = "EXTRA_THREE_CP_INPUT_SAVE_CARD"
const val EXTRA_THREE_CP_INPUT_PAY_AND_CHECK_IN = "EXTRA_THREE_CP_INPUT_PAY_AND_CHECK_IN"
const val EXTRA_THREE_CP_INPUT_AUTHORIZE_CARD = "EXTRA_THREE_CP_INPUT_AUTHORIZE_CARD"
const val EXTRA_THREE_CP_INPUT_PAY_AND_CHECK_IN_G_PAY = "EXTRA_THREE_CP_INPUT_PAY_AND_CHECK_IN_G_PAY"
const val EXTRA_THREE_C_P = "EXTRA_THREE_C_P"
const val EXTRA_CARD_TYPE = "EXTRA_CARD_TYPE"
const val EXTRA_TRANSACTION_ID = "TRANSACTION_ID"
const val PAYMENT_SUCCESS = "Payment Successful"
const val EXTRA_THREE_C_P_AMEND_RESULT = "EXTRA_THREE_C_P_AMEND_RESULT"
const val POLLING_FINISHED = "Polling finished"

// Hotels without donation
val HOTELS_WITHOUT_DONATION = immutableListOf("PRELEA", "WARWAT", "WAKHOL", "WALBEN", "WIRROY", "WIGPRI",
        "CAEBAR", "SHRBRI", "MANOLD", "PORPOR", "DUBAIR", "BOUWES", "MANAIR", "STOTRE", "BIRDUC", "LISBAR", "HINMAR", "WESHOB",
        "IPSSWA", "SKIANC", "PORRED", "ORMMOR", "GODMAN", "TORBEL", "TWIFOU", "COVCRO", "SOUTAY", "MANTRA", "LONCIT", "LLAPEM",
        "HELMAY", "MARGEO", "SUNCIT", "PORCEN", "LONTOT")

// Hotel Payment Providers
enum class PaymentProvider(val storedProvider: String) {
    THREE_C_P("3CP"); //TODO: Check if 3DS


    companion object {
        fun from(paymentProvider: String): PaymentProvider? = values().find { it.storedProvider == paymentProvider }
    }
}
//DEEP LINK
const val DEEP_LINK_CIOL_KEY = "ciol"
const val DEEP_LINK_REFERENCE_KEY  = "bookingRef"
const val DEEP_LINK_ARRIVAL_DATE_KEY  = "arrival"
const val DEEP_LINK_SURNAME_KEY  = "surname"
const val DEEP_LINK_PUSH_CAMPAIGN_KEY  = "pushcampaign"

//Default Dropdown Nationalities
const val UK = "GB"
const val GERMANY = "DE"
const val USA = "US"
const val UNITED_ARAB_EMIRATES = "AE"

const val OPERA = "OPERA"

//PAYMENT POLLING
const val POLLING_DELAY_3CP = 3
const val POLLING_INTERVAL_3CP = 3
const val POLLING_RETRIES_3CP = 3

//Opera Upsell
const val NO_PREFERENCE = "NO_PREFERENCE"
const val ULTIMATE_WIFI_24_HRS = "FI24HR"
const val ULTIMATE_WIFI_7_DAYS = "FI7DAY"

// Opera Basket status polling
const val CONFIRMATION_POLLING_DELAY = 1
const val CONFIRMATION_POLLING_INTERVAL = 1

// Account Type for OPERA Countries
const val BUSINESS_BOOKER = "business-booker"
const val LEISURE = "leisure"

//Payment Methods for analytics
const val GOOGLE_PAY = "GP"

//Hotel Details
const val PARKING_COP_CODE = "COP"
const val PARKING_CPP_CODE = "CPP"
const val PARKING_CPF_CODE = "CPF"
const val RESTAURANT_RES_CODE = "RES"
const val RESTAURANT_HRS_CODE = "HRS"
const val RESTAURANT_ZBF_CODE = "ZBF"
const val WIFI_WIA_CODE = "WIA"
const val WIFI_HUW_CODE = "HUW"
const val AIRCO_ACO_CODE = "ACO"
const val AIRCO_HAC_CODE = "HAC"
const val LIFT_LFT_CODE = "LFT"
const val LIFT_HUL_CODE = "HUL"
const val RESTAURANT = "Restaurant"
const val BREAKFAST = "Breakfast"
const val AVAILABLE = "available"
const val MAX_NR_OF_FACILITIES = 3

//Room variant constants
const val RESULT_BUSINESS_LOGIN_SUCCESS = 1001

// CITYTAX
const val CITY_TAX = "CITYTAX"
val ECI_LCO = listOf(
    "HSCKIN", "HSCOU2"
)