package uk.co.whitbread.token.domain.model.validation;

import static org.junit.jupiter.api.Assertions.assertThrows;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DomainValidatorTest {

  private TestDomainValidator validator;

  @BeforeEach
  void setUp() {
    ValidatorFactory.getInstance(Validation.buildDefaultValidatorFactory().getValidator());
    validator = new TestDomainValidator();
  }

  @Test
  void validateSelf_validData_shouldNotThrowException() {
    // Arrange
    validator.setValidData();

    // Act & Assert - should not throw exception
    validator.validateSelf();
  }

  @Test
  void validateSelf_invalidData_shouldThrowConstraintViolationException() {
    // Arrange - Reset and set up validator properly for this test
    resetSingleton();
    ValidatorFactory.getInstance(Validation.buildDefaultValidatorFactory().getValidator());
    TestDomainValidator testValidator = new TestDomainValidator();
    testValidator.setInvalidData();

    // Act & Assert
    assertThrows(ConstraintViolationException.class, testValidator::validateSelf);
  }

  @Test
  void validateSelf_nullValidator_shouldNotThrowException() {
    // Arrange - Reset singleton and set to null BEFORE creating validator instance
    resetSingleton();
    ValidatorFactory.getInstance(null);
    TestDomainValidator validatorWithNullValidator = new TestDomainValidator();
    validatorWithNullValidator.setInvalidData(); // Use invalid data to prove validation is skipped

    // Act & Assert - should not throw exception when validator is null
    validatorWithNullValidator.validateSelf();
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

  // Test implementation of DomainValidator
  private static class TestDomainValidator extends DomainValidator<TestDomainValidator> {

    @jakarta.validation.constraints.NotEmpty
    private String validField;

    public void setValidData() {
      this.validField = "valid";
    }

    public void setInvalidData() {
      this.validField = ""; // This will trigger @NotEmpty validation
    }
  }
}
