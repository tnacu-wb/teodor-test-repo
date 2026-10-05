package com.whitbread.premierinn.domain.graphql.amend.usecase

import com.whitbread.premierinn.domain.common.hoteldetails.entity.AncillaryCloseOutItem
import com.whitbread.premierinn.domain.continentalBreakfast
import com.whitbread.premierinn.domain.createDataPackagesDomain
import com.whitbread.premierinn.domain.createPackagesPackagesDomain
import com.whitbread.premierinn.domain.error.DataError
import com.whitbread.premierinn.domain.graphql.amend.entity.PackagesAndAncillaryCloseoutDomain
import com.whitbread.premierinn.domain.graphql.amend.repository.GraphQLAmendRepository
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
class GetUpsellsAndAncillaryCloseoutUseCaseTest {

    private val amendRepository = mockk<GraphQLAmendRepository>()

    private val getUpsellsAndAncillaryCloseoutUseCase = GetUpsellsAndAncillaryCloseoutUseCase(amendRepository)

    private val ancillaryCloseOutItems = listOf(
        AncillaryCloseOutItem(
            startDate = "25/12/2026",
            endDate = "31/12/2026",
            upsellCodes = "BFADBF,MDP"
        )
    )

    @Test
    fun `GIVEN no upsells were added on booking, WHEN packages and hotel info retrieval is successful, THEN the result will contain only the available upsells and ancillary closeout`() = runTest {
        // GIVEN
        val packagesRequestBody = mockk<HotelPackagesRequestBody>()
        val packagesPackagesDomain = createPackagesPackagesDomain().copy(roomSelection = null)
        val packagesDomain = createDataPackagesDomain().copy(packages = packagesPackagesDomain)
        val packagesAndAncillaryDomain = PackagesAndAncillaryCloseoutDomain(
            packages = packagesDomain,
            ancillaryCloseOutItems = ancillaryCloseOutItems
        )
        val expectedAvailableUpsells = mutableListOf(premierInnBreakfast, continentalBreakfast, mealForKids, ultimateWiFi)
        coEvery { amendRepository.getPackagesAndAncillariesCloseoutInfo(any()) } returns flowOf(Result.Success(packagesAndAncillaryDomain))

        // WHEN
        val result = getUpsellsAndAncillaryCloseoutUseCase(packagesRequestBody).toList()
        advanceUntilIdle()

        // THEN
        assertTrue { result.first() is Result.Success }
        (result.first() as Result.Success).data.apply {
            assertEquals(null, preselectedRoomSelections)
            assertEquals(expectedAvailableUpsells, availableUpsells)
            assertEquals(ancillaryCloseOutItems, ancillaryCloseout)
        }
    }

    @Test
    fun `GIVEN premierInnBreakfast was added on booking, WHEN packages and hotel info retrieval is successful, THEN the result will contain the preselected room selections`() = runTest {
        // GIVEN
        val packagesRequestBody = mockk<HotelPackagesRequestBody>()
        val packagesDomain = createDataPackagesDomain()
        val packagesAndAncillaryDomain = PackagesAndAncillaryCloseoutDomain(
            packages = packagesDomain,
            ancillaryCloseOutItems = ancillaryCloseOutItems
        )
        val expectedRoomSelections = mutableListOf(roomSelection)
        coEvery { amendRepository.getPackagesAndAncillariesCloseoutInfo(any()) } returns flowOf(Result.Success(packagesAndAncillaryDomain))

        // WHEN
        val result = getUpsellsAndAncillaryCloseoutUseCase(packagesRequestBody).toList()
        advanceUntilIdle()

        // THEN
        assertTrue { result.first() is Result.Success }
        (result.first() as Result.Success).data.apply {
            assertEquals(expectedRoomSelections, preselectedRoomSelections)
        }
    }

    @Test
    fun `GIVEN premierInnBreakfast was added on booking, WHEN packages and hotel info retrieval is successful, THEN the returned availableUpsells will reflect the preselectedNoOfSelections`() = runTest {
        // GIVEN
        val packagesRequestBody = mockk<HotelPackagesRequestBody>()
        val packagesDomain = createDataPackagesDomain()
        val packagesAndAncillaryDomain = PackagesAndAncillaryCloseoutDomain(
            packages = packagesDomain,
            ancillaryCloseOutItems = ancillaryCloseOutItems
        )
        val expectedAvailableUpsells = mutableListOf(
            premierInnBreakfast.copy(preselectedNoOfSelections = 2),
            continentalBreakfast,
            mealForKids.copy(preselectedNoOfSelections = 2),
            ultimateWiFi
        )
        coEvery { amendRepository.getPackagesAndAncillariesCloseoutInfo(any()) } returns flowOf(Result.Success(packagesAndAncillaryDomain))

        // WHEN
        val result = getUpsellsAndAncillaryCloseoutUseCase(packagesRequestBody).toList()
        advanceUntilIdle()

        // THEN
        assertTrue { result.first() is Result.Success }
        (result.first() as Result.Success).data.apply {
            assertEquals(expectedAvailableUpsells, availableUpsells)
        }
    }

    @Test
    fun `GIVEN packages and hotel info retrieval fails, WHEN use case is invoked, THEN the error will be propagated further`() = runTest {
        // GIVEN
        val packagesRequestBody = mockk<HotelPackagesRequestBody>()
        val expectedError = DataError.Network.GraphQlError("Error message")
        coEvery { amendRepository.getPackagesAndAncillariesCloseoutInfo(any()) } returns flowOf(Result.Error(expectedError))

        // WHEN
        val result = getUpsellsAndAncillaryCloseoutUseCase(packagesRequestBody).toList()
        advanceUntilIdle()

        // THEN
        assertTrue { result.first() is Result.Error }
        (result.first() as Result.Error).apply {
            assertEquals(expectedError, error)
        }
    }

    @Test
    fun `GIVEN empty ancillary closeout list, WHEN packages and hotel info retrieval is successful, THEN the result will contain empty ancillary closeout`() = runTest {
        // GIVEN
        val packagesRequestBody = mockk<HotelPackagesRequestBody>()
        val packagesPackagesDomain = createPackagesPackagesDomain().copy(roomSelection = null)
        val packagesDomain = createDataPackagesDomain().copy(packages = packagesPackagesDomain)
        val packagesAndAncillaryDomain = PackagesAndAncillaryCloseoutDomain(
            packages = packagesDomain,
            ancillaryCloseOutItems = emptyList()
        )
        coEvery { amendRepository.getPackagesAndAncillariesCloseoutInfo(any()) } returns flowOf(Result.Success(packagesAndAncillaryDomain))

        // WHEN
        val result = getUpsellsAndAncillaryCloseoutUseCase(packagesRequestBody).toList()
        advanceUntilIdle()

        // THEN
        assertTrue { result.first() is Result.Success }
        (result.first() as Result.Success).data.apply {
            assertEquals(emptyList(), ancillaryCloseout)
        }
    }

    @Test
    fun `GIVEN multiple ancillary closeout items, WHEN packages and hotel info retrieval is successful, THEN all ancillary closeout items are returned`() = runTest {
        // GIVEN
        val packagesRequestBody = mockk<HotelPackagesRequestBody>()
        val packagesPackagesDomain = createPackagesPackagesDomain().copy(roomSelection = null)
        val packagesDomain = createDataPackagesDomain().copy(packages = packagesPackagesDomain)
        val multipleAncillaryCloseOutItems = listOf(
            AncillaryCloseOutItem(
                startDate = "25/12/2026",
                endDate = "31/12/2026",
                upsellCodes = "BFADBF,MDP"
            ),
            AncillaryCloseOutItem(
                startDate = "25/1/2027",
                endDate = "31/12/2027",
                upsellCodes = "BFADCT"
            )
        )
        val packagesAndAncillaryDomain = PackagesAndAncillaryCloseoutDomain(
            packages = packagesDomain,
            ancillaryCloseOutItems = multipleAncillaryCloseOutItems
        )
        coEvery { amendRepository.getPackagesAndAncillariesCloseoutInfo(any()) } returns flowOf(Result.Success(packagesAndAncillaryDomain))

        // WHEN
        val result = getUpsellsAndAncillaryCloseoutUseCase(packagesRequestBody).toList()
        advanceUntilIdle()

        // THEN
        assertTrue { result.first() is Result.Success }
        (result.first() as Result.Success).data.apply {
            assertEquals(2, ancillaryCloseout.size)
            assertEquals(multipleAncillaryCloseOutItems, ancillaryCloseout)
        }
    }
}

