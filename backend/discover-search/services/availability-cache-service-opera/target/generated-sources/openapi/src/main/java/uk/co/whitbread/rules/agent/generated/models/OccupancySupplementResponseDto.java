package uk.co.whitbread.rules.agent.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.math.BigDecimal;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * OccupancySupplementResponseDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:49:44.384518+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class OccupancySupplementResponseDto {

  private @Nullable String hotelId;

  private @Nullable BigDecimal pricing;

  public OccupancySupplementResponseDto hotelId(String hotelId) {
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

  public OccupancySupplementResponseDto pricing(BigDecimal pricing) {
    this.pricing = pricing;
    return this;
  }

  /**
   * Get pricing
   * @return pricing
   */
  @Valid 
  @Schema(name = "pricing", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("pricing")
  public BigDecimal getPricing() {
    return pricing;
  }

  public void setPricing(BigDecimal pricing) {
    this.pricing = pricing;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OccupancySupplementResponseDto occupancySupplementResponseDto = (OccupancySupplementResponseDto) o;
    return Objects.equals(this.hotelId, occupancySupplementResponseDto.hotelId) &&
        Objects.equals(this.pricing, occupancySupplementResponseDto.pricing);
  }

  @Override
  public int hashCode() {
    return Objects.hash(hotelId, pricing);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OccupancySupplementResponseDto {\n");
    sb.append("    hotelId: ").append(toIndentedString(hotelId)).append("\n");
    sb.append("    pricing: ").append(toIndentedString(pricing)).append("\n");
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

