package uk.co.whitbread.integrationtests.testkit.featureflags

/**
 * Hotel Reservation Entity feature flags a scenario can override for one inbound request.
 *
 * Overrides apply only to flag reads on the request thread. A wrong key is a silent no-op, so each
 * key must match the service's Unleash configuration exactly.
 */
enum class HotelReservationFeatureFlag(
    override val key: String,
) : FeatureFlag {
    AEM_SEARCH_RULES("release_pi_bb_ccui_aem_search_rules"),
    AMEND_DISTRIBUTION_SINGLE_CALL("release_amend_distribution_single_call"),
    APPLY_OCCUPANCY_SUPPLEMENT("release_pi_ccui_distr_web3_occupancy_supplement"),
    CITY_TAX_UK("release_pi_ccui_city_tax_uk"),
    MAX_ROOMS_AMEND("release_pi_bb_ccui_maxrooms_amend"),
    MOBILE_PRE_REGISTERED_REPURPOSE("mobile_preRegistered_repurpose"),
    PI_SEARCH_BY_OPERA_CONFIRMATION("release_pi_search_opera_conf_number"),
}
