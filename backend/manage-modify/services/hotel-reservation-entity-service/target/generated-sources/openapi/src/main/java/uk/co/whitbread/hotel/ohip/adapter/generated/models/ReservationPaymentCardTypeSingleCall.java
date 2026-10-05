package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * ReservationPaymentCardTypeSingleCall
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:15.274731+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ReservationPaymentCardTypeSingleCall {

  private @Nullable String cardHolderName;

  private @Nullable String cardNumberMasked;

  private @Nullable String cardType;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private @Nullable LocalDate expirationDate;

  private @Nullable String paymentMethod;

  private @Nullable String token;

  public ReservationPaymentCardTypeSingleCall cardHolderName(String cardHolderName) {
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

  public ReservationPaymentCardTypeSingleCall cardNumberMasked(String cardNumberMasked) {
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

  public ReservationPaymentCardTypeSingleCall cardType(String cardType) {
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

  public ReservationPaymentCardTypeSingleCall expirationDate(LocalDate expirationDate) {
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

  public ReservationPaymentCardTypeSingleCall paymentMethod(String paymentMethod) {
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

  public ReservationPaymentCardTypeSingleCall token(String token) {
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
    ReservationPaymentCardTypeSingleCall reservationPaymentCardTypeSingleCall = (ReservationPaymentCardTypeSingleCall) o;
    return Objects.equals(this.cardHolderName, reservationPaymentCardTypeSingleCall.cardHolderName) &&
        Objects.equals(this.cardNumberMasked, reservationPaymentCardTypeSingleCall.cardNumberMasked) &&
        Objects.equals(this.cardType, reservationPaymentCardTypeSingleCall.cardType) &&
        Objects.equals(this.expirationDate, reservationPaymentCardTypeSingleCall.expirationDate) &&
        Objects.equals(this.paymentMethod, reservationPaymentCardTypeSingleCall.paymentMethod) &&
        Objects.equals(this.token, reservationPaymentCardTypeSingleCall.token);
  }

  @Override
  public int hashCode() {
    return Objects.hash(cardHolderName, cardNumberMasked, cardType, expirationDate, paymentMethod, token);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ReservationPaymentCardTypeSingleCall {\n");
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

