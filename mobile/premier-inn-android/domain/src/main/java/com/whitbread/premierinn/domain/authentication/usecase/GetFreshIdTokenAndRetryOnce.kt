package com.whitbread.premierinn.domain.authentication.usecase

import com.whitbread.premierinn.businessbooker.domain.customer.repository.BusinessCustomerRepository
import com.whitbread.premierinn.domain.authentication.AuthenticationError
import com.whitbread.premierinn.domain.authentication.NoLongerValidCredentials
import com.whitbread.premierinn.domain.authentication.UnAuthorizedCustomerError
import com.whitbread.premierinn.domain.authentication.UserType
import com.whitbread.premierinn.domain.authentication.repository.AuthenticationRepository
import com.whitbread.premierinn.domain.customer.repository.CustomerRepository
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.functions.Function
import org.reactivestreams.Publisher
import javax.inject.Inject

class GetFreshIdTokenAndRetryOnce @Inject constructor(
    private val authenticateCustomer: AuthenticateCustomer,
    private val logoutCustomer: LogoutCustomer,
    private val authenticationRepository: AuthenticationRepository,
    private val customerRepository: CustomerRepository,
    private val businessCustomerRepository: BusinessCustomerRepository
) {
    operator fun invoke(): Function<Flowable<Throwable>, Publisher<Any>> {
        return Function { errorFlowable ->
            var retryCount = 0
            errorFlowable.flatMap { error ->
                if (isCustomerLoggedInSessionExpired(error) && retryCount < 1) {
                    retryCount++
                    val authFlow = if (businessCustomerRepository.getLoggedInBusinessEmail().isNotEmpty()) {
                        authenticateBusinessCustomerSilently()
                    } else {
                        authenticateCustomerSilently()
                    }

                    authFlow
                        .onAuthenticationErrorForceLogoutIfNeeded()
                        .andThen(authenticationRepository.getIdToken())
                        .toFlowable()
                } else {
                    Flowable.error(error)
                }
            }
        }
    }

    private fun authenticateCustomerSilently(): Completable {
        return authenticateCustomer(
            AuthenticateCustomer.Params(
                customerRepository.getLoggedInCustomerEmail(),
                customerRepository.getLoggedInCustomerPassword(),
                UserType.LEISURE
            )
        )
    }

    private fun authenticateBusinessCustomerSilently(): Completable {
        return authenticateCustomer(
            AuthenticateCustomer.Params(
                businessCustomerRepository.getLoggedInBusinessEmail(),
                businessCustomerRepository.getLoggedInBusinessPassword(),
                UserType.BUSINESS
            )
        )
    }

    private fun isCustomerLoggedInSessionExpired(e: Throwable): Boolean {
        val leisureLoggedIn = customerRepository.getLoggedInCustomerEmail().isNotEmpty() &&
                customerRepository.getLoggedInCustomerPassword().isNotEmpty()
        val businessLoggedIn = businessCustomerRepository.getLoggedInBusinessEmail().isNotEmpty() &&
                businessCustomerRepository.getLoggedInBusinessPassword().isNotEmpty()

        val result = e is UnAuthorizedCustomerError && leisureLoggedIn.xor(businessLoggedIn)
        return result
    }

    private fun Completable.onAuthenticationErrorForceLogoutIfNeeded(): Completable {
        return this.onErrorResumeNext { e ->
            if (e is AuthenticationError && e.errorType != AuthenticationError.Type.NETWORK) {
                logoutCustomer().andThen(Completable.error(NoLongerValidCredentials(e)))
            } else {
                Completable.error(e)
            }
        }
    }
}