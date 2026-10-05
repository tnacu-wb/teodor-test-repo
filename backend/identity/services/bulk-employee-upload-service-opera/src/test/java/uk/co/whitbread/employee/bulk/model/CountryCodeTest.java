package uk.co.whitbread.employee.bulk.model;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class CountryCodeTest {

    @Test
    void getCountryCodeShouldReturnEnIfEnIsPassed() {
        final String result = CountryCode.getCountryCode("gb");

        Assertions.assertThat(result).isEqualTo("gb");
    }

    @Test
    void getCountryCodeShouldReturnEnIfInvalidCountryIsPassed() {
        final String result = CountryCode.getCountryCode("zz");

        Assertions.assertThat(result).isEqualTo("gb");
    }

    @Test
    void getCountryCodeShouldReturnDeIfDeIsPassedAndShouldBeLowerCase() {
        final String result = CountryCode.getCountryCode("DE");

        Assertions.assertThat(result).isEqualTo("de");
    }

}
