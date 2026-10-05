package uk.co.whitbread.ohip.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.ohip.generated.models.RoomTypeInfoDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * RoomTypesInfoDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:50:12.143180+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RoomTypesInfoDto {

  private @Nullable String hotelId;

  @Valid
  private List<@Valid RoomTypeInfoDto> roomType = new ArrayList<>();

  public RoomTypesInfoDto hotelId(String hotelId) {
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

  public RoomTypesInfoDto roomType(List<@Valid RoomTypeInfoDto> roomType) {
    this.roomType = roomType;
    return this;
  }

  public RoomTypesInfoDto addRoomTypeItem(RoomTypeInfoDto roomTypeItem) {
    if (this.roomType == null) {
      this.roomType = new ArrayList<>();
    }
    this.roomType.add(roomTypeItem);
    return this;
  }

  /**
   * Get roomType
   * @return roomType
   */
  @Valid 
  @Schema(name = "roomType", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomType")
  public List<@Valid RoomTypeInfoDto> getRoomType() {
    return roomType;
  }

  public void setRoomType(List<@Valid RoomTypeInfoDto> roomType) {
    this.roomType = roomType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RoomTypesInfoDto roomTypesInfoDto = (RoomTypesInfoDto) o;
    return Objects.equals(this.hotelId, roomTypesInfoDto.hotelId) &&
        Objects.equals(this.roomType, roomTypesInfoDto.roomType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(hotelId, roomType);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RoomTypesInfoDto {\n");
    sb.append("    hotelId: ").append(toIndentedString(hotelId)).append("\n");
    sb.append("    roomType: ").append(toIndentedString(roomType)).append("\n");
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

