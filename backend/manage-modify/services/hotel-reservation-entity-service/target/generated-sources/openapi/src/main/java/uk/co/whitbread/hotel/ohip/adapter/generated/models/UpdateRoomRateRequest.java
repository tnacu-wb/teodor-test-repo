package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RoomOccupancy;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * UpdateRoomRateRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:15.274731+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class UpdateRoomRateRequest {

  private @Nullable String endDate;

  private @Nullable String ratePlanCode;

  private @Nullable RoomOccupancy roomOccupancy;

  private @Nullable String roomType;

  private @Nullable String startDate;

  public UpdateRoomRateRequest endDate(String endDate) {
    this.endDate = endDate;
    return this;
  }

  /**
   * Get endDate
   * @return endDate
   */
  
  @Schema(name = "endDate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("endDate")
  public String getEndDate() {
    return endDate;
  }

  public void setEndDate(String endDate) {
    this.endDate = endDate;
  }

  public UpdateRoomRateRequest ratePlanCode(String ratePlanCode) {
    this.ratePlanCode = ratePlanCode;
    return this;
  }

  /**
   * Get ratePlanCode
   * @return ratePlanCode
   */
  
  @Schema(name = "ratePlanCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("ratePlanCode")
  public String getRatePlanCode() {
    return ratePlanCode;
  }

  public void setRatePlanCode(String ratePlanCode) {
    this.ratePlanCode = ratePlanCode;
  }

  public UpdateRoomRateRequest roomOccupancy(RoomOccupancy roomOccupancy) {
    this.roomOccupancy = roomOccupancy;
    return this;
  }

  /**
   * Get roomOccupancy
   * @return roomOccupancy
   */
  @Valid 
  @Schema(name = "roomOccupancy", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomOccupancy")
  public RoomOccupancy getRoomOccupancy() {
    return roomOccupancy;
  }

  public void setRoomOccupancy(RoomOccupancy roomOccupancy) {
    this.roomOccupancy = roomOccupancy;
  }

  public UpdateRoomRateRequest roomType(String roomType) {
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

  public UpdateRoomRateRequest startDate(String startDate) {
    this.startDate = startDate;
    return this;
  }

  /**
   * Get startDate
   * @return startDate
   */
  
  @Schema(name = "startDate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("startDate")
  public String getStartDate() {
    return startDate;
  }

  public void setStartDate(String startDate) {
    this.startDate = startDate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UpdateRoomRateRequest updateRoomRateRequest = (UpdateRoomRateRequest) o;
    return Objects.equals(this.endDate, updateRoomRateRequest.endDate) &&
        Objects.equals(this.ratePlanCode, updateRoomRateRequest.ratePlanCode) &&
        Objects.equals(this.roomOccupancy, updateRoomRateRequest.roomOccupancy) &&
        Objects.equals(this.roomType, updateRoomRateRequest.roomType) &&
        Objects.equals(this.startDate, updateRoomRateRequest.startDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(endDate, ratePlanCode, roomOccupancy, roomType, startDate);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UpdateRoomRateRequest {\n");
    sb.append("    endDate: ").append(toIndentedString(endDate)).append("\n");
    sb.append("    ratePlanCode: ").append(toIndentedString(ratePlanCode)).append("\n");
    sb.append("    roomOccupancy: ").append(toIndentedString(roomOccupancy)).append("\n");
    sb.append("    roomType: ").append(toIndentedString(roomType)).append("\n");
    sb.append("    startDate: ").append(toIndentedString(startDate)).append("\n");
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

