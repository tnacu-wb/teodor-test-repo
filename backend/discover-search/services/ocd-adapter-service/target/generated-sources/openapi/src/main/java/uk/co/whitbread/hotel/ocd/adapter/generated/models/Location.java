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
 * Details on the location of the property.
 */

@Schema(name = "Location", description = "Details on the location of the property.")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:32.554166+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class Location {

  private @Nullable String propertyLocation;

  public Location propertyLocation(String propertyLocation) {
    this.propertyLocation = propertyLocation;
    return this;
  }

  /**
   * Describes the location of the property.
   * @return propertyLocation
   */
  @Size(max = 1024) 
  @Schema(name = "propertyLocation", example = "ABC hills", description = "Describes the location of the property.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("propertyLocation")
  public String getPropertyLocation() {
    return propertyLocation;
  }

  public void setPropertyLocation(String propertyLocation) {
    this.propertyLocation = propertyLocation;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Location location = (Location) o;
    return Objects.equals(this.propertyLocation, location.propertyLocation);
  }

  @Override
  public int hashCode() {
    return Objects.hash(propertyLocation);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Location {\n");
    sb.append("    propertyLocation: ").append(toIndentedString(propertyLocation)).append("\n");
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

