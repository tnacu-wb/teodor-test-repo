package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HousekeepingRoomStatus;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RoomCondition;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RoomStatusResponse;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Housekeeping
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:50:34.031950+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class Housekeeping {

  private @Nullable HousekeepingRoomStatus housekeepingRoomStatus;

  private @Nullable RoomCondition roomCondition;

  private @Nullable RoomStatusResponse roomStatus;

  public Housekeeping housekeepingRoomStatus(HousekeepingRoomStatus housekeepingRoomStatus) {
    this.housekeepingRoomStatus = housekeepingRoomStatus;
    return this;
  }

  /**
   * Get housekeepingRoomStatus
   * @return housekeepingRoomStatus
   */
  @Valid 
  @Schema(name = "housekeepingRoomStatus", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("housekeepingRoomStatus")
  public HousekeepingRoomStatus getHousekeepingRoomStatus() {
    return housekeepingRoomStatus;
  }

  public void setHousekeepingRoomStatus(HousekeepingRoomStatus housekeepingRoomStatus) {
    this.housekeepingRoomStatus = housekeepingRoomStatus;
  }

  public Housekeeping roomCondition(RoomCondition roomCondition) {
    this.roomCondition = roomCondition;
    return this;
  }

  /**
   * Get roomCondition
   * @return roomCondition
   */
  @Valid 
  @Schema(name = "roomCondition", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomCondition")
  public RoomCondition getRoomCondition() {
    return roomCondition;
  }

  public void setRoomCondition(RoomCondition roomCondition) {
    this.roomCondition = roomCondition;
  }

  public Housekeeping roomStatus(RoomStatusResponse roomStatus) {
    this.roomStatus = roomStatus;
    return this;
  }

  /**
   * Get roomStatus
   * @return roomStatus
   */
  @Valid 
  @Schema(name = "roomStatus", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomStatus")
  public RoomStatusResponse getRoomStatus() {
    return roomStatus;
  }

  public void setRoomStatus(RoomStatusResponse roomStatus) {
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
    Housekeeping housekeeping = (Housekeeping) o;
    return Objects.equals(this.housekeepingRoomStatus, housekeeping.housekeepingRoomStatus) &&
        Objects.equals(this.roomCondition, housekeeping.roomCondition) &&
        Objects.equals(this.roomStatus, housekeeping.roomStatus);
  }

  @Override
  public int hashCode() {
    return Objects.hash(housekeepingRoomStatus, roomCondition, roomStatus);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Housekeeping {\n");
    sb.append("    housekeepingRoomStatus: ").append(toIndentedString(housekeepingRoomStatus)).append("\n");
    sb.append("    roomCondition: ").append(toIndentedString(roomCondition)).append("\n");
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

