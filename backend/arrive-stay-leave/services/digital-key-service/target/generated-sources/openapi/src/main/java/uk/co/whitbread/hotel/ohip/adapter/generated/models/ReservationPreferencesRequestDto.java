package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.PreferencesCollectionDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * ReservationPreferencesRequestDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:50:34.031950+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ReservationPreferencesRequestDto {

  private String hotelId;

  @Valid
  private @Nullable List<@Valid PreferencesCollectionDto> preferencesCollections;

  @Valid
  private List<String> reservationsIds;

  public ReservationPreferencesRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ReservationPreferencesRequestDto(String hotelId, List<String> reservationsIds) {
    this.hotelId = hotelId;
    this.reservationsIds = reservationsIds;
  }

  public ReservationPreferencesRequestDto hotelId(String hotelId) {
    this.hotelId = hotelId;
    return this;
  }

  /**
   * Get hotelId
   * @return hotelId
   */
  @NotNull 
  @Schema(name = "hotelId", example = "LONSTM", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("hotelId")
  public String getHotelId() {
    return hotelId;
  }

  public void setHotelId(String hotelId) {
    this.hotelId = hotelId;
  }

  public ReservationPreferencesRequestDto preferencesCollections(List<@Valid PreferencesCollectionDto> preferencesCollections) {
    this.preferencesCollections = preferencesCollections;
    return this;
  }

  public ReservationPreferencesRequestDto addPreferencesCollectionsItem(PreferencesCollectionDto preferencesCollectionsItem) {
    if (this.preferencesCollections == null) {
      this.preferencesCollections = new ArrayList<>();
    }
    this.preferencesCollections.add(preferencesCollectionsItem);
    return this;
  }

  /**
   * Get preferencesCollections
   * @return preferencesCollections
   */
  @Valid 
  @Schema(name = "preferencesCollections", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("preferencesCollections")
  public List<@Valid PreferencesCollectionDto> getPreferencesCollections() {
    return preferencesCollections;
  }

  public void setPreferencesCollections(List<@Valid PreferencesCollectionDto> preferencesCollections) {
    this.preferencesCollections = preferencesCollections;
  }

  public ReservationPreferencesRequestDto reservationsIds(List<String> reservationsIds) {
    this.reservationsIds = reservationsIds;
    return this;
  }

  public ReservationPreferencesRequestDto addReservationsIdsItem(String reservationsIdsItem) {
    if (this.reservationsIds == null) {
      this.reservationsIds = new ArrayList<>();
    }
    this.reservationsIds.add(reservationsIdsItem);
    return this;
  }

  /**
   * Get reservationsIds
   * @return reservationsIds
   */
  @NotNull 
  @Schema(name = "reservationsIds", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("reservationsIds")
  public List<String> getReservationsIds() {
    return reservationsIds;
  }

  public void setReservationsIds(List<String> reservationsIds) {
    this.reservationsIds = reservationsIds;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ReservationPreferencesRequestDto reservationPreferencesRequestDto = (ReservationPreferencesRequestDto) o;
    return Objects.equals(this.hotelId, reservationPreferencesRequestDto.hotelId) &&
        Objects.equals(this.preferencesCollections, reservationPreferencesRequestDto.preferencesCollections) &&
        Objects.equals(this.reservationsIds, reservationPreferencesRequestDto.reservationsIds);
  }

  @Override
  public int hashCode() {
    return Objects.hash(hotelId, preferencesCollections, reservationsIds);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ReservationPreferencesRequestDto {\n");
    sb.append("    hotelId: ").append(toIndentedString(hotelId)).append("\n");
    sb.append("    preferencesCollections: ").append(toIndentedString(preferencesCollections)).append("\n");
    sb.append("    reservationsIds: ").append(toIndentedString(reservationsIds)).append("\n");
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

