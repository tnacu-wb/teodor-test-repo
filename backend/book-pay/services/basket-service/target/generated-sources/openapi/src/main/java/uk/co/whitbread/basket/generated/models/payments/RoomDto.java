package uk.co.whitbread.basket.generated.models.payments;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Information on the rooms being booked.
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Schema(name = "Room", description = "Information on the rooms being booked.")
@JsonTypeName("Room")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:02.841275+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RoomDto {

  private @Nullable Integer adults;

  private @Nullable String rate;

  private @Nullable String type;

  public RoomDto adults(Integer adults) {
    this.adults = adults;
    return this;
  }

  /**
   * Number of adults in room being booked.
   * @return adults
   */
  
  @Schema(name = "adults", example = "2", description = "Number of adults in room being booked.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("adults")
  public Integer getAdults() {
    return adults;
  }

  public void setAdults(Integer adults) {
    this.adults = adults;
  }

  public RoomDto rate(String rate) {
    this.rate = rate;
    return this;
  }

  /**
   * Rate type for the room being booked.
   * @return rate
   */
  
  @Schema(name = "rate", example = "SV344", description = "Rate type for the room being booked.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("rate")
  public String getRate() {
    return rate;
  }

  public void setRate(String rate) {
    this.rate = rate;
  }

  public RoomDto type(String type) {
    this.type = type;
    return this;
  }

  /**
   * Type of room being booked.
   * @return type
   */
  
  @Schema(name = "type", example = "FAM", description = "Type of room being booked.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("type")
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RoomDto room = (RoomDto) o;
    return Objects.equals(this.adults, room.adults) &&
        Objects.equals(this.rate, room.rate) &&
        Objects.equals(this.type, room.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(adults, rate, type);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RoomDto {\n");
    sb.append("    adults: ").append(toIndentedString(adults)).append("\n");
    sb.append("    rate: ").append(toIndentedString(rate)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
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

