package uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.out;

import lombok.Data;

@Data
public class PaymentMethodDto {
  private String code;
  private String feeAmount;
  private String feeCurrency;
  private String listOrder;
  private String name;
  private Boolean paymentOnly;
  private String schemeLogo;
}
