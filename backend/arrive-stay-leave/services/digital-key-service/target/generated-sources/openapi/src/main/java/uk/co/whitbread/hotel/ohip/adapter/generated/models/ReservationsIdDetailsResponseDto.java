package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationsIdDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * ReservationsIdDetailsResponseDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:50:34.031950+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ReservationsIdDetailsResponseDto {

  private @Nullable ReservationsIdDto reservations;

  public ReservationsIdDetailsResponseDto reservations(ReservationsIdDto reservations) {
    this.reservations = reservations;
    return this;
  }

  /**
   * Get reservations
   * @return reservations
   */
  @Valid 
  @Schema(name = "reservations", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reservations")
  public ReservationsIdDto getReservations() {
    return reservations;
  }

  public void setReservations(ReservationsIdDto reservations) {
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
    ReservationsIdDetailsResponseDto reservationsIdDetailsResponseDto = (ReservationsIdDetailsResponseDto) o;
    return Objects.equals(this.reservations, reservationsIdDetailsResponseDto.reservations);
  }

  @Override
  public int hashCode() {
    return Objects.hash(reservations);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ReservationsIdDetailsResponseDto {\n");
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

