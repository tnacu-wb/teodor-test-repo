package com.whitbread.premierinn.domain.customer.usecase

import com.whitbread.premierinn.domain.authentication.repository.AuthenticationRepository
import com.whitbread.premierinn.domain.authentication.repository.IdToken
import com.whitbread.premierinn.domain.common.CustomerFixtureDomain
import com.whitbread.premierinn.domain.customer.entity.Customer
import com.whitbread.premierinn.domain.customer.repository.CustomerRepository
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.IOException
import java.util.*

class CreateCustomerMarketingPreferencesTest {

    private val customerRepository = mockk<CustomerRepository>()
    private val authenticationRepository = mockk<AuthenticationRepository>()
    private lateinit var customerMarketingPreferences: CreateCustomerMarketingPreferences
    private val testIdToken: IdToken = "test-jwt-token"

    @Before
    fun setUp() {
        RxJavaPlugins.reset()
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setComputationSchedulerHandler { Schedulers.trampoline() }
        every { authenticationRepository.getIdToken() } returns Single.just(testIdToken)
        customerMarketingPreferences = CreateCustomerMarketingPreferences(customerRepository, authenticationRepository)
    }

    @After
    fun tearDown() {
        RxJavaPlugins.reset()
    }

    @Test
    fun `GIVEN logged in user WHEN marketOptIn true THEN marketing preferences updated with JWT token`() {

        val aCustomer: Customer = CustomerFixtureDomain.aCustomer()
        every { customerRepository.createCustomerMarketingPrefs(testIdToken, aCustomer, true,
            Locale.UK.language) } returns Completable.complete()

        customerMarketingPreferences.execute(aCustomer, true,
            Locale.UK.language).test().assertNoErrors()
                .assertComplete()
    }

    @Test
    fun `GIVEN logged in user WHEN marketOptIn false THEN marketing preferences updated with JWT token`() {

        val aCustomer: Customer = CustomerFixtureDomain.aCustomer()
        every { customerRepository.createCustomerMarketingPrefs(testIdToken, aCustomer, false,
            Locale.UK.language) } returns
                Completable.complete()

        customerMarketingPreferences.execute(aCustomer, false,
            Locale.UK.language).test().assertNoErrors()
                .assertComplete()
    }

    @Test
    fun `GIVEN logged in user WHEN marketing API errors THEN error handled gracefully and Completable completes`() {

        val aCustomer: Customer = CustomerFixtureDomain.aCustomer()
        every { customerRepository.createCustomerMarketingPrefs(testIdToken, aCustomer, false,
            Locale.UK.language) } returns
                Completable.error(IOException("EXCEPTION"))

        customerMarketingPreferences.execute(aCustomer, false,
            Locale.UK.language).test()
                .assertComplete()
    }
}