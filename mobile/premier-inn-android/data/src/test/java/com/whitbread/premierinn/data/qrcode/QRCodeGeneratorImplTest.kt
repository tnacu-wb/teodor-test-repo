package com.whitbread.premierinn.data.qrcode

import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test

class QRCodeGeneratorImplTest {

    private lateinit var mockBitMatrix: BitMatrix

    @Before
    fun setUp() {
        // Mock BitMatrix and QRCodeWriter constructor
        mockBitMatrix = mockk()
        mockkConstructor(QRCodeWriter::class)

        // Stub the BitMatrix behavior
        every { mockBitMatrix.get(0, 0) } returns true
        every { mockBitMatrix.get(0, 1) } returns false
        every { mockBitMatrix.get(1, 0) } returns false
        every { mockBitMatrix.get(1, 1) } returns true
    }

    @Test
    fun `generate returns correct QrCodeResult`() {
        // Arrange
        val text = "Test QR Code"
        val width = 2
        val height = 2

        every {
            anyConstructed<QRCodeWriter>().encode(text, BarcodeFormat.QR_CODE, width, height, any())
        } returns mockBitMatrix

        val qrCodeGenerator = QRCodeGeneratorImpl()

        val result = qrCodeGenerator.generate(text, width, height)

        val expectedPixelData = arrayOf(
            arrayOf(true, false),
            arrayOf(false, true)
        )
        assertEquals(width, result.width)
        assertEquals(height, result.height)
        assertTrue(expectedPixelData.contentDeepEquals(result.pixelData))

        verify(exactly = 1) {
            anyConstructed<QRCodeWriter>().encode(text, BarcodeFormat.QR_CODE, width, height, mapOf(
                EncodeHintType.CHARACTER_SET to "UTF-8"))
        }
    }
}