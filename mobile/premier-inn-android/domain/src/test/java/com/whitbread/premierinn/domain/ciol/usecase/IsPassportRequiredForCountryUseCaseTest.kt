package com.whitbread.premierinn.domain.ciol.usecase

import com.google.common.truth.Truth
import com.whitbread.premierinn.domain.countries.GetCountries
import com.whitbread.premierinn.domain.countries.entity.CountryDomain
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Test

class IsPassportRequiredForCountryUseCaseTest {
    private val countries = listOf(
        CountryDomain(
            countryCode = "CY",
            countryIsoCode = "CY",
            countryName = "Cyprus",
            requiresPassportInfo = false,
            nationality = "Austrian"
        ),
        CountryDomain(
            countryCode = "D",
            countryIsoCode = "DE",
            countryName = "Germany",
            requiresPassportInfo = true,
            nationality = "Austrian"
        ),
        CountryDomain(
            countryCode = "GB",
            countryIsoCode = "GB",
            countryName = "United Kingdom (the)",
            requiresPassportInfo = false,
            nationality = "Austrian"
        )
    )

    private val getCountries = mockk<GetCountries>()
    private val isPassportRequiredForCountryUseCase =
        IsPassportRequiredForCountryUseCase(getCountries)

    @Before
    fun setup() {
        every { getCountries.fetchCountriesFromSharedPref() } returns countries
    }

    @Test
    fun `GIVEN the user selected a UK hotel, WHEN selected nationality is not part of Commonwealth, THEN passport will be required`() {
        // GIVEN
        val hotelBrand = "PI"

        // WHEN, THEN
        Truth.assertThat(isPassportRequiredForCountryUseCase("Germany", hotelBrand)).isTrue()
    }

    @Test
    fun `GIVEN the user selected a UK hotel, WHEN selected nationality is UK, THEN passport won't be required`() {
        // GIVEN
        val hotelBrand = "PI"

        // WHEN, THEN
        Truth.assertThat(isPassportRequiredForCountryUseCase("United Kingdom (the)", hotelBrand)).isFalse()
    }

    @Test
    fun `GIVEN the user selected a UK hotel, WHEN selected nationality is part of Commonwealth, THEN passport won't be required`() {
        // GIVEN
        val hotelBrand = "PI"

        // WHEN, THEN
        Truth.assertThat(isPassportRequiredForCountryUseCase("Cyprus", hotelBrand)).isFalse()
    }

    @Test
    fun `GIVEN the user selected a UK hotel, WHEN selected nationality is not found, THEN passport will be required`() {
        // GIVEN
        val hotelBrand = "PI"

        // WHEN, THEN
        Truth.assertThat(isPassportRequiredForCountryUseCase("Romania", hotelBrand)).isTrue()
    }

    @Test
    fun `GIVEN the user selected a DE hotel, WHEN selected nationality is Germany, THEN passport won't be required`() {
        // GIVEN
        val hotelBrand = "PID"

        // WHEN, THEN
        Truth.assertThat(isPassportRequiredForCountryUseCase("Germany", hotelBrand)).isFalse()
    }

    @Test
    fun `GIVEN the user selected a DE hotel, WHEN selected nationality is other than Germany, THEN passport won't be required`() {
        // GIVEN
        val hotelBrand = "PID"

        // WHEN, THEN
        Truth.assertThat(isPassportRequiredForCountryUseCase("United Kingdom (the)", hotelBrand)).isTrue()
    }

    @Test
    fun `GIVEN the user selected a DE hotel, WHEN selected nationality is not found, THEN passport will be required`() {
        // GIVEN
        val hotelBrand = "PID"

        // WHEN, THEN
        Truth.assertThat(isPassportRequiredForCountryUseCase("Romania", hotelBrand)).isTrue()
    }
}