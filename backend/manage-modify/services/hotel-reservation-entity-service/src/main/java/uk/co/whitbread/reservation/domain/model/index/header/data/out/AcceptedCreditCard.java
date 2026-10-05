package uk.co.whitbread.reservation.domain.model.index.header.data.out;

import com.fasterxml.jackson.annotation.JsonProperty;
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

  @JsonProperty("code3CP")
  private String code3cp;

  private String codeOpera;

  private String codeOperaCardType;

  private String feeAmount;

  private String feeCurrency;

  private String listOrder;

  private String name;

  private Boolean paymentOnly;

  private String schemeLogo;

}