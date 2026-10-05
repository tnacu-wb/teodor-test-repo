package uk.co.whitbread.ocd.adapter.service.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.ocd.adapter.service.generated.models.TaxDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * TaxDetailsDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:52:36.710910+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class TaxDetailsDto {

  private @Nullable BigDecimal amount;

  private @Nullable String currencyCode;

  @Valid
  private List<@Valid TaxDto> tax = new ArrayList<>();

  public TaxDetailsDto amount(BigDecimal amount) {
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

  public TaxDetailsDto currencyCode(String currencyCode) {
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

  public TaxDetailsDto tax(List<@Valid TaxDto> tax) {
    this.tax = tax;
    return this;
  }

  public TaxDetailsDto addTaxItem(TaxDto taxItem) {
    if (this.tax == null) {
      this.tax = new ArrayList<>();
    }
    this.tax.add(taxItem);
    return this;
  }

  /**
   * Get tax
   * @return tax
   */
  @Valid 
  @Schema(name = "tax", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("tax")
  public List<@Valid TaxDto> getTax() {
    return tax;
  }

  public void setTax(List<@Valid TaxDto> tax) {
    this.tax = tax;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TaxDetailsDto taxDetailsDto = (TaxDetailsDto) o;
    return Objects.equals(this.amount, taxDetailsDto.amount) &&
        Objects.equals(this.currencyCode, taxDetailsDto.currencyCode) &&
        Objects.equals(this.tax, taxDetailsDto.tax);
  }

  @Override
  public int hashCode() {
    return Objects.hash(amount, currencyCode, tax);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TaxDetailsDto {\n");
    sb.append("    amount: ").append(toIndentedString(amount)).append("\n");
    sb.append("    currencyCode: ").append(toIndentedString(currencyCode)).append("\n");
    sb.append("    tax: ").append(toIndentedString(tax)).append("\n");
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

