package com.whitbread.premierinn.domain.graphql.srp.entity

import com.whitbread.premierinn.domain.graphql.promotions.entity.PromotionsInformationDomain

/**
 * Holds both hotel availabilities and promotions information for SRP screen.
 */
data class HotelAvailabilitiesWithPromotionsDomain(
    val availabilities: HotelAvailabilitiesDomain,
    val promotions: PromotionsInformationDomain?
)