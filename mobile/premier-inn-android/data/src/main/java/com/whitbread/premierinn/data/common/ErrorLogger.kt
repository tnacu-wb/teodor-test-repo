package com.whitbread.premierinn.data.common

import java.lang.Exception

interface ErrorLogger {

    fun logException(throwable: Throwable, message: String? = null)
    fun log(message: String? = null)
    fun logInfo(throwable: Throwable, tag: String, message: String)
    fun logWarning(throwable: Throwable, tag: String, message: String)
    fun logNonFatalExceptions(exception: Exception)
    fun setBooleanCustomKey(key: String, value : Boolean)

}