package com.whitbread.premierinn.common.utils.qrBitmap

import android.graphics.Bitmap
import com.whitbread.premierinn.domain.qrcode.QrCodeResult

interface QRCodeCreateBitmap {
    fun convertToBitmap(qrcodeResult: QrCodeResult): Bitmap
}