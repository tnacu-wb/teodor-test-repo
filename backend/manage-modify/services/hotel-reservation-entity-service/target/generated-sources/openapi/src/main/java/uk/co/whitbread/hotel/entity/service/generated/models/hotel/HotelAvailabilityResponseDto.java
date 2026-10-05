package uk.co.whitbread.hotel.entity.service.generated.models.hotel;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.entity.service.generated.models.hotel.CostDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * HotelAvailabilityResponseDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:33.749132+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class HotelAvailabilityResponseDto {

  private @Nullable Boolean available;

  private @Nullable String cellCode;

  private @Nullable String distance;

  private @Nullable String hotelId;

  private @Nullable Boolean limitedAvailability;

  private @Nullable CostDto lowestRoomRate;

  private @Nullable String name;

  private @Nullable String pmsSource;

  private @Nullable String unit;

  public HotelAvailabilityResponseDto available(Boolean available) {
    this.available = available;
    return this;
  }

  /**
   * Get available
   * @return available
   */
  
  @Schema(name = "available", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("available")
  public Boolean getAvailable() {
    return available;
  }

  public void setAvailable(Boolean available) {
    this.available = available;
  }

  public HotelAvailabilityResponseDto cellCode(String cellCode) {
    this.cellCode = cellCode;
    return this;
  }

  /**
   * Get cellCode
   * @return cellCode
   */
  
  @Schema(name = "cellCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cellCode")
  public String getCellCode() {
    return cellCode;
  }

  public void setCellCode(String cellCode) {
    this.cellCode = cellCode;
  }

  public HotelAvailabilityResponseDto distance(String distance) {
    this.distance = distance;
    return this;
  }

  /**
   * Get distance
   * @return distance
   */
  
  @Schema(name = "distance", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("distance")
  public String getDistance() {
    return distance;
  }

  public void setDistance(String distance) {
    this.distance = distance;
  }

  public HotelAvailabilityResponseDto hotelId(String hotelId) {
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

  public HotelAvailabilityResponseDto limitedAvailability(Boolean limitedAvailability) {
    this.limitedAvailability = limitedAvailability;
    return this;
  }

  /**
   * Get limitedAvailability
   * @return limitedAvailability
   */
  
  @Schema(name = "limitedAvailability", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("limitedAvailability")
  public Boolean getLimitedAvailability() {
    return limitedAvailability;
  }

  public void setLimitedAvailability(Boolean limitedAvailability) {
    this.limitedAvailability = limitedAvailability;
  }

  public HotelAvailabilityResponseDto lowestRoomRate(CostDto lowestRoomRate) {
    this.lowestRoomRate = lowestRoomRate;
    return this;
  }

  /**
   * Get lowestRoomRate
   * @return lowestRoomRate
   */
  @Valid 
  @Schema(name = "lowestRoomRate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("lowestRoomRate")
  public CostDto getLowestRoomRate() {
    return lowestRoomRate;
  }

  public void setLowestRoomRate(CostDto lowestRoomRate) {
    this.lowestRoomRate = lowestRoomRate;
  }

  public HotelAvailabilityResponseDto name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Get name
   * @return name
   */
  
  @Schema(name = "name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public HotelAvailabilityResponseDto pmsSource(String pmsSource) {
    this.pmsSource = pmsSource;
    return this;
  }

  /**
   * Get pmsSource
   * @return pmsSource
   */
  
  @Schema(name = "pmsSource", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("pmsSource")
  public String getPmsSource() {
    return pmsSource;
  }

  public void setPmsSource(String pmsSource) {
    this.pmsSource = pmsSource;
  }

  public HotelAvailabilityResponseDto unit(String unit) {
    this.unit = unit;
    return this;
  }

  /**
   * Get unit
   * @return unit
   */
  
  @Schema(name = "unit", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("unit")
  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    HotelAvailabilityResponseDto hotelAvailabilityResponseDto = (HotelAvailabilityResponseDto) o;
    return Objects.equals(this.available, hotelAvailabilityResponseDto.available) &&
        Objects.equals(this.cellCode, hotelAvailabilityResponseDto.cellCode) &&
        Objects.equals(this.distance, hotelAvailabilityResponseDto.distance) &&
        Objects.equals(this.hotelId, hotelAvailabilityResponseDto.hotelId) &&
        Objects.equals(this.limitedAvailability, hotelAvailabilityResponseDto.limitedAvailability) &&
        Objects.equals(this.lowestRoomRate, hotelAvailabilityResponseDto.lowestRoomRate) &&
        Objects.equals(this.name, hotelAvailabilityResponseDto.name) &&
        Objects.equals(this.pmsSource, hotelAvailabilityResponseDto.pmsSource) &&
        Objects.equals(this.unit, hotelAvailabilityResponseDto.unit);
  }

  @Override
  public int hashCode() {
    return Objects.hash(available, cellCode, distance, hotelId, limitedAvailability, lowestRoomRate, name, pmsSource, unit);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class HotelAvailabilityResponseDto {\n");
    sb.append("    available: ").append(toIndentedString(available)).append("\n");
    sb.append("    cellCode: ").append(toIndentedString(cellCode)).append("\n");
    sb.append("    distance: ").append(toIndentedString(distance)).append("\n");
    sb.append("    hotelId: ").append(toIndentedString(hotelId)).append("\n");
    sb.append("    limitedAvailability: ").append(toIndentedString(limitedAvailability)).append("\n");
    sb.append("    lowestRoomRate: ").append(toIndentedString(lowestRoomRate)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    pmsSource: ").append(toIndentedString(pmsSource)).append("\n");
    sb.append("    unit: ").append(toIndentedString(unit)).append("\n");
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

