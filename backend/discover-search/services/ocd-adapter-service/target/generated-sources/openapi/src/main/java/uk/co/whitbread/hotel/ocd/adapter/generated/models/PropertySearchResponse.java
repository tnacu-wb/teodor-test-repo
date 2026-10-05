package uk.co.whitbread.hotel.ocd.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.PropertySearchRoomStay;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * Property Search Response
 */

@Schema(name = "PropertySearchResponse", description = "Property Search Response")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:32.554166+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PropertySearchResponse {

  @Valid
  private List<@Valid PropertySearchRoomStay> roomStays = new ArrayList<>();

  public PropertySearchResponse roomStays(List<@Valid PropertySearchRoomStay> roomStays) {
    this.roomStays = roomStays;
    return this;
  }

  public PropertySearchResponse addRoomStaysItem(PropertySearchRoomStay roomStaysItem) {
    if (this.roomStays == null) {
      this.roomStays = new ArrayList<>();
    }
    this.roomStays.add(roomStaysItem);
    return this;
  }

  /**
   * List of properties including property information, availability status, and rate range for a given availability request.
   * @return roomStays
   */
  @Valid 
  @Schema(name = "roomStays", description = "List of properties including property information, availability status, and rate range for a given availability request.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomStays")
  public List<@Valid PropertySearchRoomStay> getRoomStays() {
    return roomStays;
  }

  public void setRoomStays(List<@Valid PropertySearchRoomStay> roomStays) {
    this.roomStays = roomStays;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PropertySearchResponse propertySearchResponse = (PropertySearchResponse) o;
    return Objects.equals(this.roomStays, propertySearchResponse.roomStays);
  }

  @Override
  public int hashCode() {
    return Objects.hash(roomStays);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PropertySearchResponse {\n");
    sb.append("    roomStays: ").append(toIndentedString(roomStays)).append("\n");
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

