package uk.co.whitbread.payapp.generated.models.company;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.springframework.lang.Nullable;
import uk.co.whitbread.payapp.generated.models.company.AddressDto;
import uk.co.whitbread.payapp.generated.models.company.CardNotPresentDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * PaymentCardDto
 */

@JsonTypeName("PaymentCard")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:52:38.523560+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PaymentCardDto {

  private @Nullable AddressDto billingAddress;

  private @Nullable String cardId;

  private @Nullable String cardLabel;

  private @Nullable CardNotPresentDto cardNotPresent;

  private @Nullable Boolean cardNotPresentRequired;

  private @Nullable String cardNumber;

  private @Nullable String cardToken;

  private @Nullable String cardType;

  private @Nullable String expiryDate;

  private @Nullable String issueNumber;

  private @Nullable String nameOnCard;

  private @Nullable String startDate;

  public PaymentCardDto billingAddress(AddressDto billingAddress) {
    this.billingAddress = billingAddress;
    return this;
  }

  /**
   * Get billingAddress
   * @return billingAddress
   */
  @Valid 
  @Schema(name = "billingAddress", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("billingAddress")
  public AddressDto getBillingAddress() {
    return billingAddress;
  }

  public void setBillingAddress(AddressDto billingAddress) {
    this.billingAddress = billingAddress;
  }

  public PaymentCardDto cardId(String cardId) {
    this.cardId = cardId;
    return this;
  }

  /**
   * Get cardId
   * @return cardId
   */
  
  @Schema(name = "cardId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cardId")
  public String getCardId() {
    return cardId;
  }

  public void setCardId(String cardId) {
    this.cardId = cardId;
  }

  public PaymentCardDto cardLabel(String cardLabel) {
    this.cardLabel = cardLabel;
    return this;
  }

  /**
   * Get cardLabel
   * @return cardLabel
   */
  
  @Schema(name = "cardLabel", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cardLabel")
  public String getCardLabel() {
    return cardLabel;
  }

  public void setCardLabel(String cardLabel) {
    this.cardLabel = cardLabel;
  }

  public PaymentCardDto cardNotPresent(CardNotPresentDto cardNotPresent) {
    this.cardNotPresent = cardNotPresent;
    return this;
  }

  /**
   * Get cardNotPresent
   * @return cardNotPresent
   */
  @Valid 
  @Schema(name = "cardNotPresent", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cardNotPresent")
  public CardNotPresentDto getCardNotPresent() {
    return cardNotPresent;
  }

  public void setCardNotPresent(CardNotPresentDto cardNotPresent) {
    this.cardNotPresent = cardNotPresent;
  }

  public PaymentCardDto cardNotPresentRequired(Boolean cardNotPresentRequired) {
    this.cardNotPresentRequired = cardNotPresentRequired;
    return this;
  }

  /**
   * Get cardNotPresentRequired
   * @return cardNotPresentRequired
   */
  
  @Schema(name = "cardNotPresentRequired", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cardNotPresentRequired")
  public Boolean getCardNotPresentRequired() {
    return cardNotPresentRequired;
  }

  public void setCardNotPresentRequired(Boolean cardNotPresentRequired) {
    this.cardNotPresentRequired = cardNotPresentRequired;
  }

  public PaymentCardDto cardNumber(String cardNumber) {
    this.cardNumber = cardNumber;
    return this;
  }

  /**
   * Get cardNumber
   * @return cardNumber
   */
  
  @Schema(name = "cardNumber", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cardNumber")
  public String getCardNumber() {
    return cardNumber;
  }

  public void setCardNumber(String cardNumber) {
    this.cardNumber = cardNumber;
  }

  public PaymentCardDto cardToken(String cardToken) {
    this.cardToken = cardToken;
    return this;
  }

  /**
   * Get cardToken
   * @return cardToken
   */
  
  @Schema(name = "cardToken", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cardToken")
  public String getCardToken() {
    return cardToken;
  }

  public void setCardToken(String cardToken) {
    this.cardToken = cardToken;
  }

  public PaymentCardDto cardType(String cardType) {
    this.cardType = cardType;
    return this;
  }

  /**
   * Get cardType
   * @return cardType
   */
  
  @Schema(name = "cardType", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cardType")
  public String getCardType() {
    return cardType;
  }

  public void setCardType(String cardType) {
    this.cardType = cardType;
  }

  public PaymentCardDto expiryDate(String expiryDate) {
    this.expiryDate = expiryDate;
    return this;
  }

  /**
   * Get expiryDate
   * @return expiryDate
   */
  
  @Schema(name = "expiryDate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("expiryDate")
  public String getExpiryDate() {
    return expiryDate;
  }

  public void setExpiryDate(String expiryDate) {
    this.expiryDate = expiryDate;
  }

  public PaymentCardDto issueNumber(String issueNumber) {
    this.issueNumber = issueNumber;
    return this;
  }

  /**
   * Get issueNumber
   * @return issueNumber
   */
  
  @Schema(name = "issueNumber", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("issueNumber")
  public String getIssueNumber() {
    return issueNumber;
  }

  public void setIssueNumber(String issueNumber) {
    this.issueNumber = issueNumber;
  }

  public PaymentCardDto nameOnCard(String nameOnCard) {
    this.nameOnCard = nameOnCard;
    return this;
  }

  /**
   * Get nameOnCard
   * @return nameOnCard
   */
  
  @Schema(name = "nameOnCard", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("nameOnCard")
  public String getNameOnCard() {
    return nameOnCard;
  }

  public void setNameOnCard(String nameOnCard) {
    this.nameOnCard = nameOnCard;
  }

  public PaymentCardDto startDate(String startDate) {
    this.startDate = startDate;
    return this;
  }

  /**
   * Get startDate
   * @return startDate
   */
  
  @Schema(name = "startDate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("startDate")
  public String getStartDate() {
    return startDate;
  }

  public void setStartDate(String startDate) {
    this.startDate = startDate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PaymentCardDto paymentCard = (PaymentCardDto) o;
    return Objects.equals(this.billingAddress, paymentCard.billingAddress) &&
        Objects.equals(this.cardId, paymentCard.cardId) &&
        Objects.equals(this.cardLabel, paymentCard.cardLabel) &&
        Objects.equals(this.cardNotPresent, paymentCard.cardNotPresent) &&
        Objects.equals(this.cardNotPresentRequired, paymentCard.cardNotPresentRequired) &&
        Objects.equals(this.cardNumber, paymentCard.cardNumber) &&
        Objects.equals(this.cardToken, paymentCard.cardToken) &&
        Objects.equals(this.cardType, paymentCard.cardType) &&
        Objects.equals(this.expiryDate, paymentCard.expiryDate) &&
        Objects.equals(this.issueNumber, paymentCard.issueNumber) &&
        Objects.equals(this.nameOnCard, paymentCard.nameOnCard) &&
        Objects.equals(this.startDate, paymentCard.startDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(billingAddress, cardId, cardLabel, cardNotPresent, cardNotPresentRequired, cardNumber, cardToken, cardType, expiryDate, issueNumber, nameOnCard, startDate);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PaymentCardDto {\n");
    sb.append("    billingAddress: ").append(toIndentedString(billingAddress)).append("\n");
    sb.append("    cardId: ").append(toIndentedString(cardId)).append("\n");
    sb.append("    cardLabel: ").append(toIndentedString(cardLabel)).append("\n");
    sb.append("    cardNotPresent: ").append(toIndentedString(cardNotPresent)).append("\n");
    sb.append("    cardNotPresentRequired: ").append(toIndentedString(cardNotPresentRequired)).append("\n");
    sb.append("    cardNumber: ").append(toIndentedString(cardNumber)).append("\n");
    sb.append("    cardToken: ").append(toIndentedString(cardToken)).append("\n");
    sb.append("    cardType: ").append(toIndentedString(cardType)).append("\n");
    sb.append("    expiryDate: ").append(toIndentedString(expiryDate)).append("\n");
    sb.append("    issueNumber: ").append(toIndentedString(issueNumber)).append("\n");
    sb.append("    nameOnCard: ").append(toIndentedString(nameOnCard)).append("\n");
    sb.append("    startDate: ").append(toIndentedString(startDate)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

