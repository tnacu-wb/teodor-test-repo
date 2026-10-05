package com.whitbread.premierinn.businessbooker.data.common.persistence

import com.whitbread.premierinn.businessbooker.domain.company.Company
import com.whitbread.premierinn.businessbooker.domain.company.PaymentCard
import com.whitbread.premierinn.domain.customer.entity.AccessLevel
import com.whitbread.premierinn.domain.customer.entity.BookingPreferences

interface BusinessPersistenceManager {

    fun setBusinessCustomerEmail(value: String?)
    fun setBusinessCustomerPass(value: String?)
    fun getBusinessCustomerEmail(): String
    fun getBusinessCustomerPass(): String

    fun storeBusinessSessionId(sessionId: String) // Might Be needed later
    fun retrieveBusinessSessionId(): String
    fun clearBusinessSessionId()

    fun saveValuesForBusinessRulesInnBusiness(maxNights: Int, maxRooms: Int, maxArrivalDate: Int)
    fun clearValuesForBusinessRulesInnBusiness()

    fun getMaxRoomsInnBusiness(): Int
    fun getMaxNightsInnBusiness(): Int
    fun getMaxArrivalDateInnBusiness(): Int

    fun setCustomerBookingPreferences(bookingPreferences: BookingPreferences?)
    fun getCustomerBookingPreferences(): BookingPreferences

    fun setCompanyName(companyName: String)
    fun getCompanyName(): String

    fun storeBusinessAccountCard(paymentCard: PaymentCard?)
    fun getBusinessAccountCard(): PaymentCard?

    fun storeCustomerAccessLevel(accessLevel: AccessLevel)
    fun getCustomerAccessLevel(): AccessLevel
    fun clearCustomerAccessLevel()

    fun companyCardAllocated(allocated: Boolean)
    fun isCompanyCardAllocated(): Boolean
    fun clearCompanyCardAllocated()

    fun personalCardAllowed(allowed: Boolean)
    fun isPersonalCardAllowed(): Boolean
    fun clearPersonalCardAllowed()

    fun storeOperaCompanyId(value: String?)
    fun getOperaCompanyId(): String

    fun storeCompany(company: Company?)
    fun getCompany(): Company?
}
