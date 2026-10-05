package com.whitbread.premierinn.ciol.usecase

import com.google.common.truth.Truth.assertThat
import com.whitbread.premierinn.ciol.entity.AdditionalGuestUiModel
import com.whitbread.premierinn.ciol.entity.DeRegCardAddressUiModel
import com.whitbread.premierinn.ciol.entity.LeadGuestUiModel
import com.whitbread.premierinn.domain.ciol.usecase.GetIsoCodeFromCountryNameUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class ValidateRegCardGuestDetailsUseCaseTest {

    private val additionalGuest = AdditionalGuestUiModel(
        firstName = "John",
        lastName = "Doe",
        dateOfBirth = "1960-01-01",
        passportNumber = "ABC123",
        nationality = "Australia",
        sameAsBooker = false,
        reservationId = "2103124",
        isAccompanyingGuest = true
    )

    private val guest = LeadGuestUiModel(
        title = "Mr",
        address = DeRegCardAddressUiModel(addressLine1 = "Address line 1"),
        additionalGuestUiModel = additionalGuest.copy()
    )

    private val getIsoCodeFromCountryNameUseCase = mockk<GetIsoCodeFromCountryNameUseCase>()
    private val validateRegCardGuestDetailsUseCase = ValidateRegCardGuestDetailsUseCase(getIsoCodeFromCountryNameUseCase)


    @Before
    fun setUp() {
        every { getIsoCodeFromCountryNameUseCase("Australia") } returns "AU"
        every { getIsoCodeFromCountryNameUseCase("Germany") } returns "DE"
    }

    @Test
    fun `GIVEN the guest address was empty, WHEN performing validation,THEN the guest id will be added to an invalid ids list`() = runTest {
        // GIVEN
        val leadGuest = guest.copy(address = DeRegCardAddressUiModel(addressLine1 = ""))

        // WHEN
        val result = validateRegCardGuestDetailsUseCase(listOf(leadGuest))

        // THEN
        assertThat(result.size).isEqualTo(1)
    }

    @Test
    fun `GIVEN the guest had empty date of birth, WHEN performing validation, THEN the guest id will be added to invalid ids list`() = runTest {
        // GIVEN
        val leadGuest = guest.copy(additionalGuestUiModel = guest.additionalGuestUiModel.copy(dateOfBirth = ""))

        // WHEN
        val result = validateRegCardGuestDetailsUseCase(listOf(leadGuest))

        // THEN
        assertThat(result.size).isEqualTo(1)
    }

    @Test
    fun `GIVEN the guest had passport number empty, WHEN performing validation, THEN the guest ids will be added to invalid ids list`() = runTest {
            // GIVEN
            val leadGuest = guest.copy(additionalGuestUiModel = guest.additionalGuestUiModel.copy(passportNumber = ""))

            // WHEN
            val result = validateRegCardGuestDetailsUseCase(listOf(leadGuest))

            // THEN
            assertThat(result.size).isEqualTo(1)
    }

    @Test
    fun `GIVEN the guest nationality is DE, and passport is empty WHEN performing validation, THEN the guest ids will NOT be added to invalid ids list`() = runTest {
        // GIVEN
        val leadGuest = guest.copy(additionalGuestUiModel = guest.additionalGuestUiModel.copy(nationality = "Germany", passportNumber = ""))

        // WHEN
        val result = validateRegCardGuestDetailsUseCase(listOf(leadGuest))

        // THEN
        assertThat(result.size).isEqualTo(0)
    }
}
