package uk.co.whitbread.reservation.domain.model.index.header.data.out;

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

  private String listOrder;

  private String name;

  private Boolean paymentOnly;

  private String schemeLogo;

}
