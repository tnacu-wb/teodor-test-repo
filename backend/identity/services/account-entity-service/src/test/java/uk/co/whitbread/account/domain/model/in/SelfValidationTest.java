package uk.co.whitbread.account.domain.model.in;

import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.Assert;
import uk.co.whitbread.account.domain.model.validation.ValidatorFactory;
import uk.co.whitbread.account.infrastructure.rest.client.customers.model.in.CustomerRegistrationRequestDto;

@ExtendWith(MockitoExtension.class)
class SelfValidationTest {

  private ValidatorFactory validatorFactory = ValidatorFactory.getInstance(
      Validation.buildDefaultValidatorFactory()
          .getValidator());

  @Test
  void constructor_missingCaptcha_shouldSelfValidateAndThrow() {
    String expectedMessage = "captcha: must not be empty";

    checkErrorThrown(() -> {
      CustomerRegistrationRequestDto.builder().captcha("").build();
    }, expectedMessage);
  }

  @Test
  void constructor_missingPassword_shouldSelfValidateAndThrow() {
    String expectedMessage = "password: must not be empty";

    checkErrorThrown(() -> {
      CustomerRegistrationRequestDto.builder()
          .captcha("Test Captch")
          .password("")
          .build();
    }, expectedMessage);
  }
  @Test
  void constructor_invalidContactDetails_shouldSelfValidateAndThrow() {
    String expectedMessage = "contactDetail: must not be null";

    checkErrorThrown(() -> {
      CustomerRegistrationRequestDto.builder()
          .captcha("Test Captch")
          .password("Test password")
          .contactDetail(null)
          .build();
    }, expectedMessage);
  }

  private void checkErrorThrown(Executable executable, String expectedMessage) {
    var thrownException = assertThrowsExactly(ConstraintViolationException.class, executable);
    String actualMessage = thrownException.getMessage();

    Assert.hasText(expectedMessage, actualMessage);
  }
}