package uk.co.whitbread.rules.agent.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.rules.agent.generated.models.OccupancySupplementRequestDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * MultiOccupancySupplementRequestDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:49:44.384518+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class MultiOccupancySupplementRequestDto {

  @Valid
  private List<@Valid OccupancySupplementRequestDto> hotelIds = new ArrayList<>();

  public MultiOccupancySupplementRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public MultiOccupancySupplementRequestDto(List<@Valid OccupancySupplementRequestDto> hotelIds) {
    this.hotelIds = hotelIds;
  }

  public MultiOccupancySupplementRequestDto hotelIds(List<@Valid OccupancySupplementRequestDto> hotelIds) {
    this.hotelIds = hotelIds;
    return this;
  }

  public MultiOccupancySupplementRequestDto addHotelIdsItem(OccupancySupplementRequestDto hotelIdsItem) {
    if (this.hotelIds == null) {
      this.hotelIds = new ArrayList<>();
    }
    this.hotelIds.add(hotelIdsItem);
    return this;
  }

  /**
   * Get hotelIds
   * @return hotelIds
   */
  @NotNull @Valid 
  @Schema(name = "hotelIds", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("hotelIds")
  public List<@Valid OccupancySupplementRequestDto> getHotelIds() {
    return hotelIds;
  }

  public void setHotelIds(List<@Valid OccupancySupplementRequestDto> hotelIds) {
    this.hotelIds = hotelIds;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MultiOccupancySupplementRequestDto multiOccupancySupplementRequestDto = (MultiOccupancySupplementRequestDto) o;
    return Objects.equals(this.hotelIds, multiOccupancySupplementRequestDto.hotelIds);
  }

  @Override
  public int hashCode() {
    return Objects.hash(hotelIds);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MultiOccupancySupplementRequestDto {\n");
    sb.append("    hotelIds: ").append(toIndentedString(hotelIds)).append("\n");
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

