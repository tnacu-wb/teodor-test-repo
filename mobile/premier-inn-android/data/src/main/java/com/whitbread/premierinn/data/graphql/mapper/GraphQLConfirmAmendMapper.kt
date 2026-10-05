package com.whitbread.premierinn.data.graphql.mapper

import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.data.remote.graphql.contracts.ConfirmAmendGraphQLContract
import com.whitbread.premierinn.domain.graphql.amend.entity.AmendPaymentRequiredDetailsDomain
import com.whitbread.premierinn.domain.graphql.amend.entity.ConfirmAmendLogicDomain
import com.whitbread.premierinn.domain.graphql.common.GraphQLErrorDomain

fun ConfirmAmendGraphQLContract.ConfirmAmendData.mapToConfirmAmendLogicGQL(): ConfirmAmendLogicDomain {
    val listOfErrors = mutableListOf<GraphQLErrorDomain>()
    this.errors?.let {
        it.forEach { error ->
            listOfErrors.add(
                GraphQLErrorDomain(
                path = error.path ?: emptyList(),
                errorType = error.errorType,
                message = error.message
            )
            )
        }
    }

    return ConfirmAmendLogicDomain(
        status = this.data?.confirmAmendLogic?.payment?.status ?: EMPTY_STRING,
        paymentRequiredDetailsDomain = this.data?.confirmAmendLogic?.payment?.paymentRequiredDetails?.toDomain(),
        error = listOfErrors
    )
}

fun ConfirmAmendGraphQLContract.AmendPaymentRequiredDetails.toDomain() : AmendPaymentRequiredDetailsDomain {
    return AmendPaymentRequiredDetailsDomain(this.paymentRedirect, this.sessionId)
}