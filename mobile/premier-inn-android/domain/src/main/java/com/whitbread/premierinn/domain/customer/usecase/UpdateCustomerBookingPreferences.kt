package com.whitbread.premierinn.domain.customer.usecase

import com.whitbread.premierinn.businessbooker.domain.customer.repository.BusinessCustomerRepository
import com.whitbread.premierinn.domain.authentication.repository.AuthenticationRepository
import com.whitbread.premierinn.domain.authentication.usecase.GetFreshIdTokenAndRetryOnce
import com.whitbread.premierinn.domain.authentication.usecase.IsCustomerLoggedIn
import com.whitbread.premierinn.domain.customer.entity.BookingPreferences
import com.whitbread.premierinn.domain.customer.repository.CustomerRepository
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class UpdateCustomerBookingPreferences @Inject constructor(private val authenticationRepository: AuthenticationRepository,
                                                           private val getFreshIdTokenAndRetryOnce: GetFreshIdTokenAndRetryOnce,
                                                           private val customerRepository: CustomerRepository,
                                                           private val businessCustomerRepository: BusinessCustomerRepository,
                                                           private val isCustomerLoggedIn: IsCustomerLoggedIn) {
    operator fun invoke(params: Params): Completable {
        return if (isCustomerLoggedIn.isLoggedInAsBusinessCustomer()) {
            businessCustomerRepository
                    .updateBookingPreferences(businessCustomerRepository.getLoggedInBusinessEmail(), params.preferences)
                    .doOnComplete { businessCustomerRepository.saveLoggedInBusinessCustomerBookingPreferences(params.preferences) }
                    .subscribeOn(Schedulers.io())
        } else {
            authenticationRepository.getIdToken()
                    .flatMapCompletable { token ->
                        customerRepository
                                .updateBookingPreferences(token, customerRepository.getLoggedInCustomerEmail(), params.preferences)
                                .doOnComplete { customerRepository.saveLoggedInCustomerBookingPreferences(params.preferences) }

                    }
                    .retryWhen(getFreshIdTokenAndRetryOnce())
                    .subscribeOn(Schedulers.io())
        }
    }

    fun updateCustomerBookingPreferencesToSharedPreferences(preferences: BookingPreferences) {
        val currentPreferences = customerRepository.getCustomerFromSharedPref()
        val updatedPreferences = currentPreferences.copy( bookingPreferences = preferences)
        customerRepository.saveCustomerToSharedPref(updatedPreferences)
    }

    data class Params(val preferences: BookingPreferences)
}