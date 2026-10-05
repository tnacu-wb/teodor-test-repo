package com.whitbread.premierinn.domain.graphql.promotions.usecase

import com.whitbread.premierinn.domain.graphql.promotions.entity.PromotionsInformationDomain
import com.whitbread.premierinn.domain.graphql.promotions.repository.GraphQLPromotionsInformationRepository
import io.reactivex.Single
import javax.inject.Inject

class GraphQLPromotionsInformationUseCase @Inject constructor(
    private val graphQLPromotionsInformationRepository: GraphQLPromotionsInformationRepository
) {
    fun execute(
        country: String,
        language: String,
        channel: String,
        brand: String,
        stayStartDate: String,
        stayEndDate: String,
        basketReference: String? = null,
        promotionCode: String? = null,
        isPromoBox: Boolean? = null
    ): Single<PromotionsInformationDomain> {
        return graphQLPromotionsInformationRepository.getPromotionsInformation(
            country,
            language,
            channel,
            brand,
            stayStartDate,
            stayEndDate,
            basketReference,
            promotionCode,
            isPromoBox
        )
    }
}