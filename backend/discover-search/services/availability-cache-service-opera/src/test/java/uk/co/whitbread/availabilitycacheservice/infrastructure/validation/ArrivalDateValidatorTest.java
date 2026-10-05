package uk.co.whitbread.availabilitycacheservice.infrastructure.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ArrivalDateValidatorTest {

  private ArrivalDateValidator validator;
  private ConstraintValidatorContext context;

  @BeforeEach
  void setUp() {
    validator = new ArrivalDateValidator();
    validator.setFormat("yyyy-MM-dd");
    context = mock(ConstraintValidatorContext.class);
  }

  @Test
  void testIsValidDateFormat_ValidDate() {
    assertTrue(validator.isValidDateFormat("2025-08-12"));
  }

  @Test
  void testIsValidDateFormat_InvalidDate() {
    assertFalse(validator.isValidDateFormat( LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))));
  }

  @Test
  void testIsValidDateFormat_NullDate() {
    assertFalse(validator.isValidDateFormat(null));
  }

  @Test
  void testIsValid_NullDate() {
    assertFalse(validator.isValid(null, context));
  }

  @Test
  void testIsValid_Today() {
    String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    assertTrue(validator.isValid(today, context));
  }

  @Test
  void testIsValid_PastDate() {
    String pastDate = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    assertFalse(validator.isValid(pastDate, context));
  }

  @Test
  void testIsValid_FutureDate() {
    String futureDate = LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    assertTrue(validator.isValid(futureDate, context));
  }
}
