package com.whitbread.premierinn.common.service

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.whitbread.premierinn.data.common.ErrorLogger

class LogService : ErrorLogger {

    @Synchronized
    override fun logException(throwable: Throwable, message: String?) {
        message?.let {
            FirebaseCrashlytics.getInstance().log(it)
        }
        FirebaseCrashlytics.getInstance().recordException(throwable)
    }

    @Synchronized
    override fun log(message: String?) {
        message?.let {
            FirebaseCrashlytics.getInstance().log(it)
        }
    }

    @Synchronized
    override fun logInfo(throwable: Throwable, tag: String, message: String) =
            logWithPriority(throwable, Log.INFO, tag, message)

    @Synchronized
    override fun logWarning(throwable: Throwable, tag: String, message: String) =
            logWithPriority(throwable, Log.WARN, tag, message)

    private fun logWithPriority(throwable: Throwable, priority: Int, tag: String, message: String) {
        FirebaseCrashlytics.getInstance().setCustomKey(tag, message)
        FirebaseCrashlytics.getInstance().log(message)
        FirebaseCrashlytics.getInstance().recordException(throwable)
    }

    override fun logNonFatalExceptions(exception: Exception) {
        FirebaseCrashlytics.getInstance().recordException(exception)
    }

    override fun setBooleanCustomKey(key: String, value: Boolean) {
        FirebaseCrashlytics.getInstance().setCustomKey(key,value)
    }
}