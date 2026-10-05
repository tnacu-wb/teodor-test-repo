package uk.co.whitbread.payments.domain.model.in;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

class PaymentMethodsRequestSelfValidationTest {

  @Test
  void constructor_shouldSelfValidateOk() {
    assertDoesNotThrow(() -> {
      PaymentMethodsRequest.builder()
          .country("gb")
          .language("en")
          .basketReference("DUNCRO0764349")
          .build();
    });
  }

}
