package uk.co.whitbread.basket.generated.models.ohip;

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
 * TotalDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:10.951524+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class TotalDto {

  private @Nullable BigDecimal amountBeforeTax;

  public TotalDto amountBeforeTax(BigDecimal amountBeforeTax) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TotalDto totalDto = (TotalDto) o;
    return Objects.equals(this.amountBeforeTax, totalDto.amountBeforeTax);
  }

  @Override
  public int hashCode() {
    return Objects.hash(amountBeforeTax);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TotalDto {\n");
    sb.append("    amountBeforeTax: ").append(toIndentedString(amountBeforeTax)).append("\n");
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

