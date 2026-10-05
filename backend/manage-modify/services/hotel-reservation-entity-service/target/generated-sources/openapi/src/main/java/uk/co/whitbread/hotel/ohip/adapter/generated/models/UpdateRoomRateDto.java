package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RateTypeDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UpdateRoomOccupancyDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * UpdateRoomRateDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:15.274731+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class UpdateRoomRateDto {

  private @Nullable String endDate;

  private @Nullable String ratePlanCode;

  private @Nullable RateTypeDto rates;

  private @Nullable UpdateRoomOccupancyDto roomOccupancy;

  private @Nullable String roomType;

  private @Nullable String startDate;

  public UpdateRoomRateDto endDate(String endDate) {
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

  public UpdateRoomRateDto ratePlanCode(String ratePlanCode) {
    this.ratePlanCode = ratePlanCode;
    return this;
  }

  /**
   * Get ratePlanCode
   * @return ratePlanCode
   */
  
  @Schema(name = "ratePlanCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("ratePlanCode")
  public String getRatePlanCode() {
    return ratePlanCode;
  }

  public void setRatePlanCode(String ratePlanCode) {
    this.ratePlanCode = ratePlanCode;
  }

  public UpdateRoomRateDto rates(RateTypeDto rates) {
    this.rates = rates;
    return this;
  }

  /**
   * Get rates
   * @return rates
   */
  @Valid 
  @Schema(name = "rates", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("rates")
  public RateTypeDto getRates() {
    return rates;
  }

  public void setRates(RateTypeDto rates) {
    this.rates = rates;
  }

  public UpdateRoomRateDto roomOccupancy(UpdateRoomOccupancyDto roomOccupancy) {
    this.roomOccupancy = roomOccupancy;
    return this;
  }

  /**
   * Get roomOccupancy
   * @return roomOccupancy
   */
  @Valid 
  @Schema(name = "roomOccupancy", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomOccupancy")
  public UpdateRoomOccupancyDto getRoomOccupancy() {
    return roomOccupancy;
  }

  public void setRoomOccupancy(UpdateRoomOccupancyDto roomOccupancy) {
    this.roomOccupancy = roomOccupancy;
  }

  public UpdateRoomRateDto roomType(String roomType) {
    this.roomType = roomType;
    return this;
  }

  /**
   * Get roomType
   * @return roomType
   */
  
  @Schema(name = "roomType", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomType")
  public String getRoomType() {
    return roomType;
  }

  public void setRoomType(String roomType) {
    this.roomType = roomType;
  }

  public UpdateRoomRateDto startDate(String startDate) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UpdateRoomRateDto updateRoomRateDto = (UpdateRoomRateDto) o;
    return Objects.equals(this.endDate, updateRoomRateDto.endDate) &&
        Objects.equals(this.ratePlanCode, updateRoomRateDto.ratePlanCode) &&
        Objects.equals(this.rates, updateRoomRateDto.rates) &&
        Objects.equals(this.roomOccupancy, updateRoomRateDto.roomOccupancy) &&
        Objects.equals(this.roomType, updateRoomRateDto.roomType) &&
        Objects.equals(this.startDate, updateRoomRateDto.startDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(endDate, ratePlanCode, rates, roomOccupancy, roomType, startDate);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UpdateRoomRateDto {\n");
    sb.append("    endDate: ").append(toIndentedString(endDate)).append("\n");
    sb.append("    ratePlanCode: ").append(toIndentedString(ratePlanCode)).append("\n");
    sb.append("    rates: ").append(toIndentedString(rates)).append("\n");
    sb.append("    roomOccupancy: ").append(toIndentedString(roomOccupancy)).append("\n");
    sb.append("    roomType: ").append(toIndentedString(roomType)).append("\n");
    sb.append("    startDate: ").append(toIndentedString(startDate)).append("\n");
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

