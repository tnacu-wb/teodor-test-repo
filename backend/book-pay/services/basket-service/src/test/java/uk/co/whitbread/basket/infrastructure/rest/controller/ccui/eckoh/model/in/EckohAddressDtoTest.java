package uk.co.whitbread.basket.infrastructure.rest.controller.ccui.eckoh.model.in;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

class EckohAddressDtoTest {

  @Test
  void verifyFields() {
    assertDoesNotThrow(() -> EckohAddressDto.builder()
        .line1("line1")
        .line2("line2")
        .line3("line3")
        .line4("line4")
        .countryCode("GB")
        .postalCode("postalCode")
        .build());
  }

}
