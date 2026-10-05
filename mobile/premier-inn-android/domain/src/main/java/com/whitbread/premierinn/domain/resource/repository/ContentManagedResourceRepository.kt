package com.whitbread.premierinn.domain.resource.repository

import io.reactivex.Completable
import io.reactivex.Single

// Abstraction of Firebase RemoteConfig
interface ContentManagedResourceRepository {
    fun sync(): Completable
    fun getString(key: String): String
    fun getStringSingle(key: String): Single<String>
    fun getBoolean(key: String): Boolean
    fun getLong(key: String): Long

    enum class Key(val value: String) {
        MINIMUM_SUPPORTED_VERSION("force_update_minimum_supported_version"),
        FORCE_UPDATE_TITLE("force_update_title"),
        FORCE_UPDATE_DESCRIPTION("force_update_description"),
        BART_DOWN("feature_bartDown"),
        BART_DOWNTIME_INFO("bartDowntimeInfo"),

        TOP_DESTINATIONS("top_destinations"),
        CUSTOMER_SERVICE_NUMBER("telephone_number"),
        GROUP_BOOKINGS_NUMBER("telephone_group_number"),
        CALL_CUSTOMER_SERVICE_DESC("telephone_message"),
        CHARGEABLE_PHONE_DESC("telephone_cost"),
        NON_CHARGEABLE_PHONE_DESC("telephone_no_cost"),
        MY_ACCOUNT_LINKS("my_account_links"),

        // TODO Rate names and descriptions to be removed when are guaranteed from MS Hotel availability
        RATE_FLEX_NAME("rate_name_flex"),
        RATE_FLEX_DESC("rate_description_flex"),
        RATE_SAVER_NAME("rate_name_saver"),
        RATE_SAVER_DESC("rate_description_saver"),
        RATE_SEMI_FLEX_NAME("rate_name_semi_flex"),
        RATE_SEMI_FLEX_DESC("rate_description_semi_flex"),
        RATE_CONTENT("rate_content"),
        RATE_OVERRIDE_STRINGS("override_strings"),

        BOOKING_PRIVACY_FOOTER("booking_privacy_footer"),

        GDPR_PRIVACY_MESSAGE("privacy_interstitial_message"),
        GDPR_PRIVACY_FOOTER("privacy_generic_footer"),
        GDPR_MY_DETAILS_USAGE("my_details_usage_box"),
        GDPR_DATA_USAGE("data_usage_message"),
        GDPR_PAYMENT_DETAILS_DATA_USAGE("payments_details_usage_box"),
        GDPR_GUEST_DETAILS_PRIVACY("sharing_guest_details_privacy_message"),

        FEATURE_COVID_NOTIFICATION_MESSAGE("notifications_message"),

        FEATURE_COVID_SRP_AND_HDP("feature_coronavirus_messaging_srp_hdp"),
        FEATURE_COVID_HOME("feature_coronavirus_messaging_homepage"),
        COVID_BANNER_MESSAGE("coronavirus_banner_messaging"),

        PASSWORD_VALIDATOR("password_validator"),

        CHECK_IN_CHECK_OUT_TIME("checkin_checkout_info_android"),
        ALL_CHECK_IN_CHECK_OUT_TIMES("all_check_in_times_android"),

        FEATURE_CIOL("feature_CIOL"),
        FEATURE_CIOL_UPSELLS("feature_CIOLUpsells"),

        TWIN_ROOM_INFO("twin_room_info"),

        FEATURE_DONATION("feature_donation"),
        DONATION_PLEDGE("donation_pledge"),
        DONATION_DETAILS("donation_details"),

        HOTELS_WITHOUT_DONATIONS("hotels_without_donations"),
        DONATIONS("donations"),
        DONATION_TITLE_OPERA("donation_title_opera"),

        IPAGE_COMPLETE_TRIGGER("ipage_complete_trigger"),
        IPAGE_LOADED_TRIGGER("ipage_loaded_trigger"),
        IPAGE_URL_PAYMENT_TEXT("ipage_url_payment_string"),
        IPAGE_3DS_NOTIFICATION_TEXT("ipage_3ds_notification_string"),
        IPAGE_3DS_COMPLETE_STRING("ipage_3ds_complete_string"),

        //PAYMENT ERROR KEYS
        BOOKING_PAYMENT_DECLINED("booking_payment_declined"),
        BOOKING_PAYMENT_NOT_FOUND("booking_payment_not_found"),
        GENERAL_PAYMENT_ERROR("general_payment_error"),
        PAYMENT_AMOUNT_CONFLICT("payment_amount_conflict"),
        TRANSACTIONAL_REFUND_ERROR("transactional_refund_error"),
        PAYMENT_FRAUD_CHECK("payment_fraud_check"),

        //PAYMENT POLLING
        POLLING_DELAY_3CP("polling_delay_3cp"),
        POLLING_INTERVAL_3CP("polling_interval_3cp"),
        POLLING_RETRIES_3CP("polling_retries_3cp"),

        // OPERA FALLBACK KEYS
        SHOULD_OPERA_REDIRECT_TO_WEB("feature_should_opera_redirect_to_web"),
        OPERA_FALLBACK_POPUP_DETAILS("opera_fallback_popup_details"),

        // OPERA BASKET STATUS POLLING
        CONFIRMATION_POLLING_DELAY("confirmation_polling_delay"),
        CONFIRMATION_POLLING_INTERVAL("confirmation_polling_interval"),
        CONFIRMATION_POLLING_MESSAGES_CONFIG("confirmation_polling_messages_config"),

        // SRP Opera
        FEATURE_USE_GRAPHQL_AVAILABILITIES("feature_use_graphQL_availabilities"), //TODO: BART DECOM: Remove once Landing View Model & ShortcutLocationTrampolinePresenter have been cleaned up

        // PAYPAL
        USE_PAYPAL_INITIATE_PAYMENT_MUTATION("feature_use_paypal_initiate_payment_mutation"),
        FEATURE_PAYPAL("feature_paypal"),

        /**
         * Business booker key
         * Feature toggle to activate or deactivate business booker feature
         */
        FEATURE_BUSINESS_BOOKER_QA("feature_business_booker_qa"),

        // Amend Banner
        FEATURE_SHOULD_SHOW_AMEND_BANNER("feature_should_show_amend_banner"),
        AMEND_OPERA_BOOKING_INFO("amend_opera_booking_info"),

        // Amend Banner Business
        FEATURE_SHOULD_SHOW_AMEND_BANNER_BUSINESS("feature_should_show_amend_banner_business"),
        AMEND_OPERA_BOOKING_INFO_BUSINESS("amend_opera_booking_info_business"),

        //GOOGLE PAY
        FEATURE_GOOGLE_PAY("feature_googlePay"),

        // QR Kiosk Hotels
        QR_KIOSK_HOTELS("qr_kiosk_hotels"),

        // DeepLink
        FEATURE_DEEPLINK_HDP("feature_deeplink_HDP"),
        FEATURE_DEEPLINK_SRP("feature_deeplink_SRP"),
        FEATURE_DEEPLINK_CIOL("feature_deeplink_CIOL"),

        //ECI_LCO
        FEATURE_ALLOW_ECI_LCO("feature_allow_ECI_LCO"),
        ECI_LCO_BANNER_MESSAGE("eci_lco_banner_message"),

        //SAVED_CARD
        FEATURE_ADD_NEW_CARD("feature_add_new_card"),

        // Employee offer
        FEATURE_ALLOW_EMPLOYEE_OFFER("feature_allow_employee_offer"),

        // ContentSquare masking
        FEATURE_CONTENT_SQUARE_UNMASK("feature_contentsquare_unmask"),

        // App Promotional Incentive
        FEATURE_IS_APP_PROMOTIONAL_INCENTIVE_ENABLED("feature_is_app_promotional_incentive_enabled"),
        APP_PROMO_CONTENT("app_promo_content"),
        APP_PROMO_DISCOUNT_TAG("app_promo_discount_tag"),
        APP_PROMO_CODE("app_promo_promocode"),

        // Free Breakfast Promotion
        FREE_BREAKFAST_PROMO_CONTENT("free_breakfast_promo_content"),
        FREE_BREAKFAST_PROMO_TAG("free_breakfast_promo_tag"),

        // Discount Box
        FEATURE_SHOW_SINGLE_USE_DISCOUNT_BOX_HDP("feature_show_single_use_discount_box_HDP"),
        HDP_DISCOUNT_BOX_ALLOWED_BRANDS("hdp_discount_box_allowed_brands"),

        //PIBA CIOL
        FEATURE_PIBA_CP_ENABLED("feature_piba_cp_enabled")
    }
}