package uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMethod {

  private String code;
  private String feeAmount;
  private String feeCurrency;
  private String paymentOnly;
  private String listOrder;
  private String name;
  private String schemeLogo;
}

