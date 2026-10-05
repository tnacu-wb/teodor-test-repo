package uk.co.whitbread.basket.generated.models.payments;

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
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Schema(name = "Card", description = "Customers card for refund of the payment.")
@JsonTypeName("Card")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:02.841275+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CardDto {

  private @Nullable String cvv;

  private @Nullable String expiryMonth;

  private @Nullable String expiryYear;

  private @Nullable String token;

  public CardDto cvv(String cvv) {
    this.cvv = cvv;
    return this;
  }

  /**
   * CVV for the payment card/token.
   * @return cvv
   */
  
  @Schema(name = "cvv", example = "222", description = "CVV for the payment card/token.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cvv")
  public String getCvv() {
    return cvv;
  }

  public void setCvv(String cvv) {
    this.cvv = cvv;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CardDto card = (CardDto) o;
    return Objects.equals(this.cvv, card.cvv) &&
        Objects.equals(this.expiryMonth, card.expiryMonth) &&
        Objects.equals(this.expiryYear, card.expiryYear) &&
        Objects.equals(this.token, card.token);
  }

  @Override
  public int hashCode() {
    return Objects.hash(cvv, expiryMonth, expiryYear, token);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CardDto {\n");
    sb.append("    cvv: ").append(toIndentedString(cvv)).append("\n");
    sb.append("    expiryMonth: ").append(toIndentedString(expiryMonth)).append("\n");
    sb.append("    expiryYear: ").append(toIndentedString(expiryYear)).append("\n");
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

