/*
 * Copyright (c) 2023 Whitbread plc. All rights reserved.
 */
package uk.co.whitbread.ohip.domain.model.reservation.in;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import java.util.Collections;
import org.junit.jupiter.api.Test;

class ReservationGuestRequestTest extends BaseValidation {

  @Test
  void constructor_emptyHotelId_shouldSelfValidateAndThrow() {
    String expectedMessage = "hotelId: must not be empty";

    checkErrorThrown(() -> ReservationGuestRequest.builder()
        .booker(ReservationTestUtils.mockBookerDetails())
        .stayingGuests(Collections.singletonList(ReservationTestUtils.mockReservationGuests()))
        .reasonForStay("LEI")
        .build(), expectedMessage);
  }

  @Test
  void constructor_emptyReservationGuests_shouldSelfValidateAndThrow() {
    String expectedMessage = "stayingGuests: must not be empty";

    checkErrorThrown(() -> ReservationGuestRequest.builder()
        .booker(ReservationTestUtils.mockBookerDetails())
        .hotelId("12345")
        .reasonForStay("LEI")
        .build(), expectedMessage);
  }

  @Test
  void constructor_emptyBooker_shouldSelfValidateAndThrow() {
    String expectedMessage = "booker: must not be null";

    checkErrorThrown(() -> ReservationGuestRequest.builder()
        .stayingGuests(Collections.singletonList(ReservationTestUtils.mockReservationGuests()))
        .hotelId("12345")
        .reasonForStay("LEI")
        .build(), expectedMessage);
  }

  @Test
  void constructor_emptyReasonForStay_shouldSelfValidateAndThrow() {
    String expectedMessage = "reasonForStay: must not be empty";

    checkErrorThrown(() -> ReservationGuestRequest.builder()
        .booker(ReservationTestUtils.mockBookerDetails())
        .stayingGuests(Collections.singletonList(ReservationTestUtils.mockReservationGuests()))
        .hotelId("12345")
        .build(), expectedMessage);
  }

  @Test
  void constructor_shouldValidateSelfOk() {
    assertDoesNotThrow(() -> {
      ReservationGuestRequest.builder()
          .hotelId("12345")
          .reasonForStay("LEI")
          .booker(ReservationTestUtils.mockBookerDetails())
          .stayingGuests(Collections.singletonList(ReservationTestUtils.mockReservationGuests()))
          .build();
    });
  }

}