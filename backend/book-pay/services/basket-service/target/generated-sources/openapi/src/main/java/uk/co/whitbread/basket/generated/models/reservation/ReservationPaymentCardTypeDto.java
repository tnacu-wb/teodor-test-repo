package uk.co.whitbread.basket.generated.models.reservation;

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
 * ReservationPaymentCardTypeDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:03.993735+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ReservationPaymentCardTypeDto {

  private @Nullable String cardHolderName;

  private @Nullable String cardNumberMasked;

  private @Nullable String cardType;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private @Nullable LocalDate expirationDate;

  private @Nullable String paymentMethod;

  private @Nullable String token;

  public ReservationPaymentCardTypeDto cardHolderName(String cardHolderName) {
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

  public ReservationPaymentCardTypeDto cardNumberMasked(String cardNumberMasked) {
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

  public ReservationPaymentCardTypeDto cardType(String cardType) {
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

  public ReservationPaymentCardTypeDto expirationDate(LocalDate expirationDate) {
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

  public ReservationPaymentCardTypeDto paymentMethod(String paymentMethod) {
    this.paymentMethod = paymentMethod;
    return this;
  }

  /**
   * Get paymentMethod
   * @return paymentMethod
   */
  
  @Schema(name = "paymentMethod", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("paymentMethod")
  public String getPaymentMethod() {
    return paymentMethod;
  }

  public void setPaymentMethod(String paymentMethod) {
    this.paymentMethod = paymentMethod;
  }

  public ReservationPaymentCardTypeDto token(String token) {
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
    ReservationPaymentCardTypeDto reservationPaymentCardTypeDto = (ReservationPaymentCardTypeDto) o;
    return Objects.equals(this.cardHolderName, reservationPaymentCardTypeDto.cardHolderName) &&
        Objects.equals(this.cardNumberMasked, reservationPaymentCardTypeDto.cardNumberMasked) &&
        Objects.equals(this.cardType, reservationPaymentCardTypeDto.cardType) &&
        Objects.equals(this.expirationDate, reservationPaymentCardTypeDto.expirationDate) &&
        Objects.equals(this.paymentMethod, reservationPaymentCardTypeDto.paymentMethod) &&
        Objects.equals(this.token, reservationPaymentCardTypeDto.token);
  }

  @Override
  public int hashCode() {
    return Objects.hash(cardHolderName, cardNumberMasked, cardType, expirationDate, paymentMethod, token);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ReservationPaymentCardTypeDto {\n");
    sb.append("    cardHolderName: ").append(toIndentedString(cardHolderName)).append("\n");
    sb.append("    cardNumberMasked: ").append(toIndentedString(cardNumberMasked)).append("\n");
    sb.append("    cardType: ").append(toIndentedString(cardType)).append("\n");
    sb.append("    expirationDate: ").append(toIndentedString(expirationDate)).append("\n");
    sb.append("    paymentMethod: ").append(toIndentedString(paymentMethod)).append("\n");
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

