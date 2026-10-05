package uk.co.whitbread.payments.infrastructure.rest.client.hotels.model.out;

import lombok.Data;

@Data
public class AcceptedCreditCardDto {
  private String code;
  private String feeAmount;
  private String feeCurrency;
  private String listOrder;
  private String name;
  private Boolean paymentOnly;
  private String schemeLogo;
}
