package com.whitbread.premierinn.common.dagger

import android.content.SharedPreferences
import com.whitbread.premierinn.api.Urls
import com.whitbread.premierinn.common.AppConfiguration
import com.whitbread.premierinn.domain.common.usecase.IsFeatureOn
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository
import androidx.core.content.edit
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManagerImpl.Constants.KEY_EMPLOYEE_OFFER_SECTION
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManagerImpl.Constants.KEY_EMPLOYEE_OFFER_TOGGLE


open class StagingConfiguration(
    private val preferences: SharedPreferences,
    isFeatureOn: IsFeatureOn
) : AppConfiguration {
    private val isEmployeeOfferEnabledFb: Boolean = isFeatureOn.invoke(ContentManagedResourceRepository.Key.FEATURE_ALLOW_EMPLOYEE_OFFER)

    override val snowdropUrl: String
        get() {
            return if (STAGING == environment) {
                preferences.getString(
                    PREF_UAT_MS_SNOWDROP_URL,
                    Urls.UAT_MICRO_SERVICE_URL
                )!!
            } else if (LIVE == environment || WANDA == environment || HULK == environment) {
                Urls.LIVE_MICRO_SERVICE_URL
            } else {
                Urls.QA_MICRO_SERVICE_URL
            }
        }

    override val microServicesUrl: String
        get() {
            return if (STAGING == environment) {
                this.restEndpointBasedOnGQLEnvt
            } else if (LIVE == environment || WANDA == environment || HULK == environment) {
                Urls.LIVE_OPERA_MICRO_SERVICE_URL
            } else {
                Urls.QA_MICRO_SERVICE_URL
            }
        }

    private val restEndpointBasedOnGQLEnvt: String
        get() {
            val graphQlEnvtUrl: String = preferences.getString(
                PREF_UAT_GQL_URL,
                Urls.GRAPHQL_UAT
            )!!
            return when (graphQlEnvtUrl) {
                Urls.GRAPHQL_DIT -> Urls.GRAPHQL_DIT_REST_ENDPOINT
                Urls.GRAPHQL_SIT -> Urls.GRAPHQL_SIT_REST_ENDPOINT
                Urls.GRAPHQL_DEMO -> Urls.GRAPHQL_DEMO_REST_ENDPOINT
                Urls.GRAPHQL_UAT -> Urls.GRAPHQL_UAT_REST_ENDPOINT
                Urls.GRAPHQL_PERF -> Urls.GRAPHQL_PERF_REST_ENDPOINT
                else -> Urls.GRAPHQL_UAT_REST_ENDPOINT
            }
        }

    override val graphQLUrl: String
        get() {
            return if (STAGING == environment) {
                preferences.getString(
                    PREF_UAT_GQL_URL,
                    Urls.GRAPHQL_UAT
                )!!
            } else if (LIVE == environment) {
                Urls.LIVE_GRAPHQL_URL
            } else if (WANDA == environment) {
                Urls.PRELIVE_WANDA_URL
            } else if (HULK == environment) {
                Urls.PRELIVE_HULK_URL
            } else {
                Urls.QA_GRAPHQL_URL
            }
        }

    override val checkInOnlineUrl: String
        get() = if (STAGING == environment) Urls.UAT_CHECK_IN_ONLINE_URL else Urls.LIVE_CHECK_IN_ONLINE_URL

    override var isCacheEnabled: Boolean
        get() = preferences.getBoolean(PREF_CACHE_ENABLED, true)
        set(enabled) {
            preferences.edit {
                putBoolean(PREF_CACHE_ENABLED, enabled)
            }
        }

    override var isSSLPinningEnabled: Boolean
        get() = preferences.getBoolean(PREF_SSL_PINNING, true)
        set(enabled) {
            preferences.edit {
                putBoolean(PREF_SSL_PINNING, enabled)
            }
        }

    override var isEmployeeOfferEnabled: Boolean
        get() = preferences.getBoolean(KEY_EMPLOYEE_OFFER_SECTION, false)
                && preferences.getBoolean(KEY_EMPLOYEE_OFFER_TOGGLE, false)
        set(enabled) {
            if (isEmployeeOfferEnabledFb && enabled) {
                preferences.edit { putBoolean(KEY_EMPLOYEE_OFFER_SECTION, true) }
                preferences.edit { putBoolean(KEY_EMPLOYEE_OFFER_TOGGLE, true) }
            } else {
                preferences.edit { putBoolean(KEY_EMPLOYEE_OFFER_SECTION, false) }
                preferences.edit { putBoolean(KEY_EMPLOYEE_OFFER_TOGGLE, false) }
            }
        }

    override var environment: String
        get() = preferences.getString(
            PREF_ENVIRONMENT,
            STAGING
        )!!
        set(environment) {
            preferences.edit {
                putString(PREF_ENVIRONMENT, environment)
            }
        }

    override fun setMicroServicesURL(value: String) {
        preferences.edit(commit = true) { putString(PREF_UAT_MS_URL, value) }
    }

    override fun setSnowdropServicesURL(value: String) {
        preferences.edit(commit = true) { putString(PREF_UAT_MS_SNOWDROP_URL, value) }
    }

    override fun setGraphQlURL(value: String) {
        preferences.edit(commit = true) { putString(PREF_UAT_GQL_URL, value) }
    }

    override val isLive: Boolean
        get() = LIVE == environment

    override val isPreLive: Boolean
        get() = WANDA == environment || HULK == environment

    override var isApolloEnabled: Boolean
        get() = preferences.getBoolean(PREF_USE_APOLLO, false)
        set(enabled) {
            preferences.edit {
                putBoolean(PREF_USE_APOLLO, enabled)
            }
        }

    companion object {
        const val STAGING: String = "STAGING"
        const val LIVE: String = "LIVE"
        const val QA: String = "QA"
        const val WANDA: String = "WANDA"
        const val HULK: String = "HULK"
        const val PREF_ENVIRONMENT: String = "PREF_ENVIRONMENT"
        private const val PREF_SSL_PINNING = "PREF_SSL_PINNING"
        private const val PREF_CACHE_ENABLED = "PREF_CACHE_ENABLED"
        private const val PREF_EMPLOYEE_OFFER = "PREF_EMPLOYEE_OFFER"
        const val PREF_UAT_MS_URL: String = "PREF_UAT_MS_URL"
        const val PREF_UAT_MS_SNOWDROP_URL: String = "PREF_UAT_MS_SNOWDROP_URL"
        const val PREF_UAT_GQL_URL: String = "PREF_UAT_GQL_URL"
        const val PREF_USE_APOLLO: String = "PREF_USE_APOLLO"
    }
}