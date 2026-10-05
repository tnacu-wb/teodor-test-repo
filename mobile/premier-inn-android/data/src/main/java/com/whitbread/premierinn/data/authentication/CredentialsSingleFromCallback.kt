package com.whitbread.premierinn.data.authentication

import com.auth0.android.authentication.storage.CredentialsManagerException
import com.auth0.android.callback.BaseCallback
import com.auth0.android.result.Credentials
import com.whitbread.premierinn.data.common.ErrorLogger
import com.whitbread.premierinn.domain.authentication.UnAuthorizedCustomerError
import io.reactivex.SingleEmitter

class CredentialsSingleFromCallback(private val emitter: SingleEmitter<Credentials>,
                                    private val errorLogger: ErrorLogger
) : BaseCallback<Credentials, CredentialsManagerException> {
    override fun onSuccess(payload: Credentials?) {
        if (emitter.isDisposed) return
        try {
            emitter.onSuccess(payload!!)
        } catch (e: Throwable) {
            errorLogger.logWarning(throwable = e, tag = "CredentialsSingleFromCallback", message = "retrieve credentials payload")
            emitter.tryOnError(UnAuthorizedCustomerError)
        }
    }

    override fun onFailure(error: CredentialsManagerException?) {
        if (emitter.isDisposed) return

        emitter.tryOnError(UnAuthorizedCustomerError)
    }
}