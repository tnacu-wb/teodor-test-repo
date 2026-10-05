package com.whitbread.premierinn.domain.qrcode

import javax.inject.Inject

class GenerateQrCodeUseCase @Inject constructor (private val qrCodeGenerator: QRCodeGenerator) {
    operator fun invoke(text: String, width: Int, height: Int): QrCodeResult {
        return qrCodeGenerator.generate(text, width, height)
    }
}
