package com.whitbread.premierinn.domain.ciol.usecase

import com.google.common.truth.Truth
import com.whitbread.premierinn.domain.countries.GetCountries
import com.whitbread.premierinn.domain.countries.entity.CountryDomain
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Test

class GetCountryNameFromIsoCodeUseCaseTest {
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
    private val getCountryNameFromIsoCodeUseCase = GetCountryNameFromIsoCodeUseCase(getCountries)

    @Before
    fun setup() {
        every { getCountries.fetchCountriesFromSharedPref() } returns countries
    }

    @Test
    fun `GIVEN selected nationality had DE country iso code, WHEN the isoCode is found in countries list, THEN Germany will be returned`() {
        Truth.assertThat(getCountryNameFromIsoCodeUseCase("DE")).isEqualTo("Germany")
    }

    @Test
    fun `GIVEN selected nationality had RO country iso code, WHEN the isoCode is not found in countries list, THEN an empty string will be returned`() {
        Truth.assertThat(getCountryNameFromIsoCodeUseCase("RO")).isEqualTo("")
    }

    @Test
    fun `GIVEN selected nationality had DE country iso code, WHEN getCountries returns null, THEN an empty string will be returned`() {
        // GIVEN
        every { getCountries.fetchCountriesFromSharedPref() } returns null

        // WHEN THEN
        Truth.assertThat(getCountryNameFromIsoCodeUseCase("DE")).isEqualTo("")
    }

}
