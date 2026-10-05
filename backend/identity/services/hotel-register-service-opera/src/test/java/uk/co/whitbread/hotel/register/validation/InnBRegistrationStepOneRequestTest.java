package uk.co.whitbread.hotel.register.validation;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.hotel.register.model.InnBCompanyAddress;
import uk.co.whitbread.hotel.register.model.InnBRegistrationStepOneRequest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class InnBRegistrationStepOneRequestTest {

  private static Validator validator;

  @BeforeAll
  static void setupValidator() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  void language_shouldAcceptEnOrDe() {
    InnBRegistrationStepOneRequest req = new InnBRegistrationStepOneRequest();
    req.setEmail("test@example.com");
    req.setCompanyName("Test Company");
    var address = new InnBCompanyAddress();
    address.setCountryCode("GB");
    address.setLine1("123 Test Street");
    req.setAddress(address);
    req.setLanguage("en");

    Set<ConstraintViolation<InnBRegistrationStepOneRequest>> violations = validator.validate(req);
    assertTrue(violations.isEmpty());

    req.setLanguage("de");
    violations = validator.validate(req);
    assertTrue(violations.isEmpty());
  }

  @Test
  void language_shouldRejectOtherValues() {
    InnBRegistrationStepOneRequest req = new InnBRegistrationStepOneRequest();
    req.setEmail("test@example.com");
    req.setCompanyName("Test Company");
    req.setAddress(new InnBCompanyAddress());
    req.setLanguage("fr");

    Set<ConstraintViolation<InnBRegistrationStepOneRequest>> violations = validator.validate(req);
    assertFalse(violations.isEmpty());
    assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("language")));
  }

}