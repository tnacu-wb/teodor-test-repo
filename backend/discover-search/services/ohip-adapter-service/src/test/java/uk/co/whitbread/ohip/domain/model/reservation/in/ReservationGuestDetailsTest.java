/*
 * Copyright (c) 2023 Whitbread plc. All rights reserved.
 */
package uk.co.whitbread.ohip.domain.model.reservation.in;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

class ReservationGuestDetailsTest extends BaseValidation {

  @Test
  void constructor_emptyReservationId_shouldSelfValidateAndThrow() {
    String expectedMessage = "reservationId: must not be empty";

    checkErrorThrown(() -> StayingGuest.builder()
        .sameAsBooker(false)
        .stayingGuestDetails(ReservationTestUtils.mockGuestDetails())
        .build(), expectedMessage);
  }

  @Test
  void constructor_emptyGuest_shouldSelfValidateSelfOk() {

    assertDoesNotThrow(() -> {
      StayingGuest.builder()
          .reservationId("12345")
          .sameAsBooker(true)
          .build();
    });
  }

  @Test
  void constructor_shouldValidateSelfOk() {
    assertDoesNotThrow(() -> {
      StayingGuest.builder()
          .reservationId("12345")
          .sameAsBooker(false)
          .stayingGuestDetails(ReservationTestUtils.mockGuestDetails())
          .build();
    });
  }

}