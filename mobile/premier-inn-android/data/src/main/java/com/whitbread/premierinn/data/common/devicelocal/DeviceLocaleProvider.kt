package com.whitbread.premierinn.data.common.devicelocal

import android.content.Context
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.data.common.LANGUAGE_DEUTSCH
import com.whitbread.premierinn.data.common.LANGUAGE_ENGLISH
import com.whitbread.premierinn.domain.common.BOOKING_CHANNEL_MOBILE
import com.whitbread.premierinn.domain.common.BOOKING_CHANNEL_WEB_DE
import com.whitbread.premierinn.domain.common.COUNTRY_CODE_UK
import java.util.Locale
import javax.inject.Inject

open class DeviceLocaleProvider @Inject constructor(
    private val context: Context
) {

    fun getDeviceLocale(): Locale {
        var locale =  context.resources.configuration.locales.get(0)

        if (locale.language != LANGUAGE_DEUTSCH) {
            locale = Locale.UK
        }

        return locale
    }

    fun getCountryIfRegion(locale: Locale): String {
        return when (locale.language) {
            LANGUAGE_DEUTSCH -> {
                //    We want to return country as "de" irrespective of any region for germany i.e Belgium, Austria etc...
                //    On android, selecting different region like Deutsch(belgien) returns de_BE for locale
                locale.language.uppercase()
            }

            else -> {
                COUNTRY_CODE_UK.uppercase()
            }
        }
    }

    fun getDeviceLanguage(): String {
        return getDeviceLocale().language
    }

    fun getNationalityBasedOnDeviceLanguage() : String {
        val deviceLanguage = context.resources.configuration.locales.get(0).language

        return when (deviceLanguage.lowercase()) {
            LANGUAGE_DEUTSCH.lowercase() -> deviceLanguage.uppercase()
            LANGUAGE_ENGLISH.lowercase() -> COUNTRY_CODE_UK.uppercase()
            else -> EMPTY_STRING
        }
    }

    fun getBookingChannel() : String {
        return if (getDeviceLocale().language == LANGUAGE_DEUTSCH) {
            BOOKING_CHANNEL_WEB_DE
        } else {
            BOOKING_CHANNEL_MOBILE
        }
    }

    fun isLanguageGerman(): Boolean {
        return getDeviceLocale().language == LANGUAGE_DEUTSCH
    }
}