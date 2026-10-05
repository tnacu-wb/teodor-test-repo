package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RoomDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * RoomTypeDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:50:34.031950+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RoomTypeDto {

  private @Nullable Integer adults;

  private @Nullable Integer children;

  private @Nullable Boolean cotRequested;

  private @Nullable String roomType;

  @Valid
  private @Nullable List<@Valid RoomDto> rooms;

  public RoomTypeDto adults(Integer adults) {
    this.adults = adults;
    return this;
  }

  /**
   * Get adults
   * @return adults
   */
  
  @Schema(name = "adults", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("adults")
  public Integer getAdults() {
    return adults;
  }

  public void setAdults(Integer adults) {
    this.adults = adults;
  }

  public RoomTypeDto children(Integer children) {
    this.children = children;
    return this;
  }

  /**
   * Get children
   * @return children
   */
  
  @Schema(name = "children", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("children")
  public Integer getChildren() {
    return children;
  }

  public void setChildren(Integer children) {
    this.children = children;
  }

  public RoomTypeDto cotRequested(Boolean cotRequested) {
    this.cotRequested = cotRequested;
    return this;
  }

  /**
   * Get cotRequested
   * @return cotRequested
   */
  
  @Schema(name = "cotRequested", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cotRequested")
  public Boolean getCotRequested() {
    return cotRequested;
  }

  public void setCotRequested(Boolean cotRequested) {
    this.cotRequested = cotRequested;
  }

  public RoomTypeDto roomType(String roomType) {
    this.roomType = roomType;
    return this;
  }

  /**
   * Get roomType
   * @return roomType
   */
  
  @Schema(name = "roomType", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomType")
  public String getRoomType() {
    return roomType;
  }

  public void setRoomType(String roomType) {
    this.roomType = roomType;
  }

  public RoomTypeDto rooms(List<@Valid RoomDto> rooms) {
    this.rooms = rooms;
    return this;
  }

  public RoomTypeDto addRoomsItem(RoomDto roomsItem) {
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
  public List<@Valid RoomDto> getRooms() {
    return rooms;
  }

  public void setRooms(List<@Valid RoomDto> rooms) {
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
    RoomTypeDto roomTypeDto = (RoomTypeDto) o;
    return Objects.equals(this.adults, roomTypeDto.adults) &&
        Objects.equals(this.children, roomTypeDto.children) &&
        Objects.equals(this.cotRequested, roomTypeDto.cotRequested) &&
        Objects.equals(this.roomType, roomTypeDto.roomType) &&
        Objects.equals(this.rooms, roomTypeDto.rooms);
  }

  @Override
  public int hashCode() {
    return Objects.hash(adults, children, cotRequested, roomType, rooms);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RoomTypeDto {\n");
    sb.append("    adults: ").append(toIndentedString(adults)).append("\n");
    sb.append("    children: ").append(toIndentedString(children)).append("\n");
    sb.append("    cotRequested: ").append(toIndentedString(cotRequested)).append("\n");
    sb.append("    roomType: ").append(toIndentedString(roomType)).append("\n");
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

