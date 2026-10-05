/*
 * Copyright (c) 2023 Whitbread plc. All rights reserved.
 */
package uk.co.whitbread.ohip.domain.model.reservation.in;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

class ConfirmReservationRequestTest extends BaseValidation {

  @Test
  void constructor_emptyHotelId_shouldSelfValidateAndThrow() {
    String expectedMessage = "hotelId: must not be empty";

    checkErrorThrown(() -> ConfirmReservationRequest.builder()
        .reservationId("1234")
        .paymentOption(PaymentOption.PAY_ON_ARRIVAL)
        .paymentMethod("DVA")
        .paymentCard(ReservationTestUtils.mockPaymentCard())
        .build(), expectedMessage);
  }

  @Test
  void constructor_emptyReservationId_shouldSelfValidateAndThrow() {
    String expectedMessage = "reservationId: must not be empty";

    checkErrorThrown(() -> ConfirmReservationRequest.builder()
        .hotelId("LONEUS")
        .paymentOption(PaymentOption.PAY_ON_ARRIVAL)
        .paymentMethod("DVA")
        .paymentCard(ReservationTestUtils.mockPaymentCard())
        .build(), expectedMessage);
  }

  @Test
  void constructor_emptyPaymentCard_shouldSelfValidateAndThrow() {
    String expectedMessage = "paymentOption: must not be null";
    checkErrorThrown(() -> ConfirmReservationRequest.builder()
        .reservationId("1234")
        .hotelId("LONEUS")
        .paymentMethod("DVA")
        .build(), expectedMessage);
  }

  @Test
  void constructor_shouldValidateSelfOk() {
    assertDoesNotThrow(() -> {
      ConfirmReservationRequest.builder()
          .reservationId("1234")
          .hotelId("LONEUS")
          .paymentOption(PaymentOption.PAY_ON_ARRIVAL)
          .paymentMethod("DVA")
          .paymentCard(ReservationTestUtils.mockPaymentCard())
          .build();
    });
  }

}