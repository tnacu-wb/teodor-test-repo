package com.whitbread.premierinn.businessbooker.data.company

import com.whitbread.premierinn.businessbooker.data.common.persistence.BusinessPersistenceManager
import com.whitbread.premierinn.businessbooker.data.common.toDomain
import com.whitbread.premierinn.businessbooker.data.remote.CompanyApi
import com.whitbread.premierinn.businessbooker.domain.company.Company
import com.whitbread.premierinn.businessbooker.domain.company.CompanyRepository
import com.whitbread.premierinn.data.common.ErrorLogger
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.data.remote.AUTHORIZATION_BEARER
import com.whitbread.premierinn.domain.authentication.repository.IdToken
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider

class CompanyRepositoryImpl @Inject constructor(
    private val companyApi: CompanyApi,
    private val businessPersistenceManager: BusinessPersistenceManager,
    private val logger: ErrorLogger,
    private val deviceLocaleProvider: DeviceLocaleProvider,
    @Named("AkamaiSensorData") private val sensorData: Provider<String>
) : CompanyRepository {

    override fun getCompany(
        idToken: IdToken,
        companyId: String,
        centralCard: String?
    ): Single<Company> {
        val deviceLocale = deviceLocaleProvider.getDeviceLocale()
        val country = deviceLocaleProvider.getCountryIfRegion(deviceLocale).lowercase()
        val language = deviceLocale.language

        return companyApi.getCompany(
            "$AUTHORIZATION_BEARER $idToken",
            sensorData.get(),
            companyId,
            country,
            language
        )
            .map { response -> response.toDomain() }
            .doOnSuccess {
                try {
                    businessPersistenceManager.setCompanyName(
                        if (it.requestedCompany.companyDetails.alternateCompanyName.isNotEmpty())
                            it.requestedCompany.companyDetails.alternateCompanyName
                        else
                            it.requestedCompany.companyDetails.companyName
                    )
                    if (centralCard != null) {
                        businessPersistenceManager.storeBusinessAccountCard(
                            it.requestedCompany.paymentDetails?.paymentCards?.first { paymentCard -> paymentCard.cardId == centralCard })
                    }
                    businessPersistenceManager.companyCardAllocated(it.allowCentralCreditCard)
                    businessPersistenceManager.personalCardAllowed(it.requestedCompany.paymentDetails?.allowIndividualCards == true)
                } catch (exception: NullPointerException) {
                    logger.logException(exception, "Get company NullPointerException")
                } catch (exception: NoSuchElementException) {
                    logger.logException(exception, "Get company NoSuchElementException")
                }
            }
            .subscribeOn(Schedulers.io())
    }
}