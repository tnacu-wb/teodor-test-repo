/*
 * Copyright (c) 2023 Whitbread plc. All rights reserved.
 */
package uk.co.whitbread.ohip.domain.model.reservation.in;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

class PaymentCardTest extends BaseValidation {
  @Test
  void constructor_emptyCardType_shouldSelfValidateAndThrow() {
    String expectedMessage = "cardType: must not be empty";

    checkErrorThrown(() -> PaymentCard.builder()
        .token("12344455565234")
        .cardHolderName("JOHN DOE")
        .expirationDate("2023-12-03")
        .build(), expectedMessage);
  }

  @Test
  void constructor_emptyToken_shouldSelfValidateAndThrow() {
    String expectedMessage = "token: must not be empty";

    checkErrorThrown(() -> PaymentCard.builder()
        .cardType("VISA")
        .cardHolderName("JOHN DOE")
        .expirationDate("2023-12-03")
        .build(), expectedMessage);
  }
/*

  @Ignore
  @Test
  void constructor_emptyCardHolderName_shouldSelfValidateAndThrow() {
    String expectedMessage = "cardHolderName: must not be empty";

    checkErrorThrown(() -> PaymentCard.builder()
        .cardType("VISA")
        .token("12344455565234")
        .expirationDate("2023-12-03")
        .build(), expectedMessage);
  }
*/

  @Test
  void constructor_emptyExpirationDate_shouldSelfValidateAndThrow() {
    String expectedMessage = "expirationDate: must not be empty";

    checkErrorThrown(() -> PaymentCard.builder()
        .cardType("VISA")
        .token("12344455565234")
        .cardHolderName("JOHN DOE")
        .build(), expectedMessage);
  }

  @Test
  void constructor_shouldValidateSelfOk() {
    assertDoesNotThrow(() -> {
      PaymentCard.builder()
          .cardType("VISA")
          .token("12344455565234")
          .cardHolderName("JOHN DOE")
          .expirationDate("2023-12-03")
          .build();
    });
  }

}