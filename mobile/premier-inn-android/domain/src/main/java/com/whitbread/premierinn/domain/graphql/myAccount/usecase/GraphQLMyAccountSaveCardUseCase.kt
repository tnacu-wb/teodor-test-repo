package com.whitbread.premierinn.domain.graphql.myAccount.usecase

import com.whitbread.premierinn.domain.graphql.myAccount.entity.SaveCardDomain
import com.whitbread.premierinn.domain.graphql.myAccount.repository.GraphQLMyAccountSaveCardRepository
import com.whitbread.premierinn.domain.graphql.requestBodyModels.SaveCardRequestBody
import io.reactivex.Single
import javax.inject.Inject

class GraphQLMyAccountSaveCardUseCase @Inject constructor(private val graphQLMyAccountSaveCardRepository: GraphQLMyAccountSaveCardRepository) {

    fun saveCard(token: String, saveCardRequestBody: SaveCardRequestBody): Single<SaveCardDomain> {
        return graphQLMyAccountSaveCardRepository.saveCard(token, saveCardRequestBody)
    }
}