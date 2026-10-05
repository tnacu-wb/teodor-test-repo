package com.whitbread.premierinn.domain.graphql.promotions.repository

import com.whitbread.premierinn.domain.graphql.promotions.entity.PromotionsInformationDomain
import io.reactivex.Single

interface GraphQLPromotionsInformationRepository {
    fun getPromotionsInformation(
        country: String,
        language: String,
        channel: String,
        brand: String,
        stayStartDate: String,
        stayEndDate: String,
        basketReference: String? = null,
        promotionCode: String? = null,
        isPromoBox: Boolean? = null
    ): Single<PromotionsInformationDomain>
}