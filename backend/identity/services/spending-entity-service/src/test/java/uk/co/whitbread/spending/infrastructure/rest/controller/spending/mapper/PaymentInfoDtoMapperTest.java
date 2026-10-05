package uk.co.whitbread.spending.infrastructure.rest.controller.spending.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.spending.domain.model.out.worldline.PaymentData;
import uk.co.whitbread.spending.domain.model.out.worldline.PaymentInfoResponse;
import uk.co.whitbread.spending.domain.model.out.worldline.PaymentValue;
import uk.co.whitbread.spending.infrastructure.rest.controller.spending.model.in.PaymentInfoQueryParamsDto;
import uk.co.whitbread.spending.infrastructure.rest.controller.spending.model.out.PaymentDto;
import uk.co.whitbread.spending.infrastructure.rest.controller.spending.model.out.PaymentInfoResponseDto;

@ExtendWith(MockitoExtension.class)
class PaymentInfoDtoMapperTest {

  private PaymentInfoDtoMapper mapper;

  @BeforeEach
  void setup() {
    mapper = new PaymentInfoDtoMapperImpl();
  }

  @Test
  void shouldMapToModel() {
    //Arrange
    var dto = new PaymentInfoQueryParamsDto("123", null, 1, 1, true);
    var token = "token";
    var ipAddress = "2.2.2.2";

    //Act
    var result = mapper.toModel(dto, token, ipAddress);

    //Assert
    assertNotNull(result);
    assertEquals("123", result.getAccountId());
    assertEquals(1, result.getPage());
    assertEquals(1, result.getSize());
    assertTrue(result.isNonInvoiceOnly());
    assertEquals("token", result.getAuthorization());
    assertEquals("2.2.2.2", result.getIpAddress());
  }

  @Test
  void mapWorldLineResponse() {
    //Arrange
    var wlResponse = createWorldLineResponse();

    //Act
    var result = mapper.toPaymentInfoResponseDto(wlResponse);

    //Assert
    checkWordLineResponseMapping(result);

    // Test errors
    assertEquals("fourth payment failed", result.getErrors());
  }

  private void checkWordLineResponseMapping(PaymentInfoResponseDto result) {
    assertNotNull(result);
    // Test response code and overall structure
    assertEquals(5, result.getPayments().size());

    // Test first payment
    PaymentDto payment0 = result.getPayments().get(0);
    assertEquals("01-01-2020", payment0.getPaymentDate());
    assertEquals("firstPayment", payment0.getPaymentDescription());
    assertEquals("N/A", payment0.getFailureReason());
    assertFalse(payment0.isPaymentFailed());
    assertEquals("1", payment0.getPaymentValue().getValue());
    assertEquals("826", payment0.getPaymentValue().getCurrencyCode());
    assertEquals("£", payment0.getPaymentValue().getCurrencySymbol());

    // Test second payment
    PaymentDto payment1 = result.getPayments().get(1);
    assertEquals("02-02-2020", payment1.getPaymentDate());
    assertEquals("secondPayment", payment1.getPaymentDescription());
    assertEquals("N/A", payment1.getFailureReason());
    assertFalse(payment1.isPaymentFailed());
    assertEquals("2", payment1.getPaymentValue().getValue());
    assertEquals("978", payment1.getPaymentValue().getCurrencyCode());
    assertEquals("€", payment1.getPaymentValue().getCurrencySymbol());

    // Test third payment
    PaymentDto payment2 = result.getPayments().get(2);
    assertEquals("03-03-2020", payment2.getPaymentDate());
    assertEquals("thirdPayment", payment2.getPaymentDescription());
    assertEquals("N/A", payment2.getFailureReason());
    assertFalse(payment2.isPaymentFailed());
    assertEquals("3", payment2.getPaymentValue().getValue());
    assertEquals("111", payment2.getPaymentValue().getCurrencyCode());
    assertEquals("", payment2.getPaymentValue().getCurrencySymbol());

    // Test fourth payment
    PaymentDto payment3 = result.getPayments().get(3);
    assertEquals("04-04-2020", payment3.getPaymentDate());
    assertEquals("fourthPayment", payment3.getPaymentDescription());
    assertEquals("failed", payment3.getFailureReason());
    assertTrue(payment3.isPaymentFailed());
    assertEquals("4", payment3.getPaymentValue().getValue());
    assertEquals("978", payment3.getPaymentValue().getCurrencyCode());
    assertEquals("€", payment3.getPaymentValue().getCurrencySymbol());

    // Test fourth payment
    PaymentDto payment4 = result.getPayments().get(4);
    assertEquals("04-04-2020", payment4.getPaymentDate());
    assertEquals("fifthPayment", payment4.getPaymentDescription());
    assertNull(payment4.getFailureReason());
    assertTrue(payment4.isPaymentFailed());
    assertEquals("5", payment4.getPaymentValue().getValue());
    assertEquals("222", payment4.getPaymentValue().getCurrencyCode());
    assertEquals("", payment4.getPaymentValue().getCurrencySymbol());
  }

  private PaymentInfoResponse createWorldLineResponse() {
    var wlResponse = new PaymentInfoResponse();
    wlResponse.setResponseCode("200");
    wlResponse.setErrors("fourth payment failed");
    var firstPayment = createPaymentData("01-01-2020", "firstPayment", "N/A", "1", "826");
    var secondPayment = createPaymentData("02-02-2020", "secondPayment", "N/A", "2", "978");
    var thirdPayment = createPaymentData("03-03-2020", "thirdPayment", "N/A", "3", "111");
    var fourthPayment = createPaymentData("04-04-2020", "fourthPayment", "failed", "4", "978");
    var fifthPayment = createPaymentData("04-04-2020", "fifthPayment", null, "5", "222");
    wlResponse.setData(
        List.of(firstPayment, secondPayment, thirdPayment, fourthPayment, fifthPayment));
    return wlResponse;
  }

  private PaymentData createPaymentData(String paymentDate, String description,
      String failureReason, String value, String currencyCode) {
    var paymentData = new PaymentData();
    paymentData.setPaymentDate(paymentDate);
    paymentData.setPaymentDescription(description);
    paymentData.setFailureReason(failureReason);
    paymentData.setPaymentValue(new PaymentValue(value, currencyCode));
    return paymentData;
  }

}