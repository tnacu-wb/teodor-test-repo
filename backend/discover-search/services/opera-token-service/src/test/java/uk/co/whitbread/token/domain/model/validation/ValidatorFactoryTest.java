package uk.co.whitbread.token.domain.model.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ValidatorFactoryTest {

  @BeforeEach
  void setUp() {
    // Reset singleton instance for each test
    resetSingleton();
  }

  @Test
  void getInstance_validValidator_shouldReturnSingleton() {
    // Arrange
    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    // Act
    ValidatorFactory instance1 = ValidatorFactory.getInstance(validator);
    ValidatorFactory instance2 = ValidatorFactory.getInstance(validator);

    // Assert
    assertNotNull(instance1);
    assertNotNull(instance2);
    assertEquals(instance1, instance2); // Same instance (singleton)
  }

  @Test
  void getInstance_nullValidator_shouldReturnSingleton() {
    // Act
    ValidatorFactory instance1 = ValidatorFactory.getInstance(null);
    ValidatorFactory instance2 = ValidatorFactory.getInstance(null);

    // Assert
    assertNotNull(instance1);
    assertNotNull(instance2);
    assertEquals(instance1, instance2); // Same instance (singleton)
  }

  @Test
  void getValidator_withInitializedInstance_shouldReturnValidator() {
    // Arrange
    Validator expectedValidator = Validation.buildDefaultValidatorFactory().getValidator();
    ValidatorFactory.getInstance(expectedValidator);

    // Act
    Validator actualValidator = ValidatorFactory.getValidator();

    // Assert
    assertNotNull(actualValidator);
    assertEquals(expectedValidator, actualValidator);
  }

  @Test
  void getValidator_withoutInitializedInstance_shouldReturnNull() {
    // Act
    Validator validator = ValidatorFactory.getValidator();

    // Assert
    assertNull(validator);
  }

  @Test
  void getValidator_withNullValidator_shouldReturnNull() {
    // Arrange
    ValidatorFactory.getInstance(null);

    // Act
    Validator validator = ValidatorFactory.getValidator();

    // Assert
    assertNull(validator);
  }

  // Helper method to reset singleton for testing
  private void resetSingleton() {
    try {
      java.lang.reflect.Field instanceField = ValidatorFactory.class.getDeclaredField("instance");
      instanceField.setAccessible(true);
      instanceField.set(null, null);
    } catch (Exception e) {
      throw new RuntimeException("Failed to reset singleton", e);
    }
  }
}
