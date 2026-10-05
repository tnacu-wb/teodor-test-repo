package com.whitbread.premierinn.domain.graphql.marketingPreferences.usecase

import com.whitbread.premierinn.domain.graphql.marketingPreferences.entity.UpdateMarketingPreferencesResponseDomain
import com.whitbread.premierinn.domain.graphql.marketingPreferences.repository.GraphQLMarketingPreferencesRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Single
import org.junit.Before
import org.junit.Test

class GraphQLMarketingPreferencesUseCaseTest {

    private val repository: GraphQLMarketingPreferencesRepository = mockk()
    private lateinit var useCase: GraphQLMarketingPreferencesUseCase

    @Before
    fun setup() {
        useCase = GraphQLMarketingPreferencesUseCase(repository)
    }

    @Test
    fun `WHEN UK user opts in THEN repository is called with correct parameters and returns success`() {
        testSuccessfulUpdate(
            email = TEST_EMAIL,
            optIn = true,
            doubleOptIn = false,
            countryCode = UK_COUNTRY_CODE
        )
    }

    @Test
    fun `WHEN UK user opts out THEN repository is called with optIn false and returns success`() {
        testSuccessfulUpdate(
            email = TEST_EMAIL,
            optIn = false,
            doubleOptIn = false,
            countryCode = UK_COUNTRY_CODE
        )
    }

    @Test
    fun `WHEN German user opts in THEN repository is called with doubleOptIn true and returns success`() {
        testSuccessfulUpdate(
            email = TEST_EMAIL,
            optIn = true,
            doubleOptIn = true,
            countryCode = DE_COUNTRY_CODE
        )
    }

    @Test
    fun `WHEN German user opts out THEN repository is called with optIn false and doubleOptIn true`() {
        testSuccessfulUpdate(
            email = TEST_EMAIL,
            optIn = false,
            doubleOptIn = true,
            countryCode = DE_COUNTRY_CODE
        )
    }

    @Test
    fun `WHEN repository returns error THEN error is propagated to caller`() {
        val testException = Exception("Network error")
        mockRepositoryError(testException)

        val result = useCase.updateMarketingPreferences(
            email = TEST_EMAIL,
            optIn = true,
            doubleOptIn = false,
            brandCodes = TEST_BRAND_CODES,
            isoCountryCode = UK_COUNTRY_CODE
        )

        result.test()
            .assertError { it.message == "Network error" }

        verifyRepositoryCalled(
            email = TEST_EMAIL,
            optIn = true,
            doubleOptIn = false,
            countryCode = UK_COUNTRY_CODE
        )
    }

    @Test
    fun `WHEN multiple brand codes are passed THEN repository receives all brand codes`() {
        val multipleBrands = listOf("PINN", "HUB")
        mockRepositorySuccess(
            email = TEST_EMAIL,
            optIn = true,
            doubleOptIn = false,
            brandCodes = multipleBrands,
            countryCode = UK_COUNTRY_CODE
        )

        val result = useCase.updateMarketingPreferences(
            email = TEST_EMAIL,
            optIn = true,
            doubleOptIn = false,
            brandCodes = multipleBrands,
            isoCountryCode = UK_COUNTRY_CODE
        )

        result.test()
            .assertNoErrors()
            .assertValue { it.updateMarketingPreferences == SUCCESS_RESPONSE }

        verify(exactly = 1) {
            repository.updateMarketingPreferences(
                email = TEST_EMAIL,
                optIn = true,
                doubleOptIn = false,
                brandCodes = multipleBrands,
                isoCountryCode = UK_COUNTRY_CODE
            )
        }
    }

    private fun testSuccessfulUpdate(
        email: String,
        optIn: Boolean,
        doubleOptIn: Boolean,
        countryCode: String,
        brandCodes: List<String> = TEST_BRAND_CODES
    ) {
        mockRepositorySuccess(email, optIn, doubleOptIn, brandCodes, countryCode)

        val result = useCase.updateMarketingPreferences(
            email = email,
            optIn = optIn,
            doubleOptIn = doubleOptIn,
            brandCodes = brandCodes,
            isoCountryCode = countryCode
        )

        result.test()
            .assertNoErrors()
            .assertValue { it.updateMarketingPreferences == SUCCESS_RESPONSE }

        verifyRepositoryCalled(email, optIn, doubleOptIn, brandCodes, countryCode)
    }

    private fun mockRepositorySuccess(
        email: String,
        optIn: Boolean,
        doubleOptIn: Boolean,
        brandCodes: List<String>,
        countryCode: String
    ) {
        val mockResponse = UpdateMarketingPreferencesResponseDomain(SUCCESS_RESPONSE)
        every {
            repository.updateMarketingPreferences(
                email = email,
                optIn = optIn,
                doubleOptIn = doubleOptIn,
                brandCodes = brandCodes,
                isoCountryCode = countryCode
            )
        } returns Single.just(mockResponse)
    }

    private fun mockRepositoryError(exception: Exception) {
        every {
            repository.updateMarketingPreferences(
                email = TEST_EMAIL,
                optIn = true,
                doubleOptIn = false,
                brandCodes = TEST_BRAND_CODES,
                isoCountryCode = UK_COUNTRY_CODE
            )
        } returns Single.error(exception)
    }

    private fun verifyRepositoryCalled(
        email: String,
        optIn: Boolean,
        doubleOptIn: Boolean,
        brandCodes: List<String> = TEST_BRAND_CODES,
        countryCode: String
    ) {
        verify(exactly = 1) {
            repository.updateMarketingPreferences(
                email = email,
                optIn = optIn,
                doubleOptIn = doubleOptIn,
                brandCodes = brandCodes,
                isoCountryCode = countryCode
            )
        }
    }

    companion object {
        private const val TEST_EMAIL = "test@example.com"
        private val TEST_BRAND_CODES = listOf("PINN")
        private const val UK_COUNTRY_CODE = "GB"
        private const val DE_COUNTRY_CODE = "DE"
        private const val SUCCESS_RESPONSE = "Success"
    }
}