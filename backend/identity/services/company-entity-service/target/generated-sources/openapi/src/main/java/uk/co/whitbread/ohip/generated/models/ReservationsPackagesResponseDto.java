package uk.co.whitbread.ohip.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.ohip.generated.models.RoomsSelectionsDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * ReservationsPackagesResponseDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:50:12.143180+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ReservationsPackagesResponseDto {

  @Valid
  private List<@Valid RoomsSelectionsDto> roomsSelections = new ArrayList<>();

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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ReservationsPackagesResponseDto reservationsPackagesResponseDto = (ReservationsPackagesResponseDto) o;
    return Objects.equals(this.roomsSelections, reservationsPackagesResponseDto.roomsSelections);
  }

  @Override
  public int hashCode() {
    return Objects.hash(roomsSelections);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ReservationsPackagesResponseDto {\n");
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

