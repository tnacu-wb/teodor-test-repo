package com.whitbread.premierinn.domain.ciol.usecase

import com.google.common.truth.Truth
import com.whitbread.premierinn.domain.countries.GetCountries
import com.whitbread.premierinn.domain.countries.entity.CountryDomain
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import kotlin.test.Test

class GetIsoCodeFromCountryNameUseCaseTest {
    private val countries = listOf(
        CountryDomain(
            countryCode = "D",
            countryIsoCode = "DE",
            countryName = "Germany",
            requiresPassportInfo = true,
            nationality = "German"
        )
    )

    private val getCountries = mockk<GetCountries>()
    private val getIsoCodeFromCountryNameUseCase = GetIsoCodeFromCountryNameUseCase(getCountries)

    @Before
    fun setup() {
        every { getCountries.fetchCountriesFromSharedPref() } returns countries
    }

    @Test
    fun `GIVEN selected country was Germany, WHEN use case is invoked, THEN DE country code will be returned`() {
        Truth.assertThat(getIsoCodeFromCountryNameUseCase("Germany")).isEqualTo("DE")
    }

    @Test
    fun `GIVEN selected country was null, WHEN use case is invoked, THEN null will be returned`() {
        Truth.assertThat(getIsoCodeFromCountryNameUseCase(null)).isEqualTo(null)
    }

    @Test
    fun `GIVEN selected country was invalid, WHEN use case is invoked, THEN the actual country will be returned instead of country code`() {
        Truth.assertThat(getIsoCodeFromCountryNameUseCase("Invalid")).isEqualTo("Invalid")
    }
}
