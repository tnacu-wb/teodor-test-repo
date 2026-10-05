package uk.co.whitbread.cdh.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import uk.co.whitbread.cdh.generated.models.BillingAddressDto;
import uk.co.whitbread.cdh.generated.models.CardNotPresentDto;
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

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:50:16.086993+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PaymentCardDto {

  private @Nullable BillingAddressDto billingAddress;

  private @Nullable String cardId;

  private @Nullable String cardLabel;

  private @Nullable CardNotPresentDto cardNotPresent;

  private @Nullable Boolean cardNotPresentRequired;

  private @Nullable String cardNumber;

  private @Nullable String cardType;

  private @Nullable String created;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private @Nullable LocalDate dateDeleted;

  private @Nullable Boolean deleted;

  private @Nullable String expiryDate;

  private @Nullable String modified;

  private @Nullable String nameOnCard;

  private @Nullable Integer position;

  private @Nullable String token;

  public PaymentCardDto billingAddress(BillingAddressDto billingAddress) {
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
  public BillingAddressDto getBillingAddress() {
    return billingAddress;
  }

  public void setBillingAddress(BillingAddressDto billingAddress) {
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

  public PaymentCardDto created(String created) {
    this.created = created;
    return this;
  }

  /**
   * Get created
   * @return created
   */
  
  @Schema(name = "created", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("created")
  public String getCreated() {
    return created;
  }

  public void setCreated(String created) {
    this.created = created;
  }

  public PaymentCardDto dateDeleted(LocalDate dateDeleted) {
    this.dateDeleted = dateDeleted;
    return this;
  }

  /**
   * Get dateDeleted
   * @return dateDeleted
   */
  @Valid 
  @Schema(name = "dateDeleted", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("dateDeleted")
  public LocalDate getDateDeleted() {
    return dateDeleted;
  }

  public void setDateDeleted(LocalDate dateDeleted) {
    this.dateDeleted = dateDeleted;
  }

  public PaymentCardDto deleted(Boolean deleted) {
    this.deleted = deleted;
    return this;
  }

  /**
   * Get deleted
   * @return deleted
   */
  
  @Schema(name = "deleted", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("deleted")
  public Boolean getDeleted() {
    return deleted;
  }

  public void setDeleted(Boolean deleted) {
    this.deleted = deleted;
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

  public PaymentCardDto modified(String modified) {
    this.modified = modified;
    return this;
  }

  /**
   * Get modified
   * @return modified
   */
  
  @Schema(name = "modified", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("modified")
  public String getModified() {
    return modified;
  }

  public void setModified(String modified) {
    this.modified = modified;
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

  public PaymentCardDto position(Integer position) {
    this.position = position;
    return this;
  }

  /**
   * Get position
   * @return position
   */
  
  @Schema(name = "position", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("position")
  public Integer getPosition() {
    return position;
  }

  public void setPosition(Integer position) {
    this.position = position;
  }

  public PaymentCardDto token(String token) {
    this.token = token;
    return this;
  }

  /**
   * Get token
   * @return token
   */
  
  @Schema(name = "token", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("token")
  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PaymentCardDto paymentCardDto = (PaymentCardDto) o;
    return Objects.equals(this.billingAddress, paymentCardDto.billingAddress) &&
        Objects.equals(this.cardId, paymentCardDto.cardId) &&
        Objects.equals(this.cardLabel, paymentCardDto.cardLabel) &&
        Objects.equals(this.cardNotPresent, paymentCardDto.cardNotPresent) &&
        Objects.equals(this.cardNotPresentRequired, paymentCardDto.cardNotPresentRequired) &&
        Objects.equals(this.cardNumber, paymentCardDto.cardNumber) &&
        Objects.equals(this.cardType, paymentCardDto.cardType) &&
        Objects.equals(this.created, paymentCardDto.created) &&
        Objects.equals(this.dateDeleted, paymentCardDto.dateDeleted) &&
        Objects.equals(this.deleted, paymentCardDto.deleted) &&
        Objects.equals(this.expiryDate, paymentCardDto.expiryDate) &&
        Objects.equals(this.modified, paymentCardDto.modified) &&
        Objects.equals(this.nameOnCard, paymentCardDto.nameOnCard) &&
        Objects.equals(this.position, paymentCardDto.position) &&
        Objects.equals(this.token, paymentCardDto.token);
  }

  @Override
  public int hashCode() {
    return Objects.hash(billingAddress, cardId, cardLabel, cardNotPresent, cardNotPresentRequired, cardNumber, cardType, created, dateDeleted, deleted, expiryDate, modified, nameOnCard, position, token);
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
    sb.append("    cardType: ").append(toIndentedString(cardType)).append("\n");
    sb.append("    created: ").append(toIndentedString(created)).append("\n");
    sb.append("    dateDeleted: ").append(toIndentedString(dateDeleted)).append("\n");
    sb.append("    deleted: ").append(toIndentedString(deleted)).append("\n");
    sb.append("    expiryDate: ").append(toIndentedString(expiryDate)).append("\n");
    sb.append("    modified: ").append(toIndentedString(modified)).append("\n");
    sb.append("    nameOnCard: ").append(toIndentedString(nameOnCard)).append("\n");
    sb.append("    position: ").append(toIndentedString(position)).append("\n");
    sb.append("    token: ").append(toIndentedString(token)).append("\n");
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

