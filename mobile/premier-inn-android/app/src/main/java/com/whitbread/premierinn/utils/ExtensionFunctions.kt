package com.whitbread.premierinn.utils

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.core.os.BundleCompat
import com.whitbread.premierinn.data.common.EMPTY_STRING

const val POUND_SIGN: String = "\u00a3"
private const val EURO_SIGN: String = "\u20ac"

inline fun <reified T : Parcelable> Bundle.parcelable(key: String): T? = BundleCompat.getParcelable(this, key, T::class.java) ?: run {
    Log.w("Arguments", "Serializable argument for key $key is null")
    return null
}

fun String.getCurrencySign() = when(this) {
    "GBP" -> POUND_SIGN
    "EUR" -> EURO_SIGN
    else -> EMPTY_STRING
}

fun Intent.isDeeplinkIntent(): Boolean = Intent.ACTION_VIEW == this.action && this.data != null
fun Intent.isNotificationIntent(): Boolean = Intent.ACTION_MAIN == this.action && this.hasExtra("google.message_id")
fun Intent.isEmployeeQRCode(): Boolean = Intent.ACTION_VIEW == this.action