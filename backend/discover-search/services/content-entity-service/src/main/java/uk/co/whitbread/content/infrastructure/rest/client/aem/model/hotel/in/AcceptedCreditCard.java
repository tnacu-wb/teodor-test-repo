package uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in;

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
  private String paymentOnly;
  private String listOrder;
  private String name;
  private String schemeLogo;
}