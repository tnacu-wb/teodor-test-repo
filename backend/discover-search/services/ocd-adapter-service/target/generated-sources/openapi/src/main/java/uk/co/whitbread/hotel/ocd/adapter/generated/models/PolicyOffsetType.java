package uk.co.whitbread.hotel.ocd.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.OffsetTimeUnit;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * Deposit deadline, relative.
 */

@Schema(name = "PolicyOffsetType", description = "Deposit deadline, relative.")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:32.554166+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PolicyOffsetType {

  private @Nullable Integer offsetUnitMultiplier;

  private @Nullable OffsetTimeUnit offsetTimeUnit;

  public PolicyOffsetType offsetUnitMultiplier(Integer offsetUnitMultiplier) {
    this.offsetUnitMultiplier = offsetUnitMultiplier;
    return this;
  }

  /**
   * The number of days before the arrival date when the deposit is due.
   * @return offsetUnitMultiplier
   */
  
  @Schema(name = "offsetUnitMultiplier", example = "5", description = "The number of days before the arrival date when the deposit is due.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("offsetUnitMultiplier")
  public Integer getOffsetUnitMultiplier() {
    return offsetUnitMultiplier;
  }

  public void setOffsetUnitMultiplier(Integer offsetUnitMultiplier) {
    this.offsetUnitMultiplier = offsetUnitMultiplier;
  }

  public PolicyOffsetType offsetTimeUnit(OffsetTimeUnit offsetTimeUnit) {
    this.offsetTimeUnit = offsetTimeUnit;
    return this;
  }

  /**
   * Get offsetTimeUnit
   * @return offsetTimeUnit
   */
  @Valid 
  @Schema(name = "offsetTimeUnit", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("offsetTimeUnit")
  public OffsetTimeUnit getOffsetTimeUnit() {
    return offsetTimeUnit;
  }

  public void setOffsetTimeUnit(OffsetTimeUnit offsetTimeUnit) {
    this.offsetTimeUnit = offsetTimeUnit;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PolicyOffsetType policyOffsetType = (PolicyOffsetType) o;
    return Objects.equals(this.offsetUnitMultiplier, policyOffsetType.offsetUnitMultiplier) &&
        Objects.equals(this.offsetTimeUnit, policyOffsetType.offsetTimeUnit);
  }

  @Override
  public int hashCode() {
    return Objects.hash(offsetUnitMultiplier, offsetTimeUnit);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PolicyOffsetType {\n");
    sb.append("    offsetUnitMultiplier: ").append(toIndentedString(offsetUnitMultiplier)).append("\n");
    sb.append("    offsetTimeUnit: ").append(toIndentedString(offsetTimeUnit)).append("\n");
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

