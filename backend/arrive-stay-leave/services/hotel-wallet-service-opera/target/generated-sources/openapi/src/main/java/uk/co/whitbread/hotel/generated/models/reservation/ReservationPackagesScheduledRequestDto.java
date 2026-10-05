package uk.co.whitbread.hotel.generated.models.reservation;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.generated.models.reservation.RoomReservationPackagesScheduledRequestDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * ReservationPackagesScheduledRequestDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:11:18.711997+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ReservationPackagesScheduledRequestDto {

  private @Nullable String hotelId;

  @Valid
  private List<@Valid RoomReservationPackagesScheduledRequestDto> reservations = new ArrayList<>();

  public ReservationPackagesScheduledRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ReservationPackagesScheduledRequestDto(List<@Valid RoomReservationPackagesScheduledRequestDto> reservations) {
    this.reservations = reservations;
  }

  public ReservationPackagesScheduledRequestDto hotelId(String hotelId) {
    this.hotelId = hotelId;
    return this;
  }

  /**
   * Get hotelId
   * @return hotelId
   */
  
  @Schema(name = "hotelId", example = "FRAMTI", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hotelId")
  public String getHotelId() {
    return hotelId;
  }

  public void setHotelId(String hotelId) {
    this.hotelId = hotelId;
  }

  public ReservationPackagesScheduledRequestDto reservations(List<@Valid RoomReservationPackagesScheduledRequestDto> reservations) {
    this.reservations = reservations;
    return this;
  }

  public ReservationPackagesScheduledRequestDto addReservationsItem(RoomReservationPackagesScheduledRequestDto reservationsItem) {
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
  @NotNull @Valid 
  @Schema(name = "reservations", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("reservations")
  public List<@Valid RoomReservationPackagesScheduledRequestDto> getReservations() {
    return reservations;
  }

  public void setReservations(List<@Valid RoomReservationPackagesScheduledRequestDto> reservations) {
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
    ReservationPackagesScheduledRequestDto reservationPackagesScheduledRequestDto = (ReservationPackagesScheduledRequestDto) o;
    return Objects.equals(this.hotelId, reservationPackagesScheduledRequestDto.hotelId) &&
        Objects.equals(this.reservations, reservationPackagesScheduledRequestDto.reservations);
  }

  @Override
  public int hashCode() {
    return Objects.hash(hotelId, reservations);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ReservationPackagesScheduledRequestDto {\n");
    sb.append("    hotelId: ").append(toIndentedString(hotelId)).append("\n");
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

