package com.whitbread.premierinn.data.common

import java.lang.Exception

class MockErrorLogger : ErrorLogger {

    override fun logException(throwable: Throwable, message: String?) {
        println("Fake log: $message")
    }

    override fun log(message: String?) {
        println("Fake log: $message")
    }

    override fun logInfo(throwable: Throwable, tag: String, message: String) {
        println("Fake log: $message")
    }

    override fun logWarning(throwable: Throwable, tag: String, message: String) {
        println("Fake log: $message")
    }

    override fun logNonFatalExceptions(exception: Exception) {
        println("Fake log: $exception")
    }

    override fun setBooleanCustomKey(key: String, value: Boolean) {
        println("Fake log: $String")
    }
}