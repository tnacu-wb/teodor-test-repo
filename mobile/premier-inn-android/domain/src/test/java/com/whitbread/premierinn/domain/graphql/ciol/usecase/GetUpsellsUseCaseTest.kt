package com.whitbread.premierinn.domain.graphql.ciol.usecase

import com.whitbread.premierinn.domain.continentalBreakfast
import com.whitbread.premierinn.domain.createPackagesPackagesDomain
import com.whitbread.premierinn.domain.error.DataError
import com.whitbread.premierinn.domain.graphql.ciol.repository.GraphQLPackagesRepository
import com.whitbread.premierinn.domain.graphql.requestBodyModels.HotelPackagesRequestBody
import com.whitbread.premierinn.domain.mealForKids
import com.whitbread.premierinn.domain.premierInnBreakfast
import com.whitbread.premierinn.domain.result.Result
import com.whitbread.premierinn.domain.roomSelection
import com.whitbread.premierinn.domain.ultimateWiFi
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class GetUpsellsUseCaseTest {
    private val packagesRepository = mockk<GraphQLPackagesRepository>()

    private val getUpsellsUseCase = GetUpsellsUseCase(packagesRepository)

    @Test
    fun `GIVEN no upsells were added on booking, WHEN packages retrieval is returning the packages, THEN the result will contain only the available upsells`() = runTest {
        // GIVEN
        val packagesRequestBody = mockk<HotelPackagesRequestBody>()
        val packagesDomain = createPackagesPackagesDomain().copy(roomSelection = null)
        val expectedAvailableUpsells = mutableListOf(premierInnBreakfast, continentalBreakfast, mealForKids, ultimateWiFi)
        coEvery { packagesRepository.getPackages(any()) } returns flowOf(Result.Success(packagesDomain))

        // WHEN
        val result = getUpsellsUseCase(packagesRequestBody).toList()
        advanceUntilIdle()

        // THEN
        assertTrue { result.first() is Result.Success }
        (result.first() as Result.Success).data.apply {
            assertEquals(null, preselectedRoomSelections)
            assertEquals(expectedAvailableUpsells, availableUpsells)
        }
    }

    @Test
    fun `GIVEN premierInnBreakfast was added on booking, WHEN packages retrieval is returning the packages, THEN the result will contain the preselected room selections`() = runTest {
        // GIVEN
        val packagesRequestBody = mockk<HotelPackagesRequestBody>()
        val packagesDomain = createPackagesPackagesDomain()
        val expectedRoomSelections = mutableListOf(roomSelection)
        coEvery { packagesRepository.getPackages(any()) } returns flowOf(Result.Success(packagesDomain))

        // WHEN
        val result = getUpsellsUseCase(packagesRequestBody).toList()
        advanceUntilIdle()

        // THEN
        assertTrue { result.first() is Result.Success }
        (result.first() as Result.Success).data.apply {
            assertEquals(expectedRoomSelections, preselectedRoomSelections)
        }
    }

    @Test
    fun `GIVEN premierInnBreakfast was added on booking, WHEN packages retrieval is returning the packages, THEN the returned availableUpsells will reflect the preselectedNoOfSelections`() = runTest {
        // GIVEN
        val packagesRequestBody = mockk<HotelPackagesRequestBody>()
        val packagesDomain = createPackagesPackagesDomain()
        val expectedAvailableUpsells = mutableListOf(
            premierInnBreakfast.copy(preselectedNoOfSelections = 2),
            continentalBreakfast,
            mealForKids.copy(preselectedNoOfSelections = 2),
            ultimateWiFi
        )
        coEvery { packagesRepository.getPackages(any()) } returns flowOf(Result.Success(packagesDomain))

        // WHEN
        val result = getUpsellsUseCase(packagesRequestBody).toList()
        advanceUntilIdle()

        // THEN
        assertTrue { result.first() is Result.Success }
        (result.first() as Result.Success).data.apply {
            assertEquals(expectedAvailableUpsells, availableUpsells)
        }
    }

    @Test
    fun `GIVEN no upsells were added on booking, WHEN packages retrieval is returning an error, THEN the error will be propagated further`() = runTest {
        // GIVEN
        val packagesRequestBody = mockk<HotelPackagesRequestBody>()
        val expectedError = DataError.Network.GraphQlError("Error message")
        coEvery { packagesRepository.getPackages(any()) } returns flowOf(Result.Error(expectedError))

        // WHEN
        val result = getUpsellsUseCase(packagesRequestBody).toList()
        advanceUntilIdle()

        // THEN
        assertTrue { result.first() is Result.Error }
        (result.first() as Result.Error).apply {
            assertEquals(expectedError, error)
        }
    }
}
