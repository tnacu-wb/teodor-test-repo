package uk.co.whitbread.ohip.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * RoomTypeInfoDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:50:12.143180+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RoomTypeInfoDto {

  private @Nullable Boolean accessible;

  private @Nullable String numberOfRooms;

  private @Nullable String roomClass;

  private @Nullable String roomType;

  public RoomTypeInfoDto accessible(Boolean accessible) {
    this.accessible = accessible;
    return this;
  }

  /**
   * Get accessible
   * @return accessible
   */
  
  @Schema(name = "accessible", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("accessible")
  public Boolean getAccessible() {
    return accessible;
  }

  public void setAccessible(Boolean accessible) {
    this.accessible = accessible;
  }

  public RoomTypeInfoDto numberOfRooms(String numberOfRooms) {
    this.numberOfRooms = numberOfRooms;
    return this;
  }

  /**
   * Get numberOfRooms
   * @return numberOfRooms
   */
  
  @Schema(name = "numberOfRooms", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("numberOfRooms")
  public String getNumberOfRooms() {
    return numberOfRooms;
  }

  public void setNumberOfRooms(String numberOfRooms) {
    this.numberOfRooms = numberOfRooms;
  }

  public RoomTypeInfoDto roomClass(String roomClass) {
    this.roomClass = roomClass;
    return this;
  }

  /**
   * Get roomClass
   * @return roomClass
   */
  
  @Schema(name = "roomClass", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomClass")
  public String getRoomClass() {
    return roomClass;
  }

  public void setRoomClass(String roomClass) {
    this.roomClass = roomClass;
  }

  public RoomTypeInfoDto roomType(String roomType) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RoomTypeInfoDto roomTypeInfoDto = (RoomTypeInfoDto) o;
    return Objects.equals(this.accessible, roomTypeInfoDto.accessible) &&
        Objects.equals(this.numberOfRooms, roomTypeInfoDto.numberOfRooms) &&
        Objects.equals(this.roomClass, roomTypeInfoDto.roomClass) &&
        Objects.equals(this.roomType, roomTypeInfoDto.roomType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(accessible, numberOfRooms, roomClass, roomType);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RoomTypeInfoDto {\n");
    sb.append("    accessible: ").append(toIndentedString(accessible)).append("\n");
    sb.append("    numberOfRooms: ").append(toIndentedString(numberOfRooms)).append("\n");
    sb.append("    roomClass: ").append(toIndentedString(roomClass)).append("\n");
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

