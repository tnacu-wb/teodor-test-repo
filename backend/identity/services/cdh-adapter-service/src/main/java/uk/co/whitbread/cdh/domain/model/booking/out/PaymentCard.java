package uk.co.whitbread.cdh.domain.model.booking.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCard {

  @JsonProperty("BillingAddress")
  private Address billingAddress;

  @JsonProperty("CardHolderName")
  private String cardHolderName;

  @JsonProperty("CardNumber")
  private String cardNumber;

  @JsonProperty("CardType")
  private String cardType;

  @JsonProperty("ExpiryDate")
  private String expiryDate;

  @JsonProperty("CnpRequired")
  private boolean cnpRequired;

  @JsonProperty("Token")
  private String token;

  @JsonProperty("CnpBusinessAccountPassword")
  private String cnpBusinessAccountPassword;

  @JsonProperty("CnpBusinessAccountUsername")
  private String cnpBusinessAccountUsername;

}
