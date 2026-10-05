package com.whitbread.premierinn.domain.customer.usecase

import com.whitbread.premierinn.domain.common.CustomerFixtureDomain
import com.whitbread.premierinn.domain.customer.entity.Customer
import com.whitbread.premierinn.domain.customer.repository.CustomerRepository
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Completable
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.IOException
import java.util.*

class CreateCustomerTest {

    private val customerRepository = mockk<CustomerRepository>()
    private lateinit var createCustomer: CreateCustomer
    private val aCustomer: Customer = CustomerFixtureDomain.aCustomer()

    @Before
    fun setUp() {
        RxJavaPlugins.reset()
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setComputationSchedulerHandler { Schedulers.trampoline() }

        createCustomer = CreateCustomer(customerRepository)
    }

    @After
    fun tearDown() {
        RxJavaPlugins.reset()
    }

    @Test
    fun `when create customer THEN Completable is returned`() {

        every { customerRepository.createCustomer(aCustomer, "PASSWORD", Locale.UK.language) } returns Completable.complete()

        createCustomer.invoke(CreateCustomer.Params(
                aCustomer,
                "PASSWORD",
                Locale.UK.language
        )).test().assertComplete().assertNoErrors()
    }

    @Test
    fun `when create customer errors THEN Completable error is returned`() {

        every { customerRepository.createCustomer(aCustomer, "PASSWORD", Locale.UK.language) } returns Completable
                .error(IOException("ERROR"))

        createCustomer.invoke(CreateCustomer.Params(
                aCustomer,
                "PASSWORD",
                Locale.UK.language
        )).test().assertError(IOException::class.java)
    }

    @Test
    fun `when create customer completes successfully THEN Completable is returned`() {

        every { customerRepository.createCustomer(aCustomer, "PASSWORD", Locale.UK.language) } returns Completable.complete()

        createCustomer.invoke(CreateCustomer.Params(
                aCustomer,
                "PASSWORD",
                Locale.UK.language
        )).test().assertComplete()
    }
}