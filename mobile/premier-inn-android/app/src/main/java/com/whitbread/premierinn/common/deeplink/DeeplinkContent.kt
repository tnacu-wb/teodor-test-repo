package com.whitbread.premierinn.common.deeplink

import android.os.Bundle
import androidx.annotation.Keep
import java.io.Serializable

@Keep
data class DeeplinkContent(
        val deeplinkStatus: DeeplinkStatus,
        val destination: String,
        val bundleData: Bundle,
        val params: Map<String, String> = mutableMapOf()
) : Serializable

enum class DeeplinkStatus {
    KNOWN_DEEPLINK,
    UNKNOWN_DEEPLINK,
    WRONG_DEEPLINK,
    FEATURE_DISABLED
}
