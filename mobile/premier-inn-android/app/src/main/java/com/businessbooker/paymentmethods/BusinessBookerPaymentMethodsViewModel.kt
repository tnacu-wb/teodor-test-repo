package com.businessbooker.paymentmethods

import com.whitbread.premierinn.businessbooker.data.common.persistence.BusinessPersistenceManager
import com.whitbread.premierinn.common.Reducer
import com.whitbread.premierinn.common.RxViewModelStore
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.ScreenState.MANAGE_SAVED_CARDS
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Type.MY_PREMIER_INN
import com.whitbread.premierinn.common.analytics.TrackingAnalytics
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BusinessBookerPaymentMethodsViewModel @Inject constructor(
    businessPersistenceManager: BusinessPersistenceManager,
    simplePersistenceManager: SimplePersistenceManager,
    private val trackingAnalytics: TrackingAnalytics
) : RxViewModelStore<BusinessBookerPaymentMethodsState, BusinessBookerPaymentMethodsViewModel.BusinessBookerPaymentMethodsEvent>(
    BusinessBookerPaymentMethodsState(businessPersistenceManager = businessPersistenceManager,
        simplePersistenceManager = simplePersistenceManager)
) {

    init {
        applyState(Reducer { it.copy(businessPersistenceManager = businessPersistenceManager) })
        trackAnalytics()
    }

    private fun trackAnalytics() {
        trackingAnalytics.track(MANAGE_SAVED_CARDS, MY_PREMIER_INN)
    }

    sealed class BusinessBookerPaymentMethodsEvent
}

