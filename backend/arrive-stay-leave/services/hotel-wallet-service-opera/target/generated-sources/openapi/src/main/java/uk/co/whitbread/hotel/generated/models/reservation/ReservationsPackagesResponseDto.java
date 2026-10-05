package uk.co.whitbread.hotel.generated.models.reservation;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.generated.models.reservation.RoomsSelectionsDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * ReservationsPackagesResponseDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:11:18.711997+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ReservationsPackagesResponseDto {

  private @Nullable String ratePlanCode;

  @Valid
  private List<@Valid RoomsSelectionsDto> roomsSelections = new ArrayList<>();

  @Valid
  private List<@Valid RoomsSelectionsDto> roomsSelectionsAmendExtras = new ArrayList<>();

  public ReservationsPackagesResponseDto ratePlanCode(String ratePlanCode) {
    this.ratePlanCode = ratePlanCode;
    return this;
  }

  /**
   * Get ratePlanCode
   * @return ratePlanCode
   */
  
  @Schema(name = "ratePlanCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("ratePlanCode")
  public String getRatePlanCode() {
    return ratePlanCode;
  }

  public void setRatePlanCode(String ratePlanCode) {
    this.ratePlanCode = ratePlanCode;
  }

  public ReservationsPackagesResponseDto roomsSelections(List<@Valid RoomsSelectionsDto> roomsSelections) {
    this.roomsSelections = roomsSelections;
    return this;
  }

  public ReservationsPackagesResponseDto addRoomsSelectionsItem(RoomsSelectionsDto roomsSelectionsItem) {
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

  public ReservationsPackagesResponseDto roomsSelectionsAmendExtras(List<@Valid RoomsSelectionsDto> roomsSelectionsAmendExtras) {
    this.roomsSelectionsAmendExtras = roomsSelectionsAmendExtras;
    return this;
  }

  public ReservationsPackagesResponseDto addRoomsSelectionsAmendExtrasItem(RoomsSelectionsDto roomsSelectionsAmendExtrasItem) {
    if (this.roomsSelectionsAmendExtras == null) {
      this.roomsSelectionsAmendExtras = new ArrayList<>();
    }
    this.roomsSelectionsAmendExtras.add(roomsSelectionsAmendExtrasItem);
    return this;
  }

  /**
   * Get roomsSelectionsAmendExtras
   * @return roomsSelectionsAmendExtras
   */
  @Valid 
  @Schema(name = "roomsSelectionsAmendExtras", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomsSelectionsAmendExtras")
  public List<@Valid RoomsSelectionsDto> getRoomsSelectionsAmendExtras() {
    return roomsSelectionsAmendExtras;
  }

  public void setRoomsSelectionsAmendExtras(List<@Valid RoomsSelectionsDto> roomsSelectionsAmendExtras) {
    this.roomsSelectionsAmendExtras = roomsSelectionsAmendExtras;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ReservationsPackagesResponseDto reservationsPackagesResponseDto = (ReservationsPackagesResponseDto) o;
    return Objects.equals(this.ratePlanCode, reservationsPackagesResponseDto.ratePlanCode) &&
        Objects.equals(this.roomsSelections, reservationsPackagesResponseDto.roomsSelections) &&
        Objects.equals(this.roomsSelectionsAmendExtras, reservationsPackagesResponseDto.roomsSelectionsAmendExtras);
  }

  @Override
  public int hashCode() {
    return Objects.hash(ratePlanCode, roomsSelections, roomsSelectionsAmendExtras);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ReservationsPackagesResponseDto {\n");
    sb.append("    ratePlanCode: ").append(toIndentedString(ratePlanCode)).append("\n");
    sb.append("    roomsSelections: ").append(toIndentedString(roomsSelections)).append("\n");
    sb.append("    roomsSelectionsAmendExtras: ").append(toIndentedString(roomsSelectionsAmendExtras)).append("\n");
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

