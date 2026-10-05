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
 * HousekeepingRoomStatus
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:15.274731+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class HousekeepingRoomStatus {

  private @Nullable String housekeepingRoomStatus;

  public HousekeepingRoomStatus housekeepingRoomStatus(String housekeepingRoomStatus) {
    this.housekeepingRoomStatus = housekeepingRoomStatus;
    return this;
  }

  /**
   * Get housekeepingRoomStatus
   * @return housekeepingRoomStatus
   */
  
  @Schema(name = "housekeepingRoomStatus", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("housekeepingRoomStatus")
  public String getHousekeepingRoomStatus() {
    return housekeepingRoomStatus;
  }

  public void setHousekeepingRoomStatus(String housekeepingRoomStatus) {
    this.housekeepingRoomStatus = housekeepingRoomStatus;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    HousekeepingRoomStatus housekeepingRoomStatus = (HousekeepingRoomStatus) o;
    return Objects.equals(this.housekeepingRoomStatus, housekeepingRoomStatus.housekeepingRoomStatus);
  }

  @Override
  public int hashCode() {
    return Objects.hash(housekeepingRoomStatus);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class HousekeepingRoomStatus {\n");
    sb.append("    housekeepingRoomStatus: ").append(toIndentedString(housekeepingRoomStatus)).append("\n");
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

