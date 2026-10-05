package com.whitbread.premierinn.data.graphql.mapper

import com.whitbread.premierinn.data.remote.graphql.contracts.HotelAvailabilityGraphQLContract
import com.whitbread.premierinn.domain.graphql.hdp.entity.RateClassificationsDomain

fun List<HotelAvailabilityGraphQLContract.RateClassificationExtraInfo>.toListOfRateClassification(): List<RateClassificationsDomain> {
    val listOfRateClassification = mutableListOf<RateClassificationsDomain>()
    this.forEach {
        listOfRateClassification.add(
                RateClassificationsDomain(
                        rateClassification = it.rateClassification,
                        rateOrder = it.rateOrder,
                        rateName = it.rateName,
                        rateDescription = it.rateDescription,
                        rateLongDescription = it.rateLongDescription,
                        rateNotes = it.rateNotes,
                        rateTags = it.rateTags
                )
        )
    }

    return listOfRateClassification
}