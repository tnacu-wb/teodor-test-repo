package com.whitbread.premierinn.data.graphql

import com.whitbread.premierinn.data.common.FileDataProvider
import com.whitbread.premierinn.data.remote.graphql.GraphQLBase
import com.whitbread.premierinn.data.remote.graphql.NonRxGraphQLServicesApi
import com.whitbread.premierinn.data.remote.graphql.contracts.AttachFileToReservationGraphQLContract
import com.whitbread.premierinn.domain.common.AppDispatchers
import com.whitbread.premierinn.domain.graphql.requestBodyModels.AttachFileToReservationRequestBody
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
class GraphQLAttachFileToReservationRepositoryImplTest {
    private val nonRxGraphQLServicesApi: NonRxGraphQLServicesApi = mockk()
    private val fileDataProvider: FileDataProvider = mockk()
    private val jsonObject: JSONObject = mockk(relaxed = true)
    private val dispatchers: AppDispatchers = mockk(relaxed = true)
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var graphQLAttachFileToReservationRepository: GraphQLAttachFileToReservationRepositoryImpl

    @Before
    fun setUp() {
        every { dispatchers.io } returns testDispatcher
        graphQLAttachFileToReservationRepository = GraphQLAttachFileToReservationRepositoryImpl(
            nonRxGraphQLServicesApi,
            fileDataProvider,
            jsonObject,
            dispatchers
        )

        val attachFileToReservationQuery = "attach file to reservation query"
        every { fileDataProvider.loadFileFromAssetGQL(any<String>()) } returns attachFileToReservationQuery
    }

    @Test
    fun `attachFileToReservation is successful when api call is successful`() = runTest {
        //GIVEN
        val statusValue = "Success"
        val messageValue = "Call is success"
        val attachFileToReservationRequestBody = AttachFileToReservationRequestBody(
            fileName = "file_name",
            reservationId = "reservation_id",
            hotelId = "hotel_id",
            fileAttachment = "file_attachment"
        )
        val apiReturnValue = AttachFileToReservationGraphQLContract.AttachFileToReservationData(
            data = AttachFileToReservationGraphQLContract.Data(
                attachFileToReservation = AttachFileToReservationGraphQLContract.AttachFileToReservation(
                    status = statusValue,
                    message = messageValue
                )
            ),
            errors = null
        )

        coEvery { nonRxGraphQLServicesApi.attachFileToReservation(any<String>()) } returns apiReturnValue

        //WHEN
        val attachFileToReservationResult =
            graphQLAttachFileToReservationRepository.attachFileToReservation(attachFileToReservationRequestBody).first()
        advanceUntilIdle()

        //THEN
        assertTrue(attachFileToReservationResult is Result.Success)
        assertEquals(statusValue, attachFileToReservationResult.data?.status)
        assertEquals(messageValue, attachFileToReservationResult.data?.message)
    }

    @Test
    fun `attachFileToReservation is error when api call is successful and result status is error`() = runTest {
        //GIVEN
        val statusValue = "Error"
        val messageValue = "Call is error"
        val attachFileToReservationRequestBody = AttachFileToReservationRequestBody(
            fileName = "file_name",
            reservationId = "reservation_id",
            hotelId = "hotel_id",
            fileAttachment = "file_attachment"
        )
        val apiReturnValue = AttachFileToReservationGraphQLContract.AttachFileToReservationData(
            data = AttachFileToReservationGraphQLContract.Data(
                attachFileToReservation = AttachFileToReservationGraphQLContract.AttachFileToReservation(
                    status = statusValue,
                    message = messageValue
                )
            ),
            errors = null
        )

        coEvery { nonRxGraphQLServicesApi.attachFileToReservation(any<String>()) } returns apiReturnValue

        //WHEN
        val attachFileToReservationResult =
            graphQLAttachFileToReservationRepository.attachFileToReservation(attachFileToReservationRequestBody).first()
        advanceUntilIdle()

        //THEN
        assertTrue(attachFileToReservationResult is Result.Error)
        assertEquals(messageValue, attachFileToReservationResult.error.message)
    }

    @Test
    fun `attachFileToReservation is error when api call is successful and result has errors`() = runTest {
        //GIVEN
        val errorMessage = "Server error"
        val errors = listOf(GraphQLBase.BaseError(listOf("attachFileToReservation"), "500", errorMessage))
        val attachFileToReservationRequestBody = AttachFileToReservationRequestBody(
            fileName = "file_name",
            reservationId = "reservation_id",
            hotelId = "hotel_id",
            fileAttachment = "file_attachment"
        )
        val apiReturnValue = AttachFileToReservationGraphQLContract.AttachFileToReservationData(
            data = null,
            errors = errors
        )

        coEvery { nonRxGraphQLServicesApi.attachFileToReservation(any<String>()) } returns apiReturnValue

        //WHEN
        val attachFileToReservationResult =
            graphQLAttachFileToReservationRepository.attachFileToReservation(attachFileToReservationRequestBody).first()
        advanceUntilIdle()

        //THEN
        assertTrue(attachFileToReservationResult is Result.Error)
        assertEquals(errorMessage, attachFileToReservationResult.error.message)
    }

    @Test
    fun `attachFileToReservation is error when api call throws error`() = runTest {
        //GIVEN
        val errorMessage = "Server error"
        val attachFileToReservationRequestBody = AttachFileToReservationRequestBody(
            fileName = "file_name",
            reservationId = "reservation_id",
            hotelId = "hotel_id",
            fileAttachment = "file_attachment"
        )

        coEvery { nonRxGraphQLServicesApi.attachFileToReservation(any<String>()) } throws IOException(errorMessage)

        //WHEN
        val attachFileToReservationResult =
            graphQLAttachFileToReservationRepository.attachFileToReservation(attachFileToReservationRequestBody).first()
        advanceUntilIdle()

        //THEN
        assertTrue(attachFileToReservationResult is Result.Error)
        assertEquals(errorMessage, attachFileToReservationResult.error.message)
    }
}
