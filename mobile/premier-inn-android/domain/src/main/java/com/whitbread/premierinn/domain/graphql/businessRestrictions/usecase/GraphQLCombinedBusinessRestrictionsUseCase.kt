package com.whitbread.premierinn.domain.graphql.businessRestrictions.usecase

import com.whitbread.premierinn.domain.graphql.businessRestrictions.entity.CombinedBusinessRestrictionsDomain
import com.whitbread.premierinn.domain.graphql.businessRestrictions.repository.GraphQLCombinedBusinessRestrictionsRepository
import io.reactivex.Single
import javax.inject.Inject

class GraphQLCombinedBusinessRestrictionsUseCase @Inject constructor(
    private val graphQLCombinedBusinessRestrictionsRepository: GraphQLCombinedBusinessRestrictionsRepository
) {
    fun execute(): Single<CombinedBusinessRestrictionsDomain> {
        return graphQLCombinedBusinessRestrictionsRepository.getCombinedBusinessRestrictions()
    }
}