package uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.in;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class RefundRequestDtoDeserializationTest {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void shouldDeserializeRefundRequestDto() throws Exception {
    var json = """
        {
          "hotelCode": "HOTEL_1",
          "refundType": "FULL",
          "refund": {
            "type": "CARD",
            "reason": "CANCEL",
            "card": {
              "cardholderName": "John Doe",
              "token": "tok_123",
              "expiryMonth": "01",
              "expiryYear": "30",
              "cardType": "VISA",
              "cnpRequired": true,
              "logoUrl": "logo",
              "type": "CREDIT",
              "last4Digits": "1111"
            },
            "amount": {
              "currency": "GBP",
              "minorUnits": 10.50
            }
          },
          "booking": {
            "channel": "WEB",
            "journey": "CCUI",
            "type": "HOTEL",
            "businessSite": {
              "identifier": "id-1",
              "type": "CORP"
            }
          }
        }
        """;

    var dto = objectMapper.readValue(json, RefundRequestDto.class);

    assertNotNull(dto);
    assertNotNull(dto.getRefund());
    assertEquals("HOTEL_1", dto.getHotelCode());
    assertEquals(PaymentType.CARD, dto.getRefund().getType());
    assertEquals(RefundReason.CANCEL, dto.getRefund().getReason());
    assertEquals(RefundType.FULL, dto.getRefundType());
  }
}
