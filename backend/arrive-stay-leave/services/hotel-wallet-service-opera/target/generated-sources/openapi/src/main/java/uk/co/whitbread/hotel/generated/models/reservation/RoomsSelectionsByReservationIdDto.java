package uk.co.whitbread.hotel.generated.models.reservation;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.generated.models.reservation.PackagesSelectionDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * RoomsSelectionsByReservationIdDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:11:18.711997+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RoomsSelectionsByReservationIdDto {

  @Valid
  private List<@Valid PackagesSelectionDto> packagesSelection = new ArrayList<>();

  private @Nullable String reservationId;

  public RoomsSelectionsByReservationIdDto packagesSelection(List<@Valid PackagesSelectionDto> packagesSelection) {
    this.packagesSelection = packagesSelection;
    return this;
  }

  public RoomsSelectionsByReservationIdDto addPackagesSelectionItem(PackagesSelectionDto packagesSelectionItem) {
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
  public List<@Valid PackagesSelectionDto> getPackagesSelection() {
    return packagesSelection;
  }

  public void setPackagesSelection(List<@Valid PackagesSelectionDto> packagesSelection) {
    this.packagesSelection = packagesSelection;
  }

  public RoomsSelectionsByReservationIdDto reservationId(String reservationId) {
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
    RoomsSelectionsByReservationIdDto roomsSelectionsByReservationIdDto = (RoomsSelectionsByReservationIdDto) o;
    return Objects.equals(this.packagesSelection, roomsSelectionsByReservationIdDto.packagesSelection) &&
        Objects.equals(this.reservationId, roomsSelectionsByReservationIdDto.reservationId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(packagesSelection, reservationId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RoomsSelectionsByReservationIdDto {\n");
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

