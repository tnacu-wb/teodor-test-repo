package com.whitbread.premierinn.amend.amendguestsrooms

import com.whitbread.premierinn.amend.AmendStringProvider
import com.whitbread.premierinn.common.AsyncResult
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManagerImpl
import com.whitbread.premierinn.domain.common.Guest
import com.whitbread.premierinn.domain.common.PriceDomain
import com.whitbread.premierinn.domain.common.RatePlanOpera
import com.whitbread.premierinn.domain.common.RoomCriteria
import com.whitbread.premierinn.domain.reservation.usecase.AddRoomUseCase
import com.whitbread.premierinn.domain.reservation.usecase.StoreUpdatedAmendedReservation
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class AmendGuestsRoomStateTest {

    @MockK
    lateinit var amendGuestsRoomState: AmendGuestsRoomState
    private val guestResult: AsyncResult.Success<Guest> = mockk()
    private val roomCriteriaUpdateResult: AsyncResult.Success<RoomCriteria> = mockk()
    private val stringProvider: AmendStringProvider = mockk()
    private val storage: SimplePersistenceManagerImpl = mockk()
    private val deviceLocaleProvider: DeviceLocaleProvider = mockk()

    @Before
    fun setUp() {
        amendGuestsRoomState = AmendGuestsRoomState(
                guestResult = guestResult,
                roomCriteriaUpdateResult = roomCriteriaUpdateResult,
                updateState = null,
                addRoomState = null,
                titleOptions = emptyList(),
                stringResourceProvider = stringProvider,
                storage = storage,
                buttonText = "Update",
                deviceLocaleProvider = deviceLocaleProvider)
    }

    @Test
    fun `When room is added Then get total cost from addRoomState`() {
        amendGuestsRoomState = amendGuestsRoomState.copy(addRoomState = AddRoomUseCase.AddARoomState.AvailabilityUpdated(
            RatePlanOpera(
                "RT315",
                "Flex",
                "cell Code",
                PriceDomain(amount = 1400f, currency = "GBP"),
                null,
                emptyList(),
                emptyList(),
                emptyList(),
                emptyList()),
            null)
        )

        assertEquals(1400f, amendGuestsRoomState.getRatePlan()?.totalCost?.amount)
    }

    @Test
    fun `When guest or room is amended Then get total cost from updateState`() {
        amendGuestsRoomState = amendGuestsRoomState.copy(updateState = StoreUpdatedAmendedReservation.UpdateState.AvailabilityUpdated(
            RatePlanOpera(
                "RT315",
                "Flex",
                "cell Code",
                PriceDomain(amount = 1000f, currency = "GBP"),
                null,
                emptyList(),
                emptyList(),
                emptyList(),
                emptyList()),
            null)
        )

        assertEquals(1000f, amendGuestsRoomState.getRatePlan()?.totalCost?.amount)
    }
}