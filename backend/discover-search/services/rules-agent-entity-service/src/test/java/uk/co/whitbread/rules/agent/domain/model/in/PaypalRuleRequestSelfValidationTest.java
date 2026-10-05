package uk.co.whitbread.rules.agent.domain.model.in;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

class PaypalRuleRequestSelfValidationTest {

  @Test
  void constructor__shouldSelfValidateOk() {

    assertDoesNotThrow(() -> {
      PaypalRuleRequest.builder()
          .channelId("PI")
          .country("GB")
          .hotelId("testHotel")
          .build();
    });
  }
}
