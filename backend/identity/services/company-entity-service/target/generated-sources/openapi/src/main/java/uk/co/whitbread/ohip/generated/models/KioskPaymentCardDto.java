package uk.co.whitbread.ohip.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.ohip.generated.models.CardIdDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * KioskPaymentCardDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:50:12.143180+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class KioskPaymentCardDto {

  private @Nullable String cardHolderName;

  private @Nullable CardIdDto cardId;

  private @Nullable String cardNumberMasked;

  private @Nullable String cardOrToken;

  private @Nullable Boolean cardPresent;

  private @Nullable String cardType;

  private @Nullable Boolean expirationDateExpired;

  private @Nullable String expirationDateMasked;

  private @Nullable String processing;

  private @Nullable Boolean swiped;

  public KioskPaymentCardDto cardHolderName(String cardHolderName) {
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

  public KioskPaymentCardDto cardId(CardIdDto cardId) {
    this.cardId = cardId;
    return this;
  }

  /**
   * Get cardId
   * @return cardId
   */
  @Valid 
  @Schema(name = "cardId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cardId")
  public CardIdDto getCardId() {
    return cardId;
  }

  public void setCardId(CardIdDto cardId) {
    this.cardId = cardId;
  }

  public KioskPaymentCardDto cardNumberMasked(String cardNumberMasked) {
    this.cardNumberMasked = cardNumberMasked;
    return this;
  }

  /**
   * Get cardNumberMasked
   * @return cardNumberMasked
   */
  
  @Schema(name = "cardNumberMasked", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cardNumberMasked")
  public String getCardNumberMasked() {
    return cardNumberMasked;
  }

  public void setCardNumberMasked(String cardNumberMasked) {
    this.cardNumberMasked = cardNumberMasked;
  }

  public KioskPaymentCardDto cardOrToken(String cardOrToken) {
    this.cardOrToken = cardOrToken;
    return this;
  }

  /**
   * Get cardOrToken
   * @return cardOrToken
   */
  
  @Schema(name = "cardOrToken", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cardOrToken")
  public String getCardOrToken() {
    return cardOrToken;
  }

  public void setCardOrToken(String cardOrToken) {
    this.cardOrToken = cardOrToken;
  }

  public KioskPaymentCardDto cardPresent(Boolean cardPresent) {
    this.cardPresent = cardPresent;
    return this;
  }

  /**
   * Get cardPresent
   * @return cardPresent
   */
  
  @Schema(name = "cardPresent", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cardPresent")
  public Boolean getCardPresent() {
    return cardPresent;
  }

  public void setCardPresent(Boolean cardPresent) {
    this.cardPresent = cardPresent;
  }

  public KioskPaymentCardDto cardType(String cardType) {
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

  public KioskPaymentCardDto expirationDateExpired(Boolean expirationDateExpired) {
    this.expirationDateExpired = expirationDateExpired;
    return this;
  }

  /**
   * Get expirationDateExpired
   * @return expirationDateExpired
   */
  
  @Schema(name = "expirationDateExpired", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("expirationDateExpired")
  public Boolean getExpirationDateExpired() {
    return expirationDateExpired;
  }

  public void setExpirationDateExpired(Boolean expirationDateExpired) {
    this.expirationDateExpired = expirationDateExpired;
  }

  public KioskPaymentCardDto expirationDateMasked(String expirationDateMasked) {
    this.expirationDateMasked = expirationDateMasked;
    return this;
  }

  /**
   * Get expirationDateMasked
   * @return expirationDateMasked
   */
  
  @Schema(name = "expirationDateMasked", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("expirationDateMasked")
  public String getExpirationDateMasked() {
    return expirationDateMasked;
  }

  public void setExpirationDateMasked(String expirationDateMasked) {
    this.expirationDateMasked = expirationDateMasked;
  }

  public KioskPaymentCardDto processing(String processing) {
    this.processing = processing;
    return this;
  }

  /**
   * Get processing
   * @return processing
   */
  
  @Schema(name = "processing", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("processing")
  public String getProcessing() {
    return processing;
  }

  public void setProcessing(String processing) {
    this.processing = processing;
  }

  public KioskPaymentCardDto swiped(Boolean swiped) {
    this.swiped = swiped;
    return this;
  }

  /**
   * Get swiped
   * @return swiped
   */
  
  @Schema(name = "swiped", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("swiped")
  public Boolean getSwiped() {
    return swiped;
  }

  public void setSwiped(Boolean swiped) {
    this.swiped = swiped;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    KioskPaymentCardDto kioskPaymentCardDto = (KioskPaymentCardDto) o;
    return Objects.equals(this.cardHolderName, kioskPaymentCardDto.cardHolderName) &&
        Objects.equals(this.cardId, kioskPaymentCardDto.cardId) &&
        Objects.equals(this.cardNumberMasked, kioskPaymentCardDto.cardNumberMasked) &&
        Objects.equals(this.cardOrToken, kioskPaymentCardDto.cardOrToken) &&
        Objects.equals(this.cardPresent, kioskPaymentCardDto.cardPresent) &&
        Objects.equals(this.cardType, kioskPaymentCardDto.cardType) &&
        Objects.equals(this.expirationDateExpired, kioskPaymentCardDto.expirationDateExpired) &&
        Objects.equals(this.expirationDateMasked, kioskPaymentCardDto.expirationDateMasked) &&
        Objects.equals(this.processing, kioskPaymentCardDto.processing) &&
        Objects.equals(this.swiped, kioskPaymentCardDto.swiped);
  }

  @Override
  public int hashCode() {
    return Objects.hash(cardHolderName, cardId, cardNumberMasked, cardOrToken, cardPresent, cardType, expirationDateExpired, expirationDateMasked, processing, swiped);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class KioskPaymentCardDto {\n");
    sb.append("    cardHolderName: ").append(toIndentedString(cardHolderName)).append("\n");
    sb.append("    cardId: ").append(toIndentedString(cardId)).append("\n");
    sb.append("    cardNumberMasked: ").append(toIndentedString(cardNumberMasked)).append("\n");
    sb.append("    cardOrToken: ").append(toIndentedString(cardOrToken)).append("\n");
    sb.append("    cardPresent: ").append(toIndentedString(cardPresent)).append("\n");
    sb.append("    cardType: ").append(toIndentedString(cardType)).append("\n");
    sb.append("    expirationDateExpired: ").append(toIndentedString(expirationDateExpired)).append("\n");
    sb.append("    expirationDateMasked: ").append(toIndentedString(expirationDateMasked)).append("\n");
    sb.append("    processing: ").append(toIndentedString(processing)).append("\n");
    sb.append("    swiped: ").append(toIndentedString(swiped)).append("\n");
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

