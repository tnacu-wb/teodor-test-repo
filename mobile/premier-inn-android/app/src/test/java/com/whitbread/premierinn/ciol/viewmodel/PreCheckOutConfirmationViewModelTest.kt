package com.whitbread.premierinn.ciol.viewmodel

import com.whitbread.premierinn.ciol.analytics.logCiolAnalytics
import com.whitbread.premierinn.ciol.mapper.convertToUiModel
import com.whitbread.premierinn.ciol.preStayModel
import com.whitbread.premierinn.common.analytics.AnalyticsConstants
import com.whitbread.premierinn.common.analytics.TrackingAnalytics
import com.whitbread.premierinn.domain.error.DataError
import com.whitbread.premierinn.domain.graphql.ciol.entity.PreCheckOutConfirmationDomain
import com.whitbread.premierinn.domain.graphql.ciol.usecase.ConfirmPreCheckOutUseCase
import com.whitbread.premierinn.domain.result.Result
import com.whitbread.premierinn.utils.TestCoroutineRule
import com.whitbread.premierinn.utils.collectEmissions
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(TestCoroutineRule::class)
class PreCheckOutConfirmationViewModelTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private val confirmPreCheckOutUseCase = mockk<ConfirmPreCheckOutUseCase>()
    private val trackingAnalytics = mockk<TrackingAnalytics>()

    private var stateResults = mutableListOf<PreCheckOutConfirmationViewModel.PreCheckOutConfirmationState>()
    private lateinit var preCheckOutConfirmationViewModel: PreCheckOutConfirmationViewModel

    @BeforeEach
    fun setUp() {
        preCheckOutConfirmationViewModel = PreCheckOutConfirmationViewModel(confirmPreCheckOutUseCase, trackingAnalytics)

        stateResults.clear()
        collectEmissions(preCheckOutConfirmationViewModel.state, stateResults, testCoroutineRule.testScheduler)
    }


    @Test
    fun `onConfirmCheckout is success when use case returns success`() = runTest {
        //GIVEN
        val basketReference = "basket_reference"
        val basketStatus = "basket_status"

        coEvery { confirmPreCheckOutUseCase.invoke(basketReference) } returns
                flowOf(Result.Success(PreCheckOutConfirmationDomain(basketStatus)))

        //WHEN
        preCheckOutConfirmationViewModel.onConfirmCheckOut(basketReference, preStayModel.convertToUiModel())
        every {
            logCiolAnalytics(
                trackingAnalytics, preStayModel.convertToUiModel(),
                screenName = AnalyticsConstants.ScreenState.LEAVE_EASY_CONFIRMATION,
                action = AnalyticsConstants.Action.LEAVE_EASY_CONFIRMATION_ACTION,
                screenType = AnalyticsConstants.Type.LEAVE_EASY_FLOW,
                isError = false
            )
        } just runs
        advanceUntilIdle()

        //THEN
        coVerify(exactly = 1) {
            confirmPreCheckOutUseCase.invoke(basketReference)
        }

        stateResults[stateResults.size - 2].apply {
            Assertions.assertTrue(this.isLoading)
            Assertions.assertFalse(this.isConfirmed)
            Assertions.assertFalse(this.isError)
        }

        stateResults.last().apply {
            Assertions.assertFalse(this.isLoading)
            Assertions.assertTrue(this.isConfirmed)
            Assertions.assertFalse(this.isError)
        }
    }

    @Test
    fun `onConfirmCheckout is error when use case returns error`() = runTest {
        //GIVEN
        val basketReference = "basket_reference"
        val errorMessage = "error"

        coEvery { confirmPreCheckOutUseCase.invoke(basketReference) } returns
                flowOf(Result.Error(DataError.Network.BaseError(errorMessage)))

        //WHEN
        preCheckOutConfirmationViewModel.onConfirmCheckOut(basketReference, preStayModel.convertToUiModel())
        every {
            logCiolAnalytics(
                trackingAnalytics, preStayModel.convertToUiModel(),
                screenName = AnalyticsConstants.ScreenState.LEAVE_EASY_CONFIRMATION,
                action = AnalyticsConstants.Action.LEAVE_EASY_CONFIRMATION_ACTION,
                screenType = AnalyticsConstants.Type.LEAVE_EASY_FLOW,
                isError = true
            )
        } just runs
        advanceUntilIdle()

        //THEN
        coVerify(exactly = 1) {
            confirmPreCheckOutUseCase.invoke(basketReference)
        }

        stateResults[stateResults.size - 2].apply {
            Assertions.assertTrue(this.isLoading)
            Assertions.assertFalse(this.isConfirmed)
            Assertions.assertFalse(this.isError)
        }

        stateResults.last().apply {
            Assertions.assertFalse(this.isLoading)
            Assertions.assertFalse(this.isConfirmed)
            Assertions.assertTrue(this.isError)
        }
    }
}
