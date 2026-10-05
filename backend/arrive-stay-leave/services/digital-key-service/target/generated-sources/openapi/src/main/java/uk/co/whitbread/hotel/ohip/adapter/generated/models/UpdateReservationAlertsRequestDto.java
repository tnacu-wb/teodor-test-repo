package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.AlertDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * UpdateReservationAlertsRequestDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:50:34.031950+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class UpdateReservationAlertsRequestDto {

  @Valid
  private List<@Valid AlertDto> alerts;

  private String hotelId;

  @Valid
  private Set<String> reservationIds;

  public UpdateReservationAlertsRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public UpdateReservationAlertsRequestDto(List<@Valid AlertDto> alerts, String hotelId, Set<String> reservationIds) {
    this.alerts = alerts;
    this.hotelId = hotelId;
    this.reservationIds = reservationIds;
  }

  public UpdateReservationAlertsRequestDto alerts(List<@Valid AlertDto> alerts) {
    this.alerts = alerts;
    return this;
  }

  public UpdateReservationAlertsRequestDto addAlertsItem(AlertDto alertsItem) {
    if (this.alerts == null) {
      this.alerts = new ArrayList<>();
    }
    this.alerts.add(alertsItem);
    return this;
  }

  /**
   * Get alerts
   * @return alerts
   */
  @NotNull @Valid 
  @Schema(name = "alerts", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("alerts")
  public List<@Valid AlertDto> getAlerts() {
    return alerts;
  }

  public void setAlerts(List<@Valid AlertDto> alerts) {
    this.alerts = alerts;
  }

  public UpdateReservationAlertsRequestDto hotelId(String hotelId) {
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

  public UpdateReservationAlertsRequestDto reservationIds(Set<String> reservationIds) {
    this.reservationIds = reservationIds;
    return this;
  }

  public UpdateReservationAlertsRequestDto addReservationIdsItem(String reservationIdsItem) {
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
    UpdateReservationAlertsRequestDto updateReservationAlertsRequestDto = (UpdateReservationAlertsRequestDto) o;
    return Objects.equals(this.alerts, updateReservationAlertsRequestDto.alerts) &&
        Objects.equals(this.hotelId, updateReservationAlertsRequestDto.hotelId) &&
        Objects.equals(this.reservationIds, updateReservationAlertsRequestDto.reservationIds);
  }

  @Override
  public int hashCode() {
    return Objects.hash(alerts, hotelId, reservationIds);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UpdateReservationAlertsRequestDto {\n");
    sb.append("    alerts: ").append(toIndentedString(alerts)).append("\n");
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

