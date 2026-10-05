package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.LinkedHashSet;
import java.util.Set;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * CopyReservationsRequestDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:15.274731+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CopyReservationsRequestDto {

  private String externalReferenceId;

  private String hotelId;

  @Valid
  private Set<String> reservationIds = new LinkedHashSet<>();

  public CopyReservationsRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public CopyReservationsRequestDto(String externalReferenceId, String hotelId, Set<String> reservationIds) {
    this.externalReferenceId = externalReferenceId;
    this.hotelId = hotelId;
    this.reservationIds = reservationIds;
  }

  public CopyReservationsRequestDto externalReferenceId(String externalReferenceId) {
    this.externalReferenceId = externalReferenceId;
    return this;
  }

  /**
   * Get externalReferenceId
   * @return externalReferenceId
   */
  @NotNull 
  @Schema(name = "externalReferenceId", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("externalReferenceId")
  public String getExternalReferenceId() {
    return externalReferenceId;
  }

  public void setExternalReferenceId(String externalReferenceId) {
    this.externalReferenceId = externalReferenceId;
  }

  public CopyReservationsRequestDto hotelId(String hotelId) {
    this.hotelId = hotelId;
    return this;
  }

  /**
   * Get hotelId
   * @return hotelId
   */
  @NotNull 
  @Schema(name = "hotelId", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("hotelId")
  public String getHotelId() {
    return hotelId;
  }

  public void setHotelId(String hotelId) {
    this.hotelId = hotelId;
  }

  public CopyReservationsRequestDto reservationIds(Set<String> reservationIds) {
    this.reservationIds = reservationIds;
    return this;
  }

  public CopyReservationsRequestDto addReservationIdsItem(String reservationIdsItem) {
    if (this.reservationIds == null) {
      this.reservationIds = new LinkedHashSet<>();
    }
    this.reservationIds.add(reservationIdsItem);
    return this;
  }

  /**
   * Get reservationIds
   * @return reservationIds
   */
  @NotNull 
  @Schema(name = "reservationIds", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("reservationIds")
  public Set<String> getReservationIds() {
    return reservationIds;
  }

  @JsonDeserialize(as = LinkedHashSet.class)
  public void setReservationIds(Set<String> reservationIds) {
    this.reservationIds = reservationIds;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CopyReservationsRequestDto copyReservationsRequestDto = (CopyReservationsRequestDto) o;
    return Objects.equals(this.externalReferenceId, copyReservationsRequestDto.externalReferenceId) &&
        Objects.equals(this.hotelId, copyReservationsRequestDto.hotelId) &&
        Objects.equals(this.reservationIds, copyReservationsRequestDto.reservationIds);
  }

  @Override
  public int hashCode() {
    return Objects.hash(externalReferenceId, hotelId, reservationIds);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CopyReservationsRequestDto {\n");
    sb.append("    externalReferenceId: ").append(toIndentedString(externalReferenceId)).append("\n");
    sb.append("    hotelId: ").append(toIndentedString(hotelId)).append("\n");
    sb.append("    reservationIds: ").append(toIndentedString(reservationIds)).append("\n");
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

