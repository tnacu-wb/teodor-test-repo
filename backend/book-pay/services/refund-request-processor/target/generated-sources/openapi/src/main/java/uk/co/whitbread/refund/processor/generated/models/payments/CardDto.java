package uk.co.whitbread.refund.processor.generated.models.payments;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Customers card for refund of the payment.
 */

@Schema(name = "Card", description = "Customers card for refund of the payment.")
@JsonTypeName("Card")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:12:20.597747+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CardDto {

  private @Nullable String cardholderName;

  private @Nullable String token;

  private @Nullable String expiryMonth;

  private @Nullable String expiryYear;

  private @Nullable String cardType;

  private @Nullable Boolean cnpRequired;

  private @Nullable String logoUrl;

  private @Nullable String type;

  public CardDto cardholderName(String cardholderName) {
    this.cardholderName = cardholderName;
    return this;
  }

  /**
   * Card holder name.
   * @return cardholderName
   */
  
  @Schema(name = "cardholderName", example = "Smith", description = "Card holder name.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cardholderName")
  public String getCardholderName() {
    return cardholderName;
  }

  public void setCardholderName(String cardholderName) {
    this.cardholderName = cardholderName;
  }

  public CardDto token(String token) {
    this.token = token;
    return this;
  }

  /**
   * 3C Payment token.
   * @return token
   */
  
  @Schema(name = "token", example = "4943056398164344242", description = "3C Payment token.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("token")
  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public CardDto expiryMonth(String expiryMonth) {
    this.expiryMonth = expiryMonth;
    return this;
  }

  /**
   * Expiry month for the payment card/token.
   * @return expiryMonth
   */
  
  @Schema(name = "expiryMonth", example = "01", description = "Expiry month for the payment card/token.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
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
   * Expiry year for the payment card/token.
   * @return expiryYear
   */
  
  @Schema(name = "expiryYear", example = "21", description = "Expiry year for the payment card/token.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("expiryYear")
  public String getExpiryYear() {
    return expiryYear;
  }

  public void setExpiryYear(String expiryYear) {
    this.expiryYear = expiryYear;
  }

  public CardDto cardType(String cardType) {
    this.cardType = cardType;
    return this;
  }

  /**
   * Card Type.
   * @return cardType
   */
  
  @Schema(name = "cardType", example = "BUSINESS_PERSONAL_STORED_CARD", description = "Card Type.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cardType")
  public String getCardType() {
    return cardType;
  }

  public void setCardType(String cardType) {
    this.cardType = cardType;
  }

  public CardDto cnpRequired(Boolean cnpRequired) {
    this.cnpRequired = cnpRequired;
    return this;
  }

  /**
   * CNP.
   * @return cnpRequired
   */
  
  @Schema(name = "cnpRequired", example = "true", description = "CNP.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cnpRequired")
  public Boolean getCnpRequired() {
    return cnpRequired;
  }

  public void setCnpRequired(Boolean cnpRequired) {
    this.cnpRequired = cnpRequired;
  }

  public CardDto logoUrl(String logoUrl) {
    this.logoUrl = logoUrl;
    return this;
  }

  /**
   * Card log.
   * @return logoUrl
   */
  
  @Schema(name = "logoUrl", example = "/content/dam/global/booking/VC.jpg", description = "Card log.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("logoUrl")
  public String getLogoUrl() {
    return logoUrl;
  }

  public void setLogoUrl(String logoUrl) {
    this.logoUrl = logoUrl;
  }

  public CardDto type(String type) {
    this.type = type;
    return this;
  }

  /**
   * Type.
   * @return type
   */
  
  @Schema(name = "type", example = "VI", description = "Type.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
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
    CardDto card = (CardDto) o;
    return Objects.equals(this.cardholderName, card.cardholderName) &&
        Objects.equals(this.token, card.token) &&
        Objects.equals(this.expiryMonth, card.expiryMonth) &&
        Objects.equals(this.expiryYear, card.expiryYear) &&
        Objects.equals(this.cardType, card.cardType) &&
        Objects.equals(this.cnpRequired, card.cnpRequired) &&
        Objects.equals(this.logoUrl, card.logoUrl) &&
        Objects.equals(this.type, card.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(cardholderName, token, expiryMonth, expiryYear, cardType, cnpRequired, logoUrl, type);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CardDto {\n");
    sb.append("    cardholderName: ").append(toIndentedString(cardholderName)).append("\n");
    sb.append("    token: ").append(toIndentedString(token)).append("\n");
    sb.append("    expiryMonth: ").append(toIndentedString(expiryMonth)).append("\n");
    sb.append("    expiryYear: ").append(toIndentedString(expiryYear)).append("\n");
    sb.append("    cardType: ").append(toIndentedString(cardType)).append("\n");
    sb.append("    cnpRequired: ").append(toIndentedString(cnpRequired)).append("\n");
    sb.append("    logoUrl: ").append(toIndentedString(logoUrl)).append("\n");
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

