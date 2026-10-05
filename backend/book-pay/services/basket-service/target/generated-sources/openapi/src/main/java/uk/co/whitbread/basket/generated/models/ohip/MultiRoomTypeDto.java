package uk.co.whitbread.basket.generated.models.ohip;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.ohip.RoomRateInfoDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * MultiRoomTypeDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:10.951524+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class MultiRoomTypeDto {

  private @Nullable String adults;

  private @Nullable String children;

  private @Nullable String cotRequested;

  private @Nullable Integer numberOfRooms;

  @Valid
  private List<@Valid RoomRateInfoDto> roomRates = new ArrayList<>();

  private @Nullable String roomType;

  public MultiRoomTypeDto adults(String adults) {
    this.adults = adults;
    return this;
  }

  /**
   * Get adults
   * @return adults
   */
  
  @Schema(name = "adults", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("adults")
  public String getAdults() {
    return adults;
  }

  public void setAdults(String adults) {
    this.adults = adults;
  }

  public MultiRoomTypeDto children(String children) {
    this.children = children;
    return this;
  }

  /**
   * Get children
   * @return children
   */
  
  @Schema(name = "children", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("children")
  public String getChildren() {
    return children;
  }

  public void setChildren(String children) {
    this.children = children;
  }

  public MultiRoomTypeDto cotRequested(String cotRequested) {
    this.cotRequested = cotRequested;
    return this;
  }

  /**
   * Get cotRequested
   * @return cotRequested
   */
  
  @Schema(name = "cotRequested", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cotRequested")
  public String getCotRequested() {
    return cotRequested;
  }

  public void setCotRequested(String cotRequested) {
    this.cotRequested = cotRequested;
  }

  public MultiRoomTypeDto numberOfRooms(Integer numberOfRooms) {
    this.numberOfRooms = numberOfRooms;
    return this;
  }

  /**
   * Get numberOfRooms
   * @return numberOfRooms
   */
  
  @Schema(name = "numberOfRooms", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("numberOfRooms")
  public Integer getNumberOfRooms() {
    return numberOfRooms;
  }

  public void setNumberOfRooms(Integer numberOfRooms) {
    this.numberOfRooms = numberOfRooms;
  }

  public MultiRoomTypeDto roomRates(List<@Valid RoomRateInfoDto> roomRates) {
    this.roomRates = roomRates;
    return this;
  }

  public MultiRoomTypeDto addRoomRatesItem(RoomRateInfoDto roomRatesItem) {
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
  public List<@Valid RoomRateInfoDto> getRoomRates() {
    return roomRates;
  }

  public void setRoomRates(List<@Valid RoomRateInfoDto> roomRates) {
    this.roomRates = roomRates;
  }

  public MultiRoomTypeDto roomType(String roomType) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MultiRoomTypeDto multiRoomTypeDto = (MultiRoomTypeDto) o;
    return Objects.equals(this.adults, multiRoomTypeDto.adults) &&
        Objects.equals(this.children, multiRoomTypeDto.children) &&
        Objects.equals(this.cotRequested, multiRoomTypeDto.cotRequested) &&
        Objects.equals(this.numberOfRooms, multiRoomTypeDto.numberOfRooms) &&
        Objects.equals(this.roomRates, multiRoomTypeDto.roomRates) &&
        Objects.equals(this.roomType, multiRoomTypeDto.roomType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(adults, children, cotRequested, numberOfRooms, roomRates, roomType);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MultiRoomTypeDto {\n");
    sb.append("    adults: ").append(toIndentedString(adults)).append("\n");
    sb.append("    children: ").append(toIndentedString(children)).append("\n");
    sb.append("    cotRequested: ").append(toIndentedString(cotRequested)).append("\n");
    sb.append("    numberOfRooms: ").append(toIndentedString(numberOfRooms)).append("\n");
    sb.append("    roomRates: ").append(toIndentedString(roomRates)).append("\n");
    sb.append("    roomType: ").append(toIndentedString(roomType)).append("\n");
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

