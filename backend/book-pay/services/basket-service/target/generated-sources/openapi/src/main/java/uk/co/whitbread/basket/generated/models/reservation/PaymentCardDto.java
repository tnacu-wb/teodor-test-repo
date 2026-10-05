package uk.co.whitbread.basket.generated.models.reservation;

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
 * PaymentCardDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:03.993735+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PaymentCardDto {

  private @Nullable String cardHolderName;

  private @Nullable String cardNumberLast4Digits;

  private @Nullable String cardType;

  private @Nullable String citId;

  private @Nullable String expirationDate;

  private @Nullable String token;

  public PaymentCardDto cardHolderName(String cardHolderName) {
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

  public PaymentCardDto cardNumberLast4Digits(String cardNumberLast4Digits) {
    this.cardNumberLast4Digits = cardNumberLast4Digits;
    return this;
  }

  /**
   * Get cardNumberLast4Digits
   * @return cardNumberLast4Digits
   */
  
  @Schema(name = "cardNumberLast4Digits", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cardNumberLast4Digits")
  public String getCardNumberLast4Digits() {
    return cardNumberLast4Digits;
  }

  public void setCardNumberLast4Digits(String cardNumberLast4Digits) {
    this.cardNumberLast4Digits = cardNumberLast4Digits;
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

  public PaymentCardDto citId(String citId) {
    this.citId = citId;
    return this;
  }

  /**
   * Get citId
   * @return citId
   */
  
  @Schema(name = "citId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("citId")
  public String getCitId() {
    return citId;
  }

  public void setCitId(String citId) {
    this.citId = citId;
  }

  public PaymentCardDto expirationDate(String expirationDate) {
    this.expirationDate = expirationDate;
    return this;
  }

  /**
   * Get expirationDate
   * @return expirationDate
   */
  
  @Schema(name = "expirationDate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("expirationDate")
  public String getExpirationDate() {
    return expirationDate;
  }

  public void setExpirationDate(String expirationDate) {
    this.expirationDate = expirationDate;
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
    return Objects.equals(this.cardHolderName, paymentCardDto.cardHolderName) &&
        Objects.equals(this.cardNumberLast4Digits, paymentCardDto.cardNumberLast4Digits) &&
        Objects.equals(this.cardType, paymentCardDto.cardType) &&
        Objects.equals(this.citId, paymentCardDto.citId) &&
        Objects.equals(this.expirationDate, paymentCardDto.expirationDate) &&
        Objects.equals(this.token, paymentCardDto.token);
  }

  @Override
  public int hashCode() {
    return Objects.hash(cardHolderName, cardNumberLast4Digits, cardType, citId, expirationDate, token);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PaymentCardDto {\n");
    sb.append("    cardHolderName: ").append(toIndentedString(cardHolderName)).append("\n");
    sb.append("    cardNumberLast4Digits: ").append(toIndentedString(cardNumberLast4Digits)).append("\n");
    sb.append("    cardType: ").append(toIndentedString(cardType)).append("\n");
    sb.append("    citId: ").append(toIndentedString(citId)).append("\n");
    sb.append("    expirationDate: ").append(toIndentedString(expirationDate)).append("\n");
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

