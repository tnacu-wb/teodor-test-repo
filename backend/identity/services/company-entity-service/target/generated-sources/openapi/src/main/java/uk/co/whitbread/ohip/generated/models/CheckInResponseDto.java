package uk.co.whitbread.ohip.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.ohip.generated.models.CheckInReservationDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * CheckInResponseDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:50:12.143180+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CheckInResponseDto {

  @Valid
  private List<@Valid CheckInReservationDto> reservation = new ArrayList<>();

  public CheckInResponseDto reservation(List<@Valid CheckInReservationDto> reservation) {
    this.reservation = reservation;
    return this;
  }

  public CheckInResponseDto addReservationItem(CheckInReservationDto reservationItem) {
    if (this.reservation == null) {
      this.reservation = new ArrayList<>();
    }
    this.reservation.add(reservationItem);
    return this;
  }

  /**
   * Get reservation
   * @return reservation
   */
  @Valid 
  @Schema(name = "reservation", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reservation")
  public List<@Valid CheckInReservationDto> getReservation() {
    return reservation;
  }

  public void setReservation(List<@Valid CheckInReservationDto> reservation) {
    this.reservation = reservation;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CheckInResponseDto checkInResponseDto = (CheckInResponseDto) o;
    return Objects.equals(this.reservation, checkInResponseDto.reservation);
  }

  @Override
  public int hashCode() {
    return Objects.hash(reservation);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CheckInResponseDto {\n");
    sb.append("    reservation: ").append(toIndentedString(reservation)).append("\n");
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

