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
 * PaymentDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:49:41.570079+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PaymentDto {

  private @Nullable String amex;

  private @Nullable String mastercard;

  private @Nullable String piba;

  private @Nullable String pibaEuro;

  private @Nullable String visa;

  public PaymentDto amex(String amex) {
    this.amex = amex;
    return this;
  }

  /**
   * Get amex
   * @return amex
   */
  
  @Schema(name = "amex", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("amex")
  public String getAmex() {
    return amex;
  }

  public void setAmex(String amex) {
    this.amex = amex;
  }

  public PaymentDto mastercard(String mastercard) {
    this.mastercard = mastercard;
    return this;
  }

  /**
   * Get mastercard
   * @return mastercard
   */
  
  @Schema(name = "mastercard", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("mastercard")
  public String getMastercard() {
    return mastercard;
  }

  public void setMastercard(String mastercard) {
    this.mastercard = mastercard;
  }

  public PaymentDto piba(String piba) {
    this.piba = piba;
    return this;
  }

  /**
   * Get piba
   * @return piba
   */
  
  @Schema(name = "piba", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("piba")
  public String getPiba() {
    return piba;
  }

  public void setPiba(String piba) {
    this.piba = piba;
  }

  public PaymentDto pibaEuro(String pibaEuro) {
    this.pibaEuro = pibaEuro;
    return this;
  }

  /**
   * Get pibaEuro
   * @return pibaEuro
   */
  
  @Schema(name = "pibaEuro", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("pibaEuro")
  public String getPibaEuro() {
    return pibaEuro;
  }

  public void setPibaEuro(String pibaEuro) {
    this.pibaEuro = pibaEuro;
  }

  public PaymentDto visa(String visa) {
    this.visa = visa;
    return this;
  }

  /**
   * Get visa
   * @return visa
   */
  
  @Schema(name = "visa", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("visa")
  public String getVisa() {
    return visa;
  }

  public void setVisa(String visa) {
    this.visa = visa;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PaymentDto paymentDto = (PaymentDto) o;
    return Objects.equals(this.amex, paymentDto.amex) &&
        Objects.equals(this.mastercard, paymentDto.mastercard) &&
        Objects.equals(this.piba, paymentDto.piba) &&
        Objects.equals(this.pibaEuro, paymentDto.pibaEuro) &&
        Objects.equals(this.visa, paymentDto.visa);
  }

  @Override
  public int hashCode() {
    return Objects.hash(amex, mastercard, piba, pibaEuro, visa);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PaymentDto {\n");
    sb.append("    amex: ").append(toIndentedString(amex)).append("\n");
    sb.append("    mastercard: ").append(toIndentedString(mastercard)).append("\n");
    sb.append("    piba: ").append(toIndentedString(piba)).append("\n");
    sb.append("    pibaEuro: ").append(toIndentedString(pibaEuro)).append("\n");
    sb.append("    visa: ").append(toIndentedString(visa)).append("\n");
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

