package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RoomsSelectionsByReservationId;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * UpdateReservationPackagesByIdRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:50:34.031950+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class UpdateReservationPackagesByIdRequest {

  private @Nullable String arrival;

  private @Nullable String basketReference;

  private @Nullable String departure;

  private @Nullable String hotelId;

  @Valid
  private @Nullable List<@Valid RoomsSelectionsByReservationId> previousRoomsSelections;

  @Valid
  private @Nullable List<@Valid RoomsSelectionsByReservationId> roomsSelections;

  public UpdateReservationPackagesByIdRequest arrival(String arrival) {
    this.arrival = arrival;
    return this;
  }

  /**
   * Get arrival
   * @return arrival
   */
  
  @Schema(name = "arrival", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("arrival")
  public String getArrival() {
    return arrival;
  }

  public void setArrival(String arrival) {
    this.arrival = arrival;
  }

  public UpdateReservationPackagesByIdRequest basketReference(String basketReference) {
    this.basketReference = basketReference;
    return this;
  }

  /**
   * Get basketReference
   * @return basketReference
   */
  
  @Schema(name = "basketReference", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("basketReference")
  public String getBasketReference() {
    return basketReference;
  }

  public void setBasketReference(String basketReference) {
    this.basketReference = basketReference;
  }

  public UpdateReservationPackagesByIdRequest departure(String departure) {
    this.departure = departure;
    return this;
  }

  /**
   * Get departure
   * @return departure
   */
  
  @Schema(name = "departure", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("departure")
  public String getDeparture() {
    return departure;
  }

  public void setDeparture(String departure) {
    this.departure = departure;
  }

  public UpdateReservationPackagesByIdRequest hotelId(String hotelId) {
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

  public UpdateReservationPackagesByIdRequest previousRoomsSelections(List<@Valid RoomsSelectionsByReservationId> previousRoomsSelections) {
    this.previousRoomsSelections = previousRoomsSelections;
    return this;
  }

  public UpdateReservationPackagesByIdRequest addPreviousRoomsSelectionsItem(RoomsSelectionsByReservationId previousRoomsSelectionsItem) {
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
  public List<@Valid RoomsSelectionsByReservationId> getPreviousRoomsSelections() {
    return previousRoomsSelections;
  }

  public void setPreviousRoomsSelections(List<@Valid RoomsSelectionsByReservationId> previousRoomsSelections) {
    this.previousRoomsSelections = previousRoomsSelections;
  }

  public UpdateReservationPackagesByIdRequest roomsSelections(List<@Valid RoomsSelectionsByReservationId> roomsSelections) {
    this.roomsSelections = roomsSelections;
    return this;
  }

  public UpdateReservationPackagesByIdRequest addRoomsSelectionsItem(RoomsSelectionsByReservationId roomsSelectionsItem) {
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
  public List<@Valid RoomsSelectionsByReservationId> getRoomsSelections() {
    return roomsSelections;
  }

  public void setRoomsSelections(List<@Valid RoomsSelectionsByReservationId> roomsSelections) {
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
    UpdateReservationPackagesByIdRequest updateReservationPackagesByIdRequest = (UpdateReservationPackagesByIdRequest) o;
    return Objects.equals(this.arrival, updateReservationPackagesByIdRequest.arrival) &&
        Objects.equals(this.basketReference, updateReservationPackagesByIdRequest.basketReference) &&
        Objects.equals(this.departure, updateReservationPackagesByIdRequest.departure) &&
        Objects.equals(this.hotelId, updateReservationPackagesByIdRequest.hotelId) &&
        Objects.equals(this.previousRoomsSelections, updateReservationPackagesByIdRequest.previousRoomsSelections) &&
        Objects.equals(this.roomsSelections, updateReservationPackagesByIdRequest.roomsSelections);
  }

  @Override
  public int hashCode() {
    return Objects.hash(arrival, basketReference, departure, hotelId, previousRoomsSelections, roomsSelections);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UpdateReservationPackagesByIdRequest {\n");
    sb.append("    arrival: ").append(toIndentedString(arrival)).append("\n");
    sb.append("    basketReference: ").append(toIndentedString(basketReference)).append("\n");
    sb.append("    departure: ").append(toIndentedString(departure)).append("\n");
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

