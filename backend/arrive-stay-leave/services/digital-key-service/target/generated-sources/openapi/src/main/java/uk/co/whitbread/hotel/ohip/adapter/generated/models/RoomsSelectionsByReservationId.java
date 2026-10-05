package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.PackagesSelection;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * RoomsSelectionsByReservationId
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:50:34.031950+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RoomsSelectionsByReservationId {

  @Valid
  private @Nullable List<@Valid PackagesSelection> packagesSelection;

  private @Nullable String reservationId;

  public RoomsSelectionsByReservationId packagesSelection(List<@Valid PackagesSelection> packagesSelection) {
    this.packagesSelection = packagesSelection;
    return this;
  }

  public RoomsSelectionsByReservationId addPackagesSelectionItem(PackagesSelection packagesSelectionItem) {
    if (this.packagesSelection == null) {
      this.packagesSelection = new ArrayList<>();
    }
    this.packagesSelection.add(packagesSelectionItem);
    return this;
  }

  /**
   * Get packagesSelection
   * @return packagesSelection
   */
  @Valid 
  @Schema(name = "packagesSelection", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("packagesSelection")
  public List<@Valid PackagesSelection> getPackagesSelection() {
    return packagesSelection;
  }

  public void setPackagesSelection(List<@Valid PackagesSelection> packagesSelection) {
    this.packagesSelection = packagesSelection;
  }

  public RoomsSelectionsByReservationId reservationId(String reservationId) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RoomsSelectionsByReservationId roomsSelectionsByReservationId = (RoomsSelectionsByReservationId) o;
    return Objects.equals(this.packagesSelection, roomsSelectionsByReservationId.packagesSelection) &&
        Objects.equals(this.reservationId, roomsSelectionsByReservationId.reservationId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(packagesSelection, reservationId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RoomsSelectionsByReservationId {\n");
    sb.append("    packagesSelection: ").append(toIndentedString(packagesSelection)).append("\n");
    sb.append("    reservationId: ").append(toIndentedString(reservationId)).append("\n");
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

