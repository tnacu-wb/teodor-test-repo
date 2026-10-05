package uk.co.whitbread.piba.registration.validation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class NullOrNotBlankValidatorTest {

  private NullOrNotBlankValidator validator;
  private ConstraintValidatorContext context;

  @BeforeEach
  void setUp() {
    validator = new NullOrNotBlankValidator();
    context = mock(ConstraintValidatorContext.class);
  }

  @Test
  void whenValueIsNull_thenIsValid() {
    assertTrue(
        validator.isValid(null, context),
        "Validator should return true for null input."
    );
  }

  @ParameterizedTest
  @ValueSource(strings = {"a", "  abc  ", "123", "\t value \n"})
  void whenValueIsNotBlank_thenIsValid(String value) {
    assertTrue(
        validator.isValid(value, context),
        "Validator should return true for non-blank string: '" +
            value +
            "'"
    );
  }

  @ParameterizedTest
  @ValueSource(strings = {"", " ", "   ", "\t", "\n", "\t\n"})
  void whenValueIsBlank_thenIsInvalid(String value) {
    assertFalse(
        validator.isValid(value, context),
        "Validator should return false for blank string: '" +
            value +
            "'"
    );
  }
}