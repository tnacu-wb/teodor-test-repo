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
 * CoordinatesDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:49:46.057591+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CoordinatesDto {

  private @Nullable Boolean hideHotelDistance;

  private @Nullable Double latitude;

  private @Nullable Double longitude;

  private @Nullable String radius;

  public CoordinatesDto hideHotelDistance(Boolean hideHotelDistance) {
    this.hideHotelDistance = hideHotelDistance;
    return this;
  }

  /**
   * Get hideHotelDistance
   * @return hideHotelDistance
   */
  
  @Schema(name = "hideHotelDistance", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hideHotelDistance")
  public Boolean getHideHotelDistance() {
    return hideHotelDistance;
  }

  public void setHideHotelDistance(Boolean hideHotelDistance) {
    this.hideHotelDistance = hideHotelDistance;
  }

  public CoordinatesDto latitude(Double latitude) {
    this.latitude = latitude;
    return this;
  }

  /**
   * Get latitude
   * @return latitude
   */
  
  @Schema(name = "latitude", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("latitude")
  public Double getLatitude() {
    return latitude;
  }

  public void setLatitude(Double latitude) {
    this.latitude = latitude;
  }

  public CoordinatesDto longitude(Double longitude) {
    this.longitude = longitude;
    return this;
  }

  /**
   * Get longitude
   * @return longitude
   */
  
  @Schema(name = "longitude", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("longitude")
  public Double getLongitude() {
    return longitude;
  }

  public void setLongitude(Double longitude) {
    this.longitude = longitude;
  }

  public CoordinatesDto radius(String radius) {
    this.radius = radius;
    return this;
  }

  /**
   * Get radius
   * @return radius
   */
  
  @Schema(name = "radius", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("radius")
  public String getRadius() {
    return radius;
  }

  public void setRadius(String radius) {
    this.radius = radius;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CoordinatesDto coordinatesDto = (CoordinatesDto) o;
    return Objects.equals(this.hideHotelDistance, coordinatesDto.hideHotelDistance) &&
        Objects.equals(this.latitude, coordinatesDto.latitude) &&
        Objects.equals(this.longitude, coordinatesDto.longitude) &&
        Objects.equals(this.radius, coordinatesDto.radius);
  }

  @Override
  public int hashCode() {
    return Objects.hash(hideHotelDistance, latitude, longitude, radius);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CoordinatesDto {\n");
    sb.append("    hideHotelDistance: ").append(toIndentedString(hideHotelDistance)).append("\n");
    sb.append("    latitude: ").append(toIndentedString(latitude)).append("\n");
    sb.append("    longitude: ").append(toIndentedString(longitude)).append("\n");
    sb.append("    radius: ").append(toIndentedString(radius)).append("\n");
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

