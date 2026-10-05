package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RoomRateAvailabilityDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * AvailabilityResponseDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:50:34.031950+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class AvailabilityResponseDto {

  private @Nullable Boolean available;

  private @Nullable String endDate;

  private @Nullable String hotelId;

  private @Nullable Boolean limitedAvailability;

  @Valid
  private @Nullable List<@Valid RoomRateAvailabilityDto> roomRates;

  private @Nullable String startDate;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private @Nullable OffsetDateTime timestamp;

  public AvailabilityResponseDto available(Boolean available) {
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

  public AvailabilityResponseDto endDate(String endDate) {
    this.endDate = endDate;
    return this;
  }

  /**
   * Get endDate
   * @return endDate
   */
  
  @Schema(name = "endDate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("endDate")
  public String getEndDate() {
    return endDate;
  }

  public void setEndDate(String endDate) {
    this.endDate = endDate;
  }

  public AvailabilityResponseDto hotelId(String hotelId) {
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

  public AvailabilityResponseDto limitedAvailability(Boolean limitedAvailability) {
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

  public AvailabilityResponseDto roomRates(List<@Valid RoomRateAvailabilityDto> roomRates) {
    this.roomRates = roomRates;
    return this;
  }

  public AvailabilityResponseDto addRoomRatesItem(RoomRateAvailabilityDto roomRatesItem) {
    if (this.roomRates == null) {
      this.roomRates = new ArrayList<>();
    }
    this.roomRates.add(roomRatesItem);
    return this;
  }

  /**
   * Get roomRates
   * @return roomRates
   */
  @Valid 
  @Schema(name = "roomRates", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomRates")
  public List<@Valid RoomRateAvailabilityDto> getRoomRates() {
    return roomRates;
  }

  public void setRoomRates(List<@Valid RoomRateAvailabilityDto> roomRates) {
    this.roomRates = roomRates;
  }

  public AvailabilityResponseDto startDate(String startDate) {
    this.startDate = startDate;
    return this;
  }

  /**
   * Get startDate
   * @return startDate
   */
  
  @Schema(name = "startDate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("startDate")
  public String getStartDate() {
    return startDate;
  }

  public void setStartDate(String startDate) {
    this.startDate = startDate;
  }

  public AvailabilityResponseDto timestamp(OffsetDateTime timestamp) {
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
  public OffsetDateTime getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(OffsetDateTime timestamp) {
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
    AvailabilityResponseDto availabilityResponseDto = (AvailabilityResponseDto) o;
    return Objects.equals(this.available, availabilityResponseDto.available) &&
        Objects.equals(this.endDate, availabilityResponseDto.endDate) &&
        Objects.equals(this.hotelId, availabilityResponseDto.hotelId) &&
        Objects.equals(this.limitedAvailability, availabilityResponseDto.limitedAvailability) &&
        Objects.equals(this.roomRates, availabilityResponseDto.roomRates) &&
        Objects.equals(this.startDate, availabilityResponseDto.startDate) &&
        Objects.equals(this.timestamp, availabilityResponseDto.timestamp);
  }

  @Override
  public int hashCode() {
    return Objects.hash(available, endDate, hotelId, limitedAvailability, roomRates, startDate, timestamp);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AvailabilityResponseDto {\n");
    sb.append("    available: ").append(toIndentedString(available)).append("\n");
    sb.append("    endDate: ").append(toIndentedString(endDate)).append("\n");
    sb.append("    hotelId: ").append(toIndentedString(hotelId)).append("\n");
    sb.append("    limitedAvailability: ").append(toIndentedString(limitedAvailability)).append("\n");
    sb.append("    roomRates: ").append(toIndentedString(roomRates)).append("\n");
    sb.append("    startDate: ").append(toIndentedString(startDate)).append("\n");
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

