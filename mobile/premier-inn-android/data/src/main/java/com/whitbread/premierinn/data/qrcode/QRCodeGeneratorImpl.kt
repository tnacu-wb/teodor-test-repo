package com.whitbread.premierinn.data.qrcode

import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.whitbread.premierinn.domain.qrcode.QRCodeGenerator
import com.whitbread.premierinn.domain.qrcode.QrCodeResult

class QRCodeGeneratorImpl : QRCodeGenerator {
    override fun generate(text: String, width: Int, height: Int): QrCodeResult {
        val qrCodeWriter = QRCodeWriter()
        val hints = mapOf(EncodeHintType.CHARACTER_SET to "UTF-8")
        val bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints)

        val pixelData = Array(width) { x ->
            Array(height) { y ->
                bitMatrix.get(x, y)
            }
        }

        return QrCodeResult(pixelData, width, height)
    }
}