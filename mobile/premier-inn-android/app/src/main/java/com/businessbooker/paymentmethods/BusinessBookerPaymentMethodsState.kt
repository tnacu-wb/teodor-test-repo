package com.businessbooker.paymentmethods

import com.whitbread.premierinn.businessbooker.data.common.persistence.BusinessPersistenceManager
import com.whitbread.premierinn.businessbooker.domain.company.PaymentCard
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManager
import com.whitbread.premierinn.domain.customer.entity.AccessLevel

data class BusinessBookerPaymentMethodsState(
    val businessPersistenceManager: BusinessPersistenceManager,
    val simplePersistenceManager: SimplePersistenceManager
) {

    val businessAccountCard: PaymentCard?
        get() = businessPersistenceManager.getBusinessAccountCard()

    val personalCard: com.whitbread.premierinn.domain.customer.entity.PaymentCard?
        get() = simplePersistenceManager.getPersonalCard()

    val customerAccessLevel: AccessLevel
        get() = businessPersistenceManager.getCustomerAccessLevel()

    val isCompanyCardAllocated: Boolean
        get() = businessPersistenceManager.isCompanyCardAllocated()

    val isPersonalCardAllowed: Boolean
        get() = businessPersistenceManager.isPersonalCardAllowed()
}