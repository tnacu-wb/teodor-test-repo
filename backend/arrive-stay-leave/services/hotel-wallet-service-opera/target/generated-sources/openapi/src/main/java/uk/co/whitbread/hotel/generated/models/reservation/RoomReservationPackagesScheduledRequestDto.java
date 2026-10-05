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
import uk.co.whitbread.hotel.generated.models.reservation.PackagesSelectionScheduledDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * RoomReservationPackagesScheduledRequestDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:11:18.711997+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RoomReservationPackagesScheduledRequestDto {

  @Valid
  private List<@Valid PackagesSelectionScheduledDto> addPackages = new ArrayList<>();

  @Valid
  private List<@Valid PackagesSelectionDto> removePackages = new ArrayList<>();

  private String reservationsId;

  public RoomReservationPackagesScheduledRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public RoomReservationPackagesScheduledRequestDto(List<@Valid PackagesSelectionScheduledDto> addPackages, List<@Valid PackagesSelectionDto> removePackages, String reservationsId) {
    this.addPackages = addPackages;
    this.removePackages = removePackages;
    this.reservationsId = reservationsId;
  }

  public RoomReservationPackagesScheduledRequestDto addPackages(List<@Valid PackagesSelectionScheduledDto> addPackages) {
    this.addPackages = addPackages;
    return this;
  }

  public RoomReservationPackagesScheduledRequestDto addAddPackagesItem(PackagesSelectionScheduledDto addPackagesItem) {
    if (this.addPackages == null) {
      this.addPackages = new ArrayList<>();
    }
    this.addPackages.add(addPackagesItem);
    return this;
  }

  /**
   * Get addPackages
   * @return addPackages
   */
  @NotNull @Valid 
  @Schema(name = "addPackages", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("addPackages")
  public List<@Valid PackagesSelectionScheduledDto> getAddPackages() {
    return addPackages;
  }

  public void setAddPackages(List<@Valid PackagesSelectionScheduledDto> addPackages) {
    this.addPackages = addPackages;
  }

  public RoomReservationPackagesScheduledRequestDto removePackages(List<@Valid PackagesSelectionDto> removePackages) {
    this.removePackages = removePackages;
    return this;
  }

  public RoomReservationPackagesScheduledRequestDto addRemovePackagesItem(PackagesSelectionDto removePackagesItem) {
    if (this.removePackages == null) {
      this.removePackages = new ArrayList<>();
    }
    this.removePackages.add(removePackagesItem);
    return this;
  }

  /**
   * Get removePackages
   * @return removePackages
   */
  @NotNull @Valid 
  @Schema(name = "removePackages", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("removePackages")
  public List<@Valid PackagesSelectionDto> getRemovePackages() {
    return removePackages;
  }

  public void setRemovePackages(List<@Valid PackagesSelectionDto> removePackages) {
    this.removePackages = removePackages;
  }

  public RoomReservationPackagesScheduledRequestDto reservationsId(String reservationsId) {
    this.reservationsId = reservationsId;
    return this;
  }

  /**
   * Get reservationsId
   * @return reservationsId
   */
  @NotNull 
  @Schema(name = "reservationsId", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("reservationsId")
  public String getReservationsId() {
    return reservationsId;
  }

  public void setReservationsId(String reservationsId) {
    this.reservationsId = reservationsId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RoomReservationPackagesScheduledRequestDto roomReservationPackagesScheduledRequestDto = (RoomReservationPackagesScheduledRequestDto) o;
    return Objects.equals(this.addPackages, roomReservationPackagesScheduledRequestDto.addPackages) &&
        Objects.equals(this.removePackages, roomReservationPackagesScheduledRequestDto.removePackages) &&
        Objects.equals(this.reservationsId, roomReservationPackagesScheduledRequestDto.reservationsId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(addPackages, removePackages, reservationsId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RoomReservationPackagesScheduledRequestDto {\n");
    sb.append("    addPackages: ").append(toIndentedString(addPackages)).append("\n");
    sb.append("    removePackages: ").append(toIndentedString(removePackages)).append("\n");
    sb.append("    reservationsId: ").append(toIndentedString(reservationsId)).append("\n");
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

