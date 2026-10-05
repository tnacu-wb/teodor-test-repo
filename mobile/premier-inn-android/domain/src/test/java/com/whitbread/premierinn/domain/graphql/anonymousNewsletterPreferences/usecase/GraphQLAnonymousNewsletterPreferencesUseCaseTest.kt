package com.whitbread.premierinn.domain.graphql.anonymousNewsletterPreferences.usecase

import com.whitbread.premierinn.domain.common.COUNTRY_CODE_DE
import com.whitbread.premierinn.domain.common.COUNTRY_CODE_UK
import com.whitbread.premierinn.domain.common.LANGUAGE_DEUTSCH_DOMAIN
import com.whitbread.premierinn.domain.common.LANGUAGE_ENGLISH_DOMAIN
import com.whitbread.premierinn.domain.graphql.anonymousNewsletterPreferences.entity.AnonymousNewsletterPreferencesDomain
import com.whitbread.premierinn.domain.graphql.anonymousNewsletterPreferences.repository.GraphQLAnonymousNewsletterPreferencesRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import org.junit.After
import org.junit.Before
import org.junit.Test

class GraphQLAnonymousNewsletterPreferencesUseCaseTest {

    private val repository: GraphQLAnonymousNewsletterPreferencesRepository = mockk()
    private lateinit var useCase: GraphQLAnonymousNewsletterPreferencesUseCase
    private val testObservers = mutableListOf<TestObserver<*>>()

    companion object {
        private const val TEST_EMAIL = "test@example.com"
        private const val BRAND_CODE = "PINN"
    }

    @Before
    fun setup() {
        useCase = GraphQLAnonymousNewsletterPreferencesUseCase(repository)
    }

    @After
    fun tearDown() {
        testObservers.forEach { it.dispose() }
        testObservers.clear()
    }

    @Test
    fun `WHEN UK user with opt-in preference THEN repository is called with correct parameters and returns success`() {
        val expectedResult = AnonymousNewsletterPreferencesDomain(
            optIn = true, suppressMarketingCheckbox = false
        )

        every {
            repository.getAnonymousNewsletterPreferences(
                TEST_EMAIL,
                BRAND_CODE,
                COUNTRY_CODE_UK,
                LANGUAGE_ENGLISH_DOMAIN
            )
        } returns Single.just(expectedResult)

        val testObserver =
            useCase.getAnonymousNewsletterPreferences(
                TEST_EMAIL,
                BRAND_CODE,
                COUNTRY_CODE_UK,
                LANGUAGE_ENGLISH_DOMAIN
            )
                .test()
        testObservers.add(testObserver)

        testObserver
            .assertNoErrors()
            .assertComplete()
            .assertValue { it.optIn && !it.suppressMarketingCheckbox }

        verify(exactly = 1) {
            repository.getAnonymousNewsletterPreferences(
                TEST_EMAIL,
                BRAND_CODE,
                COUNTRY_CODE_UK,
                LANGUAGE_ENGLISH_DOMAIN
            )
        }
    }

    @Test
    fun `WHEN UK user with opt-out preference THEN repository returns opt-in false`() {
        val expectedResult = AnonymousNewsletterPreferencesDomain(
            optIn = false, suppressMarketingCheckbox = false
        )

        every {
            repository.getAnonymousNewsletterPreferences(
                TEST_EMAIL,
                BRAND_CODE,
                COUNTRY_CODE_UK,
                LANGUAGE_ENGLISH_DOMAIN
            )
        } returns Single.just(expectedResult)

        val testObserver =
            useCase.getAnonymousNewsletterPreferences(
                TEST_EMAIL,
                BRAND_CODE,
                COUNTRY_CODE_UK,
                LANGUAGE_ENGLISH_DOMAIN
            )
                .test()
        testObservers.add(testObserver)

        testObserver
            .assertNoErrors()
            .assertComplete()
            .assertValue { !it.optIn && !it.suppressMarketingCheckbox }

        verify(exactly = 1) {
            repository.getAnonymousNewsletterPreferences(
                TEST_EMAIL,
                BRAND_CODE,
                COUNTRY_CODE_UK,
                LANGUAGE_ENGLISH_DOMAIN
            )
        }
    }

    @Test
    fun `WHEN German user with marketing suppressed THEN repository returns suppressMarketingCheckbox true`() {
        val expectedResult = AnonymousNewsletterPreferencesDomain(
            optIn = false, suppressMarketingCheckbox = true
        )

        every {
            repository.getAnonymousNewsletterPreferences(
                TEST_EMAIL,
                BRAND_CODE,
                COUNTRY_CODE_DE,
                LANGUAGE_DEUTSCH_DOMAIN
            )
        } returns Single.just(expectedResult)

        val testObserver =
            useCase.getAnonymousNewsletterPreferences(
                TEST_EMAIL,
                BRAND_CODE,
                COUNTRY_CODE_DE,
                LANGUAGE_DEUTSCH_DOMAIN
            )
                .test()
        testObservers.add(testObserver)

        testObserver
            .assertNoErrors()
            .assertComplete()
            .assertValue { !it.optIn && it.suppressMarketingCheckbox }

        verify(exactly = 1) {
            repository.getAnonymousNewsletterPreferences(
                TEST_EMAIL, BRAND_CODE, COUNTRY_CODE_DE,
                LANGUAGE_DEUTSCH_DOMAIN
            )
        }
    }

    @Test
    fun `WHEN repository call fails THEN error is propagated`() {
        val exception = RuntimeException("Network error")

        every {
            repository.getAnonymousNewsletterPreferences(
                TEST_EMAIL,
                BRAND_CODE,
                COUNTRY_CODE_UK,
                LANGUAGE_ENGLISH_DOMAIN
            )
        } returns Single.error(exception)

        val testObserver =
            useCase.getAnonymousNewsletterPreferences(
                TEST_EMAIL,
                BRAND_CODE,
                COUNTRY_CODE_UK,
                LANGUAGE_ENGLISH_DOMAIN
            )
                .test()
        testObservers.add(testObserver)

        testObserver
            .assertError { it is RuntimeException && it.message == "Network error" }

        verify(exactly = 1) {
            repository.getAnonymousNewsletterPreferences(
                TEST_EMAIL, BRAND_CODE,
                COUNTRY_CODE_UK, LANGUAGE_ENGLISH_DOMAIN
            )
        }
    }
}