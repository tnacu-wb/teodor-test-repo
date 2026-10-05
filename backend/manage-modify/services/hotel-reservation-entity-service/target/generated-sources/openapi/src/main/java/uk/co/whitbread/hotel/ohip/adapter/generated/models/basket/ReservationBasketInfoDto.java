package uk.co.whitbread.hotel.ohip.adapter.generated.models.basket;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.HashMap;
import java.util.Map;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * ReservationBasketInfoDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:25.444555+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ReservationBasketInfoDto {

  @Valid
  private Map<String, String> linkAmendReservations = new HashMap<>();

  private @Nullable String migratedResNo;

  private @Nullable String originalBasketId;

  public ReservationBasketInfoDto linkAmendReservations(Map<String, String> linkAmendReservations) {
    this.linkAmendReservations = linkAmendReservations;
    return this;
  }

  public ReservationBasketInfoDto putLinkAmendReservationsItem(String key, String linkAmendReservationsItem) {
    if (this.linkAmendReservations == null) {
      this.linkAmendReservations = new HashMap<>();
    }
    this.linkAmendReservations.put(key, linkAmendReservationsItem);
    return this;
  }

  /**
   * Get linkAmendReservations
   * @return linkAmendReservations
   */
  
  @Schema(name = "linkAmendReservations", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("linkAmendReservations")
  public Map<String, String> getLinkAmendReservations() {
    return linkAmendReservations;
  }

  public void setLinkAmendReservations(Map<String, String> linkAmendReservations) {
    this.linkAmendReservations = linkAmendReservations;
  }

  public ReservationBasketInfoDto migratedResNo(String migratedResNo) {
    this.migratedResNo = migratedResNo;
    return this;
  }

  /**
   * Get migratedResNo
   * @return migratedResNo
   */
  
  @Schema(name = "migratedResNo", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("migratedResNo")
  public String getMigratedResNo() {
    return migratedResNo;
  }

  public void setMigratedResNo(String migratedResNo) {
    this.migratedResNo = migratedResNo;
  }

  public ReservationBasketInfoDto originalBasketId(String originalBasketId) {
    this.originalBasketId = originalBasketId;
    return this;
  }

  /**
   * Get originalBasketId
   * @return originalBasketId
   */
  
  @Schema(name = "originalBasketId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("originalBasketId")
  public String getOriginalBasketId() {
    return originalBasketId;
  }

  public void setOriginalBasketId(String originalBasketId) {
    this.originalBasketId = originalBasketId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ReservationBasketInfoDto reservationBasketInfoDto = (ReservationBasketInfoDto) o;
    return Objects.equals(this.linkAmendReservations, reservationBasketInfoDto.linkAmendReservations) &&
        Objects.equals(this.migratedResNo, reservationBasketInfoDto.migratedResNo) &&
        Objects.equals(this.originalBasketId, reservationBasketInfoDto.originalBasketId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(linkAmendReservations, migratedResNo, originalBasketId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ReservationBasketInfoDto {\n");
    sb.append("    linkAmendReservations: ").append(toIndentedString(linkAmendReservations)).append("\n");
    sb.append("    migratedResNo: ").append(toIndentedString(migratedResNo)).append("\n");
    sb.append("    originalBasketId: ").append(toIndentedString(originalBasketId)).append("\n");
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

