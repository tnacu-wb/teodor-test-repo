package com.whitbread.premierinn.data.authentication

import com.auth0.android.result.Credentials
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import com.whitbread.premierinn.data.common.ErrorLogger
import com.whitbread.premierinn.domain.authentication.UnAuthorizedCustomerError
import io.reactivex.SingleEmitter
import org.junit.Test

class CredentialsSingleFromCallbackTest {

    private val credentials: Credentials = mock()
    private val errorLogger: ErrorLogger = mock()

    @Test
    fun onSuccess() {
        val emitter: SingleEmitter<Credentials> = mock {
            on { isDisposed } doReturn false
        }
        val singleFromCallback = CredentialsSingleFromCallback(emitter, errorLogger)

        singleFromCallback.onSuccess(credentials)

        verify(emitter).isDisposed
        verify(emitter).onSuccess(credentials)

        verifyNoMoreInteractions(emitter)
        verifyNoMoreInteractions(errorLogger)

    }

    @Test
    fun `onSuccess but emitter is disposed`() {
        val emitter: SingleEmitter<Credentials> = mock {
            on { isDisposed } doReturn true
        }
        val singleFromCallback = CredentialsSingleFromCallback(emitter, errorLogger)

        singleFromCallback.onSuccess(credentials)

        verify(emitter).isDisposed

        verifyNoMoreInteractions(emitter)
        verifyNoMoreInteractions(errorLogger)
    }

    @Test
    fun `onSuccess but payload is null`() {
        val emitter: SingleEmitter<Credentials> = mock {
            on { isDisposed } doReturn false
        }
        val singleFromCallback = CredentialsSingleFromCallback(emitter, errorLogger)

        singleFromCallback.onSuccess(null)

        verify(emitter).isDisposed

        verify(errorLogger).logWarning(throwable = any<NullPointerException>(), tag = any(), message = any())
        verify(emitter).tryOnError(UnAuthorizedCustomerError)

        verifyNoMoreInteractions(emitter)
        verifyNoMoreInteractions(errorLogger)
    }

    @Test
    fun onFailure() {
        val emitter: SingleEmitter<Credentials> = mock {
            on { isDisposed } doReturn false
        }
        val singleFromCallback = CredentialsSingleFromCallback(emitter, errorLogger)

        singleFromCallback.onFailure(null) // cannot create CredentialsManagerException constructor is private

        verify(emitter).isDisposed
        verify(emitter).tryOnError(any())

        verifyNoMoreInteractions(emitter)
        verifyNoMoreInteractions(errorLogger)
    }

    @Test
    fun `onFailure but emitter is disposed`() {

        val emitter: SingleEmitter<Credentials> = mock {
            on { isDisposed } doReturn true
        }
        val singleFromCallback = CredentialsSingleFromCallback(emitter, errorLogger)

        singleFromCallback.onFailure(null) // cannot create CredentialsManagerException constructor is private

        verify(emitter).isDisposed

        verifyNoMoreInteractions(emitter)
        verifyNoMoreInteractions(errorLogger)
    }
}