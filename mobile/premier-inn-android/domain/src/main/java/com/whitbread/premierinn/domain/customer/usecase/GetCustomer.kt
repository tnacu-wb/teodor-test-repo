package com.whitbread.premierinn.domain.customer.usecase

import com.whitbread.premierinn.businessbooker.domain.company.CompanyRepository
import com.whitbread.premierinn.businessbooker.domain.customer.repository.BusinessCustomerRepository
import com.whitbread.premierinn.domain.authentication.repository.AuthenticationRepository
import com.whitbread.premierinn.domain.authentication.usecase.GetFreshIdTokenAndRetryOnce
import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import com.whitbread.premierinn.domain.common.PI_COMPANY_ID
import com.whitbread.premierinn.domain.common.decodeJwt
import com.whitbread.premierinn.domain.common.usecase.IsFeatureOn
import com.whitbread.premierinn.domain.customer.entity.Customer
import com.whitbread.premierinn.domain.customer.repository.CustomerRepository
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository
import io.reactivex.Single
import java.util.logging.Logger
import javax.inject.Inject

class GetCustomer @Inject constructor(
    private val authentication: AuthenticationRepository,
    private val getFreshIdTokenAndRetryOnce: GetFreshIdTokenAndRetryOnce,
    private val customerRepository: CustomerRepository,
    private val businessCustomerRepository: BusinessCustomerRepository,
    private val companyRepository: CompanyRepository,
    private val isFeatureOn: IsFeatureOn,
    private val getMarketingPreference: GetMarketingPreference
) {

    operator fun invoke(): Single<Customer> {
        return if (isFeatureOn.invoke(ContentManagedResourceRepository.Key.FEATURE_BUSINESS_BOOKER_QA) &&
            businessCustomerRepository.getLoggedInBusinessEmail().isNotEmpty()) {
            getBusinessUser()
        } else {
            getLeisureUser()
        }
    }

    private fun getBusinessUser(): Single<Customer> {
        return authentication.getIdToken()
            .flatMap { idToken ->
                val payload = idToken.decodeJwt()
                val operaCompanyId = payload[PI_COMPANY_ID] as? String ?: EMPTY_STRING_DOMAIN
                if (operaCompanyId.isEmpty()) {
                    Logger.getLogger("JWTDecoder").warning("Empty operaCompanyId in JWT payload")
                }
                businessCustomerRepository.saveOperaCompanyId(operaCompanyId)
                businessCustomerRepository.getBusinessCustomer(idToken)
                    .map { customer -> Pair(idToken, customer.copy(operaCompanyID = operaCompanyId)) }
            }.doOnSuccess { (_, customer) ->
                val bookingPreferences = customer.bookingPreferences
                businessCustomerRepository.saveLoggedInBusinessCustomerBookingPreferences(
                    bookingPreferences
                )
                businessCustomerRepository.storePersonalCard(customer.paymentCard)
            }.flatMap { (idToken, customer) ->
                val bookingPreferences =
                    businessCustomerRepository.getLoggedInBusinessCustomerBookingPreferences()
                if (customer.companyId == null || customer.business == null) {
                    return@flatMap Single.just(customer.copy(bookingPreferences = bookingPreferences))
                } else {
                    return@flatMap companyRepository.getCompany(
                        idToken, customer.companyId, customer.business.centralCard
                    ).map { company ->
                        businessCustomerRepository.storeCompany(company)
                        customer.copy(company = company, bookingPreferences = bookingPreferences)
                    }
                }
            }
    }

    private fun getLeisureUser(): Single<Customer> {
        return authentication.getIdToken()
            .flatMap {
                customerRepository.getCustomer(it)
            }.doOnSuccess {
                val preference = getMarketingPreference.invoke(it.contact.email).blockingGet()
                customerRepository.saveContactChannelId(preference.contactChannelId)
                customerRepository.updateCampaignDevice(isLoggedIn = true)
                customerRepository.saveLoggedInCustomerBookingPreferences(it.bookingPreferences)
            }
            .retryWhen(getFreshIdTokenAndRetryOnce())
    }

     fun getCustomerFromSharedPref(): Customer {
        return customerRepository.getCustomerFromSharedPref()
    }
}