package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.AvailabilityResultV2Dto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * AvailabilityByIdsResponseV2Dto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:50:34.031950+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class AvailabilityByIdsResponseV2Dto {

  @Valid
  private @Nullable List<@Valid AvailabilityResultV2Dto> hotelAvailability;

  public AvailabilityByIdsResponseV2Dto hotelAvailability(List<@Valid AvailabilityResultV2Dto> hotelAvailability) {
    this.hotelAvailability = hotelAvailability;
    return this;
  }

  public AvailabilityByIdsResponseV2Dto addHotelAvailabilityItem(AvailabilityResultV2Dto hotelAvailabilityItem) {
    if (this.hotelAvailability == null) {
      this.hotelAvailability = new ArrayList<>();
    }
    this.hotelAvailability.add(hotelAvailabilityItem);
    return this;
  }

  /**
   * Get hotelAvailability
   * @return hotelAvailability
   */
  @Valid 
  @Schema(name = "hotelAvailability", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hotelAvailability")
  public List<@Valid AvailabilityResultV2Dto> getHotelAvailability() {
    return hotelAvailability;
  }

  public void setHotelAvailability(List<@Valid AvailabilityResultV2Dto> hotelAvailability) {
    this.hotelAvailability = hotelAvailability;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AvailabilityByIdsResponseV2Dto availabilityByIdsResponseV2Dto = (AvailabilityByIdsResponseV2Dto) o;
    return Objects.equals(this.hotelAvailability, availabilityByIdsResponseV2Dto.hotelAvailability);
  }

  @Override
  public int hashCode() {
    return Objects.hash(hotelAvailability);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AvailabilityByIdsResponseV2Dto {\n");
    sb.append("    hotelAvailability: ").append(toIndentedString(hotelAvailability)).append("\n");
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

