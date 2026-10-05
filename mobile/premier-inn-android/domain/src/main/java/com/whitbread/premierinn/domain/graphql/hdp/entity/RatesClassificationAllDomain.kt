package com.whitbread.premierinn.domain.graphql.hdp.entity

data class RateClassificationsDomain(
    val rateClassification: String,
    val rateOrder: String,
    val rateName: String,
    val rateDescription: String,
    val rateLongDescription: String,
    val rateNotes: String,
    val rateTags: List<String>
)
