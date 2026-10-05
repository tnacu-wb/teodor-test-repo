package uk.co.whitbread.content.domain.model.labels.in;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import uk.co.whitbread.content.utils.TestUtils;

class LabelsRequestTest {

  @Test
  void verifyMandatoryFields() {
    assertDoesNotThrow(() -> LabelsRequest.builder()
        .category(CategoryEnum.BOOKING)
        .country("gb")
        .language("en")
        .build());
  }

  @Test
  void verifyErrorMessageWhenMandatoryFieldsAreNotSet() {

    String[] errors = {"category: must not be null",
        "country: must not be empty",
        "language: must not be empty"};

    TestUtils.checkErrorThrown(() -> LabelsRequest.builder().build(), errors);

  }

}
