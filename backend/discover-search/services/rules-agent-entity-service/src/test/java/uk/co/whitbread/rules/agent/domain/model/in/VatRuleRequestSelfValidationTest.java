package uk.co.whitbread.rules.agent.domain.model.in;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.Arrays;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.rules.agent.domain.model.validation.ValidatorFactory;
import uk.co.whitbread.rules.agent.utils.TestUtils;

class VatRuleRequestSelfValidationTest {

  private final ValidatorFactory validatorFactory = ValidatorFactory.getInstance(
      Validation.buildDefaultValidatorFactory()
          .getValidator());

  @Test
  void verifyMandatoryFields() {
    assertDoesNotThrow(VatRuleRequest.builder()
        .vatRegion("UK")
        .pkgCodeArr(Arrays.asList("MD2DIN", "MDBEVA", "FI24HR"))::build);
  }

  @Test
  void verifyErrorMessageWhenMandatoryFieldsAreNotSet() {
    String[] expectedMessage = {"vatRegion: must not be empty",
        "pkgCodeArr: must not be empty"};

    TestUtils.checkErrorThrown(VatRuleRequest.builder()
            .vatRegion("")
            .pkgCodeArr(null)::build,
        expectedMessage);
  }

}
