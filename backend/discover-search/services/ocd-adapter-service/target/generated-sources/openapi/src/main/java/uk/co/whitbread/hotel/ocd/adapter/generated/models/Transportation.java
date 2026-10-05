package uk.co.whitbread.hotel.ocd.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * Transportation
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:32.554166+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class Transportation {

  private @Nullable String transportationCode;

  private @Nullable String description;

  private @Nullable Boolean includeInRate;

  private @Nullable Boolean reservationRequired;

  public Transportation transportationCode(String transportationCode) {
    this.transportationCode = transportationCode;
    return this;
  }

  /**
   * The code of the transportation service such as Bus, Taxi. Enum values from globalCodes TRP - Transportation Code
   * @return transportationCode
   */
  
  @Schema(name = "transportationCode", example = "Metro", description = "The code of the transportation service such as Bus, Taxi. Enum values from globalCodes TRP - Transportation Code", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("transportationCode")
  public String getTransportationCode() {
    return transportationCode;
  }

  public void setTransportationCode(String transportationCode) {
    this.transportationCode = transportationCode;
  }

  public Transportation description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Description of the transportation service.
   * @return description
   */
  @Size(max = 1024) 
  @Schema(name = "description", example = "Red line metro", description = "Description of the transportation service.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Transportation includeInRate(Boolean includeInRate) {
    this.includeInRate = includeInRate;
    return this;
  }

  /**
   * When true the transportation service is included with the room rate.
   * @return includeInRate
   */
  
  @Schema(name = "includeInRate", example = "false", description = "When true the transportation service is included with the room rate.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("includeInRate")
  public Boolean getIncludeInRate() {
    return includeInRate;
  }

  public void setIncludeInRate(Boolean includeInRate) {
    this.includeInRate = includeInRate;
  }

  public Transportation reservationRequired(Boolean reservationRequired) {
    this.reservationRequired = reservationRequired;
    return this;
  }

  /**
   * When true a reservation is required for the transportation service.
   * @return reservationRequired
   */
  
  @Schema(name = "reservationRequired", example = "true", description = "When true a reservation is required for the transportation service.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reservationRequired")
  public Boolean getReservationRequired() {
    return reservationRequired;
  }

  public void setReservationRequired(Boolean reservationRequired) {
    this.reservationRequired = reservationRequired;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Transportation transportation = (Transportation) o;
    return Objects.equals(this.transportationCode, transportation.transportationCode) &&
        Objects.equals(this.description, transportation.description) &&
        Objects.equals(this.includeInRate, transportation.includeInRate) &&
        Objects.equals(this.reservationRequired, transportation.reservationRequired);
  }

  @Override
  public int hashCode() {
    return Objects.hash(transportationCode, description, includeInRate, reservationRequired);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Transportation {\n");
    sb.append("    transportationCode: ").append(toIndentedString(transportationCode)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    includeInRate: ").append(toIndentedString(includeInRate)).append("\n");
    sb.append("    reservationRequired: ").append(toIndentedString(reservationRequired)).append("\n");
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

