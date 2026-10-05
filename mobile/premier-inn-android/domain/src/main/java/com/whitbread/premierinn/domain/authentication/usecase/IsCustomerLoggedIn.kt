package com.whitbread.premierinn.domain.authentication.usecase

import com.whitbread.premierinn.businessbooker.domain.customer.repository.BusinessCustomerRepository
import com.whitbread.premierinn.domain.common.usecase.IsFeatureOn
import com.whitbread.premierinn.domain.customer.repository.CustomerRepository
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class IsCustomerLoggedIn @Inject constructor(
        private val customerRepository: CustomerRepository,
        private val businessCustomerRepository: BusinessCustomerRepository,
        private val isFeatureOn: IsFeatureOn) {
    operator fun invoke(): Single<Boolean> {
        return Single.defer {
            if (isLoggedInAsLeisureCustomer() || isLoggedInAsBusinessCustomer() ) {
                Single.just(true)
            } else Single.just(false)
        }.subscribeOn(Schedulers.io())
    }

    private fun isLoggedInAsLeisureCustomer() : Boolean {
        return customerRepository.getLoggedInCustomerEmail().isNotEmpty()
                && customerRepository.getLoggedInCustomerPassword().isNotEmpty()
    }

    fun isLoggedInAsBusinessCustomer() : Boolean {
        return isFeatureOn.invoke(ContentManagedResourceRepository.Key.FEATURE_BUSINESS_BOOKER_QA) &&
                businessCustomerRepository.getLoggedInBusinessEmail().isNotEmpty() &&
                businessCustomerRepository.getLoggedInBusinessPassword().isNotEmpty()
    }
}