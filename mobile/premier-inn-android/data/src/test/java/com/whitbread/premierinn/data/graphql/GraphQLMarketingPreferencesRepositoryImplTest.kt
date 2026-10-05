package com.whitbread.premierinn.data.graphql

import com.whitbread.premierinn.data.GsonFactory
import com.whitbread.premierinn.data.common.BRAND_CODE
import com.whitbread.premierinn.data.common.FileDataProvider
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.data.remote.GraphQLErrorBody
import com.whitbread.premierinn.data.remote.GraphQlThrowable
import com.whitbread.premierinn.data.remote.graphql.WBGraphQLServicesApi
import com.whitbread.premierinn.data.remote.graphql.contracts.UpdateMarketingPreferencesGraphQLContract
import com.whitbread.premierinn.data.utils.FileUtils
import com.whitbread.premierinn.domain.authentication.repository.AuthenticationRepository
import com.whitbread.premierinn.domain.countries.entity.CountryDomain
import com.whitbread.premierinn.domain.graphql.GraphQLServerError
import com.whitbread.premierinn.domain.graphql.marketingPreferences.entity.UpdateMarketingPreferencesResponseDomain
import com.whitbread.premierinn.domain.graphql.marketingPreferences.repository.GraphQLMarketingPreferencesRepository
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Single
import junitparams.JUnitParamsRunner
import org.json.JSONObject
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(JUnitParamsRunner::class)
class GraphQLMarketingPreferencesRepositoryImplTest {

    private val wbGraphQLServicesApi: WBGraphQLServicesApi = mockk()
    private val authenticationRepository: AuthenticationRepository = mockk()
    private val deviceLocaleProvider: DeviceLocaleProvider = mockk()
    private val fileDataProvider: FileDataProvider = mockk()
    private var graphQLMarketingPreferencesRepository: GraphQLMarketingPreferencesRepository =
        mockk()
    private var jsonObject: JSONObject = mockk(relaxed = true)
    private val jsonObjectForVariables: JSONObject = mockk(relaxed = true)

    val UPDATE_MARKETING_PREFERENCES_SUCCESS = GsonFactory.create().fromJson(
        FileUtils.loadFileFromResource("api/graphql-responses/update_marketing_preferences_success_gql.json"),
        UpdateMarketingPreferencesGraphQLContract.UpdateMarketingPreferencesData::class.java
    )

    val UPDATE_MARKETING_PREFERENCES_MUTATION = "mutation UpdateMarketingPreferences"

    @Before
    fun setUp() {
        graphQLMarketingPreferencesRepository = GraphQLMarketingPreferencesRepositoryImpl(
            wbGraphQLServicesApi,
            authenticationRepository,
            deviceLocaleProvider,
            fileDataProvider,
            jsonObject,
            jsonObjectForVariables
        )

        every { fileDataProvider.loadFileFromAssetGQL(any()) } returns UPDATE_MARKETING_PREFERENCES_MUTATION
        every { jsonObject.toString() } returns UPDATE_MARKETING_PREFERENCES_MUTATION
        every { authenticationRepository.getIdToken() } returns Single.just("mock_token")
        every { deviceLocaleProvider.getDeviceLanguage() } returns "en"
    }

    @Test
    fun `Given update marketing preferences is successful for UK then no error is thrown and mapping works`() {
        mockApiSuccess()

        testSuccessfulUpdate(
            optIn = true,
            doubleOptIn = false,
            countryCode = CountryDomain.UK_CODE
        )
    }

    @Test
    fun `Given update marketing preferences is successful for Germany then no error is thrown and mapping works`() {
        mockApiSuccess()

        testSuccessfulUpdate(
            optIn = false,
            doubleOptIn = true,
            countryCode = CountryDomain.GERMANY_ISO_CODE
        )
    }

    @Test
    fun `Given update marketing preferences contains Error then return response as GraphQL Error`() {
        every {
            wbGraphQLServicesApi.updateMarketingPreferencesGraphQL(any(), any())
        } returns Single.error(
            GraphQlThrowable.GraphQLError(500, GraphQLErrorBody(500, "Server Error"))
        )

        graphQLMarketingPreferencesRepository.updateMarketingPreferences(
            email = TEST_EMAIL,
            optIn = true,
            doubleOptIn = false,
            brandCodes = listOf(BRAND_CODE),
            isoCountryCode = CountryDomain.UK_CODE
        ).test()
            .assertError { it is GraphQLServerError }
    }

    @Test
    fun `Given update marketing preferences with opt-out for UK then no error is thrown`() {
        mockApiSuccess()

        testSuccessfulUpdate(
            optIn = false,
            doubleOptIn = false,
            countryCode = CountryDomain.UK_CODE
        )
    }

    private fun mockApiSuccess() {
        every {
            wbGraphQLServicesApi.updateMarketingPreferencesGraphQL(any(), any())
        } returns Single.just(UPDATE_MARKETING_PREFERENCES_SUCCESS)
    }

    private fun testSuccessfulUpdate(
        optIn: Boolean,
        doubleOptIn: Boolean,
        countryCode: String
    ) {
        val expectedResponse = UpdateMarketingPreferencesResponseDomain(
            updateMarketingPreferences = SUCCESS_MESSAGE
        )

        graphQLMarketingPreferencesRepository.updateMarketingPreferences(
            email = TEST_EMAIL,
            optIn = optIn,
            doubleOptIn = doubleOptIn,
            brandCodes = listOf(BRAND_CODE),
            isoCountryCode = countryCode
        ).test()
            .assertNoErrors()
            .assertValue { it == expectedResponse }
    }

    companion object {
        private const val TEST_EMAIL = "test@example.com"
        private const val SUCCESS_MESSAGE = "Success"
    }
}