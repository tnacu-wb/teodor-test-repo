package com.whitbread.premierinn.data.graphql.mapper

import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.data.remote.graphql.contracts.AuthorizeCardGraphQLContract
import com.whitbread.premierinn.domain.graphql.ciol.entity.AuthorizeCardDomain

fun AuthorizeCardGraphQLContract.AuthorizeCard.toAuthorizeCardDomain(): AuthorizeCardDomain {
    return AuthorizeCardDomain(
        paymentRedirect = this.paymentRedirect ?: EMPTY_STRING,
        template = this.template ?: EMPTY_STRING,
        sessionId = this.sessionId ?: EMPTY_STRING,
        providerUrl = this.providerUrl ?: EMPTY_STRING
    )
}
