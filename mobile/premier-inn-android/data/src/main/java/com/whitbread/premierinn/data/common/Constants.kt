@file:JvmName("Constants")

package com.whitbread.premierinn.data.common
// TODO: Move into common module https://whitbreadis.atlassian.net/browse/DNRQ-17701
// Note: check :app Urls.java for duplicate values -

const val BUSINESS_BOOKING_CHANNEL = "CBT"
const val HOTEL_BRAND = "PI"
const val WILDCARD_CHARACTER = "*"

const val BRAND_CODE = "PINN"
const val EMAIL = "Email"

// Bookings
const val NOT_AVAILABLE = "N/A"

// Address
const val ADDRESS_TYPE_HOME = "HOME"
const val ADDRESS_TYPE_BUSINESS = "BUSINESS"

const val EMPTY_STRING = ""
const val COUNTRY_GERMANY = "Germany"

const val PAYMENT_CARD_TYPE = "CARD"
const val PAYMENT_PIBA_TYPE = "PIBA"

// Hotel Brands
const val BRAND_HUB = "HUB"
const val BRAND_PI = "PI"
const val BRAND_ZIP = "ZIP"
const val BRAND_PI_GERMANY = "PID"
const val BB = "BB"


const val LANGUAGE_ENGLISH = "en"
const val LANGUAGE_DEUTSCH = "de"

const val UPSELL_FORMAT = "PH"

// Default Business Rules
const val DEFAULT_MAX_NIGHTS_LEISURE = 9
const val DEFAULT_MAX_ROOMS_LEISURE = 4
const val MAX_ROOMS_ERROR_THRESHOLD = 9
const val DEFAULT_MAX_ROOMS_AMEND = 4
const val DEFAULT_MAX_ARRIVAL_DATE = 364
const val WIFI_UPSELL_ALLOWED_ID = "135"

// Default Business Rules InnBusiness
const val DEFAULT_MAX_NIGHTS_INN_BUSINESS = 9
const val DEFAULT_MAX_ROOMS_INN_BUSINESS = 1


const val CONTENT_BASE_URL = "https://www.premierinn.com"

// GQL environment url to access for Auth0 Repo
const val GRAPHQL_DEV_URL = "https://api.dev.premierinn.digital"
const val GRAPHQL_DIT_URL  = "https://api.dit.premierinn.digital"
const val GRAPHQL_SIT_URL  = "https://api.sit.premierinn.digital"
const val GRAPHQL_DEMO_URL  = "https://api.demo.premierinn.digital"
const val GRAPHQL_PRE_PROD_URL  = "https://api.preprod.premierinn.digital"
const val GRAPHQL_PERF_URL = "https://api.perf.premierinn.digital"
const val GRAPHQL_UAT_URL = "https://api.uat.premierinn.digital"
