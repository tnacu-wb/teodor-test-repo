package com.whitbread.premierinn.data.authentication

import com.auth0.android.authentication.AuthenticationAPIClient
import com.auth0.android.authentication.storage.SecureCredentialsManager
import com.auth0.android.request.AuthenticationRequest
import com.auth0.android.result.Credentials
import com.whitbread.premierinn.data.common.ErrorLogger
import com.whitbread.premierinn.data.common.GRAPHQL_DEMO_URL
import com.whitbread.premierinn.data.common.GRAPHQL_DEV_URL
import com.whitbread.premierinn.data.common.GRAPHQL_DIT_URL
import com.whitbread.premierinn.data.common.GRAPHQL_PERF_URL
import com.whitbread.premierinn.data.common.GRAPHQL_PRE_PROD_URL
import com.whitbread.premierinn.data.common.GRAPHQL_SIT_URL
import com.whitbread.premierinn.data.common.GRAPHQL_UAT_URL
import com.whitbread.premierinn.domain.authentication.UserType
import com.whitbread.premierinn.domain.authentication.repository.AuthenticationRepository
import com.whitbread.premierinn.domain.authentication.repository.IdToken
import com.whitbread.premierinn.domain.authentication.repository.RefreshToken
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Auth0 Login setScope("openid offline_access") is defined here
 * https://auth0.com/docs/tokens/concepts/refresh-tokens
 *
 *
 *  There are few reported crashes in Auth0 SDK SecureCredential Manager class.
 *   https://github.com/auth0/Auth0.Android/issues/224
 *   https://github.com/auth0/Auth0.Android/issues/230
 */
class AuthenticationRepositoryImpl @Inject constructor(
    private val auth0Client: AuthenticationAPIClient,
    private val credentialsManager: SecureCredentialsManager,
    private val errorLogger: ErrorLogger,
    private val isLiveEnvironment: Boolean,
    private val isPreLiveEnvironment: Boolean,
    private val graphQlEnvironment: String
) : AuthenticationRepository {

    override fun authenticate(user: String, password: String, userType: UserType): Completable {

        fun buildRequest(): AuthenticationRequest {
            return if (isLiveEnvironment || isPreLiveEnvironment) {
                auth0Client.login(user, password, getLiveRealmName(userType))
            } else {
                auth0Client.login(user, password, graphQlEnvironment.getStagingRealmName(userType))
            }
        }

        return Completable.create {
            val listener = AuthenticationCompletableFromCallback(it, credentialsManager, errorLogger)
            buildRequest().setScope("openid offline_access").start(listener)
        }.subscribeOn(Schedulers.io())
    }

    private fun String.getStagingRealmName(userType: UserType): String {
        val (perf, uat) = if (userType == UserType.LEISURE) "pi-perf" to "pi-uat" else "bb-perf" to "bb-uat"
        val sit = if (userType == UserType.LEISURE) "pi-sit" else "bb-sit"

        return when (this) {
            GRAPHQL_PERF_URL -> perf

            GRAPHQL_DEMO_URL,
            GRAPHQL_DEV_URL,
            GRAPHQL_DIT_URL,
            GRAPHQL_PRE_PROD_URL,
            GRAPHQL_UAT_URL-> uat

            GRAPHQL_SIT_URL -> sit

            else -> uat
        }
    }

    private fun getLiveRealmName(userType: UserType): String = when (userType) {
        UserType.LEISURE -> "pi-prod"
        else -> "bb-prod"
    }

    override fun renewRefreshToken(): Single<IdToken> {
        fun forceRenew(refreshToken: RefreshToken) = Single.create<IdToken> {
            val listener = RenewAuthSingleFromCallback(it, refreshToken, credentialsManager, errorLogger)
            auth0Client.renewAuth(refreshToken).start(listener)
        }.subscribeOn(Schedulers.io())

        return getRefreshToken().flatMap { forceRenew(it) }
    }

    override fun logout(): Completable {
        return Completable.fromCallable { credentialsManager.clearCredentials() }
                .doOnError { errorLogger.logInfo(throwable = it, tag = "AuthenticationRepositoryImpl", message = "logout") }
    }

    override fun isTokenValid(): Boolean {
        return credentialsManager.hasValidCredentials()
    }

    override fun getIdToken(): Single<IdToken> {
        return getCredentialsWithAutoRenewWhenTokenExpires().map { it.idToken }
    }

    override fun getRefreshToken(): Single<RefreshToken> {
        return getCredentialsWithAutoRenewWhenTokenExpires().map { it.refreshToken }
    }

    private fun getCredentialsWithAutoRenewWhenTokenExpires(): Single<Credentials> {
        return Single.create<Credentials> {
            val listener = CredentialsSingleFromCallback(it, errorLogger)
            credentialsManager.getCredentials(listener)
        }
    }
}