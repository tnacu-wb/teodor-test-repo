package uk.co.whitbread.refund.processor.generated.models.payments;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Information on amount to process refund for.
 */

@Schema(name = "Amount", description = "Information on amount to process refund for.")
@JsonTypeName("Amount")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:12:20.597747+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class AmountDto {

  private String currency;

  private Integer minorUnits;

  public AmountDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public AmountDto(String currency, Integer minorUnits) {
    this.currency = currency;
    this.minorUnits = minorUnits;
  }

  public AmountDto currency(String currency) {
    this.currency = currency;
    return this;
  }

  /**
   * Currency of payment.
   * @return currency
   */
  @NotNull 
  @Schema(name = "currency", example = "GBP", description = "Currency of payment.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("currency")
  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public AmountDto minorUnits(Integer minorUnits) {
    this.minorUnits = minorUnits;
    return this;
  }

  /**
   * Minor units of payment..e.g 1 == £0.01 or €0.01
   * minimum: 1
   * @return minorUnits
   */
  @NotNull @Min(1) 
  @Schema(name = "minorUnits", example = "1", description = "Minor units of payment..e.g 1 == £0.01 or €0.01", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("minorUnits")
  public Integer getMinorUnits() {
    return minorUnits;
  }

  public void setMinorUnits(Integer minorUnits) {
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
    AmountDto amount = (AmountDto) o;
    return Objects.equals(this.currency, amount.currency) &&
        Objects.equals(this.minorUnits, amount.minorUnits);
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

