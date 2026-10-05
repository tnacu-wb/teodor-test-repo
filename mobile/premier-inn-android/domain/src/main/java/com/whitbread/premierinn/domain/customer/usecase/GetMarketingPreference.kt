package com.whitbread.premierinn.domain.customer.usecase

import com.whitbread.premierinn.domain.authentication.repository.AuthenticationRepository
import com.whitbread.premierinn.domain.common.NewsletterPreferenceDomain
import com.whitbread.premierinn.domain.customer.repository.CustomerRepository
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class GetMarketingPreference @Inject constructor(private val customerRepository: CustomerRepository,
                                                 private val authenticationRepository: AuthenticationRepository){
    operator fun invoke(email: String): Single<NewsletterPreferenceDomain> {
        return authenticationRepository.getIdToken()
            .flatMap { bearerToken ->
                customerRepository.getMarketingPreference(email, bearerToken)
                    .subscribeOn(Schedulers.io())
            }
    }
}