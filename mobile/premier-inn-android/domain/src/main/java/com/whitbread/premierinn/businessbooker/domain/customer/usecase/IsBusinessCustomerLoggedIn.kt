package com.whitbread.premierinn.businessbooker.domain.customer.usecase

import com.whitbread.premierinn.businessbooker.domain.customer.repository.BusinessCustomerRepository
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class IsBusinessCustomerLoggedIn @Inject constructor(
        private val businessCustomerRepository: BusinessCustomerRepository) {
    operator fun invoke(): Single<Boolean> {
        return Single.defer {
            if (businessCustomerRepository.getLoggedInBusinessEmail().isNotEmpty()
                    && businessCustomerRepository.getLoggedInBusinessPassword().isNotEmpty()) {
                Single.just(true)
            } else Single.just(false)
        }.subscribeOn(Schedulers.io())
    }
}