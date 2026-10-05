package com.whitbread.premierinn.data.graphql

import com.whitbread.premierinn.data.common.FileDataProvider
import com.whitbread.premierinn.data.common.onGraphQLError
import com.whitbread.premierinn.data.graphql.mapper.mapToCombinedBusinessRestrictionsGQL
import com.whitbread.premierinn.data.remote.graphql.WBGraphQLServicesApi
import com.whitbread.premierinn.domain.graphql.businessRestrictions.entity.CombinedBusinessRestrictionsDomain
import com.whitbread.premierinn.domain.graphql.businessRestrictions.repository.GraphQLCombinedBusinessRestrictionsRepository
import io.reactivex.Single
import org.json.JSONObject

class GraphQLCombinedBusinessRestrictionsRepositoryImpl(
    private val fileDataProvider: FileDataProvider,
    private val jsonObject: JSONObject,
    private val wbGraphQLServicesApi: WBGraphQLServicesApi
) : GraphQLCombinedBusinessRestrictionsRepository {
    override fun getCombinedBusinessRestrictions(): Single<CombinedBusinessRestrictionsDomain> {
        val query =
            fileDataProvider.loadFileFromAssetGQL("graphql/CombinedBusinessRestrictionsQueryGQL.txt")
        jsonObject.put("query", query)
        return wbGraphQLServicesApi.getCombinedBusinessRestrictionsGraphQL(query = jsonObject.toString())
            .onGraphQLError()
            .map { response ->
                response.data?.mapToCombinedBusinessRestrictionsGQL()
                    ?: throw IllegalStateException("Invalid combined business restrictions response")
            }
            .doOnError { throwable ->
                println("GraphQl not able to connect: " + throwable.localizedMessage)
            }
    }
}