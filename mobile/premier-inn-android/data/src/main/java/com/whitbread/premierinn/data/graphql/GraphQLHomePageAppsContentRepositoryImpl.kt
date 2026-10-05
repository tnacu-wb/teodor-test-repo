package com.whitbread.premierinn.data.graphql

import com.whitbread.premierinn.data.common.FileDataProvider
import com.whitbread.premierinn.data.common.onGraphQLError
import com.whitbread.premierinn.data.graphql.mapper.toHomePageAppsContentDomain
import com.whitbread.premierinn.data.remote.graphql.WBGraphQLServicesApi
import com.whitbread.premierinn.domain.graphql.common.GraphQLHomePageAppsContentRepository
import com.whitbread.premierinn.domain.graphql.common.entity.HomePageAppsContentDomain
import com.whitbread.premierinn.domain.graphql.requestBodyModels.HomePageContentRequestBody
import io.reactivex.Single
import org.json.JSONObject
import javax.inject.Inject

class GraphQLHomePageAppsContentRepositoryImpl @Inject constructor(
    private val wbGraphQLServicesApi: WBGraphQLServicesApi,
    private val fileDataProvider: FileDataProvider,
    private val jsonObject: JSONObject
) : GraphQLHomePageAppsContentRepository {

    override fun getHomePageAppsContent(
        homePageContentRequestBody: HomePageContentRequestBody
    ): Single<HomePageAppsContentDomain> {
        jsonObject.apply {
            put(
                "query",
                fileDataProvider.loadFileFromAssetGQL("graphql/HomepageAppsContentQueryGQL.txt")
            )
            with(homePageContentRequestBody) {
                put("variables", JSONObject().apply {
                    put(CHANNEL, channel)
                    put(SUB_CHANNEL, subChannel)
                    put(LANGUAGE, language)
                    put(COUNTRY, country)
                })
            }
        }

        return wbGraphQLServicesApi.getHomePageAppsContent(jsonObject.toString())
            .onGraphQLError()
            .map { response ->
                return@map response.toHomePageAppsContentDomain()
            }.doOnError { throwable ->
                println("GraphQl not able to connect: " + throwable.localizedMessage)
            }
    }

    companion object {
        const val CHANNEL = "channel"
        const val SUB_CHANNEL = "subchannel"
        const val LANGUAGE = "language"
        const val COUNTRY = "country"
    }
}
