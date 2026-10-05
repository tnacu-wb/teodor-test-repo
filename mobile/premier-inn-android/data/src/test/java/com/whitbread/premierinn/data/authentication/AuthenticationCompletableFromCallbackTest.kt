package com.whitbread.premierinn.data.authentication

import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.authentication.storage.SecureCredentialsManager
import com.auth0.android.result.Credentials
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import com.whitbread.premierinn.data.common.ErrorLogger
import com.whitbread.premierinn.domain.authentication.AuthenticationError
import com.whitbread.premierinn.domain.authentication.AuthenticationError.Type.SAVE_CREDENTIALS
import io.reactivex.CompletableEmitter
import org.junit.Test

class AuthenticationCompletableFromCallbackTest {

    private val credentials: Credentials = mock()
    private val errorLogger: ErrorLogger = mock()
    private val storage: SecureCredentialsManager = mock()

    @Test
    fun success() {
        val emitter: CompletableEmitter = mock {
            on { isDisposed } doReturn false
        }
        val authentication = AuthenticationCompletableFromCallback(emitter, storage, errorLogger)

        authentication.onSuccess(credentials)

        verify(storage).saveCredentials(credentials)
        verify(emitter).onComplete()
    }

    @Test
    fun `success but with save credentials (crypto util) error`() {
        val emitter: CompletableEmitter = mock {
            on { isDisposed } doReturn false
        }
        val throwable = NullPointerException()
        val storage: SecureCredentialsManager = mock {
            on { saveCredentials(any()) } doThrow throwable
        }
        val authentication = AuthenticationCompletableFromCallback(emitter, storage, errorLogger)

        authentication.onSuccess(credentials)

        verify(emitter).tryOnError(AuthenticationError.ofType(SAVE_CREDENTIALS, throwable))
        verify(errorLogger).logWarning(throwable, "AuthenticationCompletableFromCallback", "Auth0 saving credentials")
    }

    @Test
    fun `success but emitter disposed`() {
        val emitter: CompletableEmitter = mock {
            on { isDisposed } doReturn true
        }

        val authentication = AuthenticationCompletableFromCallback(emitter, storage, errorLogger)

        authentication.onSuccess(credentials)

        verify(storage, times(0)).saveCredentials(credentials)
        verify(emitter, times(0)).onComplete()
    }

    @Test
    fun failure() {
        val emitter: CompletableEmitter = mock {
            on { isDisposed } doReturn false
        }
        val authentication = AuthenticationCompletableFromCallback(emitter, storage, errorLogger)

        authentication.onFailure(AuthenticationException("e"))

        verify(storage, times(0)).saveCredentials(credentials)
        verify(emitter).tryOnError(any())
    }

    @Test
    fun `failure but emitter disposed`() {
        val emitter: CompletableEmitter = mock {
            on { isDisposed } doReturn true
        }

        val authentication = AuthenticationCompletableFromCallback(emitter, storage, errorLogger)

        authentication.onFailure(AuthenticationException("e"))

        verify(storage, times(0)).saveCredentials(credentials)
        verify(emitter, times(0)).tryOnError(any())
    }
}