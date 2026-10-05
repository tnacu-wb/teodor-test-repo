package uk.co.whitbread.cdh.domain.model.account.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCard {

  @JsonProperty("CardId")
  private String cardId;
  @JsonProperty("Position")
  private Integer position;
  @JsonProperty("BillingAddress")
  private BillingAddress billingAddress;
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
  @JsonProperty("Created")
  private String created;
  @JsonProperty("Modified")
  private String modified;
  @JsonProperty("Deleted")
  private boolean deleted;
  @JsonProperty("DateDeleted")
  private LocalDate dateDeleted;
}
