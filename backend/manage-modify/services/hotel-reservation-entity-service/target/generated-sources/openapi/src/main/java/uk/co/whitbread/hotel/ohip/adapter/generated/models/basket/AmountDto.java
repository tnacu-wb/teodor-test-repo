package uk.co.whitbread.hotel.ohip.adapter.generated.models.basket;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.math.BigDecimal;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * AmountDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:25.444555+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class AmountDto {

  private @Nullable String currency;

  private @Nullable BigDecimal minorUnits;

  public AmountDto currency(String currency) {
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

  public AmountDto minorUnits(BigDecimal minorUnits) {
    this.minorUnits = minorUnits;
    return this;
  }

  /**
   * Get minorUnits
   * @return minorUnits
   */
  @Valid 
  @Schema(name = "minorUnits", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("minorUnits")
  public BigDecimal getMinorUnits() {
    return minorUnits;
  }

  public void setMinorUnits(BigDecimal minorUnits) {
    this.minorUnits = minorUnits;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AmountDto amountDto = (AmountDto) o;
    return Objects.equals(this.currency, amountDto.currency) &&
        Objects.equals(this.minorUnits, amountDto.minorUnits);
  }

  @Override
  public int hashCode() {
    return Objects.hash(currency, minorUnits);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AmountDto {\n");
    sb.append("    currency: ").append(toIndentedString(currency)).append("\n");
    sb.append("    minorUnits: ").append(toIndentedString(minorUnits)).append("\n");
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

