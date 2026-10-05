package uk.co.whitbread.content.domain.model.booking.in;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import uk.co.whitbread.content.utils.TestUtils;

class RateInformationRequestTest {

  @Test
  void verifyMandatoryFields() {
    assertDoesNotThrow(() -> RateInformationRequest.builder()
        .brand("brand")
        .country("gb")
        .language("en")
        .build());
  }

  @Test
  void verifyErrorMessageWhenMandatoryFieldsAreNotSet() {

    String[] errors = {"country: must not be empty",
        "brand: must not be empty",
        "language: must not be empty"};

    TestUtils.checkErrorThrown(() -> RateInformationRequest.builder().build(), errors);

  }

}
