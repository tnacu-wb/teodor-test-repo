package uk.co.whitbread.basket.generated.models.ohip;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * ExpectedTimesDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:10.951524+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ExpectedTimesDto {

  private @Nullable String reservationExpectedArrivalTime;

  private @Nullable String reservationExpectedDepartureTime;

  public ExpectedTimesDto reservationExpectedArrivalTime(String reservationExpectedArrivalTime) {
    this.reservationExpectedArrivalTime = reservationExpectedArrivalTime;
    return this;
  }

  /**
   * Get reservationExpectedArrivalTime
   * @return reservationExpectedArrivalTime
   */
  
  @Schema(name = "reservationExpectedArrivalTime", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reservationExpectedArrivalTime")
  public String getReservationExpectedArrivalTime() {
    return reservationExpectedArrivalTime;
  }

  public void setReservationExpectedArrivalTime(String reservationExpectedArrivalTime) {
    this.reservationExpectedArrivalTime = reservationExpectedArrivalTime;
  }

  public ExpectedTimesDto reservationExpectedDepartureTime(String reservationExpectedDepartureTime) {
    this.reservationExpectedDepartureTime = reservationExpectedDepartureTime;
    return this;
  }

  /**
   * Get reservationExpectedDepartureTime
   * @return reservationExpectedDepartureTime
   */
  
  @Schema(name = "reservationExpectedDepartureTime", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reservationExpectedDepartureTime")
  public String getReservationExpectedDepartureTime() {
    return reservationExpectedDepartureTime;
  }

  public void setReservationExpectedDepartureTime(String reservationExpectedDepartureTime) {
    this.reservationExpectedDepartureTime = reservationExpectedDepartureTime;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ExpectedTimesDto expectedTimesDto = (ExpectedTimesDto) o;
    return Objects.equals(this.reservationExpectedArrivalTime, expectedTimesDto.reservationExpectedArrivalTime) &&
        Objects.equals(this.reservationExpectedDepartureTime, expectedTimesDto.reservationExpectedDepartureTime);
  }

  @Override
  public int hashCode() {
    return Objects.hash(reservationExpectedArrivalTime, reservationExpectedDepartureTime);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExpectedTimesDto {\n");
    sb.append("    reservationExpectedArrivalTime: ").append(toIndentedString(reservationExpectedArrivalTime)).append("\n");
    sb.append("    reservationExpectedDepartureTime: ").append(toIndentedString(reservationExpectedDepartureTime)).append("\n");
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

