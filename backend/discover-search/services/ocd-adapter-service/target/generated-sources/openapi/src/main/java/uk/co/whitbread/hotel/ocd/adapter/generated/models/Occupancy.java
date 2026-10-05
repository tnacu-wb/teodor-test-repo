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
 * Details on the occupancy of the room type.
 */

@Schema(name = "Occupancy", description = "Details on the occupancy of the room type.")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:32.554166+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class Occupancy {

  private @Nullable Integer minOccupancy;

  private @Nullable Integer maxOccupancy;

  private @Nullable Integer maxAdultOccupancy;

  private @Nullable Integer maxChildOccupancy;

  public Occupancy minOccupancy(Integer minOccupancy) {
    this.minOccupancy = minOccupancy;
    return this;
  }

  /**
   * The minimum number of individuals allowed in the room type.
   * minimum: 0
   * @return minOccupancy
   */
  @Min(0) 
  @Schema(name = "minOccupancy", example = "2", description = "The minimum number of individuals allowed in the room type.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("minOccupancy")
  public Integer getMinOccupancy() {
    return minOccupancy;
  }

  public void setMinOccupancy(Integer minOccupancy) {
    this.minOccupancy = minOccupancy;
  }

  public Occupancy maxOccupancy(Integer maxOccupancy) {
    this.maxOccupancy = maxOccupancy;
    return this;
  }

  /**
   * The maximum number of individuals allowed in the room type.
   * minimum: 0
   * @return maxOccupancy
   */
  @Min(0) 
  @Schema(name = "maxOccupancy", example = "8", description = "The maximum number of individuals allowed in the room type.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("maxOccupancy")
  public Integer getMaxOccupancy() {
    return maxOccupancy;
  }

  public void setMaxOccupancy(Integer maxOccupancy) {
    this.maxOccupancy = maxOccupancy;
  }

  public Occupancy maxAdultOccupancy(Integer maxAdultOccupancy) {
    this.maxAdultOccupancy = maxAdultOccupancy;
    return this;
  }

  /**
   * The maximum number of adults allowed in the room type.
   * minimum: 0
   * @return maxAdultOccupancy
   */
  @Min(0) 
  @Schema(name = "maxAdultOccupancy", example = "4", description = "The maximum number of adults allowed in the room type.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("maxAdultOccupancy")
  public Integer getMaxAdultOccupancy() {
    return maxAdultOccupancy;
  }

  public void setMaxAdultOccupancy(Integer maxAdultOccupancy) {
    this.maxAdultOccupancy = maxAdultOccupancy;
  }

  public Occupancy maxChildOccupancy(Integer maxChildOccupancy) {
    this.maxChildOccupancy = maxChildOccupancy;
    return this;
  }

  /**
   * The maximum number of children allowed in the room type.
   * minimum: 0
   * @return maxChildOccupancy
   */
  @Min(0) 
  @Schema(name = "maxChildOccupancy", example = "3", description = "The maximum number of children allowed in the room type.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("maxChildOccupancy")
  public Integer getMaxChildOccupancy() {
    return maxChildOccupancy;
  }

  public void setMaxChildOccupancy(Integer maxChildOccupancy) {
    this.maxChildOccupancy = maxChildOccupancy;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Occupancy occupancy = (Occupancy) o;
    return Objects.equals(this.minOccupancy, occupancy.minOccupancy) &&
        Objects.equals(this.maxOccupancy, occupancy.maxOccupancy) &&
        Objects.equals(this.maxAdultOccupancy, occupancy.maxAdultOccupancy) &&
        Objects.equals(this.maxChildOccupancy, occupancy.maxChildOccupancy);
  }

  @Override
  public int hashCode() {
    return Objects.hash(minOccupancy, maxOccupancy, maxAdultOccupancy, maxChildOccupancy);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Occupancy {\n");
    sb.append("    minOccupancy: ").append(toIndentedString(minOccupancy)).append("\n");
    sb.append("    maxOccupancy: ").append(toIndentedString(maxOccupancy)).append("\n");
    sb.append("    maxAdultOccupancy: ").append(toIndentedString(maxAdultOccupancy)).append("\n");
    sb.append("    maxChildOccupancy: ").append(toIndentedString(maxChildOccupancy)).append("\n");
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

