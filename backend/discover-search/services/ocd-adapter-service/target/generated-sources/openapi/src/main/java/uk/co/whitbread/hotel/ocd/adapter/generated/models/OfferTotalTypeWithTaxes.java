package uk.co.whitbread.hotel.ocd.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.math.BigDecimal;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.OfferTaxesType;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * Information on the total cost for the entire stay, including taxes.
 */

@Schema(name = "OfferTotalTypeWithTaxes", description = "Information on the total cost for the entire stay, including taxes.")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:32.554166+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class OfferTotalTypeWithTaxes {

  private @Nullable BigDecimal amountBeforeTax;

  private @Nullable BigDecimal amountAfterTax;

  private @Nullable String currencyCode;

  private @Nullable OfferTaxesType taxes;

  public OfferTotalTypeWithTaxes amountBeforeTax(BigDecimal amountBeforeTax) {
    this.amountBeforeTax = amountBeforeTax;
    return this;
  }

  /**
   * The total amount not including any associated tax (e.g., sales tax, VAT, GST or any associated tax).
   * @return amountBeforeTax
   */
  @Valid 
  @Schema(name = "amountBeforeTax", example = "100.5", description = "The total amount not including any associated tax (e.g., sales tax, VAT, GST or any associated tax).", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("amountBeforeTax")
  public BigDecimal getAmountBeforeTax() {
    return amountBeforeTax;
  }

  public void setAmountBeforeTax(BigDecimal amountBeforeTax) {
    this.amountBeforeTax = amountBeforeTax;
  }

  public OfferTotalTypeWithTaxes amountAfterTax(BigDecimal amountAfterTax) {
    this.amountAfterTax = amountAfterTax;
    return this;
  }

  /**
   * The total amount including all associated taxes (e.g., sales tax, VAT, GST or any associated tax).
   * @return amountAfterTax
   */
  @Valid 
  @Schema(name = "amountAfterTax", example = "123.2", description = "The total amount including all associated taxes (e.g., sales tax, VAT, GST or any associated tax).", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("amountAfterTax")
  public BigDecimal getAmountAfterTax() {
    return amountAfterTax;
  }

  public void setAmountAfterTax(BigDecimal amountAfterTax) {
    this.amountAfterTax = amountAfterTax;
  }

  public OfferTotalTypeWithTaxes currencyCode(String currencyCode) {
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

  public OfferTotalTypeWithTaxes taxes(OfferTaxesType taxes) {
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
  public OfferTaxesType getTaxes() {
    return taxes;
  }

  public void setTaxes(OfferTaxesType taxes) {
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
    OfferTotalTypeWithTaxes offerTotalTypeWithTaxes = (OfferTotalTypeWithTaxes) o;
    return Objects.equals(this.amountBeforeTax, offerTotalTypeWithTaxes.amountBeforeTax) &&
        Objects.equals(this.amountAfterTax, offerTotalTypeWithTaxes.amountAfterTax) &&
        Objects.equals(this.currencyCode, offerTotalTypeWithTaxes.currencyCode) &&
        Objects.equals(this.taxes, offerTotalTypeWithTaxes.taxes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(amountBeforeTax, amountAfterTax, currencyCode, taxes);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OfferTotalTypeWithTaxes {\n");
    sb.append("    amountBeforeTax: ").append(toIndentedString(amountBeforeTax)).append("\n");
    sb.append("    amountAfterTax: ").append(toIndentedString(amountAfterTax)).append("\n");
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

