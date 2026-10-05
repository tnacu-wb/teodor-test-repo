package uk.co.whitbread.payapp.domain.model.in;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.payapp.domain.model.validation.ValidatorFactory;

@ExtendWith(MockitoExtension.class)
class SelfValidationTest {

  @SuppressWarnings("unused")
  private final ValidatorFactory validatorFactory = ValidatorFactory.getInstance(
      Validation.buildDefaultValidatorFactory()
          .getValidator());

  @Test
  void constructor_invalidEmail_shouldSelfValidateAndThrow() {
    String expectedMessage = "email: must not be empty";

    checkErrorThrown(() -> InitializeApplicationRequest.builder().build(), expectedMessage);
  }

  @Test
  void constructor_getAppLookupRequest_invalidLookupNames_shouldSelfValidateAndThrow() {
    String expectedMessage = "lookupNames: must not be empty";

    checkErrorThrown(() -> GetAppLookupRequest.builder().build(), expectedMessage);
  }

  @Test
  void constructor_deletePayApplicationRequest_invalidApplicationId_shouldSelfValidateAndThrow() {
    String expectedMessage = "applicationId: must not be empty";

    checkErrorThrown(() -> DeletePayApplicationRequest.builder().applicationGuid("guid").build(), expectedMessage);
  }

  @Test
  void constructor_deletePayApplicationRequest_invalidApplicationGuid_shouldSelfValidateAndThrow() {
    String expectedMessage = "applicationGuid: must not be empty";

    checkErrorThrown(() -> DeletePayApplicationRequest.builder().applicationId("id").build(), expectedMessage);
  }

  private void checkErrorThrown(Executable executable, String expectedMessage) {

    var thrownException = assertThrowsExactly(ConstraintViolationException.class, executable);
    String actualMessage = thrownException.getMessage();

    assertEquals(expectedMessage, actualMessage);
  }
}