package uk.co.whitbread.hotel.ocd.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.CompassDirection;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.DistanceUnit;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * AlternatePropertyDistance
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:32.554166+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class AlternatePropertyDistance {

  private @Nullable Double distance;

  private @Nullable DistanceUnit distanceUnit;

  private @Nullable CompassDirection compassDirection;

  private @Nullable String comments;

  public AlternatePropertyDistance distance(Double distance) {
    this.distance = distance;
    return this;
  }

  /**
   * Numeric value of the distance from the property to the alternate property.
   * minimum: 0.01
   * @return distance
   */
  @DecimalMin("0.01") 
  @Schema(name = "distance", example = "12.0", description = "Numeric value of the distance from the property to the alternate property.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("distance")
  public Double getDistance() {
    return distance;
  }

  public void setDistance(Double distance) {
    this.distance = distance;
  }

  public AlternatePropertyDistance distanceUnit(DistanceUnit distanceUnit) {
    this.distanceUnit = distanceUnit;
    return this;
  }

  /**
   * Get distanceUnit
   * @return distanceUnit
   */
  @Valid 
  @Schema(name = "distanceUnit", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("distanceUnit")
  public DistanceUnit getDistanceUnit() {
    return distanceUnit;
  }

  public void setDistanceUnit(DistanceUnit distanceUnit) {
    this.distanceUnit = distanceUnit;
  }

  public AlternatePropertyDistance compassDirection(CompassDirection compassDirection) {
    this.compassDirection = compassDirection;
    return this;
  }

  /**
   * Get compassDirection
   * @return compassDirection
   */
  @Valid 
  @Schema(name = "compassDirection", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("compassDirection")
  public CompassDirection getCompassDirection() {
    return compassDirection;
  }

  public void setCompassDirection(CompassDirection compassDirection) {
    this.compassDirection = compassDirection;
  }

  public AlternatePropertyDistance comments(String comments) {
    this.comments = comments;
    return this;
  }

  /**
   * Additional text that describes the alternate property.
   * @return comments
   */
  
  @Schema(name = "comments", example = "Resort3 is the next pereferred hotel after Resort1.", description = "Additional text that describes the alternate property.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("comments")
  public String getComments() {
    return comments;
  }

  public void setComments(String comments) {
    this.comments = comments;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AlternatePropertyDistance alternatePropertyDistance = (AlternatePropertyDistance) o;
    return Objects.equals(this.distance, alternatePropertyDistance.distance) &&
        Objects.equals(this.distanceUnit, alternatePropertyDistance.distanceUnit) &&
        Objects.equals(this.compassDirection, alternatePropertyDistance.compassDirection) &&
        Objects.equals(this.comments, alternatePropertyDistance.comments);
  }

  @Override
  public int hashCode() {
    return Objects.hash(distance, distanceUnit, compassDirection, comments);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AlternatePropertyDistance {\n");
    sb.append("    distance: ").append(toIndentedString(distance)).append("\n");
    sb.append("    distanceUnit: ").append(toIndentedString(distanceUnit)).append("\n");
    sb.append("    compassDirection: ").append(toIndentedString(compassDirection)).append("\n");
    sb.append("    comments: ").append(toIndentedString(comments)).append("\n");
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

