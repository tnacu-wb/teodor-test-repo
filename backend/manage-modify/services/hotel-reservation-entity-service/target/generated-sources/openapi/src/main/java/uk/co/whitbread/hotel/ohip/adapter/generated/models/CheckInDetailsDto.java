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
 * CheckInDetailsDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:15.274731+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CheckInDetailsDto {

  private @Nullable String hotelId;

  private @Nullable String reservationNumber;

  private @Nullable String roomId;

  public CheckInDetailsDto hotelId(String hotelId) {
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

  public CheckInDetailsDto reservationNumber(String reservationNumber) {
    this.reservationNumber = reservationNumber;
    return this;
  }

  /**
   * Get reservationNumber
   * @return reservationNumber
   */
  
  @Schema(name = "reservationNumber", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reservationNumber")
  public String getReservationNumber() {
    return reservationNumber;
  }

  public void setReservationNumber(String reservationNumber) {
    this.reservationNumber = reservationNumber;
  }

  public CheckInDetailsDto roomId(String roomId) {
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
    CheckInDetailsDto checkInDetailsDto = (CheckInDetailsDto) o;
    return Objects.equals(this.hotelId, checkInDetailsDto.hotelId) &&
        Objects.equals(this.reservationNumber, checkInDetailsDto.reservationNumber) &&
        Objects.equals(this.roomId, checkInDetailsDto.roomId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(hotelId, reservationNumber, roomId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CheckInDetailsDto {\n");
    sb.append("    hotelId: ").append(toIndentedString(hotelId)).append("\n");
    sb.append("    reservationNumber: ").append(toIndentedString(reservationNumber)).append("\n");
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

