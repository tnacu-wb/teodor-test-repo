package uk.co.whitbread.hotel.ocd.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.PropertyOffersRoomStay;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * PropertyOffersResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:32.554166+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PropertyOffersResponse {

  @Valid
  private List<@Valid PropertyOffersRoomStay> roomStays = new ArrayList<>();

  public PropertyOffersResponse roomStays(List<@Valid PropertyOffersRoomStay> roomStays) {
    this.roomStays = roomStays;
    return this;
  }

  public PropertyOffersResponse addRoomStaysItem(PropertyOffersRoomStay roomStaysItem) {
    if (this.roomStays == null) {
      this.roomStays = new ArrayList<>();
    }
    this.roomStays.add(roomStaysItem);
    return this;
  }

  /**
   * List of  available offers at the property including property information, availability status of the property, room types and rate plans information.
   * @return roomStays
   */
  @Valid 
  @Schema(name = "roomStays", description = "List of  available offers at the property including property information, availability status of the property, room types and rate plans information.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomStays")
  public List<@Valid PropertyOffersRoomStay> getRoomStays() {
    return roomStays;
  }

  public void setRoomStays(List<@Valid PropertyOffersRoomStay> roomStays) {
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
    PropertyOffersResponse propertyOffersResponse = (PropertyOffersResponse) o;
    return Objects.equals(this.roomStays, propertyOffersResponse.roomStays);
  }

  @Override
  public int hashCode() {
    return Objects.hash(roomStays);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PropertyOffersResponse {\n");
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

