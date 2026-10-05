package com.whitbread.premierinn.domain.qrcode

data class QrCodeResult(
    val pixelData: Array<Array<Boolean>>,
    val width: Int,
    val height: Int
)
