package uk.co.whitbread.infrastructure.rest.controller.validation;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CountryFormatValidatorTest {

  private CountryFormatValidator countryFormatValidator = new CountryFormatValidator();

  @ParameterizedTest
  @CsvSource({
      "gb, true",
      "gbd, false",
      "null, false"
  })
  void idValidTests(String country, boolean isValid) {
    //Arrange

    //Act
    boolean valid = countryFormatValidator.isValid(country, null);

    //Assert
    assertThat(valid, is(isValid));

  }

}