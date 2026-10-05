package uk.co.whitbread.hotel.reservation.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.reservation.generated.models.RoomsSelectionsDto;
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

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:09:52.163805+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ReservationPackagesRequestDto {

  private String arrivalDate;

  private String basketReferenceId;

  private String departureDate;

  private @Nullable String hotelId;

  @Valid
  private List<@Valid RoomsSelectionsDto> previousRoomsSelections = new ArrayList<>();

  @Valid
  private List<@Valid RoomsSelectionsDto> roomsSelections = new ArrayList<>();

  public ReservationPackagesRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ReservationPackagesRequestDto(String arrivalDate, String basketReferenceId, String departureDate) {
    this.arrivalDate = arrivalDate;
    this.basketReferenceId = basketReferenceId;
    this.departureDate = departureDate;
  }

  public ReservationPackagesRequestDto arrivalDate(String arrivalDate) {
    this.arrivalDate = arrivalDate;
    return this;
  }

  /**
   * Get arrivalDate
   * @return arrivalDate
   */
  @NotNull 
  @Schema(name = "arrivalDate", example = "2015-10-20", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("arrivalDate")
  public String getArrivalDate() {
    return arrivalDate;
  }

  public void setArrivalDate(String arrivalDate) {
    this.arrivalDate = arrivalDate;
  }

  public ReservationPackagesRequestDto basketReferenceId(String basketReferenceId) {
    this.basketReferenceId = basketReferenceId;
    return this;
  }

  /**
   * Get basketReferenceId
   * @return basketReferenceId
   */
  @NotNull 
  @Schema(name = "basketReferenceId", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("basketReferenceId")
  public String getBasketReferenceId() {
    return basketReferenceId;
  }

  public void setBasketReferenceId(String basketReferenceId) {
    this.basketReferenceId = basketReferenceId;
  }

  public ReservationPackagesRequestDto departureDate(String departureDate) {
    this.departureDate = departureDate;
    return this;
  }

  /**
   * Get departureDate
   * @return departureDate
   */
  @NotNull 
  @Schema(name = "departureDate", example = "2015-10-21", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("departureDate")
  public String getDepartureDate() {
    return departureDate;
  }

  public void setDepartureDate(String departureDate) {
    this.departureDate = departureDate;
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
    return Objects.equals(this.arrivalDate, reservationPackagesRequestDto.arrivalDate) &&
        Objects.equals(this.basketReferenceId, reservationPackagesRequestDto.basketReferenceId) &&
        Objects.equals(this.departureDate, reservationPackagesRequestDto.departureDate) &&
        Objects.equals(this.hotelId, reservationPackagesRequestDto.hotelId) &&
        Objects.equals(this.previousRoomsSelections, reservationPackagesRequestDto.previousRoomsSelections) &&
        Objects.equals(this.roomsSelections, reservationPackagesRequestDto.roomsSelections);
  }

  @Override
  public int hashCode() {
    return Objects.hash(arrivalDate, basketReferenceId, departureDate, hotelId, previousRoomsSelections, roomsSelections);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ReservationPackagesRequestDto {\n");
    sb.append("    arrivalDate: ").append(toIndentedString(arrivalDate)).append("\n");
    sb.append("    basketReferenceId: ").append(toIndentedString(basketReferenceId)).append("\n");
    sb.append("    departureDate: ").append(toIndentedString(departureDate)).append("\n");
    sb.append("    hotelId: ").append(toIndentedString(hotelId)).append("\n");
    sb.append("    previousRoomsSelections: ").append(toIndentedString(previousRoomsSelections)).append("\n");
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

