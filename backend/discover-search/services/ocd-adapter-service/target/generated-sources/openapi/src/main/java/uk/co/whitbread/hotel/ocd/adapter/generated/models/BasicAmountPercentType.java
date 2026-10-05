package uk.co.whitbread.hotel.ocd.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.math.BigDecimal;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.PolicyBasisType;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * BasicAmountPercentType
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:32.554166+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class BasicAmountPercentType {

  private @Nullable Integer nights;

  private @Nullable Double percent;

  private @Nullable BigDecimal amount;

  private @Nullable PolicyBasisType basisType;

  public BasicAmountPercentType nights(Integer nights) {
    this.nights = nights;
    return this;
  }

  /**
   * The number of nights of the hotel stay used to calculate the cancellation amount when basis type is nights.
   * @return nights
   */
  
  @Schema(name = "nights", example = "2", description = "The number of nights of the hotel stay used to calculate the cancellation amount when basis type is nights.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("nights")
  public Integer getNights() {
    return nights;
  }

  public void setNights(Integer nights) {
    this.nights = nights;
  }

  public BasicAmountPercentType percent(Double percent) {
    this.percent = percent;
    return this;
  }

  /**
   * The percentage of the stay used to calculate the cancellation amount when basis type is Percentage or NightPercentage.
   * @return percent
   */
  
  @Schema(name = "percent", example = "50.0", description = "The percentage of the stay used to calculate the cancellation amount when basis type is Percentage or NightPercentage.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("percent")
  public Double getPercent() {
    return percent;
  }

  public void setPercent(Double percent) {
    this.percent = percent;
  }

  public BasicAmountPercentType amount(BigDecimal amount) {
    this.amount = amount;
    return this;
  }

  /**
   * The total amount of the cancellation penalty.
   * @return amount
   */
  @Valid 
  @Schema(name = "amount", example = "540.0", description = "The total amount of the cancellation penalty.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("amount")
  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public BasicAmountPercentType basisType(PolicyBasisType basisType) {
    this.basisType = basisType;
    return this;
  }

  /**
   * Get basisType
   * @return basisType
   */
  @Valid 
  @Schema(name = "basisType", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("basisType")
  public PolicyBasisType getBasisType() {
    return basisType;
  }

  public void setBasisType(PolicyBasisType basisType) {
    this.basisType = basisType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BasicAmountPercentType basicAmountPercentType = (BasicAmountPercentType) o;
    return Objects.equals(this.nights, basicAmountPercentType.nights) &&
        Objects.equals(this.percent, basicAmountPercentType.percent) &&
        Objects.equals(this.amount, basicAmountPercentType.amount) &&
        Objects.equals(this.basisType, basicAmountPercentType.basisType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(nights, percent, amount, basisType);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BasicAmountPercentType {\n");
    sb.append("    nights: ").append(toIndentedString(nights)).append("\n");
    sb.append("    percent: ").append(toIndentedString(percent)).append("\n");
    sb.append("    amount: ").append(toIndentedString(amount)).append("\n");
    sb.append("    basisType: ").append(toIndentedString(basisType)).append("\n");
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

