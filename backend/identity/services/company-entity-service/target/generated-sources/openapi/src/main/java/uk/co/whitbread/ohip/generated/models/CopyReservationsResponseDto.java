package uk.co.whitbread.ohip.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.lang.Nullable;
import uk.co.whitbread.ohip.generated.models.CopyReservationResponseDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * CopyReservationsResponseDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:50:12.143180+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CopyReservationsResponseDto {

  @Valid
  private Map<String, String> linkBetweenReservations = new HashMap<>();

  @Valid
  private List<@Valid CopyReservationResponseDto> reservations = new ArrayList<>();

  public CopyReservationsResponseDto linkBetweenReservations(Map<String, String> linkBetweenReservations) {
    this.linkBetweenReservations = linkBetweenReservations;
    return this;
  }

  public CopyReservationsResponseDto putLinkBetweenReservationsItem(String key, String linkBetweenReservationsItem) {
    if (this.linkBetweenReservations == null) {
      this.linkBetweenReservations = new HashMap<>();
    }
    this.linkBetweenReservations.put(key, linkBetweenReservationsItem);
    return this;
  }

  /**
   * Get linkBetweenReservations
   * @return linkBetweenReservations
   */
  
  @Schema(name = "linkBetweenReservations", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("linkBetweenReservations")
  public Map<String, String> getLinkBetweenReservations() {
    return linkBetweenReservations;
  }

  public void setLinkBetweenReservations(Map<String, String> linkBetweenReservations) {
    this.linkBetweenReservations = linkBetweenReservations;
  }

  public CopyReservationsResponseDto reservations(List<@Valid CopyReservationResponseDto> reservations) {
    this.reservations = reservations;
    return this;
  }

  public CopyReservationsResponseDto addReservationsItem(CopyReservationResponseDto reservationsItem) {
    if (this.reservations == null) {
      this.reservations = new ArrayList<>();
    }
    this.reservations.add(reservationsItem);
    return this;
  }

  /**
   * Get reservations
   * @return reservations
   */
  @Valid 
  @Schema(name = "reservations", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reservations")
  public List<@Valid CopyReservationResponseDto> getReservations() {
    return reservations;
  }

  public void setReservations(List<@Valid CopyReservationResponseDto> reservations) {
    this.reservations = reservations;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CopyReservationsResponseDto copyReservationsResponseDto = (CopyReservationsResponseDto) o;
    return Objects.equals(this.linkBetweenReservations, copyReservationsResponseDto.linkBetweenReservations) &&
        Objects.equals(this.reservations, copyReservationsResponseDto.reservations);
  }

  @Override
  public int hashCode() {
    return Objects.hash(linkBetweenReservations, reservations);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CopyReservationsResponseDto {\n");
    sb.append("    linkBetweenReservations: ").append(toIndentedString(linkBetweenReservations)).append("\n");
    sb.append("    reservations: ").append(toIndentedString(reservations)).append("\n");
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

