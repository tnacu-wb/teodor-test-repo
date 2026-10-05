package uk.co.whitbread.hotel.reservation.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * PaymentCardDetailsDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:09:52.163805+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PaymentCardDetailsDto {

  private @Nullable String cardHolderName;

  private @Nullable String cardLogoSrc;

  private @Nullable String cardName;

  private @Nullable String cardNumberLast4Digits;

  private @Nullable String cardNumberMasked;

  private @Nullable String cardType;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private @Nullable LocalDate expirationDate;

  private @Nullable String token;

  public PaymentCardDetailsDto cardHolderName(String cardHolderName) {
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

  public PaymentCardDetailsDto cardLogoSrc(String cardLogoSrc) {
    this.cardLogoSrc = cardLogoSrc;
    return this;
  }

  /**
   * Get cardLogoSrc
   * @return cardLogoSrc
   */
  
  @Schema(name = "cardLogoSrc", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cardLogoSrc")
  public String getCardLogoSrc() {
    return cardLogoSrc;
  }

  public void setCardLogoSrc(String cardLogoSrc) {
    this.cardLogoSrc = cardLogoSrc;
  }

  public PaymentCardDetailsDto cardName(String cardName) {
    this.cardName = cardName;
    return this;
  }

  /**
   * Get cardName
   * @return cardName
   */
  
  @Schema(name = "cardName", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cardName")
  public String getCardName() {
    return cardName;
  }

  public void setCardName(String cardName) {
    this.cardName = cardName;
  }

  public PaymentCardDetailsDto cardNumberLast4Digits(String cardNumberLast4Digits) {
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

  public PaymentCardDetailsDto cardNumberMasked(String cardNumberMasked) {
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

  public PaymentCardDetailsDto cardType(String cardType) {
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

  public PaymentCardDetailsDto expirationDate(LocalDate expirationDate) {
    this.expirationDate = expirationDate;
    return this;
  }

  /**
   * Get expirationDate
   * @return expirationDate
   */
  @Valid 
  @Schema(name = "expirationDate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("expirationDate")
  public LocalDate getExpirationDate() {
    return expirationDate;
  }

  public void setExpirationDate(LocalDate expirationDate) {
    this.expirationDate = expirationDate;
  }

  public PaymentCardDetailsDto token(String token) {
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
    PaymentCardDetailsDto paymentCardDetailsDto = (PaymentCardDetailsDto) o;
    return Objects.equals(this.cardHolderName, paymentCardDetailsDto.cardHolderName) &&
        Objects.equals(this.cardLogoSrc, paymentCardDetailsDto.cardLogoSrc) &&
        Objects.equals(this.cardName, paymentCardDetailsDto.cardName) &&
        Objects.equals(this.cardNumberLast4Digits, paymentCardDetailsDto.cardNumberLast4Digits) &&
        Objects.equals(this.cardNumberMasked, paymentCardDetailsDto.cardNumberMasked) &&
        Objects.equals(this.cardType, paymentCardDetailsDto.cardType) &&
        Objects.equals(this.expirationDate, paymentCardDetailsDto.expirationDate) &&
        Objects.equals(this.token, paymentCardDetailsDto.token);
  }

  @Override
  public int hashCode() {
    return Objects.hash(cardHolderName, cardLogoSrc, cardName, cardNumberLast4Digits, cardNumberMasked, cardType, expirationDate, token);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PaymentCardDetailsDto {\n");
    sb.append("    cardHolderName: ").append(toIndentedString(cardHolderName)).append("\n");
    sb.append("    cardLogoSrc: ").append(toIndentedString(cardLogoSrc)).append("\n");
    sb.append("    cardName: ").append(toIndentedString(cardName)).append("\n");
    sb.append("    cardNumberLast4Digits: ").append(toIndentedString(cardNumberLast4Digits)).append("\n");
    sb.append("    cardNumberMasked: ").append(toIndentedString(cardNumberMasked)).append("\n");
    sb.append("    cardType: ").append(toIndentedString(cardType)).append("\n");
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

