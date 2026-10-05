package uk.co.whitbread.basket.generated.models.ohip;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * HotelPreferenceDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:10.951524+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class HotelPreferenceDto {

  private @Nullable String code;

  private @Nullable String description;

  private @Nullable String hotelId;

  private @Nullable Boolean housekeeping;

  private @Nullable Integer orderSequence;

  private @Nullable String preferenceGroup;

  public HotelPreferenceDto code(String code) {
    this.code = code;
    return this;
  }

  /**
   * Get code
   * @return code
   */
  
  @Schema(name = "code", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("code")
  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public HotelPreferenceDto description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Get description
   * @return description
   */
  
  @Schema(name = "description", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public HotelPreferenceDto hotelId(String hotelId) {
    this.hotelId = hotelId;
    return this;
  }

  /**
   * Get hotelId
   * @return hotelId
   */
  
  @Schema(name = "hotelId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hotelId")
  public String getHotelId() {
    return hotelId;
  }

  public void setHotelId(String hotelId) {
    this.hotelId = hotelId;
  }

  public HotelPreferenceDto housekeeping(Boolean housekeeping) {
    this.housekeeping = housekeeping;
    return this;
  }

  /**
   * Get housekeeping
   * @return housekeeping
   */
  
  @Schema(name = "housekeeping", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("housekeeping")
  public Boolean getHousekeeping() {
    return housekeeping;
  }

  public void setHousekeeping(Boolean housekeeping) {
    this.housekeeping = housekeeping;
  }

  public HotelPreferenceDto orderSequence(Integer orderSequence) {
    this.orderSequence = orderSequence;
    return this;
  }

  /**
   * Get orderSequence
   * @return orderSequence
   */
  
  @Schema(name = "orderSequence", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("orderSequence")
  public Integer getOrderSequence() {
    return orderSequence;
  }

  public void setOrderSequence(Integer orderSequence) {
    this.orderSequence = orderSequence;
  }

  public HotelPreferenceDto preferenceGroup(String preferenceGroup) {
    this.preferenceGroup = preferenceGroup;
    return this;
  }

  /**
   * Get preferenceGroup
   * @return preferenceGroup
   */
  
  @Schema(name = "preferenceGroup", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("preferenceGroup")
  public String getPreferenceGroup() {
    return preferenceGroup;
  }

  public void setPreferenceGroup(String preferenceGroup) {
    this.preferenceGroup = preferenceGroup;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    HotelPreferenceDto hotelPreferenceDto = (HotelPreferenceDto) o;
    return Objects.equals(this.code, hotelPreferenceDto.code) &&
        Objects.equals(this.description, hotelPreferenceDto.description) &&
        Objects.equals(this.hotelId, hotelPreferenceDto.hotelId) &&
        Objects.equals(this.housekeeping, hotelPreferenceDto.housekeeping) &&
        Objects.equals(this.orderSequence, hotelPreferenceDto.orderSequence) &&
        Objects.equals(this.preferenceGroup, hotelPreferenceDto.preferenceGroup);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code, description, hotelId, housekeeping, orderSequence, preferenceGroup);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class HotelPreferenceDto {\n");
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    hotelId: ").append(toIndentedString(hotelId)).append("\n");
    sb.append("    housekeeping: ").append(toIndentedString(housekeeping)).append("\n");
    sb.append("    orderSequence: ").append(toIndentedString(orderSequence)).append("\n");
    sb.append("    preferenceGroup: ").append(toIndentedString(preferenceGroup)).append("\n");
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

