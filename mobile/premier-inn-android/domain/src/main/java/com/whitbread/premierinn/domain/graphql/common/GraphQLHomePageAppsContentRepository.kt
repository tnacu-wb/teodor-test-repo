package com.whitbread.premierinn.domain.graphql.common

import com.whitbread.premierinn.domain.graphql.common.entity.HomePageAppsContentDomain
import com.whitbread.premierinn.domain.graphql.requestBodyModels.HomePageContentRequestBody
import io.reactivex.Single

interface GraphQLHomePageAppsContentRepository {
    fun getHomePageAppsContent(homePageContentRequestBody: HomePageContentRequestBody): Single<HomePageAppsContentDomain>
}
