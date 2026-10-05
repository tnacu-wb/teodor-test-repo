package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * ConsumptionDetailsDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:50:34.031950+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ConsumptionDetailsDto {

  private @Nullable Boolean allowanceConsumed;

  private @Nullable Integer defaultQuantity;

  private @Nullable Integer totalQuantity;

  public ConsumptionDetailsDto allowanceConsumed(Boolean allowanceConsumed) {
    this.allowanceConsumed = allowanceConsumed;
    return this;
  }

  /**
   * Get allowanceConsumed
   * @return allowanceConsumed
   */
  
  @Schema(name = "allowanceConsumed", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("allowanceConsumed")
  public Boolean getAllowanceConsumed() {
    return allowanceConsumed;
  }

  public void setAllowanceConsumed(Boolean allowanceConsumed) {
    this.allowanceConsumed = allowanceConsumed;
  }

  public ConsumptionDetailsDto defaultQuantity(Integer defaultQuantity) {
    this.defaultQuantity = defaultQuantity;
    return this;
  }

  /**
   * Get defaultQuantity
   * @return defaultQuantity
   */
  
  @Schema(name = "defaultQuantity", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("defaultQuantity")
  public Integer getDefaultQuantity() {
    return defaultQuantity;
  }

  public void setDefaultQuantity(Integer defaultQuantity) {
    this.defaultQuantity = defaultQuantity;
  }

  public ConsumptionDetailsDto totalQuantity(Integer totalQuantity) {
    this.totalQuantity = totalQuantity;
    return this;
  }

  /**
   * Get totalQuantity
   * @return totalQuantity
   */
  
  @Schema(name = "totalQuantity", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("totalQuantity")
  public Integer getTotalQuantity() {
    return totalQuantity;
  }

  public void setTotalQuantity(Integer totalQuantity) {
    this.totalQuantity = totalQuantity;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ConsumptionDetailsDto consumptionDetailsDto = (ConsumptionDetailsDto) o;
    return Objects.equals(this.allowanceConsumed, consumptionDetailsDto.allowanceConsumed) &&
        Objects.equals(this.defaultQuantity, consumptionDetailsDto.defaultQuantity) &&
        Objects.equals(this.totalQuantity, consumptionDetailsDto.totalQuantity);
  }

  @Override
  public int hashCode() {
    return Objects.hash(allowanceConsumed, defaultQuantity, totalQuantity);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ConsumptionDetailsDto {\n");
    sb.append("    allowanceConsumed: ").append(toIndentedString(allowanceConsumed)).append("\n");
    sb.append("    defaultQuantity: ").append(toIndentedString(defaultQuantity)).append("\n");
    sb.append("    totalQuantity: ").append(toIndentedString(totalQuantity)).append("\n");
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

