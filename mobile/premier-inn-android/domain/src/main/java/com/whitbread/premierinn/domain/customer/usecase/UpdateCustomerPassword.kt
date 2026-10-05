package com.whitbread.premierinn.domain.customer.usecase

import com.whitbread.premierinn.businessbooker.domain.customer.repository.BusinessCustomerRepository
import com.whitbread.premierinn.domain.authentication.repository.AuthenticationRepository
import com.whitbread.premierinn.domain.authentication.repository.IdToken
import com.whitbread.premierinn.domain.authentication.usecase.GetFreshIdTokenAndRetryOnce
import com.whitbread.premierinn.domain.authentication.usecase.IsCustomerLoggedIn
import com.whitbread.premierinn.domain.customer.PasswordsDoNoMatchError
import com.whitbread.premierinn.domain.customer.repository.CustomerRepository
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class UpdateCustomerPassword @Inject constructor(private val authenticationRepository: AuthenticationRepository,
                                                 private val getFreshIdTokenAndRetryOnce: GetFreshIdTokenAndRetryOnce,
                                                 private val customerRepository: CustomerRepository,
                                                 private val businessCustomerRepository: BusinessCustomerRepository,
                                                 private val isCustomerLoggedIn: IsCustomerLoggedIn) {
    operator fun invoke(params: Params): Completable {
        if (params.newPassword != params.confirmationPassword) {
            return Completable.error(PasswordsDoNoMatchError)
        }
        return updateCustomerPassword(params)
    }

    private fun updateCustomerPassword(params: Params): Completable {
        return if (isCustomerLoggedIn.isLoggedInAsBusinessCustomer()) {
            updateBusinessCustomerPassword(params)
        } else {
            authenticationRepository.getIdToken()
                    .flatMapCompletable { token ->
                        updateLeisureCustomerPassword(params, token)
                    }
                    .retryWhen(getFreshIdTokenAndRetryOnce())
                    .subscribeOn(Schedulers.io())
        }
    }

    private fun updateLeisureCustomerPassword(params: Params, token: IdToken): Completable {
        val email = customerRepository.getLoggedInCustomerEmail()
        val pass = customerRepository.getLoggedInCustomerPassword() //TODO to be replaced with confirmationPassword - POST auth0 MIGRATION
        return customerRepository.updatePassword(token, email, pass, params.newPassword)
                .andThen(Completable.fromCallable {
                    customerRepository.saveLoggedInCustomerPassword(params.confirmationPassword)
                })  //TODO to be removed - POST auth0 MIGRATION
    }

    private fun updateBusinessCustomerPassword(params: Params): Completable {
        val email = businessCustomerRepository.getLoggedInBusinessEmail()
        val pass = businessCustomerRepository.getLoggedInBusinessPassword()
        return authenticationRepository.getIdToken()
                .flatMapCompletable { token ->
                    businessCustomerRepository.updatePassword(token, email, pass, params.newPassword)
                        .andThen(Completable.fromCallable {
                            businessCustomerRepository.saveLoggedInBusinessPassword(params.confirmationPassword)
                        })
                }
    }

    data class Params(val newPassword: String, val confirmationPassword: String)
}