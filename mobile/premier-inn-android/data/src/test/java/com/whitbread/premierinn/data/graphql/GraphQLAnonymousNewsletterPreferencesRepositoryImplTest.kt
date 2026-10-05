package com.whitbread.premierinn.data.graphql

import com.whitbread.premierinn.data.GsonFactory
import com.whitbread.premierinn.data.common.FileDataProvider
import com.whitbread.premierinn.data.remote.GraphQLErrorBody
import com.whitbread.premierinn.data.remote.GraphQlThrowable
import com.whitbread.premierinn.data.remote.graphql.WBGraphQLServicesApi
import com.whitbread.premierinn.data.remote.graphql.contracts.AnonymousNewsletterPreferencesGraphQLContract
import com.whitbread.premierinn.data.utils.FileUtils
import com.whitbread.premierinn.domain.common.COUNTRY_CODE_DE
import com.whitbread.premierinn.domain.common.COUNTRY_CODE_UK
import com.whitbread.premierinn.domain.common.LANGUAGE_DEUTSCH_DOMAIN
import com.whitbread.premierinn.domain.common.LANGUAGE_ENGLISH_DOMAIN
import com.whitbread.premierinn.domain.graphql.GraphQLServerError
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import org.json.JSONObject
import org.junit.After
import org.junit.Before
import org.junit.Test

class GraphQLAnonymousNewsletterPreferencesRepositoryImplTest {

    private val wbGraphQLServicesApi: WBGraphQLServicesApi = mockk()
    private val fileDataProvider: FileDataProvider = mockk()
    private lateinit var repository: GraphQLAnonymousNewsletterPreferencesRepositoryImpl
    private var queryJsonObject: JSONObject = mockk(relaxed = true)
    private val jsonObjectForVariables: JSONObject = mockk(relaxed = true)
    private val testObservers = mutableListOf<TestObserver<*>>()

    companion object {
        private const val TEST_EMAIL = "test@example.com"
        private const val BRAND_CODE = "PINN"
    }

    private val anonymousNewsletterPreferencesSuccess = GsonFactory.create().fromJson(
        FileUtils.loadFileFromResource("api/graphql-responses/anonymous_newsletter_preferences_success_gql.json"),
        AnonymousNewsletterPreferencesGraphQLContract.AnonymousNewsletterPreferencesData::class.java
    )

    private val queryString = "Anonymous newsletter preferences query string"

    @Before
    fun setUp() {
        repository = GraphQLAnonymousNewsletterPreferencesRepositoryImpl(
            wbGraphQLServicesApi,
            fileDataProvider,
            queryJsonObject,
            jsonObjectForVariables
        )
        every { fileDataProvider.loadFileFromAssetGQL(any()) } returns queryString
        every { queryJsonObject.toString() } returns queryString
    }

    @After
    fun tearDown() {
        testObservers.forEach { it.dispose() }
        testObservers.clear()
    }

    @Test
    fun `Given anonymous newsletter preferences query is successful then no error is thrown and mapping works`() {
        every { wbGraphQLServicesApi.getAnonymousNewsletterPreferencesGraphQL(any()) } returns Single.just(
            anonymousNewsletterPreferencesSuccess
        )

        val testObserver = repository.getAnonymousNewsletterPreferences(
            email = TEST_EMAIL,
            brandCode = BRAND_CODE,
            countryOfResidence = COUNTRY_CODE_UK,
            language = LANGUAGE_ENGLISH_DOMAIN
        ).test()
        testObservers.add(testObserver)

        testObserver
            .assertNoErrors()
            .assertComplete()
            .assertValue { result ->
                result.optIn == (anonymousNewsletterPreferencesSuccess.data?.anonymousNewsletterPreferences?.optIn
                    ?: false) &&
                        result.suppressMarketingCheckbox == (anonymousNewsletterPreferencesSuccess.data?.anonymousNewsletterPreferences?.suppressMarketingCheckbox
                    ?: false)
            }
    }

    @Test
    fun `Given anonymous newsletter preferences query with opt-in true then returns correct value`() {
        val mockResponse =
            AnonymousNewsletterPreferencesGraphQLContract.AnonymousNewsletterPreferencesData(
                data = AnonymousNewsletterPreferencesGraphQLContract.Data(
                    anonymousNewsletterPreferences = AnonymousNewsletterPreferencesGraphQLContract.AnonymousNewsletterPreferences(
                        optIn = true,
                        suppressMarketingCheckbox = false
                    )
                ),
                errors = null
            )

        every { wbGraphQLServicesApi.getAnonymousNewsletterPreferencesGraphQL(any()) } returns Single.just(
            mockResponse
        )

        val testObserver = repository.getAnonymousNewsletterPreferences(
            email = TEST_EMAIL,
            brandCode = BRAND_CODE,
            countryOfResidence = COUNTRY_CODE_UK,
            language = LANGUAGE_ENGLISH_DOMAIN
        ).test()
        testObservers.add(testObserver)

        testObserver
            .assertNoErrors()
            .assertComplete()
            .assertValue { it.optIn && !it.suppressMarketingCheckbox }
    }

    @Test
    fun `Given anonymous newsletter preferences query with suppress checkbox true then returns correct value`() {
        val mockResponse =
            AnonymousNewsletterPreferencesGraphQLContract.AnonymousNewsletterPreferencesData(
                data = AnonymousNewsletterPreferencesGraphQLContract.Data(
                    anonymousNewsletterPreferences = AnonymousNewsletterPreferencesGraphQLContract.AnonymousNewsletterPreferences(
                        optIn = false,
                        suppressMarketingCheckbox = true
                    )
                ),
                errors = null
            )

        every { wbGraphQLServicesApi.getAnonymousNewsletterPreferencesGraphQL(any()) } returns Single.just(
            mockResponse
        )

        val testObserver = repository.getAnonymousNewsletterPreferences(
            email = TEST_EMAIL,
            brandCode = BRAND_CODE,
            countryOfResidence = COUNTRY_CODE_DE,
            language = LANGUAGE_DEUTSCH_DOMAIN
        ).test()
        testObservers.add(testObserver)

        testObserver
            .assertNoErrors()
            .assertComplete()
            .assertValue { !it.optIn && it.suppressMarketingCheckbox }
    }

    @Test
    fun `Given anonymous newsletter preferences query contains Error then return response as GraphQL Error`() {
        every { wbGraphQLServicesApi.getAnonymousNewsletterPreferencesGraphQL(any()) } returns Single.error(
            GraphQlThrowable.GraphQLError(500, GraphQLErrorBody(500, "Server Error"))
        )

        val testObserver = repository.getAnonymousNewsletterPreferences(
            email = TEST_EMAIL,
            brandCode = BRAND_CODE,
            countryOfResidence = COUNTRY_CODE_UK,
            language = LANGUAGE_ENGLISH_DOMAIN
        ).test()
        testObservers.add(testObserver)

        testObserver.assertError { it is GraphQLServerError }
    }

    @Test
    fun `Given anonymous newsletter preferences query with null data then returns default values`() {
        val mockResponse =
            AnonymousNewsletterPreferencesGraphQLContract.AnonymousNewsletterPreferencesData(
                data = null,
                errors = null
            )

        every { wbGraphQLServicesApi.getAnonymousNewsletterPreferencesGraphQL(any()) } returns Single.just(
            mockResponse
        )

        val testObserver = repository.getAnonymousNewsletterPreferences(
            email = TEST_EMAIL,
            brandCode = BRAND_CODE,
            countryOfResidence = COUNTRY_CODE_UK,
            language = LANGUAGE_ENGLISH_DOMAIN
        ).test()
        testObservers.add(testObserver)

        testObserver
            .assertNoErrors()
            .assertComplete()
            .assertValue { !it.optIn && !it.suppressMarketingCheckbox }
    }

    @Test
    fun `Given anonymous newsletter preferences query with network error then returns error`() {
        every { wbGraphQLServicesApi.getAnonymousNewsletterPreferencesGraphQL(any()) } returns Single.error(
            RuntimeException("Network error")
        )

        val testObserver = repository.getAnonymousNewsletterPreferences(
            email = TEST_EMAIL,
            brandCode = BRAND_CODE,
            countryOfResidence = COUNTRY_CODE_UK,
            language = LANGUAGE_ENGLISH_DOMAIN
        ).test()
        testObservers.add(testObserver)

        testObserver.assertError { it is RuntimeException && it.message == "Network error" }
    }
}