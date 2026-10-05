package uk.co.whitbread.basket.domain.model.content.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AcceptedCreditCard {

  private String code;
  private String code3CP;
  private String codeOpera;
  private String codeOperaCardType;
  private String feeAmount;
  private String feeCurrency;
  private String listOrder;
  private String name;
  private Boolean paymentOnly;
  private String schemeLogo;
}
