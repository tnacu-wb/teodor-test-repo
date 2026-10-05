package uk.co.whitbread.hotel.generated.models.reservation;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.generated.models.reservation.BookingChannelDto;
import uk.co.whitbread.hotel.generated.models.reservation.ReservationDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * ReservationRequestDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:11:18.711997+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ReservationRequestDto {

  private BookingChannelDto bookingChannel;

  private @Nullable String bookingFlowId;

  private @Nullable Boolean getReservationsByIds;

  private @Nullable Boolean isOta;

  @Valid
  private List<@Valid ReservationDto> reservations = new ArrayList<>();

  public ReservationRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ReservationRequestDto(BookingChannelDto bookingChannel, List<@Valid ReservationDto> reservations) {
    this.bookingChannel = bookingChannel;
    this.reservations = reservations;
  }

  public ReservationRequestDto bookingChannel(BookingChannelDto bookingChannel) {
    this.bookingChannel = bookingChannel;
    return this;
  }

  /**
   * Get bookingChannel
   * @return bookingChannel
   */
  @NotNull @Valid 
  @Schema(name = "bookingChannel", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("bookingChannel")
  public BookingChannelDto getBookingChannel() {
    return bookingChannel;
  }

  public void setBookingChannel(BookingChannelDto bookingChannel) {
    this.bookingChannel = bookingChannel;
  }

  public ReservationRequestDto bookingFlowId(String bookingFlowId) {
    this.bookingFlowId = bookingFlowId;
    return this;
  }

  /**
   * Get bookingFlowId
   * @return bookingFlowId
   */
  
  @Schema(name = "bookingFlowId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookingFlowId")
  public String getBookingFlowId() {
    return bookingFlowId;
  }

  public void setBookingFlowId(String bookingFlowId) {
    this.bookingFlowId = bookingFlowId;
  }

  public ReservationRequestDto getReservationsByIds(Boolean getReservationsByIds) {
    this.getReservationsByIds = getReservationsByIds;
    return this;
  }

  /**
   * Get getReservationsByIds
   * @return getReservationsByIds
   */
  
  @Schema(name = "getReservationsByIds", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("getReservationsByIds")
  public Boolean getGetReservationsByIds() {
    return getReservationsByIds;
  }

  public void setGetReservationsByIds(Boolean getReservationsByIds) {
    this.getReservationsByIds = getReservationsByIds;
  }

  public ReservationRequestDto isOta(Boolean isOta) {
    this.isOta = isOta;
    return this;
  }

  /**
   * Get isOta
   * @return isOta
   */
  
  @Schema(name = "isOta", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("isOta")
  public Boolean getIsOta() {
    return isOta;
  }

  public void setIsOta(Boolean isOta) {
    this.isOta = isOta;
  }

  public ReservationRequestDto reservations(List<@Valid ReservationDto> reservations) {
    this.reservations = reservations;
    return this;
  }

  public ReservationRequestDto addReservationsItem(ReservationDto reservationsItem) {
    if (this.reservations == null) {
      this.reservations = new ArrayList<>();
    }
    this.reservations.add(reservationsItem);
    return this;
  }

  /**
   * Get reservations
   * @return reservations
   */
  @NotNull @Valid @Size(min = 1, max = 2147483647) 
  @Schema(name = "reservations", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("reservations")
  public List<@Valid ReservationDto> getReservations() {
    return reservations;
  }

  public void setReservations(List<@Valid ReservationDto> reservations) {
    this.reservations = reservations;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ReservationRequestDto reservationRequestDto = (ReservationRequestDto) o;
    return Objects.equals(this.bookingChannel, reservationRequestDto.bookingChannel) &&
        Objects.equals(this.bookingFlowId, reservationRequestDto.bookingFlowId) &&
        Objects.equals(this.getReservationsByIds, reservationRequestDto.getReservationsByIds) &&
        Objects.equals(this.isOta, reservationRequestDto.isOta) &&
        Objects.equals(this.reservations, reservationRequestDto.reservations);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bookingChannel, bookingFlowId, getReservationsByIds, isOta, reservations);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ReservationRequestDto {\n");
    sb.append("    bookingChannel: ").append(toIndentedString(bookingChannel)).append("\n");
    sb.append("    bookingFlowId: ").append(toIndentedString(bookingFlowId)).append("\n");
    sb.append("    getReservationsByIds: ").append(toIndentedString(getReservationsByIds)).append("\n");
    sb.append("    isOta: ").append(toIndentedString(isOta)).append("\n");
    sb.append("    reservations: ").append(toIndentedString(reservations)).append("\n");
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

