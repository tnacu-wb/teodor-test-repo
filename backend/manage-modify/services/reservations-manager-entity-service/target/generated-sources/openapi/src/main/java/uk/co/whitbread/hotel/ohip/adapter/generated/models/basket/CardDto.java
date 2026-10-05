package uk.co.whitbread.hotel.ohip.adapter.generated.models.basket;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * CardDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:52:44.119190+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CardDto {

  private @Nullable String cardType;

  private @Nullable String cardholderName;

  private @Nullable Boolean cnpRequired;

  private @Nullable String expiryMonth;

  private @Nullable String expiryYear;

  private @Nullable String last4Digits;

  private @Nullable String logoUrl;

  private @Nullable String token;

  private @Nullable String type;

  public CardDto cardType(String cardType) {
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

  public CardDto cardholderName(String cardholderName) {
    this.cardholderName = cardholderName;
    return this;
  }

  /**
   * Get cardholderName
   * @return cardholderName
   */
  
  @Schema(name = "cardholderName", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cardholderName")
  public String getCardholderName() {
    return cardholderName;
  }

  public void setCardholderName(String cardholderName) {
    this.cardholderName = cardholderName;
  }

  public CardDto cnpRequired(Boolean cnpRequired) {
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

  public CardDto expiryMonth(String expiryMonth) {
    this.expiryMonth = expiryMonth;
    return this;
  }

  /**
   * Get expiryMonth
   * @return expiryMonth
   */
  
  @Schema(name = "expiryMonth", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("expiryMonth")
  public String getExpiryMonth() {
    return expiryMonth;
  }

  public void setExpiryMonth(String expiryMonth) {
    this.expiryMonth = expiryMonth;
  }

  public CardDto expiryYear(String expiryYear) {
    this.expiryYear = expiryYear;
    return this;
  }

  /**
   * Get expiryYear
   * @return expiryYear
   */
  
  @Schema(name = "expiryYear", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("expiryYear")
  public String getExpiryYear() {
    return expiryYear;
  }

  public void setExpiryYear(String expiryYear) {
    this.expiryYear = expiryYear;
  }

  public CardDto last4Digits(String last4Digits) {
    this.last4Digits = last4Digits;
    return this;
  }

  /**
   * Get last4Digits
   * @return last4Digits
   */
  
  @Schema(name = "last4Digits", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("last4Digits")
  public String getLast4Digits() {
    return last4Digits;
  }

  public void setLast4Digits(String last4Digits) {
    this.last4Digits = last4Digits;
  }

  public CardDto logoUrl(String logoUrl) {
    this.logoUrl = logoUrl;
    return this;
  }

  /**
   * Get logoUrl
   * @return logoUrl
   */
  
  @Schema(name = "logoUrl", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("logoUrl")
  public String getLogoUrl() {
    return logoUrl;
  }

  public void setLogoUrl(String logoUrl) {
    this.logoUrl = logoUrl;
  }

  public CardDto token(String token) {
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

  public CardDto type(String type) {
    this.type = type;
    return this;
  }

  /**
   * Get type
   * @return type
   */
  
  @Schema(name = "type", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("type")
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CardDto cardDto = (CardDto) o;
    return Objects.equals(this.cardType, cardDto.cardType) &&
        Objects.equals(this.cardholderName, cardDto.cardholderName) &&
        Objects.equals(this.cnpRequired, cardDto.cnpRequired) &&
        Objects.equals(this.expiryMonth, cardDto.expiryMonth) &&
        Objects.equals(this.expiryYear, cardDto.expiryYear) &&
        Objects.equals(this.last4Digits, cardDto.last4Digits) &&
        Objects.equals(this.logoUrl, cardDto.logoUrl) &&
        Objects.equals(this.token, cardDto.token) &&
        Objects.equals(this.type, cardDto.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(cardType, cardholderName, cnpRequired, expiryMonth, expiryYear, last4Digits, logoUrl, token, type);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CardDto {\n");
    sb.append("    cardType: ").append(toIndentedString(cardType)).append("\n");
    sb.append("    cardholderName: ").append(toIndentedString(cardholderName)).append("\n");
    sb.append("    cnpRequired: ").append(toIndentedString(cnpRequired)).append("\n");
    sb.append("    expiryMonth: ").append(toIndentedString(expiryMonth)).append("\n");
    sb.append("    expiryYear: ").append(toIndentedString(expiryYear)).append("\n");
    sb.append("    last4Digits: ").append(toIndentedString(last4Digits)).append("\n");
    sb.append("    logoUrl: ").append(toIndentedString(logoUrl)).append("\n");
    sb.append("    token: ").append(toIndentedString(token)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
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

