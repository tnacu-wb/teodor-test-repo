package uk.co.whitbread.basket.domain.model.payments.in;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.basket.domain.model.validation.ValidatorFactory;
import uk.co.whitbread.basket.utils.TestUtils;

class PaymentTest {

  private final ValidatorFactory validatorFactory = ValidatorFactory.getInstance(
      Validation.buildDefaultValidatorFactory().getValidator()
  );

  @Test
  void verifyMandatoryFields() {
    assertDoesNotThrow(() -> Payment.builder()
        .billing(Billing.builder()
            .address(Address.builder()
                .line1("120 Holborn")
                .line2("")
                .line3("")
                .line4("")
                .countryCode("gb")
                .postalCode("EC1N 2TD")
                .build())
            .email("email@whitbread.com")
            .firstName("Samuel")
            .lastName("Whitbread")
            .telephone("0777777777")
            .title("Mr")
            .build())
        .card(Card.builder()
            .cardholderName("Samuel Whitbread")
            .cardType("cardType")
            .cnpRequired(false)
            .expiryMonth("01")
            .expiryYear("24")
            .logoUrl("https://www.premierinn.com/logo")
            .token("4943056398164344242")
            .type("type")
            .build())
        .environment("https://www.premierinn.com")
        .subType("ECOMM")
        .type("CARD")
        .build());
  }

  @Test
  void verifyErrorMessageWhenMandatoryFieldsAreNotSet() {

    String[] errors = {"type: must not be empty",
        "subType: must not be empty"
    };

    TestUtils.checkErrorThrown(() -> Payment.builder().build(), errors);

  }

}
