package uk.co.whitbread.content.domain.model.notes.business.in;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import uk.co.whitbread.content.domain.model.note.business.in.BusinessNotesRequest;
import uk.co.whitbread.content.utils.TestUtils;

class BusinessNotesRequestTest {

  @Test
  void verifyMandatoryFields() {
    assertDoesNotThrow(() -> BusinessNotesRequest.builder()
        .lang("en")
        .build());
  }

  @Test
  void verifyErrorMessageWhenMandatoryFieldsAreNotSet() {
    String[] errors = {"lang: must not be empty"};
    TestUtils.checkErrorThrown(() -> BusinessNotesRequest.builder().build(), errors);

  }
}
