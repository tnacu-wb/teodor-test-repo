package com.whitbread.premierinn.criteria.model

import com.whitbread.premierinn.criteria.view.CotState
import com.whitbread.premierinn.criteria.view.NumberSelectorViewInput
import com.whitbread.premierinn.domain.common.RoomType
import io.mockk.every
import io.mockk.mockk
import org.junit.Test
import kotlin.test.assertEquals

class RoomRulesTest {

    val adultsViewInputMock: NumberSelectorViewInput = mockk()
    val childrenViewInputMock: NumberSelectorViewInput = mockk()
    val infantsViewInputMock: NumberSelectorViewInput = mockk()

    @Test
    fun `when in search selected room is single, one adult, infant and cot are selected then change room type to double`() {
        every { adultsViewInputMock.value() } returns 1
        every { childrenViewInputMock.value() } returns 0
        every { infantsViewInputMock.value() } returns 1

        val roomConfig = RoomConfiguration.builder()
                .roomType(RoomType.SINGLE)
                .cot(CotState.SELECTED)
                .adults(adultsViewInputMock)
                .children(childrenViewInputMock)
                .infants(infantsViewInputMock)
                .number(0)
                .autobuild()
        val result = RoomRules.apply(roomConfig, false)
        assertEquals(RoomType.DOUBLE, result.roomType())
    }

    @Test
    fun `when in search selected room is single, one adult and one child then change room type to family`() {
        every { adultsViewInputMock.value() } returns 1
        every { childrenViewInputMock.value() } returns 1
        every { infantsViewInputMock.value() } returns 0

        val roomConfig = RoomConfiguration.builder()
                .roomType(RoomType.SINGLE)
                .cot(CotState.NOT_SELECTED)
                .adults(adultsViewInputMock)
                .children(childrenViewInputMock)
                .infants(infantsViewInputMock)
                .number(0)
                .autobuild()
        val result = RoomRules.apply(roomConfig, false)
        assertEquals(RoomType.FAMILY, result.roomType())
    }

    @Test
    fun `when in search selected room is accessible, one adult, infant and cot are selected then room is still accessible but cot is not available`() {
        every { adultsViewInputMock.value() } returns 1
        every { childrenViewInputMock.value() } returns 0
        every { infantsViewInputMock.value() } returns 1

        val roomConfig = RoomConfiguration.builder()
                .roomType(RoomType.ACCESSIBLE)
                .cot(CotState.SELECTED)
                .adults(adultsViewInputMock)
                .children(childrenViewInputMock)
                .infants(infantsViewInputMock)
                .number(0)
                .autobuild()
        val result = RoomRules.apply(roomConfig, false)
        assertEquals(RoomType.ACCESSIBLE, result.roomType())
        assertEquals(CotState.NOT_AVAILABLE, result.cot())
    }

    @Test
    fun `when in preferences selected room is accessible, one adult, infant and cot are selected then room is changed to double and cot is still selected`() {
        every { adultsViewInputMock.value() } returns 1
        every { childrenViewInputMock.value() } returns 0
        every { infantsViewInputMock.value() } returns 1

        val roomConfig = RoomConfiguration.builder()
                .roomType(RoomType.ACCESSIBLE)
                .cot(CotState.SELECTED)
                .adults(adultsViewInputMock)
                .children(childrenViewInputMock)
                .infants(infantsViewInputMock)
                .number(0)
                .autobuild()
        val result = RoomRules.apply(roomConfig, true)
        assertEquals(RoomType.DOUBLE, result.roomType())
        assertEquals(CotState.SELECTED, result.cot())
    }

    @Test
    fun `when in search selected room is twin and one adult then change room type to double`() {
        every { adultsViewInputMock.value() } returns 1
        every { childrenViewInputMock.value() } returns 0
        every { infantsViewInputMock.value() } returns 0

        val roomConfig = RoomConfiguration.builder()
                .roomType(RoomType.TWIN)
                .cot(CotState.NOT_SELECTED)
                .adults(adultsViewInputMock)
                .children(childrenViewInputMock)
                .infants(infantsViewInputMock)
                .number(0)
                .autobuild()
        val result = RoomRules.apply(roomConfig, false)
        assertEquals(RoomType.DOUBLE, result.roomType())
    }
}