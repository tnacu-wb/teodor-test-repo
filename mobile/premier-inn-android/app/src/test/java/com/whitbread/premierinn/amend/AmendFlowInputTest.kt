package com.whitbread.premierinn.amend

import com.whitbread.premierinn.common.model.UpsellFixture
import com.whitbread.premierinn.domain.common.GBP
import com.whitbread.premierinn.domain.common.PriceDomain
import com.whitbread.premierinn.domain.common.Upsell
import org.junit.Test
import kotlin.test.assertEquals

val A_UPSELL = UpsellFixture.aUpsell()

class AmendFlowInputTest {

    @Test
    fun `when amend cost is greater than original cost THEN produce positive difference `() {
        assertEquals( priceDifference(amendCost, originalCost), positivePrice )
    }

    @Test
    fun `when amend cost is lower than original cost THEN produce negative difference `() {
        assertEquals( priceDifference(lowerAmendCost, originalCost), negativePrice )
    }

    @Test
    fun `when new total cost is zero a THEN return empty Price`() {
        assertEquals( totalDatesCost(emptyPrice, originalTotalCost.amount), emptyPrice )
    }

    @Test
    fun `when new total cost is greater than original cost THEN produce positive difference `() {
        assertEquals( totalDatesCost(higherDateTotalCost, originalTotalCost.amount), positiveDatesPrice )
    }

    @Test
    fun `when new total cost is lower than original cost THEN produce negative difference `() {
        assertEquals( totalDatesCost(lowerDateTotalCost, originalTotalCost.amount), negativeDatesPrice )
    }

    @Test
    fun `when given a list of room upsells THEN return the total cost`() {
        val upsells = listOf(A_UPSELL, A_UPSELL, A_UPSELL)

        assertEquals(calculateUpsellTotalCost(upsells), upsellTotalCost)
    }

    @Test
    fun `when given empty list of room upsells THEN return the total cost`() {
        val upsells = listOf<Upsell>()

        assertEquals(calculateUpsellTotalCost(upsells), 0f)
    }

    companion object {
        const val amendCost = 13963.0f
        const val lowerAmendCost = 4531.0f
        const val upsellTotalCost = 26.97f

        val emptyPrice = PriceDomain(0.0f, GBP)

        val originalCost = PriceDomain(5330.5f, GBP)
        val positivePrice = PriceDomain(8632.5f, GBP)
        val negativePrice = PriceDomain(-799.5f, GBP)

        val higherDateTotalCost = PriceDomain(33512.0f, GBP)
        val lowerDateTotalCost = PriceDomain(1210.0f, GBP)
        val originalTotalCost = PriceDomain(20420.0f, GBP)

        val positiveDatesPrice = PriceDomain(13092.0f, GBP)
        val negativeDatesPrice = PriceDomain(-19210.0f, GBP)

    }
}