package com.whitbread.premierinn.data.authentication

import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.authentication.storage.SecureCredentialsManager
import com.auth0.android.callback.BaseCallback
import com.auth0.android.result.Credentials
import com.whitbread.premierinn.data.common.ErrorLogger
import com.whitbread.premierinn.data.common.logAllExceptNetwork
import com.whitbread.premierinn.data.common.toDomainError
import com.whitbread.premierinn.domain.authentication.AuthenticationError
import com.whitbread.premierinn.domain.authentication.AuthenticationError.Type
import com.whitbread.premierinn.domain.authentication.repository.RefreshToken
import io.reactivex.SingleEmitter

class RenewAuthSingleFromCallback(private val emitter: SingleEmitter<String>,
                                  private val refreshToken: RefreshToken,
                                  private val credentialsManager: SecureCredentialsManager,
                                  private val errorLogger: ErrorLogger
) : BaseCallback<Credentials, AuthenticationException> {
    override fun onSuccess(fresh: Credentials?) {
        if (emitter.isDisposed) return

        try {
            credentialsManager.saveCredentials(Credentials(fresh!!.idToken, fresh.accessToken,
                    fresh.type, refreshToken, fresh.expiresAt, fresh.scope))
            emitter.onSuccess(fresh.idToken!!)
        } catch (e: Throwable) {
            errorLogger.logWarning(throwable = e, tag = "RenewAuthSingleFromCallback", message = "Auth0 saving credentials")
            emitter.tryOnError(AuthenticationError.ofType(Type.SAVE_CREDENTIALS, e))
        }
    }

    override fun onFailure(error: AuthenticationException?) {
        if (emitter.isDisposed) return

        val authenticationError = (error?.toDomainError()
                ?: AuthenticationError.unexpected("FATAL: Auth0 Exception is null"))

        errorLogger.logAllExceptNetwork("RenewAuthSingleFromCallback", authenticationError)
        emitter.tryOnError(authenticationError)
    }
}