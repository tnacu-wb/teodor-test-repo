/*
 * Copyright (c) 2023 Whitbread plc. All rights reserved.
 */
package uk.co.whitbread.ohip.domain.model.reservation.in;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

class BookerAddressTest {

  @Test
  void constructor_shouldValidateSelfOk_completeBookerAddress() {
    assertDoesNotThrow(() -> {
      BookerAddress.builder()
          .addressType("1234")
          .postalCode("123456")
          .addressLine1("SOME STREET")
          .addressLine2("SOME STREET")
          .addressLine3("SOME STREET")
          .addressLine4("SOME STREET")
          .countryCode("UK")
          .cityName("London")
          .companyName("WB")
          .build();
    });
  }

  @Test
  void constructor_shouldValidateSelfOk_emptyBookerAddress() {
    assertDoesNotThrow(() -> {
      BookerAddress.builder().build();
    });
  }

  @Test
  void constructor_shouldValidateSelfOk_partialBookerAddress() {
    assertDoesNotThrow(() -> {
      BookerAddress.builder()
          .addressType("1234")
          .cityName("London")
          .companyName("WB")
          .build();
    });
  }
}