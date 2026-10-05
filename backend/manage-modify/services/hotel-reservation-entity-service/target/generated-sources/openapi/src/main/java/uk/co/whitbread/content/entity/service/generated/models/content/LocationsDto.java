package uk.co.whitbread.content.entity.service.generated.models.content;

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
 * LocationsDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:28.563902+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class LocationsDto {

  private @Nullable String locationId;

  private @Nullable String locationName;

  private @Nullable Integer locationOrder;

  public LocationsDto locationId(String locationId) {
    this.locationId = locationId;
    return this;
  }

  /**
   * Get locationId
   * @return locationId
   */
  
  @Schema(name = "locationId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("locationId")
  public String getLocationId() {
    return locationId;
  }

  public void setLocationId(String locationId) {
    this.locationId = locationId;
  }

  public LocationsDto locationName(String locationName) {
    this.locationName = locationName;
    return this;
  }

  /**
   * Get locationName
   * @return locationName
   */
  
  @Schema(name = "locationName", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("locationName")
  public String getLocationName() {
    return locationName;
  }

  public void setLocationName(String locationName) {
    this.locationName = locationName;
  }

  public LocationsDto locationOrder(Integer locationOrder) {
    this.locationOrder = locationOrder;
    return this;
  }

  /**
   * Get locationOrder
   * @return locationOrder
   */
  
  @Schema(name = "locationOrder", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("locationOrder")
  public Integer getLocationOrder() {
    return locationOrder;
  }

  public void setLocationOrder(Integer locationOrder) {
    this.locationOrder = locationOrder;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LocationsDto locationsDto = (LocationsDto) o;
    return Objects.equals(this.locationId, locationsDto.locationId) &&
        Objects.equals(this.locationName, locationsDto.locationName) &&
        Objects.equals(this.locationOrder, locationsDto.locationOrder);
  }

  @Override
  public int hashCode() {
    return Objects.hash(locationId, locationName, locationOrder);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LocationsDto {\n");
    sb.append("    locationId: ").append(toIndentedString(locationId)).append("\n");
    sb.append("    locationName: ").append(toIndentedString(locationName)).append("\n");
    sb.append("    locationOrder: ").append(toIndentedString(locationOrder)).append("\n");
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

