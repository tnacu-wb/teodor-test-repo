package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
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

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:15.274731+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PriceInfoDto {

  private @Nullable BigDecimal amountAfterTax;

  private @Nullable BigDecimal amountBeforeTax;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private @Nullable LocalDate stayDate;

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

  public PriceInfoDto stayDate(LocalDate stayDate) {
    this.stayDate = stayDate;
    return this;
  }

  /**
   * Get stayDate
   * @return stayDate
   */
  @Valid 
  @Schema(name = "stayDate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("stayDate")
  public LocalDate getStayDate() {
    return stayDate;
  }

  public void setStayDate(LocalDate stayDate) {
    this.stayDate = stayDate;
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
        Objects.equals(this.stayDate, priceInfoDto.stayDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(amountAfterTax, amountBeforeTax, stayDate);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PriceInfoDto {\n");
    sb.append("    amountAfterTax: ").append(toIndentedString(amountAfterTax)).append("\n");
    sb.append("    amountBeforeTax: ").append(toIndentedString(amountBeforeTax)).append("\n");
    sb.append("    stayDate: ").append(toIndentedString(stayDate)).append("\n");
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

