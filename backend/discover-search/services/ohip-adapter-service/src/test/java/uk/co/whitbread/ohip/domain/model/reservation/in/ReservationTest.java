/*
 * Copyright (c) 2023 Whitbread plc. All rights reserved.
 */
package uk.co.whitbread.ohip.domain.model.reservation.in;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

class ReservationTest extends BaseValidation {

  private final RoomRateReservation roomRateReservation = ReservationTestUtils.mockRoomRateReservation();

  @Test
  void constructor_emptyHotelId_shouldSelfValidateAndThrow() {
    String expectedMessage = "hotelId: must not be empty";

    checkErrorThrown(() -> Reservation.builder()
        .arrival("2015-10-20")
        .departure("2015-10-22")
        .externalReferenceId("12345")
        .roomRates(roomRateReservation)
        .adults(2)
        .children(0)
        .build(), expectedMessage);
  }

  @Test
  void constructor_emptyArrival_shouldSelfValidateAndThrow() {
    String expectedMessage = "arrival: must not be empty";

    checkErrorThrown(() -> Reservation.builder()
        .hotelId("LONEUS")
        .departure("2015-10-22")
        .externalReferenceId("12345")
        .roomRates(roomRateReservation)
        .adults(2)
        .children(0)
        .build(), expectedMessage);
  }

  @Test
  void constructor_emptyDeparture_shouldSelfValidateAndThrow() {
    String expectedMessage = "departure: must not be empty";

    checkErrorThrown(() -> Reservation.builder()
        .hotelId("LONEUS")
        .arrival("2015-10-20")
        .externalReferenceId("12345")
        .roomRates(roomRateReservation)
        .adults(2)
        .children(0)
        .build(), expectedMessage);
  }

  @Test
  void constructor_emptyExternalReferenceId_shouldSelfValidateAndThrow() {
    String expectedMessage = "externalReferenceId: must not be empty";

    checkErrorThrown(() -> Reservation.builder()
        .hotelId("LONEUS")
        .arrival("2015-10-20")
        .departure("2015-10-22")
        .roomRates(roomRateReservation)
        .adults(2)
        .children(0)
        .build(), expectedMessage);
  }

  @Test
  void constructor_emptyRoomRates_shouldSelfValidateAndThrow() {
    String expectedMessage = "roomRates: must not be null";

    checkErrorThrown(() -> Reservation.builder()
        .hotelId("LONEUS")
        .arrival("2015-10-20")
        .departure("2015-10-22")
        .externalReferenceId("12345")
        .adults(2)
        .children(0)
        .build(), expectedMessage);
  }

  @Test
  void constructor_emptyAdults_shouldSelfValidateAndThrow() {
    String expectedMessage = "adults: must not be null";

    checkErrorThrown(() -> Reservation.builder()
        .hotelId("LONEUS")
        .arrival("2015-10-20")
        .departure("2015-10-22")
        .externalReferenceId("12345")
        .roomRates(roomRateReservation)
        .children(0)
        .build(), expectedMessage);
  }


  @Test
  void constructor_shouldValidateSelfOk() {
    assertDoesNotThrow((Executable) ReservationTestUtils::mockReservation);
  }

}