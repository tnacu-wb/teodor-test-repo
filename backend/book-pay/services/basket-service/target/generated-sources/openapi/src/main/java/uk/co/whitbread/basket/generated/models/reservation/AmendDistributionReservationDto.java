package uk.co.whitbread.basket.generated.models.reservation;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.reservation.AmendDistributionGuestDto;
import uk.co.whitbread.basket.generated.models.reservation.AmendPackagesDistributionDto;
import uk.co.whitbread.basket.generated.models.reservation.RoomStayDistributionDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * AmendDistributionReservationDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:03.993735+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class AmendDistributionReservationDto {

  private String hotelId;

  @Valid
  private List<@Valid AmendDistributionGuestDto> reservationGuestList = new ArrayList<>();

  private @Nullable String reservationId;

  @Valid
  private List<@Valid AmendPackagesDistributionDto> reservationPackageList = new ArrayList<>();

  private @Nullable RoomStayDistributionDto roomStay;

  public AmendDistributionReservationDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public AmendDistributionReservationDto(String hotelId) {
    this.hotelId = hotelId;
  }

  public AmendDistributionReservationDto hotelId(String hotelId) {
    this.hotelId = hotelId;
    return this;
  }

  /**
   * Get hotelId
   * @return hotelId
   */
  @NotNull 
  @Schema(name = "hotelId", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("hotelId")
  public String getHotelId() {
    return hotelId;
  }

  public void setHotelId(String hotelId) {
    this.hotelId = hotelId;
  }

  public AmendDistributionReservationDto reservationGuestList(List<@Valid AmendDistributionGuestDto> reservationGuestList) {
    this.reservationGuestList = reservationGuestList;
    return this;
  }

  public AmendDistributionReservationDto addReservationGuestListItem(AmendDistributionGuestDto reservationGuestListItem) {
    if (this.reservationGuestList == null) {
      this.reservationGuestList = new ArrayList<>();
    }
    this.reservationGuestList.add(reservationGuestListItem);
    return this;
  }

  /**
   * Get reservationGuestList
   * @return reservationGuestList
   */
  @Valid 
  @Schema(name = "reservationGuestList", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reservationGuestList")
  public List<@Valid AmendDistributionGuestDto> getReservationGuestList() {
    return reservationGuestList;
  }

  public void setReservationGuestList(List<@Valid AmendDistributionGuestDto> reservationGuestList) {
    this.reservationGuestList = reservationGuestList;
  }

  public AmendDistributionReservationDto reservationId(String reservationId) {
    this.reservationId = reservationId;
    return this;
  }

  /**
   * Get reservationId
   * @return reservationId
   */
  
  @Schema(name = "reservationId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reservationId")
  public String getReservationId() {
    return reservationId;
  }

  public void setReservationId(String reservationId) {
    this.reservationId = reservationId;
  }

  public AmendDistributionReservationDto reservationPackageList(List<@Valid AmendPackagesDistributionDto> reservationPackageList) {
    this.reservationPackageList = reservationPackageList;
    return this;
  }

  public AmendDistributionReservationDto addReservationPackageListItem(AmendPackagesDistributionDto reservationPackageListItem) {
    if (this.reservationPackageList == null) {
      this.reservationPackageList = new ArrayList<>();
    }
    this.reservationPackageList.add(reservationPackageListItem);
    return this;
  }

  /**
   * Get reservationPackageList
   * @return reservationPackageList
   */
  @Valid 
  @Schema(name = "reservationPackageList", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reservationPackageList")
  public List<@Valid AmendPackagesDistributionDto> getReservationPackageList() {
    return reservationPackageList;
  }

  public void setReservationPackageList(List<@Valid AmendPackagesDistributionDto> reservationPackageList) {
    this.reservationPackageList = reservationPackageList;
  }

  public AmendDistributionReservationDto roomStay(RoomStayDistributionDto roomStay) {
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
  public RoomStayDistributionDto getRoomStay() {
    return roomStay;
  }

  public void setRoomStay(RoomStayDistributionDto roomStay) {
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
    AmendDistributionReservationDto amendDistributionReservationDto = (AmendDistributionReservationDto) o;
    return Objects.equals(this.hotelId, amendDistributionReservationDto.hotelId) &&
        Objects.equals(this.reservationGuestList, amendDistributionReservationDto.reservationGuestList) &&
        Objects.equals(this.reservationId, amendDistributionReservationDto.reservationId) &&
        Objects.equals(this.reservationPackageList, amendDistributionReservationDto.reservationPackageList) &&
        Objects.equals(this.roomStay, amendDistributionReservationDto.roomStay);
  }

  @Override
  public int hashCode() {
    return Objects.hash(hotelId, reservationGuestList, reservationId, reservationPackageList, roomStay);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AmendDistributionReservationDto {\n");
    sb.append("    hotelId: ").append(toIndentedString(hotelId)).append("\n");
    sb.append("    reservationGuestList: ").append(toIndentedString(reservationGuestList)).append("\n");
    sb.append("    reservationId: ").append(toIndentedString(reservationId)).append("\n");
    sb.append("    reservationPackageList: ").append(toIndentedString(reservationPackageList)).append("\n");
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

