package com.whitbread.premierinn.common

import android.content.SharedPreferences
import com.whitbread.premierinn.api.Urls
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManagerImpl.Constants.KEY_EMPLOYEE_OFFER_SECTION
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManagerImpl.Constants.KEY_EMPLOYEE_OFFER_TOGGLE
import com.whitbread.premierinn.domain.common.usecase.IsFeatureOn
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository

class ProductionConfiguration(
    isFeatureOn: IsFeatureOn,
    private val preferences: SharedPreferences
) : AppConfiguration {
    private val isEmployeeofferEnabledFb: Boolean = isFeatureOn.invoke(ContentManagedResourceRepository.Key.FEATURE_ALLOW_EMPLOYEE_OFFER)

    override val snowdropUrl: String
        get() = Urls.LIVE_MICRO_SERVICE_URL

    override val microServicesUrl: String
        get() = Urls.LIVE_OPERA_MICRO_SERVICE_URL

    override val graphQLUrl: String
        get() = Urls.LIVE_GRAPHQL_URL

    override val checkInOnlineUrl: String
        get() = Urls.LIVE_CHECK_IN_ONLINE_URL

    override var environment: String
        get() = ""
        set(environment) {
            //void not valid in production
        }

    override fun setMicroServicesURL(value: String) {
        //void not valid in production
    }

    override fun setSnowdropServicesURL(value: String) {
    }

    override fun setGraphQlURL(value: String) {
        //void not valid in production
    }

    override val isLive: Boolean
        get() = true

    override val isPreLive: Boolean
        get() = true

    override var isCacheEnabled: Boolean
        get() = true
        set(enabled) {
            //void not valid in production
        }

    override var isSSLPinningEnabled: Boolean
        get() = true
        set(enabled) {
            //void not valid in production
        }

    override var isEmployeeOfferEnabled: Boolean
        get() = preferences.getBoolean(KEY_EMPLOYEE_OFFER_SECTION, false)
                && preferences.getBoolean(KEY_EMPLOYEE_OFFER_TOGGLE, false)
        set(enabled) {
            //void not valid in production
            // This is used for staging version when we enable the toggle
            // Since for prod it will not go thr' this flow
        }

    override var isApolloEnabled: Boolean
        get() = false
        set(enabled) {
            // void not valid in production
        }
}