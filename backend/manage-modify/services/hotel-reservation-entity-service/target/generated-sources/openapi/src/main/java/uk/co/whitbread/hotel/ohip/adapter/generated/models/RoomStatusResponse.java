package uk.co.whitbread.hotel.ohip.adapter.generated.models;

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
 * RoomStatusResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:15.274731+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RoomStatusResponse {

  private @Nullable String frontOfficeStatus;

  private @Nullable String roomStatus;

  public RoomStatusResponse frontOfficeStatus(String frontOfficeStatus) {
    this.frontOfficeStatus = frontOfficeStatus;
    return this;
  }

  /**
   * Get frontOfficeStatus
   * @return frontOfficeStatus
   */
  
  @Schema(name = "frontOfficeStatus", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("frontOfficeStatus")
  public String getFrontOfficeStatus() {
    return frontOfficeStatus;
  }

  public void setFrontOfficeStatus(String frontOfficeStatus) {
    this.frontOfficeStatus = frontOfficeStatus;
  }

  public RoomStatusResponse roomStatus(String roomStatus) {
    this.roomStatus = roomStatus;
    return this;
  }

  /**
   * Get roomStatus
   * @return roomStatus
   */
  
  @Schema(name = "roomStatus", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomStatus")
  public String getRoomStatus() {
    return roomStatus;
  }

  public void setRoomStatus(String roomStatus) {
    this.roomStatus = roomStatus;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RoomStatusResponse roomStatusResponse = (RoomStatusResponse) o;
    return Objects.equals(this.frontOfficeStatus, roomStatusResponse.frontOfficeStatus) &&
        Objects.equals(this.roomStatus, roomStatusResponse.roomStatus);
  }

  @Override
  public int hashCode() {
    return Objects.hash(frontOfficeStatus, roomStatus);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RoomStatusResponse {\n");
    sb.append("    frontOfficeStatus: ").append(toIndentedString(frontOfficeStatus)).append("\n");
    sb.append("    roomStatus: ").append(toIndentedString(roomStatus)).append("\n");
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

