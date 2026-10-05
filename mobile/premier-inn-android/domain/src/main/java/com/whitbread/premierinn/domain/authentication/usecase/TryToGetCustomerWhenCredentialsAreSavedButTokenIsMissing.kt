package com.whitbread.premierinn.domain.authentication.usecase

import com.whitbread.premierinn.domain.authentication.repository.AuthenticationRepository
import com.whitbread.premierinn.domain.customer.repository.CustomerRepository
import com.whitbread.premierinn.domain.customer.usecase.GetCustomer
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

// TODO remove this after POST AUTH0 migration - This covers an one off UseCase.
// It is just to re-login the user and aquire a valid token ahead of time
// Useful only on cases where app is upgraded from 3.13 to 3.14 so that it re-logins the user silently if credentials are saved not token exists
class TryToGetCustomerWhenCredentialsAreSavedButTokenIsMissing @Inject constructor(
        private val customerRepository: CustomerRepository,
        private val authenticationRepository: AuthenticationRepository,
        private val getCustomer: GetCustomer) {
    operator fun invoke(): Completable {
        fun credentialsExist(): Boolean {
            return customerRepository.getLoggedInCustomerEmail().isNotEmpty()
                    && customerRepository.getLoggedInCustomerPassword().isNotEmpty()
        }
        return if (credentialsExist() && !authenticationRepository.isTokenValid()) {
            return getCustomer.invoke().ignoreElement().subscribeOn(Schedulers.io())
        } else Completable.complete()
                .subscribeOn(Schedulers.io())
    }
}