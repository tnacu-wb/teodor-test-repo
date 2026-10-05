package uk.co.whitbread.content.domain.model.booking.in;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import uk.co.whitbread.content.utils.TestUtils;

class BookingInformationRequestTest {

  @Test
  void verifyMandatoryFields() {
    assertDoesNotThrow(() -> BookingInformationRequest.builder()
        .bookingFlowId("some-id")
        .country("gb")
        .language("en")
        .hotelId("DUBSOU")
        .build());
  }

  @Test
  void verifyErrorMessageWhenMandatoryFieldsAreNotSet() {

    String[] errors = {"country: must not be empty",
        "bookingFlowId: must not be empty",
        "language: must not be empty"};

    TestUtils.checkErrorThrown(() -> BookingInformationRequest.builder().build(), errors);

  }

}
