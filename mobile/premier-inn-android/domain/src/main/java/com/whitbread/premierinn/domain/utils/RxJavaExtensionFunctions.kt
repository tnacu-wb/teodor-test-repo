package com.whitbread.premierinn.domain.utils

import io.reactivex.Single
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

suspend fun <T> Single<T>.await(): T {
    return suspendCancellableCoroutine { cont ->
        val disposable = subscribe({
            cont.resume(it)
        }, {
            cont.resumeWithException(it)
        })
        cont.invokeOnCancellation { error ->
            error?.let { cont.resumeWithException(it) } ?: cont.resumeWithException(Exception("Something went wrong"))
            disposable.dispose()
        }
    }
}