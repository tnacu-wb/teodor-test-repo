package com.whitbread.premierinn.data.graphql.mapper

import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.data.remote.graphql.contracts.MyAccountSaveCardGraphQLContract
import com.whitbread.premierinn.domain.graphql.myAccount.entity.SaveCardDomain

fun MyAccountSaveCardGraphQLContract.SaveCardGraphQLContractData.mapToSaveCardDomainGQL(): SaveCardDomain {
    return SaveCardDomain(
        paymentRedirect = this.data.saveCard.paymentRedirect
        )
}