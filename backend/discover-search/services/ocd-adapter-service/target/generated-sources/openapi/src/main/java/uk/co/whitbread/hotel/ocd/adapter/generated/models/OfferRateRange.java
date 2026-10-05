package uk.co.whitbread.hotel.ocd.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.OfferRateTimeUnit;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * Rate range information
 */

@Schema(name = "OfferRateRange", description = "Rate range information")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:32.554166+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class OfferRateRange {

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private @Nullable LocalDate start;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private @Nullable LocalDate end;

  private @Nullable BigDecimal amountBeforeTax;

  private @Nullable BigDecimal amountAfterTax;

  private @Nullable String currencyCode;

  private @Nullable OfferRateTimeUnit rateTimeUnit;

  public OfferRateRange start(LocalDate start) {
    this.start = start;
    return this;
  }

  /**
   * The first night of the stay where the charge will be applied.
   * @return start
   */
  @Valid 
  @Schema(name = "start", example = "2021-06-01", description = "The first night of the stay where the charge will be applied.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("start")
  public LocalDate getStart() {
    return start;
  }

  public void setStart(LocalDate start) {
    this.start = start;
  }

  public OfferRateRange end(LocalDate end) {
    this.end = end;
    return this;
  }

  /**
   * The last night of the stay where the charge will be applied.
   * @return end
   */
  @Valid 
  @Schema(name = "end", example = "2021-06-05", description = "The last night of the stay where the charge will be applied.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("end")
  public LocalDate getEnd() {
    return end;
  }

  public void setEnd(LocalDate end) {
    this.end = end;
  }

  public OfferRateRange amountBeforeTax(BigDecimal amountBeforeTax) {
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

  public OfferRateRange amountAfterTax(BigDecimal amountAfterTax) {
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

  public OfferRateRange currencyCode(String currencyCode) {
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

  public OfferRateRange rateTimeUnit(OfferRateTimeUnit rateTimeUnit) {
    this.rateTimeUnit = rateTimeUnit;
    return this;
  }

  /**
   * Get rateTimeUnit
   * @return rateTimeUnit
   */
  @Valid 
  @Schema(name = "rateTimeUnit", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("rateTimeUnit")
  public OfferRateTimeUnit getRateTimeUnit() {
    return rateTimeUnit;
  }

  public void setRateTimeUnit(OfferRateTimeUnit rateTimeUnit) {
    this.rateTimeUnit = rateTimeUnit;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OfferRateRange offerRateRange = (OfferRateRange) o;
    return Objects.equals(this.start, offerRateRange.start) &&
        Objects.equals(this.end, offerRateRange.end) &&
        Objects.equals(this.amountBeforeTax, offerRateRange.amountBeforeTax) &&
        Objects.equals(this.amountAfterTax, offerRateRange.amountAfterTax) &&
        Objects.equals(this.currencyCode, offerRateRange.currencyCode) &&
        Objects.equals(this.rateTimeUnit, offerRateRange.rateTimeUnit);
  }

  @Override
  public int hashCode() {
    return Objects.hash(start, end, amountBeforeTax, amountAfterTax, currencyCode, rateTimeUnit);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OfferRateRange {\n");
    sb.append("    start: ").append(toIndentedString(start)).append("\n");
    sb.append("    end: ").append(toIndentedString(end)).append("\n");
    sb.append("    amountBeforeTax: ").append(toIndentedString(amountBeforeTax)).append("\n");
    sb.append("    amountAfterTax: ").append(toIndentedString(amountAfterTax)).append("\n");
    sb.append("    currencyCode: ").append(toIndentedString(currencyCode)).append("\n");
    sb.append("    rateTimeUnit: ").append(toIndentedString(rateTimeUnit)).append("\n");
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

