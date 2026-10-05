package com.whitbread.premierinn.domain.reservation.usecase

import com.whitbread.premierinn.domain.common.model.RatePlanFixture
import com.whitbread.premierinn.domain.common.model.ReservationFixture
import com.whitbread.premierinn.domain.reservation.repository.AmendedReservationRepository
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Completable
import io.reactivex.Observable
import org.junit.Test
import org.threeten.bp.LocalDate

private val RATE_PLAN = RatePlanFixture.aRatePlan()
private val RESERVATION = ReservationFixture.aReservation()

class StoreUpdatedDateUseCaseTest {

    private val repository: AmendedReservationRepository = mockk()
    private val storeUpdatedDateUseCase: StoreUpdatedDateUseCase = mockk()

    @Test
    fun `WHEN updated reservation dates THEN updated state is returned`() {
        every { repository.updateReservationDatesAndUpsells(referenceNumber, updatedDates, RESERVATION) } returns Completable.complete()
        every { storeUpdatedDateUseCase.invoke(referenceNumber, updatedDates, RATE_PLAN) } returns Observable.just(StoreUpdatedDateUseCase.UpdateDateState.Updated)

        storeUpdatedDateUseCase(referenceNumber, updatedDates, RATE_PLAN).test()
                .assertValueAt(0, StoreUpdatedDateUseCase.UpdateDateState.Updated)
    }

    @Test
    fun `WHEN updated reservation returns error THEN updated state is returned`() {
        val exception = Exception()
        every { repository.updateReservationDatesAndUpsells(referenceNumber, updatedDates, RESERVATION) } returns Completable.error(exception)
        every { storeUpdatedDateUseCase.invoke(referenceNumber, updatedDates, RATE_PLAN) } returns Observable.just(StoreUpdatedDateUseCase.UpdateDateState.Error(exception))

        storeUpdatedDateUseCase(referenceNumber, updatedDates, RATE_PLAN).test()
                .assertValueAt(0, StoreUpdatedDateUseCase.UpdateDateState.Error(exception))
    }

    companion object {
        private const val referenceNumber = "BER2334242"
        private val updatedArrival = LocalDate.of(2020, 8, 11)
        private val updatedDeparture = LocalDate.of(2020, 8, 14)
        private val updatedDates = Pair(updatedArrival, updatedDeparture)
    }
}