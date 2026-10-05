//
//  FirebaseConstants.swift
//  PremierInn
//
//  Created by Freddie Parks on 10/12/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Foundation

enum FirebaseConstants {
    static var googleServiceInfo: String {
        #if DEV
        return "GoogleService-Info-Debug"
        #else
        return "GoogleService-Info"
        #endif
    }

    private static let fetchFrequencyThresholdMinutes = 60
    private static let fetchRetryFrequencyThresholdMinutes = 12

    static let fetchTime = (FirebaseConstants.fetchFrequencyThresholdMinutes * 60)
    static let fetchTimeRetry = (FirebaseConstants.fetchRetryFrequencyThresholdMinutes * 60)
    static let fetchTimeForce = 0

    enum cloudMessaging {
        #if DEV
        static let configTopic = "REMOTE_CONFIG_PURGE_IOS_STAGE"
        #else
        static let configTopic = "REMOTE_CONFIG_PURGE_IOS_PROD"
        #endif
    }

    enum notificationKeys {
        static let configState = "CONFIG_STATE"
    }

    enum localStorageKeys {
        static let fetchExpirationDuration = "FETCH_EXPIRATION_DURATION"
        static let configStale = "CONFIG_STALE"
    }

    enum remoteValueKeys {
        static let unsupportedVersions = "force_update_unsupported_versions"
        static let minimumSupportedVersion = "force_update_minimum_supported_version"
        static let forceUpdateTitle = "force_update_title"
        static let forceUpdateDescription = "force_update_description"
        static let cmsStrings = "override_strings"
        static let testPush = "feature_test_push"
        static let versioning = "microservices_versioning"
        static let notificationsMessageFeature = "feature_notifications_message"
        static let notificationsMessage = "notifications_message"
        static let myAccountLinks = "my_account_links"
        static let coronavirusMessagingSRPAndHDP = "feature_coronavirus_messaging_srp_hdp"
        static let coronavirusMessagingHomepage = "feature_coronavirus_messaging_homepage"
        static let coronavirusBannerMessage = "coronavirus_banner_messaging"
        static let rateContent = "rate_content"
        static let passwordRegexs = "password_validator"
        static let twinRoomInfo = "twin_room_info"
        static let bartDown = "feature_bartDown"
        static let shouldOperaRedirectToWeb = "feature_should_opera_redirect_to_web"
        static let shouldOperaShowFallBackForBB = "feature_should_bb_show_opera_fallback"
        static let shouldShowAmendBanner = "feature_should_show_amend_banner"
        static let employeeQuestionsOperaFeature = "feature_employee_questions_opera"
        static let allowEmployeeOfferFeature = "feature_allow_employee_offer"
        static let featureShouldShowDashboardEcommerceContent = "feature_should_show_dashboard_ecommerce_content"

        // endpoints migration
        static let featureUseOperaRestProdEndpoint = "feature_use_opera_rest_prod_endpoint"
        static let featureUseOperaRestLowerEnvironmentEndpoint = "feature_use_opera_rest_lower_environment_endpoint"
        static let featureUseSnowdropOperaLowerEnvEndpoint = "feature_use_snowdrop_opera_lower_environment_endpoint"
        static let featureUseSnowdropOperaProdEndpoint = "feature_use_snowdrop_opera_prod_endpoint"

        static let orderAndPay = "feature_orderAndPay"
        static let getKey = "feature_getKey"

        // confirmation/basket polling
        static let confirmationPollingMessagesConfig = "confirmation_polling_messages_config"
        static let confirmationPollingDelay = "confirmation_polling_delay"
        static let confirmationPollingInterval = "confirmation_polling_interval"

        // PayPal
        static let featurePayPal = "feature_paypal"
        static let featureUsePaypalInitiatePayment = "feature_use_paypal_initiate_payment"

        // ApplePay
        static let featureApplePay = "feature_applepay"

        static let kioskHotels = "qr_kiosk_hotels"
        static let snpWifiSites = "snp_wifi_hotels"

        // Deeplinking
        static let featureDeeplinkSRP = "feature_deeplink_SRP"
        static let featureDeeplinkHDP = "feature_deeplink_HDP"
        static let featureDeeplinkCIOL = "feature_deeplink_CIOL"
        static let featureDeeplinkHomepage = "feature_deeplink_homepage"

        /// CIOL - this feature flag will enable the posibility to start check in
        /// Controls wheter the user can start check-in online via CTAs/deeplink/push
        static let featureCIOL = "feature_CIOL"

        /// Feature flag to display the upsells in the CIOL flow
        static let featureCIOLUpsells = "feature_CIOLUpsells"

        // Early check-in, Late check-out
        static let featureAllowEciLco = "feature_allow_ECI_LCO"

        static let featureAppleWalletPass = "feature_apple_wallet_pass"

        // Dashboard
        static let featureShowDashboard = "feature_should_show_dashboard"

        static let featureAddNewCard = "feature_add_new_card"
        static let featureDonations = "feature_donation"
        static let featureContentsquareUnmask = "feature_contentsquare_unmask"

        static let featureDigitalKeys = "feature_digital_keys"
        static let featureAppIncentive = "feature_is_app_promotional_incentive_enabled"

        static let featureHDPDiscountCode = "feature_show_single_use_discount_box_HDP"
        static let hdpDiscountCodeAllowedBrands = "hdp_discount_box_allowed_brands"

        static let featureThirdPartyPrepaid = "feature_third_party_prepaid"
        static let featurePIBACPEnabled = "feature_piba_cp_enabled"
    }

    enum remoteValueDefaults {
        static let unsupportedVersions: [String] = ["2.9"]
        static let minimumSupportedVersion = "3.6"
        static let forceUpdateTitle = PILocalizedString("Your app needs an upgrade")
        static let forceUpdateDescription = PILocalizedString("appUpdateRequiredFeaturesAndSecurityMessage")
        static let cmsStrings: [String: String] = [:]
        static let testPush = "ios_a"
        static let versioning: [String: String] = [:]
        static let notificationsMessageFeature = "false"
        static let notificationsMessage: [String: Any] = [
            "notificationTitle": "An Announcement",
            "notificationBody": "We may send you announcements from time to time.",
            "notificationCTA": "Continue",
            "notificationID": 1
        ]
        static let myAccountLinks: [PIDictionary] = []
        static let coronavirusMessagingSRPAndHDP = false
        static let coronavirusMessagingHomepage = false
        static let coronavirusBannerMessage = PILocalizedString("coronavirusBannerMessaging")
        static let rateContent: [PIDictionary] = []
        static let passwordRegex: [String: String] = [:]
        static let twinRoomInfo: [PIDictionary] = []
        static let bartDown = false
        static let orderAndPay = false
        static let getKey = false
        static let employeeQuestionsOperaFeature = "true"
        static let featurePayPal = "true"
        static let featureUsePaypalInitiatePayment = "true"
        static let applePayFeature = "true"
        static let featureUseOperaRestProdEndpoint = "false"
        static let featureUseOperaRestLowerEnvironmentEndpoint = "false"
        static let featureUseSnowdropOperaLowerEnvEndpoint = "false"
        static let featureUseSnowdropOperaProdEndpoint = "false"
        static let allowEmployeeOfferFeature = "true"
        static let featureAllowEciLco = "false"
        static let featureAppleWalletPass = "true"
        static let featureShowDashboard = "true"
        static let featureShouldShowDashboardEcommerceContent = "false"
        static let featureAddNewCard = "true"
        static let featureDigitalKeys = "false"
        static let featureAppIncentive = "true"
        static let featureHDPDiscountCode = "false"
        static let featurePIBACPEnabled = "false"
    }
}
