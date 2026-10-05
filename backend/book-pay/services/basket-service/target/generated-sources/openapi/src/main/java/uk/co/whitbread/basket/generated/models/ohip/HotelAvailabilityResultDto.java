package uk.co.whitbread.basket.generated.models.ohip;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.ohip.MultiRoomTypeDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * HotelAvailabilityResultDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:10.951524+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class HotelAvailabilityResultDto {

  private @Nullable String arrivalDate;

  private @Nullable Boolean available;

  private @Nullable String departureDate;

  private @Nullable String hotelId;

  @Valid
  private List<@Valid MultiRoomTypeDto> roomTypes = new ArrayList<>();

  public HotelAvailabilityResultDto arrivalDate(String arrivalDate) {
    this.arrivalDate = arrivalDate;
    return this;
  }

  /**
   * Get arrivalDate
   * @return arrivalDate
   */
  
  @Schema(name = "arrivalDate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("arrivalDate")
  public String getArrivalDate() {
    return arrivalDate;
  }

  public void setArrivalDate(String arrivalDate) {
    this.arrivalDate = arrivalDate;
  }

  public HotelAvailabilityResultDto available(Boolean available) {
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

  public HotelAvailabilityResultDto departureDate(String departureDate) {
    this.departureDate = departureDate;
    return this;
  }

  /**
   * Get departureDate
   * @return departureDate
   */
  
  @Schema(name = "departureDate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("departureDate")
  public String getDepartureDate() {
    return departureDate;
  }

  public void setDepartureDate(String departureDate) {
    this.departureDate = departureDate;
  }

  public HotelAvailabilityResultDto hotelId(String hotelId) {
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

  public HotelAvailabilityResultDto roomTypes(List<@Valid MultiRoomTypeDto> roomTypes) {
    this.roomTypes = roomTypes;
    return this;
  }

  public HotelAvailabilityResultDto addRoomTypesItem(MultiRoomTypeDto roomTypesItem) {
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
  @Valid 
  @Schema(name = "roomTypes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomTypes")
  public List<@Valid MultiRoomTypeDto> getRoomTypes() {
    return roomTypes;
  }

  public void setRoomTypes(List<@Valid MultiRoomTypeDto> roomTypes) {
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
    HotelAvailabilityResultDto hotelAvailabilityResultDto = (HotelAvailabilityResultDto) o;
    return Objects.equals(this.arrivalDate, hotelAvailabilityResultDto.arrivalDate) &&
        Objects.equals(this.available, hotelAvailabilityResultDto.available) &&
        Objects.equals(this.departureDate, hotelAvailabilityResultDto.departureDate) &&
        Objects.equals(this.hotelId, hotelAvailabilityResultDto.hotelId) &&
        Objects.equals(this.roomTypes, hotelAvailabilityResultDto.roomTypes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(arrivalDate, available, departureDate, hotelId, roomTypes);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class HotelAvailabilityResultDto {\n");
    sb.append("    arrivalDate: ").append(toIndentedString(arrivalDate)).append("\n");
    sb.append("    available: ").append(toIndentedString(available)).append("\n");
    sb.append("    departureDate: ").append(toIndentedString(departureDate)).append("\n");
    sb.append("    hotelId: ").append(toIndentedString(hotelId)).append("\n");
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

