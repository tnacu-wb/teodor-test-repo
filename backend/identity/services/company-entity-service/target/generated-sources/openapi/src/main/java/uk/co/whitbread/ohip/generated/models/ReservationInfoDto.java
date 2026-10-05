package uk.co.whitbread.ohip.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.ohip.generated.models.ExternalReferenceTypeDto;
import uk.co.whitbread.ohip.generated.models.ReservationGuestDto;
import uk.co.whitbread.ohip.generated.models.RoomStayDto;
import uk.co.whitbread.ohip.generated.models.UniqueIdTypeDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * ReservationInfoDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:50:12.143180+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ReservationInfoDto {

  @Valid
  private List<@Valid ExternalReferenceTypeDto> externalReferences = new ArrayList<>();

  private @Nullable String hotelId;

  private @Nullable String hotelName;

  private @Nullable ReservationGuestDto reservationGuest;

  @Valid
  private List<@Valid UniqueIdTypeDto> reservationIdList = new ArrayList<>();

  private @Nullable String reservationStatus;

  private @Nullable RoomStayDto roomStay;

  private @Nullable Boolean roomStayReservation;

  public ReservationInfoDto externalReferences(List<@Valid ExternalReferenceTypeDto> externalReferences) {
    this.externalReferences = externalReferences;
    return this;
  }

  public ReservationInfoDto addExternalReferencesItem(ExternalReferenceTypeDto externalReferencesItem) {
    if (this.externalReferences == null) {
      this.externalReferences = new ArrayList<>();
    }
    this.externalReferences.add(externalReferencesItem);
    return this;
  }

  /**
   * Get externalReferences
   * @return externalReferences
   */
  @Valid 
  @Schema(name = "externalReferences", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("externalReferences")
  public List<@Valid ExternalReferenceTypeDto> getExternalReferences() {
    return externalReferences;
  }

  public void setExternalReferences(List<@Valid ExternalReferenceTypeDto> externalReferences) {
    this.externalReferences = externalReferences;
  }

  public ReservationInfoDto hotelId(String hotelId) {
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

  public ReservationInfoDto hotelName(String hotelName) {
    this.hotelName = hotelName;
    return this;
  }

  /**
   * Get hotelName
   * @return hotelName
   */
  
  @Schema(name = "hotelName", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hotelName")
  public String getHotelName() {
    return hotelName;
  }

  public void setHotelName(String hotelName) {
    this.hotelName = hotelName;
  }

  public ReservationInfoDto reservationGuest(ReservationGuestDto reservationGuest) {
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
  public ReservationGuestDto getReservationGuest() {
    return reservationGuest;
  }

  public void setReservationGuest(ReservationGuestDto reservationGuest) {
    this.reservationGuest = reservationGuest;
  }

  public ReservationInfoDto reservationIdList(List<@Valid UniqueIdTypeDto> reservationIdList) {
    this.reservationIdList = reservationIdList;
    return this;
  }

  public ReservationInfoDto addReservationIdListItem(UniqueIdTypeDto reservationIdListItem) {
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

  public ReservationInfoDto reservationStatus(String reservationStatus) {
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

  public ReservationInfoDto roomStay(RoomStayDto roomStay) {
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
  public RoomStayDto getRoomStay() {
    return roomStay;
  }

  public void setRoomStay(RoomStayDto roomStay) {
    this.roomStay = roomStay;
  }

  public ReservationInfoDto roomStayReservation(Boolean roomStayReservation) {
    this.roomStayReservation = roomStayReservation;
    return this;
  }

  /**
   * Get roomStayReservation
   * @return roomStayReservation
   */
  
  @Schema(name = "roomStayReservation", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomStayReservation")
  public Boolean getRoomStayReservation() {
    return roomStayReservation;
  }

  public void setRoomStayReservation(Boolean roomStayReservation) {
    this.roomStayReservation = roomStayReservation;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ReservationInfoDto reservationInfoDto = (ReservationInfoDto) o;
    return Objects.equals(this.externalReferences, reservationInfoDto.externalReferences) &&
        Objects.equals(this.hotelId, reservationInfoDto.hotelId) &&
        Objects.equals(this.hotelName, reservationInfoDto.hotelName) &&
        Objects.equals(this.reservationGuest, reservationInfoDto.reservationGuest) &&
        Objects.equals(this.reservationIdList, reservationInfoDto.reservationIdList) &&
        Objects.equals(this.reservationStatus, reservationInfoDto.reservationStatus) &&
        Objects.equals(this.roomStay, reservationInfoDto.roomStay) &&
        Objects.equals(this.roomStayReservation, reservationInfoDto.roomStayReservation);
  }

  @Override
  public int hashCode() {
    return Objects.hash(externalReferences, hotelId, hotelName, reservationGuest, reservationIdList, reservationStatus, roomStay, roomStayReservation);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ReservationInfoDto {\n");
    sb.append("    externalReferences: ").append(toIndentedString(externalReferences)).append("\n");
    sb.append("    hotelId: ").append(toIndentedString(hotelId)).append("\n");
    sb.append("    hotelName: ").append(toIndentedString(hotelName)).append("\n");
    sb.append("    reservationGuest: ").append(toIndentedString(reservationGuest)).append("\n");
    sb.append("    reservationIdList: ").append(toIndentedString(reservationIdList)).append("\n");
    sb.append("    reservationStatus: ").append(toIndentedString(reservationStatus)).append("\n");
    sb.append("    roomStay: ").append(toIndentedString(roomStay)).append("\n");
    sb.append("    roomStayReservation: ").append(toIndentedString(roomStayReservation)).append("\n");
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

