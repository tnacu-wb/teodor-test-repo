/*
 * Copyright (c) 2023 Whitbread plc. All rights reserved.
 */
package uk.co.whitbread.ohip.domain.model.reservation.in;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import java.util.Collections;
import org.junit.jupiter.api.Test;

class ReservationRequestTest extends BaseValidation {

  @Test
  void constructor_shouldValidateSelfOk() {
    assertDoesNotThrow(() -> {
      new ReservationRequest(
          Collections.singletonList(ReservationTestUtils.mockReservation()),
          BookingChannel.builder().channel("PI").subchannel("WEB").language("EN").build(), true);
    });
  }

  @Test
  void constructor_shouldValidateSelfAndThrow() {
    String expectedMessage = "bookingChannel: must not be null";
    checkErrorThrown(() -> {
      new ReservationRequest(Collections.singletonList(ReservationTestUtils.mockReservation()),
          null, false);
    }, expectedMessage);
  }

}