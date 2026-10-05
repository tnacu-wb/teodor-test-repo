package uk.co.whitbread.infrastructure.rest.controller.validation;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LanguageFormatValidatorTest {

  private LanguageFormatValidator languageFormatValidator = new LanguageFormatValidator();

  @ParameterizedTest
  @CsvSource(value = {
      "en, true",
      "enn, false",
      "null, false"
  }, nullValues = {"null"})
  void idValidTests(String language, boolean isValid) {
    //Arrange

    //Act
    boolean valid = languageFormatValidator.isValid(language, null);

    //Assert
    assertThat(valid, is(isValid));
  }

}