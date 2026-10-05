package uk.co.whitbread.basket.infrastructure.rest.controller.payments.mapper;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.basket.domain.model.payments.in.PaymentRequest;
import uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.in.AddressDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.in.BillingDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.in.BookingDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.in.BusinessSiteDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.in.PaymentDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.in.PaymentRequestDto;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
    PaymentMapperImpl.class,
    AddressMapperRequestImpl.class
})
class PaymentMapperTest {

  @Autowired
  private PaymentMapper paymentMapper;

  @ParameterizedTest(name = "isCiol={0}, type={1}, initialValue={2} => pibaCardPresent={3}")
  @CsvSource({
      "true, PIBA, true, false",
      "true, piba, true, false",
      "true, PiBa, true, false",
      "true, Piba, true, false",
      "false, PIBA, true, true",
      ", PIBA, true, true",
      "true, PIBA, false, false",
      "true, PIBA, , false"
  })
  void toPaymentReqModel_ShouldHandlePibaCardPresentTransformation(
      Boolean isCiol, String paymentType, Boolean initialValue, Boolean expectedValue) {
    // Arrange
    PaymentRequestDto paymentRequestDto = createPaymentRequestDto(isCiol, paymentType,
        initialValue);

    // Act
    PaymentRequest result = paymentMapper.toPaymentReqModel(paymentRequestDto);

    // Assert
    assertThat(result, notNullValue());
    assertThat(paymentRequestDto.getPayment().getPibaCardPresent(), is(expectedValue));
    assertThat(result.getPayment().getPibaCardPresent(), is(expectedValue));
  }

  @Test
  void toPaymentReqModel_ShouldNotChangePibaCardPresent_WhenIsCiolTrueAndTypeNotPIBA() {
    // Arrange
    PaymentRequestDto paymentRequestDto = createPaymentRequestDto(true, "test", true);

    // Act
    PaymentRequest result = paymentMapper.toPaymentReqModel(paymentRequestDto);

    // Assert
    assertThat(result, notNullValue());
    assertThat(paymentRequestDto.getPayment().getPibaCardPresent(), is(true));
    assertThat(result.getPayment().getPibaCardPresent(), is(true));
  }


  @Test
  void toPaymentReqModel_ShouldHandleNullPaymentDto_WhenIsCiolTrue() {
    // Arrange
    PaymentRequestDto paymentRequestDto = createPaymentRequestDtoWithNullPayment(true);

    // Act
    PaymentRequest result = paymentMapper.toPaymentReqModel(paymentRequestDto);

    // Assert
    assertThat(result, notNullValue());
    assertThat(paymentRequestDto.getPayment(), nullValue());
  }

  private PaymentRequestDto createPaymentRequestDto(Boolean isCiol, String paymentType,
      Boolean pibaCardPresent) {
    BillingDto billingDto = BillingDto.builder()
        .address(AddressDto.builder()
            .addressLine1("line1")
            .postalCode("1234")
            .build())
        .build();

    PaymentDto paymentDto = PaymentDto.builder()
        .type(paymentType)
        .subType("subType")
        .billing(billingDto)
        .pibaCardPresent(pibaCardPresent)
        .build();

    BookingDto bookingDto = BookingDto.builder()
        .channel("BB")
        .journey("BOOKING")
        .type("LEISURE")
        .businessSite(BusinessSiteDto.builder()
            .identifier("TEST001")
            .type("HOTEL")
            .build())
        .build();

    return PaymentRequestDto.builder()
        .isCiol(isCiol)
        .payment(paymentDto)
        .booking(bookingDto)
        .build();
  }

  private PaymentRequestDto createPaymentRequestDtoWithNullPayment(Boolean isCiol) {
    // Create a minimal valid payment DTO first to pass constructor validation
    BillingDto billingDto = BillingDto.builder()
        .address(AddressDto.builder()
            .addressLine1("line1")
            .postalCode("1234")
            .build())
        .build();

    PaymentDto tempPaymentDto = PaymentDto.builder()
        .type("CC")
        .subType("VISA")
        .billing(billingDto)
        .build();

    BookingDto bookingDto = BookingDto.builder()
        .channel("BB")
        .journey("BOOKING")
        .type("LEISURE")
        .businessSite(BusinessSiteDto.builder()
            .identifier("TEST001")
            .type("HOTEL")
            .build())
        .build();

    // Use constructor with temp payment to pass validation
    PaymentRequestDto dto = PaymentRequestDto.builder()
        .isCiol(isCiol)
        .payment(tempPaymentDto)
        .booking(bookingDto)
        .build();

    // Now set payment to null after construction
    dto.setPayment(null);

    return dto;
  }
}

