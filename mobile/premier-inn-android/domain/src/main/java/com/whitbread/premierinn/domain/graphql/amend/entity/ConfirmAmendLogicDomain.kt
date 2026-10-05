package com.whitbread.premierinn.domain.graphql.amend.entity

import com.whitbread.premierinn.domain.graphql.common.GraphQLErrorDomain

data class ConfirmAmendLogicDomain(
    val status: String,
    val paymentRequiredDetailsDomain: AmendPaymentRequiredDetailsDomain?,
    val error: List<GraphQLErrorDomain>
)

data class AmendPaymentRequiredDetailsDomain(
    val paymentRedirectDomain: String,
    val sessionId: String
)
    
