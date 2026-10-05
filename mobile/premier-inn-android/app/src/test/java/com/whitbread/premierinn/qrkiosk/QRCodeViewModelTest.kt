package com.whitbread.premierinn.qrkiosk

import android.graphics.Bitmap
import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.whitbread.premierinn.common.service.LogService
import com.whitbread.premierinn.common.utils.qrBitmap.QRCodeCreateBitmap
import com.whitbread.premierinn.domain.common.AppDispatchers
import com.whitbread.premierinn.domain.qrcode.GenerateQrCodeUseCase
import com.whitbread.premierinn.domain.qrcode.QrCodeResult
import com.whitbread.premierinn.utils.MainDispatcherRule
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class QRCodeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    // Sets the main coroutines dispatcher to a TestCoroutineDispatcher
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(testDispatcher)

    private val generateQrCodeUseCase = mockk<GenerateQrCodeUseCase>(relaxed = true)
    private val crashlyticsLogger = mockk<LogService>(relaxed = true)
    private val qrCodeResult = mockk<QrCodeResult>(relaxed = true)
    private val qrCodeCreateBitmap = mockk<QRCodeCreateBitmap>(relaxed = true)
    private val bitmap = mockk<Bitmap>(relaxed = true)
    private val dispatchers = AppDispatchers(
        io = testDispatcher,
        default = testDispatcher,
        main = testDispatcher
    )

    private val bookingReference = "TEST123"
    private val savedStateHandleMock: SavedStateHandle = mockk()
    private lateinit var viewModel: QRCodeViewModel

    @Before
    fun setUp() {
        every { savedStateHandleMock.get<String>(KIOSK_VIEW_INPUT) } returns bookingReference

        viewModel = QRCodeViewModel(
            savedStateHandleMock,
            generateQrCodeUseCase,
            crashlyticsLogger,
            qrCodeCreateBitmap,
            dispatchers
        )
    }

    @Test
    fun initialise() = runTest(testDispatcher) {

        viewModel.state.test {

            // Capture the initial default state
            val initialState = awaitItem()
            assertEquals(false, initialState.isLoading)

            // Trigger the first state update
            viewModel.init()
            advanceUntilIdle()

            // Capture the loading state
            val loadingState = awaitItem()
            assertEquals(true, loadingState.isLoading)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `state updates with non-null QR code on success`() = runTest(testDispatcher) {
        every { bitmap.width } returns 2000
        every { bitmap.height } returns 2000
        every { qrCodeCreateBitmap.convertToBitmap(any()) } returns bitmap
        every { generateQrCodeUseCase.invoke(any(), any(), any()) } returns qrCodeResult

        viewModel.state.test {

            // Ignore the initial state
            awaitItem()

            // Now trigger the side effects
            viewModel.init()
            advanceUntilIdle()

            // Capture the loading state
            val loadingState = awaitItem()
            assertEquals(true, loadingState.isLoading)
            assertEquals(null, loadingState.qrCode)
            assertEquals(null, loadingState.error)

            // Capture the final state after CreateReservation success
            val finalState = awaitItem()
            assertEquals(false, finalState.isLoading)
            assertEquals(bitmap, finalState.qrCode)
            assertEquals(null, finalState.error)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `state updates with error on exception`() = runTest(testDispatcher) {
        val exception = Exception("QR generation failed")
        every { crashlyticsLogger.logException(any(), any()) } just Runs
        every { generateQrCodeUseCase.invoke(any(), any(), any()) } throws exception

        viewModel.state.test {

            // Ignore the initial state
            awaitItem()

            // Now trigger the side effects
            viewModel.init()
            advanceUntilIdle()

            // Capture the loading state
            val loadingState = awaitItem()
            assertEquals(true, loadingState.isLoading)
            assertEquals(null, loadingState.qrCode)
            assertEquals(null, loadingState.error)

            // Capture the final state after CreateReservation success
            val errorState = awaitItem()
            assertEquals(false, errorState.isLoading)
            assertEquals(errorState.qrCode, null)
            assertEquals("Unexpected error: QR generation failed", errorState.error)

            verify { crashlyticsLogger.logException(
                    match { it is Exception && it.message == exception.message },
                    null
                )
            }

            cancelAndIgnoreRemainingEvents()
        }
    }
}