/*
 * Copyright (c) 2023 Whitbread plc. All rights reserved.
 */
package uk.co.whitbread.ohip.domain.model.reservation.in;

import java.util.Collections;
import org.junit.jupiter.api.Test;

class CancelReservationRequestTest extends BaseValidation {

  @Test
  void constructor_emptyHotelId_shouldSelfValidateAndThrow() {
    String expectedMessage = "hotelId: must not be empty";

    checkErrorThrown(() -> CancelReservationRequest.builder()
        .reservationIds(Collections.singletonList("123445"))
        .paymentOption(PaymentOption.PAY_NOW)
        .build(), expectedMessage);
  }

  @Test
  void constructor_emptyReservationIds_shouldSelfValidateAndThrow() {
    String expectedMessage = "reservationIds: must not be empty";

    checkErrorThrown(() -> CancelReservationRequest.builder()
        .hotelId("LONEUS")
        .paymentOption(PaymentOption.PAY_ON_ARRIVAL)
        .build(), expectedMessage);
  }

}
