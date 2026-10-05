package uk.co.whitbread.rules.agent.domain.model.in;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.rules.agent.domain.model.validation.ValidatorFactory;
import uk.co.whitbread.rules.agent.utils.TestUtils;

class RoomSubstitutionRuleRequestSelfValidationTest {

  private final ValidatorFactory validatorFactory = ValidatorFactory.getInstance(
      Validation.buildDefaultValidatorFactory()
          .getValidator());

  @Test
  void verifyMandatoryFields() {
    assertDoesNotThrow(RoomSubstitutionRuleRequest.builder()
        .adults(2)
        .children(0)
        .roomType("Double")
        .pms("OP").channel("PI")::build);
  }

  @Test
  void verifyErrorMessageWhenMandatoryFieldsAreNotSet() {
    String[] expectedMessage = {"adults: must be greater than or equal to 1",
        "children: must be less than or equal to 3",
        "roomType: must not be empty",
        "pms: must not be empty"};

    TestUtils.checkErrorThrown(RoomSubstitutionRuleRequest.builder()
            .adults(0)
            .children(4)
            .roomType("")
            .pms("")::build,
        expectedMessage);
  }

  @Test
  void verifyErrorMessageWhenMandatoryFieldsAreMissing() {
    String[] expectedMessage = {"adults: must not be null",
        "children: must not be null",
        "roomType: must not be empty",
        "pms: must not be empty"};

    TestUtils.checkErrorThrown(RoomSubstitutionRuleRequest.builder()
            .adults(null)
            .children(null)
            .roomType("")
            .pms("")::build,
        expectedMessage);
  }

}
