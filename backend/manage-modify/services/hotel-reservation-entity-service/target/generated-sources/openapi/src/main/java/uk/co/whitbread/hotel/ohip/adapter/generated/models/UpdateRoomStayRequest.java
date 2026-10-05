package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RoomOccupancy;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UpdateRoomRateRequest;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * UpdateRoomStayRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:15.274731+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class UpdateRoomStayRequest {

  private @Nullable String arrivalDate;

  private @Nullable String departureDate;

  private @Nullable RoomOccupancy roomOccupancy;

  @Valid
  private List<@Valid UpdateRoomRateRequest> roomRates = new ArrayList<>();

  public UpdateRoomStayRequest arrivalDate(String arrivalDate) {
    this.arrivalDate = arrivalDate;
    return this;
  }

  /**
   * Get arrivalDate
   * @return arrivalDate
   */
  
  @Schema(name = "arrivalDate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("arrivalDate")
  public String getArrivalDate() {
    return arrivalDate;
  }

  public void setArrivalDate(String arrivalDate) {
    this.arrivalDate = arrivalDate;
  }

  public UpdateRoomStayRequest departureDate(String departureDate) {
    this.departureDate = departureDate;
    return this;
  }

  /**
   * Get departureDate
   * @return departureDate
   */
  
  @Schema(name = "departureDate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("departureDate")
  public String getDepartureDate() {
    return departureDate;
  }

  public void setDepartureDate(String departureDate) {
    this.departureDate = departureDate;
  }

  public UpdateRoomStayRequest roomOccupancy(RoomOccupancy roomOccupancy) {
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

  public UpdateRoomStayRequest roomRates(List<@Valid UpdateRoomRateRequest> roomRates) {
    this.roomRates = roomRates;
    return this;
  }

  public UpdateRoomStayRequest addRoomRatesItem(UpdateRoomRateRequest roomRatesItem) {
    if (this.roomRates == null) {
      this.roomRates = new ArrayList<>();
    }
    this.roomRates.add(roomRatesItem);
    return this;
  }

  /**
   * Get roomRates
   * @return roomRates
   */
  @Valid 
  @Schema(name = "roomRates", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomRates")
  public List<@Valid UpdateRoomRateRequest> getRoomRates() {
    return roomRates;
  }

  public void setRoomRates(List<@Valid UpdateRoomRateRequest> roomRates) {
    this.roomRates = roomRates;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UpdateRoomStayRequest updateRoomStayRequest = (UpdateRoomStayRequest) o;
    return Objects.equals(this.arrivalDate, updateRoomStayRequest.arrivalDate) &&
        Objects.equals(this.departureDate, updateRoomStayRequest.departureDate) &&
        Objects.equals(this.roomOccupancy, updateRoomStayRequest.roomOccupancy) &&
        Objects.equals(this.roomRates, updateRoomStayRequest.roomRates);
  }

  @Override
  public int hashCode() {
    return Objects.hash(arrivalDate, departureDate, roomOccupancy, roomRates);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UpdateRoomStayRequest {\n");
    sb.append("    arrivalDate: ").append(toIndentedString(arrivalDate)).append("\n");
    sb.append("    departureDate: ").append(toIndentedString(departureDate)).append("\n");
    sb.append("    roomOccupancy: ").append(toIndentedString(roomOccupancy)).append("\n");
    sb.append("    roomRates: ").append(toIndentedString(roomRates)).append("\n");
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

