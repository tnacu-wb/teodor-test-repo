package com.whitbread.premierinn.ciol.utils

import android.app.Activity
import android.content.Intent
import android.icu.text.NumberFormat
import android.icu.util.Currency
import android.net.Uri
import android.text.InputFilter
import android.text.Spanned
import android.util.Log
import com.whitbread.premierinn.ciol.fragments.UPSELLS_FEATURE_TAG
import com.whitbread.premierinn.data.common.EMPTY_STRING
import java.util.Locale

const val EMPTY_SPACE_CHAR = ' '
private const val ASTERISK = " *"
const val ADDRESS_REGEX = "^[\\p{L}0-9\u00E4\u00F6\u00FC\u00C4\u00D6\u00DC\u00DF/'.,\\s-]*$"

private val RESTRICTED_CHARS = arrayOf(':', ';', '~', '<', '>', '[', ']', '@', '=', '?', '/', '\\')
fun formatPrice(price: Double?, currency: String, deviceLocale: Locale): String {
    val formatter = NumberFormat.getCurrencyInstance(deviceLocale)
    formatter.currency = Currency.getInstance(currency)
    return price?.let { formatter.format(price) } ?: EMPTY_STRING
}

fun showPdfViaImplicitIntent(
    pdfUrl: String,
    activity: Activity,
    onPdfOpened: (errorOnOpening: Boolean) -> Unit
) {
    val intent = Intent().apply {
        action = Intent.ACTION_VIEW
        setDataAndType(Uri.parse(pdfUrl), "application/pdf")
    }
    if (intent.resolveActivity(activity.packageManager) == null) {
        Log.w(UPSELLS_FEATURE_TAG, "No application found for mime type application/pdf")
        intent.setDataAndType(Uri.parse(pdfUrl), "*/*")
        if (intent.resolveActivity(activity.packageManager) != null) {
            activity.startActivity(intent)
            onPdfOpened.invoke(false)
        } else {
            Log.w(UPSELLS_FEATURE_TAG, "No application found for mime type */*")
            onPdfOpened.invoke(true)
        }
    } else {
        onPdfOpened.invoke(false)
        activity.startActivity(intent)
    }
}

fun createNameInputFilter() = createInputFilter { char ->
    !Character.isLowerCase(char) && !Character.isUpperCase(char) && char != EMPTY_SPACE_CHAR
}

fun createPassportNumberInputFilter() = createInputFilter { char ->
    !Character.isDigit(char) && !Character.isLowerCase(char) && !Character.isUpperCase(char)
}

fun createPostcodeInputFilter() = createInputFilter { char ->
    !Character.isDigit(char) && !Character.isLowerCase(char) && !Character.isUpperCase(char) &&
        char != EMPTY_SPACE_CHAR
}

fun createAddressInputFilter() = createInputFilter { char ->
    return@createInputFilter !char.toString().matches(Regex(ADDRESS_REGEX)) || char in RESTRICTED_CHARS
}

fun createInputFilter(rule: (currentChar: Char) -> Boolean): InputFilter = object : InputFilter {
    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence {
        for (i in start until end) {
            if (rule(source[i])) {
                return EMPTY_STRING
            }
        }
        return source.toString()
    }
}

fun getStringWithAsterisk(text: CharSequence) =
    StringBuilder().append(text).append(ASTERISK).toString()

fun String?.value() = this ?: ""