package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationGuestsSingleCall;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UpdateRoomStayRequest;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * UpdateReservationRequestSingleCall
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:15.274731+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class UpdateReservationRequestSingleCall {

  private @Nullable String hotelId;

  @Valid
  private List<@Valid ReservationGuestsSingleCall> reservationGuests = new ArrayList<>();

  private @Nullable String reservationId;

  private @Nullable UpdateRoomStayRequest roomStay;

  public UpdateReservationRequestSingleCall hotelId(String hotelId) {
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

  public UpdateReservationRequestSingleCall reservationGuests(List<@Valid ReservationGuestsSingleCall> reservationGuests) {
    this.reservationGuests = reservationGuests;
    return this;
  }

  public UpdateReservationRequestSingleCall addReservationGuestsItem(ReservationGuestsSingleCall reservationGuestsItem) {
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
  public List<@Valid ReservationGuestsSingleCall> getReservationGuests() {
    return reservationGuests;
  }

  public void setReservationGuests(List<@Valid ReservationGuestsSingleCall> reservationGuests) {
    this.reservationGuests = reservationGuests;
  }

  public UpdateReservationRequestSingleCall reservationId(String reservationId) {
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

  public UpdateReservationRequestSingleCall roomStay(UpdateRoomStayRequest roomStay) {
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
  public UpdateRoomStayRequest getRoomStay() {
    return roomStay;
  }

  public void setRoomStay(UpdateRoomStayRequest roomStay) {
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
    UpdateReservationRequestSingleCall updateReservationRequestSingleCall = (UpdateReservationRequestSingleCall) o;
    return Objects.equals(this.hotelId, updateReservationRequestSingleCall.hotelId) &&
        Objects.equals(this.reservationGuests, updateReservationRequestSingleCall.reservationGuests) &&
        Objects.equals(this.reservationId, updateReservationRequestSingleCall.reservationId) &&
        Objects.equals(this.roomStay, updateReservationRequestSingleCall.roomStay);
  }

  @Override
  public int hashCode() {
    return Objects.hash(hotelId, reservationGuests, reservationId, roomStay);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UpdateReservationRequestSingleCall {\n");
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

