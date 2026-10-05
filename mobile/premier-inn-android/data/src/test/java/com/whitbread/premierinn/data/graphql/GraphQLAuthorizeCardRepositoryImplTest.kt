package com.whitbread.premierinn.data.graphql

import com.whitbread.premierinn.data.common.FileDataProvider
import com.whitbread.premierinn.data.remote.graphql.GraphQLBase
import com.whitbread.premierinn.data.remote.graphql.NonRxGraphQLServicesApi
import com.whitbread.premierinn.data.remote.graphql.contracts.AuthorizeCardGraphQLContract
import com.whitbread.premierinn.domain.common.AppDispatchers
import com.whitbread.premierinn.domain.graphql.requestBodyModels.AuthorizeCardRequestBody
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
class GraphQLAuthorizeCardRepositoryImplTest {
    private val nonRxGraphQLServicesApi: NonRxGraphQLServicesApi = mockk()
    private val fileDataProvider: FileDataProvider = mockk()
    private val jsonObject: JSONObject = mockk(relaxed = true)
    private val dispatchers: AppDispatchers = mockk(relaxed = true)
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var graphQLAuthorizeCardRepository: GraphQLAuthorizeCardRepositoryImpl

    @Before
    fun setUp() {
        every { dispatchers.io } returns testDispatcher
        graphQLAuthorizeCardRepository = GraphQLAuthorizeCardRepositoryImpl(
            nonRxGraphQLServicesApi,
            fileDataProvider,
            jsonObject,
            dispatchers
        )

        val authorizeCardQuery = "authorize card query"
        every { fileDataProvider.loadFileFromAssetGQL(any<String>()) } returns authorizeCardQuery
    }

    @Test
    fun `authorizeCard is successful when api call is successful`() = runTest {
        //GIVEN
        val paymentRedirect = "payment_redirect"
        val template = "template"
        val sessionId = "session_id"
        val providerUrl = "provider_url"

        val apiReturnValue = AuthorizeCardGraphQLContract.AuthorizeCardData(
            data = AuthorizeCardGraphQLContract.Data(
                authorizeCard = AuthorizeCardGraphQLContract.AuthorizeCard(
                    paymentRedirect,
                    template,
                    sessionId,
                    providerUrl
                )
            ),
            errors = null
        )

        coEvery { nonRxGraphQLServicesApi.authorizeCard(any<String>()) } returns apiReturnValue

        //WHEN
        val authorizeCardResult = graphQLAuthorizeCardRepository.authorizeCard(AuthorizeCardRequestBody()).first()
        advanceUntilIdle()

        //THEN
        assertTrue(authorizeCardResult is Result.Success)
        assertEquals(paymentRedirect, authorizeCardResult.data?.paymentRedirect)
        assertEquals(template, authorizeCardResult.data?.template)
        assertEquals(sessionId, authorizeCardResult.data?.sessionId)
        assertEquals(providerUrl, authorizeCardResult.data?.providerUrl)
    }

    @Test
    fun `authorizeCard is error when api call is successful and result has errors`() = runTest {
        //GIVEN
        val errorMessage = "Server error"
        val errors = listOf(GraphQLBase.BaseError(listOf("preCheckIn"), "500", errorMessage))

        val apiReturnValue = AuthorizeCardGraphQLContract.AuthorizeCardData(
            data = null,
            errors = errors
        )

        coEvery { nonRxGraphQLServicesApi.authorizeCard(any<String>()) } returns apiReturnValue

        //WHEN
        val authorizeCardResult = graphQLAuthorizeCardRepository.authorizeCard(AuthorizeCardRequestBody()).first()
        advanceUntilIdle()

        //THEN
        assertTrue(authorizeCardResult is Result.Error)
        assertEquals(errorMessage, authorizeCardResult.error.message)
    }

    @Test
    fun `authorizeCard is error when api call throws error`() = runTest {
        //GIVEN
        val errorMessage = "Server error"

        coEvery { nonRxGraphQLServicesApi.authorizeCard(any<String>()) } throws IOException(errorMessage)

        //WHEN
        val authorizeCardResult = graphQLAuthorizeCardRepository.authorizeCard(AuthorizeCardRequestBody()).first()
        advanceUntilIdle()

        //THEN
        assertTrue(authorizeCardResult is Result.Error)
        assertEquals(errorMessage, authorizeCardResult.error.message)
    }
}