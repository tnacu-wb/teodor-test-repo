package uk.co.whitbread.ocd.adapter.service.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.math.BigDecimal;
import org.springframework.lang.Nullable;
import uk.co.whitbread.ocd.adapter.service.generated.models.TaxDetailsDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * PriceInfoDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:49:48.880965+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PriceInfoDto {

  private @Nullable BigDecimal amountAfterTax;

  private @Nullable BigDecimal amountBeforeTax;

  private @Nullable String currencyCode;

  private @Nullable TaxDetailsDto taxes;

  public PriceInfoDto amountAfterTax(BigDecimal amountAfterTax) {
    this.amountAfterTax = amountAfterTax;
    return this;
  }

  /**
   * Get amountAfterTax
   * @return amountAfterTax
   */
  @Valid 
  @Schema(name = "amountAfterTax", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("amountAfterTax")
  public BigDecimal getAmountAfterTax() {
    return amountAfterTax;
  }

  public void setAmountAfterTax(BigDecimal amountAfterTax) {
    this.amountAfterTax = amountAfterTax;
  }

  public PriceInfoDto amountBeforeTax(BigDecimal amountBeforeTax) {
    this.amountBeforeTax = amountBeforeTax;
    return this;
  }

  /**
   * Get amountBeforeTax
   * @return amountBeforeTax
   */
  @Valid 
  @Schema(name = "amountBeforeTax", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("amountBeforeTax")
  public BigDecimal getAmountBeforeTax() {
    return amountBeforeTax;
  }

  public void setAmountBeforeTax(BigDecimal amountBeforeTax) {
    this.amountBeforeTax = amountBeforeTax;
  }

  public PriceInfoDto currencyCode(String currencyCode) {
    this.currencyCode = currencyCode;
    return this;
  }

  /**
   * Get currencyCode
   * @return currencyCode
   */
  
  @Schema(name = "currencyCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("currencyCode")
  public String getCurrencyCode() {
    return currencyCode;
  }

  public void setCurrencyCode(String currencyCode) {
    this.currencyCode = currencyCode;
  }

  public PriceInfoDto taxes(TaxDetailsDto taxes) {
    this.taxes = taxes;
    return this;
  }

  /**
   * Get taxes
   * @return taxes
   */
  @Valid 
  @Schema(name = "taxes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("taxes")
  public TaxDetailsDto getTaxes() {
    return taxes;
  }

  public void setTaxes(TaxDetailsDto taxes) {
    this.taxes = taxes;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PriceInfoDto priceInfoDto = (PriceInfoDto) o;
    return Objects.equals(this.amountAfterTax, priceInfoDto.amountAfterTax) &&
        Objects.equals(this.amountBeforeTax, priceInfoDto.amountBeforeTax) &&
        Objects.equals(this.currencyCode, priceInfoDto.currencyCode) &&
        Objects.equals(this.taxes, priceInfoDto.taxes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(amountAfterTax, amountBeforeTax, currencyCode, taxes);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PriceInfoDto {\n");
    sb.append("    amountAfterTax: ").append(toIndentedString(amountAfterTax)).append("\n");
    sb.append("    amountBeforeTax: ").append(toIndentedString(amountBeforeTax)).append("\n");
    sb.append("    currencyCode: ").append(toIndentedString(currencyCode)).append("\n");
    sb.append("    taxes: ").append(toIndentedString(taxes)).append("\n");
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

