package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelPreferenceDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * HotelPreferencesResponseDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:50:34.031950+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class HotelPreferencesResponseDto {

  @Valid
  private @Nullable List<@Valid HotelPreferenceDto> hotelPreferences;

  public HotelPreferencesResponseDto hotelPreferences(List<@Valid HotelPreferenceDto> hotelPreferences) {
    this.hotelPreferences = hotelPreferences;
    return this;
  }

  public HotelPreferencesResponseDto addHotelPreferencesItem(HotelPreferenceDto hotelPreferencesItem) {
    if (this.hotelPreferences == null) {
      this.hotelPreferences = new ArrayList<>();
    }
    this.hotelPreferences.add(hotelPreferencesItem);
    return this;
  }

  /**
   * Get hotelPreferences
   * @return hotelPreferences
   */
  @Valid 
  @Schema(name = "hotelPreferences", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hotelPreferences")
  public List<@Valid HotelPreferenceDto> getHotelPreferences() {
    return hotelPreferences;
  }

  public void setHotelPreferences(List<@Valid HotelPreferenceDto> hotelPreferences) {
    this.hotelPreferences = hotelPreferences;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    HotelPreferencesResponseDto hotelPreferencesResponseDto = (HotelPreferencesResponseDto) o;
    return Objects.equals(this.hotelPreferences, hotelPreferencesResponseDto.hotelPreferences);
  }

  @Override
  public int hashCode() {
    return Objects.hash(hotelPreferences);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class HotelPreferencesResponseDto {\n");
    sb.append("    hotelPreferences: ").append(toIndentedString(hotelPreferences)).append("\n");
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

