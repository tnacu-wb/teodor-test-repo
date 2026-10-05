package uk.co.whitbread.hotel.ocd.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.math.BigDecimal;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.OfferRateMode;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * OfferMinMaxTotalType
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:32.554166+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class OfferMinMaxTotalType {

  private @Nullable BigDecimal amountBeforeTax;

  private @Nullable BigDecimal amountAfterTax;

  private @Nullable String currencyCode;

  private @Nullable OfferRateMode rateMode;

  private @Nullable Boolean isCommissionable;

  private Boolean hasRateChange = false;

  public OfferMinMaxTotalType amountBeforeTax(BigDecimal amountBeforeTax) {
    this.amountBeforeTax = amountBeforeTax;
    return this;
  }

  /**
   * The available room rate not including any associated taxes (e.g., sales tax, VAT, GST or any associated taxes).
   * @return amountBeforeTax
   */
  @Valid 
  @Schema(name = "amountBeforeTax", example = "100.5", description = "The available room rate not including any associated taxes (e.g., sales tax, VAT, GST or any associated taxes).", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("amountBeforeTax")
  public BigDecimal getAmountBeforeTax() {
    return amountBeforeTax;
  }

  public void setAmountBeforeTax(BigDecimal amountBeforeTax) {
    this.amountBeforeTax = amountBeforeTax;
  }

  public OfferMinMaxTotalType amountAfterTax(BigDecimal amountAfterTax) {
    this.amountAfterTax = amountAfterTax;
    return this;
  }

  /**
   * The available room rate including all associated taxes (e.g., sales tax, VAT, GST or any associated tax).
   * @return amountAfterTax
   */
  @Valid 
  @Schema(name = "amountAfterTax", example = "123.2", description = "The available room rate including all associated taxes (e.g., sales tax, VAT, GST or any associated tax).", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("amountAfterTax")
  public BigDecimal getAmountAfterTax() {
    return amountAfterTax;
  }

  public void setAmountAfterTax(BigDecimal amountAfterTax) {
    this.amountAfterTax = amountAfterTax;
  }

  public OfferMinMaxTotalType currencyCode(String currencyCode) {
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

  public OfferMinMaxTotalType rateMode(OfferRateMode rateMode) {
    this.rateMode = rateMode;
    return this;
  }

  /**
   * Get rateMode
   * @return rateMode
   */
  @Valid 
  @Schema(name = "rateMode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("rateMode")
  public OfferRateMode getRateMode() {
    return rateMode;
  }

  public void setRateMode(OfferRateMode rateMode) {
    this.rateMode = rateMode;
  }

  public OfferMinMaxTotalType isCommissionable(Boolean isCommissionable) {
    this.isCommissionable = isCommissionable;
    return this;
  }

  /**
   * When true indicates the rate plan is commissionable.
   * @return isCommissionable
   */
  
  @Schema(name = "isCommissionable", example = "true", description = "When true indicates the rate plan is commissionable.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("isCommissionable")
  public Boolean getIsCommissionable() {
    return isCommissionable;
  }

  public void setIsCommissionable(Boolean isCommissionable) {
    this.isCommissionable = isCommissionable;
  }

  public OfferMinMaxTotalType hasRateChange(Boolean hasRateChange) {
    this.hasRateChange = hasRateChange;
    return this;
  }

  /**
   * When true indicates there is a rate change over the course of a multiple night stay.
   * @return hasRateChange
   */
  
  @Schema(name = "hasRateChange", example = "false", description = "When true indicates there is a rate change over the course of a multiple night stay.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hasRateChange")
  public Boolean getHasRateChange() {
    return hasRateChange;
  }

  public void setHasRateChange(Boolean hasRateChange) {
    this.hasRateChange = hasRateChange;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OfferMinMaxTotalType offerMinMaxTotalType = (OfferMinMaxTotalType) o;
    return Objects.equals(this.amountBeforeTax, offerMinMaxTotalType.amountBeforeTax) &&
        Objects.equals(this.amountAfterTax, offerMinMaxTotalType.amountAfterTax) &&
        Objects.equals(this.currencyCode, offerMinMaxTotalType.currencyCode) &&
        Objects.equals(this.rateMode, offerMinMaxTotalType.rateMode) &&
        Objects.equals(this.isCommissionable, offerMinMaxTotalType.isCommissionable) &&
        Objects.equals(this.hasRateChange, offerMinMaxTotalType.hasRateChange);
  }

  @Override
  public int hashCode() {
    return Objects.hash(amountBeforeTax, amountAfterTax, currencyCode, rateMode, isCommissionable, hasRateChange);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OfferMinMaxTotalType {\n");
    sb.append("    amountBeforeTax: ").append(toIndentedString(amountBeforeTax)).append("\n");
    sb.append("    amountAfterTax: ").append(toIndentedString(amountAfterTax)).append("\n");
    sb.append("    currencyCode: ").append(toIndentedString(currencyCode)).append("\n");
    sb.append("    rateMode: ").append(toIndentedString(rateMode)).append("\n");
    sb.append("    isCommissionable: ").append(toIndentedString(isCommissionable)).append("\n");
    sb.append("    hasRateChange: ").append(toIndentedString(hasRateChange)).append("\n");
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

