package uk.co.whitbread.shared.cdh.model.card;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.co.whitbread.shared.cdh.model.BusinessAddress;
import uk.co.whitbread.shared.cdh.model.CardNotPresent;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
public class PaymentCardDetails {

  @JsonProperty("BillingAddress")
  private BusinessAddress billingAddress;

  @JsonProperty("CardLabel")
  private String cardLabel;

  @JsonProperty("CardNotPresent")
  private CardNotPresent cardNotPresent;

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

}
