package com.whitbread.premierinn.domain.qrcode

interface QRCodeGenerator {
    fun generate(text: String, width: Int, height: Int): QrCodeResult
}