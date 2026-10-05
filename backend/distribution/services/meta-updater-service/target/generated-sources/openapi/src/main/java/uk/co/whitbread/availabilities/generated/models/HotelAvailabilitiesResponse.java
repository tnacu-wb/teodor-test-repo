package uk.co.whitbread.availabilities.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.availabilities.generated.models.Room;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * HotelAvailabilitiesResponse
 */
@lombok.Builder @lombok.AllArgsConstructor

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:31.761066+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class HotelAvailabilitiesResponse {

  private @Nullable String hotelCode;

  @lombok.Builder.Default
  @Valid
  private List<@Valid Room> rooms = new ArrayList<>();

  public HotelAvailabilitiesResponse hotelCode(String hotelCode) {
    this.hotelCode = hotelCode;
    return this;
  }

  /**
   * Get hotelCode
   * @return hotelCode
   */
  
  @Schema(name = "hotelCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hotelCode")
  public String getHotelCode() {
    return hotelCode;
  }

  public void setHotelCode(String hotelCode) {
    this.hotelCode = hotelCode;
  }

  public HotelAvailabilitiesResponse rooms(List<@Valid Room> rooms) {
    this.rooms = rooms;
    return this;
  }

  public HotelAvailabilitiesResponse addRoomsItem(Room roomsItem) {
    if (this.rooms == null) {
      this.rooms = new ArrayList<>();
    }
    this.rooms.add(roomsItem);
    return this;
  }

  /**
   * Get rooms
   * @return rooms
   */
  @Valid 
  @Schema(name = "rooms", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("rooms")
  public List<@Valid Room> getRooms() {
    return rooms;
  }

  public void setRooms(List<@Valid Room> rooms) {
    this.rooms = rooms;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    HotelAvailabilitiesResponse hotelAvailabilitiesResponse = (HotelAvailabilitiesResponse) o;
    return Objects.equals(this.hotelCode, hotelAvailabilitiesResponse.hotelCode) &&
        Objects.equals(this.rooms, hotelAvailabilitiesResponse.rooms);
  }

  @Override
  public int hashCode() {
    return Objects.hash(hotelCode, rooms);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class HotelAvailabilitiesResponse {\n");
    sb.append("    hotelCode: ").append(toIndentedString(hotelCode)).append("\n");
    sb.append("    rooms: ").append(toIndentedString(rooms)).append("\n");
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

