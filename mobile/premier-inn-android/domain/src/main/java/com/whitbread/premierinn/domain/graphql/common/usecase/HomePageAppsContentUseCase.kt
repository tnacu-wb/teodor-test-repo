package com.whitbread.premierinn.domain.graphql.common.usecase

import com.whitbread.premierinn.domain.graphql.common.GraphQLHomePageAppsContentRepository
import com.whitbread.premierinn.domain.graphql.requestBodyModels.HomePageContentRequestBody
import javax.inject.Inject


class HomePageAppsContentUseCase @Inject constructor(
    private val graphQLHomePageAppsContentRepository: GraphQLHomePageAppsContentRepository
) {

    operator fun invoke(homePageContentRequestBody: HomePageContentRequestBody) =
        graphQLHomePageAppsContentRepository.getHomePageAppsContent(homePageContentRequestBody)
}
