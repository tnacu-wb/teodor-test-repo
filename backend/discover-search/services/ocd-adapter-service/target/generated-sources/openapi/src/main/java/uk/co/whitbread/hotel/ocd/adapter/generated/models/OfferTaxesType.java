package uk.co.whitbread.hotel.ocd.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.OfferTaxType;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * A collection of taxes.
 */

@Schema(name = "OfferTaxesType", description = "A collection of taxes.")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:32.554166+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class OfferTaxesType {

  @Valid
  private List<@Valid OfferTaxType> tax = new ArrayList<>();

  private @Nullable BigDecimal amount;

  private @Nullable String currencyCode;

  public OfferTaxesType tax(List<@Valid OfferTaxType> tax) {
    this.tax = tax;
    return this;
  }

  public OfferTaxesType addTaxItem(OfferTaxType taxItem) {
    if (this.tax == null) {
      this.tax = new ArrayList<>();
    }
    this.tax.add(taxItem);
    return this;
  }

  /**
   * An individual tax.
   * @return tax
   */
  @Valid @Size(max = 99) 
  @Schema(name = "tax", description = "An individual tax.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("tax")
  public List<@Valid OfferTaxType> getTax() {
    return tax;
  }

  public void setTax(List<@Valid OfferTaxType> tax) {
    this.tax = tax;
  }

  public OfferTaxesType amount(BigDecimal amount) {
    this.amount = amount;
    return this;
  }

  /**
   * The total tax amount.
   * @return amount
   */
  @Valid 
  @Schema(name = "amount", example = "25.0", description = "The total tax amount.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("amount")
  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public OfferTaxesType currencyCode(String currencyCode) {
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
    OfferTaxesType offerTaxesType = (OfferTaxesType) o;
    return Objects.equals(this.tax, offerTaxesType.tax) &&
        Objects.equals(this.amount, offerTaxesType.amount) &&
        Objects.equals(this.currencyCode, offerTaxesType.currencyCode);
  }

  @Override
  public int hashCode() {
    return Objects.hash(tax, amount, currencyCode);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OfferTaxesType {\n");
    sb.append("    tax: ").append(toIndentedString(tax)).append("\n");
    sb.append("    amount: ").append(toIndentedString(amount)).append("\n");
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

