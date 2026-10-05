package uk.co.whitbread.hotel.ocd.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.math.BigDecimal;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.PolicyBasisType;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * The method used to compute the penalty associated to the cancellation rule.
 */

@Schema(name = "CancelPolicyAmountPercentType", description = "The method used to compute the penalty associated to the cancellation rule.")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:32.554166+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CancelPolicyAmountPercentType {

  private @Nullable Integer nights;

  private @Nullable Double percent;

  private @Nullable BigDecimal amount;

  private @Nullable PolicyBasisType basisType;

  private @Nullable Boolean taxInclusive;

  private @Nullable String currencyCode;

  public CancelPolicyAmountPercentType nights(Integer nights) {
    this.nights = nights;
    return this;
  }

  /**
   * The number of nights of the hotel stay used to calculate the cancellation amount when basis type is nights.
   * @return nights
   */
  
  @Schema(name = "nights", example = "2", description = "The number of nights of the hotel stay used to calculate the cancellation amount when basis type is nights.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("nights")
  public Integer getNights() {
    return nights;
  }

  public void setNights(Integer nights) {
    this.nights = nights;
  }

  public CancelPolicyAmountPercentType percent(Double percent) {
    this.percent = percent;
    return this;
  }

  /**
   * The percentage of the stay used to calculate the cancellation amount when basis type is Percentage or NightPercentage.
   * @return percent
   */
  
  @Schema(name = "percent", example = "50.0", description = "The percentage of the stay used to calculate the cancellation amount when basis type is Percentage or NightPercentage.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("percent")
  public Double getPercent() {
    return percent;
  }

  public void setPercent(Double percent) {
    this.percent = percent;
  }

  public CancelPolicyAmountPercentType amount(BigDecimal amount) {
    this.amount = amount;
    return this;
  }

  /**
   * The total amount of the cancellation penalty.
   * @return amount
   */
  @Valid 
  @Schema(name = "amount", example = "540.0", description = "The total amount of the cancellation penalty.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("amount")
  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public CancelPolicyAmountPercentType basisType(PolicyBasisType basisType) {
    this.basisType = basisType;
    return this;
  }

  /**
   * Get basisType
   * @return basisType
   */
  @Valid 
  @Schema(name = "basisType", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("basisType")
  public PolicyBasisType getBasisType() {
    return basisType;
  }

  public void setBasisType(PolicyBasisType basisType) {
    this.basisType = basisType;
  }

  public CancelPolicyAmountPercentType taxInclusive(Boolean taxInclusive) {
    this.taxInclusive = taxInclusive;
    return this;
  }

  /**
   * When true the cancellation amount charged includes taxes.
   * @return taxInclusive
   */
  
  @Schema(name = "taxInclusive", example = "true", description = "When true the cancellation amount charged includes taxes.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("taxInclusive")
  public Boolean getTaxInclusive() {
    return taxInclusive;
  }

  public void setTaxInclusive(Boolean taxInclusive) {
    this.taxInclusive = taxInclusive;
  }

  public CancelPolicyAmountPercentType currencyCode(String currencyCode) {
    this.currencyCode = currencyCode;
    return this;
  }

  /**
   * The code used for the local currency of the property. ISO 4217 currency code
   * @return currencyCode
   */
  @Pattern(regexp = "[A-Z]{3}") @Size(min = 3, max = 3) 
  @Schema(name = "currencyCode", example = "USD", description = "The code used for the local currency of the property. ISO 4217 currency code", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("currencyCode")
  public String getCurrencyCode() {
    return currencyCode;
  }

  public void setCurrencyCode(String currencyCode) {
    this.currencyCode = currencyCode;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CancelPolicyAmountPercentType cancelPolicyAmountPercentType = (CancelPolicyAmountPercentType) o;
    return Objects.equals(this.nights, cancelPolicyAmountPercentType.nights) &&
        Objects.equals(this.percent, cancelPolicyAmountPercentType.percent) &&
        Objects.equals(this.amount, cancelPolicyAmountPercentType.amount) &&
        Objects.equals(this.basisType, cancelPolicyAmountPercentType.basisType) &&
        Objects.equals(this.taxInclusive, cancelPolicyAmountPercentType.taxInclusive) &&
        Objects.equals(this.currencyCode, cancelPolicyAmountPercentType.currencyCode);
  }

  @Override
  public int hashCode() {
    return Objects.hash(nights, percent, amount, basisType, taxInclusive, currencyCode);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CancelPolicyAmountPercentType {\n");
    sb.append("    nights: ").append(toIndentedString(nights)).append("\n");
    sb.append("    percent: ").append(toIndentedString(percent)).append("\n");
    sb.append("    amount: ").append(toIndentedString(amount)).append("\n");
    sb.append("    basisType: ").append(toIndentedString(basisType)).append("\n");
    sb.append("    taxInclusive: ").append(toIndentedString(taxInclusive)).append("\n");
    sb.append("    currencyCode: ").append(toIndentedString(currencyCode)).append("\n");
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

