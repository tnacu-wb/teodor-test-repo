package com.whitbread.premierinn.domain.graphql.ciol.usecase

import com.whitbread.premierinn.domain.common.Address
import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import com.whitbread.premierinn.domain.countries.GetCountries
import com.whitbread.premierinn.domain.countries.entity.CountryDomain
import com.whitbread.premierinn.domain.error.DomainError.BillingAddressValidationError.CountriesRetrievalError
import com.whitbread.premierinn.domain.error.DomainError.BillingAddressValidationError.InvalidFieldsError
import com.whitbread.premierinn.domain.graphql.ciol.usecase.ValidateBillingAddressUseCase.InvalidBillingAddressFields.FIRST_LINE
import com.whitbread.premierinn.domain.graphql.ciol.usecase.ValidateBillingAddressUseCase.InvalidBillingAddressFields.POSTCODE
import com.whitbread.premierinn.domain.result.Result
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Before
import kotlin.test.Test
import kotlin.test.assertEquals

class ValidateBillingAddressUseCaseTest {

    private val getCountries = mockk<GetCountries>()

    private val validateBillingAddressUseCase = ValidateBillingAddressUseCase(getCountries)

    private val billingAddress = Address(
        line1 = "Line 1",
        postCode = "SW1W 0NY",
        countryCode = "GB"
    )

    @Before
    fun setup() {
        val countries = listOf(
            CountryDomain("UK", "GB", "United Kingdom", false, "Austrian")
        )
        every { getCountries.fetchCountriesFromSharedPref() } returns countries
    }

    @Test
    fun `GIVEN shouldDisplayDetailedAddress was set to false, WHEN continue to pay button is pressed, THEN Success will be emitted AND shouldDisplayDetailedAddress will be false`() =
        runTest {
            // GIVEN
            val shouldDisplayDetailedAddress = false
            val billingAddress = null

            // WHEN
            val result =
                validateBillingAddressUseCase(shouldDisplayDetailedAddress, billingAddress).toList()

            // THEN
            assertEquals(1, result.size)
            assertEquals(Result.Success(false), result.first())
        }

    @Test
    fun `GIVEN shouldDisplayDetailedAddress was set to true, WHEN billingAddress is correct, THEN Success will be emitted AND shouldDisplayDetailedAddress will be true`() =
        runTest {
            // GIVEN
            val shouldDisplayDetailedAddress = true

            // WHEN
            val result =
                validateBillingAddressUseCase(shouldDisplayDetailedAddress, billingAddress).toList()

            // THEN
            assertEquals(1, result.size)
            assertEquals(Result.Success(true), result.first())
        }

    @Test
    fun `GIVEN shouldDisplayDetailedAddress was set to true, WHEN line1 is missing from address, THEN an InvalidFieldsError containing FIRST_LINE will be emitted`() =
        runTest {
            // GIVEN
            val shouldDisplayDetailedAddress = true
            val billingAddress = billingAddress.copy(line1 = EMPTY_STRING_DOMAIN)

            // WHEN
            val result =
                validateBillingAddressUseCase(shouldDisplayDetailedAddress, billingAddress).toList()

            // THEN
            assertEquals(1, result.size)
            assertEquals(
                Result.Error(InvalidFieldsError(listOf(FIRST_LINE))),
                result.first()
            )
        }

    @Test
    fun `GIVEN shouldDisplayDetailedAddress was set to true, WHEN postcode is missing from address, THEN an InvalidFieldsError containing POSTCODE will be emitted`() =
        runTest {
            // GIVEN
            val shouldDisplayDetailedAddress = true
            val billingAddress = billingAddress.copy(postCode = EMPTY_STRING_DOMAIN)

            // WHEN
            val result =
                validateBillingAddressUseCase(shouldDisplayDetailedAddress, billingAddress).toList()

            // THEN
            assertEquals(1, result.size)
            assertEquals(
                Result.Error(InvalidFieldsError(listOf(POSTCODE))),
                result.first()
            )
        }

    @Test
    fun `GIVEN shouldDisplayDetailedAddress was set to true, WHEN address fields are not set, THEN an InvalidFieldsError containing POSTCODE and FIRST_LINE will be emitted`() =
        runTest {
            // GIVEN
            val shouldDisplayDetailedAddress = true
            val billingAddress =
                billingAddress.copy(postCode = EMPTY_STRING_DOMAIN, line1 = EMPTY_STRING_DOMAIN)

            // WHEN
            val result =
                validateBillingAddressUseCase(shouldDisplayDetailedAddress, billingAddress).toList()

            // THEN
            assertEquals(1, result.size)
            assertEquals(
                Result.Error(InvalidFieldsError(listOf(POSTCODE, FIRST_LINE))),
                result.first()
            )
        }

    @Test
    fun `GIVEN shouldDisplayDetailedAddress was set to true, WHEN address is null, THEN a CountriesRetrievalError will be emitted`() =
        runTest {
            // GIVEN
            val shouldDisplayDetailedAddress = true
            val billingAddress = null

            // WHEN
            val result =
                validateBillingAddressUseCase(shouldDisplayDetailedAddress, billingAddress).toList()

            // THEN
            assertEquals(1, result.size)
            assertEquals(
                Result.Error(CountriesRetrievalError),
                result.first()
            )
        }
}
