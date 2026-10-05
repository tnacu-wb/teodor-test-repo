package com.whitbread.premierinn.ciol.viewmodel

import com.google.common.truth.Truth
import com.whitbread.premierinn.ciol.entity.AdditionalGuestUiModel
import com.whitbread.premierinn.ciol.entity.RegCardGuest
import com.whitbread.premierinn.ciol.entity.mapToStayingGuestDetailsRegCard
import com.whitbread.premierinn.ciol.viewmodel.state.FormValidation
import com.whitbread.premierinn.common.analytics.TrackingAnalytics
import com.whitbread.premierinn.domain.ciol.usecase.GetCountryNameFromIsoCodeUseCase
import com.whitbread.premierinn.domain.ciol.usecase.GetIsoCodeFromCountryNameUseCase
import com.whitbread.premierinn.domain.ciol.usecase.GetNationalityFromIsoCodeUseCase
import com.whitbread.premierinn.domain.ciol.usecase.ValidateRegCardGuestUseCase
import com.whitbread.premierinn.domain.error.DomainError
import com.whitbread.premierinn.domain.result.Result
import com.whitbread.premierinn.utils.TestCoroutineRule
import com.whitbread.premierinn.utils.collectEmissions
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(TestCoroutineRule::class)
class EditGuestDetailsViewModelTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private val getIsoCodeFromCountryNameUseCase = mockk<GetIsoCodeFromCountryNameUseCase>()
    private val getCountryNameFromIsoCodeUseCase = mockk<GetCountryNameFromIsoCodeUseCase>()
    private val validateRegCardGuestUseCase = mockk<ValidateRegCardGuestUseCase>()
    private val getNationalityFromIsoCodeUseCase = mockk<GetNationalityFromIsoCodeUseCase>()
    private val analytics = mockk<TrackingAnalytics>()

    private val selectedNationalityResult = mutableListOf<String>()
    private val isPassportRequiredResult = mutableListOf<Boolean>()
    private val formValidationResult = mutableListOf<List<FormValidation>>()
    private val receivedGuestResult = mutableListOf<RegCardGuest?>()

    private lateinit var viewModel: EditGuestDetailsViewModel

    @BeforeEach
    fun setup() {
        viewModel = EditGuestDetailsViewModel(
            getIsoCodeFromCountryNameUseCase,
            validateRegCardGuestUseCase,
            getCountryNameFromIsoCodeUseCase,
            getNationalityFromIsoCodeUseCase,
            analytics
        )

        every { getIsoCodeFromCountryNameUseCase("German") } returns "DE"

        collectEmissions(viewModel.selectedNationality, selectedNationalityResult, testCoroutineRule.testScheduler)
        collectEmissions(viewModel.isPassportRequired, isPassportRequiredResult, testCoroutineRule.testScheduler)
        collectEmissions(viewModel.formValidation, formValidationResult, testCoroutineRule.testScheduler)
        collectEmissions(viewModel.receivedGuest, receivedGuestResult, testCoroutineRule.testScheduler)
    }

    @Test
    @DisplayName("GIVEN the user selected a nationality, WHEN the bottom sheet is closed, " +
        "THEN the nationality state will be updated with the selected nationality")
    fun scenario1() = runTest {
        // GIVEN
        val selectedNationality = "German"

        // WHEN
        viewModel.onNationalitySelected(selectedNationality)
        advanceUntilIdle()

        // THEN
        Truth.assertThat(selectedNationalityResult.last()).isEqualTo(selectedNationality)
    }

    @Test
    @DisplayName("GIVEN the user selected German nationality, WHEN the bottom sheet is closed, " +
        "THEN isPassportRequired state will be set to false")
    fun scenario2() = runTest {
        // GIVEN
        val selectedNationality = "German"

        // WHEN
        viewModel.onNationalitySelected(selectedNationality)
        advanceUntilIdle()

        // THEN
        Truth.assertThat(isPassportRequiredResult.last()).isEqualTo(false)
    }

    @Test
    @DisplayName("GIVEN the user selected a non German nationality, WHEN the bottom sheet is closed, " +
        "THEN isPassportRequired state will be set to true")
    fun scenario3() = runTest {
        // GIVEN
        val selectedNationality = "Austrian"
        every { getIsoCodeFromCountryNameUseCase(selectedNationality) } returns "AT"

        // WHEN
        viewModel.onNationalitySelected(selectedNationality)
        advanceUntilIdle()

        // THEN
        Truth.assertThat(isPassportRequiredResult.last()).isEqualTo(true)
    }

    @Test
    @DisplayName("GIVEN the user completed the form AND pressed Save, WHEN all the data is valid, " +
        "THEN form validation will be updated with FormValid result")
    fun scenario4() = runTest {
        // GIVEN
        val additionalGuestDetails = AdditionalGuestUiModel(
            firstName = "Tzidane",
            lastName = "Tzinedi",
            dateOfBirth = "1960-01-01",
            passportNumber = "ABC123",
            nationality = "United Kingdom (the)",
            sameAsBooker = false,
            reservationId = "2103124",
            isAccompanyingGuest = true
        )
        coEvery {
            validateRegCardGuestUseCase(
                additionalGuestDetails.mapToStayingGuestDetailsRegCard(),
                false
            )
        } returns flowOf(Result.Success(Unit))

        // WHEN
        viewModel.onSaveButtonPressed(additionalGuestDetails, false)
        advanceUntilIdle()

        // THEN
        Truth.assertThat(formValidationResult.last())
            .isEqualTo(listOf(FormValidation.FormValid(additionalGuestDetails, false)))
    }

    @Test
    @DisplayName("GIVEN the user focused out of a field, WHEN all the data is valid, " +
        "THEN form validation will be updated and an empty list will be emitted")
    fun scenario5() = runTest {
        // GIVEN
        val additionalGuestDetails = AdditionalGuestUiModel(
            firstName = "Tzidane",
            lastName = "Tzinedi",
            dateOfBirth = "1960-01-01",
            passportNumber = "ABC123",
            nationality = "United Kingdom (the)",
            sameAsBooker = false,
            reservationId = "2103124",
            isAccompanyingGuest = true
        )
        coEvery {
            validateRegCardGuestUseCase(
                additionalGuestDetails.mapToStayingGuestDetailsRegCard(),
                false
            )
        } returns flowOf(Result.Success(Unit))

        // WHEN
        viewModel.validateForm(additionalGuestDetails, false)
        advanceUntilIdle()

        // THEN
        Truth.assertThat(formValidationResult.last()).isEqualTo(emptyList<FormValidation>())
    }

    @Test
    @DisplayName("GIVEN the user completed the form AND pressed Save, WHEN some fields are invalid, " +
        "THEN form validation will be updated with the invalid fields errors")
    fun scenario6() = runTest {
        // GIVEN
        val additionalGuestDetails = AdditionalGuestUiModel(
            firstName = "Tzidane",
            lastName = "Tzinedi",
            dateOfBirth = "1960-01-01",
            passportNumber = "ABC123",
            nationality = "United Kingdom (the)",
            sameAsBooker = false,
            reservationId = "2103124",
            isAccompanyingGuest = true
        )
        coEvery {
            validateRegCardGuestUseCase(
                additionalGuestDetails.mapToStayingGuestDetailsRegCard(),
                false
            )
        } returns flowOf(
            Result.Error(
                DomainError.GuestRegCardValidationError(
                    listOf(
                        ValidateRegCardGuestUseCase.InvalidField.NationalityMissing
                    )
                )
            )
        )

        // WHEN
        viewModel.validateForm(additionalGuestDetails, false)
        advanceUntilIdle()

        // THEN
        Truth.assertThat(formValidationResult.last())
            .isEqualTo(listOf(FormValidation.NationalityMissing))
    }

    @Test
    @DisplayName("GIVEN the edit guest details was accessed with a non null guest, WHEN onGuestReceived " +
        "is invoked, THEN receivedGuest state will be updated accordingly")
    fun scenario7() = runTest {
        // GIVEN
        val additionalGuestDetails = AdditionalGuestUiModel(
            firstName = "Tzidane",
            lastName = "Tzinedi",
            dateOfBirth = "1960-01-01",
            passportNumber = "ABC123",
            nationality = "United Kingdom (the)",
            sameAsBooker = false,
            reservationId = "2103124",
            isAccompanyingGuest = true
        )

        // WHEN
        viewModel.onGuestReceived(additionalGuestDetails)
        advanceUntilIdle()

        // THEN
        Truth.assertThat(receivedGuestResult.last())
            .isEqualTo(additionalGuestDetails)
    }
}
