package uk.co.whitbread.content.domain.model.index.header.data.in;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import uk.co.whitbread.content.utils.TestUtils;

class IndexHeaderDataRequestTest {

  @Test
  void verifyMandatoryFields() {
    assertDoesNotThrow(() -> IndexHeaderDataRequest.builder()
        .country("gb")
        .language("en")
        .build());
  }

  @Test
  void verifyErrorMessageWhenMandatoryFieldsAreNotSet() {

    String[] errors = {"country: must not be empty",
        "language: must not be empty"};

    TestUtils.checkErrorThrown(() -> IndexHeaderDataRequest.builder().build(), errors);
  }

}
