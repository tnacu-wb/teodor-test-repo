package com.whitbread.premierinn.domain.common

import com.google.common.truth.Truth.assertThat
import com.whitbread.premierinn.domain.createSearchItem
import org.junit.Test
import org.threeten.bp.LocalDate

class BookingCriteriaTest {

    @Test
    fun getDepartureDate() {

        val availabilityRequest = BookingCriteria(arrivalDate = LocalDate.of(2018, 4,21), numberOfNights = 1, roomsCriteria = emptyList(),
                searchCriteria = createSearchItem())

        assertThat(availabilityRequest.departureDate).isEqualTo(LocalDate.of(2018, 4,22))
    }
}