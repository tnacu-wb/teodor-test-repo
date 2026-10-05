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
 * OccupancySupplementResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:49:44.384518+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class OccupancySupplementResponse {

  private String hotelId;

  private BigDecimal pricing;

  public OccupancySupplementResponse() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public OccupancySupplementResponse(String hotelId, BigDecimal pricing) {
    this.hotelId = hotelId;
    this.pricing = pricing;
  }

  public OccupancySupplementResponse hotelId(String hotelId) {
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

  public OccupancySupplementResponse pricing(BigDecimal pricing) {
    this.pricing = pricing;
    return this;
  }

  /**
   * Get pricing
   * @return pricing
   */
  @NotNull @Valid 
  @Schema(name = "pricing", requiredMode = Schema.RequiredMode.REQUIRED)
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
    OccupancySupplementResponse occupancySupplementResponse = (OccupancySupplementResponse) o;
    return Objects.equals(this.hotelId, occupancySupplementResponse.hotelId) &&
        Objects.equals(this.pricing, occupancySupplementResponse.pricing);
  }

  @Override
  public int hashCode() {
    return Objects.hash(hotelId, pricing);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OccupancySupplementResponse {\n");
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

