package uk.co.whitbread.hotel.ocd.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * Additional information related to the rate plan.
 */

@Schema(name = "AdditionalDetails", description = "Additional information related to the rate plan.")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:32.554166+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class AdditionalDetails {

  private @Nullable Boolean ratePlanMatches;

  /**
   * Parameter used to indicate the rate plan match.
   */
  public enum ValueEnum {
    RATE_PLAN_CODE("RatePlanCode"),
    
    ACCESS_CODE("AccessCode"),
    
    RATE_PLAN_TYPE("RatePlanType");

    private String value;

    ValueEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static ValueEnum fromValue(String value) {
      for (ValueEnum b : ValueEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private @Nullable ValueEnum value;

  public AdditionalDetails ratePlanMatches(Boolean ratePlanMatches) {
    this.ratePlanMatches = ratePlanMatches;
    return this;
  }

  /**
   * When true indicates there is a match between the rate plan and the requested parameters (ratePlanCode, accessCode, ratePlanType).
   * @return ratePlanMatches
   */
  
  @Schema(name = "ratePlanMatches", description = "When true indicates there is a match between the rate plan and the requested parameters (ratePlanCode, accessCode, ratePlanType).", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("ratePlanMatches")
  public Boolean getRatePlanMatches() {
    return ratePlanMatches;
  }

  public void setRatePlanMatches(Boolean ratePlanMatches) {
    this.ratePlanMatches = ratePlanMatches;
  }

  public AdditionalDetails value(ValueEnum value) {
    this.value = value;
    return this;
  }

  /**
   * Parameter used to indicate the rate plan match.
   * @return value
   */
  
  @Schema(name = "value", description = "Parameter used to indicate the rate plan match.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("value")
  public ValueEnum getValue() {
    return value;
  }

  public void setValue(ValueEnum value) {
    this.value = value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AdditionalDetails additionalDetails = (AdditionalDetails) o;
    return Objects.equals(this.ratePlanMatches, additionalDetails.ratePlanMatches) &&
        Objects.equals(this.value, additionalDetails.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(ratePlanMatches, value);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AdditionalDetails {\n");
    sb.append("    ratePlanMatches: ").append(toIndentedString(ratePlanMatches)).append("\n");
    sb.append("    value: ").append(toIndentedString(value)).append("\n");
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

