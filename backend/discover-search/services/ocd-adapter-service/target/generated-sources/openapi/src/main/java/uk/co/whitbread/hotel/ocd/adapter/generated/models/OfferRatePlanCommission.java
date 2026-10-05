package uk.co.whitbread.hotel.ocd.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * Information on commission rate associated to the rate plan.
 */

@Schema(name = "OfferRatePlanCommission", description = "Information on commission rate associated to the rate plan.")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:32.554166+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class OfferRatePlanCommission {

  private @Nullable Integer percentage;

  public OfferRatePlanCommission percentage(Integer percentage) {
    this.percentage = percentage;
    return this;
  }

  /**
   * Commission rate in percentage associated to the rate plan.
   * @return percentage
   */
  
  @Schema(name = "percentage", example = "10", description = "Commission rate in percentage associated to the rate plan.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("percentage")
  public Integer getPercentage() {
    return percentage;
  }

  public void setPercentage(Integer percentage) {
    this.percentage = percentage;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OfferRatePlanCommission offerRatePlanCommission = (OfferRatePlanCommission) o;
    return Objects.equals(this.percentage, offerRatePlanCommission.percentage);
  }

  @Override
  public int hashCode() {
    return Objects.hash(percentage);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OfferRatePlanCommission {\n");
    sb.append("    percentage: ").append(toIndentedString(percentage)).append("\n");
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

