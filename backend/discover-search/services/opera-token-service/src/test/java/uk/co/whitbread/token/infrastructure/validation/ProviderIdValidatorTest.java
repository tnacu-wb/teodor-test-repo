package uk.co.whitbread.token.infrastructure.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Unit tests for ProviderIdValidator.
 * Tests the validation logic for provider ID format validation.
 */
class ProviderIdValidatorTest {

  private ProviderIdValidator validator;

  @Mock
  private ConstraintValidatorContext context;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    validator = new ProviderIdValidator();
  }

  @Test
  @DisplayName("Should return true for valid alphanumeric provider ID")
  void isValid_validAlphanumeric_shouldReturnTrue() {
    // Given
    String validProviderId = "ohip123";

    // When
    boolean result = validator.isValid(validProviderId, context);

    // Then
    assertTrue(result);
  }

  @Test
  @DisplayName("Should return true for valid provider ID with hyphens")
  void isValid_validWithHyphens_shouldReturnTrue() {
    // Given
    String validProviderId = "opera-test";

    // When
    boolean result = validator.isValid(validProviderId, context);

    // Then
    assertTrue(result);
  }

  @Test
  @DisplayName("Should return true for valid provider ID with underscores")
  void isValid_validWithUnderscores_shouldReturnTrue() {
    // Given
    String validProviderId = "opera_test";

    // When
    boolean result = validator.isValid(validProviderId, context);

    // Then
    assertTrue(result);
  }

  @Test
  @DisplayName("Should return true for valid provider ID with mixed valid characters")
  void isValid_validMixedCharacters_shouldReturnTrue() {
    // Given
    String validProviderId = "opera-test_123";

    // When
    boolean result = validator.isValid(validProviderId, context);

    // Then
    assertTrue(result);
  }

  @Test
  @DisplayName("Should return true for single character valid provider ID")
  void isValid_singleCharacter_shouldReturnTrue() {
    // Given
    String validProviderId = "a";

    // When
    boolean result = validator.isValid(validProviderId, context);

    // Then
    assertTrue(result);
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "   "})
  @DisplayName("Should return false for empty or whitespace-only provider ID")
  void isValid_emptyOrWhitespace_shouldReturnFalse(String invalidProviderId) {
    // When
    boolean result = validator.isValid(invalidProviderId, context);

    // Then
    assertFalse(result);
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "provider@invalid",
      "provider.invalid",
      "provider invalid",
      "provider+invalid",
      "provider=invalid",
      "provider&invalid",
      "provider#invalid",
      "provider$invalid",
      "provider%invalid",
      "provider*invalid",
      "provider!invalid",
      "provider?invalid",
      "provider<invalid",
      "provider>invalid",
      "provider[invalid",
      "provider]invalid",
      "provider{invalid",
      "provider}invalid",
      "provider(invalid",
      "provider)invalid",
      "provider|invalid",
      "provider\\invalid",
      "provider/invalid",
      "provider:invalid",
      "provider;invalid",
      "provider\"invalid",
      "provider'invalid",
      "provider`invalid",
      "provider~invalid",
      "provider^invalid",
      "provider\ninvalid",
      "provider\rinvalid",
      "provider\tinvalid"
  })
  @DisplayName("Should return false for provider ID with invalid characters")
  void isValid_invalidCharacters_shouldReturnFalse(String invalidProviderId) {
    // When
    boolean result = validator.isValid(invalidProviderId, context);

    // Then
    assertFalse(result);
  }

  @Test
  @DisplayName("Should return false for null provider ID")
  void isValid_nullProviderId_shouldReturnFalse() {
    // When
    boolean result = validator.isValid(null, context);

    // Then
    assertFalse(result);
  }

  @Test
  @DisplayName("Should return true for valid provider ID with numbers only")
  void isValid_numbersOnly_shouldReturnTrue() {
    // Given
    String validProviderId = "123456";

    // When
    boolean result = validator.isValid(validProviderId, context);

    // Then
    assertTrue(result);
  }

  @Test
  @DisplayName("Should return true for valid provider ID with letters only")
  void isValid_lettersOnly_shouldReturnTrue() {
    // Given
    String validProviderId = "abcdef";

    // When
    boolean result = validator.isValid(validProviderId, context);

    // Then
    assertTrue(result);
  }

  @Test
  @DisplayName("Should return true for valid provider ID starting with underscore")
  void isValid_startingWithUnderscore_shouldReturnTrue() {
    // Given
    String validProviderId = "_private";

    // When
    boolean result = validator.isValid(validProviderId, context);

    // Then
    assertTrue(result);
  }

  @Test
  @DisplayName("Should return true for valid provider ID starting with hyphen")
  void isValid_startingWithHyphen_shouldReturnTrue() {
    // Given
    String validProviderId = "-special";

    // When
    boolean result = validator.isValid(validProviderId, context);

    // Then
    assertTrue(result);
  }

  @Test
  @DisplayName("Should return true for valid provider ID ending with underscore")
  void isValid_endingWithUnderscore_shouldReturnTrue() {
    // Given
    String validProviderId = "provider_";

    // When
    boolean result = validator.isValid(validProviderId, context);

    // Then
    assertTrue(result);
  }

  @Test
  @DisplayName("Should return true for valid provider ID ending with hyphen")
  void isValid_endingWithHyphen_shouldReturnTrue() {
    // Given
    String validProviderId = "provider-";

    // When
    boolean result = validator.isValid(validProviderId, context);

    // Then
    assertTrue(result);
  }
}
