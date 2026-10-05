/*
 * Copyright (c) 2023 Whitbread plc. All rights reserved.
 */
package uk.co.whitbread.ohip.domain.model.reservation.in;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

class RoomRateReservationTest extends BaseValidation {

  @Test
  void constructor_emptyStart_shouldSelfValidateAndThrow() {
    String expectedMessage = "start: must not be empty";

    checkErrorThrown(() -> RoomRateReservation.builder()
        .end("2015-10-22")
        .roomType("DB")
        .ratePlanCode("DELUXE")
        .cellCode("ABC")
        .build(), expectedMessage);
  }

  @Test
  void constructor_emptyEnd_shouldSelfValidateAndThrow() {
    String expectedMessage = "end: must not be empty";

    checkErrorThrown(() -> RoomRateReservation.builder()
        .start("2015-10-20")
        .roomType("DB")
        .ratePlanCode("DELUXE")
        .cellCode("ABC")
        .build(), expectedMessage);
  }

  @Test
  void constructor_emptyRoomType_shouldSelfValidateAndThrow() {
    String expectedMessage = "roomType: must not be empty";

    checkErrorThrown(() -> RoomRateReservation.builder()
        .start("2015-10-20")
        .end("2015-10-22")
        .ratePlanCode("DELUXE")
        .cellCode("ABC")
        .build(), expectedMessage);
  }

  @Test
  void constructor_emptyRatePlanCode_shouldSelfValidateAndThrow() {
    String expectedMessage = "ratePlanCode: must not be empty";

    checkErrorThrown(() -> RoomRateReservation.builder()
        .start("2015-10-20")
        .end("2015-10-22")
        .roomType("DB")
        .cellCode("ABC")
        .build(), expectedMessage);
  }

  @Test
  void constructor_shouldValidateSelfOk() {
    assertDoesNotThrow((Executable) ReservationTestUtils::mockRoomRateReservation);
  }

}