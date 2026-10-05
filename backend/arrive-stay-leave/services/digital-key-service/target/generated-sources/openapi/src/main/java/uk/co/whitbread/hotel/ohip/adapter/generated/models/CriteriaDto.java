package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationIdListDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * CriteriaDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:50:34.031950+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CriteriaDto {

  private @Nullable String hotelId;

  @Valid
  private List<@Valid ReservationIdListDto> reservationIdList;

  private @Nullable String roomId;

  private @Nullable Boolean roomNumberLocked;

  private @Nullable Boolean updateRoomTypeCharged;

  public CriteriaDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public CriteriaDto(List<@Valid ReservationIdListDto> reservationIdList) {
    this.reservationIdList = reservationIdList;
  }

  public CriteriaDto hotelId(String hotelId) {
    this.hotelId = hotelId;
    return this;
  }

  /**
   * Get hotelId
   * @return hotelId
   */
  
  @Schema(name = "hotelId", example = "LONSTM", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hotelId")
  public String getHotelId() {
    return hotelId;
  }

  public void setHotelId(String hotelId) {
    this.hotelId = hotelId;
  }

  public CriteriaDto reservationIdList(List<@Valid ReservationIdListDto> reservationIdList) {
    this.reservationIdList = reservationIdList;
    return this;
  }

  public CriteriaDto addReservationIdListItem(ReservationIdListDto reservationIdListItem) {
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
  @NotNull @Valid 
  @Schema(name = "reservationIdList", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("reservationIdList")
  public List<@Valid ReservationIdListDto> getReservationIdList() {
    return reservationIdList;
  }

  public void setReservationIdList(List<@Valid ReservationIdListDto> reservationIdList) {
    this.reservationIdList = reservationIdList;
  }

  public CriteriaDto roomId(String roomId) {
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

  public CriteriaDto roomNumberLocked(Boolean roomNumberLocked) {
    this.roomNumberLocked = roomNumberLocked;
    return this;
  }

  /**
   * Get roomNumberLocked
   * @return roomNumberLocked
   */
  
  @Schema(name = "roomNumberLocked", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomNumberLocked")
  public Boolean getRoomNumberLocked() {
    return roomNumberLocked;
  }

  public void setRoomNumberLocked(Boolean roomNumberLocked) {
    this.roomNumberLocked = roomNumberLocked;
  }

  public CriteriaDto updateRoomTypeCharged(Boolean updateRoomTypeCharged) {
    this.updateRoomTypeCharged = updateRoomTypeCharged;
    return this;
  }

  /**
   * Get updateRoomTypeCharged
   * @return updateRoomTypeCharged
   */
  
  @Schema(name = "updateRoomTypeCharged", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("updateRoomTypeCharged")
  public Boolean getUpdateRoomTypeCharged() {
    return updateRoomTypeCharged;
  }

  public void setUpdateRoomTypeCharged(Boolean updateRoomTypeCharged) {
    this.updateRoomTypeCharged = updateRoomTypeCharged;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CriteriaDto criteriaDto = (CriteriaDto) o;
    return Objects.equals(this.hotelId, criteriaDto.hotelId) &&
        Objects.equals(this.reservationIdList, criteriaDto.reservationIdList) &&
        Objects.equals(this.roomId, criteriaDto.roomId) &&
        Objects.equals(this.roomNumberLocked, criteriaDto.roomNumberLocked) &&
        Objects.equals(this.updateRoomTypeCharged, criteriaDto.updateRoomTypeCharged);
  }

  @Override
  public int hashCode() {
    return Objects.hash(hotelId, reservationIdList, roomId, roomNumberLocked, updateRoomTypeCharged);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CriteriaDto {\n");
    sb.append("    hotelId: ").append(toIndentedString(hotelId)).append("\n");
    sb.append("    reservationIdList: ").append(toIndentedString(reservationIdList)).append("\n");
    sb.append("    roomId: ").append(toIndentedString(roomId)).append("\n");
    sb.append("    roomNumberLocked: ").append(toIndentedString(roomNumberLocked)).append("\n");
    sb.append("    updateRoomTypeCharged: ").append(toIndentedString(updateRoomTypeCharged)).append("\n");
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

