package uk.co.whitbread.integrationtests.testkit.featureflags

/** OHIP adapter feature flags a scenario can override for the duration of one request. */
enum class OhipFeatureFlag(
    override val key: String,
) : FeatureFlag {
    AVAILABILITY_FROM_DIFFERENT_ROOM_CLASSES("release_availability_from_different_room_classes"),
    BB_FLEX_RATE_STRIKETHROUGH("release_bb_flex_rate_strikethrough"),

    /**
     * Switches billing-address profile selection to the request's update indicators when the
     * channel is BB, and marks every written billing address as the profile's primary address.
     *
     * Read through the override-aware Unleash wrapper on the request thread at both of its
     * evaluation points, so request baggage reaches both states — the endpoint's only real
     * ON/OFF axis.
     */
    CAPTURE_BILLING_ADDRESS_BB("release_bb_capture_billing_address"),
    DEPOSIT_FOLIO_POST_AFTER_DISABLE_ON_HOLD("release_deposit_folio_post_after_disable_on_hold"),
    DISTRIBUTION_BOOKING_FEE("release_distr_booking_fee"),
    MOBILE_ACCEPTS_OTA_BOOKING("mobile_accepts_ota_booking"),
    MOBILE_PRE_REGISTERED_REPURPOSE("mobile_preRegistered_repurpose"),
    SET_CNP_BOOKING_ALERTS("release_set_cnp_booking_alerts"),
    SET_DEFAULT_PAYMENT_METHOD_DS("release_set_default_payment_method_DS"),
    USE_TOKEN_SERVICE("release_ohip_use_token_service"),

    /**
     * Applies the configured token refresh clock skew to directly acquired Opera OAuth tokens.
     *
     * Evaluated once, when ohip-adapter-service creates its direct OAuth authorized-client
     * provider bean, so request baggage can never change it: the integration environment fixes
     * it false. The entry exists so a scenario whose flow lists the flag can state that fixed
     * invariant explicitly in its flag-pin map; it is never an ON/OFF scenario axis.
     */
    USE_TOKEN_REFRESH_SKEW("release_ohip_use_token_refresh_skew"),
}
