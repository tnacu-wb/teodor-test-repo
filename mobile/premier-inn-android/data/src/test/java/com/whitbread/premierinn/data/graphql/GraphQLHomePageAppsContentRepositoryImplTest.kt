package com.whitbread.premierinn.data.graphql

import com.whitbread.premierinn.data.GsonFactory
import com.whitbread.premierinn.data.common.FileDataProvider
import com.whitbread.premierinn.data.graphql.mapper.toHomePageAppsContentDomain
import com.whitbread.premierinn.data.remote.GraphQLErrorBody
import com.whitbread.premierinn.data.remote.GraphQlThrowable
import com.whitbread.premierinn.data.remote.graphql.WBGraphQLServicesApi
import com.whitbread.premierinn.data.remote.graphql.contracts.HomePageAppsContentGraphQLContract
import com.whitbread.premierinn.data.utils.FileUtils
import com.whitbread.premierinn.domain.graphql.GraphQLServerError
import com.whitbread.premierinn.domain.graphql.requestBodyModels.HomePageContentRequestBody
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Single
import junitparams.JUnitParamsRunner
import org.json.JSONObject
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(JUnitParamsRunner::class)
class GraphQLHomePageAppsContentRepositoryImplTest {
    private val graphQlApi: WBGraphQLServicesApi = mockk()
    private val fileDataProvider: FileDataProvider = mockk()
    private val jsonObject: JSONObject = mockk(relaxed = true)
    private lateinit var graphQLHomePageAppsContentRepository: GraphQLHomePageAppsContentRepositoryImpl

    private val homePageContent: HomePageAppsContentGraphQLContract.HomePageAppsContentData =
        GsonFactory.create().fromJson(
            FileUtils.loadFileFromResource("api/graphql-responses/home_page_apps_content_success_gql.json"),
            HomePageAppsContentGraphQLContract.HomePageAppsContentData::class.java
        )

    private val requestBody = HomePageContentRequestBody(
        channel = "PI",
        subChannel = "apps",
        language = "en",
        country = "gb"
    )

    @Before
    fun setup() {
        graphQLHomePageAppsContentRepository = GraphQLHomePageAppsContentRepositoryImpl(
            graphQlApi,
            fileDataProvider,
            jsonObject
        )

        val query = "home page apps content query"
        every { fileDataProvider.loadFileFromAssetGQL(any<String>()) } returns query
    }

    @Test
    fun `getHomePageAppsContent is successful when api call is successful`() {
        //GIVEN
        every { graphQlApi.getHomePageAppsContent(any()) } returns Single.just(homePageContent)

        val expectedResult = homePageContent.toHomePageAppsContentDomain()

        //WHEN
        val contentResult = graphQLHomePageAppsContentRepository.getHomePageAppsContent(requestBody)

        //THEN
        contentResult.test()
            .assertNoErrors()
            .assertValue {
                it.contentCards == expectedResult.contentCards
            }
            .assertValue {
                it.destinationCards == expectedResult.destinationCards
            }
            .assertValue {
                it.promoCards == expectedResult.promoCards
            }
            .assertValue {
                it.logo == expectedResult.logo
            }
            .assertValue {
                it.heading == expectedResult.heading
            }
            .assertValue {
                it.notification == expectedResult.notification
            }
    }

    @Test
    fun `getHomePageAppsContent api call fails and throws error`() {
        //GIVEN
        every { graphQlApi.getHomePageAppsContent(any()) } returns Single.error(
            GraphQlThrowable.GraphQLError(500, GraphQLErrorBody(500, "Server Error"))
        )

        //WHEN
        val contentResult = graphQLHomePageAppsContentRepository.getHomePageAppsContent(requestBody)

        //THEN
        contentResult.test().assertError { it is GraphQLServerError }
    }
}
