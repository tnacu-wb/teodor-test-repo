package com.whitbread.premierinn.data

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapterFactory
import org.threeten.bp.LocalDate

/**
 * Helper class to Expose Gson Configuration in Tests
 * For Production use please reference DI instance.
 * Note: // Todo Remove if (autoValueTypeAdapter == null) {} when autovalue is gone.
 * This is only needed for tests/In data module tests we don't use AutoValue dependecy
 */

class GsonFactory {
    companion object Factory {
        @JvmStatic
        fun create(autoValueTypeAdapter: TypeAdapterFactory? = null): Gson {
            return if (autoValueTypeAdapter == null) {
                GsonBuilder()
                        .registerTypeAdapter(LocalDate::class.java, LocalDateJsonAdapter())
                        .create()
            } else {
                GsonBuilder()
                        .registerTypeAdapterFactory(autoValueTypeAdapter)
                        .registerTypeAdapter(LocalDate::class.java, LocalDateJsonAdapter())
                        .create()
            }
        }
    }
}