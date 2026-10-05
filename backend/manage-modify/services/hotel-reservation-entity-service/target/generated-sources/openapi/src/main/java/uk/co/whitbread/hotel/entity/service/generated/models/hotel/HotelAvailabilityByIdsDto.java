package uk.co.whitbread.hotel.entity.service.generated.models.hotel;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.entity.service.generated.models.hotel.HotelAvailabilityDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * HotelAvailabilityByIdsDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:33.749132+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class HotelAvailabilityByIdsDto {

  @Valid
  private List<@Valid HotelAvailabilityDto> hotelAvailability = new ArrayList<>();

  public HotelAvailabilityByIdsDto hotelAvailability(List<@Valid HotelAvailabilityDto> hotelAvailability) {
    this.hotelAvailability = hotelAvailability;
    return this;
  }

  public HotelAvailabilityByIdsDto addHotelAvailabilityItem(HotelAvailabilityDto hotelAvailabilityItem) {
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
  public List<@Valid HotelAvailabilityDto> getHotelAvailability() {
    return hotelAvailability;
  }

  public void setHotelAvailability(List<@Valid HotelAvailabilityDto> hotelAvailability) {
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
    HotelAvailabilityByIdsDto hotelAvailabilityByIdsDto = (HotelAvailabilityByIdsDto) o;
    return Objects.equals(this.hotelAvailability, hotelAvailabilityByIdsDto.hotelAvailability);
  }

  @Override
  public int hashCode() {
    return Objects.hash(hotelAvailability);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class HotelAvailabilityByIdsDto {\n");
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

