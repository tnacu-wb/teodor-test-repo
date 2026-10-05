package uk.co.whitbread.rules.agent.domain.model.in;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;


import org.junit.jupiter.api.Test;

import uk.co.whitbread.rules.agent.utils.TestUtils;

class OccupancySupplementRequestTest {

  @Test
  void verifyMandatoryFields() {
    assertDoesNotThrow(OccupancySupplementRequest.builder()
        .hotelId("FRAMTI")::build);
  }

  @Test
  void verifyErrorMessageWhenMandatoryFieldsAreNotSet() {
    String[] expectedMessage = {"hotelId: must not be empty"};

    TestUtils.checkErrorThrown(OccupancySupplementRequest.builder()
            .hotelId("")::build,
        expectedMessage);
  }
}