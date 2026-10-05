package uk.co.whitbread.ohip.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.ohip.generated.models.BookingChannelDto;
import uk.co.whitbread.ohip.generated.models.ReservationDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * ReservationRequestDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:50:12.143180+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ReservationRequestDto {

  private BookingChannelDto bookingChannel;

  private Boolean getReservationsByIds;

  @Valid
  private List<@Valid ReservationDto> reservations = new ArrayList<>();

  public ReservationRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ReservationRequestDto(BookingChannelDto bookingChannel, Boolean getReservationsByIds, List<@Valid ReservationDto> reservations) {
    this.bookingChannel = bookingChannel;
    this.getReservationsByIds = getReservationsByIds;
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

  public ReservationRequestDto getReservationsByIds(Boolean getReservationsByIds) {
    this.getReservationsByIds = getReservationsByIds;
    return this;
  }

  /**
   * Get getReservationsByIds
   * @return getReservationsByIds
   */
  @NotNull 
  @Schema(name = "getReservationsByIds", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("getReservationsByIds")
  public Boolean getGetReservationsByIds() {
    return getReservationsByIds;
  }

  public void setGetReservationsByIds(Boolean getReservationsByIds) {
    this.getReservationsByIds = getReservationsByIds;
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
        Objects.equals(this.getReservationsByIds, reservationRequestDto.getReservationsByIds) &&
        Objects.equals(this.reservations, reservationRequestDto.reservations);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bookingChannel, getReservationsByIds, reservations);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ReservationRequestDto {\n");
    sb.append("    bookingChannel: ").append(toIndentedString(bookingChannel)).append("\n");
    sb.append("    getReservationsByIds: ").append(toIndentedString(getReservationsByIds)).append("\n");
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

