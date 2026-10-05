package uk.co.whitbread.basket.generated.models.ohip;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.ohip.ReservationGuestsDto;
import uk.co.whitbread.basket.generated.models.ohip.UpdateRoomStayDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * UpdateReservationRequestDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:10.951524+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class UpdateReservationRequestDto {

  private String hotelId;

  @Valid
  private List<@Valid ReservationGuestsDto> reservationGuests = new ArrayList<>();

  private String reservationId;

  private @Nullable UpdateRoomStayDto roomStay;

  public UpdateReservationRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public UpdateReservationRequestDto(String hotelId, String reservationId) {
    this.hotelId = hotelId;
    this.reservationId = reservationId;
  }

  public UpdateReservationRequestDto hotelId(String hotelId) {
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

  public UpdateReservationRequestDto reservationGuests(List<@Valid ReservationGuestsDto> reservationGuests) {
    this.reservationGuests = reservationGuests;
    return this;
  }

  public UpdateReservationRequestDto addReservationGuestsItem(ReservationGuestsDto reservationGuestsItem) {
    if (this.reservationGuests == null) {
      this.reservationGuests = new ArrayList<>();
    }
    this.reservationGuests.add(reservationGuestsItem);
    return this;
  }

  /**
   * Get reservationGuests
   * @return reservationGuests
   */
  @Valid 
  @Schema(name = "reservationGuests", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reservationGuests")
  public List<@Valid ReservationGuestsDto> getReservationGuests() {
    return reservationGuests;
  }

  public void setReservationGuests(List<@Valid ReservationGuestsDto> reservationGuests) {
    this.reservationGuests = reservationGuests;
  }

  public UpdateReservationRequestDto reservationId(String reservationId) {
    this.reservationId = reservationId;
    return this;
  }

  /**
   * Get reservationId
   * @return reservationId
   */
  @NotNull 
  @Schema(name = "reservationId", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("reservationId")
  public String getReservationId() {
    return reservationId;
  }

  public void setReservationId(String reservationId) {
    this.reservationId = reservationId;
  }

  public UpdateReservationRequestDto roomStay(UpdateRoomStayDto roomStay) {
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
  public UpdateRoomStayDto getRoomStay() {
    return roomStay;
  }

  public void setRoomStay(UpdateRoomStayDto roomStay) {
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
    UpdateReservationRequestDto updateReservationRequestDto = (UpdateReservationRequestDto) o;
    return Objects.equals(this.hotelId, updateReservationRequestDto.hotelId) &&
        Objects.equals(this.reservationGuests, updateReservationRequestDto.reservationGuests) &&
        Objects.equals(this.reservationId, updateReservationRequestDto.reservationId) &&
        Objects.equals(this.roomStay, updateReservationRequestDto.roomStay);
  }

  @Override
  public int hashCode() {
    return Objects.hash(hotelId, reservationGuests, reservationId, roomStay);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UpdateReservationRequestDto {\n");
    sb.append("    hotelId: ").append(toIndentedString(hotelId)).append("\n");
    sb.append("    reservationGuests: ").append(toIndentedString(reservationGuests)).append("\n");
    sb.append("    reservationId: ").append(toIndentedString(reservationId)).append("\n");
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

