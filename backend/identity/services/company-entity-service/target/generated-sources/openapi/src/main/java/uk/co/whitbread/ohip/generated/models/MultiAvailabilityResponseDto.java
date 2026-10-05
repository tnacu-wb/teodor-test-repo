package uk.co.whitbread.ohip.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import uk.co.whitbread.ohip.generated.models.HotelAvailabilityResultDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * MultiAvailabilityResponseDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:50:12.143180+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class MultiAvailabilityResponseDto {

  @Valid
  private List<@Valid HotelAvailabilityResultDto> hotelAvailabilityResults = new ArrayList<>();

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private @Nullable Date timestamp;

  public MultiAvailabilityResponseDto hotelAvailabilityResults(List<@Valid HotelAvailabilityResultDto> hotelAvailabilityResults) {
    this.hotelAvailabilityResults = hotelAvailabilityResults;
    return this;
  }

  public MultiAvailabilityResponseDto addHotelAvailabilityResultsItem(HotelAvailabilityResultDto hotelAvailabilityResultsItem) {
    if (this.hotelAvailabilityResults == null) {
      this.hotelAvailabilityResults = new ArrayList<>();
    }
    this.hotelAvailabilityResults.add(hotelAvailabilityResultsItem);
    return this;
  }

  /**
   * Get hotelAvailabilityResults
   * @return hotelAvailabilityResults
   */
  @Valid 
  @Schema(name = "hotelAvailabilityResults", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hotelAvailabilityResults")
  public List<@Valid HotelAvailabilityResultDto> getHotelAvailabilityResults() {
    return hotelAvailabilityResults;
  }

  public void setHotelAvailabilityResults(List<@Valid HotelAvailabilityResultDto> hotelAvailabilityResults) {
    this.hotelAvailabilityResults = hotelAvailabilityResults;
  }

  public MultiAvailabilityResponseDto timestamp(Date timestamp) {
    this.timestamp = timestamp;
    return this;
  }

  /**
   * Get timestamp
   * @return timestamp
   */
  @Valid 
  @Schema(name = "timestamp", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("timestamp")
  public Date getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Date timestamp) {
    this.timestamp = timestamp;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MultiAvailabilityResponseDto multiAvailabilityResponseDto = (MultiAvailabilityResponseDto) o;
    return Objects.equals(this.hotelAvailabilityResults, multiAvailabilityResponseDto.hotelAvailabilityResults) &&
        Objects.equals(this.timestamp, multiAvailabilityResponseDto.timestamp);
  }

  @Override
  public int hashCode() {
    return Objects.hash(hotelAvailabilityResults, timestamp);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MultiAvailabilityResponseDto {\n");
    sb.append("    hotelAvailabilityResults: ").append(toIndentedString(hotelAvailabilityResults)).append("\n");
    sb.append("    timestamp: ").append(toIndentedString(timestamp)).append("\n");
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

