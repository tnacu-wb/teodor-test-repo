package uk.co.whitbread.hotel.account.service.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.account.service.generated.models.BillingAddress;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * PaymentCard
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:35.247020+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PaymentCard {

  private @Nullable BillingAddress billingAddress;

  private @Nullable String cardHolderName;

  private @Nullable String cardID;

  private @Nullable String cardLabel;

  private @Nullable String cardNumber;

  private @Nullable String cardToken;

  private @Nullable String cardType;

  private @Nullable String cnpBusinessAccountPassword;

  private @Nullable String cnpBusinessAccountUsername;

  private @Nullable Boolean cnpRequired;

  private @Nullable String expiryDate;

  private @Nullable String issueNumber;

  private @Nullable String startDate;

  public PaymentCard billingAddress(BillingAddress billingAddress) {
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
  public BillingAddress getBillingAddress() {
    return billingAddress;
  }

  public void setBillingAddress(BillingAddress billingAddress) {
    this.billingAddress = billingAddress;
  }

  public PaymentCard cardHolderName(String cardHolderName) {
    this.cardHolderName = cardHolderName;
    return this;
  }

  /**
   * Get cardHolderName
   * @return cardHolderName
   */
  
  @Schema(name = "cardHolderName", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cardHolderName")
  public String getCardHolderName() {
    return cardHolderName;
  }

  public void setCardHolderName(String cardHolderName) {
    this.cardHolderName = cardHolderName;
  }

  public PaymentCard cardID(String cardID) {
    this.cardID = cardID;
    return this;
  }

  /**
   * Get cardID
   * @return cardID
   */
  
  @Schema(name = "cardID", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cardID")
  public String getCardID() {
    return cardID;
  }

  public void setCardID(String cardID) {
    this.cardID = cardID;
  }

  public PaymentCard cardLabel(String cardLabel) {
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

  public PaymentCard cardNumber(String cardNumber) {
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

  public PaymentCard cardToken(String cardToken) {
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

  public PaymentCard cardType(String cardType) {
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

  public PaymentCard cnpBusinessAccountPassword(String cnpBusinessAccountPassword) {
    this.cnpBusinessAccountPassword = cnpBusinessAccountPassword;
    return this;
  }

  /**
   * Get cnpBusinessAccountPassword
   * @return cnpBusinessAccountPassword
   */
  
  @Schema(name = "cnpBusinessAccountPassword", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cnpBusinessAccountPassword")
  public String getCnpBusinessAccountPassword() {
    return cnpBusinessAccountPassword;
  }

  public void setCnpBusinessAccountPassword(String cnpBusinessAccountPassword) {
    this.cnpBusinessAccountPassword = cnpBusinessAccountPassword;
  }

  public PaymentCard cnpBusinessAccountUsername(String cnpBusinessAccountUsername) {
    this.cnpBusinessAccountUsername = cnpBusinessAccountUsername;
    return this;
  }

  /**
   * Get cnpBusinessAccountUsername
   * @return cnpBusinessAccountUsername
   */
  
  @Schema(name = "cnpBusinessAccountUsername", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cnpBusinessAccountUsername")
  public String getCnpBusinessAccountUsername() {
    return cnpBusinessAccountUsername;
  }

  public void setCnpBusinessAccountUsername(String cnpBusinessAccountUsername) {
    this.cnpBusinessAccountUsername = cnpBusinessAccountUsername;
  }

  public PaymentCard cnpRequired(Boolean cnpRequired) {
    this.cnpRequired = cnpRequired;
    return this;
  }

  /**
   * Get cnpRequired
   * @return cnpRequired
   */
  
  @Schema(name = "cnpRequired", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cnpRequired")
  public Boolean getCnpRequired() {
    return cnpRequired;
  }

  public void setCnpRequired(Boolean cnpRequired) {
    this.cnpRequired = cnpRequired;
  }

  public PaymentCard expiryDate(String expiryDate) {
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

  public PaymentCard issueNumber(String issueNumber) {
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

  public PaymentCard startDate(String startDate) {
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
    PaymentCard paymentCard = (PaymentCard) o;
    return Objects.equals(this.billingAddress, paymentCard.billingAddress) &&
        Objects.equals(this.cardHolderName, paymentCard.cardHolderName) &&
        Objects.equals(this.cardID, paymentCard.cardID) &&
        Objects.equals(this.cardLabel, paymentCard.cardLabel) &&
        Objects.equals(this.cardNumber, paymentCard.cardNumber) &&
        Objects.equals(this.cardToken, paymentCard.cardToken) &&
        Objects.equals(this.cardType, paymentCard.cardType) &&
        Objects.equals(this.cnpBusinessAccountPassword, paymentCard.cnpBusinessAccountPassword) &&
        Objects.equals(this.cnpBusinessAccountUsername, paymentCard.cnpBusinessAccountUsername) &&
        Objects.equals(this.cnpRequired, paymentCard.cnpRequired) &&
        Objects.equals(this.expiryDate, paymentCard.expiryDate) &&
        Objects.equals(this.issueNumber, paymentCard.issueNumber) &&
        Objects.equals(this.startDate, paymentCard.startDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(billingAddress, cardHolderName, cardID, cardLabel, cardNumber, cardToken, cardType, cnpBusinessAccountPassword, cnpBusinessAccountUsername, cnpRequired, expiryDate, issueNumber, startDate);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PaymentCard {\n");
    sb.append("    billingAddress: ").append(toIndentedString(billingAddress)).append("\n");
    sb.append("    cardHolderName: ").append(toIndentedString(cardHolderName)).append("\n");
    sb.append("    cardID: ").append(toIndentedString(cardID)).append("\n");
    sb.append("    cardLabel: ").append(toIndentedString(cardLabel)).append("\n");
    sb.append("    cardNumber: ").append(toIndentedString(cardNumber)).append("\n");
    sb.append("    cardToken: ").append(toIndentedString(cardToken)).append("\n");
    sb.append("    cardType: ").append(toIndentedString(cardType)).append("\n");
    sb.append("    cnpBusinessAccountPassword: ").append(toIndentedString(cnpBusinessAccountPassword)).append("\n");
    sb.append("    cnpBusinessAccountUsername: ").append(toIndentedString(cnpBusinessAccountUsername)).append("\n");
    sb.append("    cnpRequired: ").append(toIndentedString(cnpRequired)).append("\n");
    sb.append("    expiryDate: ").append(toIndentedString(expiryDate)).append("\n");
    sb.append("    issueNumber: ").append(toIndentedString(issueNumber)).append("\n");
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

