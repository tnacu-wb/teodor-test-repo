package com.whitbread.premierinn.domain.ciol.usecase

import com.google.common.truth.Truth
import com.whitbread.premierinn.domain.ciol.usecase.ValidateRegCardGuestUseCase.InvalidField.FirstNameMissing
import com.whitbread.premierinn.domain.ciol.usecase.ValidateRegCardGuestUseCase.InvalidField.FirstNameTooLong
import com.whitbread.premierinn.domain.ciol.usecase.ValidateRegCardGuestUseCase.InvalidField.FirstNameTooShort
import com.whitbread.premierinn.domain.ciol.usecase.ValidateRegCardGuestUseCase.InvalidField.LastNameMissing
import com.whitbread.premierinn.domain.ciol.usecase.ValidateRegCardGuestUseCase.InvalidField.LastNameTooLong
import com.whitbread.premierinn.domain.ciol.usecase.ValidateRegCardGuestUseCase.InvalidField.LastNameTooShort
import com.whitbread.premierinn.domain.ciol.usecase.ValidateRegCardGuestUseCase.InvalidField.NationalityMissing
import com.whitbread.premierinn.domain.ciol.usecase.ValidateRegCardGuestUseCase.InvalidField.PassportNumberMissing
import com.whitbread.premierinn.domain.ciol.usecase.ValidateRegCardGuestUseCase.InvalidField.PassportNumberTooLong
import com.whitbread.premierinn.domain.ciol.usecase.ValidateRegCardGuestUseCase.InvalidField.PassportNumberTooShort
import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import com.whitbread.premierinn.domain.graphql.requestBodyModels.StayingGuestAdditionalDetails
import com.whitbread.premierinn.domain.graphql.requestBodyModels.StayingGuestAddress
import com.whitbread.premierinn.domain.graphql.requestBodyModels.StayingGuestDetailsRegCard
import com.whitbread.premierinn.domain.result.Result
import com.whitbread.premierinn.domain.utils.isAdult
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ValidateRegCardGuestUseCaseTest {

    private val additionalGuestDetails = StayingGuestDetailsRegCard(
        firstName = "Tzidane",
        lastName = "Tzinedi",
        profileId = EMPTY_STRING_DOMAIN,
        address = null,
        additionalDetails = StayingGuestAdditionalDetails(
            dob = "1960-01-01",
            passportNumber = "ABC123",
            nationality = "United Kingdom (the)"
        ),
        sameAsBooker = false,
        reservationId = "2103124",
        isAccompanyingGuest = true
    )
    private val leadGuestDetails = additionalGuestDetails.copy(
        address = StayingGuestAddress(
            addressLine1 = "Belgravia St",
            addressLine2 = "121B",
            addressLine3 = "addressLine3",
            cityName = "London",
            countryCode = "GB",
            postalCode = "SW1W 0NY",
            addressType = "HOME"
        )
    )
    private val getIsoCodeFromCountryNameUseCase = mockk<GetIsoCodeFromCountryNameUseCase>()
    private val validateRegCardGuestUseCase = ValidateRegCardGuestUseCase(getIsoCodeFromCountryNameUseCase)

    @Before
    fun setup() {
        mockkStatic("com.whitbread.premierinn.domain.utils.CalendarUtilsKt")
        every { isAdult(any()) } returns true
        every { getIsoCodeFromCountryNameUseCase("United Kingdom (the)") } returns "GB"
        every { getIsoCodeFromCountryNameUseCase("Germany") } returns "DE"
    }

    @Test
    fun `GIVEN user wasn't lead guest and first name was empty, WHEN performing the validation, THEN FirstNameMissing will be returned as an error`() = runTest {
        // GIVEN
        val guest = additionalGuestDetails.copy(firstName = "")

        // WHEN
        val result = validateRegCardGuestUseCase(guest, false).toList()
        advanceUntilIdle()

        // THEN
        Truth.assertThat(result.size).isEqualTo(1)
        testSingleInvalidField(result[0], FirstNameMissing)
    }

    @Test
    fun `GIVEN user wasn't lead guest and first name was shorter than 2 chars, WHEN performing the validation, THEN FirstNameTooShort will be returned as an error`() = runTest {
        // GIVEN
        val guest = additionalGuestDetails.copy(firstName = "A")

        // WHEN
        val result = validateRegCardGuestUseCase(guest, false).toList()
        advanceUntilIdle()

        // THEN
        Truth.assertThat(result.size).isEqualTo(1)
        testSingleInvalidField(result[0], FirstNameTooShort)
    }

    @Test
    fun `GIVEN user wasn't lead guest and first name was longer than 20 chars, WHEN performing the validation, THEN FirstNameTooLong will be returned as an error`() = runTest {
        // GIVEN
        val guest = additionalGuestDetails.copy(firstName = "Elisabeth Anastasia Gabriella")

        // WHEN
        val result = validateRegCardGuestUseCase(guest, false).toList()
        advanceUntilIdle()

        // THEN
        Truth.assertThat(result.size).isEqualTo(1)
        testSingleInvalidField(result[0], FirstNameTooLong)
    }

    @Test
    fun `GIVEN user wasn't lead guest and last name was empty, WHEN performing the validation, THEN LastNameMissing will be returned as an error`() = runTest {
        // GIVEN
        val guest = additionalGuestDetails.copy(lastName = "")

        // WHEN
        val result = validateRegCardGuestUseCase(guest, false).toList()
        advanceUntilIdle()

        // THEN
        Truth.assertThat(result.size).isEqualTo(1)
        testSingleInvalidField(result[0], LastNameMissing)
    }

    @Test
    fun `GIVEN user wasn't lead guest and last name was shorter than 2 chars, WHEN performing the validation, THEN LastNameTooShort will be returned as an error`() = runTest {
        // GIVEN
        val guest = additionalGuestDetails.copy(lastName = "A")

        // WHEN
        val result = validateRegCardGuestUseCase(guest, false).toList()
        advanceUntilIdle()

        // THEN
        Truth.assertThat(result.size).isEqualTo(1)
        testSingleInvalidField(result[0], LastNameTooShort)
    }

    @Test
    fun `GIVEN user wasn't lead guest and last name was longer than 30 chars, WHEN performing the validation, THEN LastNameTooLong will be returned as an error`() = runTest {
        // GIVEN
        val guest = additionalGuestDetails.copy(lastName = "Bonaventura Montgomery Fleetwood")

        // WHEN
        val result = validateRegCardGuestUseCase(guest, false).toList()
        advanceUntilIdle()

        // THEN
        Truth.assertThat(result.size).isEqualTo(1)
        testSingleInvalidField(result[0], LastNameTooLong)
    }

    @Test
    fun `GIVEN user wasn't lead guest and nationality was empty, WHEN performing the validation, THEN NationalityMissing will be returned as an error`() = runTest {
        // GIVEN
        val guest = additionalGuestDetails.copy(additionalDetails = additionalGuestDetails.additionalDetails.copy(nationality = ""))

        // WHEN
        val result = validateRegCardGuestUseCase(guest, false).toList()
        advanceUntilIdle()

        // THEN
        Truth.assertThat(result.size).isEqualTo(1)
        testSingleInvalidField(result[0], NationalityMissing)
    }

    @Test
    fun `GIVEN user wasn't lead guest, nationality was not German and passport number was missing, WHEN performing the validation, THEN PassportNumberMissing will be returned as an error`() = runTest {
        // GIVEN
        val guest = additionalGuestDetails.copy(
            additionalDetails = additionalGuestDetails.additionalDetails.copy(
                nationality = "United Kingdom (the)",
                passportNumber = ""
            )
        )

        // WHEN
        val result = validateRegCardGuestUseCase(guest, false).toList()
        advanceUntilIdle()

        // THEN
        Truth.assertThat(result.size).isEqualTo(1)
        testSingleInvalidField(result[0], PassportNumberMissing)
    }

    @Test
    fun `GIVEN user wasn't lead guest, nationality was not German and passport number was shorter than 2 chars, WHEN performing the validation, THEN PassportNumberTooShort will be returned as an error`() = runTest {
        // GIVEN
        val guest = additionalGuestDetails.copy(
            additionalDetails = additionalGuestDetails.additionalDetails.copy(
                nationality = "United Kingdom (the)",
                passportNumber = "1"
            )
        )

        // WHEN
        val result = validateRegCardGuestUseCase(guest, false).toList()
        advanceUntilIdle()

        // THEN
        Truth.assertThat(result.size).isEqualTo(1)
        testSingleInvalidField(result[0], PassportNumberTooShort)
    }

    @Test
    fun `GIVEN user wasn't lead guest, nationality was not German and passport number was longer than 20 chars, WHEN performing the validation, THEN PassportNumberTooLong will be returned as an error`() = runTest {
        // GIVEN
        val guest = additionalGuestDetails.copy(
            additionalDetails = additionalGuestDetails.additionalDetails.copy(
                nationality = "United Kingdom (the)",
                passportNumber = "1234567890ABCDEFGHIJK"
            )
        )

        // WHEN
        val result = validateRegCardGuestUseCase(guest, false).toList()
        advanceUntilIdle()

        // THEN
        Truth.assertThat(result.size).isEqualTo(1)
        testSingleInvalidField(result[0], PassportNumberTooLong)
    }

    @Test
    fun `GIVEN user wasn't lead guest, nationality was German and passportNumber was missing, WHEN performing the validation, THEN Result Success will be returned`() = runTest {
        // GIVEN
        val guest = additionalGuestDetails.copy(
            additionalDetails = additionalGuestDetails.additionalDetails.copy(
                nationality = "Germany",
                passportNumber = ""
            )
        )

        // WHEN
        val result = validateRegCardGuestUseCase(guest, false).toList()
        advanceUntilIdle()

        // THEN
        Truth.assertThat(result.size).isEqualTo(1)
        Truth.assertThat(result[0]).isInstanceOf(Result.Success::class.java)
    }

    @Test
    fun `GIVEN user wasn't lead guest and all data was valid, WHEN performing the validation, THEN Result Success will be returned`() = runTest {
        // WHEN
        val result = validateRegCardGuestUseCase(additionalGuestDetails, false).toList()
        advanceUntilIdle()

        // THEN
        Truth.assertThat(result.size).isEqualTo(1)
        Truth.assertThat(result[0]).isInstanceOf(Result.Success::class.java)
    }

    @Test
    fun `GIVEN user was lead guest and home address was missing, WHEN performing the validation, THEN HomeAddressMissing Error will be returned`() = runTest {
        // GIVEN
        val guest = leadGuestDetails.copy(address = leadGuestDetails.address?.copy(addressLine1 = ""))

        // WHEN
        val result = validateRegCardGuestUseCase(guest, true).toList()
        advanceUntilIdle()

        // THEN
        Truth.assertThat(result.size).isEqualTo(1)
        testSingleInvalidField(result[0], ValidateRegCardGuestUseCase.InvalidField.HomeAddressMissing)
    }

    @Test
    fun `GIVEN user was lead guest and home address was longer than 35 chars, WHEN performing the validation, THEN HomeAddressTooLong Error will be returned`() = runTest {
        // GIVEN
        val guest = leadGuestDetails.copy(address = leadGuestDetails.address?.copy(
            addressLine1 = "Leopold Karl Walter Graf von Kalkreuth Strasse"
        ))

        // WHEN
        val result = validateRegCardGuestUseCase(guest, true).toList()
        advanceUntilIdle()

        // THEN
        Truth.assertThat(result.size).isEqualTo(1)
        testSingleInvalidField(result[0], ValidateRegCardGuestUseCase.InvalidField.HomeAddressTooLong)
    }

    @Test
    fun `GIVEN user was lead guest and postcode was missing, WHEN performing the validation, THEN PostcodeMissing Error will be returned`() = runTest {
        // GIVEN
        val guest = leadGuestDetails.copy(address = leadGuestDetails.address?.copy(postalCode = ""))

        // WHEN
        val result = validateRegCardGuestUseCase(guest, true).toList()
        advanceUntilIdle()

        // THEN
        Truth.assertThat(result.size).isEqualTo(1)
        testSingleInvalidField(result[0], ValidateRegCardGuestUseCase.InvalidField.PostcodeMissing)
    }

    @Test
    fun `GIVEN user was lead guest and city name was missing, WHEN performing the validation, THEN CityNameMissing Error will be returned`() = runTest {
        // GIVEN
        val guest = leadGuestDetails.copy(address = leadGuestDetails.address?.copy(cityName = ""))

        // WHEN
        val result = validateRegCardGuestUseCase(guest, true).toList()
        advanceUntilIdle()

        // THEN
        Truth.assertThat(result.size).isEqualTo(1)
        testSingleInvalidField(result[0], ValidateRegCardGuestUseCase.InvalidField.CityNameMissing)
    }

    @Test
    fun `GIVEN user was lead guest and city name was longer than 35 chars, WHEN performing the validation, THEN CityNameTooLong Error will be returned`() = runTest {
        // GIVEN
        val guest = leadGuestDetails.copy(address = leadGuestDetails.address?.copy(
            cityName = "Fuerstenfeldbruck Fuerstenfeldbruck Fuerstenfeldbruck"
        ))

        // WHEN
        val result = validateRegCardGuestUseCase(guest, true).toList()
        advanceUntilIdle()

        // THEN
        Truth.assertThat(result.size).isEqualTo(1)
        testSingleInvalidField(result[0], ValidateRegCardGuestUseCase.InvalidField.CityNameTooLong)
    }

    @Test
    fun `GIVEN user was lead guest and country code was missing, WHEN performing the validation, THEN CountryMissing Error will be returned`() = runTest {
        // GIVEN
        val guest = leadGuestDetails.copy(address = leadGuestDetails.address?.copy(countryCode = ""))

        // WHEN
        val result = validateRegCardGuestUseCase(guest, true).toList()
        advanceUntilIdle()

        // THEN
        Truth.assertThat(result.size).isEqualTo(1)
        testSingleInvalidField(result[0], ValidateRegCardGuestUseCase.InvalidField.CountryMissing)
    }

    @Test
    fun `GIVEN user was lead guest and all data was valid, WHEN performing the validation, THEN Result Success will be returned`() = runTest {
        // WHEN
        val result = validateRegCardGuestUseCase(leadGuestDetails, true).toList()
        advanceUntilIdle()

        // THEN
        Truth.assertThat(result.size).isEqualTo(1)
        Truth.assertThat(result[0]).isInstanceOf(Result.Success::class.java)
    }

    @Test
    fun `GIVEN user wasn't lead guest and dob was missing, WHEN performing the validation, THEN DateOfBirthMissing Error will be returned`() = runTest {
        // GIVEN
        val guest = additionalGuestDetails.copy(additionalDetails = additionalGuestDetails.additionalDetails.copy(dob = ""))

        // WHEN
        val result = validateRegCardGuestUseCase(guest, false).toList()
        advanceUntilIdle()

        // THEN
        Truth.assertThat(result.size).isEqualTo(1)

        testSingleInvalidField(result[0], ValidateRegCardGuestUseCase.InvalidField.DateOfBirthMissing)
    }

    @Test
    fun `GIVEN user wasn't lead guest and dob is not missing, WHEN performing the validation, THEN Result Success will be returned`() = runTest {
        // GIVEN
        val guest = additionalGuestDetails

        // WHEN
        val result = validateRegCardGuestUseCase(guest, false).toList()
        advanceUntilIdle()

        // THEN
        Truth.assertThat(result.size).isEqualTo(1)
        Truth.assertThat(result[0]).isInstanceOf(Result.Success::class.java)
    }

    @Test
    fun `GIVEN user was lead guest and dob was missing, WHEN performing the validation, THEN DateOfBirthMissing Error will be returned`() = runTest {
        // GIVEN
        val guest = leadGuestDetails.copy(additionalDetails = leadGuestDetails.additionalDetails.copy(dob = ""))

        // WHEN
        val result = validateRegCardGuestUseCase(guest, true).toList()
        advanceUntilIdle()

        // THEN
        Truth.assertThat(result.size).isEqualTo(1)
        testSingleInvalidField(result[0], ValidateRegCardGuestUseCase.InvalidField.DateOfBirthMissing)
    }

    @Test
    fun `GIVEN user was lead guest and dob is not 18 years old, WHEN performing the validation, THEN GuestNotAdult Error will be returned`() = runTest {
        // GIVEN
        every { isAdult("2023-10-04") } returns false
        val guest = leadGuestDetails.copy(additionalDetails = leadGuestDetails.additionalDetails.copy(dob = "2023-10-04"))

        // WHEN
        val result = validateRegCardGuestUseCase(guest, true).toList()
        advanceUntilIdle()

        // THEN
        Truth.assertThat(result.size).isEqualTo(1)
        testSingleInvalidField(result[0], ValidateRegCardGuestUseCase.InvalidField.GuestNotAdult)
    }

    @Test
    fun `GIVEN user was lead guest and dob is greater than 18 years old, WHEN performing the validation, THEN Result Success will be returned`() = runTest {
        // GIVEN
        val guest = leadGuestDetails.copy(additionalDetails = leadGuestDetails.additionalDetails.copy(dob = "2000-10-04"))

        // WHEN
        val result = validateRegCardGuestUseCase(guest, true).toList()
        advanceUntilIdle()

        // THEN
        Truth.assertThat(result.size).isEqualTo(1)
        Truth.assertThat(result[0]).isInstanceOf(Result.Success::class.java)
    }

    private fun testSingleInvalidField(response: ValidateRegCardGuestResponse, expected: ValidateRegCardGuestUseCase.InvalidField) {
        Truth.assertThat(response).isInstanceOf(Result.Error::class.java)
        (response as Result.Error).error.apply {
            Truth.assertThat(invalidFields.size).isEqualTo(1)
            Truth.assertThat(invalidFields[0]).isEqualTo(expected)
        }
    }
}
