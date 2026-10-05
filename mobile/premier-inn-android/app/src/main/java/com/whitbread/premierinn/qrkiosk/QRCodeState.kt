
package com.whitbread.premierinn.qrkiosk

import android.graphics.Bitmap

data class QRCodeState(
    val isLoading: Boolean = false,
    val qrCode: Bitmap? = null,
    val error: String? = null
)
