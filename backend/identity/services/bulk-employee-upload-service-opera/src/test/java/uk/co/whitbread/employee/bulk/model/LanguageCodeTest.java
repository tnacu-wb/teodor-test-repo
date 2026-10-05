package uk.co.whitbread.employee.bulk.model;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class LanguageCodeTest {

    @Test
    void getLanguageCodeShouldReturnEnIfEnIsPassed() {
        final String result = LanguageCode.getLanguageCode("en");

        Assertions.assertThat(result).isEqualTo("en");
    }

    @Test
    void getLanguageCodeShouldReturnEnIfInvalidLanguageIsPassed() {
        final String result = LanguageCode.getLanguageCode("zz");

        Assertions.assertThat(result).isEqualTo("en");
    }

    @Test
    void getLanguageCodeShouldReturnDeIfDeIsPassedAndShouldBeLowerCase() {
        final String result = LanguageCode.getLanguageCode("DE");

        Assertions.assertThat(result).isEqualTo("de");
    }
}
