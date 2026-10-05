package com.whitbread.premierinn.common.utils.qrBitmap

import android.graphics.Bitmap
import android.graphics.Color
import com.whitbread.premierinn.domain.qrcode.QrCodeResult

class QrCodeCreateBitmapImpl: QRCodeCreateBitmap {

    override fun convertToBitmap(qrcodeResult: QrCodeResult): Bitmap {
        val bitmap =
            Bitmap.createBitmap(qrcodeResult.width, qrcodeResult.height, Bitmap.Config.RGB_565)
        for (x in 0 until qrcodeResult.width) {
            for (y in 0 until qrcodeResult.height) {
                bitmap.setPixel(
                    x,
                    y,
                    if (qrcodeResult.pixelData[x][y]) Color.rgb(81, 30, 98) else Color.WHITE
                )
            }
        }
        return bitmap
    }
}