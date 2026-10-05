package com.whitbread.premierinn.data.graphql

import com.whitbread.premierinn.data.common.FileDataProvider
import com.whitbread.premierinn.data.remote.graphql.GraphQLBase
import com.whitbread.premierinn.data.remote.graphql.NonRxGraphQLServicesApi
import com.whitbread.premierinn.data.remote.graphql.contracts.PreCheckInGraphQLContract
import com.whitbread.premierinn.domain.common.AppDispatchers
import com.whitbread.premierinn.domain.graphql.requestBodyModels.PreCheckInRequestBody
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

@OptIn(ExperimentalCoroutinesApi::class)
class GraphQLPreCheckInRepositoryImplTest {
    private val nonRxGraphQLServicesApi: NonRxGraphQLServicesApi = mockk()
    private val fileDataProvider: FileDataProvider = mockk()
    private val jsonObject: JSONObject = mockk(relaxed = true)
    private val dispatchers: AppDispatchers = mockk(relaxed = true)
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var graphQLPreCheckInRepository: GraphQLPreCheckInRepositoryImpl

    @Before
    fun setUp() {
        every { dispatchers.io } returns testDispatcher
        graphQLPreCheckInRepository = GraphQLPreCheckInRepositoryImpl(
            nonRxGraphQLServicesApi,
            fileDataProvider,
            jsonObject,
            dispatchers
        )

        val preCheckInQuery = "pre check-in query"
        every { fileDataProvider.loadFileFromAssetGQL(any<String>()) } returns preCheckInQuery
    }

    @Test
    fun `preCheckIn is successful when api call is successful`() = runTest {
        //GIVEN
        val statusValue = "Success"
        val messageValue = "Call is success"

        val apiReturnValue = PreCheckInGraphQLContract.PreCheckInData(
            data = PreCheckInGraphQLContract.Data(
                preCheckInStatus = PreCheckInGraphQLContract.PreCheckInStatus(
                    status = statusValue,
                    message = messageValue
                )
            ),
            errors = null
        )

        coEvery { nonRxGraphQLServicesApi.preCheckIn(any<String>()) } returns apiReturnValue

        //WHEN
        val preCheckInResult = graphQLPreCheckInRepository.preCheckIn(PreCheckInRequestBody()).first()
        advanceUntilIdle()

        //THEN
        kotlin.test.assertTrue(preCheckInResult is Result.Success)
        kotlin.test.assertEquals(statusValue, preCheckInResult.data?.status)
        kotlin.test.assertEquals(messageValue, preCheckInResult.data?.message)
    }

    @Test
    fun `preCheckIn is error when api call is successful and result status is error`() = runTest {
        //GIVEN
        val statusValue = "Error"
        val messageValue = "Call is error"

        val apiReturnValue = PreCheckInGraphQLContract.PreCheckInData(
            data = PreCheckInGraphQLContract.Data(
                preCheckInStatus = PreCheckInGraphQLContract.PreCheckInStatus(
                    status = statusValue,
                    message = messageValue
                )
            ),
            errors = null
        )

        coEvery { nonRxGraphQLServicesApi.preCheckIn(any<String>()) } returns apiReturnValue

        //WHEN
        val preCheckInResult = graphQLPreCheckInRepository.preCheckIn(PreCheckInRequestBody()).first()
        advanceUntilIdle()

        //THEN
        kotlin.test.assertTrue(preCheckInResult is Result.Error)
        kotlin.test.assertEquals(messageValue, preCheckInResult.error.message)
    }

    @Test
    fun `preCheckIn is error when api call is successful and result has errors`() = runTest {
        //GIVEN
        val errorMessage = "Server error"
        val errors = listOf(GraphQLBase.BaseError(listOf("preCheckIn"), "500", errorMessage))

        val apiReturnValue = PreCheckInGraphQLContract.PreCheckInData(
            data = null,
            errors = errors
        )

        coEvery { nonRxGraphQLServicesApi.preCheckIn(any<String>()) } returns apiReturnValue

        //WHEN
        val preCheckInResult = graphQLPreCheckInRepository.preCheckIn(PreCheckInRequestBody()).first()
        advanceUntilIdle()

        //THEN
        kotlin.test.assertTrue(preCheckInResult is Result.Error)
        kotlin.test.assertEquals(errorMessage, preCheckInResult.error.message)
    }

    @Test
    fun `preCheckIn is error when api call throws error`() = runTest {
        //GIVEN
        val errorMessage = "Server error"

        coEvery { nonRxGraphQLServicesApi.preCheckIn(any<String>()) } throws IOException(errorMessage)

        //WHEN
        val preCheckInResult = graphQLPreCheckInRepository.preCheckIn(PreCheckInRequestBody()).first()
        advanceUntilIdle()

        //THEN
        kotlin.test.assertTrue(preCheckInResult is Result.Error)
        kotlin.test.assertEquals(errorMessage, preCheckInResult.error.message)
    }
}