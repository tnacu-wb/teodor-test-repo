package uk.co.whitbread.shared.cdh.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BusinessPaymentCard {

  @JsonProperty("BillingAddress")
  private BusinessAddress billingAddress;

  @JsonProperty("CardHolderName")
  private String cardHolderName;

  @JsonProperty("CardNumber")
  private String cardNumber;

  @JsonProperty("CardType")
  private String cardType;

  @JsonProperty("ExpiryDate")
  private String expiryDate;

  @JsonProperty("Token")
  private String token;

  @JsonProperty("CnpRequired")
  private boolean cnpRequired;

  @JsonProperty("CnpBusinessAccountUsername")
  private String cnpBusinessAccountUsername;

  @JsonProperty("CnpBusinessAccountPassword")
  private String cnpBusinessAccountPassword;
}
