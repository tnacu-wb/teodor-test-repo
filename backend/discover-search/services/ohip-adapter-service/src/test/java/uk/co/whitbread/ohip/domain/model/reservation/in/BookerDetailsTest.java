/*
 * Copyright (c) 2023 Whitbread plc. All rights reserved.
 */
package uk.co.whitbread.ohip.domain.model.reservation.in;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

class BookerDetailsTest extends BaseValidation {

  @Test
  void constructor_emptyFirstName_shouldSelfValidateAndThrow() {
    String expectedMessage = "firstName: must not be empty";

    checkErrorThrown(() -> BookerDetails.builder()
        .title("LONEUS")
        .lastName("Doe")
        .emailAddress("john_doe@whitbread.com")
        .address(ReservationTestUtils.mockBookerAddress())
        .acceptFutureMailing(Boolean.FALSE)
        .build(), expectedMessage);
  }

  @Test
  void constructor_emptyLastName_shouldSelfValidateAndThrow() {
    String expectedMessage = "lastName: must not be empty";

    checkErrorThrown(() -> BookerDetails.builder()
        .title("LONEUS")
        .firstName("Doe")
        .emailAddress("john_doe@whitbread.com")
        .address(ReservationTestUtils.mockBookerAddress())
        .acceptFutureMailing(Boolean.FALSE)
        .build(), expectedMessage);
  }

  @Test
  void constructor_shouldValidateSelfOk() {
    assertDoesNotThrow(() -> {
      BookerDetails.builder()
          .title("LONEUS")
          .firstName("John")
          .lastName("Doe")
          .emailAddress("john_doe@whitbread.com")
          .address(ReservationTestUtils.mockBookerAddress())
          .acceptFutureMailing(Boolean.FALSE)
          .build();
    });
  }

  @Test
  void constructor_missingAddress_shouldValidateSelfOk() {
    assertDoesNotThrow(() -> {
      BookerDetails.builder()
          .title("LONEUS")
          .firstName("John")
          .lastName("Doe")
          .emailAddress("john_doe@whitbread.com")
          .acceptFutureMailing(Boolean.FALSE)
          .build();
    });
  }

}