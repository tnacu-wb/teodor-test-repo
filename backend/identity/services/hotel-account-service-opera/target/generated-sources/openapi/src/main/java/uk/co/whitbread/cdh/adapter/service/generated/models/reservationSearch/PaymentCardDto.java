package uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch.BillingAddressDto;
import uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch.CardNotPresentDto;
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

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-09T08:37:59.335673+03:00[Europe/Bucharest]", comments = "Generator version: 7.14.0")
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

  public PaymentCardDto billingAddress(@Nullable BillingAddressDto billingAddress) {
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
  public @Nullable BillingAddressDto getBillingAddress() {
    return billingAddress;
  }

  public void setBillingAddress(@Nullable BillingAddressDto billingAddress) {
    this.billingAddress = billingAddress;
  }

  public PaymentCardDto cardId(@Nullable String cardId) {
    this.cardId = cardId;
    return this;
  }

  /**
   * Get cardId
   * @return cardId
   */
  
  @Schema(name = "cardId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cardId")
  public @Nullable String getCardId() {
    return cardId;
  }

  public void setCardId(@Nullable String cardId) {
    this.cardId = cardId;
  }

  public PaymentCardDto cardLabel(@Nullable String cardLabel) {
    this.cardLabel = cardLabel;
    return this;
  }

  /**
   * Get cardLabel
   * @return cardLabel
   */
  
  @Schema(name = "cardLabel", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cardLabel")
  public @Nullable String getCardLabel() {
    return cardLabel;
  }

  public void setCardLabel(@Nullable String cardLabel) {
    this.cardLabel = cardLabel;
  }

  public PaymentCardDto cardNotPresent(@Nullable CardNotPresentDto cardNotPresent) {
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
  public @Nullable CardNotPresentDto getCardNotPresent() {
    return cardNotPresent;
  }

  public void setCardNotPresent(@Nullable CardNotPresentDto cardNotPresent) {
    this.cardNotPresent = cardNotPresent;
  }

  public PaymentCardDto cardNotPresentRequired(@Nullable Boolean cardNotPresentRequired) {
    this.cardNotPresentRequired = cardNotPresentRequired;
    return this;
  }

  /**
   * Get cardNotPresentRequired
   * @return cardNotPresentRequired
   */
  
  @Schema(name = "cardNotPresentRequired", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cardNotPresentRequired")
  public @Nullable Boolean getCardNotPresentRequired() {
    return cardNotPresentRequired;
  }

  public void setCardNotPresentRequired(@Nullable Boolean cardNotPresentRequired) {
    this.cardNotPresentRequired = cardNotPresentRequired;
  }

  public PaymentCardDto cardNumber(@Nullable String cardNumber) {
    this.cardNumber = cardNumber;
    return this;
  }

  /**
   * Get cardNumber
   * @return cardNumber
   */
  
  @Schema(name = "cardNumber", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cardNumber")
  public @Nullable String getCardNumber() {
    return cardNumber;
  }

  public void setCardNumber(@Nullable String cardNumber) {
    this.cardNumber = cardNumber;
  }

  public PaymentCardDto cardType(@Nullable String cardType) {
    this.cardType = cardType;
    return this;
  }

  /**
   * Get cardType
   * @return cardType
   */
  
  @Schema(name = "cardType", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cardType")
  public @Nullable String getCardType() {
    return cardType;
  }

  public void setCardType(@Nullable String cardType) {
    this.cardType = cardType;
  }

  public PaymentCardDto created(@Nullable String created) {
    this.created = created;
    return this;
  }

  /**
   * Get created
   * @return created
   */
  
  @Schema(name = "created", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("created")
  public @Nullable String getCreated() {
    return created;
  }

  public void setCreated(@Nullable String created) {
    this.created = created;
  }

  public PaymentCardDto dateDeleted(@Nullable LocalDate dateDeleted) {
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
  public @Nullable LocalDate getDateDeleted() {
    return dateDeleted;
  }

  public void setDateDeleted(@Nullable LocalDate dateDeleted) {
    this.dateDeleted = dateDeleted;
  }

  public PaymentCardDto deleted(@Nullable Boolean deleted) {
    this.deleted = deleted;
    return this;
  }

  /**
   * Get deleted
   * @return deleted
   */
  
  @Schema(name = "deleted", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("deleted")
  public @Nullable Boolean getDeleted() {
    return deleted;
  }

  public void setDeleted(@Nullable Boolean deleted) {
    this.deleted = deleted;
  }

  public PaymentCardDto expiryDate(@Nullable String expiryDate) {
    this.expiryDate = expiryDate;
    return this;
  }

  /**
   * Get expiryDate
   * @return expiryDate
   */
  
  @Schema(name = "expiryDate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("expiryDate")
  public @Nullable String getExpiryDate() {
    return expiryDate;
  }

  public void setExpiryDate(@Nullable String expiryDate) {
    this.expiryDate = expiryDate;
  }

  public PaymentCardDto modified(@Nullable String modified) {
    this.modified = modified;
    return this;
  }

  /**
   * Get modified
   * @return modified
   */
  
  @Schema(name = "modified", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("modified")
  public @Nullable String getModified() {
    return modified;
  }

  public void setModified(@Nullable String modified) {
    this.modified = modified;
  }

  public PaymentCardDto nameOnCard(@Nullable String nameOnCard) {
    this.nameOnCard = nameOnCard;
    return this;
  }

  /**
   * Get nameOnCard
   * @return nameOnCard
   */
  
  @Schema(name = "nameOnCard", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("nameOnCard")
  public @Nullable String getNameOnCard() {
    return nameOnCard;
  }

  public void setNameOnCard(@Nullable String nameOnCard) {
    this.nameOnCard = nameOnCard;
  }

  public PaymentCardDto position(@Nullable Integer position) {
    this.position = position;
    return this;
  }

  /**
   * Get position
   * @return position
   */
  
  @Schema(name = "position", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("position")
  public @Nullable Integer getPosition() {
    return position;
  }

  public void setPosition(@Nullable Integer position) {
    this.position = position;
  }

  public PaymentCardDto token(@Nullable String token) {
    this.token = token;
    return this;
  }

  /**
   * Get token
   * @return token
   */
  
  @Schema(name = "token", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("token")
  public @Nullable String getToken() {
    return token;
  }

  public void setToken(@Nullable String token) {
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

