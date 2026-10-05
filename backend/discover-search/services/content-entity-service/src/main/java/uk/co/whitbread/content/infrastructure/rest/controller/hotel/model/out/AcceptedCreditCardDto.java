package uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AcceptedCreditCardDto {

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
