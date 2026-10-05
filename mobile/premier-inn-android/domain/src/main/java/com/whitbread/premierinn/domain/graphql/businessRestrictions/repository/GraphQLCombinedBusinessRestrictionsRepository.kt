package com.whitbread.premierinn.domain.graphql.businessRestrictions.repository

import com.whitbread.premierinn.domain.graphql.businessRestrictions.entity.CombinedBusinessRestrictionsDomain
import io.reactivex.Single

interface GraphQLCombinedBusinessRestrictionsRepository {
    fun getCombinedBusinessRestrictions(): Single<CombinedBusinessRestrictionsDomain>
}