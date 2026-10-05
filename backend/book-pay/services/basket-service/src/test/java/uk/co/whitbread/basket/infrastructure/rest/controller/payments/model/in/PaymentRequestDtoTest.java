package uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.in;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.basket.domain.model.validation.ValidatorFactory;

class PaymentRequestDtoTest {

  private final ValidatorFactory validatorFactory = ValidatorFactory.getInstance(
      Validation.buildDefaultValidatorFactory().getValidator()
  );

  @Test
  void verifyNoExceptionIsThrown() {
    assertDoesNotThrow(() -> PaymentRequestDto.builder()
        .payment(PaymentDto.builder().subType("subtype").type("type")
            .billing(BillingDto.builder().address(
                AddressDto.builder()
                    .addressLine1("120 Holborn")
                    .country("GB")
                    .postalCode("L1 8JQ")
                    .build()).build()).build()).booking(
            BookingDto.builder().channel("channel").journey("journey").type("type")
                .businessSite(BusinessSiteDto.builder().type("type").identifier("identifier")
                    .build()).build()).build());
  }

  @Test
  void verifyNoExceptionIsThrown_addressHasSpecialCharacters_ForDistr() {
    assertDoesNotThrow(() -> PaymentRequestDto.builder()
        .payment(PaymentDto.builder().subType("subtype").type("type")
            .billing(BillingDto.builder().address(
                AddressDto.builder()
                    .addressLine1("120 Holborn\r")
                    .addressLine2("120 Holborn%")
                    .addressLine1("120 Holborn\t")
                    .addressLine1("120 Holborn\n")
                    .country("GB")
                    .postalCode("L1 8JQ")
                    .build()).build()).build()).booking(
            BookingDto.builder().channel("DISTR").journey("journey").type("type")
                .businessSite(BusinessSiteDto.builder().type("type").identifier("identifier")
                    .build()).build()).build());
  }
}
