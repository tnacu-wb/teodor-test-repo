package uk.co.whitbread.content.entity.service.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.content.entity.service.generated.models.content.AcceptedRoomTypesDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * SearchRulesDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:28.563902+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class SearchRulesDto {

  private @Nullable Integer maxArrivalDate;

  private @Nullable Integer maxNights;

  private @Nullable Integer maxRooms;

  private @Nullable Integer maxRoomsAmend;

  @Valid
  private List<@Valid AcceptedRoomTypesDto> roomOccupancies = new ArrayList<>();

  public SearchRulesDto maxArrivalDate(Integer maxArrivalDate) {
    this.maxArrivalDate = maxArrivalDate;
    return this;
  }

  /**
   * Get maxArrivalDate
   * @return maxArrivalDate
   */
  
  @Schema(name = "maxArrivalDate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("maxArrivalDate")
  public Integer getMaxArrivalDate() {
    return maxArrivalDate;
  }

  public void setMaxArrivalDate(Integer maxArrivalDate) {
    this.maxArrivalDate = maxArrivalDate;
  }

  public SearchRulesDto maxNights(Integer maxNights) {
    this.maxNights = maxNights;
    return this;
  }

  /**
   * Get maxNights
   * @return maxNights
   */
  
  @Schema(name = "maxNights", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("maxNights")
  public Integer getMaxNights() {
    return maxNights;
  }

  public void setMaxNights(Integer maxNights) {
    this.maxNights = maxNights;
  }

  public SearchRulesDto maxRooms(Integer maxRooms) {
    this.maxRooms = maxRooms;
    return this;
  }

  /**
   * Get maxRooms
   * @return maxRooms
   */
  
  @Schema(name = "maxRooms", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("maxRooms")
  public Integer getMaxRooms() {
    return maxRooms;
  }

  public void setMaxRooms(Integer maxRooms) {
    this.maxRooms = maxRooms;
  }

  public SearchRulesDto maxRoomsAmend(Integer maxRoomsAmend) {
    this.maxRoomsAmend = maxRoomsAmend;
    return this;
  }

  /**
   * Get maxRoomsAmend
   * @return maxRoomsAmend
   */
  
  @Schema(name = "maxRoomsAmend", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("maxRoomsAmend")
  public Integer getMaxRoomsAmend() {
    return maxRoomsAmend;
  }

  public void setMaxRoomsAmend(Integer maxRoomsAmend) {
    this.maxRoomsAmend = maxRoomsAmend;
  }

  public SearchRulesDto roomOccupancies(List<@Valid AcceptedRoomTypesDto> roomOccupancies) {
    this.roomOccupancies = roomOccupancies;
    return this;
  }

  public SearchRulesDto addRoomOccupanciesItem(AcceptedRoomTypesDto roomOccupanciesItem) {
    if (this.roomOccupancies == null) {
      this.roomOccupancies = new ArrayList<>();
    }
    this.roomOccupancies.add(roomOccupanciesItem);
    return this;
  }

  /**
   * Get roomOccupancies
   * @return roomOccupancies
   */
  @Valid 
  @Schema(name = "roomOccupancies", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomOccupancies")
  public List<@Valid AcceptedRoomTypesDto> getRoomOccupancies() {
    return roomOccupancies;
  }

  public void setRoomOccupancies(List<@Valid AcceptedRoomTypesDto> roomOccupancies) {
    this.roomOccupancies = roomOccupancies;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SearchRulesDto searchRulesDto = (SearchRulesDto) o;
    return Objects.equals(this.maxArrivalDate, searchRulesDto.maxArrivalDate) &&
        Objects.equals(this.maxNights, searchRulesDto.maxNights) &&
        Objects.equals(this.maxRooms, searchRulesDto.maxRooms) &&
        Objects.equals(this.maxRoomsAmend, searchRulesDto.maxRoomsAmend) &&
        Objects.equals(this.roomOccupancies, searchRulesDto.roomOccupancies);
  }

  @Override
  public int hashCode() {
    return Objects.hash(maxArrivalDate, maxNights, maxRooms, maxRoomsAmend, roomOccupancies);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SearchRulesDto {\n");
    sb.append("    maxArrivalDate: ").append(toIndentedString(maxArrivalDate)).append("\n");
    sb.append("    maxNights: ").append(toIndentedString(maxNights)).append("\n");
    sb.append("    maxRooms: ").append(toIndentedString(maxRooms)).append("\n");
    sb.append("    maxRoomsAmend: ").append(toIndentedString(maxRoomsAmend)).append("\n");
    sb.append("    roomOccupancies: ").append(toIndentedString(roomOccupancies)).append("\n");
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

