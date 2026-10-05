package uk.co.whitbread.ohip.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.ohip.generated.models.ConfirmationCustomerDto;
import uk.co.whitbread.ohip.generated.models.ConfirmationRoomStayDto;
import uk.co.whitbread.ohip.generated.models.UniqueIdTypeDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * ConfirmReservationResponseDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:50:12.143180+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ConfirmReservationResponseDto {

  private @Nullable String hotelId;

  private @Nullable ConfirmationCustomerDto reservationGuest;

  @Valid
  private List<@Valid UniqueIdTypeDto> reservationIdList = new ArrayList<>();

  private @Nullable String reservationStatus;

  private @Nullable ConfirmationRoomStayDto roomStay;

  public ConfirmReservationResponseDto hotelId(String hotelId) {
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

  public ConfirmReservationResponseDto reservationGuest(ConfirmationCustomerDto reservationGuest) {
    this.reservationGuest = reservationGuest;
    return this;
  }

  /**
   * Get reservationGuest
   * @return reservationGuest
   */
  @Valid 
  @Schema(name = "reservationGuest", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reservationGuest")
  public ConfirmationCustomerDto getReservationGuest() {
    return reservationGuest;
  }

  public void setReservationGuest(ConfirmationCustomerDto reservationGuest) {
    this.reservationGuest = reservationGuest;
  }

  public ConfirmReservationResponseDto reservationIdList(List<@Valid UniqueIdTypeDto> reservationIdList) {
    this.reservationIdList = reservationIdList;
    return this;
  }

  public ConfirmReservationResponseDto addReservationIdListItem(UniqueIdTypeDto reservationIdListItem) {
    if (this.reservationIdList == null) {
      this.reservationIdList = new ArrayList<>();
    }
    this.reservationIdList.add(reservationIdListItem);
    return this;
  }

  /**
   * Get reservationIdList
   * @return reservationIdList
   */
  @Valid 
  @Schema(name = "reservationIdList", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reservationIdList")
  public List<@Valid UniqueIdTypeDto> getReservationIdList() {
    return reservationIdList;
  }

  public void setReservationIdList(List<@Valid UniqueIdTypeDto> reservationIdList) {
    this.reservationIdList = reservationIdList;
  }

  public ConfirmReservationResponseDto reservationStatus(String reservationStatus) {
    this.reservationStatus = reservationStatus;
    return this;
  }

  /**
   * Get reservationStatus
   * @return reservationStatus
   */
  
  @Schema(name = "reservationStatus", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reservationStatus")
  public String getReservationStatus() {
    return reservationStatus;
  }

  public void setReservationStatus(String reservationStatus) {
    this.reservationStatus = reservationStatus;
  }

  public ConfirmReservationResponseDto roomStay(ConfirmationRoomStayDto roomStay) {
    this.roomStay = roomStay;
    return this;
  }

  /**
   * Get roomStay
   * @return roomStay
   */
  @Valid 
  @Schema(name = "roomStay", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomStay")
  public ConfirmationRoomStayDto getRoomStay() {
    return roomStay;
  }

  public void setRoomStay(ConfirmationRoomStayDto roomStay) {
    this.roomStay = roomStay;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ConfirmReservationResponseDto confirmReservationResponseDto = (ConfirmReservationResponseDto) o;
    return Objects.equals(this.hotelId, confirmReservationResponseDto.hotelId) &&
        Objects.equals(this.reservationGuest, confirmReservationResponseDto.reservationGuest) &&
        Objects.equals(this.reservationIdList, confirmReservationResponseDto.reservationIdList) &&
        Objects.equals(this.reservationStatus, confirmReservationResponseDto.reservationStatus) &&
        Objects.equals(this.roomStay, confirmReservationResponseDto.roomStay);
  }

  @Override
  public int hashCode() {
    return Objects.hash(hotelId, reservationGuest, reservationIdList, reservationStatus, roomStay);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ConfirmReservationResponseDto {\n");
    sb.append("    hotelId: ").append(toIndentedString(hotelId)).append("\n");
    sb.append("    reservationGuest: ").append(toIndentedString(reservationGuest)).append("\n");
    sb.append("    reservationIdList: ").append(toIndentedString(reservationIdList)).append("\n");
    sb.append("    reservationStatus: ").append(toIndentedString(reservationStatus)).append("\n");
    sb.append("    roomStay: ").append(toIndentedString(roomStay)).append("\n");
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

