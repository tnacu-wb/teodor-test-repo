package com.whitbread.premierinn.domain.graphql.myAccount.repository

import com.whitbread.premierinn.domain.graphql.myAccount.entity.SaveCardDomain
import com.whitbread.premierinn.domain.graphql.requestBodyModels.SaveCardRequestBody
import io.reactivex.Single

interface GraphQLMyAccountSaveCardRepository {

    fun saveCard(token: String, input: SaveCardRequestBody): Single<SaveCardDomain>

}