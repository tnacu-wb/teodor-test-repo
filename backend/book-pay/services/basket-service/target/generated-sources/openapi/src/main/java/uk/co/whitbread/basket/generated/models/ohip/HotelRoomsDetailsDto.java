package uk.co.whitbread.basket.generated.models.ohip;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.ohip.KioskRoomDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * HotelRoomsDetailsDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:10.951524+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class HotelRoomsDetailsDto {

  private @Nullable String hotelId;

  @Valid
  private List<@Valid KioskRoomDto> room = new ArrayList<>();

  public HotelRoomsDetailsDto hotelId(String hotelId) {
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

  public HotelRoomsDetailsDto room(List<@Valid KioskRoomDto> room) {
    this.room = room;
    return this;
  }

  public HotelRoomsDetailsDto addRoomItem(KioskRoomDto roomItem) {
    if (this.room == null) {
      this.room = new ArrayList<>();
    }
    this.room.add(roomItem);
    return this;
  }

  /**
   * Get room
   * @return room
   */
  @Valid 
  @Schema(name = "room", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("room")
  public List<@Valid KioskRoomDto> getRoom() {
    return room;
  }

  public void setRoom(List<@Valid KioskRoomDto> room) {
    this.room = room;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    HotelRoomsDetailsDto hotelRoomsDetailsDto = (HotelRoomsDetailsDto) o;
    return Objects.equals(this.hotelId, hotelRoomsDetailsDto.hotelId) &&
        Objects.equals(this.room, hotelRoomsDetailsDto.room);
  }

  @Override
  public int hashCode() {
    return Objects.hash(hotelId, room);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class HotelRoomsDetailsDto {\n");
    sb.append("    hotelId: ").append(toIndentedString(hotelId)).append("\n");
    sb.append("    room: ").append(toIndentedString(room)).append("\n");
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

