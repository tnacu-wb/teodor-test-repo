package com.whitbread.premierinn.domain.authentication.usecase

import com.whitbread.premierinn.businessbooker.domain.customer.repository.BusinessCustomerRepository
import com.whitbread.premierinn.domain.authentication.UserType
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import com.whitbread.premierinn.domain.authentication.repository.AuthenticationRepository
import com.whitbread.premierinn.domain.customer.repository.CustomerRepository
import io.reactivex.Completable
import org.junit.Test

class AuthenticateCustomerTest {

    @Test
    fun `successful leisure customer log in`() {

        val authenticateRepository: AuthenticationRepository = mock {
            on { authenticate(any(), any(), any()) } doReturn Completable.complete()
        }
        val customerRepository: CustomerRepository = mock()

        val businessRepository: BusinessCustomerRepository = mock()

        val authenticate = AuthenticateCustomer(authenticateRepository, customerRepository, businessRepository)

        authenticate.invoke(AuthenticateCustomer.Params("user", "pass", UserType.LEISURE))
                .test()
                .assertNoErrors()

        verify(customerRepository).saveLoggedInCustomerEmail(any())
        verify(customerRepository).saveLoggedInCustomerPassword(any())
    }

    @Test
    fun `failed leisure customer log in`() {

        val authenticateRepository: AuthenticationRepository = mock {
            on { authenticate(any(), any(), any()) } doReturn Completable.error(Exception())
        }
        val customerRepository: CustomerRepository = mock()

        val businessRepository: BusinessCustomerRepository = mock()

        val authenticate = AuthenticateCustomer(authenticateRepository, customerRepository, businessRepository)

        authenticate.invoke(AuthenticateCustomer.Params("user", "pass", UserType.LEISURE))
                .test()
                .assertError { it is Exception }

        verify(customerRepository, never()).saveLoggedInCustomerEmail(any())
        verify(customerRepository, never()).saveLoggedInCustomerPassword(any())
    }

    @Test
    fun `successful business customer log in`() {

        val authenticateRepository: AuthenticationRepository = mock {
            on { authenticate(any(), any(), any()) } doReturn Completable.complete()
        }
        val customerRepository: CustomerRepository = mock()

        val businessRepository: BusinessCustomerRepository = mock()

        val authenticate = AuthenticateCustomer(authenticateRepository, customerRepository, businessRepository)

        authenticate.invoke(AuthenticateCustomer.Params("user", "pass", UserType.BUSINESS))
            .test()
            .assertNoErrors()

        verify(businessRepository).saveLoggedInBusinessEmail(any())
        verify(businessRepository).saveLoggedInBusinessPassword(any())
    }

    @Test
    fun `failed business customer log in`() {

        val authenticateRepository: AuthenticationRepository = mock {
            on { authenticate(any(), any(), any()) } doReturn Completable.error(Exception())
        }
        val customerRepository: CustomerRepository = mock()

        val businessRepository: BusinessCustomerRepository = mock()

        val authenticate = AuthenticateCustomer(authenticateRepository, customerRepository, businessRepository)

        authenticate.invoke(AuthenticateCustomer.Params("user", "pass", UserType.BUSINESS))
            .test()
            .assertError { it is Exception }

        verify(businessRepository, never()).saveLoggedInBusinessEmail(any())
        verify(businessRepository, never()).saveLoggedInBusinessPassword(any())
    }
}