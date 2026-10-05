package com.whitbread.premierinn.data.authentication

import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.authentication.storage.SecureCredentialsManager
import com.auth0.android.callback.BaseCallback
import com.auth0.android.result.Credentials
import com.whitbread.premierinn.data.common.ErrorLogger
import com.whitbread.premierinn.data.common.logAllExceptNetwork
import com.whitbread.premierinn.data.common.toDomainError
import com.whitbread.premierinn.domain.authentication.AuthenticationError
import com.whitbread.premierinn.domain.authentication.AuthenticationError.Type.SAVE_CREDENTIALS
import io.reactivex.CompletableEmitter

class AuthenticationCompletableFromCallback(private val emitter: CompletableEmitter,
                                            private val credentialsManager: SecureCredentialsManager,
                                            private val errorLogger: ErrorLogger
) : BaseCallback<Credentials, AuthenticationException> {
    override fun onSuccess(payload: Credentials?) {
        if (emitter.isDisposed) return

        try {
            credentialsManager.saveCredentials(payload!!)
            emitter.onComplete()
        } catch (e: Throwable) {
            errorLogger.logWarning(throwable = e, tag = "AuthenticationCompletableFromCallback", message = "Auth0 saving credentials")
            emitter.tryOnError(AuthenticationError.ofType(SAVE_CREDENTIALS, e))
        }
    }

    override fun onFailure(error: AuthenticationException?) {
        if (emitter.isDisposed) return

        val authenticationError = (error?.toDomainError()
                ?: AuthenticationError.unexpected("FATAL: Auth0 Exception is null"))

        errorLogger.logAllExceptNetwork("AuthenticationCompletableFromCallback", authenticationError)
        emitter.tryOnError(authenticationError)
    }
}