package com.whitbread.premierinn.data.graphql

import com.whitbread.premierinn.data.common.FileDataProvider
import com.whitbread.premierinn.data.remote.graphql.GraphQLBase
import com.whitbread.premierinn.data.remote.graphql.NonRxGraphQLServicesApi
import com.whitbread.premierinn.data.remote.graphql.contracts.ConfirmPreCheckOutGraphQLContract
import com.whitbread.premierinn.domain.common.AppDispatchers
import com.whitbread.premierinn.domain.result.Result
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.json.JSONObject
import org.junit.Before
import org.junit.Test
import java.io.IOException
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class GraphQLPreCheckOutConfirmationRepositoryImplTest {
    private val nonRxGraphQLServicesApi: NonRxGraphQLServicesApi = mockk()
    private val fileDataProvider: FileDataProvider = mockk()
    private val jsonObject: JSONObject = mockk(relaxed = true)
    private val dispatchers: AppDispatchers = mockk(relaxed = true)
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var graphQLPreCheckOutConfirmationRepository: GraphQLPreCheckOutConfirmationRepositoryImpl

    @Before
    fun setUp() {
        every { dispatchers.io } returns testDispatcher
        graphQLPreCheckOutConfirmationRepository = GraphQLPreCheckOutConfirmationRepositoryImpl(
            nonRxGraphQLServicesApi,
            fileDataProvider,
            jsonObject,
            dispatchers
        )

        val preCheckOutConfirmationQuery = "pre check-out confirmation query"
        every { fileDataProvider.loadFileFromAssetGQL(any<String>()) } returns preCheckOutConfirmationQuery
    }

    @Test
    fun `preCheckOutConfirmation is successful when api call is successful`() = runTest {
        //GIVEN
        val basketStatus = "basket_status"
        val basketReference = "basket_reference_id"
        val apiReturnValue = ConfirmPreCheckOutGraphQLContract.ConfirmPreCheckOutData(
            data = ConfirmPreCheckOutGraphQLContract.Data(
                preCheckOutConfirmation = ConfirmPreCheckOutGraphQLContract.PreCheckOutConfirmation(
                    basketStatus = basketStatus
                )
            ),
            errors = null
        )

        coEvery { nonRxGraphQLServicesApi.confirmPreCheckOut(any<String>()) } returns apiReturnValue

        //WHEN
        val confirmPreCheckoutResult = graphQLPreCheckOutConfirmationRepository.confirmPreCheckOut(basketReference).first()
        advanceUntilIdle()

        //THEN
        assertTrue(confirmPreCheckoutResult is Result.Success)
        assertEquals(basketStatus, confirmPreCheckoutResult.data?.basketStatus)
    }

    @Test
    fun `preCheckOutConfirmation is error when api call is successful and result has errors`() = runTest {
        //GIVEN
        val errorMessage = "Server error"
        val basketReference = "basket_reference_id"
        val errors = listOf(GraphQLBase.BaseError(listOf("preCheckOutConfirmation"), "500", errorMessage))

        val apiReturnValue = ConfirmPreCheckOutGraphQLContract.ConfirmPreCheckOutData(
            data = null,
            errors = errors
        )

        coEvery { nonRxGraphQLServicesApi.confirmPreCheckOut(any<String>()) } returns apiReturnValue

        //WHEN
        val confirmPreCheckoutResult = graphQLPreCheckOutConfirmationRepository.confirmPreCheckOut(basketReference).first()
        advanceUntilIdle()

        //THEN
        assertTrue(confirmPreCheckoutResult is Result.Error)
        assertEquals(errorMessage, confirmPreCheckoutResult.error.message)
    }

    @Test
    fun `preCheckOutConfirmation is error when api call throws error`() = runTest {
        //GIVEN
        val errorMessage = "Server error"
        val basketReference = "basket_reference_id"

        coEvery { nonRxGraphQLServicesApi.confirmPreCheckOut(any<String>()) } throws IOException(errorMessage)

        //WHEN
        val confirmPreCheckoutResult = graphQLPreCheckOutConfirmationRepository.confirmPreCheckOut(basketReference).first()
        advanceUntilIdle()

        //THEN
        assertTrue(confirmPreCheckoutResult is Result.Error)
        assertEquals(errorMessage, confirmPreCheckoutResult.error.message)
    }
}
