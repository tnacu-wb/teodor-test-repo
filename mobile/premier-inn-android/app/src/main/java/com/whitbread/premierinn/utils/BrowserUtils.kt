package com.whitbread.premierinn.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri

fun openUrlWithFallback(context: Context, url: String) {
    val chromePackage = "com.android.chrome"
    val uri = url.toUri()

    // Try Chrome Custom Tabs first
    if (isPackageInstalled(context, chromePackage)) {
        val customTabsIntent = CustomTabsIntent.Builder().build()
        customTabsIntent.intent.setPackage(chromePackage)
        try {
            customTabsIntent.launchUrl(context, uri)
            return
        } catch (_: Exception) {
            // Chrome Custom Tabs failed, try Chrome as regular browser
            try {
                val chromeIntent = Intent(Intent.ACTION_VIEW, uri).apply {
                    setPackage(chromePackage)
                }
                context.startActivity(chromeIntent)
                return
            } catch (_: ActivityNotFoundException) {
                // Chrome browser also failed, fall back to default browser
                openDefaultBrowser(context, uri)
            }
        }
    } else {
        // Chrome not installed, use default browser
        openDefaultBrowser(context, uri)
    }
}

private fun openDefaultBrowser(context: Context, uri: android.net.Uri) {
    // Fall back to default browser (Samsung Internet, Edge, or any other)
    val intent = Intent(Intent.ACTION_VIEW, uri).apply {
        addCategory(Intent.CATEGORY_BROWSABLE)
    }

    val packageManager = context.packageManager
    val resolveInfos =
        packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).toList()

    val browserInfo = resolveInfos.firstOrNull {
        it.activityInfo.packageName != context.packageName
    }

    if (browserInfo != null) {
        // Launch the external browser explicitly
        intent.setPackage(browserInfo.activityInfo.packageName)
        try {
            context.startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            // No browser available
        }
    }
}

private fun isPackageInstalled(context: Context, packageName: String): Boolean {
    return try {
        context.packageManager.getPackageInfo(packageName, 0)
        true
    } catch (_: PackageManager.NameNotFoundException) {
        false
    }
}