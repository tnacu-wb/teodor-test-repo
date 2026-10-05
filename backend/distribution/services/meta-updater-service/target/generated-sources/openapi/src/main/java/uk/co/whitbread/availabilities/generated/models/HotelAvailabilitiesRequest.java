package uk.co.whitbread.availabilities.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * HotelAvailabilitiesRequest
 */
@lombok.Builder @lombok.AllArgsConstructor

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:31.761066+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class HotelAvailabilitiesRequest {

  private @Nullable String hotelCode;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private @Nullable LocalDate startDate;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private @Nullable LocalDate endDate;

  @lombok.Builder.Default
  @Valid
  private List<String> roomTypes = new ArrayList<>();

  public HotelAvailabilitiesRequest hotelCode(String hotelCode) {
    this.hotelCode = hotelCode;
    return this;
  }

  /**
   * Get hotelCode
   * @return hotelCode
   */
  
  @Schema(name = "hotelCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hotelCode")
  public String getHotelCode() {
    return hotelCode;
  }

  public void setHotelCode(String hotelCode) {
    this.hotelCode = hotelCode;
  }

  public HotelAvailabilitiesRequest startDate(LocalDate startDate) {
    this.startDate = startDate;
    return this;
  }

  /**
   * Get startDate
   * @return startDate
   */
  @Valid 
  @Schema(name = "startDate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("startDate")
  public LocalDate getStartDate() {
    return startDate;
  }

  public void setStartDate(LocalDate startDate) {
    this.startDate = startDate;
  }

  public HotelAvailabilitiesRequest endDate(LocalDate endDate) {
    this.endDate = endDate;
    return this;
  }

  /**
   * Get endDate
   * @return endDate
   */
  @Valid 
  @Schema(name = "endDate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("endDate")
  public LocalDate getEndDate() {
    return endDate;
  }

  public void setEndDate(LocalDate endDate) {
    this.endDate = endDate;
  }

  public HotelAvailabilitiesRequest roomTypes(List<String> roomTypes) {
    this.roomTypes = roomTypes;
    return this;
  }

  public HotelAvailabilitiesRequest addRoomTypesItem(String roomTypesItem) {
    if (this.roomTypes == null) {
      this.roomTypes = new ArrayList<>();
    }
    this.roomTypes.add(roomTypesItem);
    return this;
  }

  /**
   * Get roomTypes
   * @return roomTypes
   */
  
  @Schema(name = "roomTypes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomTypes")
  public List<String> getRoomTypes() {
    return roomTypes;
  }

  public void setRoomTypes(List<String> roomTypes) {
    this.roomTypes = roomTypes;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    HotelAvailabilitiesRequest hotelAvailabilitiesRequest = (HotelAvailabilitiesRequest) o;
    return Objects.equals(this.hotelCode, hotelAvailabilitiesRequest.hotelCode) &&
        Objects.equals(this.startDate, hotelAvailabilitiesRequest.startDate) &&
        Objects.equals(this.endDate, hotelAvailabilitiesRequest.endDate) &&
        Objects.equals(this.roomTypes, hotelAvailabilitiesRequest.roomTypes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(hotelCode, startDate, endDate, roomTypes);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class HotelAvailabilitiesRequest {\n");
    sb.append("    hotelCode: ").append(toIndentedString(hotelCode)).append("\n");
    sb.append("    startDate: ").append(toIndentedString(startDate)).append("\n");
    sb.append("    endDate: ").append(toIndentedString(endDate)).append("\n");
    sb.append("    roomTypes: ").append(toIndentedString(roomTypes)).append("\n");
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

