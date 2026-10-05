package uk.co.whitbread.ohip.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.ohip.generated.models.RoomStayV2Dto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * AvailabilityResultV2Dto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:50:12.143180+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class AvailabilityResultV2Dto {

  private @Nullable String hotelId;

  @Valid
  private List<@Valid RoomStayV2Dto> roomStays = new ArrayList<>();

  public AvailabilityResultV2Dto hotelId(String hotelId) {
    this.hotelId = hotelId;
    return this;
  }

  /**
   * Get hotelId
   * @return hotelId
   */
  
  @Schema(name = "hotelId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hotelId")
  public String getHotelId() {
    return hotelId;
  }

  public void setHotelId(String hotelId) {
    this.hotelId = hotelId;
  }

  public AvailabilityResultV2Dto roomStays(List<@Valid RoomStayV2Dto> roomStays) {
    this.roomStays = roomStays;
    return this;
  }

  public AvailabilityResultV2Dto addRoomStaysItem(RoomStayV2Dto roomStaysItem) {
    if (this.roomStays == null) {
      this.roomStays = new ArrayList<>();
    }
    this.roomStays.add(roomStaysItem);
    return this;
  }

  /**
   * Get roomStays
   * @return roomStays
   */
  @Valid 
  @Schema(name = "roomStays", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomStays")
  public List<@Valid RoomStayV2Dto> getRoomStays() {
    return roomStays;
  }

  public void setRoomStays(List<@Valid RoomStayV2Dto> roomStays) {
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
    AvailabilityResultV2Dto availabilityResultV2Dto = (AvailabilityResultV2Dto) o;
    return Objects.equals(this.hotelId, availabilityResultV2Dto.hotelId) &&
        Objects.equals(this.roomStays, availabilityResultV2Dto.roomStays);
  }

  @Override
  public int hashCode() {
    return Objects.hash(hotelId, roomStays);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AvailabilityResultV2Dto {\n");
    sb.append("    hotelId: ").append(toIndentedString(hotelId)).append("\n");
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

