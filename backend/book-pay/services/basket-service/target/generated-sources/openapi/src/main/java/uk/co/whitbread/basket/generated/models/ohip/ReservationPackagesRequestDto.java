package uk.co.whitbread.basket.generated.models.ohip;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.ohip.RoomsSelectionsDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * ReservationPackagesRequestDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:10.951524+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ReservationPackagesRequestDto {

  private String arrival;

  private String departure;

  private @Nullable String hotelId;

  @Valid
  private List<@Valid RoomsSelectionsDto> previousRoomsSelections = new ArrayList<>();

  @Valid
  private List<String> reservationsId = new ArrayList<>();

  @Valid
  private List<@Valid RoomsSelectionsDto> roomsSelections = new ArrayList<>();

  public ReservationPackagesRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ReservationPackagesRequestDto(String arrival, String departure, List<String> reservationsId) {
    this.arrival = arrival;
    this.departure = departure;
    this.reservationsId = reservationsId;
  }

  public ReservationPackagesRequestDto arrival(String arrival) {
    this.arrival = arrival;
    return this;
  }

  /**
   * Get arrival
   * @return arrival
   */
  @NotNull 
  @Schema(name = "arrival", example = "2015-10-20", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("arrival")
  public String getArrival() {
    return arrival;
  }

  public void setArrival(String arrival) {
    this.arrival = arrival;
  }

  public ReservationPackagesRequestDto departure(String departure) {
    this.departure = departure;
    return this;
  }

  /**
   * Get departure
   * @return departure
   */
  @NotNull 
  @Schema(name = "departure", example = "2015-10-21", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("departure")
  public String getDeparture() {
    return departure;
  }

  public void setDeparture(String departure) {
    this.departure = departure;
  }

  public ReservationPackagesRequestDto hotelId(String hotelId) {
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

  public ReservationPackagesRequestDto previousRoomsSelections(List<@Valid RoomsSelectionsDto> previousRoomsSelections) {
    this.previousRoomsSelections = previousRoomsSelections;
    return this;
  }

  public ReservationPackagesRequestDto addPreviousRoomsSelectionsItem(RoomsSelectionsDto previousRoomsSelectionsItem) {
    if (this.previousRoomsSelections == null) {
      this.previousRoomsSelections = new ArrayList<>();
    }
    this.previousRoomsSelections.add(previousRoomsSelectionsItem);
    return this;
  }

  /**
   * Get previousRoomsSelections
   * @return previousRoomsSelections
   */
  @Valid 
  @Schema(name = "previousRoomsSelections", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("previousRoomsSelections")
  public List<@Valid RoomsSelectionsDto> getPreviousRoomsSelections() {
    return previousRoomsSelections;
  }

  public void setPreviousRoomsSelections(List<@Valid RoomsSelectionsDto> previousRoomsSelections) {
    this.previousRoomsSelections = previousRoomsSelections;
  }

  public ReservationPackagesRequestDto reservationsId(List<String> reservationsId) {
    this.reservationsId = reservationsId;
    return this;
  }

  public ReservationPackagesRequestDto addReservationsIdItem(String reservationsIdItem) {
    if (this.reservationsId == null) {
      this.reservationsId = new ArrayList<>();
    }
    this.reservationsId.add(reservationsIdItem);
    return this;
  }

  /**
   * Get reservationsId
   * @return reservationsId
   */
  @NotNull 
  @Schema(name = "reservationsId", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("reservationsId")
  public List<String> getReservationsId() {
    return reservationsId;
  }

  public void setReservationsId(List<String> reservationsId) {
    this.reservationsId = reservationsId;
  }

  public ReservationPackagesRequestDto roomsSelections(List<@Valid RoomsSelectionsDto> roomsSelections) {
    this.roomsSelections = roomsSelections;
    return this;
  }

  public ReservationPackagesRequestDto addRoomsSelectionsItem(RoomsSelectionsDto roomsSelectionsItem) {
    if (this.roomsSelections == null) {
      this.roomsSelections = new ArrayList<>();
    }
    this.roomsSelections.add(roomsSelectionsItem);
    return this;
  }

  /**
   * Get roomsSelections
   * @return roomsSelections
   */
  @Valid 
  @Schema(name = "roomsSelections", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomsSelections")
  public List<@Valid RoomsSelectionsDto> getRoomsSelections() {
    return roomsSelections;
  }

  public void setRoomsSelections(List<@Valid RoomsSelectionsDto> roomsSelections) {
    this.roomsSelections = roomsSelections;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ReservationPackagesRequestDto reservationPackagesRequestDto = (ReservationPackagesRequestDto) o;
    return Objects.equals(this.arrival, reservationPackagesRequestDto.arrival) &&
        Objects.equals(this.departure, reservationPackagesRequestDto.departure) &&
        Objects.equals(this.hotelId, reservationPackagesRequestDto.hotelId) &&
        Objects.equals(this.previousRoomsSelections, reservationPackagesRequestDto.previousRoomsSelections) &&
        Objects.equals(this.reservationsId, reservationPackagesRequestDto.reservationsId) &&
        Objects.equals(this.roomsSelections, reservationPackagesRequestDto.roomsSelections);
  }

  @Override
  public int hashCode() {
    return Objects.hash(arrival, departure, hotelId, previousRoomsSelections, reservationsId, roomsSelections);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ReservationPackagesRequestDto {\n");
    sb.append("    arrival: ").append(toIndentedString(arrival)).append("\n");
    sb.append("    departure: ").append(toIndentedString(departure)).append("\n");
    sb.append("    hotelId: ").append(toIndentedString(hotelId)).append("\n");
    sb.append("    previousRoomsSelections: ").append(toIndentedString(previousRoomsSelections)).append("\n");
    sb.append("    reservationsId: ").append(toIndentedString(reservationsId)).append("\n");
    sb.append("    roomsSelections: ").append(toIndentedString(roomsSelections)).append("\n");
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

