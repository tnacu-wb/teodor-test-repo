package uk.co.whitbread.shared.cdh.model.company;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import uk.co.whitbread.shared.cdh.model.BusinessAddress;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CompanyPaymentCard {

  @JsonProperty("CardId")
  private String cardId;
  @JsonProperty("Position")
  private String position;
  @JsonProperty("BillingAddress")
  private BusinessAddress billingAddress;
  @JsonProperty("CardNotPresent")
  private CardNotPresent cardNotPresent;
  @JsonProperty("CardLabel")
  private String cardLabel;
  @JsonProperty("CardNotPresentRequired")
  private boolean cardNotPresentRequired;
  @JsonProperty("CardNumber")
  private String cardNumber;
  @JsonProperty("Token")
  private String token;
  @JsonProperty("CardType")
  private String cardType;
  @JsonProperty("ExpiryDate")
  private String expiryDate;
  @JsonProperty("NameOnCard")
  private String nameOnCard;
  @JsonProperty("Deleted")
  private boolean deleted;
  @JsonProperty("Created")
  private String created;
}