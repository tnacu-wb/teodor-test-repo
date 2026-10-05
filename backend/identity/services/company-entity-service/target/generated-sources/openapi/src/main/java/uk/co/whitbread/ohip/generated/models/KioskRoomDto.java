package uk.co.whitbread.ohip.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.ohip.generated.models.HousekeepingDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * KioskRoomDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:50:12.143180+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class KioskRoomDto {

  private @Nullable HousekeepingDto housekeeping;

  private @Nullable String roomId;

  public KioskRoomDto housekeeping(HousekeepingDto housekeeping) {
    this.housekeeping = housekeeping;
    return this;
  }

  /**
   * Get housekeeping
   * @return housekeeping
   */
  @Valid 
  @Schema(name = "housekeeping", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("housekeeping")
  public HousekeepingDto getHousekeeping() {
    return housekeeping;
  }

  public void setHousekeeping(HousekeepingDto housekeeping) {
    this.housekeeping = housekeeping;
  }

  public KioskRoomDto roomId(String roomId) {
    this.roomId = roomId;
    return this;
  }

  /**
   * Get roomId
   * @return roomId
   */
  
  @Schema(name = "roomId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomId")
  public String getRoomId() {
    return roomId;
  }

  public void setRoomId(String roomId) {
    this.roomId = roomId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    KioskRoomDto kioskRoomDto = (KioskRoomDto) o;
    return Objects.equals(this.housekeeping, kioskRoomDto.housekeeping) &&
        Objects.equals(this.roomId, kioskRoomDto.roomId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(housekeeping, roomId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class KioskRoomDto {\n");
    sb.append("    housekeeping: ").append(toIndentedString(housekeeping)).append("\n");
    sb.append("    roomId: ").append(toIndentedString(roomId)).append("\n");
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

