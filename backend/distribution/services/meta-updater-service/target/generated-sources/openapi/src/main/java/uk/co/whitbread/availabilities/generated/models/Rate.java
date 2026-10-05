package uk.co.whitbread.availabilities.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.math.BigDecimal;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Rate
 */
@lombok.Builder @lombok.AllArgsConstructor

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:31.761066+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class Rate {

  private @Nullable Boolean availability;

  private @Nullable String rateClassification;

  private @Nullable String rateCode;

  private @Nullable BigDecimal amount;

  private @Nullable String currency;

  private @Nullable Integer minNights;

  private @Nullable Integer maxNights;

  private @Nullable BigDecimal premiumAmount;

  public Rate availability(Boolean availability) {
    this.availability = availability;
    return this;
  }

  /**
   * Get availability
   * @return availability
   */
  
  @Schema(name = "availability", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("availability")
  public Boolean getAvailability() {
    return availability;
  }

  public void setAvailability(Boolean availability) {
    this.availability = availability;
  }

  public Rate rateClassification(String rateClassification) {
    this.rateClassification = rateClassification;
    return this;
  }

  /**
   * Get rateClassification
   * @return rateClassification
   */
  
  @Schema(name = "rateClassification", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("rateClassification")
  public String getRateClassification() {
    return rateClassification;
  }

  public void setRateClassification(String rateClassification) {
    this.rateClassification = rateClassification;
  }

  public Rate rateCode(String rateCode) {
    this.rateCode = rateCode;
    return this;
  }

  /**
   * Get rateCode
   * @return rateCode
   */
  
  @Schema(name = "rateCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("rateCode")
  public String getRateCode() {
    return rateCode;
  }

  public void setRateCode(String rateCode) {
    this.rateCode = rateCode;
  }

  public Rate amount(BigDecimal amount) {
    this.amount = amount;
    return this;
  }

  /**
   * Get amount
   * @return amount
   */
  @Valid 
  @Schema(name = "amount", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("amount")
  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public Rate currency(String currency) {
    this.currency = currency;
    return this;
  }

  /**
   * Get currency
   * @return currency
   */
  
  @Schema(name = "currency", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("currency")
  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public Rate minNights(Integer minNights) {
    this.minNights = minNights;
    return this;
  }

  /**
   * Get minNights
   * @return minNights
   */
  
  @Schema(name = "minNights", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("minNights")
  public Integer getMinNights() {
    return minNights;
  }

  public void setMinNights(Integer minNights) {
    this.minNights = minNights;
  }

  public Rate maxNights(Integer maxNights) {
    this.maxNights = maxNights;
    return this;
  }

  /**
   * Get maxNights
   * @return maxNights
   */
  
  @Schema(name = "maxNights", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("maxNights")
  public Integer getMaxNights() {
    return maxNights;
  }

  public void setMaxNights(Integer maxNights) {
    this.maxNights = maxNights;
  }

  public Rate premiumAmount(BigDecimal premiumAmount) {
    this.premiumAmount = premiumAmount;
    return this;
  }

  /**
   * Get premiumAmount
   * @return premiumAmount
   */
  @Valid 
  @Schema(name = "premiumAmount", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("premiumAmount")
  public BigDecimal getPremiumAmount() {
    return premiumAmount;
  }

  public void setPremiumAmount(BigDecimal premiumAmount) {
    this.premiumAmount = premiumAmount;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Rate rate = (Rate) o;
    return Objects.equals(this.availability, rate.availability) &&
        Objects.equals(this.rateClassification, rate.rateClassification) &&
        Objects.equals(this.rateCode, rate.rateCode) &&
        Objects.equals(this.amount, rate.amount) &&
        Objects.equals(this.currency, rate.currency) &&
        Objects.equals(this.minNights, rate.minNights) &&
        Objects.equals(this.maxNights, rate.maxNights) &&
        Objects.equals(this.premiumAmount, rate.premiumAmount);
  }

  @Override
  public int hashCode() {
    return Objects.hash(availability, rateClassification, rateCode, amount, currency, minNights, maxNights, premiumAmount);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Rate {\n");
    sb.append("    availability: ").append(toIndentedString(availability)).append("\n");
    sb.append("    rateClassification: ").append(toIndentedString(rateClassification)).append("\n");
    sb.append("    rateCode: ").append(toIndentedString(rateCode)).append("\n");
    sb.append("    amount: ").append(toIndentedString(amount)).append("\n");
    sb.append("    currency: ").append(toIndentedString(currency)).append("\n");
    sb.append("    minNights: ").append(toIndentedString(minNights)).append("\n");
    sb.append("    maxNights: ").append(toIndentedString(maxNights)).append("\n");
    sb.append("    premiumAmount: ").append(toIndentedString(premiumAmount)).append("\n");
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

