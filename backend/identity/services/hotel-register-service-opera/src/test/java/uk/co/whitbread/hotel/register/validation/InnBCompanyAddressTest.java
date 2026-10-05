package uk.co.whitbread.hotel.register.validation;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.hotel.register.model.InnBCompanyAddress;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class InnBCompanyAddressTest {

  private static Validator validator;

  @BeforeAll
  static void setupValidator() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  void countryCode_shouldAcceptValidCodes() {
    InnBCompanyAddress address = InnBCompanyAddress.builder()
        .line1("Test Street")
        .countryCode("DE")
        .build();

    Set<ConstraintViolation<InnBCompanyAddress>> violations = validator.validate(address);
    assertTrue(violations.isEmpty());

    address.setCountryCode("GBR");
    violations = validator.validate(address);
    assertTrue(violations.isEmpty());
  }

  @Test
  void countryCode_shouldRejectInvalidCodes() {
    InnBCompanyAddress address = InnBCompanyAddress.builder()
        .line1("Test Street")
        .countryCode("de") // lowercase
        .build();

    Set<ConstraintViolation<InnBCompanyAddress>> violations = validator.validate(address);
    assertFalse(violations.isEmpty());

    address.setCountryCode("GERM"); // too long
    violations = validator.validate(address);
    assertFalse(violations.isEmpty());

    address.setCountryCode("D3"); // contains digit
    violations = validator.validate(address);
    assertFalse(violations.isEmpty());

    address.setCountryCode(""); // empty
    violations = validator.validate(address);
    assertFalse(violations.isEmpty());
  }
}