package com.whitbread.premierinn.domain.customer.usecase


import com.whitbread.premierinn.businessbooker.domain.customer.repository.BusinessCustomerRepository
import com.whitbread.premierinn.domain.authentication.UnAuthorizedCustomerError
import com.whitbread.premierinn.domain.authentication.repository.AuthenticationRepository
import com.whitbread.premierinn.domain.authentication.usecase.AuthenticateCustomer
import com.whitbread.premierinn.domain.authentication.usecase.GetFreshIdTokenAndRetryOnce
import com.whitbread.premierinn.domain.authentication.usecase.IsCustomerLoggedIn
import com.whitbread.premierinn.domain.authentication.usecase.LogoutCustomer
import com.whitbread.premierinn.domain.customer.PasswordsDoNoMatchError
import com.whitbread.premierinn.domain.customer.repository.CustomerRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Single
import io.reactivex.functions.Function
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.CompletableSubject
import io.reactivex.subjects.SingleSubject
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.reactivestreams.Publisher

class UpdateCustomerPasswordTest {

    private val authenticationRepository = mockk<AuthenticationRepository>()
    private val customerRepository = mockk<CustomerRepository>()
    private val getTokenIdUseCase = mockk<GetFreshIdTokenAndRetryOnce>()
    private val logoutCustomerUseCase = mockk<LogoutCustomer>()
    private val businessCustomerRepository= mockk<BusinessCustomerRepository>()
    private val isCustomerLoggedIn = mockk<IsCustomerLoggedIn>()

    @Before
    fun setUp() {
        RxJavaPlugins.reset()
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setComputationSchedulerHandler { Schedulers.trampoline() }
        every { isCustomerLoggedIn.isLoggedInAsBusinessCustomer() } returns false
    }

    @After
    fun tearDown() {
        RxJavaPlugins.reset()
    }

    @Test
    fun updateLeisureCustomerSuccess() {
        val updateSubject = CompletableSubject.create()

        every { authenticationRepository.getIdToken() } returns Single.just("tokenId")

        every { customerRepository.updatePassword(any(), any(), any(), any()) } returns updateSubject
        every { customerRepository.getLoggedInCustomerEmail() } returns "email"
        every { customerRepository.getLoggedInCustomerPassword() } returns "pass"
        every { customerRepository.saveLoggedInCustomerPassword(any()) } returns Unit

        every { getTokenIdUseCase.invoke() } returns Function { Publisher { } }

        val useCase = UpdateCustomerPassword(
                authenticationRepository, getTokenIdUseCase, customerRepository,
                businessCustomerRepository, isCustomerLoggedIn
        ).invoke(UpdateCustomerPassword.Params("newPass", "newPass"))

        val testObserver = useCase.test()

        updateSubject.onComplete()

        testObserver
                .assertComplete()
                .assertNoErrors()

        verify { customerRepository.updatePassword(any(), any(), any(), any()) }
        verify { customerRepository.saveLoggedInCustomerPassword(any()) }
    }

    @Test
    fun updateBusinessCustomerPasswordSuccess() {
        val updateSubject = CompletableSubject.create()
        every { isCustomerLoggedIn.isLoggedInAsBusinessCustomer() } returns true
        every { authenticationRepository.getIdToken() } returns Single.just("someToken")
        every { businessCustomerRepository.updatePassword(any(), any(), any(), any()) } returns updateSubject
        every { businessCustomerRepository.getLoggedInBusinessEmail() } returns "user@business.com"
        every { businessCustomerRepository.getLoggedInBusinessPassword() } returns "password1"
        every { businessCustomerRepository.saveLoggedInBusinessPassword(any()) } returns Unit

        val useCase = UpdateCustomerPassword(
                authenticationRepository, getTokenIdUseCase, customerRepository,
                businessCustomerRepository, isCustomerLoggedIn
        ).invoke(UpdateCustomerPassword.Params("newPassword", "newPassword"))

        val testObserver = useCase.test()

        updateSubject.onComplete()

        testObserver
                .assertComplete()
                .assertNoErrors()

        verify { businessCustomerRepository.updatePassword(any(), any(), any(), any()) }
        verify { businessCustomerRepository.saveLoggedInBusinessPassword(any()) }
    }

    @Test
    fun updateFailureUnAuthorizedCustomerError_When_CredentialsDoNotExist() {
        val tokenSubject = SingleSubject.create<String>()
        val updateSubject = CompletableSubject.create()
        val authenticateCustomer = mockk<AuthenticateCustomer>()

        every { authenticationRepository.getIdToken() } returns tokenSubject
        every { customerRepository.updatePassword(any(), any(), any(), any()) } returns updateSubject
        every { customerRepository.getLoggedInCustomerEmail() } returns ""
        every { customerRepository.getLoggedInCustomerPassword() } returns ""

       every { businessCustomerRepository.getLoggedInBusinessEmail() } returns ""
       every { businessCustomerRepository.getLoggedInBusinessPassword() } returns ""

        every { customerRepository.saveLoggedInCustomerPassword(any()) } returns Unit

        val retryUseCase = GetFreshIdTokenAndRetryOnce(
            authenticateCustomer,
            logoutCustomerUseCase,
            authenticationRepository,
            customerRepository,
            businessCustomerRepository
        )

        val useCase = UpdateCustomerPassword(
            authenticationRepository,
            retryUseCase,
            customerRepository,
            businessCustomerRepository,
            isCustomerLoggedIn
        ).invoke(UpdateCustomerPassword.Params("newPass", "newPass"))

        val testObserver = useCase.test()
        tokenSubject.onError(UnAuthorizedCustomerError)

        testObserver.assertError { it is UnAuthorizedCustomerError }

        verify(exactly = 0) { customerRepository.updatePassword(any(), any(), any(), any()) }
        verify(exactly = 0) { customerRepository.saveLoggedInCustomerPassword(any()) }
    }

    @Test
    fun updateLeisureCustomerFailurePasswordsDoNotMatch() {

        val updateSubject = CompletableSubject.create()

        every { authenticationRepository.getIdToken() } returns Single.just("tokenId")

        every { customerRepository.updatePassword(any(), any(), any(), any()) } returns updateSubject
        every { customerRepository.getLoggedInCustomerEmail() } returns "email"
        every { customerRepository.getLoggedInCustomerPassword() } returns "pass"
        every { customerRepository.saveLoggedInCustomerPassword(any()) } returns Unit
        val authenticateCustomer = mockk<AuthenticateCustomer>()

        val retryUseCase = GetFreshIdTokenAndRetryOnce(authenticateCustomer, logoutCustomerUseCase, authenticationRepository,
            customerRepository, businessCustomerRepository)
        val useCase = UpdateCustomerPassword(
                authenticationRepository, retryUseCase, customerRepository,
                businessCustomerRepository, isCustomerLoggedIn
        ).invoke(UpdateCustomerPassword.Params("newPass1", "newPass"))

        val testObserver = useCase.test()
        val error = PasswordsDoNoMatchError

        testObserver.assertError { it == error }

        verify(exactly = 0) { customerRepository.updatePassword(any(), any(), any(), any()) }
        verify(exactly = 0) { customerRepository.saveLoggedInCustomerPassword(any()) }
    }

    @Test
    fun updateBusinessCustomerFailurePasswordsDoNotMatch() {

        val updateSubject = CompletableSubject.create()
        every { isCustomerLoggedIn.isLoggedInAsBusinessCustomer() } returns true
        every { businessCustomerRepository.updatePassword(any(), any(), any(), any()) } returns updateSubject
        every { businessCustomerRepository.getLoggedInBusinessEmail() } returns "user@business.com"
        every { businessCustomerRepository.getLoggedInBusinessPassword() } returns "password1"
        every { businessCustomerRepository.saveLoggedInBusinessPassword(any()) } returns Unit

        val useCase = UpdateCustomerPassword(
                authenticationRepository, getTokenIdUseCase, customerRepository,
                businessCustomerRepository, isCustomerLoggedIn
        ).invoke(UpdateCustomerPassword.Params("wrongpassword", "ouchmeouch"))

        val testObserver = useCase.test()
        val error = PasswordsDoNoMatchError

        testObserver.assertError { it == error }

        verify(exactly = 0) { businessCustomerRepository.updatePassword(any(), any(), any(), any()) }
        verify(exactly = 0) { businessCustomerRepository.saveLoggedInBusinessPassword(any()) }
    }

    @Test
    fun updateLeisurePasswordUnexpectedFailure() {
        val tokenSubject = SingleSubject.create<String>()
        val updateSubject = CompletableSubject.create()

        every { authenticationRepository.getIdToken() } returns tokenSubject
        every { customerRepository.getLoggedInCustomerEmail() } returns "email"
        every { customerRepository.getLoggedInCustomerPassword() } returns "pass"
        every { customerRepository.updatePassword("token", "email", "pass", "newPass") } returns updateSubject
        every { customerRepository.saveLoggedInCustomerPassword(any()) } returns Unit
        every { businessCustomerRepository.getLoggedInBusinessEmail() } returns ""
        every { businessCustomerRepository.getLoggedInBusinessPassword() } returns ""

        val authenticateCustomer = mockk<AuthenticateCustomer>()
        val retryUseCase = GetFreshIdTokenAndRetryOnce(
            authenticateCustomer,
            logoutCustomerUseCase,
            authenticationRepository,
            customerRepository,
            businessCustomerRepository
        )

        val useCase = UpdateCustomerPassword(
            authenticationRepository,
            retryUseCase,
            customerRepository,
            businessCustomerRepository,
            isCustomerLoggedIn
        ).invoke(UpdateCustomerPassword.Params("newPass", "newPass"))

        val testObserver = useCase.test()
        val error = Exception("Unexpected failure")
        tokenSubject.onSuccess("token")
        updateSubject.onError(error)

        testObserver.assertError { it.message == error.message }

        verify { customerRepository.updatePassword(any(), any(), any(), any()) }
        verify(exactly = 0) { customerRepository.saveLoggedInCustomerPassword(any()) }
    }

    @Test
    fun updateBusinessCustomerPasswordUnexpectedFailure() {
        val tokenSubject = SingleSubject.create<String>()
        val updateSubject = CompletableSubject.create()

        every { isCustomerLoggedIn.isLoggedInAsBusinessCustomer() } returns true
        every { authenticationRepository.getIdToken() } returns Single.just("someToken")
        every { businessCustomerRepository.updatePassword("someToken", "user@business.com", "butter", "butter2") } returns updateSubject
        every { businessCustomerRepository.getLoggedInBusinessEmail() } returns "user@business.com"
        every { businessCustomerRepository.getLoggedInBusinessPassword() } returns "butter"
        every { businessCustomerRepository.saveLoggedInBusinessPassword(any()) } returns Unit

        val useCase = UpdateCustomerPassword(authenticationRepository, getTokenIdUseCase, customerRepository,
                businessCustomerRepository, isCustomerLoggedIn)
                .invoke(UpdateCustomerPassword.Params("butter2", "butter2"))

        val testObserver = useCase.test()
        val error = Exception()
        tokenSubject.onSuccess("token")
        updateSubject.onError(error)

        testObserver.assertError { it == error }

        verify { businessCustomerRepository.updatePassword(any(), any(), any(), any()) }
        verify(exactly = 0) { businessCustomerRepository.saveLoggedInBusinessPassword(any()) }
    }
}