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
 * Details on directions from and to the property.
 */

@Schema(name = "Direction", description = "Details on directions from and to the property.")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:32.554166+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class Direction {

  private @Nullable String propertyDirection;

  public Direction propertyDirection(String propertyDirection) {
    this.propertyDirection = propertyDirection;
    return this;
  }

  /**
   * The direction to the property.
   * @return propertyDirection
   */
  @Size(max = 1024) 
  @Schema(name = "propertyDirection", example = "SE ABC hills", description = "The direction to the property.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("propertyDirection")
  public String getPropertyDirection() {
    return propertyDirection;
  }

  public void setPropertyDirection(String propertyDirection) {
    this.propertyDirection = propertyDirection;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Direction direction = (Direction) o;
    return Objects.equals(this.propertyDirection, direction.propertyDirection);
  }

  @Override
  public int hashCode() {
    return Objects.hash(propertyDirection);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Direction {\n");
    sb.append("    propertyDirection: ").append(toIndentedString(propertyDirection)).append("\n");
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

