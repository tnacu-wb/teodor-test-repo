package com.whitbread.premierinn.data.graphql.mapper

import com.whitbread.premierinn.data.remote.graphql.contracts.RatesInformationGraphQLContract
import com.whitbread.premierinn.domain.graphql.hdp.entity.RateClassificationsDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.RatesInformationDomain

fun RatesInformationGraphQLContract.RatesInformationData.mapToRatesInformationGQL() : RatesInformationDomain {
    return RatesInformationDomain(
        listOfRatesClassification = this.toRatesClassification()
    )
}

fun RatesInformationGraphQLContract.RatesInformationData.toRatesClassification(): List<RateClassificationsDomain> {
    val listOfRateClassification = mutableListOf<RateClassificationsDomain>()
    this.data?.ratesInformationV2?.rateClassifications?.forEach {
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
