package uk.co.whitbread.payments.domain.model.out;

import lombok.Data;

@Data
public class HotelPaymentMethod {
  private String code;
  private String feeAmount;
  private String feeCurrency;
  private String listOrder;
  private String name;
  private Boolean paymentOnly;
  private String schemeLogo;
}
