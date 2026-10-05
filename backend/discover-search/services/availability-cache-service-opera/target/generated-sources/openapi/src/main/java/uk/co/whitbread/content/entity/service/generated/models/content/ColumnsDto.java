package uk.co.whitbread.content.entity.service.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * ColumnsDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:49:41.570079+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ColumnsDto {

  private @Nullable String cardHolderName;

  private @Nullable String cardHolderRegistered;

  private @Nullable String cardId;

  private @Nullable String cardLabel;

  private @Nullable String cardNumber;

  private @Nullable String cardStatus;

  private @Nullable String edit;

  private @Nullable String expiry;

  private @Nullable String yourCard;

  public ColumnsDto cardHolderName(String cardHolderName) {
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

  public ColumnsDto cardHolderRegistered(String cardHolderRegistered) {
    this.cardHolderRegistered = cardHolderRegistered;
    return this;
  }

  /**
   * Get cardHolderRegistered
   * @return cardHolderRegistered
   */
  
  @Schema(name = "cardHolderRegistered", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cardHolderRegistered")
  public String getCardHolderRegistered() {
    return cardHolderRegistered;
  }

  public void setCardHolderRegistered(String cardHolderRegistered) {
    this.cardHolderRegistered = cardHolderRegistered;
  }

  public ColumnsDto cardId(String cardId) {
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

  public ColumnsDto cardLabel(String cardLabel) {
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

  public ColumnsDto cardNumber(String cardNumber) {
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

  public ColumnsDto cardStatus(String cardStatus) {
    this.cardStatus = cardStatus;
    return this;
  }

  /**
   * Get cardStatus
   * @return cardStatus
   */
  
  @Schema(name = "cardStatus", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cardStatus")
  public String getCardStatus() {
    return cardStatus;
  }

  public void setCardStatus(String cardStatus) {
    this.cardStatus = cardStatus;
  }

  public ColumnsDto edit(String edit) {
    this.edit = edit;
    return this;
  }

  /**
   * Get edit
   * @return edit
   */
  
  @Schema(name = "edit", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("edit")
  public String getEdit() {
    return edit;
  }

  public void setEdit(String edit) {
    this.edit = edit;
  }

  public ColumnsDto expiry(String expiry) {
    this.expiry = expiry;
    return this;
  }

  /**
   * Get expiry
   * @return expiry
   */
  
  @Schema(name = "expiry", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("expiry")
  public String getExpiry() {
    return expiry;
  }

  public void setExpiry(String expiry) {
    this.expiry = expiry;
  }

  public ColumnsDto yourCard(String yourCard) {
    this.yourCard = yourCard;
    return this;
  }

  /**
   * Get yourCard
   * @return yourCard
   */
  
  @Schema(name = "yourCard", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("yourCard")
  public String getYourCard() {
    return yourCard;
  }

  public void setYourCard(String yourCard) {
    this.yourCard = yourCard;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ColumnsDto columnsDto = (ColumnsDto) o;
    return Objects.equals(this.cardHolderName, columnsDto.cardHolderName) &&
        Objects.equals(this.cardHolderRegistered, columnsDto.cardHolderRegistered) &&
        Objects.equals(this.cardId, columnsDto.cardId) &&
        Objects.equals(this.cardLabel, columnsDto.cardLabel) &&
        Objects.equals(this.cardNumber, columnsDto.cardNumber) &&
        Objects.equals(this.cardStatus, columnsDto.cardStatus) &&
        Objects.equals(this.edit, columnsDto.edit) &&
        Objects.equals(this.expiry, columnsDto.expiry) &&
        Objects.equals(this.yourCard, columnsDto.yourCard);
  }

  @Override
  public int hashCode() {
    return Objects.hash(cardHolderName, cardHolderRegistered, cardId, cardLabel, cardNumber, cardStatus, edit, expiry, yourCard);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ColumnsDto {\n");
    sb.append("    cardHolderName: ").append(toIndentedString(cardHolderName)).append("\n");
    sb.append("    cardHolderRegistered: ").append(toIndentedString(cardHolderRegistered)).append("\n");
    sb.append("    cardId: ").append(toIndentedString(cardId)).append("\n");
    sb.append("    cardLabel: ").append(toIndentedString(cardLabel)).append("\n");
    sb.append("    cardNumber: ").append(toIndentedString(cardNumber)).append("\n");
    sb.append("    cardStatus: ").append(toIndentedString(cardStatus)).append("\n");
    sb.append("    edit: ").append(toIndentedString(edit)).append("\n");
    sb.append("    expiry: ").append(toIndentedString(expiry)).append("\n");
    sb.append("    yourCard: ").append(toIndentedString(yourCard)).append("\n");
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

