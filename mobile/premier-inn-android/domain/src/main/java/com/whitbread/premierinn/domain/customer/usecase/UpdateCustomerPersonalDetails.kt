package com.whitbread.premierinn.domain.customer.usecase

import com.whitbread.premierinn.domain.authentication.repository.AuthenticationRepository
import com.whitbread.premierinn.domain.authentication.usecase.GetFreshIdTokenAndRetryOnce
import com.whitbread.premierinn.domain.customer.entity.Customer
import com.whitbread.premierinn.domain.customer.repository.CustomerRepository
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class UpdateCustomerPersonalDetails @Inject constructor(private val authenticationRepository: AuthenticationRepository,
                                                        private val getFreshIdTokenAndRetryOnce: GetFreshIdTokenAndRetryOnce,
                                                        private val customerRepository: CustomerRepository) {
    operator fun invoke(params: Params): Completable {
        return authenticationRepository.getIdToken()
                .flatMapCompletable { token ->
                    customerRepository.updatePersonalDetails(token, customerRepository.getLoggedInCustomerEmail(), params.customer)
                }
                .retryWhen(getFreshIdTokenAndRetryOnce())
                .subscribeOn(Schedulers.io())
    }

    fun updateAndSavePersonalDetailsToSharedPreferences(customer: Customer) {
        val currentCustomer = customerRepository.getCustomerFromSharedPref()
        val updatedCustomer = currentCustomer.copy(fullName = customer.fullName, contact = customer.contact,
                address = customer.address, nationality = customer.nationality, passport = customer.passport,
                carRegistration = customer.carRegistration)
        customerRepository.saveCustomerToSharedPref(updatedCustomer)
    }

    data class Params(val customer: Customer)
}